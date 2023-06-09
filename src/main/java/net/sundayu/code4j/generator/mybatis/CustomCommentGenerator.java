package net.sundayu.code4j.generator.mybatis;

import net.sundayu.code4j.generator.mybatis.constant.DatabaseCRUD;
import net.sundayu.code4j.generator.mybatis.constant.StatementType;
import net.sundayu.code4j.generator.mybatis.util.GeneralUtil;
import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.kotlin.KotlinFile;
import org.mybatis.generator.api.dom.kotlin.KotlinFunction;
import org.mybatis.generator.api.dom.kotlin.KotlinProperty;
import org.mybatis.generator.api.dom.kotlin.KotlinType;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.internal.util.StringUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 新增实现类
 */
public class CustomCommentGenerator implements CommentGenerator {

    private static final Logger LOG = LoggerFactory.getLogger(CustomCommentGenerator.class);

    private Properties properties = new Properties();
    private boolean suppressDate = false;
    private boolean suppressAllComments = false;
    private boolean addRemarkComments = false;
    private SimpleDateFormat dateFormat;
    private FullyQualifiedJavaType javaType;


    @Override
    public void addConfigurationProperties(Properties properties) {
        this.properties.putAll(properties);
        this.suppressDate = StringUtility.isTrue(properties.getProperty("suppressDate"));
        this.suppressAllComments = StringUtility.isTrue(properties.getProperty("suppressAllComments"));
        this.addRemarkComments = StringUtility.isTrue(properties.getProperty("addRemarkComments"));
        String dateFormatString = properties.getProperty("dateFormat");
        if (StringUtility.stringHasValue(dateFormatString)) {
            this.dateFormat = new SimpleDateFormat(dateFormatString);
        }
    }

    @Override
    public void addJavaFileComment(CompilationUnit compilationUnit) {
        compilationUnit.addFileCommentLine("/**");
        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append(" * ");
        sBuilder.append(compilationUnit.getType().getShortName());
        sBuilder.append(".java");
        sBuilder.append(' ');
        sBuilder.append(GeneralUtil.getCurrentDate());
        compilationUnit.addFileCommentLine(sBuilder.toString());
        compilationUnit.addFileCommentLine(" * <p>");
        compilationUnit.addFileCommentLine(" * 版权所有 @ xiyusu since 2021。保留一切权力。");
        compilationUnit.addFileCommentLine(" */");
    }

    @Override
    public void addComment(XmlElement xmlElement) {
        if (!this.suppressAllComments) {
            String methodName = "";
            String comment = "";
            for (Attribute attribute : xmlElement.getAttributes()) {
                if (attribute.getName().equals("id")) {
                    methodName = attribute.getValue();
                    break;
                }
            }
            switch (methodName) {
                case "insert":
                case "insertSelective":
                    comment = "单笔新增";
                    break;
                case "batchInsert":
                    comment = "多笔新增";
                    break;
                case "deleteByPrimaryKey":
                    comment = "单笔删除";
                    break;
                case "selectByPrimaryKey":
                    comment = "单笔查询";
                    break;
                case "updateByPrimaryKey":
                case "updateByPrimaryKeySelective":
                    comment = "单笔更新";
                    break;
                default:
                    comment = getCommets(methodName);
                    break;
            }

            if (GeneralUtil.isEmpty(comment)) {
                xmlElement.addElement(new TextElement("<!-- "));
                StringBuilder sBuilder = new StringBuilder();
                sBuilder.append(" WARNING - ");
                sBuilder.append("@mbg.generated");
                xmlElement.addElement(new TextElement(sBuilder.toString()));
                xmlElement.addElement(new TextElement(" This element automatically generated by Mybatis Generator Windvane, do not modify."));
                xmlElement.addElement(new TextElement("-->"));
            } else {
                StringBuilder sBuilder = new StringBuilder();
                sBuilder.append("<!-- ");
                sBuilder.append(comment);
                sBuilder.append("-->");
                xmlElement.addElement(new TextElement(sBuilder.toString()));
            }
        }
    }

    @Override
    public void addRootComment(XmlElement xmlElement) {

    }


    @Override
    public void addClassComment(InnerClass innerClass, IntrospectedTable introspectedTable) {
        if (!this.suppressAllComments) {
            StringBuilder sb = new StringBuilder();
            innerClass.addJavaDocLine("/**");
            innerClass.addJavaDocLine(" * This class was generated by MyBatis Generator.");
            sb.append(" * This class corresponds to the database table ");
            sb.append(introspectedTable.getFullyQualifiedTable());
            innerClass.addJavaDocLine(sb.toString());
            this.addJavadocTag(innerClass, false);
            innerClass.addJavaDocLine(" */");
        }
    }

    @Override
    public void addClassComment(InnerClass innerClass, IntrospectedTable introspectedTable, boolean markAsDoNotDelete) {
        if (!this.suppressAllComments) {
            StringBuilder sb = new StringBuilder();
            innerClass.addJavaDocLine("/**");
            innerClass.addJavaDocLine(" * This class was generated by MyBatis Generator.");
            sb.append(" * This class corresponds to the database table ");
            sb.append(introspectedTable.getFullyQualifiedTable());
            innerClass.addJavaDocLine(sb.toString());
            this.addJavadocTag(innerClass, markAsDoNotDelete);
            innerClass.addJavaDocLine(" */");
        }
    }

    @Override
    public void addEnumComment(InnerEnum innerEnum, IntrospectedTable introspectedTable) {

    }

    @Override
    public void addModelClassComment(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        javaType = topLevelClass.getType();
        if (!this.suppressAllComments && this.addRemarkComments) {
            topLevelClass.addJavaDocLine("/**");
            topLevelClass.addJavaDocLine(" *");
            StringBuilder sBuilder = new StringBuilder();
            sBuilder.append(" * ");
            sBuilder.append(introspectedTable.getTableConfiguration().getTableName());
            sBuilder.append(' ');
            sBuilder.append(introspectedTable.getRemarks());
            sBuilder.append('.');
            topLevelClass.addJavaDocLine(sBuilder.toString());
            topLevelClass.addJavaDocLine(" */");
        }
    }

    @Override
    public void addModelClassComment(KotlinType modelClass, IntrospectedTable introspectedTable) {
        CommentGenerator.super.addModelClassComment(modelClass, introspectedTable);
    }

    @Override
    public void addFieldComment(Field field, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {
        if (!this.suppressAllComments) {
            field.addJavaDocLine("/**");
            field.addJavaDocLine(" *");
            StringBuilder sBuilder = new StringBuilder();
            sBuilder.append(" * ");
            sBuilder.append(introspectedTable.getTableConfiguration().getTableName());
            sBuilder.append('.');
            sBuilder.append(introspectedColumn.getActualColumnName());
            sBuilder.append(' ');
            sBuilder.append(introspectedColumn.getRemarks());
            sBuilder.append('.');
            field.addJavaDocLine(sBuilder.toString());
            field.addJavaDocLine(" */");
        }
    }

    @Override
    public void addFieldComment(Field field, IntrospectedTable introspectedTable) {
        if (!this.suppressAllComments) {
            StringBuilder sb = new StringBuilder();
            field.addJavaDocLine("/**");
            field.addJavaDocLine(" * This field was generated by MyBatis Generator.");
            sb.append(" * This field corresponds to the database table ");
            sb.append(introspectedTable.getFullyQualifiedTable());
            field.addJavaDocLine(sb.toString());
            this.addJavadocTag(field, false);
            field.addJavaDocLine(" */");
        }
    }

    @Override
    public void addGeneralMethodComment(Method method, IntrospectedTable introspectedTable) {
        if (!this.suppressAllComments) {
            String tableName = introspectedTable.getTableConfiguration().getTableName();
            StringBuilder sBuilder = new StringBuilder();
            List<Parameter> parameterList = method.getParameters();
            if (Optional.empty().equals(method.getReturnType()) && parameterList.size() == 0) {
                return;
            }
            String returnDesc = "line";
            method.addJavaDocLine("/**");
            if (method.getName().startsWith("insert")) {
                method.addJavaDocLine(" * 新增记录");
            } else if (method.getName().startsWith("select")) {
                method.addJavaDocLine(" * 查询记录");
                returnDesc = "结果";
            } else if (method.getName().startsWith("update")) {
                method.addJavaDocLine(" * 更新记录");
            } else if (method.getName().startsWith("delete")) {
                method.addJavaDocLine(" * 删除记录");
            }
            method.addJavaDocLine(" *");

            for (Parameter parameter : method.getParameters()) {
                String paraName = GeneralUtil.humpToUnderLine(parameter.getName());
                Optional<IntrospectedColumn> optional = introspectedTable.getColumn(paraName);
                sBuilder.append(" * @param ");
                sBuilder.append(parameter.getName());
                if (optional.isPresent()) {
                    sBuilder.append(' ');
                    sBuilder.append(tableName);
                    sBuilder.append('.');
                    sBuilder.append(optional.get().getActualColumnName());
                    sBuilder.append(' ');
                    sBuilder.append(optional.get().getRemarks());
                    sBuilder.append('.');
                } else {
                    FullyQualifiedJavaType listType = FullyQualifiedJavaType.getNewListInstance();
                    listType.addTypeArgument(javaType);
                    if (parameter.getType().equals(javaType)) {
                        // 单笔
                        sBuilder.append(" 实例");
                        sBuilder.append('.');
                    } else if (parameter.getType().equals(listType)) {
                        // 多笔
                        sBuilder.append(" 实例列表");
                        sBuilder.append('.');
                    } else {
                        LOG.warn("未找到：{}，不生成参数注释", paraName);
                    }
                }
                method.addJavaDocLine(sBuilder.toString());
                sBuilder.setLength(0);
            }
            if (!Optional.empty().equals(method.getReturnType())) {
                method.addJavaDocLine(" * @return " + returnDesc);
            }
            method.addJavaDocLine(" */");
        }
    }


    public void addGetterComment(Method method, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {
        if (!this.suppressAllComments) {
            method.addJavaDocLine("/**");
            StringBuilder sb = new StringBuilder();
            sb.append(" * Get the value of ");
            sb.append(introspectedTable.getTableConfiguration().getTableName());
            sb.append('.');
            sb.append(introspectedColumn.getActualColumnName());
            sb.append(' ');
            sb.append(introspectedColumn.getRemarks());
            method.addJavaDocLine(sb.toString());
            method.addJavaDocLine(" *");
            sb.setLength(0);
            sb.append(" * @return ");
            String name = method.getName().substring(3);
            String returnPara = name.substring(0, 1).toLowerCase() + name.substring(1);
            sb.append(returnPara);
            sb.append(' ');
            sb.append(introspectedColumn.getRemarks());
            method.addJavaDocLine(sb.toString());
            method.addJavaDocLine(" */");
        }
    }

    public void addSetterComment(Method method, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {
        if (!this.suppressAllComments) {
            method.addJavaDocLine("/**");
            StringBuilder sb = new StringBuilder();
            sb.append(" * Set value for ");
            sb.append(introspectedTable.getTableConfiguration().getTableName());
            sb.append('.');
            sb.append(introspectedColumn.getActualColumnName());
            sb.append(' ');
            sb.append(introspectedColumn.getRemarks());
            method.addJavaDocLine(sb.toString());
            method.addJavaDocLine(" *");
            sb.setLength(0);
            sb.append(" * @param ");
            Parameter param = method.getParameters().get(0);
            sb.append(param.getName());
            sb.append(' ');
            sb.append(introspectedColumn.getRemarks());
            method.addJavaDocLine(sb.toString());
            method.addJavaDocLine(" */");
        }
    }

    public void addGeneralMethodAnnotation(Method method, IntrospectedTable introspectedTable, Set<FullyQualifiedJavaType> imports) {
        imports.add(new FullyQualifiedJavaType("javax.annotation.Generated"));
        String comment = "Source Table: " + introspectedTable.getFullyQualifiedTable().toString();
        method.addAnnotation(this.getGeneratedAnnotation(comment));
    }

    public void addGeneralMethodAnnotation(Method method, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn, Set<FullyQualifiedJavaType> imports) {
        imports.add(new FullyQualifiedJavaType("javax.annotation.Generated"));
        String comment = "Source field: " + introspectedTable.getFullyQualifiedTable().toString() + '.' + introspectedColumn.getActualColumnName();
        method.addAnnotation(this.getGeneratedAnnotation(comment));
    }

    public void addFieldAnnotation(Field field, IntrospectedTable introspectedTable, Set<FullyQualifiedJavaType> imports) {
        imports.add(new FullyQualifiedJavaType("javax.annotation.Generated"));
        String comment = "Source Table: " + introspectedTable.getFullyQualifiedTable().toString();
        field.addAnnotation(this.getGeneratedAnnotation(comment));
    }

    public void addFieldAnnotation(Field field, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn, Set<FullyQualifiedJavaType> imports) {
        imports.add(new FullyQualifiedJavaType("javax.annotation.Generated"));
        String comment = "Source field: " + introspectedTable.getFullyQualifiedTable().toString() + '.' + introspectedColumn.getActualColumnName();
        field.addAnnotation(this.getGeneratedAnnotation(comment));
        if (!this.suppressAllComments && this.addRemarkComments) {
            String remarks = introspectedColumn.getRemarks();
            if (this.addRemarkComments && StringUtility.stringHasValue(remarks)) {
                field.addJavaDocLine("/**");
                field.addJavaDocLine(" * Database Column Remarks:");
                String[] remarkLines = remarks.split(System.getProperty("line.separator"));
                String[] var8 = remarkLines;
                int var9 = remarkLines.length;

                for (int var10 = 0; var10 < var9; ++var10) {
                    String remarkLine = var8[var10];
                    field.addJavaDocLine(" *   " + remarkLine);
                }

                field.addJavaDocLine(" */");
            }
        }

    }

    public void addClassAnnotation(InnerClass innerClass, IntrospectedTable introspectedTable, Set<FullyQualifiedJavaType> imports) {
        imports.add(new FullyQualifiedJavaType("javax.annotation.Generated"));
        String comment = "Source Table: " + introspectedTable.getFullyQualifiedTable().toString();
        innerClass.addAnnotation(this.getGeneratedAnnotation(comment));
    }

    private String getGeneratedAnnotation(String comment) {
        StringBuilder buffer = new StringBuilder();
        buffer.append("@Generated(");
        if (this.suppressAllComments) {
            buffer.append('"');
        } else {
            buffer.append("value=\"");
        }

        buffer.append(MyBatisGenerator.class.getName());
        buffer.append('"');
        if (!this.suppressDate && !this.suppressAllComments) {
            buffer.append(", date=\"");
            buffer.append(DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(ZonedDateTime.now()));
            buffer.append('"');
        }

        if (!this.suppressAllComments) {
            buffer.append(", comments=\"");
            buffer.append(comment);
            buffer.append('"');
        }

        buffer.append(')');
        return buffer.toString();
    }

    public void addFileComment(KotlinFile kotlinFile) {
        if (!this.suppressAllComments) {
            kotlinFile.addFileCommentLine("/*");
            kotlinFile.addFileCommentLine(" * Auto-generated file. Created by MyBatis Generator");
            if (!this.suppressDate) {
                kotlinFile.addFileCommentLine(" * Generation date: " + DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(ZonedDateTime.now()));
            }

            kotlinFile.addFileCommentLine(" */");
        }
    }

    @Override
    public void addGeneralFunctionComment(KotlinFunction kf, IntrospectedTable introspectedTable, Set<String> imports) {
        CommentGenerator.super.addGeneralFunctionComment(kf, introspectedTable, imports);
    }

    @Override
    public void addGeneralPropertyComment(KotlinProperty property, IntrospectedTable introspectedTable, Set<String> imports) {
        CommentGenerator.super.addGeneralPropertyComment(property, introspectedTable, imports);
    }


    protected void addJavadocTag(JavaElement javaElement, boolean markAsDoNotDelete) {
        javaElement.addJavaDocLine(" *");
        StringBuilder sb = new StringBuilder();
        sb.append(" * ");
        sb.append("@mbg.generated");
        if (markAsDoNotDelete) {
            sb.append(" do_not_delete_during_merge");
        }

        String s = this.getDateString();
        if (s != null) {
            sb.append(' ');
            sb.append(s);
        }

        javaElement.addJavaDocLine(sb.toString());
    }

    protected String getDateString() {
        if (this.suppressDate) {
            return null;
        } else {
            return this.dateFormat != null ? this.dateFormat.format(new Date()) : (new Date()).toString();
        }
    }


    /**
     * 获取注释
     *
     * @param methodName 方法名
     * @return 注释
     */
    private String getCommets(String methodName) {
        String comment = "";
        if (methodName.endsWith(StatementType.NOLOCK_RECORD_A.get())) {
            comment = DatabaseCRUD.RETRIEVE.desc() + StatementType.NOLOCK_RECORD_A.desc();
        } else if (methodName.endsWith(StatementType.NOLOCK_RECORD_0.get())) {
            comment = DatabaseCRUD.RETRIEVE.desc() + StatementType.NOLOCK_RECORD_0.desc();
        } else if (methodName.endsWith(StatementType.NOLOCK_RECORD_1.get())) {
            comment = DatabaseCRUD.RETRIEVE.desc() + StatementType.NOLOCK_RECORD_1.desc();
        } else if (methodName.endsWith(StatementType.LOCK_RECORD_A.get())) {
            comment = DatabaseCRUD.RETRIEVE.desc() + StatementType.LOCK_RECORD_A.desc();
        } else if (methodName.endsWith(StatementType.LOCK_RECORD_0.get())) {
            comment = DatabaseCRUD.RETRIEVE.desc() + StatementType.LOCK_RECORD_0.desc();
        } else if (methodName.endsWith(StatementType.LOCK_RECORD_1.get())) {
            comment = DatabaseCRUD.RETRIEVE.desc() + StatementType.LOCK_RECORD_1.desc();
        } else if (methodName.startsWith("update") && methodName.endsWith(StatementType.RECORD_SINGLE_A.get())) {
            comment = DatabaseCRUD.UPDATE.desc() + StatementType.RECORD_SINGLE_A.desc();
        } else if (methodName.startsWith("update") && methodName.endsWith(StatementType.RECORD_SINGLE_0.get())) {
            comment = DatabaseCRUD.UPDATE.desc() + StatementType.RECORD_SINGLE_0.desc();
        } else if (methodName.startsWith("update") && methodName.endsWith(StatementType.RECORD_SINGLE_1.get())) {
            comment = DatabaseCRUD.UPDATE.desc() + StatementType.RECORD_SINGLE_1.desc();
        } else if (methodName.startsWith("update") && methodName.endsWith(StatementType.RECORD_MULTI_A.get())) {
            comment = DatabaseCRUD.UPDATE.desc() + StatementType.RECORD_MULTI_A.desc();
        } else if (methodName.startsWith("update") && methodName.endsWith(StatementType.RECORD_MULTI_0.get())) {
            comment = DatabaseCRUD.UPDATE.desc() + StatementType.RECORD_MULTI_0.desc();
        } else if (methodName.startsWith("update") && methodName.endsWith(StatementType.RECORD_MULTI_1.get())) {
            comment = DatabaseCRUD.UPDATE.desc() + StatementType.RECORD_MULTI_1.desc();
        } else if (methodName.startsWith("delete") && methodName.endsWith(StatementType.RECORD_SINGLE_A.get())) {
            comment = DatabaseCRUD.UPDATE.desc() + StatementType.RECORD_SINGLE_A.desc();
        } else if (methodName.startsWith("delete") && methodName.endsWith(StatementType.RECORD_SINGLE_0.get())) {
            comment = DatabaseCRUD.DELETE.desc() + StatementType.RECORD_SINGLE_0.desc();
        } else if (methodName.startsWith("delete") && methodName.endsWith(StatementType.RECORD_SINGLE_1.get())) {
            comment = DatabaseCRUD.DELETE.desc() + StatementType.RECORD_SINGLE_1.desc();
        } else if (methodName.startsWith("delete") && methodName.endsWith(StatementType.RECORD_MULTI_A.get())) {
            comment = DatabaseCRUD.DELETE.desc() + StatementType.RECORD_MULTI_A.desc();
        } else if (methodName.startsWith("delete") && methodName.endsWith(StatementType.RECORD_MULTI_0.get())) {
            comment = DatabaseCRUD.DELETE.desc() + StatementType.RECORD_MULTI_0.desc();
        } else if (methodName.startsWith("delete") && methodName.endsWith(StatementType.RECORD_MULTI_1.get())) {
            comment = DatabaseCRUD.DELETE.desc() + StatementType.RECORD_MULTI_1.desc();
        }
        return comment;
    }
}
