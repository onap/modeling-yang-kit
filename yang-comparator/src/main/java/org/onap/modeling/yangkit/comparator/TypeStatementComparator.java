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

import org.yangcentral.yangkit.model.api.restriction.YangInteger;
import org.yangcentral.yangkit.model.api.stmt.Type;
import org.yangcentral.yangkit.model.api.stmt.YangStatement;



/**
 * comparator for type statement.
 *
 * @author lllyfeng
 * @since 2022-06-10
 */
public class TypeStatementComparator extends CommonYangStatementComparator<Type> {
    @Override
    protected List<CompatibilityRule.ChangeInfo> getChangeInfo(Type left, Type right) {
        List<CompatibilityRule.ChangeInfo> changeInfos = new ArrayList<>();
        if (left == null && right != null) {
            changeInfos.add(CompatibilityRule.ChangeInfo.ADDED);
            return changeInfos;
        } else if (left != null && right == null) {
            changeInfos.add(CompatibilityRule.ChangeInfo.DELETED);
            return changeInfos;
        } else if (left == null && right == null) {
            return changeInfos;
        }
        if (left.getRestriction().getClass() == right.getRestriction().getClass()) {
            return changeInfos;
        }
        if (left.getRestriction().getClass() != right.getRestriction().getClass()) {
            if ((left.getRestriction() instanceof YangInteger) && (right.getRestriction() instanceof YangInteger)) {
                changeInfos.add(CompatibilityRule.ChangeInfo.INTEGER_TYPE_CHANGED);
            } else {
                if (!changeInfos.contains(CompatibilityRule.ChangeInfo.CHANGED)) {
                    changeInfos.add(CompatibilityRule.ChangeInfo.CHANGED);
                }
            }
        }
        return changeInfos;
    }

    @Override
    public List<YangCompareResult> compare(Type left, Type right) {
        List<YangCompareResult> compareResults = new ArrayList<>();
        List<CompatibilityRule.ChangeInfo> changeInfos = getChangeInfo(left, right);

        if (!changeInfos.isEmpty()) {
            YangStatement effectiveStmt = left == null ? right : left;
            String statement = getStatement(effectiveStmt);
            String parentStmt = getStatement(effectiveStmt.getParentStatement());
            for (CompatibilityRule.ChangeInfo changeInfo : changeInfos) {
                CompatibilityRule compatibilityRule = null;
                if (getCompatibilityRules() != null) {
                    compatibilityRule = getCompatibilityRules().searchRule(statement, parentStmt, changeInfo);
                }
                if (compatibilityRule == null) {
                    if (changeInfo == CompatibilityRule.ChangeInfo.SEQUENCE_CHANGED) {
                        continue;
                    }
                    YangStatementCompareResult statementCompareResult =
                            new YangStatementCompareResult(getChangeType(changeInfo), left, right);
                    statementCompareResult.setCompatibilityInfo(defaultCompatibility(left, right, changeInfo));
                    compareResults.add(statementCompareResult);
                } else {
                    YangStatementCompareResult statementCompareResult =
                            new YangStatementCompareResult(getChangeType(changeInfo), left, right);
                    statementCompareResult.setCompatibilityInfo(
                            new CompatibilityInfo(compatibilityRule.getCompatibility(),
                                    compatibilityRule.getDescription()));
                    compareResults.add(statementCompareResult);
                }

            }
        }

        //sub statements
        if (left != null && right != null) {
            if (left.getRestriction().getClass() != right.getRestriction().getClass()) {
                if ((left.getRestriction() instanceof YangInteger) && (right.getRestriction() instanceof YangInteger)) {
                    compareResults.addAll(compareChildren(left, right));
                }
            } else {
                compareResults.addAll(compareChildren(left, right));
            }

        }
        return compareResults;
    }

    @Override
    protected CompatibilityInfo defaultCompatibility(Type left, Type right,
                                                     CompatibilityRule.ChangeInfo changeInfo) {
        if (changeInfo == CompatibilityRule.ChangeInfo.CHANGED
                || changeInfo == CompatibilityRule.ChangeInfo.INTEGER_TYPE_CHANGED
                || changeInfo == CompatibilityRule.ChangeInfo.DELETED) {
            return new CompatibilityInfo(CompatibilityRule.Compatibility.NBC, null);
        }

        return super.defaultCompatibility(left, right, changeInfo);
    }


}
