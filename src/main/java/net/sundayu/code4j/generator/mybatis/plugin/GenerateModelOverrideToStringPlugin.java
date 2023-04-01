package net.sundayu.code4j.generator.mybatis.plugin;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;

import java.util.List;

/**
 * @author xiyusu
 * @datetime 2021年04月13 23:15
 * @description model类生成plugin插件
 */
public class GenerateModelOverrideToStringPlugin extends PluginAdapter {
    @Override
    public boolean validate(List<String> list) {
        return true;
    }

    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        generateOverrideToStringMethod(topLevelClass, introspectedTable);
        return super.modelBaseRecordClassGenerated(topLevelClass, introspectedTable);
    }

    @Override
    public boolean modelPrimaryKeyClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        generateOverrideToStringMethod(topLevelClass, introspectedTable);
        return super.modelPrimaryKeyClassGenerated(topLevelClass, introspectedTable);
    }

    @Override
    public boolean modelRecordWithBLOBsClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        generateOverrideToStringMethod(topLevelClass, introspectedTable);
        return super.modelRecordWithBLOBsClassGenerated(topLevelClass, introspectedTable);
    }

    /**
     * 重写toString方法
     *
     * @param topLevelClass     类
     * @param introspectedTable 表对象
     */
    private void generateOverrideToStringMethod(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        Method method = new Method("toString");
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(FullyQualifiedJavaType.getStringInstance());
        method.addAnnotation("@Override");
        //注释
        //context.getCommentGenerator().addGeneralMethodComment(method, introspectedTable);
        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append("return ");
        sBuilder.append("\"");
        sBuilder.append(topLevelClass.getType().getShortName());
        sBuilder.append("[\"");
        int count = 0;
        int fieldCount = 1;
        for (Field field : topLevelClass.getFields()) {
            String fieldName = field.getName();
            if (count == 1 && fieldCount != 1) {
                sBuilder.append(" +");
            } else {
                sBuilder.append("+");
            }
            if (fieldCount == 1) {
                sBuilder.append("\"");
            } else {
                sBuilder.append("\",");
            }
            sBuilder.append(fieldName);
            sBuilder.append("=\"");
            sBuilder.append("+ ");
            sBuilder.append(fieldName);
            if (count == 2) {
                method.addBodyLine(sBuilder.toString());
                sBuilder.setLength(0);
                count = 0;
            } else {
                count++;
            }
            fieldCount++;
        }
        sBuilder.append("+");
        sBuilder.append("\"]\";");
        method.addBodyLine(sBuilder.toString());
        topLevelClass.addMethod(method);
    }
}
