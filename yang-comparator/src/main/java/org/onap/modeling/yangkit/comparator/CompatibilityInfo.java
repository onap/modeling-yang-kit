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

package org.onap.modeling.yangkit.comparator;

public class CompatibilityInfo {
    private final CompatibilityRule.Compatibility compatibility;
    private final String description;

    /**
     * constructor.
     * @param compatibility compatibility
     * @param description description
     */
    public CompatibilityInfo(CompatibilityRule.Compatibility compatibility, String description) {
        this.compatibility = compatibility;
        this.description = description;
    }

    /**
     * get the compatibility.
     * @return compatibility.
     */
    public CompatibilityRule.Compatibility getCompatibility() {
        return compatibility;
    }

    /**
     * get the description.
     * @return string.
     */
    public String getDescription() {
        return description;
    }
}
