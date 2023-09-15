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

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.onap.modeling.yangkit.comparator.CompareType;
import org.onap.modeling.yangkit.comparator.YangComparator;
import org.onap.modeling.yangkit.comparator.YangCompareResult;
import org.onap.modeling.yangkit.compiler.BuildOption;
import org.onap.modeling.yangkit.compiler.Settings;
import org.onap.modeling.yangkit.compiler.Source;
import org.onap.modeling.yangkit.compiler.YangCompiler;
import org.onap.modeling.yangkit.compiler.YangCompilerException;
import org.onap.modeling.yangkit.compiler.plugin.YangCompilerPlugin;
import org.onap.modeling.yangkit.compiler.plugin.YangCompilerPluginParameter;
import org.onap.modeling.yangkit.compiler.util.YangCompilerUtil;
import org.yangcentral.yangkit.common.api.validate.ValidatorResult;


import org.yangcentral.yangkit.model.api.schema.YangSchemaContext;
import org.yangcentral.yangkit.utils.file.FileUtil;
import org.yangcentral.yangkit.utils.xml.XmlWriter;


/**
 * yang comparator plugin.
 */
public class YangComparatorPlugin implements YangCompilerPlugin {
    @Override
    public YangCompilerPluginParameter getParameter(String name, JsonElement value) {
        return new YangComparatorPluginParameter(name,value);
    }

    @Override
    public void run(YangSchemaContext yangSchemaContext, YangCompiler yangCompiler,
                    List<YangCompilerPluginParameter> list) throws YangCompilerException {
        CompareType compareType = null;
        List<Source> sources = null;
        Settings settings = yangCompiler.getSettings();
        String rulePath = null;
        String resultPath = null;
        for (YangCompilerPluginParameter parameter : list) {
            //System.out.println("para name="+parameter.getName() + " para value="+parameter.getValue());
            if (parameter.getName().equals("old-yang")) {
                sources = (List<Source>) parameter.getValue();
            } else if (parameter.getName().equals("settings")) {
                String settingsPath = (String) parameter.getValue();
                try {
                    settings = Settings.parse(FileUtil.readFile2String(settingsPath));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else if (parameter.getName().equals("compare-type")) {
                compareType = (CompareType) parameter.getValue();
            } else if (parameter.getName().equals("rule")) {
                rulePath = (String) parameter.getValue();
            } else if (parameter.getName().equals("result")) {
                resultPath = (String) parameter.getValue();
            }

        }
        if (sources == null) {
            throw new YangCompilerException("missing mandatory parameter:old-yang");
        }
        if (compareType == null) {
            throw new YangCompilerException("missing mandatory parameter:compare-type");
        }
        if (resultPath == null) {
            throw new YangCompilerException("missing mandatory parameter:result");
        }
        File outputFile = new File(resultPath);
        if (!outputFile.exists()) {
            if (outputFile.getParentFile() != null && !outputFile.getParentFile().exists()) {
                outputFile.getParentFile().mkdirs();
            }
            try {
                outputFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        YangSchemaContext oldSchemaContext = YangCompilerUtil.buildSchemaContext(sources, settings);
        ValidatorResult oldResult = oldSchemaContext.validate();
        if (!oldResult.isOk()) {
            throw new YangCompilerException("fail to validate the schema context"
                    + ".\n"
                    + oldResult);
        }
        //System.out.println(oldSchemaContext.getValidateResult());
        YangComparator yangComparator = new YangComparator(oldSchemaContext, yangSchemaContext);
        try {
            List<YangCompareResult> results = yangComparator.compare(compareType, rulePath);
            boolean needCompatible = false;
            if (compareType == CompareType.COMPATIBLE_CHECK) {
                needCompatible = true;
            }
            Document document = yangComparator.outputXmlCompareResult(results, needCompatible, compareType);
            //System.out.println(XmlWriter.transDom4jDoc2String(document));
            XmlWriter.writeDom4jDoc(document, resultPath);
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        } catch (RuntimeException e) {
            for (StackTraceElement traceElement : e.getStackTrace()) {
                System.out.println(traceElement);
            }

        }

    }
}
