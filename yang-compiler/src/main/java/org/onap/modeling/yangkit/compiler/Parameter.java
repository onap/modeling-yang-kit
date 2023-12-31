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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class Parameter {
    private String name;
    private JsonElement value;

    /**
     * the constructor.
     *
     * @param name  the name of parameter
     * @param value the value of parameter
     */
    public Parameter(String name, JsonElement value) {
        this.name = name;
        this.value = value;
    }

    /**
     * get the parameter name.
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * get the parameter value.
     *
     * @return value
     */
    public JsonElement getValue() {
        return value;
    }

    /**
     * parse parameter from json.
     *
     * @param jsonElement json element
     * @return parameter
     */
    public static Parameter parse(JsonElement jsonElement) {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        String name = jsonObject.get("name").getAsString();
        JsonElement value = null;
        if (jsonObject.get("value") != null) {
            value = jsonObject.get("value");
        }
        Parameter parameter = new Parameter(name, value);
        return parameter;
    }
}
