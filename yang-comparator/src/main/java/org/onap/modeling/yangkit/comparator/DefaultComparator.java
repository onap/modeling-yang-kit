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

import org.yangcentral.yangkit.model.api.stmt.Default;


public class DefaultComparator extends CommonYangStatementComparator<Default> {

    /**
     * get default compatibility.
     * @param left       left statement
     * @param right      right statement
     * @param changeInfo change information.
     * @return default compatibility
     */
    @Override
    protected CompatibilityInfo defaultCompatibility(Default left, Default right,
                                                     CompatibilityRule.ChangeInfo changeInfo) {
        if (changeInfo == CompatibilityRule.ChangeInfo.DELETED
                || changeInfo == CompatibilityRule.ChangeInfo.CHANGED) {
            return new CompatibilityInfo(CompatibilityRule.Compatibility.NBC,
                    "delete or change default statement, it's non-backward-compatible");
        }
        return super.defaultCompatibility(left, right, changeInfo);
    }
}
