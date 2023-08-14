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

import org.yangcentral.yangkit.model.api.stmt.Module;


public interface YangCompareResult<T> {
    /**
     * get change type.
     * @return change type.
     */
    ChangeType getChangeType();

    /**
     * get left.
     * @return left
     */
    T getLeft();

    /**
     * get right.
     * @return right
     */
    T getRight();

    /**
     * get change description.
     * @param indent indent
     * @return string
     */
    String getChangeDescription(String indent);

    /**
     * get module.
     * @return module
     */
    Module getModule();

    /**
     * get compatibility info.
     * @return compatibility info.
     */
    CompatibilityInfo getCompatibilityInfo();

    /**
     * set compatibility info.
     * @param compatibilityInfo compatibility info
     */
    void setCompatibilityInfo(CompatibilityInfo compatibilityInfo);
}
