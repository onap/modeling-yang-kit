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

import org.yangcentral.yangkit.model.api.schema.SchemaTreeType;
import org.yangcentral.yangkit.model.api.stmt.Config;
import org.yangcentral.yangkit.model.api.stmt.SchemaNode;



/**
 * comparator for config statement.
 *
 * @since 2022-06-13
 */
public class ConfigComparator extends CommonYangStatementComparator<Config> {
    @Override
    protected List<CompatibilityRule.ChangeInfo> getChangeInfo(Config left, Config right) {
        List<CompatibilityRule.ChangeInfo> changeInfos = new ArrayList<>();
        changeInfos.addAll(super.getChangeInfo(left, right));
        if (left.getContext().getSelf() instanceof SchemaNode) {
            SchemaNode schemaNode = (SchemaNode) left.getContext().getSelf();
            if (schemaNode.getSchemaTreeType() != SchemaTreeType.DATATREE) {
                changeInfos.add(CompatibilityRule.ChangeInfo.IGNORE);
                return changeInfos;
            }
        } else if (left.getParentStatement() != null) {
            if (left.getParentStatement() instanceof SchemaNode) {
                SchemaNode schemaNode = (SchemaNode) left.getParentStatement();
                if (schemaNode.getSchemaTreeType() != SchemaTreeType.DATATREE) {
                    changeInfos.add(CompatibilityRule.ChangeInfo.IGNORE);
                    return changeInfos;
                }
            }
        }
        if (left.isConfig() && !right.isConfig()) {
            changeInfos.add(CompatibilityRule.ChangeInfo.REDUCE);
        } else if (!left.isConfig() && right.isConfig()) {
            changeInfos.add(CompatibilityRule.ChangeInfo.EXPAND);
        }

        return changeInfos;
    }

    @Override
    protected CompatibilityInfo defaultCompatibility(Config left, Config right,
                                                     CompatibilityRule.ChangeInfo changeInfo) {
        if (changeInfo == CompatibilityRule.ChangeInfo.REDUCE) {
            return new CompatibilityInfo(CompatibilityRule.Compatibility.NBC,
                    "config is changed from false to true, it's non-backward-compatible.");
        }
        return super.defaultCompatibility(left, right, changeInfo);
    }
}
