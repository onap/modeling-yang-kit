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

import java.util.ArrayList;
import java.util.List;

import org.yangcentral.yangkit.model.api.stmt.MinElements;

/**
 * comparator for min-elements statement.
 *
 * @author lllyfeng
 * @since 2022-06-15
 */
public class MinElementsComparator extends CommonYangStatementComparator<MinElements> {
    @Override
    protected List<CompatibilityRule.ChangeInfo> getChangeInfo(MinElements left, MinElements right) {
        List<CompatibilityRule.ChangeInfo> changeInfos = new ArrayList<>();
        if (left.getValue() > right.getValue()) {
            changeInfos.add(CompatibilityRule.ChangeInfo.EXPAND);
        } else if (left.getValue() < right.getValue()) {
            changeInfos.add(CompatibilityRule.ChangeInfo.REDUCE);
        }
        if (super.getChangeInfo(left, right).contains(CompatibilityRule.ChangeInfo.SEQUENCE_CHANGED)) {
            changeInfos.add(CompatibilityRule.ChangeInfo.SEQUENCE_CHANGED);
        }
        return changeInfos;
    }

    @Override
    protected CompatibilityInfo defaultCompatibility(MinElements left, MinElements right,
                                                     CompatibilityRule.ChangeInfo changeInfo) {
        if (changeInfo == CompatibilityRule.ChangeInfo.REDUCE) {
            return new CompatibilityInfo(CompatibilityRule.Compatibility.NBC, "min-elements is increased,"
                    + "it's non-backward-compatible.");
        }
        return super.defaultCompatibility(left, right, changeInfo);
    }
}
