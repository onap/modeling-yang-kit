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


public class PluginParameterInfo {
    private final String name;
    private String description;

    /**
     * the constructor.
     *
     * @param name parameter name
     */
    public PluginParameterInfo(String name) {
        this.name = name;
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
     * get the description.
     *
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * set the description.
     *
     * @param description the description to be set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "PluginParameterInfo{"
                + "name='"
                + name
                + '\''
                + ", description='"
                + description
                + '\''
                + '}';
    }
}
