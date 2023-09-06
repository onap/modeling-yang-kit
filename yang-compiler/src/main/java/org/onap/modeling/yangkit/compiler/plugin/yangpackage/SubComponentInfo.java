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

package org.onap.modeling.yangkit.compiler.plugin.yangpackage;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public abstract class SubComponentInfo {
    private final String name;
    private String revision;
    private final List<String> replaceRevisions = new ArrayList<>();
    private List<URI> locations;

    /**
     * constructor.
     * @param name the name of subcomponent.
     * @param revision the revision of subcomponent.
     */
    public SubComponentInfo(String name, String revision) {
        this.name = name;
        this.revision = revision;
    }

    /**
     * constructor.
     * @param name the name of subcomponent.
     */
    public SubComponentInfo(String name) {
        this.name = name;
    }

    /**
     * get the name of subcomponent.
     * @return name.
     */
    public String getName() {
        return name;
    }

    /**
     * get the revision of subcomponent.
     * @return revision
     */
    public String getRevision() {
        return revision;
    }

    /**
     * set the revision of subcomponent.
     * @param revision revision
     */
    public void setRevision(String revision) {
        this.revision = revision;
    }

    /**
     * get the replaced revisions.
     * @return the list of revisions.
     */
    public List<String> getReplaceRevisions() {
        return replaceRevisions;
    }

    /**
     * add replace revision.
     * @param revision the revision to be replaced.
     */
    public void addReplaceRevision(String revision) {
        if (replaceRevisions.contains(revision)) {
            return;
        }
        replaceRevisions.add(revision);
    }

    /**
     * get locations of subcomponent.
     * @return list of location uri.
     */
    public List<URI> getLocations() {
        return locations;
    }

    /**
     * add location.
     * @param location location uri.
     */
    public void addLocation(URI location) {
        if (locations.contains(location)) {
            return;
        }
        locations.add(location);
    }

    /**
     * equals method.
     * @param obj other object.
     * @return true or false.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof SubComponentInfo)) {
            return false;
        }
        SubComponentInfo that = (SubComponentInfo) obj;
        return getName().equals(that.getName()) && getRevision().equals(that.getRevision());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getRevision());
    }

    /**
     * the abstract method of serialize the revision.
     * @return serialized revision.
     */
    protected abstract Map.Entry<String, String> serializeRevision();

    /**
     * serialize the subcomponent to json element.
     * @return json element.
     */
    public JsonElement serialize() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", this.getName());
        if (this.getRevision() != null) {
            Map.Entry<String, String> revisionEntry = serializeRevision();
            jsonObject.addProperty(revisionEntry.getKey(), revisionEntry.getValue());
        }
        if (!this.getReplaceRevisions().isEmpty()) {
            JsonArray replaceRevisions = new JsonArray();
            for (String replaceRevision : this.getReplaceRevisions()) {
                replaceRevisions.add(replaceRevision);
            }
            jsonObject.add("replaces-version", replaceRevisions);
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
