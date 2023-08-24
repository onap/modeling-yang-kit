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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class SubModuleInfo {
    private final String name;
    private final String revision;
    private List<URI> locations = new ArrayList<>();

    /**
     * constructor.
     * @param name submodule name
     * @param revision submodule revision
     */
    public SubModuleInfo(String name, String revision) {
        this.name = name;
        this.revision = revision;
    }

    /**
     * get the submodule name.
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * get the revision of submodule.
     * @return revision
     */
    public String getRevision() {
        return revision;
    }

    /**
     * get locations of submodule.
     * @return list of location.
     */
    public List<URI> getLocations() {
        return locations;
    }

    /**
     * set the locations.
     * @param locations list of location.
     */
    public void setLocations(List<URI> locations) {
        this.locations = locations;
    }

    /**
     * add a location.
     * @param location location uri
     */
    public void addLocation(URI location) {
        if (locations.contains(location)) {
            return;
        }
        locations.add(location);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof SubModuleInfo)) {
            return false;
        }
        SubModuleInfo that = (SubModuleInfo) obj;
        return getName().equals(that.getName()) && getRevision().equals(that.getRevision());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getRevision());
    }

    /**
     * serialize submodule to json.
     * @return json element.
     */
    public JsonElement serialize() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", this.getName());
        if (this.getRevision() != null) {
            jsonObject.addProperty("revision", this.getRevision());
        }

        if (!this.getLocations().isEmpty()) {
            JsonArray locations = new JsonArray();
            for (URI location : this.getLocations()) {
                locations.add(location.toString());
            }
            jsonObject.add("location", locations);
        }
        return jsonObject;

    }
}
