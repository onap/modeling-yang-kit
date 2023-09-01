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

import org.yangcentral.yangkit.model.api.stmt.type.SectionExpression;

/**
 * comparator for range/length statement.
 *
 * @author lllyfeng
 * @since 2022-06-14
 */
public class RangeLengthComparator extends CommonYangStatementComparator<SectionExpression> {
    @Override
    protected List<CompatibilityRule.ChangeInfo> getChangeInfo(SectionExpression left, SectionExpression right) {
        List<CompatibilityRule.ChangeInfo> changeInfos = new ArrayList<>();
        if (!left.equals(right)) {
            if (left.isSubSet(right)) {
                changeInfos.add(CompatibilityRule.ChangeInfo.EXPAND);
            } else if (right.isSubSet(left)) {
                changeInfos.add(CompatibilityRule.ChangeInfo.REDUCE);
            }
        }

        List<CompatibilityRule.ChangeInfo> superChangeInfos = super.getChangeInfo(left, right);
        if (superChangeInfos.contains(CompatibilityRule.ChangeInfo.SEQUENCE_CHANGED)) {
            changeInfos.add(CompatibilityRule.ChangeInfo.SEQUENCE_CHANGED);
        }
        return changeInfos;
    }

    @Override
    protected CompatibilityInfo defaultCompatibility(SectionExpression left, SectionExpression right,
                                                     CompatibilityRule.ChangeInfo changeInfo) {
        if (changeInfo == CompatibilityRule.ChangeInfo.REDUCE) {
            return new CompatibilityInfo(CompatibilityRule.Compatibility.NBC, "the range or length is reduced, "
                    + "it's non-backward-compatible.");
        }
        return super.defaultCompatibility(left, right, changeInfo);
    }
}
