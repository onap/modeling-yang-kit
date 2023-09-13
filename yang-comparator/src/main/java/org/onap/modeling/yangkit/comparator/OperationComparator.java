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

import org.yangcentral.yangkit.model.api.stmt.Operation;
import org.yangcentral.yangkit.model.api.stmt.SchemaNode;
import org.yangcentral.yangkit.model.api.stmt.SchemaNodeContainer;
import org.yangcentral.yangkit.model.api.stmt.YangStatement;



public class OperationComparator extends SchemaNodeComparator<Operation> {

    private boolean hasActiveSchemaChildren(SchemaNodeContainer schemaNodeContainer) {
        for (SchemaNode schemaNode : schemaNodeContainer.getSchemaNodeChildren()) {
            if (schemaNode.isActive()) {
                return true;
            }
        }
        return false;
    }


    @Override
    protected List<YangCompareResult> compareChildren(Operation left, Operation right) {
        List<YangCompareResult> compareResults = new ArrayList<>();
        compareResults.addAll(compareStatements(left == null ? new ArrayList<>() : left.getEffectiveSubStatements(),
                right == null ? new ArrayList<>() : right.getEffectiveSubStatements(), OPTION_ONLY_META));


        List<YangStatement> leftSchemaChildren = new ArrayList<>();
        List<YangStatement> rightSchemaChildren = new ArrayList<>();
        //input
        if (left.getInput() != null && left.getInput().isActive()) {
            //check input's schema children, only more than zero can be add to left schema array
            if (hasActiveSchemaChildren(left.getInput())) {
                leftSchemaChildren.add(left.getInput());
            }
        }
        if (right.getInput() != null && right.getInput().isActive()) {
            //check input's schema children, only more than zero can be add to right schema array
            if (hasActiveSchemaChildren(right.getInput())) {
                rightSchemaChildren.add(right.getInput());
            }
        }
        //output
        if (left.getOutput() != null && left.getOutput().isActive()) {
            //check output's schema children, only more than zero can be add to left schema array
            if (hasActiveSchemaChildren(left.getOutput())) {
                leftSchemaChildren.add(left.getOutput());
            }
        }
        if (right.getOutput() != null && right.getOutput().isActive()) {
            //check out's schema children, only more than zero can be add to right schema array
            if (hasActiveSchemaChildren(right.getOutput())) {
                rightSchemaChildren.add(right.getOutput());
            }
        }
        compareResults.addAll(compareStatements(leftSchemaChildren, rightSchemaChildren, OPTION_ALL));
        return compareResults;
    }
}

