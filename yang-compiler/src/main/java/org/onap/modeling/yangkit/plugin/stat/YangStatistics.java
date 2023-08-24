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

package org.onap.modeling.yangkit.plugin.stat;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.onap.modeling.yangkit.compiler.YangCompiler;
import org.onap.modeling.yangkit.compiler.YangCompilerException;
import org.onap.modeling.yangkit.plugin.YangCompilerPlugin;
import org.onap.modeling.yangkit.plugin.YangCompilerPluginParameter;
import org.yangcentral.yangkit.model.api.schema.YangSchemaContext;
import org.yangcentral.yangkit.model.api.stmt.Module;
import org.yangcentral.yangkit.model.api.stmt.SchemaNode;
import org.yangcentral.yangkit.model.api.stmt.SchemaNodeContainer;
import org.yangcentral.yangkit.model.api.stmt.SubModule;
import org.yangcentral.yangkit.model.api.stmt.TypedDataNode;
import org.yangcentral.yangkit.model.api.stmt.VirtualSchemaNode;


public class YangStatistics implements YangCompilerPlugin {
    /**
     * get node description.
     * @param schemaNode schema node
     * @return node description
     */
    public YangNodeDescription getNodeDescription(SchemaNode schemaNode) {
        if (schemaNode == null) {
            return null;
        }
        if (schemaNode instanceof VirtualSchemaNode) {
            return null;
        }
        if (schemaNode.getSchemaPath() == null) {
            return null;
        }
        YangNodeDescription nodeDescription = new YangNodeDescription();
        nodeDescription.setPath(schemaNode.getSchemaPath().toString());
        nodeDescription.setDescription(
                schemaNode.getDescription() == null ? "" : schemaNode.getDescription().getArgStr());
        nodeDescription.setConfig(schemaNode.isConfig() ? "true" : "false");
        nodeDescription.setSchemaType(schemaNode.getYangKeyword().getLocalName());
        if (schemaNode instanceof TypedDataNode) {
            TypedDataNode typedDataNode = (TypedDataNode) schemaNode;
            nodeDescription.setNodeType(typedDataNode.getType().getArgStr());
        }
        nodeDescription.setModule(schemaNode.getContext().getCurModule().getArgStr());
        nodeDescription.setActive(schemaNode.isActive());
        nodeDescription.setDeviated(schemaNode.isDeviated());
        return nodeDescription;
    }

    /**
     * get node description for schema node container.
     * @param schemaNodeContainer schema node container
     * @return list of node description.
     */
    public List<YangNodeDescription> getNodeDescriptions(SchemaNodeContainer schemaNodeContainer) {
        if (schemaNodeContainer == null) {
            return new ArrayList<>();
        }
        List<YangNodeDescription> nodeDescriptions = new ArrayList<>();
        if (schemaNodeContainer instanceof SchemaNode) {
            SchemaNode schemaNode = (SchemaNode) schemaNodeContainer;
            YangNodeDescription nodeDescription = getNodeDescription(schemaNode);
            if (nodeDescription != null) {
                nodeDescriptions.add(nodeDescription);
            }
        }

        for (SchemaNode schemaNode : schemaNodeContainer.getSchemaNodeChildren()) {
            if (schemaNode instanceof SchemaNodeContainer) {
                nodeDescriptions.addAll(getNodeDescriptions((SchemaNodeContainer) schemaNode));
            } else {
                YangNodeDescription nodeDescription = getNodeDescription(schemaNode);
                if (nodeDescription != null) {
                    nodeDescriptions.add(nodeDescription);
                }
            }
        }
        return nodeDescriptions;
    }

    /**
     * get yang statistics for yang schema context.
     * @param schemaContext yang schema context.
     * @return list of node description.
     */
    public List<YangNodeDescription> getYangStatistics(YangSchemaContext schemaContext) {
        List<YangNodeDescription> nodeDescriptions = new ArrayList<>();
        if (schemaContext == null) {
            return nodeDescriptions;
        }
        for (Module module : schemaContext.getModules()) {
            if (module instanceof SubModule) {
                continue;
            }
            nodeDescriptions.addAll(getNodeDescriptions(module));
        }
        return nodeDescriptions;
    }

    /**
     * serialize the yang statistics of schema context to xlsx format.
     * @param schemaContext yang schema context.
     * @return xlsx document
     */
    public SXSSFWorkbook serializeXlsx(YangSchemaContext schemaContext) {
        SXSSFWorkbook workbook = new SXSSFWorkbook();
        SXSSFSheet summary = workbook.createSheet("summary");
        summary.setDisplayGridlines(true);
        summary.setAutobreaks(true);
        summary.setColumnWidth(0, 5000);
        summary.setColumnWidth(1, 5000);
        SXSSFRow sumFirstRow = summary.createRow(0);
        SXSSFCell totalModules = sumFirstRow.createCell(0);
        totalModules.setCellValue("Total modules:");
        SXSSFCell totalModulesVal = sumFirstRow.createCell(1);
        totalModulesVal.setCellValue(schemaContext.getModules().size());

        SXSSFSheet detail = workbook.createSheet("detail");
        detail.setDisplayGridlines(true);
        detail.setAutobreaks(true);
        detail.setColumnWidth(0, 20000);
        detail.setColumnWidth(1, 5000);
        detail.setColumnWidth(2, 20000);
        detail.setColumnWidth(3, 5000);
        detail.setColumnWidth(4, 5000);
        detail.setColumnWidth(5, 5000);
        detail.setColumnWidth(6, 5000);
        detail.setColumnWidth(7, 5000);

        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);

        CellStyle desStyle = workbook.createCellStyle();
        desStyle.cloneStyleFrom(style);
        desStyle.setAlignment(HorizontalAlignment.JUSTIFY);

        detail.setDefaultColumnStyle(0, style);
        detail.setDefaultColumnStyle(1, desStyle);
        detail.setDefaultColumnStyle(2, style);
        detail.setDefaultColumnStyle(3, style);
        detail.setDefaultColumnStyle(4, style);
        detail.setDefaultColumnStyle(5, style);
        detail.setDefaultColumnStyle(6, style);
        detail.setDefaultColumnStyle(7, style);

        //generate header
        SXSSFRow firstRow = detail.createRow(0);
        SXSSFCell yangpath = firstRow.createCell(0);
        yangpath.setCellValue("Path");
        yangpath.setCellStyle(style);
        SXSSFCell active = firstRow.createCell(1);
        active.setCellValue("Active");
        active.setCellStyle(style);
        SXSSFCell description = firstRow.createCell(2);
        description.setCellValue("Description");
        description.setCellStyle(style);
        SXSSFCell config = firstRow.createCell(3);
        config.setCellValue("Config");
        config.setCellStyle(style);
        SXSSFCell schema = firstRow.createCell(4);
        schema.setCellValue("schema");
        schema.setCellStyle(style);
        SXSSFCell type = firstRow.createCell(5);
        type.setCellValue("type");
        type.setCellStyle(style);
        SXSSFCell moduleHeader = firstRow.createCell(6);
        moduleHeader.setCellValue("module");
        moduleHeader.setCellStyle(style);
        SXSSFCell deviated = firstRow.createCell(7);
        deviated.setCellValue("deviated");
        deviated.setCellStyle(style);


        List<YangNodeDescription> totalPaths = getYangStatistics(schemaContext);


        if (null != totalPaths) {
            int pathSize = totalPaths.size();
            SXSSFRow summarySecRow = summary.createRow(1);
            SXSSFCell totalNodes = summarySecRow.createCell(0);
            totalNodes.setCellValue("Total nodes:");
            SXSSFCell totalNodesVal = summarySecRow.createCell(1);
            totalNodesVal.setCellValue(pathSize);
            for (int i = 0; i < pathSize; i++) {
                YangNodeDescription path = totalPaths.get(i);
                SXSSFRow row = detail.createRow(i + 1);
                SXSSFCell pathCell = row.createCell(0);
                pathCell.setCellValue(path.getPath());
                pathCell.setCellStyle(style);
                SXSSFCell activeCell = row.createCell(1);
                activeCell.setCellValue(path.isActive());
                activeCell.setCellStyle(style);
                SXSSFCell descriptionCell = row.createCell(2);
                String str = path.getDescription();
                if (str.length() >= 32767) {
                    str = str.substring(0, 32767);
                }
                descriptionCell.setCellValue(str);
                descriptionCell.setCellStyle(desStyle);

                SXSSFCell configCell = row.createCell(3);
                configCell.setCellValue(path.getConfig());
                configCell.setCellStyle(style);

                SXSSFCell schemaCell = row.createCell(4);
                schemaCell.setCellValue(path.getSchemaType());
                schemaCell.setCellStyle(style);

                SXSSFCell typeCell = row.createCell(5);
                typeCell.setCellValue(path.getNodeType());
                typeCell.setCellStyle(style);

                SXSSFCell moduleCell = row.createCell(6);
                moduleCell.setCellValue(path.getModule());
                moduleCell.setCellStyle(style);
                SXSSFCell deviateCell = row.createCell(7);
                deviateCell.setCellValue(path.isDeviated());
                deviateCell.setCellStyle(style);
            }
        }
        return workbook;
    }

    /**
     * a yang compiler plugin implementation (run method).
     * @param schemaContext yang schema context
     * @param yangCompiler yang compiler instance
     * @param parameters parameters for plugin
     * @throws YangCompilerException if error occurs, throw the exception
     */
    @Override
    public void run(YangSchemaContext schemaContext, YangCompiler yangCompiler,
                    List<YangCompilerPluginParameter> parameters) throws YangCompilerException {

        YangCompilerPluginParameter para = parameters.get(0);
        if (!para.getName().equals("output")) {
            throw new YangCompilerException("unknown parameter:" + para.getName());
        }
        String output = (String) para.getValue();
        SXSSFWorkbook workbook = serializeXlsx(schemaContext);
        try {
            workbook.write(new FileOutputStream(output));
            workbook.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
