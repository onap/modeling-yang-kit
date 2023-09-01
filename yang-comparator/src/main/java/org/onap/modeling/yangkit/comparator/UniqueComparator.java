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
import org.yangcentral.yangkit.model.api.stmt.Leaf;
import org.yangcentral.yangkit.model.api.stmt.Unique;
import org.yangcentral.yangkit.model.api.stmt.YangStatement;


public class UniqueComparator extends CommonYangStatementComparator<Unique> {

    /**
     *
     * @param left       left statement
     * @param right      right statement
     * @return
     */
    @Override
    protected List<CompatibilityRule.ChangeInfo> getChangeInfo(Unique left, Unique right) {
        List<CompatibilityRule.ChangeInfo> changeInfos = new ArrayList<>();
        if (left == null && right != null) {
            changeInfos.add(CompatibilityRule.ChangeInfo.ADDED);
        } else if (left != null && right == null) {
            changeInfos.add(CompatibilityRule.ChangeInfo.DELETED);
        } else {
            if (!left.equals(right)) {
                int leftSize = left.getUniqueNodes().size();
                int rightSize = right.getUniqueNodes().size();
                if (leftSize == rightSize) {
                    changeInfos.add(CompatibilityRule.ChangeInfo.CHANGED);
                } else if (leftSize < rightSize) {
                    boolean notfind = false;
                    for (int i = 0; i < leftSize; i++) {
                        Leaf leftUnique = left.getUniqueNodes().get(i);
                        List<YangStatement> rightUniques = new ArrayList<>();
                        for (Leaf rightUnique : right.getUniqueNodes()) {
                            rightUniques.add(rightUnique);
                        }
                        if (searchStatement(leftUnique, rightUniques, new ArrayList<>()) == null) {
                            notfind = true;
                        }
                    }
                    if (notfind) {
                        changeInfos.add(CompatibilityRule.ChangeInfo.CHANGED);
                    } else {
                        changeInfos.add(CompatibilityRule.ChangeInfo.REDUCE);
                    }
                } else {
                    boolean notfind = false;
                    for (int i = 0; i < rightSize; i++) {
                        Leaf rightUnique = right.getUniqueNodes().get(i);
                        List<YangStatement> leftUniques = new ArrayList<>();
                        for (Leaf leftUnique : left.getUniqueNodes()) {
                            leftUniques.add(leftUnique);
                        }
                        if (searchStatement(rightUnique, leftUniques, new ArrayList<>()) == null) {
                            notfind = true;
                        }
                    }
                    if (notfind) {
                        changeInfos.add(CompatibilityRule.ChangeInfo.CHANGED);
                    } else {
                        changeInfos.add(CompatibilityRule.ChangeInfo.EXPAND);
                    }
                }
            }
        }

        return changeInfos;
    }

    /**
     *
     * @param left       left statement
     * @param right      right statement
     * @param changeInfo
     * @return
     */
    @Override
    protected CompatibilityInfo defaultCompatibility(Unique left, Unique right,
                                                     CompatibilityRule.ChangeInfo changeInfo) {
        if (changeInfo == CompatibilityRule.ChangeInfo.ADDED
                || changeInfo == CompatibilityRule.ChangeInfo.REDUCE) {
            return new CompatibilityInfo(CompatibilityRule.Compatibility.NBC,
                    "add new unique or add a new node on unique is non-backward-compatible.");
        }
        return super.defaultCompatibility(left, right, changeInfo);
    }
}
