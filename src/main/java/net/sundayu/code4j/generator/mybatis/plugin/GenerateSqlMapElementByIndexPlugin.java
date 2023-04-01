package net.sundayu.code4j.generator.mybatis.plugin;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.sundayu.code4j.generator.mybatis.constant.StatementType;
import net.sundayu.code4j.generator.mybatis.plugin.base.GenerateCodeByIndexBasePlugin;
import net.sundayu.code4j.generator.mybatis.pojo.IndexInfo;

import java.util.Set;

/**
 * @author xiyusu
 * @datetime 2021年04月14 23:16
 * <p>
 * 插件：根据索引生成select查询语句(sqlMap)
 */
public class GenerateSqlMapElementByIndexPlugin extends GenerateCodeByIndexBasePlugin {
    private static final Logger LOG = LoggerFactory.getLogger(GenerateSqlMapElementByIndexPlugin.class);

    @Override
    public void generateSelectElementByIndex(XmlElement element, IntrospectedTable introspectedTable, IndexInfo indexInfo, StatementType type) {
        String methodName = getMethodNameByIndexName(indexInfo.getIdxName(), "select") + type.get();
        XmlElement selectElement = new XmlElement("select");
        selectElement.addAttribute(new Attribute("id", methodName));
        // sqlMap增加注释
        context.getCommentGenerator().addComment(selectElement);
        String paraType = "";
        if (introspectedTable.getRules().generateRecordWithBLOBsClass()) {
            paraType = introspectedTable.getRecordWithBLOBsType();
        } else {
            paraType = introspectedTable.getBaseRecordType();
        }
        //不指定parameterType
        //selectElement.addAttribute(new Attribute("parameterType", paraType));
        selectElement.addAttribute(new Attribute("resultMap", introspectedTable.getBaseResultMapId()));
        selectElement.addElement(new TextElement("select"));
        XmlElement includeElement = new XmlElement("include");
        includeElement.addAttribute(new Attribute("refid", introspectedTable.getBaseColumnListId()));
        selectElement.addElement(includeElement);
        StringBuilder sb = new StringBuilder();
        sb.append("from ");
        sb.append(introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime());
        selectElement.addElement(new TextElement(sb.toString()));
        sb.setLength(0);
        sb.append("where ");

        Set<IntrospectedColumn> introspectedColumns = indexInfo.getIntrospectedColumns();
        int count = 0;
        for (IntrospectedColumn introspectedColumn : introspectedColumns) {
            sb.append(MyBatis3FormattingUtilities.getAliasedEscapedColumnName(introspectedColumn));
            sb.append(" = ");
            sb.append(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn));
            selectElement.addElement(new TextElement(sb.toString()));
            sb.setLength(0);
            if (count < introspectedColumns.size() - 1) {
                sb.append("  and ");
            }
            count++;
        }
        Class clazz = getRecdStatType(introspectedTable);
        if (clazz == Integer.class) {
            switch (type) {
                case NOLOCK_RECORD_0:
                    selectElement.addElement(new TextElement("  and recd_stat = 0 "));
                    break;
                case NOLOCK_RECORD_1:
                    selectElement.addElement(new TextElement("  and recd_stat = 1 "));
                    break;
                case LOCK_RECORD_A:
                    selectElement.addElement(new TextElement("for update"));
                    break;
                case LOCK_RECORD_0:
                    selectElement.addElement(new TextElement("  and recd_stat = 0 "));
                    selectElement.addElement(new TextElement("for update"));
                    break;
                case LOCK_RECORD_1:
                    selectElement.addElement(new TextElement("  and recd_stat = 1 "));
                    selectElement.addElement(new TextElement("for update"));
                    break;
                default:
                    break;
            }
        } else if (clazz == String.class) {
            switch (type) {
                case NOLOCK_RECORD_0:
                    selectElement.addElement(new TextElement("  and recd_stat = '0' "));
                    break;
                case NOLOCK_RECORD_1:
                    selectElement.addElement(new TextElement("  and recd_stat = '1' "));
                    break;
                case LOCK_RECORD_A:
                    selectElement.addElement(new TextElement("for update"));
                    break;
                case LOCK_RECORD_0:
                    selectElement.addElement(new TextElement("  and recd_stat = '0' "));
                    selectElement.addElement(new TextElement("for update"));
                    break;
                case LOCK_RECORD_1:
                    selectElement.addElement(new TextElement("  and recd_stat = '1' "));
                    selectElement.addElement(new TextElement("for update"));
                    break;
                default:
                    break;
            }
        }
        xmlElements.add(selectElement);
    }

    @Override
    public void generateSingleUpdateElement(XmlElement element, IntrospectedTable introspectedTable, IndexInfo indexInfo, StatementType type) {
        String methodName = getMethodNameByIndexName(indexInfo.getIdxName(), "update") + type.get();
        XmlElement updateElement = new XmlElement("update");
        updateElement.addAttribute(new Attribute("id", methodName));
        // sqlMap增加注释
        context.getCommentGenerator().addComment(updateElement);
        String paraType = "";
        if (introspectedTable.getRules().generateRecordWithBLOBsClass()) {
            paraType = introspectedTable.getRecordWithBLOBsType();
        } else {
            paraType = introspectedTable.getBaseRecordType();
        }
        //指定parameterType
        updateElement.addAttribute(new Attribute("parameterType", paraType));
        StringBuilder sb = new StringBuilder();
        sb.append("update ");
        sb.append(introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime());
        updateElement.addElement(new TextElement(sb.toString()));
        sb.setLength(0);
        sb.append("set");
        Set<IntrospectedColumn> introspectedColumns = indexInfo.getIntrospectedColumns();
        int count = 0;
        for (IntrospectedColumn introspectedColumn : introspectedTable.getAllColumns()) {
            if (introspectedColumns.contains(introspectedColumn)) {
                count++;
                continue;
            }
            if (sb.length() == 3) {
                sb.append(" ");
            } else {
                sb.append("  ");
            }
            sb.append(MyBatis3FormattingUtilities.getAliasedEscapedColumnName(introspectedColumn));
            sb.append(" = ");
            sb.append(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn));
            if (count < introspectedTable.getAllColumns().size() - 1) {
                sb.append(",");
            }
            updateElement.addElement(new TextElement(sb.toString()));
            sb.setLength(0);
            count++;
        }
        sb.append("where ");
        count = 0;
        for (IntrospectedColumn introspectedColumn : introspectedColumns) {
            sb.append(MyBatis3FormattingUtilities.getAliasedEscapedColumnName(introspectedColumn));
            sb.append(" = ");
            sb.append(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn));
            updateElement.addElement(new TextElement(sb.toString()));
            sb.setLength(0);
            if (count < introspectedColumns.size() - 1) {
                sb.append("  and ");
            }
            count++;
        }

        Class clazz = getRecdStatType(introspectedTable);
        if (clazz == Integer.class) {
            switch (type) {
                case RECORD_SINGLE_A:
                    break;
                case RECORD_SINGLE_0:
                    updateElement.addElement(new TextElement("  and recd_stat = 0 "));
                    break;
                case RECORD_SINGLE_1:
                    updateElement.addElement(new TextElement("  and recd_stat = 1 "));
                    break;
                default:
                    break;
            }
        } else if (clazz == String.class) {
            switch (type) {
                case RECORD_SINGLE_A:
                    break;
                case RECORD_SINGLE_0:
                    updateElement.addElement(new TextElement("  and recd_stat = '0' "));
                    break;
                case RECORD_SINGLE_1:
                    updateElement.addElement(new TextElement("  and recd_stat = '1' "));
                    break;
                default:
                    break;
            }
        }
        xmlElements.add(updateElement);
    }

    @Override
    public void generateBatchUpdateElement(XmlElement element, IntrospectedTable introspectedTable, IndexInfo indexInfo, StatementType type) {
        String methodName = getMethodNameByIndexName(indexInfo.getIdxName(), "update") + type.get();
        XmlElement batchUpdateElement = new XmlElement("update");
        batchUpdateElement.addAttribute(new Attribute("id", methodName));
        // sqlMap增加注释
        context.getCommentGenerator().addComment(batchUpdateElement);
        //指定parameterType
        batchUpdateElement.addAttribute(new Attribute("parameterType", "java.util.List"));
        //foreach
        XmlElement foreachXmlElement = new XmlElement("foreach");
        foreachXmlElement.addAttribute(new Attribute("collection", "rows"));
        foreachXmlElement.addAttribute(new Attribute("item", "item"));
        // 可选
        foreachXmlElement.addAttribute(new Attribute("index", "index"));
        foreachXmlElement.addAttribute(new Attribute("open", ""));
        foreachXmlElement.addAttribute(new Attribute("close", ""));
        foreachXmlElement.addAttribute(new Attribute("separator", ";"));
        batchUpdateElement.addElement(foreachXmlElement);
        StringBuilder sb = new StringBuilder();
        sb.append("update ");
        sb.append(introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime());
        foreachXmlElement.addElement(new TextElement(sb.toString()));
        sb.setLength(0);
        XmlElement setXmlElement = new XmlElement("set");
        foreachXmlElement.addElement(setXmlElement);
        Set<IntrospectedColumn> introspectedColumns = indexInfo.getIntrospectedColumns();
        int count = 0;
        for (IntrospectedColumn introspectedColumn : introspectedTable.getAllColumns()) {
            if (introspectedColumns.contains(introspectedColumn)) {
                count++;
                continue;
            }
            sb.append("  ");
            sb.append(MyBatis3FormattingUtilities.getAliasedEscapedColumnName(introspectedColumn));
            sb.append(" = ");
            sb.append(paraToItemPara(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn), "item"));
            if (count < introspectedTable.getAllColumns().size() - 1) {
                sb.append(",");
            }
            setXmlElement.addElement(new TextElement(sb.toString()));
            sb.setLength(0);
            count++;
        }
        sb.append("where ");
        count = 0;
        for (IntrospectedColumn introspectedColumn : introspectedColumns) {
            sb.append(MyBatis3FormattingUtilities.getAliasedEscapedColumnName(introspectedColumn));
            sb.append(" = ");
            sb.append(paraToItemPara(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn), "item"));
            foreachXmlElement.addElement(new TextElement(sb.toString()));
            sb.setLength(0);
            if (count < introspectedColumns.size() - 1) {
                sb.append("  and ");
            }
            count++;
        }
        Class clazz = getRecdStatType(introspectedTable);
        if (clazz == Integer.class) {
            switch (type) {
                case RECORD_MULTI_0:
                    foreachXmlElement.addElement(new TextElement("  and recd_stat = 0 "));
                    break;
                case RECORD_MULTI_1:
                    foreachXmlElement.addElement(new TextElement("  and recd_stat = 1 "));
                    break;
                default:
                    break;
            }
        } else if (clazz == String.class) {
            switch (type) {
                case RECORD_MULTI_0:
                    foreachXmlElement.addElement(new TextElement("  and recd_stat = '0' "));
                    break;
                case RECORD_MULTI_1:
                    foreachXmlElement.addElement(new TextElement("  and recd_stat = '1' "));
                    break;
                default:
                    break;
            }
        }
        xmlElements.add(batchUpdateElement);
    }

    @Override
    public void generateSingleUpdateSelectiveElement(XmlElement element, IntrospectedTable introspectedTable, IndexInfo indexInfo, StatementType type) {

    }

    @Override
    public void generateBatchUpdateSelectiveElement(XmlElement element, IntrospectedTable introspectedTable, IndexInfo indexInfo, StatementType type) {

    }

    @Override
    public void generateDeleteElementByIndex(XmlElement element, IntrospectedTable introspectedTable, IndexInfo indexInfo, StatementType type) {
        String methodName = getMethodNameByIndexName(indexInfo.getIdxName(), "delete") + type.get();
        XmlElement deleteElement = new XmlElement("delete");
        deleteElement.addAttribute(new Attribute("id", methodName));
        // sqlMap增加注释
        context.getCommentGenerator().addComment(deleteElement);
        StringBuilder sb = new StringBuilder();
        sb.append("delete from ");
        sb.append(introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime());
        deleteElement.addElement(new TextElement(sb.toString()));
        sb.setLength(0);
        sb.append("where ");
        Set<IntrospectedColumn> introspectedColumns = indexInfo.getIntrospectedColumns();
        int count = 0;
        for (IntrospectedColumn introspectedColumn : introspectedColumns) {
            sb.append(MyBatis3FormattingUtilities.getAliasedEscapedColumnName(introspectedColumn));
            sb.append(" = ");
            sb.append(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn));
            deleteElement.addElement(new TextElement(sb.toString()));
            sb.setLength(0);
            if (count < introspectedColumns.size() - 1) {
                sb.append("  and ");
            }
            count++;
        }

        Class clazz = getRecdStatType(introspectedTable);
        if (clazz == Integer.class) {
            switch (type) {
                case RECORD_SINGLE_0:
                case RECORD_MULTI_0:
                    deleteElement.addElement(new TextElement("  and recd_stat = 0 "));
                    break;
                case RECORD_SINGLE_1:
                case RECORD_MULTI_1:
                    deleteElement.addElement(new TextElement("  and recd_stat = 1 "));
                    break;
                default:
                    break;
            }
        } else if (clazz == String.class) {
            switch (type) {
                case RECORD_SINGLE_0:
                case RECORD_MULTI_0:
                    deleteElement.addElement(new TextElement("  and recd_stat = '0' "));
                    break;
                case RECORD_SINGLE_1:
                case RECORD_MULTI_1:
                    deleteElement.addElement(new TextElement("  and recd_stat = '1' "));
                    break;
                default:
                    break;
            }
        }
        xmlElements.add(deleteElement);
    }

    @Override
    public void generateSingleDeleteElement(XmlElement element, IntrospectedTable introspectedTable, IndexInfo indexInfo, StatementType type) {
        String methodName = getMethodNameByIndexName(indexInfo.getIdxName(), "delete") + "Record" + type.get();
        XmlElement deleteElement = new XmlElement("delete");
        deleteElement.addAttribute(new Attribute("id", methodName));
        // sqlMap增加注释
        context.getCommentGenerator().addComment(deleteElement);
        String paraType = "";
        if (introspectedTable.getRules().generateRecordWithBLOBsClass()) {
            paraType = introspectedTable.getRecordWithBLOBsType();
        } else {
            paraType = introspectedTable.getBaseRecordType();
        }
        //指定parameterType
        deleteElement.addAttribute(new Attribute("parameterType", paraType));
        StringBuilder sb = new StringBuilder();
        sb.append("delete from ");
        sb.append(introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime());
        deleteElement.addElement(new TextElement(sb.toString()));
        sb.setLength(0);
        sb.append("where ");
        Set<IntrospectedColumn> introspectedColumns = indexInfo.getIntrospectedColumns();
        int count = 0;
        for (IntrospectedColumn introspectedColumn : introspectedColumns) {
            sb.append(MyBatis3FormattingUtilities.getAliasedEscapedColumnName(introspectedColumn));
            sb.append(" = ");
            sb.append(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn));
            deleteElement.addElement(new TextElement(sb.toString()));
            sb.setLength(0);
            if (count < introspectedColumns.size() - 1) {
                sb.append("  and ");
            }
            count++;
        }
        xmlElements.add(deleteElement);
    }

    @Override
    public void generateBatchDeleteElement(XmlElement element, IntrospectedTable introspectedTable, IndexInfo indexInfo, StatementType type) {
        String methodName = getMethodNameByIndexName(indexInfo.getIdxName(), "delete") + type.get();
        XmlElement batchDeleteElement = new XmlElement("delete");
        batchDeleteElement.addAttribute(new Attribute("id", methodName));
        // sqlMap增加注释
        context.getCommentGenerator().addComment(batchDeleteElement);
        //指定parameterType
        batchDeleteElement.addAttribute(new Attribute("parameterType", "java.util.List"));
        //foreach
        XmlElement foreachXmlElement = new XmlElement("foreach");
        foreachXmlElement.addAttribute(new Attribute("collection", "rows"));
        foreachXmlElement.addAttribute(new Attribute("item", "item"));
        // 可选
        foreachXmlElement.addAttribute(new Attribute("index", "index"));
        foreachXmlElement.addAttribute(new Attribute("open", ""));
        foreachXmlElement.addAttribute(new Attribute("close", ""));
        foreachXmlElement.addAttribute(new Attribute("separator", ";"));
        batchDeleteElement.addElement(foreachXmlElement);
        StringBuilder sb = new StringBuilder();
        sb.append("delete from ");
        sb.append(introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime());
        foreachXmlElement.addElement(new TextElement(sb.toString()));
        sb.setLength(0);
        Set<IntrospectedColumn> introspectedColumns = indexInfo.getIntrospectedColumns();
        sb.append("where ");
        int count = 0;
        for (IntrospectedColumn introspectedColumn : introspectedColumns) {
            sb.append(MyBatis3FormattingUtilities.getAliasedEscapedColumnName(introspectedColumn));
            sb.append(" = ");
            sb.append(paraToItemPara(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn), "item"));
            foreachXmlElement.addElement(new TextElement(sb.toString()));
            sb.setLength(0);
            if (count < introspectedColumns.size() - 1) {
                sb.append("  and ");
            }
            count++;
        }
        Class clazz = getRecdStatType(introspectedTable);
        if (clazz == Integer.class) {
            switch (type) {
                case RECORD_MULTI_0:
                    foreachXmlElement.addElement(new TextElement("  and recd_stat = 0 "));
                    break;
                case RECORD_MULTI_1:
                    foreachXmlElement.addElement(new TextElement("  and recd_stat = 1 "));
                    break;
                default:
                    break;
            }
        } else if (clazz == String.class) {
            switch (type) {
                case RECORD_MULTI_0:
                    foreachXmlElement.addElement(new TextElement("  and recd_stat = '0' "));
                    break;
                case RECORD_MULTI_1:
                    foreachXmlElement.addElement(new TextElement("  and recd_stat = '1' "));
                    break;
                default:
                    break;
            }
        }
        xmlElements.add(batchDeleteElement);
    }

    @Override
    public void generateInsertElement(XmlElement element, IntrospectedTable introspectedTable, StatementType type) {

    }

    @Override
    public void generateInsertSelectiveElement(XmlElement element, IntrospectedTable introspectedTable, IndexInfo indexInfo, StatementType type) {

    }

    @Override
    public void generateBatchInsertElement(XmlElement element, IntrospectedTable introspectedTable, StatementType type) {
        XmlElement batchInsertElement = new XmlElement("insert");
        batchInsertElement.addAttribute(new Attribute("id", "batchInsert"));
        // sqlMap增加注释
        context.getCommentGenerator().addComment(batchInsertElement);
        //指定parameterType
        batchInsertElement.addAttribute(new Attribute("parameterType", "java.util.List"));
        //foreach
        XmlElement foreachXmlElement = new XmlElement("foreach");
        foreachXmlElement.addAttribute(new Attribute("collection", "rows"));
        foreachXmlElement.addAttribute(new Attribute("item", "item"));
        // 可选
        foreachXmlElement.addAttribute(new Attribute("index", "index"));
        foreachXmlElement.addAttribute(new Attribute("open", ""));
        foreachXmlElement.addAttribute(new Attribute("close", ""));
        foreachXmlElement.addAttribute(new Attribute("separator", ";"));
        batchInsertElement.addElement(foreachXmlElement);
        StringBuilder sb = new StringBuilder();
        sb.append("insert into ");
        sb.append(introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime());
        foreachXmlElement.addElement(new TextElement(sb.toString()));
        sb.setLength(0);
        int count = 0;
        int changeLine = 1;
        sb.append("(");
        for (IntrospectedColumn introspectedColumn : introspectedTable.getAllColumns()) {
            sb.append(" ");
            sb.append(MyBatis3FormattingUtilities.getAliasedEscapedColumnName(introspectedColumn));
            if (count < introspectedTable.getAllColumns().size() - 1) {
                sb.append(",");
            }
            if (changeLine % 5 == 0) {
                foreachXmlElement.addElement(new TextElement(sb.toString()));
                sb.setLength(0);
            }
            count++;
            changeLine++;
        }
        if (sb.length() > 0) {
            foreachXmlElement.addElement(new TextElement(sb.toString()));
            sb.setLength(0);
        }
        foreachXmlElement.addElement(new TextElement(")"));
        count = 0;
        sb.append("values (");
        for (IntrospectedColumn introspectedColumn : introspectedTable.getAllColumns()) {
            sb.append(" ");
            sb.append(paraToItemPara(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn), "item"));
            if (count < introspectedTable.getAllColumns().size() - 1) {
                sb.append(",");
            } else {
                sb.append(")");
            }
            foreachXmlElement.addElement(new TextElement(sb.toString()));
            sb.setLength(0);
            count++;
        }
        xmlElements.add(batchInsertElement);
    }

    /**
     * 例：#{globlJnno,jdbcType=VARCHAR} -> #{item.globlJnno,jdbcType=VARCHAR}
     *
     * @param para 字段
     * @param var  item
     * @return
     */
    private String paraToItemPara(String para, String var) {
        if (null == para || "".equals(para)) {
            return para;
        }
        return para.replace("{", "{" + var + ".");
    }

    /**
     * 获取recd_stat数据类型，无此字段则log error
     * 不能使用sql隐式转换，必须找到原始类型，再拼接sql
     *
     * @param introspectedTable 表
     * @return 类型
     */
    private Class getRecdStatType(IntrospectedTable introspectedTable) {
        try {
            IntrospectedColumn introspectedColumn = introspectedTable.getColumn("recd_stat").get();
            switch (introspectedColumn.getFullyQualifiedJavaType().getFullyQualifiedName()) {
                case "int":
                case "java.lang.Integer":
                case "java.lang.Long":
                    return Integer.class;
                case "java.lang.String":
                    return String.class;
            }
        } catch (Exception e) {
            LOG.warn("表{}中无recd_stat字段", introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime());
        }
        return null;
    }

}

