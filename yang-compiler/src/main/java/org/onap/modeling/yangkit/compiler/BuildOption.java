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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.onap.modeling.yangkit.catalog.ModuleInfo;




public class BuildOption {
    private List<Source> sources = new ArrayList<>();
    private List<Plugin> plugins = new ArrayList<>();

    private String settings;

    /**
     * the constructor.
     */
    public BuildOption() {
    }

    /**
     * get all sources.
     * @return list of sources
     */
    public List<Source> getSources() {
        return sources;
    }

    /**
     * add a source to build option.
     * @param source candidate source
     */
    public void addSource(Source source) {
        if (sources.contains(source)) {
            return;
        }
        sources.add(source);
    }

    /**
     * get plugins of build option.
     * @return list of plugins
     */
    public List<Plugin> getPlugins() {
        return plugins;
    }

    /**
     * set the list of plugins.
     *
     * @param plugins the list of plugins
     */
    public void setPlugins(List<Plugin> plugins) {
        this.plugins = plugins;
    }

    /**
     * add a plugin.
     *
     * @param plugin plugin to be added.
     */
    public void addPlugin(Plugin plugin) {
        this.plugins.add(plugin);
    }

    public String getSettings() {
        return settings;
    }

    public void setSettings(String settings) {
        this.settings = settings;
    }

    /**
     * parse the build.json.
     *
     * @param jsonElement json element
     * @return builder structure
     */
    public static BuildOption parse(JsonElement jsonElement) {
        BuildOption buildOption = new BuildOption();
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        JsonElement yangElement = jsonObject.get("yang");
        if (yangElement != null) {
            JsonObject yang = yangElement.getAsJsonObject();
            JsonElement dirElement = yang.get("dir");
            if (dirElement != null) {
                JsonArray dirArray = dirElement.getAsJsonArray();
                List<JsonElement> dirElementList = dirArray.asList();
                List<String> dirs = new ArrayList<>();
                for (JsonElement dirElementItem : dirElementList) {
                    String yangDir = dirElementItem.getAsString();
                    dirs.add(yangDir);
                }
                DirectorySource directorySource = new DirectorySource(dirs);
                buildOption.addSource(directorySource);
            }
            JsonElement filesElement = yang.get("file");
            if (filesElement != null) {
                JsonArray fileArray = filesElement.getAsJsonArray();
                List<JsonElement> fileElementList = fileArray.asList();
                List<String> files = new ArrayList<>();
                for (JsonElement fileElementItem : fileElementList) {
                    String yangFile = fileElementItem.getAsString();
                    files.add(yangFile);
                }
                FileSource fileSource = new FileSource(files);
                buildOption.addSource(fileSource);
            }
            JsonElement modulesElement = yang.get("module");
            if (modulesElement != null) {
                JsonArray moduleArray = modulesElement.getAsJsonArray();
                List<JsonElement> moduleList = moduleArray.asList();
                List<ModuleInfo> moduleInfos = new ArrayList<>();
                for (JsonElement moduleElement : moduleList) {
                    JsonObject moduleObject = moduleElement.getAsJsonObject();
                    String name = moduleObject.get("name").getAsString();
                    String revision = moduleObject.get("revision").getAsString();
                    String organization = null;
                    if (moduleObject.get("organization") != null) {
                        organization = moduleObject.get("organization").getAsString();
                    }
                    URI schema = null;
                    if (moduleObject.get("schema") != null) {
                        schema = URI.create(moduleObject.get("schema").getAsString());
                    }
                    ModuleInfo moduleInfo = new ModuleInfo(name, revision, organization);
                    moduleInfo.setSchema(schema);
                    moduleInfos.add(moduleInfo);
                }
                ModuleSource moduleSource = new ModuleSource(moduleInfos);
                buildOption.addSource(moduleSource);
            }
        }
        JsonElement settingsElement = jsonObject.get("settings");
        if (settingsElement != null) {
            buildOption.setSettings(settingsElement.getAsString());
        }
        JsonElement pluginsElement = jsonObject.get("plugin");
        if (pluginsElement != null) {
            JsonArray plugins = pluginsElement.getAsJsonArray();
            for (int i = 0; i < plugins.size(); i++) {
                JsonElement pluginElement = plugins.get(i);
                Plugin plugin = Plugin.parse(pluginElement);
                buildOption.addPlugin(plugin);
            }
        }
        return buildOption;
    }
}
