package net.sundayu.code4j.generator.mybatis.plugin;

import net.sundayu.code4j.generator.mybatis.util.GeneralUtil;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.sundayu.code4j.generator.mybatis.constant.DatabaseCRUD;
import net.sundayu.code4j.generator.mybatis.constant.StatementType;
import net.sundayu.code4j.generator.mybatis.plugin.base.GenerateCodeByIndexBasePlugin;
import net.sundayu.code4j.generator.mybatis.pojo.IndexInfo;

/**
 * @author xiyusu
 * @datetime 2021年04月14 00:32
 * <p>
 * 插件：根据索引生成select查询语句(mapper)
 */
public class GenerateMapperMethodByIndexPlugin extends GenerateCodeByIndexBasePlugin {
    private static final Logger LOG = LoggerFactory.getLogger(GenerateMapperMethodByIndexPlugin.class);

    @Override
    public void generateSelectMethodByIndex(Method method, Interface interfaze, IntrospectedTable introspectedTable, IndexInfo indexInfo, StatementType type) {
        String methodName = getMethodNameByIndexName(indexInfo.getIdxName(), "select") + type.get();
        Method selectMethod = new Method(methodName);
        selectMethod.setVisibility(JavaVisibility.PUBLIC);
        selectMethod.setAbstract(true);
        // 方法注释
        StringBuilder javaDocBuilder = new StringBuilder();
        if (indexInfo.isUnique()) {
            //唯一索引返回实体类
            selectMethod.setReturnType(javaType);
        } else {
            //非唯一索引返回实体类列表
            //判断是否为list
            FullyQualifiedJavaType returnTypeList = FullyQualifiedJavaType.getNewListInstance();
            returnTypeList.addTypeArgument(javaType);
            selectMethod.setReturnType(returnTypeList);
            interfaze.addImportedType(FullyQualifiedJavaType.getNewListInstance());
        }
        for (IntrospectedColumn introspectedColumn : indexInfo.getIntrospectedColumns()) {
            String fieldName = GeneralUtil.underLineToHump(introspectedColumn.getActualColumnName());
            // 数据类型
            Parameter parameter = new Parameter(introspectedColumn.getFullyQualifiedJavaType(), fieldName);
            interfaze.addImportedType(introspectedColumn.getFullyQualifiedJavaType());
            parameter.addAnnotation("@Param(\"" + fieldName + "\")");
            selectMethod.addParameter(parameter);
        }
        interfaze.addImportedType(new FullyQualifiedJavaType("org.apache.ibatis.annotations.Param"));
        interfaze.addMethod(selectMethod);
        // 不使用默认注释
        // context.getCommentGenerator().addGeneralMethodComment(selectMethod, introspectedTable);
        addMapperMethodComment(selectMethod, introspectedTable, indexInfo, DatabaseCRUD.RETRIEVE, type);
    }

    @Override
    public void generateSingleUpdateMethod(Method method, Interface interfaze, IntrospectedTable introspectedTable, IndexInfo indexInfo, StatementType type) {
        String methodName = getMethodNameByIndexName(indexInfo.getIdxName(), "update") + type.get();
        Method updateMethod = new Method(methodName);
        updateMethod.setVisibility(JavaVisibility.PUBLIC);
        updateMethod.setAbstract(true);
        updateMethod.setReturnType(FullyQualifiedJavaType.getIntInstance());
        Parameter parameter = new Parameter(javaType, "record");
        updateMethod.addParameter(parameter);
        interfaze.addMethod(updateMethod);
        // 添加注释
        addMapperMethodComment(updateMethod, introspectedTable, indexInfo, DatabaseCRUD.UPDATE, type);
    }

    @Override
    public void generateBatchUpdateMethod(Method method, Interface interfaze, IntrospectedTable introspectedTable, IndexInfo indexInfo, StatementType type) {
        String methodName = getMethodNameByIndexName(indexInfo.getIdxName(), "update") + type.get();
        Method insertListMethod = new Method(methodName);
        insertListMethod.setVisibility(JavaVisibility.PUBLIC);
        insertListMethod.setAbstract(true);
        insertListMethod.setReturnType(FullyQualifiedJavaType.getIntInstance());
        FullyQualifiedJavaType paraType = FullyQualifiedJavaType.getNewListInstance();
        paraType.addTypeArgument(javaType);
        Parameter parameter = new Parameter(paraType, "rows");
        parameter.addAnnotation("@Param(\"rows\")");
        insertListMethod.addParameter(parameter);
        interfaze.addImportedType(FullyQualifiedJavaType.getNewListInstance());
        interfaze.addImportedType(javaType);
        interfaze.addMethod(insertListMethod);
        // 添加注释
        addMapperMethodComment(insertListMethod, introspectedTable, null, DatabaseCRUD.UPDATE, type);
    }

    @Override
    public void generateSingleUpdateSelectiveMethod(Method method, Interface interfaze, IntrospectedTable introspectedTable, IndexInfo indexInfo, StatementType type) {

    }

    @Override
    public void generateBatchUpdateSelectiveMethod(Method method, Interface interfaze, IntrospectedTable introspectedTable, IndexInfo indexInfo, StatementType type) {

    }

    @Override
    public void generateDeleteMethodByIndex(Method method, Interface interfaze, IntrospectedTable introspectedTable, IndexInfo indexInfo, StatementType type) {
        String methodName = getMethodNameByIndexName(indexInfo.getIdxName(), "delete") + type.get();
        Method deleteMethod = new Method(methodName);
        deleteMethod.setVisibility(JavaVisibility.PUBLIC);
        deleteMethod.setAbstract(true);
        deleteMethod.setReturnType(FullyQualifiedJavaType.getIntInstance());
        for (IntrospectedColumn introspectedColumn : indexInfo.getIntrospectedColumns()) {
            String fieldName = GeneralUtil.underLineToHump(introspectedColumn.getActualColumnName());
            // 数据类型
            Parameter parameter = new Parameter(introspectedColumn.getFullyQualifiedJavaType(), fieldName);
            interfaze.addImportedType(introspectedColumn.getFullyQualifiedJavaType());
            parameter.addAnnotation("@Param(\"" + fieldName + "\")");
            deleteMethod.addParameter(parameter);
        }
        interfaze.addMethod(deleteMethod);
        // 添加注释
        addMapperMethodComment(deleteMethod, introspectedTable, indexInfo, DatabaseCRUD.DELETE, type);
    }

    @Override
    public void generateSingleDeleteMethod(Method method, Interface interfaze, IntrospectedTable introspectedTable, IndexInfo indexInfo, StatementType type) {
        String methodName = getMethodNameByIndexName(indexInfo.getIdxName(), "delete") + "Record" + type.get();
        Method deleteMethod = new Method(methodName);
        deleteMethod.setVisibility(JavaVisibility.PUBLIC);
        deleteMethod.setAbstract(true);
        deleteMethod.setReturnType(FullyQualifiedJavaType.getIntInstance());
        Parameter parameter = new Parameter(javaType, "record");
        deleteMethod.addParameter(parameter);
        interfaze.addMethod(deleteMethod);
        // 添加注释
        addMapperMethodComment(deleteMethod, introspectedTable, indexInfo, DatabaseCRUD.DELETE, type);
    }

    @Override
    public void generateBatchDeleteMethod(Method method, Interface interfaze, IntrospectedTable introspectedTable, IndexInfo indexInfo, StatementType type) {
        String methodName = getMethodNameByIndexName(indexInfo.getIdxName(), "delete") + type.get();
        Method deleteListMethod = new Method(methodName);
        deleteListMethod.setVisibility(JavaVisibility.PUBLIC);
        deleteListMethod.setAbstract(true);
        deleteListMethod.setReturnType(FullyQualifiedJavaType.getIntInstance());
        FullyQualifiedJavaType paraType = FullyQualifiedJavaType.getNewListInstance();
        paraType.addTypeArgument(javaType);
        Parameter parameter = new Parameter(paraType, "rows");
        parameter.addAnnotation("@Param(\"rows\")");
        deleteListMethod.addParameter(parameter);
        interfaze.addImportedType(FullyQualifiedJavaType.getNewListInstance());
        interfaze.addImportedType(javaType);
        interfaze.addMethod(deleteListMethod);
        // 添加注释
        addMapperMethodComment(deleteListMethod, introspectedTable, null, DatabaseCRUD.DELETE, type);
    }

    @Override
    public void generateInsertMethod(Method method, Interface interfaze, IntrospectedTable introspectedTable, StatementType type) {

    }

    @Override
    public void generateInsertSelectiveMethod(Method method, Interface interfaze, IntrospectedTable introspectedTable, IndexInfo indexInfo, StatementType type) {

    }

    @Override
    public void generateBatchInsertMethod(Method method, Interface interfaze, IntrospectedTable introspectedTable, StatementType type) {
        Method insertListMethod = new Method("batchInsert");
        insertListMethod.setVisibility(JavaVisibility.PUBLIC);
        insertListMethod.setAbstract(true);
        insertListMethod.setReturnType(FullyQualifiedJavaType.getIntInstance());
        FullyQualifiedJavaType paraType = FullyQualifiedJavaType.getNewListInstance();
        paraType.addTypeArgument(javaType);
        Parameter parameter = new Parameter(paraType, "rows");
        insertListMethod.addParameter(parameter);
        interfaze.addImportedType(FullyQualifiedJavaType.getNewListInstance());
        interfaze.addImportedType(javaType);
        interfaze.addMethod(insertListMethod);
        // 添加注释
        addMapperMethodComment(insertListMethod, introspectedTable, null, DatabaseCRUD.INSERT, type);
    }
}
