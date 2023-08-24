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

import java.util.ArrayList;
import java.util.List;


public class Builder {
    private String yangDir;
    private List<PluginBuilder> plugins = new ArrayList<>();

    /**
     * the constructor.
     */
    public Builder() {
    }

    /**
     * get yang directory where the yang files to be compiled exist.
     *
     * @return the directory path
     */
    public String getYangDir() {
        return yangDir;
    }

    /**
     * set the yang directory.
     *
     * @param yangDir the path of yang directory
     */
    public void setYangDir(String yangDir) {
        this.yangDir = yangDir;
    }

    /**
     * get the plugins.
     *
     * @return the list of plugins
     */
    public List<PluginBuilder> getPlugins() {
        return plugins;
    }

    /**
     * set the list of plugins.
     *
     * @param plugins the list of plugins
     */
    public void setPlugins(List<PluginBuilder> plugins) {
        this.plugins = plugins;
    }

    /**
     * add a plugin.
     *
     * @param pluginBuilder plugin to be added.
     */
    public void addPlugin(PluginBuilder pluginBuilder) {
        this.plugins.add(pluginBuilder);
    }

    /**
     * parse the build.json.
     *
     * @param jsonElement json element
     * @return builder structure
     */
    public static Builder parse(JsonElement jsonElement) {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        JsonElement buildElement = jsonObject.get("build");
        Builder builder = new Builder();
        if (buildElement == null) {
            return builder;
        }
        JsonObject buildObj = buildElement.getAsJsonObject();
        JsonElement yangElement = buildObj.get("yang");
        if (yangElement != null) {
            String yang = yangElement.getAsString();
            builder.setYangDir(yang);
        }
        JsonElement pluginsElement = buildObj.get("plugin");
        if (pluginsElement != null) {
            JsonArray plugins = pluginsElement.getAsJsonArray();
            for (int i = 0; i < plugins.size(); i++) {
                JsonElement pluginElement = plugins.get(i);
                PluginBuilder pluginBuilder = PluginBuilder.parse(pluginElement);
                builder.addPlugin(pluginBuilder);
            }
        }
        return builder;
    }
}
