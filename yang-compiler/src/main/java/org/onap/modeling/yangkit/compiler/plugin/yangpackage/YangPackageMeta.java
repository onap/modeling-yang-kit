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

import java.util.ArrayList;
import java.util.List;


public class YangPackageMeta {
    private String name;
    private String revision;
    private String organization;
    private String contact;
    private String description;
    private String reference;
    private boolean local;
    private final List<String> tags = new ArrayList<>();
    private final List<String> mandatoryFeatures = new ArrayList<>();
    private final List<PackageInfo> includePackages = new ArrayList<>();

    /**
     * get the package name.
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * get revision of package.
     * @return revision
     */
    public String getRevision() {
        return revision;
    }

    /**
     * get organization.
     * @return organization.
     */
    public String getOrganization() {
        return organization;
    }

    /**
     * get contact.
     * @return contact.
     */
    public String getContact() {
        return contact;
    }

    /**
     * get description.
     * @return description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * get reference.
     * @return reference.
     */
    public String getReference() {
        return reference;
    }

    /**
     * whether the package is local.
     * @return true or false
     */
    public boolean isLocal() {
        return local;
    }

    /**
     * get tags of package.
     * @return list of tags.
     */
    public List<String> getTags() {
        return tags;
    }

    /**
     * get mandatory features.
     * @return list of features/
     */
    public List<String> getMandatoryFeatures() {
        return mandatoryFeatures;
    }

    /**
     * get include packages.
     * @return list of packages.
     */
    public List<PackageInfo> getIncludePackages() {
        return includePackages;
    }

    /**
     * deserialized package meta from json.
     * @param metaDoc json element
     */
    public void deserialize(JsonElement metaDoc) {
        JsonObject jsonObject = metaDoc.getAsJsonObject();
        JsonElement nameElement = jsonObject.get("name");
        if (nameElement != null) {
            this.name = nameElement.getAsString();
        }
        JsonElement versionElement = jsonObject.get("version");
        if (versionElement != null) {
            this.revision = versionElement.getAsString();
        }
        JsonElement organizationElement = jsonObject.get("organization");
        if (organizationElement != null) {
            this.organization = organizationElement.getAsString();
        }
        JsonElement contactElement = jsonObject.get("contact");
        if (contactElement != null) {
            this.contact = contactElement.getAsString();
        }
        JsonElement descElement = jsonObject.get("description");
        if (descElement != null) {
            this.description = descElement.getAsString();
        }
        JsonElement referElement = jsonObject.get("reference");
        if (referElement != null) {
            this.reference = referElement.getAsString();
        }

        JsonElement localElement = jsonObject.get("local");
        if (localElement != null) {
            this.local = referElement.getAsBoolean();
        }
        JsonElement tagElement = jsonObject.get("tag");
        if (tagElement != null) {
            JsonArray tagArray = tagElement.getAsJsonArray();
            int size = tagArray.size();
            for (int i = 0; i < size; i++) {
                JsonElement tagIns = tagArray.get(i);
                this.tags.add(tagIns.getAsString());
            }
        }

        JsonElement mandatoryFeaturesElement = jsonObject.get("mandatory-feature");
        if (mandatoryFeaturesElement != null) {
            JsonArray mandatoryFeaturesArray = mandatoryFeaturesElement.getAsJsonArray();
            int size = mandatoryFeaturesArray.size();
            for (int i = 0; i < size; i++) {
                JsonElement featureIns = mandatoryFeaturesArray.get(i);
                this.mandatoryFeatures.add(featureIns.getAsString());
            }
        }
        JsonElement includePackagesElement = jsonObject.get("include-package");
        if (includePackagesElement != null) {
            JsonArray includePackageArray = includePackagesElement.getAsJsonArray();
            int size = includePackageArray.size();
            for (int i = 0; i < size; i++) {
                JsonElement includePackageIns = includePackageArray.get(i);
                JsonObject includePackageObj = includePackageIns.getAsJsonObject();
                PackageInfo packageInfo = new PackageInfo(includePackageObj.get("name").getAsString(),
                        includePackageObj.get("version").getAsString());
                this.includePackages.add(packageInfo);
            }
        }

    }
}
