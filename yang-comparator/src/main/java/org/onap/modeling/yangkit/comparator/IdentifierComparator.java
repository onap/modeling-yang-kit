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

/**
 * comparator for identifier.
 *
 * @since 2022-06-15
 */
public class IdentifierComparator extends CommonYangStatementComparator {
    @Override
    protected CompatibilityInfo defaultCompatibility(YangStatement left, YangStatement right,
                                                     CompatibilityRule.ChangeInfo changeInfo) {
        if (changeInfo == CompatibilityRule.ChangeInfo.DELETED) {
            return new CompatibilityInfo(CompatibilityRule.Compatibility.NBC, "the statement is deleted, "
                    + "it's non-backward-compatible.");
        }
        return super.defaultCompatibility(left, right, changeInfo);
    }
}
