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


public class YangPackage {
    private final String name;
    private final String revision;
    private String timestamp;
    private String organization;
    private String contact;
    private String description;
    private String reference;
    private boolean complete = true;
    private boolean local;
    private List<String> tags = new ArrayList<>();
    private List<String> mandatoryFeatures = new ArrayList<>();
    private List<PackageInfo> includePackages = new ArrayList<>();
    private List<ModuleInfo> modules = new ArrayList<>();
    private List<ModuleInfo> importOnlyModules = new ArrayList<>();

    /**
     * constructor.
     * @param name package name
     * @param revision package revision
     */
    public YangPackage(String name, String revision) {
        this.name = name;
        this.revision = revision;
    }

    /**
     * get the package name.
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * get the package revision.
     * @return revision
     */
    public String getRevision() {
        return revision;
    }

    /**
     * get the timestamp.
     * @return timestamp.
     */
    public String getTimestamp() {
        return timestamp;
    }

    /**
     * set the timestamp.
     * @param timestamp timestamp
     */
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * get organization.
     * @return organization.
     */
    public String getOrganization() {
        return organization;
    }

    /**
     * set organization.
     * @param organization organization
     */
    public void setOrganization(String organization) {
        this.organization = organization;
    }

    /**
     * get contact information.
     * @return contact.
     */
    public String getContact() {
        return contact;
    }

    /**
     * set the contact.
     * @param contact contact.
     */
    public void setContact(String contact) {
        this.contact = contact;
    }

    /**
     * get description.
     * @return description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * set description.
     * @param description description.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * get reference.
     * @return reference.
     */
    public String getReference() {
        return reference;
    }

    /**
     * set reference.
     * @param reference reference.
     */
    public void setReference(String reference) {
        this.reference = reference;
    }

    /**
     * whether the package is complete.
     * @return true or false.
     */
    public boolean isComplete() {
        return complete;
    }

    /**
     * set whether the package is complete.
     * @param complete true or false.
     */
    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    /**
     * whether the package is local.
     * @return true or false
     */
    public boolean isLocal() {
        return local;
    }

    /**
     * set whether the package is local.
     * @param local true or false
     */
    public void setLocal(boolean local) {
        this.local = local;
    }

    /**
     * get the tags of package.
     * @return the list of tags.
     */
    public List<String> getTags() {
        return tags;
    }

    /**
     * set the tags of package.
     * @param tags the list of tags.
     */
    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    /**
     * add a tag to package.
     * @param tag tag
     */
    public void addTag(String tag) {
        if (tags.contains(tag)) {
            return;
        }
        tags.add(tag);
    }

    /**
     * get mandatory features of package.
     * @return list of features.
     */
    public List<String> getMandatoryFeatures() {
        return mandatoryFeatures;
    }

    /**
     * set mandatory features.
     * @param mandatoryFeatures list of mandatory features.
     */
    public void setMandatoryFeatures(List<String> mandatoryFeatures) {
        this.mandatoryFeatures = mandatoryFeatures;
    }

    /**
     * add mandatory feature.
     * @param feature feature
     */
    public void addMandatoryFeature(String feature) {
        if (mandatoryFeatures.contains(feature)) {
            return;
        }
        mandatoryFeatures.add(feature);
    }

    /**
     * get include packages.
     * @return list of included packages.
     */
    public List<PackageInfo> getIncludePackages() {
        return includePackages;
    }

    /**
     * set the include packages.
     * @param includePackages the list of include packages.
     */
    public void setIncludePackages(List<PackageInfo> includePackages) {
        this.includePackages = includePackages;
    }

    /**
     * add a include package.
     * @param packageInfo package
     */
    public void addIncludePackage(PackageInfo packageInfo) {
        if (includePackages.contains(packageInfo)) {
            return;
        }
        includePackages.add(packageInfo);
    }

    /**
     * get the modules of package.
     * @return list of modules.
     */
    public List<ModuleInfo> getModules() {
        return modules;
    }

    /**
     * set the modules of package.
     * @param modules list of modules
     */
    public void setModules(List<ModuleInfo> modules) {
        this.modules = modules;
    }

    /**
     * add a module to package.
     * @param module module
     */
    public void addModule(ModuleInfo module) {
        if (this.modules.contains(module)) {
            return;
        }
        modules.add(module);
    }

    /**
     * get import-only modules.
     * @return list of modules.
     */
    public List<ModuleInfo> getImportOnlyModules() {
        return importOnlyModules;
    }

    /**
     * add a import-only module.
     * @param module module
     */
    public void addImportOnlyModule(ModuleInfo module) {
        if (importOnlyModules.contains(module)) {
            return;
        }
        importOnlyModules.add(module);
    }

    /**
     * set import-only modules.
     * @param importOnlyModules list of modules
     */
    public void setImportOnlyModules(List<ModuleInfo> importOnlyModules) {
        this.importOnlyModules = importOnlyModules;
    }

    /**
     * serialize yang package to json.
     * @return json element
     */
    public JsonElement serialize() {
        JsonObject document = new JsonObject();
        JsonObject instanceDataset = new JsonObject();
        document.add("ietf-yang-instance-data:instance-data-set", instanceDataset);
        instanceDataset.addProperty("name", this.getName());
        JsonObject contentSchema = new JsonObject();
        instanceDataset.add("content-schema", contentSchema);
        JsonArray moduleArray = new JsonArray();
        moduleArray.add("ietf-yang-package-instance@2020-01-21");
        contentSchema.add("module", moduleArray);
        JsonObject contentData = new JsonObject();
        instanceDataset.add("content-data", contentData);
        JsonObject yangPackage = new JsonObject();
        contentData.add("ietf-yang-package-instance:yang-package", yangPackage);
        yangPackage.addProperty("name", this.getName());
        yangPackage.addProperty("version", this.getRevision());
        // SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        // String dateTime = sdf.format(new Date(System.currentTimeMillis()));
        if (this.getTimestamp() != null) {
            yangPackage.addProperty("timestamp", this.getTimestamp());
        }

        if (this.getOrganization() != null) {
            yangPackage.addProperty("organization", this.getOrganization());
        }
        if (this.getContact() != null) {
            yangPackage.addProperty("contact", this.getContact());
        }
        if (this.getDescription() != null) {
            yangPackage.addProperty("description", this.getDescription());
        }
        if (this.getReference() != null) {
            yangPackage.addProperty("reference", this.getReference());
        }
        yangPackage.addProperty("complete", this.isComplete());
        yangPackage.addProperty("local", this.isLocal());

        if (!this.getTags().isEmpty()) {
            JsonArray tags = new JsonArray();
            for (String tag : this.getTags()) {
                tags.add(tag);
            }
            yangPackage.add("tag", tags);
        }
        if (!this.getMandatoryFeatures().isEmpty()) {
            JsonArray mandatoryFeatures = new JsonArray();
            for (String mandatoryFeature : this.getMandatoryFeatures()) {
                mandatoryFeatures.add(mandatoryFeature);
            }
            yangPackage.add("mandatory-feature", mandatoryFeatures);
        }
        if (!this.getIncludePackages().isEmpty()) {
            JsonArray includePackages = new JsonArray();
            for (PackageInfo packageInfo : this.getIncludePackages()) {
                includePackages.add(packageInfo.serialize());
            }
            yangPackage.add("included-package", includePackages);
        }
        if (!this.getModules().isEmpty()) {
            JsonArray modules = new JsonArray();
            for (ModuleInfo moduleInfo : this.getModules()) {
                modules.add(moduleInfo.serialize());
            }
            yangPackage.add("module", modules);
        }

        if (!this.getImportOnlyModules().isEmpty()) {
            JsonArray importOnlyModules = new JsonArray();
            for (ModuleInfo importOnlyModule : this.getImportOnlyModules()) {
                importOnlyModules.add(importOnlyModule.serialize());
            }
            yangPackage.add("import-only-module", importOnlyModules);
        }
        return yangPackage;
    }
}
