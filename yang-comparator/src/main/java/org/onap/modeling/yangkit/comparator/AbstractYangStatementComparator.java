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

import java.util.List;

import org.yangcentral.yangkit.model.api.stmt.YangStatement;


public abstract class AbstractYangStatementComparator<T extends YangStatement> implements YangStatementComparator<T> {
    /**
     * constructor.
     */
    public AbstractYangStatementComparator() {
    }

    /**
     * get compatibility rules.
     *
     * @return rules
     */
    public CompatibilityRules getCompatibilityRules() {
        return CompatibilityRules.getInstance();
    }

    /**
     * get change information.
     *
     * @param left  left statement
     * @param right right statement
     * @return list of change information.
     */
    protected abstract List<CompatibilityRule.ChangeInfo> getChangeInfo(T left, T right);

    /**
     * the default compatibility result.
     *
     * @param left       left statement
     * @param right      right statement
     * @param changeInfo change information
     * @return compatibility result
     */
    protected abstract CompatibilityInfo defaultCompatibility(T left, T right,
                                                              CompatibilityRule.ChangeInfo changeInfo);
}
