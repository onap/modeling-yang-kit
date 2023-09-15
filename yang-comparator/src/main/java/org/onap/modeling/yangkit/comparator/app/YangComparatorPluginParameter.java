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

package org.onap.modeling.yangkit.comparator.app;

import com.google.gson.JsonElement;
import org.onap.modeling.yangkit.comparator.CompareType;
import org.onap.modeling.yangkit.compiler.BuildOption;
import org.onap.modeling.yangkit.compiler.YangCompilerException;
import org.onap.modeling.yangkit.compiler.plugin.YangCompilerPluginParameter;

public class YangComparatorPluginParameter implements YangCompilerPluginParameter {
    private String name;
    private JsonElement value;

    public YangComparatorPluginParameter(String name, JsonElement value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Object getValue() throws YangCompilerException {
        if (!name.equals("old-yang") && !name.equals("settings")
                && !name.equals("compare-type") && !name.equals("rule")
                && !name.equals("result")) {
            throw new YangCompilerException("unrecognized parameter:" + name);
        }
        if (name.equals("old-yang")) {
            return BuildOption.parseSources(value);
        } else if (name.equals("compare-type")) {
            if (value.getAsString().equals("stmt")) {
                return CompareType.STMT;
            } else if (value.getAsString().equals("tree")) {
                return CompareType.TREE;
            } else if (value.getAsString().equals("compatible-check")) {
                return CompareType.COMPATIBLE_CHECK;
            }
            throw new YangCompilerException("unrecognized value:" + value + " for parameter:" + name);
        } else {
            return value.getAsString();
        }
    }
}
