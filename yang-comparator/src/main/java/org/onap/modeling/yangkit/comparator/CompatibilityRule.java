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

import org.dom4j.Element;


/**
 * the compatibility rule definition.
 *
 * @author lllyfeng
 * @since 2022-06-01
 */
public class CompatibilityRule {
    private final String ruleId;
    private String parentStmt;
    private final List<String> statements;//[module_name:]keyword, for example, container/huawei-extension:filter
    //private List<RuleType> ruleTypes;//STMT OR TREE
    private final ChangeInfo condition;
    private List<ChangeInfo> exceptConditions;
    private final Compatibility compatibility;
    private String description;

    /**
     * constructor.
     *
     * @param ruleId        rule id
     * @param statements    statements can be applied
     * @param condition     matched condition
     * @param compatibility compatibility
     */
    public CompatibilityRule(String ruleId, List<String> statements, ChangeInfo condition,
                             Compatibility compatibility) {
        this.ruleId = ruleId;
        this.statements = statements;
        //this.ruleTypes = ruleTypes;
        this.condition = condition;
        this.compatibility = compatibility;
    }

    /**
     * get parent statement.
     *
     * @return statement.
     */
    public String getParentStmt() {
        return parentStmt;
    }

    /**
     * set parent statement.
     *
     * @param parentStmt parent statement
     */
    public void setParentStmt(String parentStmt) {
        this.parentStmt = parentStmt;
    }

    /**
     * get except conditions.
     *
     * @return list of change info
     */
    public List<ChangeInfo> getExceptConditions() {
        return exceptConditions;
    }

    /**
     * set except conditions.
     *
     * @param exceptConditions conditions
     */
    public void setExceptCondition(List<ChangeInfo> exceptConditions) {
        this.exceptConditions = exceptConditions;
    }

    /**
     * set description.
     *
     * @param description description.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * get rule id.
     *
     * @return rule id
     */
    public String getRuleId() {
        return ruleId;
    }

    /**
     * get applied statements.
     *
     * @return statements
     */
    public List<String> getStatements() {
        return statements;
    }


    /**
     * get condition.
     *
     * @return condition
     */
    public ChangeInfo getCondition() {
        return condition;
    }

    public Compatibility getCompatibility() {
        return compatibility;
    }

    /**
     * get description.
     *
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * the definition of rule type.
     */
    public enum RuleType {
        STMT("stmt"),
        TREE("tree");
        private String name;

        private RuleType(String name) {
            this.name = name;
        }

        /**
         * get rule type from name.
         *
         * @param name name
         * @return rule type
         */
        public static RuleType forName(String name) {
            if (name.equals(STMT.name)) {
                return STMT;
            } else if (name.equals(TREE.name)) {
                return TREE;
            }
            return null;
        }
    }

    /**
     * the definition of change info.
     */
    public enum ChangeInfo {
        ANY("any"),//any changes, including added,deleted,changed
        IGNORE("ignore"),//ignore any changes
        /*any sub statement is deleted for stmt type, and any schema child node is deleted for tree type
         */
        DELETED("deleted"),
        /* any sub statement is added for stmt type, and any schema child node is added for tree type*/
        ADDED("added"),
        MANDATORY_ADDED("mandatory-added"),// add mandatory schema node
        /*the meaning has been changed,for example, builtin-type changed for type, value changed for enumeration*/
        CHANGED("changed"),
        /* any sub statements sequence changed for stmt type, and any schema child nodes sequence changed for
         tree type, it can be applied to any statement.
         */
        SEQUENCE_CHANGED("sequence-changed"),
        /*expand the scope, for range,it means larger range, for length, it means larger length, for fraction-digits,
         * it means a lower value, for min-elements, it means a lower value, for max-elements, it means a higher value,
         * for mandatory, it means from true to false, for config, it means from false to true
         * for unique, it means one or more attributes are deleted*/
        EXPAND("expand"),
        /*reduce the scope, for range,it means smaller range, for length, it means smaller length, for fraction-digits,
         * it means a higher value, for min-elements, it means a higher value, for max-elements, it means a lower value,
         * for mandatory, it means from false to true, for config, it means from true to false,
         * for unique, it means new attributes are  added */
        REDUCE("reduce"),
        /* for example type from int8 to int16,it is treated non-backward-compatible default. it can be applied to
        type statement*/
        INTEGER_TYPE_CHANGED("integer-type-changed");
        private String name;

        private ChangeInfo(String name) {
            this.name = name;
        }

        /**
         * get name.
         *
         * @return name
         */
        public String getName() {
            return name;
        }

        /**
         * get change info from name.
         *
         * @param name name
         * @return change info
         */
        public static ChangeInfo forName(String name) {
            if (name.equals(ANY.name)) {
                return ANY;
            } else if (name.equals(IGNORE.getName())) {
                return IGNORE;
            } else if (name.equals(EXPAND.getName())) {
                return EXPAND;
            } else if (name.equals(REDUCE.getName())) {
                return REDUCE;
            } else if (name.equals(CHANGED.getName())) {
                return CHANGED;
            } else if (name.equals(INTEGER_TYPE_CHANGED.getName())) {
                return INTEGER_TYPE_CHANGED;
            } else if (name.equals(SEQUENCE_CHANGED.getName())) {
                return SEQUENCE_CHANGED;
            } else if (name.equals(DELETED.getName())) {
                return DELETED;
            } else if (name.equals(ADDED.getName())) {
                return ADDED;
            } else if (name.equals(MANDATORY_ADDED.getName())) {
                return MANDATORY_ADDED;
            }

            return null;
        }

    }

    /**
     * the definition of compatibility.
     */
    public enum Compatibility {
        BC("backward-compatible"),
        NBC("non-backward-compatible"),
        UNKNOWN("unknown");
        private String name;

        private Compatibility(String name) {
            this.name = name;
        }

        /**
         * get name.
         *
         * @return name
         */
        public String getName() {
            return name;
        }

        /**
         * get compatibility from name.
         *
         * @param name name
         * @return compatibility
         */
        public static Compatibility forName(String name) {
            if (name.equals(BC.getName())) {
                return BC;
            } else if (name.equals(NBC.name)) {
                return NBC;
            } else if (name.equals(UNKNOWN.name)) {
                return UNKNOWN;
            }
            return null;
        }
    }

    /**
     * deserialize compatibility rule from xml.
     *
     * @param element xml element
     * @return compatibility rule.
     */
    public static CompatibilityRule deserialize(Element element) {

        Element statementsElement = element.element("statements");
        List<Element> statementElementList = statementsElement.elements("statement");
        List<String> statements = null;
        if (statementElementList != null && statementElementList.size() > 0) {
            statements = new ArrayList<>();
            for (Element statementElement : statementElementList) {
                statements.add(statementElement.getTextTrim());
            }
        }
        List<RuleType> types = new ArrayList<>();
        List<Element> typeElements = element.elements("type");
        for (Element typeElement : typeElements) {
            types.add(RuleType.forName(typeElement.getTextTrim()));
        }
        ChangeInfo condition = ChangeInfo.forName(element.elementText("condition"));
        List<ChangeInfo> exceptConditions = null;
        List<Element> exceptConditionElements = element.elements("except-condition");
        if (exceptConditionElements != null && exceptConditionElements.size() > 0) {
            exceptConditions = new ArrayList<>();
            for (Element exceptConditionElement : exceptConditionElements) {
                exceptConditions.add(ChangeInfo.forName(exceptConditionElement.getTextTrim()));
            }
        }
        Compatibility compatibility = Compatibility.forName(element.elementText("compatible"));
        String description = element.elementText("description");
        Element ruleIdElement = element.element("rule-id");
        String ruleId = ruleIdElement.getTextTrim();
        CompatibilityRule rule = new CompatibilityRule(ruleId, statements, condition, compatibility);
        rule.setExceptCondition(exceptConditions);
        rule.setDescription(description);
        Element parentStmtElement = element.element("parent-statement");
        if (parentStmtElement != null) {
            rule.setParentStmt(parentStmtElement.getTextTrim());
        }
        return rule;
    }
}
