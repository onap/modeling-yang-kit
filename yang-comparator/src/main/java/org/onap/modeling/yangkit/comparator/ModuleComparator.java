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
import org.yangcentral.yangkit.model.api.stmt.MainModule;
import org.yangcentral.yangkit.model.api.stmt.Module;



public class ModuleComparator extends CommonYangStatementComparator<Module> {


    @Override
    protected List<CompatibilityRule.ChangeInfo> getChangeInfo(Module left, Module right) {
        return super.getChangeInfo(left, right);
    }


    @Override
    protected CompatibilityInfo defaultCompatibility(Module left, Module right,
                                                     CompatibilityRule.ChangeInfo changeInfo) {
        if (changeInfo == CompatibilityRule.ChangeInfo.DELETED
                || changeInfo == CompatibilityRule.ChangeInfo.CHANGED) {
            return new CompatibilityInfo(CompatibilityRule.Compatibility.NBC, "delete a module or change module name,"
                    + "it's non-backward-compatible.");
        }
        return super.defaultCompatibility(left, right, changeInfo);
    }


    @Override
    protected List<YangCompareResult> compareChildren(Module left, Module right) {
        List<YangCompareResult> results = new ArrayList<>();
        Module temp = left;
        if (temp == null) {
            temp = right;
        }
        results.addAll(compareStatements(left == null ? new ArrayList<>() : left.getEffectiveMetaStatements(),
                right == null ? new ArrayList<>() : right.getEffectiveMetaStatements(), OPTION_ALL));
        results.addAll(compareStatements(left == null ? new ArrayList<>() : left.getEffectiveLinkageStatement(),
                right == null ? new ArrayList<>() : right.getEffectiveLinkageStatement(), OPTION_ALL));
        if (temp instanceof MainModule) {
            results.addAll(compareStatements(left == null ? new ArrayList<>() : left.getEffectiveDefinitionStatement(),
                    right == null ? new ArrayList<>() : right.getEffectiveDefinitionStatement(), OPTION_ONLY_META));
            results.addAll(compareStatements(
                    left == null ? new ArrayList<>() : YangComparator.getEffectiveSchemaNodeChildren(left),
                    right == null ? new ArrayList<>() : YangComparator.getEffectiveSchemaNodeChildren(right),
                    OPTION_ONLY_SCHEMA));
        }
        return results;
    }


}
