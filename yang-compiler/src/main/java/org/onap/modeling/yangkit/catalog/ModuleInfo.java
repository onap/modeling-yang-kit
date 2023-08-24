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

import java.net.URI;


public class ModuleInfo {
    private final String name;
    private final String revision;
    private final String organization;
    private URI schema;

    /**
     * The constructor of ModuleInfo.
     * @param name module name
     * @param revision module revision
     * @param organization  the organization who publish this module
     */
    public ModuleInfo(String name, String revision, String organization) {
        this.name = name;
        this.revision = revision;
        this.organization = organization;
    }

    /**
     *  set schema.
     * @param schema the URI of schema
     */
    public void setSchema(URI schema) {
        this.schema = schema;
    }

    /**
     * get module name.
     * @return module name
     */
    public String getName() {
        return name;
    }

    /**
     * get module's revision.
     * @return revision
     */
    public String getRevision() {
        return revision;
    }

    /**
     * get organization.
     * @return organization
     */
    public String getOrganization() {
        return organization;
    }

    /**
     * get schema.
     * @return URI
     */
    public URI getSchema() {
        return schema;
    }

    /**
     * parse module info from json.
     * @param element json element
     * @return ModuleInfo
     */
    public static ModuleInfo parse(JsonElement element) {
        JsonObject jsonObject = element.getAsJsonObject();
        String name = jsonObject.get("name").getAsString();
        String revision = jsonObject.get("revision").getAsString();
        JsonElement organizationElement = jsonObject.get("organization");
        String organization = null;
        if (organizationElement != null) {
            organization = organizationElement.getAsString();
        }
        JsonElement schemaElement = jsonObject.get("schema");
        if (schemaElement == null) {
            return null;
        }
        URI schema = URI.create(schemaElement.getAsString());
        if (name == null || revision == null) {
            return null;
        }
        ModuleInfo moduleInfo = new ModuleInfo(name,revision,organization);
        moduleInfo.setSchema(schema);
        return moduleInfo;
    }

    /**
     * parse module information from json string.
     * @param str json string
     * @return Module information
     */
    public static ModuleInfo parse(String str) {
        JsonElement element = JsonParser.parseString(str);
        JsonObject modules = element.getAsJsonObject();
        JsonElement moduleElement = modules.get("module");
        if (moduleElement == null) {
            return null;
        }
        JsonArray jsonArray = moduleElement.getAsJsonArray();
        if (jsonArray.size() != 1) {
            return null;
        }
        return parse(jsonArray.get(0));
    }
}
