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

import org.dom4j.Document;
import org.dom4j.Element;

/**
 * the definition of compatibility rules.
 *
 * @author lllyfeng
 * @since 2022-06-09
 */
public class CompatibilityRules {
    private final List<CompatibilityRule> compatibilityRules = new ArrayList<>();
    private static final CompatibilityRules instance = new CompatibilityRules();

    private CompatibilityRules() {

    }

    /**
     * add a compatibility rule.
     *
     * @param rule rule
     * @return true or false
     */
    public boolean addCompatibilityRule(CompatibilityRule rule) {
        if (null != getCompatibilityRule(rule.getRuleId())) {
            return false;
        }
        return compatibilityRules.add(rule);
    }

    /**
     * get compatibility rules.
     *
     * @return rules
     */
    public List<CompatibilityRule> getCompatibilityRules() {
        return compatibilityRules;
    }

    /**
     * get compatibility rule by specified rule id.
     *
     * @param ruleId rule id
     * @return rule
     */
    public CompatibilityRule getCompatibilityRule(String ruleId) {
        for (CompatibilityRule rule : compatibilityRules) {
            if (rule.getRuleId().equals(ruleId)) {
                return rule;
            }
        }
        return null;
    }

    /**
     * get instance of compatibility rules.
     *
     * @return rules instance.
     */
    public static CompatibilityRules getInstance() {
        return instance;
    }

    /**
     * deserialize from xml document.
     *
     * @param document xml document
     */
    public void deserialize(Document document) {
        compatibilityRules.clear();
        Element root = document.getRootElement();
        List<Element> children = root.elements("rule");
        for (Element ruleElement : children) {
            CompatibilityRule rule = CompatibilityRule.deserialize(ruleElement);
            addCompatibilityRule(rule);
        }
    }

    private boolean matchCondition(CompatibilityRule left, CompatibilityRule.ChangeInfo right) {
        if (left.getCondition() == CompatibilityRule.ChangeInfo.ANY) {
            return true;
        } else if (left.getCondition() == CompatibilityRule.ChangeInfo.IGNORE) {
            return false;
        }
        if (left.getCondition() == right) {
            return true;
        }
        if (left.getCondition() == CompatibilityRule.ChangeInfo.CHANGED
                && (right == CompatibilityRule.ChangeInfo.EXPAND
                || right == CompatibilityRule.ChangeInfo.REDUCE
                || right == CompatibilityRule.ChangeInfo.SEQUENCE_CHANGED
                || right == CompatibilityRule.ChangeInfo.INTEGER_TYPE_CHANGED)) {
            if (left.getExceptConditions() != null && left.getExceptConditions().contains(right)) {
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     * search rule by some arguments.
     *
     * @param statement  statement
     * @param parentStmt parent statement
     * @param changeInfo change info
     * @return matched rule
     */
    public CompatibilityRule searchRule(String statement, String parentStmt,
                                        CompatibilityRule.ChangeInfo changeInfo) {
        for (CompatibilityRule rule : compatibilityRules) {
            if (rule.getStatements().contains(statement)) {
                if (rule.getParentStmt() != null) {
                    if (parentStmt == null) {
                        continue;
                    }
                    if (!rule.getParentStmt().equals(parentStmt)) {
                        continue;
                    }
                }
                if (matchCondition(rule, changeInfo)) {
                    return rule;
                }
            }
        }
        return null;
    }
}
