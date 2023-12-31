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

import java.util.AbstractMap;
import java.util.Map;


public class PackageInfo extends SubComponentInfo {
    /**
     * constructor.
     * @param name package name
     * @param revision package revision.
     */
    public PackageInfo(String name, String revision) {
        super(name, revision);
    }

    /**
     * serialize the revision.
     * @return serialized revision.
     */
    @Override
    protected Map.Entry<String, String> serializeRevision() {
        if (this.getRevision() == null) {
            return null;
        }
        Map.Entry<String, String> revisionEntry = new AbstractMap.SimpleEntry<>("version", this.getRevision());
        return revisionEntry;
    }
}
