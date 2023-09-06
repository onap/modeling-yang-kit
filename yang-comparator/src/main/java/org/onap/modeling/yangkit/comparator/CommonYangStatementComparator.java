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

import org.yangcentral.yangkit.base.Cardinality;
import org.yangcentral.yangkit.base.YangElement;
import org.yangcentral.yangkit.base.YangStatementDef;
import org.yangcentral.yangkit.model.api.stmt.IdentifierRef;
import org.yangcentral.yangkit.model.api.stmt.Referencable;
import org.yangcentral.yangkit.model.api.stmt.SchemaNode;
import org.yangcentral.yangkit.model.api.stmt.YangBuiltinStatement;
import org.yangcentral.yangkit.model.api.stmt.YangStatement;
import org.yangcentral.yangkit.model.api.stmt.YangUnknown;


public class CommonYangStatementComparator<T extends YangStatement> extends AbstractYangStatementComparator<T> {
    public static final int OPTION_ONLY_META = 1;
    public static final int OPTION_ONLY_SCHEMA = 2;
    public static final int OPTION_ALL = 3;

    /**
     *
     * @param left       left statement
     * @param right      right statement
     * @return
     */
    @Override
    protected List<CompatibilityRule.ChangeInfo> getChangeInfo(T left, T right) {
        List<CompatibilityRule.ChangeInfo> changeInfos = new ArrayList<>();
        if (left == null) {
            changeInfos.add(CompatibilityRule.ChangeInfo.ADDED);
            return changeInfos;
        }

        if (right == null) {
            changeInfos.add(CompatibilityRule.ChangeInfo.DELETED);
            return changeInfos;
        }
        if (!yangStatementIsEqual(left, right)) {
            changeInfos.add(CompatibilityRule.ChangeInfo.CHANGED);
        }

        int leftIndex = getIndex(left);
        int rightIndex = getIndex(right);
        if (leftIndex != rightIndex) {
            changeInfos.add(CompatibilityRule.ChangeInfo.SEQUENCE_CHANGED);
        }
        return changeInfos;
    }

    /**
     *
     * @param stmt
     * @return
     */
    private int getIndex(T stmt) {
        YangStatement parentStmt = stmt.getParentStatement();
        if (null == parentStmt) {
            return -1;
        }
        int index = -1;
        for (int i = 0; i < parentStmt.getSubElements().size(); i++) {
            YangElement subElement = parentStmt.getSubElements().get(i);
            if (subElement instanceof YangStatement) {
                index++;
                if (subElement == stmt) {
                    return index;
                }
            }
        }
        return index;
    }

    /**
     *
     * @param changeInfo
     * @return
     */
    protected ChangeType getChangeType(CompatibilityRule.ChangeInfo changeInfo) {
        switch (changeInfo) {
            case ADDED:
            case MANDATORY_ADDED: {
                return ChangeType.ADD;
            }
            case DELETED: {
                return ChangeType.DELETE;
            }
            default: {
                return ChangeType.MODIFY;
            }
        }
    }


    /**
     *
     * @param left       left statement
     * @param right      right statement
     * @param changeInfo
     * @return
     */
    @Override
    protected CompatibilityInfo defaultCompatibility(T left, T right, CompatibilityRule.ChangeInfo changeInfo) {
        return new CompatibilityInfo(CompatibilityRule.Compatibility.BC, null);
    }

    /**
     * get statement string representation.
     *
     * @param stmt statement
     * @return string
     */
    public static String getStatement(YangStatement stmt) {
        if (stmt == null) {
            return null;
        }
        String statement = null;
        if (stmt instanceof YangBuiltinStatement) {
            statement = stmt.getYangKeyword().getLocalName();
        } else {
            YangUnknown unknown = (YangUnknown) stmt;
            if (unknown.getExtension() == null) {
                return unknown.getKeyword();
            }
            String moduleName = unknown.getExtension().getContext().getCurModule().getMainModule().getArgStr();
            String extensionName = unknown.getExtension().getArgStr();
            statement = moduleName + ":" + extensionName;
        }
        return statement;
    }

    /**
     *
     * @param left       left statement
     * @param right      right statement
     * @return
     */
    protected List<YangCompareResult> compareChildren(T left, T right) {
        List<YangCompareResult> compareResults =
                compareStatements(left == null ? new ArrayList<>() : left.getEffectiveSubStatements(),
                        right == null ? new ArrayList<>() : right.getEffectiveSubStatements(), OPTION_ONLY_META);
        return compareResults;
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
        List<CompatibilityRule.ChangeInfo> changeInfos = getChangeInfo(left, right);
        if (!changeInfos.isEmpty()) {
            YangStatement effectiveStmt = left == null ? right : left;
            String statement = getStatement(effectiveStmt);
            String parentStmt = getStatement(effectiveStmt.getParentStatement());
            for (CompatibilityRule.ChangeInfo changeInfo : changeInfos) {
                if (changeInfo == CompatibilityRule.ChangeInfo.IGNORE) {
                    continue;
                }
                CompatibilityRule compatibilityRule = null;
                if (null != getCompatibilityRules()) {
                    compatibilityRule = getCompatibilityRules().searchRule(statement, parentStmt, changeInfo);
                }
                if (compatibilityRule == null) {
                    //ignore sequence change
                    if (changeInfo != CompatibilityRule.ChangeInfo.SEQUENCE_CHANGED) {
                        YangStatementCompareResult statementCompareResult =
                                new YangStatementCompareResult(getChangeType(changeInfo), left, right);
                        statementCompareResult.setCompatibilityInfo(defaultCompatibility(left, right, changeInfo));
                        compareResults.add(statementCompareResult);
                    }

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
            compareResults.addAll(compareChildren(left, right));
        }
        return compareResults;
    }

    /**
     * calculate the similarity.
     *
     * @param src       source statement
     * @param candidate candidate statement
     * @return similarity
     */
    public static int calSimilarity(YangStatement src, YangStatement candidate) {
        if (!src.getYangKeyword().equals(candidate.getYangKeyword())) {
            return 0;
        }
        int similarity = 1;

        if (!src.equals(candidate)) {
            return similarity;
        }
        similarity++;
        List<YangStatement> srcSubStatements = src.getEffectiveSubStatements();
        List<YangStatement> candidateSubStatements = candidate.getEffectiveSubStatements();
        if (srcSubStatements.size() != candidateSubStatements.size()) {
            return similarity;
        }
        similarity++;
        List<YangStatement> matched = new ArrayList<>();
        for (YangStatement srcSubStatement : srcSubStatements) {
            int maxSubSimilarity = 0;
            YangStatement maxsimliaritySubStatement = null;
            for (YangStatement candidateSubStatment : candidateSubStatements) {
                if (contains(matched, candidateSubStatment)) {
                    continue;
                }
                int subSimilarity = calSimilarity(srcSubStatement, candidateSubStatment);
                if (subSimilarity > maxSubSimilarity) {
                    maxSubSimilarity = subSimilarity;
                    maxsimliaritySubStatement = candidateSubStatment;
                }
            }
            similarity += maxSubSimilarity;
            matched.add(maxsimliaritySubStatement);
        }
        return similarity;

    }

    /**
     * whether list contains an object.
     *
     * @param list a list
     * @param obj  object
     * @return true or false
     */
    public static boolean contains(List list, Object obj) {
        if (list == null) {
            return false;
        }
        for (Object src : list) {
            if (src == obj) {
                return true;
            }
        }
        return false;
    }

    /**
     * search statement from target except matched statements.
     *
     * @param statement statement
     * @param target    target statements
     * @param matched   matched statements
     * @return the searched statement
     */
    public static YangStatement searchStatement(YangStatement statement, List<YangStatement> target,
                                                List<YangStatement> matched) {
        if (null == target || target.size() == 0) {
            return null;
        }
        List<YangStatement> matchedTargetStmts = new ArrayList<>();
        for (YangStatement rightSubStatement : target) {
            if (contains(matched, rightSubStatement)) {
                continue;
            }
            if (yangStatementIsEqual(statement, rightSubStatement)) {
                matchedTargetStmts.add(rightSubStatement);
                continue;
            }
        }
        if (matchedTargetStmts.size() == 1) {
            return matchedTargetStmts.get(0);
        } else if (matchedTargetStmts.size() > 1) {
            int maxSimilarity = 0;
            YangStatement maxSimilarStatement = null;
            for (YangStatement matchedTargetStmt : matchedTargetStmts) {
                int similarity = calSimilarity(statement, matchedTargetStmt);
                if (similarity <= 1) {
                    continue;
                }
                if (similarity > maxSimilarity) {
                    maxSimilarity = similarity;
                    maxSimilarStatement = matchedTargetStmt;
                }
            }
            if (maxSimilarStatement != null) {
                return maxSimilarStatement;
            }
        }

        if (statement.getParentStatement() == null) {
            return null;
        }

        YangStatementDef statementDef = statement.getContext().getYangSpecification()
                .getStatementDef(statement.getParentStatement().getYangKeyword());
        if (statementDef == null) {
            if (statement.getParentStatement() instanceof YangUnknown) {
                //unknown will be treated as cardinality with non-unbounded
                for (YangStatement rightSubStatement : target) {
                    if (contains(matched, rightSubStatement)) {
                        continue;
                    }
                    return rightSubStatement;
                }
                return null;
            } else {
                return null;
            }
        }

        Cardinality cardinality = statementDef.getSubStatementCardinality(statement.getYangKeyword());
        if (cardinality == null) {
            if (statement instanceof YangUnknown) {
                //unknown will be treated as cardinality with non-unbounded
                for (YangStatement rightSubStatement : target) {
                    if (contains(matched, rightSubStatement)) {
                        continue;
                    }
                    return rightSubStatement;
                }
            }
            return null;
        }
        if (cardinality.isUnbounded()) {
            return null;
        }

        for (YangStatement rightSubStatement : target) {
            if (contains(matched, rightSubStatement)) {
                continue;
            }
            return rightSubStatement;
        }
        return null;
    }

    /**
     * whether the left statement is equal with right statement.
     *
     * @param left  left statement
     * @param right right statement
     * @return true or false
     */
    public static boolean yangStatementIsEqual(YangStatement left, YangStatement right) {
        if ((null == left) || (null == right)) {
            if ((null == left) && (null == right)) {
                return true;
            }
            return false;
        }
        if ((left instanceof SchemaNode) && (right instanceof SchemaNode)) {
            SchemaNode leftSchemaNode = (SchemaNode) left;
            SchemaNode rightSchemaNode = (SchemaNode) right;
            if (!left.getContext().getNamespace().equals(right.getContext().getNamespace())) {
                return false;
            }
            if (!yangStatementIsEqual((YangStatement) leftSchemaNode.getClosestAncestorNode(),
                    (YangStatement) rightSchemaNode.getClosestAncestorNode())) {
                return false;
            }
        }
        if ((left instanceof IdentifierRef) && (right instanceof IdentifierRef)) {
            if (!left.getYangKeyword().equals(right.getYangKeyword())) {
                return false;
            }
            YangStatement leftRefStatement = ((IdentifierRef) left).getReferenceStatement();
            YangStatement rightRefStatement = ((IdentifierRef) right).getReferenceStatement();
            if (leftRefStatement != null && leftRefStatement.equals(rightRefStatement)) {
                return true;
            } else if (leftRefStatement == null && rightRefStatement == null) {
                if (left.getArgStr().equals(right.getArgStr())) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
        return left.equals(right);
    }

    /**
     * compare the two list of statements.
     *
     * @param leftElements  the list of left statements
     * @param rightElements the list of right statements
     * @param option        OPTION_ONLY_META =1/OPTION_ONLY_SCHEMA=2/OPTION_ALL=3
     * @return compare result
     */
    public static List<YangCompareResult> compareStatements(List<? extends YangStatement> leftElements,
                                                            List<? extends YangStatement> rightElements, int option) {
        List<YangCompareResult> compareResults = new ArrayList<>();
        List<YangStatement> foundStatements = new ArrayList<>();

        if (leftElements.size() > 0) {
            for (YangStatement subElement : leftElements) {
                YangStatement leftSubStatement = subElement;
                if (option == OPTION_ONLY_META) {
                    if ((leftSubStatement instanceof SchemaNode)
                            || (leftSubStatement instanceof Referencable)) {
                        continue;
                    }
                } else if (option == OPTION_ONLY_SCHEMA) {
                    if (!(leftSubStatement instanceof SchemaNode)) {
                        continue;
                    }
                }
                if (leftSubStatement instanceof SchemaNode) {
                    SchemaNode leftSchemaNode = (SchemaNode) leftSubStatement;
                    if (!leftSchemaNode.isActive()) {
                        continue;
                    }
                }

                List<YangStatement> rightSubStatements = new ArrayList<>();
                for (YangStatement rightElement : rightElements) {
                    if (leftSubStatement.getYangKeyword().equals(rightElement.getYangKeyword())) {
                        rightSubStatements.add(rightElement);
                    }
                }
                if (rightSubStatements.size() == 0) {
                    //no right statement, so change type is delete
                    YangStatementComparator comparator =
                            YangComparatorRegister.getInstance().getComparator(getStatement(leftSubStatement));
                    compareResults.addAll(comparator.compare(leftSubStatement, null));
                    continue;
                }
                YangStatement matchedRightSubStatement =
                        searchStatement(leftSubStatement, rightSubStatements, foundStatements);
                if (null == matchedRightSubStatement) {
                    YangStatementComparator comparator =
                            YangComparatorRegister.getInstance().getComparator(getStatement(leftSubStatement));
                    compareResults.addAll(comparator.compare(leftSubStatement, null));
                } else {
                    foundStatements.add(matchedRightSubStatement);
                    YangStatementComparator comparator = YangComparatorRegister.getInstance().getComparator(
                            getStatement(leftSubStatement == null ? matchedRightSubStatement : leftSubStatement));
                    compareResults.addAll(comparator.compare(leftSubStatement, matchedRightSubStatement));
                }
            }
        }
        if (rightElements.size() > 0) {
            for (YangStatement subElement : rightElements) {
                if (option == OPTION_ONLY_META) {
                    if ((subElement instanceof SchemaNode)
                            || (subElement instanceof Referencable)) {
                        continue;
                    }
                } else if (option == OPTION_ONLY_SCHEMA) {
                    if (!(subElement instanceof SchemaNode)) {
                        continue;
                    }
                }
                if (subElement instanceof SchemaNode) {
                    SchemaNode rightSchemaNode = (SchemaNode) subElement;
                    if (!rightSchemaNode.isActive()) {
                        continue;
                    }
                }
                YangStatement rightSubElement = subElement;
                if (contains(foundStatements, rightSubElement)) {
                    continue;
                }
                YangStatementComparator comparator =
                        YangComparatorRegister.getInstance().getComparator(getStatement(rightSubElement));
                compareResults.addAll(comparator.compare(null, rightSubElement));
            }
        }
        return compareResults;
    }

}
