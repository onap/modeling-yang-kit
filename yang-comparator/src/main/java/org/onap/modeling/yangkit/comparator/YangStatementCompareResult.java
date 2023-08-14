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
import org.yangcentral.yangkit.model.api.stmt.YangStatement;


public class YangStatementCompareResult implements YangCompareResult<YangStatement> {
    private final ChangeType changeType;
    private final YangStatement left;
    private final YangStatement right;
    private CompatibilityInfo compatibilityInfo;

    /**
     * constructor.
     *
     * @param changeType change type
     * @param left       left statement
     * @param right      right statement
     */
    public YangStatementCompareResult(ChangeType changeType, YangStatement left, YangStatement right) {
        this.changeType = changeType;
        this.left = left;
        this.right = right;
    }

    @Override
    public ChangeType getChangeType() {
        return changeType;
    }

    @Override
    public YangStatement getLeft() {
        return left;
    }

    @Override
    public YangStatement getRight() {
        return right;
    }


    @Override
    public String getChangeDescription(String indent) {
        StringBuffer sb = new StringBuffer();
        if (null != indent) {
            sb.append(indent);
        }

        switch (changeType) {
            case ADD: {
                sb.append(YangComparator.outputStatement(right));
                sb.append("\t@").append("module:").append(right.getContext().getCurModule().getArgStr()).append(" ")
                        .append(right.getElementPosition().getLocation().getLocation());
                break;
            }
            case MODIFY: {
                sb.append("FROM ");
                sb.append(YangComparator.outputStatement(left));
                sb.append(" TO ");
                sb.append(YangComparator.outputStatement(right));
                sb.append("\t@").append("module:").append(right.getContext().getCurModule().getArgStr()).append(" ")
                        .append(right.getElementPosition().getLocation().getLocation());
                break;
            }
            case DELETE: {
                sb.append(YangComparator.outputStatement(left));
                sb.append("\t@").append("module:").append(left.getContext().getCurModule().getArgStr()).append(" ")
                        .append(left.getElementPosition().getLocation().getLocation());
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
