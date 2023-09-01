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

import org.yangcentral.yangkit.model.api.stmt.Base;
import org.yangcentral.yangkit.model.api.stmt.Identity;
import org.yangcentral.yangkit.model.api.stmt.Type;

/**
 * comparator for base statement.
 *
 * @since 2022-06-15
 */
public class BaseComparator extends CommonYangStatementComparator<Base> {


    @Override
    protected CompatibilityInfo defaultCompatibility(Base left, Base right,
                                                     CompatibilityRule.ChangeInfo changeInfo) {
        Base temp = left;
        if (temp == null) {
            temp = right;
        }
        if (temp.getParentStatement() instanceof Identity) {
            if (changeInfo == CompatibilityRule.ChangeInfo.DELETED) {
                return new CompatibilityInfo(CompatibilityRule.Compatibility.NBC,
                        "delete a base for identity,it's non-backward-compatible.");
            }
        } else if (temp.getParentStatement() instanceof Type) {
            if (changeInfo == CompatibilityRule.ChangeInfo.ADDED) {
                return new CompatibilityInfo(CompatibilityRule.Compatibility.NBC,
                        "add a new base for identity-ref, it's non-backward-compatible.");
            }
        }
        return super.defaultCompatibility(left, right, changeInfo);
    }
}
