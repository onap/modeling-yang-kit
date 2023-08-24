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

import java.util.ArrayList;
import java.util.List;


public class PluginBuilder {
    private final String name;
    private final List<ParameterBuilder> parameters = new ArrayList<>();

    /**
     * the constructor.
     *
     * @param name the plugin name
     */
    public PluginBuilder(String name) {
        this.name = name;
    }

    /**
     * get the plugin name.
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * get all parameters of plugin.
     *
     * @return the list of parameters
     */
    public List<ParameterBuilder> getParameters() {
        return parameters;
    }

    /**
     * add parameter into plugin.
     *
     * @param para parameter
     */
    public void addParameter(ParameterBuilder para) {
        parameters.add(para);
    }

    /**
     * parse plugin from json.
     *
     * @param jsonElement json element
     * @return plugin builder
     */
    public static PluginBuilder parse(JsonElement jsonElement) {
        String name = jsonElement.getAsJsonObject().get("name").getAsString();
        PluginBuilder pluginBuilder = new PluginBuilder(name);
        JsonElement parasElement = jsonElement.getAsJsonObject().get("parameter");
        if (parasElement != null) {
            JsonArray paras = parasElement.getAsJsonArray();
            for (int i = 0; i < paras.size(); i++) {
                JsonElement paraElement = paras.get(i);
                ParameterBuilder parameterBuilder = ParameterBuilder.parse(paraElement);
                pluginBuilder.addParameter(parameterBuilder);
            }
        }
        return pluginBuilder;
    }
}
