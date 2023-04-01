package net.sundayu.code4j.generator.mybatis.pojo;

import org.mybatis.generator.api.IntrospectedColumn;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author xiyusu
 * @datetime 2021年05月24 23:54
 * @description 索引信息
 */
public class IndexInfo {
    /**
     * 索引名称
     */
    private String idxName;

    /**
     * 表名
     */
    private String tblName;

    /**
     * 是否唯一
     */
    private boolean unique;

    /**
     * 列名
     */
    private Set<String> columnList;


    /**
     * 列
     */
    private Set<IntrospectedColumn> introspectedColumns;

    public String getIdxName() {
        return idxName;
    }

    public void setIdxName(String idxName) {
        this.idxName = idxName;
    }

    public String getTblName() {
        return tblName;
    }

    public void setTblName(String tblName) {
        this.tblName = tblName;
    }

    public boolean isUnique() {
        return unique;
    }

    public void setUnique(boolean unique) {
        this.unique = unique;
    }

    public Set<String> getColumnList() {
        return columnList;
    }

    public void setColumnList(Set<String> columnList) {
        this.columnList = columnList;
    }

    public Set<IntrospectedColumn> getIntrospectedColumns() {
        return introspectedColumns;
    }

    public void setIntrospectedColumns(Set<IntrospectedColumn> introspectedColumns) {
        this.introspectedColumns = introspectedColumns;
    }

    public IndexInfo() {
        this.idxName = "";
        this.tblName = "";
        this.unique = false;
        this.columnList = new LinkedHashSet<>();
        this.introspectedColumns = new LinkedHashSet<>();
    }

    @Override
    public String toString() {
        String mess = "";
        if (unique) {
            mess = "唯一索引";
        } else {
            mess = "非唯一索引";
        }
        return "表名：" + tblName + "，" + mess + "：" + idxName + "，索引字段：" + columnListToString();
    }

    private String columnListToString() {
        StringBuilder sb = new StringBuilder();
        for (String column : columnList) {
            sb.append(column);
            sb.append(",");
        }
        String result = sb.toString();
        if (null != result && !"".equals(result)) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }
}
