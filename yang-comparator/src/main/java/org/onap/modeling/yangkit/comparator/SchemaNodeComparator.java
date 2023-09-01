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

import org.yangcentral.yangkit.model.api.stmt.SchemaNode;
import org.yangcentral.yangkit.model.api.stmt.SchemaNodeContainer;



public class SchemaNodeComparator<T extends SchemaNode> extends CommonYangStatementComparator<T> {
    /**
     *
     * @param left       left statement
     * @param right      right statement
     * @return
     */
    @Override
    protected List<CompatibilityRule.ChangeInfo> getChangeInfo(T left, T right) {
        List<CompatibilityRule.ChangeInfo> changeInfos = new ArrayList<>();
        if (left == null && right != null) {
            if (right.isMandatory()) {
                changeInfos.add(CompatibilityRule.ChangeInfo.MANDATORY_ADDED);
            } else {
                changeInfos.add(CompatibilityRule.ChangeInfo.ADDED);
            }
        } else if (left != null && right == null) {
            changeInfos.add(CompatibilityRule.ChangeInfo.DELETED);
        } else if (left != null && right != null) {
            changeInfos.addAll(super.getChangeInfo(left, right));
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
    protected CompatibilityInfo defaultCompatibility(T left, T right,
                                                     CompatibilityRule.ChangeInfo changeInfo) {
        if (changeInfo == CompatibilityRule.ChangeInfo.MANDATORY_ADDED
                || changeInfo == CompatibilityRule.ChangeInfo.DELETED) {
            return new CompatibilityInfo(CompatibilityRule.Compatibility.NBC, null);
        }
        return super.defaultCompatibility(left, right, changeInfo);
    }


    /**
     *
     * @param left       left statement
     * @param right      right statement
     * @return
     */
    @Override
    protected List<YangCompareResult> compareChildren(T left, T right) {
        List<YangCompareResult> results = new ArrayList<>();
        results.addAll(super.compareChildren(left, right));

        SchemaNode temp = left;
        if (temp == null) {
            temp = right;
        }
        if ((temp instanceof SchemaNodeContainer)) {
            SchemaNodeContainer leftContainer = (SchemaNodeContainer) left;
            SchemaNodeContainer rightContainer = (SchemaNodeContainer) right;
            results.addAll(compareStatements(leftContainer == null ? new ArrayList<>() :
                            YangComparator.getEffectiveSchemaNodeChildren(leftContainer),
                    rightContainer == null ? new ArrayList<>() :
                            YangComparator.getEffectiveSchemaNodeChildren(rightContainer), OPTION_ONLY_SCHEMA));
        }
        return results;
    }

    /**
     *
     * @param left       left statement
     * @param right      right statement
     * @return
     */
    @Override
    public List<YangCompareResult> compare(T left, T right) {
        List<YangCompareResult> compareResults = new ArrayList<>();
        if (left == null && right == null) {
            return compareResults;
        }
        List<CompatibilityRule.ChangeInfo> changeInfos = getChangeInfo(left, right);
        List<YangTreeCompareResult> treeCompareResults = new ArrayList<>();
        SchemaNode temp = right;
        if (temp == null) {
            temp = left;
        }
        String statement = getStatement(temp);
        String parentStatement = getStatement(temp.getParentStatement());
        if (!changeInfos.isEmpty()) {
            for (CompatibilityRule.ChangeInfo changeInfo : changeInfos) {

                CompatibilityRule compatibilityRule = null;
                if (getCompatibilityRules() != null) {
                    compatibilityRule = getCompatibilityRules().searchRule(statement, parentStatement, changeInfo);
                }
                if (compatibilityRule == null) {
                    if (changeInfo == CompatibilityRule.ChangeInfo.SEQUENCE_CHANGED) {
                        continue;
                    }
                    YangTreeCompareResult treeCompareResult =
                            new YangTreeCompareResult(temp.getSchemaPath(), getChangeType(changeInfo));
                    treeCompareResult.setLeft(left);
                    treeCompareResult.setRight(right);
                    treeCompareResult.setCompatibilityInfo(defaultCompatibility(left, right, changeInfo));
                    treeCompareResults.add(treeCompareResult);
                } else {
                    YangTreeCompareResult treeCompareResult =
                            new YangTreeCompareResult(temp.getSchemaPath(), getChangeType(changeInfo));
                    treeCompareResult.setLeft(left);
                    treeCompareResult.setRight(right);
                    treeCompareResult.setCompatibilityInfo(new CompatibilityInfo(compatibilityRule.getCompatibility(),
                            compatibilityRule.getDescription()));
                    treeCompareResults.add(treeCompareResult);
                }
            }
            compareResults.addAll(treeCompareResults);
        }

        //sub statements
        if (left != null && right != null) {
            List<YangCompareResult> childrenResults = compareChildren(left, right);
            for (YangCompareResult childResult : childrenResults) {
                if (childResult instanceof YangStatementCompareResult) {
                    if (treeCompareResults.isEmpty()) {
                        YangTreeCompareResult treeCompareResult =
                                new YangTreeCompareResult(temp.getSchemaPath(), ChangeType.MODIFY);
                        treeCompareResult.setLeft(left);
                        treeCompareResult.setRight(right);
                        treeCompareResult.setCompatibilityInfo(
                                new CompatibilityInfo(CompatibilityRule.Compatibility.BC, null));
                        treeCompareResults.add(treeCompareResult);
                        compareResults.add(treeCompareResult);
                    }
                    for (YangTreeCompareResult treeCompareResult : treeCompareResults) {
                        treeCompareResult.addMetaCompareResult(childResult);
                        if (childResult.getCompatibilityInfo().getCompatibility()
                                == CompatibilityRule.Compatibility.NBC) {
                            treeCompareResult.setCompatibilityInfo(
                                    new CompatibilityInfo(CompatibilityRule.Compatibility.NBC, null));
                        } else if (childResult.getCompatibilityInfo().getCompatibility()
                                == CompatibilityRule.Compatibility.UNKNOWN) {
                            if (treeCompareResult.getCompatibilityInfo().getCompatibility()
                                    == CompatibilityRule.Compatibility.BC) {
                                treeCompareResult.setCompatibilityInfo(
                                        new CompatibilityInfo(CompatibilityRule.Compatibility.UNKNOWN, null));
                            }
                        }
                    }
                } else {
                    compareResults.add(childResult);
                }
            }
        }
        return compareResults;
    }
}
