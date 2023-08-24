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

package org.onap.modeling.yangkit.plugin.yangtree;

import java.util.ArrayList;
import java.util.List;

import org.yangcentral.yangkit.common.api.QName;
import org.yangcentral.yangkit.common.api.validate.ValidatorResult;
import org.yangcentral.yangkit.model.api.stmt.Action;
import org.yangcentral.yangkit.model.api.stmt.ActionContainer;
import org.yangcentral.yangkit.model.api.stmt.Augment;
import org.yangcentral.yangkit.model.api.stmt.DataDefContainer;
import org.yangcentral.yangkit.model.api.stmt.DataDefinition;
import org.yangcentral.yangkit.model.api.stmt.DataNode;
import org.yangcentral.yangkit.model.api.stmt.Grouping;
import org.yangcentral.yangkit.model.api.stmt.GroupingDefContainer;
import org.yangcentral.yangkit.model.api.stmt.Notification;
import org.yangcentral.yangkit.model.api.stmt.NotificationContainer;
import org.yangcentral.yangkit.model.api.stmt.SchemaNode;
import org.yangcentral.yangkit.model.api.stmt.SchemaNodeContainer;
import org.yangcentral.yangkit.model.api.stmt.Uses;
import org.yangcentral.yangkit.model.api.stmt.YangStatement;


public class GroupingSchemaNodeContainer implements SchemaNodeContainer, GroupingDefContainer {

    private final YangStatement container;

    /**
     * constructor.
     * @param container schema node container
     */
    public GroupingSchemaNodeContainer(YangStatement container) {
        this.container = container;
    }

    @Override
    public List<Grouping> getGroupings() {
        List<Grouping> groupings = new ArrayList<>();
        if (container instanceof GroupingDefContainer) {
            groupings.addAll(((GroupingDefContainer) container).getGroupings());
        }
        return groupings;
    }

    @Override
    public Grouping getGrouping(String name) {
        return null;
    }

    @Override
    public List<SchemaNode> getSchemaNodeChildren() {
        List<SchemaNode> schemaNodes = new ArrayList<>();
        if (container instanceof DataDefContainer) {
            DataDefContainer dataDefContainer = (DataDefContainer) container;
            for (DataDefinition dataDefinition : dataDefContainer.getDataDefChildren()) {
                schemaNodes.add(dataDefinition);
            }
        }

        if (container instanceof ActionContainer) {
            ActionContainer actionContainer = (ActionContainer) container;
            for (Action action : actionContainer.getActions()) {
                schemaNodes.add(action);
            }
        }

        if (container instanceof NotificationContainer) {
            for (Notification notification : ((NotificationContainer) container).getNotifications()) {
                schemaNodes.add(notification);
            }
        }
        if (container instanceof Uses) {
            for (Augment augment : ((Uses) container).getAugments()) {
                schemaNodes.add(augment);
            }
        }

        return schemaNodes;
    }

    @Override
    public ValidatorResult addSchemaNodeChild(SchemaNode schemaNode) {
        return null;
    }

    @Override
    public ValidatorResult addSchemaNodeChildren(List<SchemaNode> schemaNodes) {
        return null;
    }

    @Override
    public SchemaNode getSchemaNodeChild(QName identifier) {
        return null;
    }

    @Override
    public DataNode getDataNodeChild(QName identifier) {
        return null;
    }

    @Override
    public List<DataNode> getDataNodeChildren() {
        return null;
    }

    @Override
    public List<SchemaNode> getEffectiveSchemaNodeChildren(boolean ignoreNamespace) {
        return null;
    }

    @Override
    public void removeSchemaNodeChild(QName identifier) {

    }

    @Override
    public void removeSchemaNodeChild(SchemaNode schemaNode) {

    }

    @Override
    public SchemaNode getMandatoryDescendant() {
        return null;
    }
}
