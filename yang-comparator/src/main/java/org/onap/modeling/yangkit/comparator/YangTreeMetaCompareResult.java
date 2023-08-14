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

import org.yangcentral.yangkit.model.api.stmt.Module;
import org.yangcentral.yangkit.model.api.stmt.SchemaNode;

/**
 * the meta compare result of yang tree.
 *
 * @author lllyfeng
 * @since 2022-06-01
 */
public class YangTreeMetaCompareResult implements YangCompareResult<String> {
    private final SchemaNode target;
    private final String meta;
    private final String left;
    private final String right;
    private final ChangeType changeType;
    private CompatibilityInfo compatibilityInfo;

    /**
     * constructor.
     *
     * @param target     target schema node
     * @param meta       meta
     * @param changeType change type
     * @param left       left
     * @param right      right
     */
    public YangTreeMetaCompareResult(SchemaNode target, String meta, ChangeType changeType, String left,
                                     String right) {
        this.target = target;
        this.left = left;
        this.right = right;
        this.changeType = changeType;
        this.meta = meta;
    }

    @Override
    public ChangeType getChangeType() {
        return changeType;
    }

    @Override
    public String getLeft() {
        return left;
    }

    @Override
    public String getRight() {
        return right;
    }

    /**
     * get meta.
     *
     * @return meta
     */
    public String getMeta() {
        return meta;
    }

    /**
     * get target.
     *
     * @return target
     */
    public SchemaNode getTarget() {
        return target;
    }

    @Override
    public String getChangeDescription(String indent) {
        StringBuffer sb = new StringBuffer();
        if (indent != null) {
            sb.append(indent);
        }
        sb.append(changeType.getName()).append(":").append(meta).append(" ");
        switch (changeType) {
            case ADD: {
                sb.append(right);
                break;
            }
            case DELETE: {
                sb.append(left);
                break;
            }
            case MODIFY: {
                sb.append("FROM:").append(left).append(" TO:")
                        .append(right);
                break;
            }
            default: {
                break;
            }
        }
        return sb.toString();
    }

    @Override
    public Module getModule() {
        return getTarget().getContext().getCurModule();
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
