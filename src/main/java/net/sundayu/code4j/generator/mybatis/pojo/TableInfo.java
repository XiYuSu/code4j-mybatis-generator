package net.sundayu.code4j.generator.mybatis.pojo;


import java.util.LinkedList;
import java.util.List;

/**
 * @author xiyusu
 * @datetime 2021年05月24 23:56
 * @description 表信息
 */
public class TableInfo {

    /**
     * 表名
     */
    private String tblName;

    /**
     * 表描述
     */
    private String tblDesc;

    /**
     * 主键列表
     */
    private List<IndexInfo> primaryKeyList;


    /**
     * 唯一索引（不含主键）
     */
    private List<IndexInfo> uniqueIndexList;

    /**
     * 非唯一索引
     */
    private List<IndexInfo> nonUniqueIndexList;

    public TableInfo() {
        this.tblName = "";
        this.tblDesc = "";
        this.primaryKeyList = new LinkedList<>();
        this.uniqueIndexList = new LinkedList<>();
        this.nonUniqueIndexList = new LinkedList<>();
    }

    public String getTblName() {
        return tblName;
    }

    public void setTblName(String tblName) {
        this.tblName = tblName;
    }

    public String getTblDesc() {
        return tblDesc;
    }

    public void setTblDesc(String tblDesc) {
        this.tblDesc = tblDesc;
    }

    public List<IndexInfo> getPrimaryKeyList() {
        return primaryKeyList;
    }

    public void setPrimaryKeyList(List<IndexInfo> primaryKeyList) {
        this.primaryKeyList = primaryKeyList;
    }

    public List<IndexInfo> getUniqueIndexList() {
        return uniqueIndexList;
    }

    public void setUniqueIndexList(List<IndexInfo> uniqueIndexList) {
        this.uniqueIndexList = uniqueIndexList;
    }

    public List<IndexInfo> getNonUniqueIndexList() {
        return nonUniqueIndexList;
    }

    public void setNonUniqueIndexList(List<IndexInfo> nonUniqueIndexList) {
        this.nonUniqueIndexList = nonUniqueIndexList;
    }

    public IndexInfo getIndexInfo(String indexName, List<IndexInfo> indexInfos) {
        for (IndexInfo indexInfo : indexInfos) {
            if (indexInfo.getIdxName().equals(indexName)) {
                return indexInfo;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "TableInfo{" +
                "tblName='" + tblName + '\'' +
                ", tblDesc='" + tblDesc + '\'' +
                ", primaryKeyList=" + primaryKeyList +
                ", uniqueIndexList=" + uniqueIndexList +
                ", nonUniqueIndexList=" + nonUniqueIndexList +
                '}';
    }
}
