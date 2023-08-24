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

package org.onap.modeling.yangkit.plugin.yangpackage;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.util.List;
import java.util.Properties;

import org.onap.modeling.yangkit.compiler.YangCompiler;
import org.onap.modeling.yangkit.compiler.YangCompilerException;
import org.onap.modeling.yangkit.plugin.YangCompilerPlugin;
import org.onap.modeling.yangkit.plugin.YangCompilerPluginParameter;
import org.yangcentral.yangkit.model.api.schema.YangSchemaContext;
import org.yangcentral.yangkit.model.api.stmt.Include;
import org.yangcentral.yangkit.model.api.stmt.MainModule;
import org.yangcentral.yangkit.model.api.stmt.Module;
import org.yangcentral.yangkit.model.api.stmt.SubModule;


public class YangPackageGenerator implements YangCompilerPlugin {
    @Override
    public YangCompilerPluginParameter getParameter(Properties compilerProps, String name, String value)
            throws YangCompilerException {
        if (!name.equals("complete") && !name.equals("meta") && !name.equals("output")) {
            throw new YangCompilerException("unrecognized parameter:" + name);
        }
        if (name.equals("complete")) {
            YangCompilerPluginParameter pluginParameter = new YangCompilerPluginParameter() {
                @Override
                public String getName() {
                    return name;
                }

                @Override
                public Object getValue() throws YangCompilerException {
                    return Boolean.valueOf(value);
                }
            };
            return pluginParameter;
        }
        return YangCompilerPlugin.super.getParameter(compilerProps, name, value);
    }

    @Override
    public void run(YangSchemaContext schemaContext, YangCompiler yangCompiler,
                    List<YangCompilerPluginParameter> parameters)
            throws YangCompilerException {
        String output = null;
        for (YangCompilerPluginParameter parameter : parameters) {
            if (parameter.getName().equals("output")) {
                output = (String) parameter.getValue();
            } else {
                throw new YangCompilerException("unknown parameter:" + parameter.getName());
            }
        }
        if (output == null) {
            throw new YangCompilerException("missing mandatory parameter:output");
        }

        File yangDir = yangCompiler.getYang();
        //search package_meta.json
        File packageMetaFile = new File(yangDir, "package_meta.json");
        if (!packageMetaFile.exists()) {
            throw new YangCompilerException("missing package_meta.json in directory:" + yangDir.getAbsolutePath());
        }

        try {
            //parse yang package meta information
            JsonElement element = JsonParser.parseReader(new FileReader(packageMetaFile));
            YangPackageMeta yangPackageMeta = new YangPackageMeta();
            yangPackageMeta.deserialize(element);
            //set meta information for yang package
            YangPackage yangPackage = new YangPackage(yangPackageMeta.getName(), yangPackageMeta.getRevision());
            yangPackage.setOrganization(yangPackageMeta.getOrganization());
            yangPackage.setContact(yangPackageMeta.getContact());
            yangPackage.setDescription(yangPackageMeta.getDescription());
            yangPackage.setReference(yangPackageMeta.getReference());
            yangPackage.setLocal(yangPackageMeta.isLocal());
            yangPackage.setTags(yangPackageMeta.getTags());
            yangPackage.setMandatoryFeatures(yangPackageMeta.getMandatoryFeatures());
            yangPackage.setIncludePackages(yangPackageMeta.getIncludePackages());
            if (schemaContext.getImportOnlyModules().isEmpty()) {
                yangPackage.setComplete(true);
            }
            FilenameFilter packageMetaFilter = new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    if (name.equals("package_meta.json")) {
                        return true;
                    }
                    return false;
                }
            };
            File[] metaFiles = yangDir.listFiles(packageMetaFilter);
            List<Module> modules = schemaContext.getModules();
            for (Module module : modules) {
                if (module instanceof MainModule) {
                    ModuleInfo moduleInfo = new ModuleInfo(module.getArgStr(), module.getCurRevisionDate().get());
                    moduleInfo.setNamespace(((MainModule) module).getNamespace().getUri());
                    if (!module.getIncludes().isEmpty()) {
                        for (Include include : module.getIncludes()) {
                            SubModule subModule = include.getInclude().get();
                            SubModuleInfo subModuleInfo =
                                    new SubModuleInfo(subModule.getArgStr(), subModule.getCurRevisionDate().get());
                            moduleInfo.addSubModule(subModuleInfo);
                        }
                    }
                    yangPackage.addModule(moduleInfo);
                }
            }
            for (Module importOnlyModule : schemaContext.getImportOnlyModules()) {
                if (!(importOnlyModule instanceof MainModule)) {
                    continue;
                }
                ModuleInfo moduleInfo =
                        new ModuleInfo(importOnlyModule.getArgStr(), importOnlyModule.getCurRevisionDate().get());
                moduleInfo.setNamespace(((MainModule) importOnlyModule).getNamespace().getUri());
                if (!importOnlyModule.getIncludes().isEmpty()) {
                    for (Include include : importOnlyModule.getIncludes()) {
                        SubModule subModule = include.getInclude().get();
                        SubModuleInfo subModuleInfo =
                                new SubModuleInfo(subModule.getArgStr(), subModule.getCurRevisionDate().get());
                        moduleInfo.addSubModule(subModuleInfo);
                    }
                }
                yangPackage.addImportOnlyModule(moduleInfo);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }
}
