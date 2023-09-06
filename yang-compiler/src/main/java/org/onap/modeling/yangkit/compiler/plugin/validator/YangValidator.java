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

package org.onap.modeling.yangkit.compiler.plugin.validator;

import java.util.List;

import org.onap.modeling.yangkit.compiler.YangCompiler;
import org.onap.modeling.yangkit.compiler.YangCompilerException;
import org.onap.modeling.yangkit.compiler.plugin.YangCompilerPlugin;
import org.onap.modeling.yangkit.compiler.plugin.YangCompilerPluginParameter;
import org.yangcentral.yangkit.model.api.schema.YangSchemaContext;
import org.yangcentral.yangkit.utils.file.FileUtil;



public class YangValidator implements YangCompilerPlugin {
    /**
     * the implementation of run method.
     * @param schemaContext yang schema context.
     * @param yangCompiler yang compiler instance.
     * @param parameters parameters of plugin.
     * @throws YangCompilerException if error occurs, the exception will be thrown.
     */
    @Override
    public void run(YangSchemaContext schemaContext, YangCompiler yangCompiler,
                    List<YangCompilerPluginParameter> parameters) throws YangCompilerException {
        YangCompilerPluginParameter parameter = parameters.get(0);
        if (!parameter.getName().equals("output")) {
            throw new YangCompilerException("unknown parameter:" + parameter.getName());
        }
        FileUtil.writeUtf8File((String) parameter.getValue(), schemaContext.getValidateResult().toString());
    }
}
