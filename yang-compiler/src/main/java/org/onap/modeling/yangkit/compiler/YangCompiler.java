/*
Copyright 2023 Huawei Technologies

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package org.onap.modeling.yangkit.compiler;



import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.onap.modeling.yangkit.catalog.ModuleInfo;
import org.onap.modeling.yangkit.compiler.plugin.YangCompilerPlugin;
import org.onap.modeling.yangkit.compiler.plugin.YangCompilerPluginParameter;
import org.onap.modeling.yangkit.compiler.util.YangCompilerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yangcentral.yangkit.base.YangElement;
import org.yangcentral.yangkit.common.api.validate.ValidatorRecord;
import org.yangcentral.yangkit.common.api.validate.ValidatorResult;
import org.yangcentral.yangkit.common.api.validate.ValidatorResultBuilder;
import org.yangcentral.yangkit.model.api.schema.YangSchemaContext;
import org.yangcentral.yangkit.model.api.stmt.Module;
import org.yangcentral.yangkit.model.api.stmt.YangStatement;
import org.yangcentral.yangkit.utils.file.FileUtil;
import org.yangcentral.yangkit.writter.YangFormatter;
import org.yangcentral.yangkit.writter.YangWriter;


public class YangCompiler {

    private Settings settings;

    private final Map<String, PluginInfo> pluginInfos = new ConcurrentHashMap<String, PluginInfo>();

    private BuildOption buildOption;

    private boolean install;

    private static final Logger logger = LoggerFactory.getLogger(YangCompiler.class);

    public YangCompiler() {
    }

    public BuildOption getBuildOption() {
        return buildOption;
    }

    public void setBuildOption(BuildOption buildOption) {
        this.buildOption = buildOption;
    }

    /**
     * get plugin information.
     *
     * @param name plugin name
     * @return plugin information
     */
    public PluginInfo getPluginInfo(String name) {
        if (pluginInfos.isEmpty()) {
            return null;
        }
        return pluginInfos.get(name);
    }

    /**
     * add a new plugin.
     *
     * @param pluginInfo plugin information
     */
    public void addPluginInfo(PluginInfo pluginInfo) {
        if (pluginInfo == null) {
            return;
        }
        if (getPluginInfo(pluginInfo.getPluginName()) != null) {
            return;
        }
        pluginInfos.put(pluginInfo.getPluginName(), pluginInfo);
    }

    /**
     * get settings.
     *
     * @return settings
     */
    public Settings getSettings() {
        return settings;
    }

    /**
     * set settings.
     *
     * @param settings settings
     */
    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    public boolean isInstall() {
        return install;
    }

    public void setInstall(boolean install) {
        this.install = install;
    }

    /**
     * build schema context from build option.
     * @return yang schema context
     */
    public YangSchemaContext buildSchemaContext() {
        YangSchemaContext schemaContext = null;
        try {
            schemaContext = YangCompilerUtil.buildSchemaContext(buildOption.getSources(),getSettings());

            return schemaContext;
        } catch (YangCompilerException e) {
            throw new RuntimeException(e);
        }
    }

    private void saveModule(String fileName, List<YangElement> elements) {
        StringBuilder sb = new StringBuilder();
        for (YangElement element : elements) {
            String yangStr = YangWriter.toYangString(element, YangFormatter.getPrettyYangFormatter(), null);
            sb.append(yangStr);
            sb.append("\n");
        }

        fileName = settings.getLocalRepository() + File.separator + fileName;
        FileUtil.writeUtf8File(fileName, sb.toString());
    }

    private void installModules(List<Module> modules) {
        for (Module module : modules) {
            String moduleName = module.getArgStr();
            String revision = "";
            if (module.getCurRevisionDate().isPresent()) {
                revision = module.getCurRevisionDate().get();
            }
            ModuleInfo moduleInfo = new ModuleInfo(moduleName, revision, null);
            ModuleInfo targetModuleInfo = YangCompilerUtil.getSchemaFromLocal(moduleInfo, settings);
            if (targetModuleInfo == null) {
                //if not found, save this module to local repository
                List<YangElement> elements = module.getContext().getSchemaContext().getParseResult()
                        .get(module.getElementPosition().getSource());
                saveModule(moduleInfo.getModuleInfo() + ".yang", elements);
                logger.info("install " + moduleInfo.getModuleInfo() + ".yang" + " to " + settings.getLocalRepository());
            }

        }
    }

    /**
     * compile yang modules and invoke plugins.
     */
    public void compile() {
        if (buildOption == null) {
            logger.warn("build.json is not found.");
            return;
        }
        logger.info("build yang schema context.");
        YangSchemaContext schemaContext = buildSchemaContext();
        ValidatorResult validatorResult = schemaContext.validate();
        if (!validatorResult.isOk()) {
            logger.error("there are some errors when validating yang schema context.");
            System.out.println(validatorResult);
            return;
        }
        for (Plugin pluginBuilder : getBuildOption().getPlugins()) {
            PluginInfo pluginInfo = getPluginInfo(pluginBuilder.getName());
            if (null == pluginInfo) {
                logger.warn("can not find a plugin named:" + pluginBuilder.getName());
                continue;
            }
            YangCompilerPlugin plugin = pluginInfo.getPlugin();
            try {
                List<YangCompilerPluginParameter> parameters = new ArrayList<>();
                if (!pluginBuilder.getParameters().isEmpty()) {
                    for (Parameter parameterBuilder : pluginBuilder.getParameters()) {
                        YangCompilerPluginParameter parameter = plugin.getParameter(
                                parameterBuilder.getName(), parameterBuilder.getValue());
                        if (parameter != null) {
                            parameters.add(parameter);
                        }
                    }
                }
                logger.info("call plugin:" + pluginInfo.getPluginName() + " ...");
                plugin.run(schemaContext, this, parameters);
                logger.info("ok.");
            } catch (YangCompilerException e) {
                logger.error(e.getMessage());
            }
        }
        ValidatorResultBuilder validatorResultBuilder = new ValidatorResultBuilder();
        List<ValidatorRecord<?, ?>> records = validatorResult.getRecords();
        for (ValidatorRecord<?, ?> record : records) {
            if (record.getBadElement() instanceof YangStatement) {
                YangStatement yangStatement = (YangStatement) record.getBadElement();
                if (schemaContext.getModules().contains(yangStatement.getContext().getCurModule())) {
                    validatorResultBuilder.addRecord(record);
                }
            }
        }
        validatorResult = validatorResultBuilder.build();
        if (install && validatorResult.isOk()) {
            installModules(schemaContext.getModules());
        }
        logger.info(validatorResult.toString());
    }


}
