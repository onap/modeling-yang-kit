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

import org.onap.modeling.yangkit.catalog.ModuleInfo;
import org.onap.modeling.yangkit.compiler.util.YangCompilerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yangcentral.yangkit.model.api.schema.YangSchemaContext;
import org.yangcentral.yangkit.model.api.stmt.Module;
import org.yangcentral.yangkit.parser.YangYinParser;


public class FileSource implements Source {
    private List<String> files;
    private static final Logger logger = LoggerFactory.getLogger(FileSource.class);

    public FileSource(List<String> files) {
        this.files = files;
    }

    @Override
    public YangSchemaContext buildSource(Settings settings, YangSchemaContext yangSchemaContext)
            throws YangCompilerException {
        return buildSource(settings, yangSchemaContext, false);
    }

    @Override
    public YangSchemaContext buildSource(Settings settings, YangSchemaContext schemaContext, boolean withDependencies)
            throws YangCompilerException {
        List<File> fileList = new ArrayList<>();
        for (String file : files) {
            fileList.add(new File(file));
        }
        try {
            logger.info("start to build schema context for files:" + files);
            schemaContext = YangYinParser.parse(fileList, schemaContext);
            if (withDependencies) {
                logger.info("start to build dependencies for files:" + files);
                for (Module module : schemaContext.getModules()) {
                    String source = module.getElementPosition().getSource();
                    if (!files.contains(source)) {
                        continue;
                    }
                    List<ModuleInfo> dependencies = YangCompilerUtil.getDependencies(module);
                    if (!dependencies.isEmpty()) {
                        List<ModuleInfo> extraDependencies = new ArrayList<>();
                        for (ModuleInfo dependency : dependencies) {
                            if (schemaContext.getModule(dependency.getName(), dependency.getRevision()).isPresent()) {
                                continue;
                            }
                            extraDependencies.add(dependency);
                        }
                        ModuleSource extraDependenciesSource = new ModuleSource(extraDependencies, true);
                        schemaContext = extraDependenciesSource.buildSource(settings, schemaContext, true);
                    }
                }
                logger.info("end to build dependencies for files:" + files);
            }
            logger.info("end to build schema context for files:" + files);
            return schemaContext;
        } catch (Exception e) {
            throw new YangCompilerException(e.getMessage());
        }
    }
}
