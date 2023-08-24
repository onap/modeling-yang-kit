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

package org.onap.modeling.yangkit.catalog;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;


public class YangCatalog {
    private final List<ModuleInfo> modules = new ArrayList<>();

    /**
     * add module information to yang catalog.
     *
     * @param moduleInfo module information structure
     */
    public void addModule(ModuleInfo moduleInfo) {
        if (getModule(moduleInfo.getName(), moduleInfo.getRevision(), moduleInfo.getOrganization())
                != null) {
            return;
        }
        modules.add(moduleInfo);
    }

    /**
     * get module information from yang catalog.
     *
     * @param name         module name
     * @param revision     module revision
     * @param organization the organization who publish the module
     * @return module information
     */
    public ModuleInfo getModule(String name, String revision, String organization) {
        if (modules.isEmpty()) {
            return null;
        }
        for (ModuleInfo moduleInfo : modules) {
            if (moduleInfo.getName().equals(name) && moduleInfo.getRevision().equals(revision)
                    && moduleInfo.getOrganization().equals(organization)) {
                return moduleInfo;
            }
        }
        return null;
    }

    /**
     * get all modules from yang catalog.
     *
     * @return list of modules
     */
    public List<ModuleInfo> getModules() {
        return modules;
    }

    /**
     * get all modules by module name.
     *
     * @param moduleName module name
     * @return list of matched modules
     */
    public List<ModuleInfo> getModules(String moduleName) {
        List<ModuleInfo> moduleInfos = new ArrayList<>();
        for (ModuleInfo moduleInfo : modules) {
            if (moduleInfo.getName().equals(moduleName)) {
                moduleInfos.add(moduleInfo);
            }
        }
        return moduleInfos;
    }

    /**
     * get the latest module by module name.
     *
     * @param moduleName module name
     * @return the latest module information
     */
    public ModuleInfo getLatestModule(String moduleName) {
        List<ModuleInfo> matched = getModules(moduleName);
        if (matched.isEmpty()) {
            return null;
        }
        if (matched.size() == 1) {
            return matched.get(0);
        }
        ModuleInfo latest = null;
        for (ModuleInfo moduleInfo : matched) {
            if (latest == null) {
                latest = moduleInfo;
            } else {
                if (moduleInfo.getRevision().compareTo(latest.getRevision()) > 0) {
                    latest = moduleInfo;
                }
            }
        }
        return latest;
    }

    /**
     * parse yang catalog from json string.
     *
     * @param str json string
     * @return yang catalog
     */
    public static YangCatalog parse(String str) {
        YangCatalog yangCatalog = new YangCatalog();
        JsonElement element = JsonParser.parseString(str);
        JsonObject jsonObject = element.getAsJsonObject();
        JsonElement modulesElement = jsonObject.get("yang-catalog:modules");
        JsonObject modules = modulesElement.getAsJsonObject();
        JsonElement moduleElement = modules.get("module");
        JsonArray moduleArray = moduleElement.getAsJsonArray();
        int size = moduleArray.size();
        for (int i = 0; i < size; i++) {
            JsonElement subElement = moduleArray.get(i);
            ModuleInfo moduleInfo = ModuleInfo.parse(subElement);
            if (moduleInfo == null) {
                continue;
            }
            yangCatalog.addModule(moduleInfo);
        }
        return yangCatalog;
    }

}
