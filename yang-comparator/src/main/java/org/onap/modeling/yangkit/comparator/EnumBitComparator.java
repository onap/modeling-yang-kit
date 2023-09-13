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

import org.yangcentral.yangkit.model.api.stmt.YangStatement;


public class EnumBitComparator extends CommonYangStatementComparator {

    /**
     * get default compatibility.
     * @param left       left statement
     * @param right      right statement
     * @param changeInfo change information.
     * @return default compatibility
     */
    @Override
    protected CompatibilityInfo defaultCompatibility(YangStatement left, YangStatement right,
                                                     CompatibilityRule.ChangeInfo changeInfo) {
        if (changeInfo == CompatibilityRule.ChangeInfo.DELETED) {
            return new CompatibilityInfo(CompatibilityRule.Compatibility.NBC,
                    "delete an enum, it's non-backward-compatible.");
        } else if (changeInfo == CompatibilityRule.ChangeInfo.ADDED) {
            return new CompatibilityInfo(CompatibilityRule.Compatibility.BC,
                    "add a new enum, it's backward-compatible.");
        }
        return super.defaultCompatibility(left, right, changeInfo);
    }
}
