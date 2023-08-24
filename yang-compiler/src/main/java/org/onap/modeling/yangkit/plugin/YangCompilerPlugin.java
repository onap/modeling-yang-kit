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

package org.onap.modeling.yangkit.plugin;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;

import org.onap.modeling.yangkit.compiler.YangCompiler;
import org.onap.modeling.yangkit.compiler.YangCompilerException;
import org.yangcentral.yangkit.model.api.schema.YangSchemaContext;


public interface YangCompilerPlugin {
    /**
     * default implementation of get parameter method.
     * @param compilerProps the properties of yang compiler.
     * @param name the name of parameter
     * @param value the value of parameter
     * @return yang compiler plugin parameter
     * @throws YangCompilerException if error occurs, the exception will be thrown.
     */
    default YangCompilerPluginParameter getParameter(Properties compilerProps, String name, String value)
            throws YangCompilerException {
        YangCompilerPluginParameter yangCompilerPluginParameter = new YangCompilerPluginParameter() {
            @Override
            public String getName() {
                return name;
            }

            @Override
            public Object getValue() {
                Iterator<Map.Entry<Object, Object>> it = compilerProps.entrySet().iterator();
                String formatStr = value;
                while (it.hasNext()) {
                    Map.Entry<Object, Object> entry = it.next();
                    formatStr = formatStr.replaceAll("\\{" + entry.getKey() + "\\}",
                            Matcher.quoteReplacement((String) entry.getValue()));
                }
                return formatStr;

            }

        };
        return yangCompilerPluginParameter;
    }

    /**
     * the definition of run method.
     * @param schemaContext yang schema context
     * @param yangCompiler yang compiler instance
     * @param parameters parameters
     * @throws YangCompilerException if error occurs, the exception will be thrown.
     */
    void run(YangSchemaContext schemaContext, YangCompiler yangCompiler, List<YangCompilerPluginParameter> parameters)
            throws YangCompilerException;

}
