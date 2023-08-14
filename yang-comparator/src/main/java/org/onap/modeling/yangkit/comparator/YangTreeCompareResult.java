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

import org.yangcentral.yangkit.model.api.schema.SchemaPath;
import org.yangcentral.yangkit.model.api.stmt.Module;
import org.yangcentral.yangkit.model.api.stmt.SchemaNode;


public class YangTreeCompareResult implements YangCompareResult<SchemaNode> {
    private final SchemaPath.Absolute schemaPath;
    private final ChangeType changeType;
    private SchemaNode left;
    private SchemaNode right;
    private String changeDescription;
    private List<YangCompareResult> metaCompareResults = new ArrayList<>();
    private CompatibilityInfo compatibilityInfo;

    /**
     * constructor.
     *
     * @param schemaPath schema path
     * @param changeType change type
     */
    public YangTreeCompareResult(SchemaPath.Absolute schemaPath, ChangeType changeType) {
        this.schemaPath = schemaPath;
        this.changeType = changeType;
    }

    /**
     * get meta compare results.
     *
     * @return list of compare results
     */
    public List<YangCompareResult> getMetaCompareResults() {
        return metaCompareResults;
    }

    /**
     * set meta compare results.
     *
     * @param metaCompareResults list of meta compare results
     */
    public void setMetaCompareResults(List<YangCompareResult> metaCompareResults) {
        this.metaCompareResults = metaCompareResults;
    }

    /**
     * add meta compare result.
     *
     * @param metaResult result
     */
    public void addMetaCompareResult(YangCompareResult metaResult) {
        metaCompareResults.add(metaResult);
        return;
    }

    @Override
    public SchemaNode getLeft() {
        return left;
    }

    /**
     * set left schema node.
     *
     * @param left left schema node.
     */
    public void setLeft(SchemaNode left) {
        this.left = left;
    }

    @Override
    public SchemaNode getRight() {
        return right;
    }

    /**
     * set right schema node.
     *
     * @param right right schema node
     */
    public void setRight(SchemaNode right) {
        this.right = right;
    }

    /**
     * get schema path.
     *
     * @return schema path
     */
    public SchemaPath.Absolute getSchemaPath() {
        return schemaPath;
    }

    @Override
    public ChangeType getChangeType() {
        return changeType;
    }

    /**
     * set change description.
     *
     * @param changeDescription description.
     */
    public void setChangeDescription(String changeDescription) {
        this.changeDescription = changeDescription;
    }

    @Override
    public String getChangeDescription(String indent) {
        StringBuffer sb = new StringBuffer();
        if (indent != null) {
            sb.append(indent);
        }
        if (changeDescription != null) {
            sb.append("\t").append(changeDescription);
            return sb.toString();
        }
        if (null != schemaPath) {
            sb.append(schemaPath.toString());
        }
        sb.append("\t@").append("module:");
        switch (changeType) {
            case ADD:
            case MODIFY: {
                sb.append(right.getContext().getCurModule().getArgStr()).append(" ")
                        .append(right.getElementPosition().getLocation().getLocation());
                break;
            }
            case DELETE: {
                sb.append(left.getContext().getCurModule().getArgStr()).append(" ")
                        .append(left.getElementPosition().getLocation().getLocation());
                break;
            }
            default: {
                break;
            }
        }

        if (metaCompareResults.size() > 0) {
            for (YangCompareResult compareResult : metaCompareResults) {
                sb.append("\n").append(indent).append("\t");
                switch (compareResult.getChangeType()) {
                    case ADD: {
                        sb.append("added:");
                        break;
                    }
                    case DELETE: {
                        sb.append("deleted:");
                        break;
                    }
                    case MODIFY: {
                        sb.append("changed:");
                        break;
                    }
                    default: {
                        break;
                    }
                }
                sb.append(" ");
                sb.append(compareResult.getChangeDescription(null));
            }
        }
        return sb.toString();
    }

    @Override
    public Module getModule() {
        switch (changeType) {
            case ADD:
            case MODIFY: {
                return right.getContext().getCurModule();
            }
            case DELETE: {
                return left.getContext().getCurModule();
            }
            default: {
                return null;
            }
        }
    }

    @Override
    public CompatibilityInfo getCompatibilityInfo() {
        return compatibilityInfo;
    }

    @Override
    public void setCompatibilityInfo(CompatibilityInfo compatibility) {
        this.compatibilityInfo = compatibility;
    }
}
