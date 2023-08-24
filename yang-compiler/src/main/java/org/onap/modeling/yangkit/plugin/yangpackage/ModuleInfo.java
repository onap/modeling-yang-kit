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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.net.URI;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ModuleInfo extends SubComponentInfo {
    private URI namespace;
    private final List<SubModuleInfo> subModules = new ArrayList<>();

    /**
     * constructor.
     * @param name module name
     * @param revision module revision
     */
    public ModuleInfo(String name, String revision) {
        super(name, revision);
    }

    /**
     * constructor.
     * @param name module name
     */
    public ModuleInfo(String name) {
        super(name);
    }

    /**
     * serialize the revision.
     * @return serialized revision
     */
    @Override
    protected Map.Entry<String, String> serializeRevision() {
        if (this.getRevision() == null) {
            return null;
        }
        return new AbstractMap.SimpleEntry<>("revision", this.getRevision());
    }

    /**
     * get namespace.
     * @return the uri of namespace
     */
    public URI getNamespace() {
        return namespace;
    }

    /**
     * set namespace.
     * @param namespace the uri of namespace
     */
    public void setNamespace(URI namespace) {
        this.namespace = namespace;
    }

    /**
     * get submodules.
     * @return list of submodules.
     */
    public List<SubModuleInfo> getSubModules() {
        return subModules;
    }

    /**
     * add submodule.
     * @param subModuleInfo submodule
     */
    public void addSubModule(SubModuleInfo subModuleInfo) {
        if (subModules.contains(subModuleInfo)) {
            return;
        }
        subModules.add(subModuleInfo);
    }

    /**
     * serialize the module information.
     * @return json element.
     */
    @Override
    public JsonElement serialize() {
        JsonObject jsonObject = super.serialize().getAsJsonObject();
        if (this.getNamespace() != null) {
            jsonObject.addProperty("namespace", this.getNamespace().toString());
        }
        if (!this.getSubModules().isEmpty()) {
            JsonArray subModules = new JsonArray();
            for (SubModuleInfo subModule : this.getSubModules()) {
                subModules.add(subModule.serialize());
            }
            jsonObject.add("submodule", subModules);
        }
        return jsonObject;
    }
}
