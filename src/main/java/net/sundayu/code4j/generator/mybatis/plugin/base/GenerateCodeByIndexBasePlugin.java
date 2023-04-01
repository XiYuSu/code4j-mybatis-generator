package net.sundayu.code4j.generator.mybatis.plugin.base;

import net.sundayu.code4j.generator.mybatis.api.GenerateCRUDStatement;
import net.sundayu.code4j.generator.mybatis.constant.DatabaseCRUD;
import net.sundayu.code4j.generator.mybatis.constant.StatementType;
import net.sundayu.code4j.generator.mybatis.context.GeneratorContext;
import net.sundayu.code4j.generator.mybatis.pojo.IndexInfo;
import net.sundayu.code4j.generator.mybatis.pojo.TableInfo;
import net.sundayu.code4j.generator.mybatis.util.GeneralUtil;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * @author xiyusu
 * @datetime 2021年05月25 00:15
 * <p>
 * 插件：根据索引生成语句(base)
 * 依赖：Table标签enableInsert=true
 */
public class GenerateCodeByIndexBasePlugin extends PluginAdapter implements GenerateCRUDStatement {
    private static final Logger LOG = LoggerFactory.getLogger(GenerateCodeByIndexBasePlugin.class);

    /**
     * sqlMap节点
     */
    protected Set<XmlElement> xmlElements = new LinkedHashSet<>();

    /**
     * 主键set
     */
    protected static Map<String, List<String>> primaryKeys = new HashMap<>();
    /**
     * model类型（用来生成方法入参、返回参数）
     */
    protected FullyQualifiedJavaType javaType;

    /**
     * 数据库类型
     */
    protected static String DATASOURCE_TYPE = "MYSQL";

    /**
     * 表信息：key:table name value:TableInfo
     */
    private static Map<String, TableInfo> tblMap = new HashMap<>();

    /**
     * xml
     */
    private Document document;

    /**
     * all-所有 unique-唯一索引 non-unique-非唯一索引
     */
    private String indexScope = "all";
    /**
     * 允许主键生成
     */
    private boolean enablePrimaryKey = false;
    /**
     * 允许select
     */
    private boolean enableSelect = true;
    /**
     * 允许update
     */
    private boolean enableUpdate = true;
    /**
     * 允许delete
     */
    private boolean enableDelete = true;
    /**
     * 默认生成（暂时不用,必须为false）
     */
    private final boolean enableInsert = false;
    /**
     * 唯一索引select
     */
    private boolean enableUniqueSelect = true;
    /**
     * 非唯一索引select
     */
    private boolean enableNonUniqueSelect = true;
    /**
     * 单笔update
     */
    private boolean enableSingleUpdate = true;
    /**
     * 批量insert
     */
    private boolean enableBatchUpdate = true;
    /**
     * delete by index
     */
    private boolean enableUniqueIndexDelete = true;
    /**
     * delete by non index
     */
    private boolean enableNonUniqueIndexDelete = true;
    /**
     * 单笔delete
     */
    private boolean enableSingleDelete = true;
    /**
     * 批量delete
     */
    private boolean enableBatchDelete = true;
    /**
     * 单笔insert
     */
    private boolean enableSingleInsert = true;
    /**
     * 批量insert
     */
    private boolean enableBatchInsert = true;
    /**
     * 是否生成forUpdate方法
     */
    private boolean enableForUpdate = false;

    @Override
    public boolean validate(List<String> list) {
        initProp();
        return true;
    }

    private void initProp() {
        String tmp0 = properties.getProperty("indexScope");
        String tmp1 = properties.getProperty("enablePrimaryKey");
        String tmp2 = properties.getProperty("enableSelect");
        String tmp3 = properties.getProperty("enableUpdate");
        String tmp4 = properties.getProperty("enableDelete");
        String tmp5 = properties.getProperty("enableUniqueSelect");
        String tmp6 = properties.getProperty("enableNonUniqueSelect");
        String tmp7 = properties.getProperty("enableSingleUpdate");
        String tmp8 = properties.getProperty("enableBatchUpdate");
        String tmp9 = properties.getProperty("enableSingleDelete");
        String tmp10 = properties.getProperty("enableBatchDelete");
        String tmp11 = properties.getProperty("enableSingleInsert");
        String tmp12 = properties.getProperty("enableBatchInsert");
        String tmp13 = properties.getProperty("enableForUpdate");
        String tmp14 = properties.getProperty("enableUniqueIndexDelete");
        String tmp15 = properties.getProperty("enableNonUniqueIndexDelete");
        indexScope = GeneralUtil.isEmpty(tmp0) ? indexScope : tmp0;
        enablePrimaryKey = GeneralUtil.isEmpty(tmp1) ? enablePrimaryKey : Boolean.valueOf(tmp1);
        enableSelect = GeneralUtil.isEmpty(tmp2) ? enableSelect : Boolean.valueOf(tmp2);
        enableUpdate = GeneralUtil.isEmpty(tmp3) ? enableUpdate : Boolean.valueOf(tmp3);
        enableDelete = GeneralUtil.isEmpty(tmp4) ? enableDelete : Boolean.valueOf(tmp4);
        enableUniqueSelect = GeneralUtil.isEmpty(tmp5) ? enableUniqueSelect : Boolean.valueOf(tmp5);
        enableNonUniqueSelect = GeneralUtil.isEmpty(tmp6) ? enableNonUniqueSelect : Boolean.valueOf(tmp6);
        enableSingleUpdate = GeneralUtil.isEmpty(tmp7) ? enableSingleUpdate : Boolean.valueOf(tmp7);
        enableBatchUpdate = GeneralUtil.isEmpty(tmp8) ? enableBatchUpdate : Boolean.valueOf(tmp8);
        enableSingleDelete = (GeneralUtil.isEmpty(tmp9) ? enableSingleDelete : Boolean.valueOf(tmp9));
        enableBatchDelete = GeneralUtil.isEmpty(tmp10) ? enableBatchDelete : Boolean.valueOf(tmp10);
        enableSingleInsert = GeneralUtil.isEmpty(tmp11) ? enableSingleInsert : Boolean.valueOf(tmp11);
        enableBatchInsert = GeneralUtil.isEmpty(tmp12) ? enableBatchInsert : Boolean.valueOf(tmp12);
        enableForUpdate = GeneralUtil.isEmpty(tmp13) ? enableForUpdate : Boolean.valueOf(tmp13);
        enableUniqueIndexDelete = GeneralUtil.isEmpty(tmp14) ? enableUniqueIndexDelete : Boolean.valueOf(tmp14);
        enableNonUniqueIndexDelete = GeneralUtil.isEmpty(tmp15) ? enableNonUniqueIndexDelete : Boolean.valueOf(tmp15);
    }

    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        javaType = topLevelClass.getType();
        LOG.debug("当前model类：{}", javaType.getFullyQualifiedName());
        return super.modelBaseRecordClassGenerated(topLevelClass, introspectedTable);
    }

    @Override
    public boolean clientInsertMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        // 根据insert参数获取java类型
        GeneratorContext generatorContext = GeneratorContext.getInstance(context);
        try {
            Connection connection = generatorContext.getConnection();
            String databaseType = connection.getMetaData().getDatabaseProductName();
            DATASOURCE_TYPE = databaseType.toUpperCase();
            // oracle生成在insert前
            getIndexInfo(method, interfaze, introspectedTable);
            switch (databaseType.toUpperCase()) {
                case "ORACLE":
                    enablePrimaryKey = false;
                    generateSelectMethod(method, interfaze, introspectedTable);
                    generateUpdateMethod(method, interfaze, introspectedTable);
                    generateDeleteMethod(method, interfaze, introspectedTable);
                    generateInsertMethod(method, interfaze, introspectedTable);
                    break;
                case "MYSQL":
                    if (!introspectedTable.hasPrimaryKeyColumns()) {
                        generateSelectMethod(method, interfaze, introspectedTable);
                        generateUpdateMethod(method, interfaze, introspectedTable);
                        generateDeleteMethod(method, interfaze, introspectedTable);
                    } else {
                        if (!introspectedTable.getTableConfiguration().isSelectByPrimaryKeyStatementEnabled()) {
                            generateSelectMethod(method, interfaze, introspectedTable);
                        }
                        if (!introspectedTable.getTableConfiguration().isUpdateByPrimaryKeyStatementEnabled()) {
                            generateUpdateMethod(method, interfaze, introspectedTable);
                        }
                        if (!introspectedTable.getTableConfiguration().isDeleteByPrimaryKeyStatementEnabled()) {
                            generateDeleteMethod(method, interfaze, introspectedTable);
                        }
                        generateInsertMethod(method, interfaze, introspectedTable);
                    }
                    break;
                default:
                    LOG.error("不支持的数据库类型：{}", databaseType.toUpperCase());
                    return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        // 接口增加注释

        if (interfaze.getJavaDocLines().size() == 0) {
            addInterfazeComment(interfaze, introspectedTable);
        }
        return super.clientInsertMethodGenerated(method, interfaze, introspectedTable);
    }

    @Override
    public boolean clientSelectByPrimaryKeyMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        if (introspectedTable.hasPrimaryKeyColumns()) {
            getIndexInfo(method, interfaze, introspectedTable);
            generateSelectMethod(method, interfaze, introspectedTable);
        }
        return super.clientSelectByPrimaryKeyMethodGenerated(method, interfaze, introspectedTable);
    }

    @Override
    public boolean clientUpdateByPrimaryKeySelectiveMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        if (introspectedTable.hasPrimaryKeyColumns()) {
            getIndexInfo(method, interfaze, introspectedTable);
            generateUpdateMethod(method, interfaze, introspectedTable);
        }
        return super.clientUpdateByPrimaryKeySelectiveMethodGenerated(method, interfaze, introspectedTable);
    }

    @Override
    public boolean clientDeleteByPrimaryKeyMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        if (introspectedTable.hasPrimaryKeyColumns()) {
            getIndexInfo(method, interfaze, introspectedTable);
            generateDeleteMethod(method, interfaze, introspectedTable);
        }
        return super.clientDeleteByPrimaryKeyMethodGenerated(method, interfaze, introspectedTable);
    }

    @Override
    public boolean sqlMapBaseColumnListElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        // 清空上一个表的内容
        xmlElements.clear();
        return super.sqlMapBaseColumnListElementGenerated(element, introspectedTable);
    }

    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {
        this.document = document;
        for (XmlElement element : xmlElements) {
            document.getRootElement().addElement(element);
        }
        return super.sqlMapDocumentGenerated(document, introspectedTable);
    }

    @Override
    public boolean sqlMapInsertElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        // 根据insert参数获取java类型
        GeneratorContext generatorContext = GeneratorContext.getInstance(context);
        try {
            Connection connection = generatorContext.getConnection();
            String databaseType = connection.getMetaData().getDatabaseProductName();
            DATASOURCE_TYPE = databaseType.toUpperCase();
            switch (databaseType.toUpperCase()) {
                case "ORACLE":
                    enablePrimaryKey = false;
                    generateSelectElement(element, introspectedTable);
                    generateUpdateElement(element, introspectedTable);
                    generateDeleteElement(element, introspectedTable);
                    generateInsertElement(element, introspectedTable);
                    break;
                case "MYSQL":
                    if (!introspectedTable.hasPrimaryKeyColumns()) {
                        generateSelectElement(element, introspectedTable);
                        generateUpdateElement(element, introspectedTable);
                        generateDeleteElement(element, introspectedTable);
                    } else {
                        if (!introspectedTable.getTableConfiguration().isSelectByPrimaryKeyStatementEnabled()) {
                            generateSelectElement(element, introspectedTable);
                        }
                        if (!introspectedTable.getTableConfiguration().isUpdateByPrimaryKeyStatementEnabled()) {
                            generateUpdateElement(element, introspectedTable);
                        }
                        if (!introspectedTable.getTableConfiguration().isDeleteByPrimaryKeyStatementEnabled()) {
                            generateDeleteElement(element, introspectedTable);
                        }
                        generateInsertElement(element, introspectedTable);
                    }
                    break;
                default:
                    LOG.error("不支持的数据库类型：{}", databaseType.toUpperCase());
                    return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return super.sqlMapInsertElementGenerated(element, introspectedTable);
    }

    @Override
    public boolean sqlMapSelectByPrimaryKeyElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        if (introspectedTable.hasPrimaryKeyColumns()) {
            generateSelectElement(element, introspectedTable);
        }
        return super.sqlMapSelectByPrimaryKeyElementGenerated(element, introspectedTable);
    }

    @Override
    public boolean sqlMapUpdateByPrimaryKeySelectiveElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        if (introspectedTable.hasPrimaryKeyColumns()) {
            // generateUpdateElement(element, introspectedTable);
        }
        return super.sqlMapUpdateByPrimaryKeySelectiveElementGenerated(element, introspectedTable);
    }

    @Override
    public boolean sqlMapUpdateByPrimaryKeyWithBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        if (introspectedTable.hasPrimaryKeyColumns()) {
            generateUpdateElement(element, introspectedTable);
        }
        return super.sqlMapUpdateByPrimaryKeyWithBLOBsElementGenerated(element, introspectedTable);
    }

    @Override
    public boolean sqlMapUpdateByPrimaryKeyWithoutBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        if (introspectedTable.hasPrimaryKeyColumns()) {
            generateUpdateElement(element, introspectedTable);
        }
        return super.sqlMapUpdateByPrimaryKeyWithoutBLOBsElementGenerated(element, introspectedTable);
    }

    @Override
    public boolean sqlMapDeleteByPrimaryKeyElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        if (introspectedTable.hasPrimaryKeyColumns()) {
            generateDeleteElement(element, introspectedTable);
        }
        return super.sqlMapDeleteByPrimaryKeyElementGenerated(element, introspectedTable);
    }

    /**
     * 获取索引信息
     *
     * @param method
     * @param interfaze
     * @param introspectedTable
     */
    private void getIndexInfo(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        GeneratorContext generatorContext = GeneratorContext.getInstance(context);
        boolean unique = true;
        switch (indexScope) {
            case "unique":
                unique = true;
                break;
            default:
                unique = false;
                break;
        }
        try {
            Connection connection = generatorContext.getConnection();
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            String tableName = introspectedTable.getTableConfiguration().getTableName();
            // 获取主键
            getPrimaryKey(databaseMetaData, introspectedTable);
            ResultSet resultSet = databaseMetaData.getIndexInfo(introspectedTable.getTableConfiguration().getCatalog(),
                    introspectedTable.getTableConfiguration().getSchema(),
                    tableName, unique, false);
            TableInfo tableInfo = null;
            if (tblMap.containsKey(tableName)) {
                tableInfo = tblMap.get(tableName);
            } else {
                tableInfo = new TableInfo();
            }
            tableInfo.setTblName(tableName);
            while (resultSet.next()) {
                String columnName = resultSet.getString("COLUMN_NAME");
                String nonUnique = resultSet.getString("NON_UNIQUE");
                String indexName = resultSet.getString("INDEX_NAME");
                if (null == indexName) {
                    continue;
                }

                IndexInfo indexInfo = null;
                if (primaryKeys.containsKey(tableName)) {
                    // 主键
                    if (primaryKeys.get(tableName).contains(indexName)) {
                        indexInfo = tableInfo.getIndexInfo(indexName, tableInfo.getPrimaryKeyList());
                        if (null == indexInfo) {
                            indexInfo = getIndexInfo(tableName, columnName, nonUnique, indexName, introspectedTable);
                            tableInfo.getPrimaryKeyList().add(indexInfo);
                        } else {
                            indexInfo.getIntrospectedColumns().add(introspectedTable.getColumn(columnName).get());
                            indexInfo.getColumnList().add(columnName);
                        }
                    } else {
                        if ("0".equals(nonUnique)) {
                            indexInfo = tableInfo.getIndexInfo(indexName, tableInfo.getUniqueIndexList());
                            if (null == indexInfo) {
                                indexInfo = getIndexInfo(tableName, columnName, nonUnique, indexName, introspectedTable);
                                tableInfo.getUniqueIndexList().add(indexInfo);
                            } else {
                                indexInfo.getIntrospectedColumns().add(introspectedTable.getColumn(columnName).get());
                                indexInfo.getColumnList().add(columnName);
                            }
                        }
                    }
                } else {
                    if ("0".equals(nonUnique)) {
                        indexInfo = tableInfo.getIndexInfo(indexName, tableInfo.getUniqueIndexList());
                        if (null == indexInfo) {
                            indexInfo = getIndexInfo(tableName, columnName, nonUnique, indexName, introspectedTable);
                            tableInfo.getUniqueIndexList().add(indexInfo);
                        } else {
                            indexInfo.getIntrospectedColumns().add(introspectedTable.getColumn(columnName).get());
                            indexInfo.getColumnList().add(columnName);
                        }
                    }
                }
                // 非唯一索引
                if ("1".equals(nonUnique)) {
                    indexInfo = tableInfo.getIndexInfo(indexName, tableInfo.getNonUniqueIndexList());
                    if (null == indexInfo) {
                        indexInfo = getIndexInfo(tableName, columnName, nonUnique, indexName, introspectedTable);
                        tableInfo.getNonUniqueIndexList().add(indexInfo);
                    } else {
                        indexInfo.getIntrospectedColumns().add(introspectedTable.getColumn(columnName).get());
                        indexInfo.getColumnList().add(columnName);
                    }
                }
            }
            if (null != tableInfo) {
                tblMap.put(tableName, tableInfo);
                LOG.debug(tableInfo.toString());
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private IndexInfo getIndexInfo(String tableName, String columnName, String nonUnique, String indexName, IntrospectedTable
            introspectedTable) {
        IndexInfo indexInfo = new IndexInfo();
        indexInfo.setTblName(tableName);
        indexInfo.getColumnList().add(columnName);
        indexInfo.setIdxName(indexName);
        Optional<IntrospectedColumn> column = introspectedTable.getColumn(columnName);
        LOG.debug(columnName);
        indexInfo.getIntrospectedColumns().add(column.get());
        switch (nonUnique) {
            case "0":
                //唯一
                indexInfo.setUnique(true);
                break;
            case "1":
                indexInfo.setUnique(false);
                break;
            default:
                LOG.error("未知索引类型：{}", nonUnique);
                break;
        }
        return indexInfo;
    }

    /**
     * 获取主键
     *
     * @param databaseMetaData
     * @param introspectedTable
     * @throws SQLException
     */
    protected void getPrimaryKey(DatabaseMetaData databaseMetaData, IntrospectedTable introspectedTable) throws SQLException {
        String tableName = introspectedTable.getTableConfiguration().getTableName();
        ResultSet primaryResultSet =
                databaseMetaData.getPrimaryKeys(introspectedTable.getTableConfiguration().getCatalog(),
                        introspectedTable.getTableConfiguration().getSchema(),
                        tableName);
        while (primaryResultSet.next()) {
            String indexName = primaryResultSet.getString("PK_NAME");
            if (primaryKeys.containsKey(tableName)) {
                primaryKeys.get(tableName).add(indexName);
            } else {
                List<String> list = new ArrayList<>();
                list.add(indexName);
                primaryKeys.put(tableName, list);
            }

        }
    }

    /**
     * 检查生成何种代码
     *
     * @param unique
     * @return
     */
    protected boolean check(boolean unique) {
        switch (indexScope) {
            case "all":
                return true;
            case "unique":
                return unique;
            case "non-unique":
                return !unique;
            default:
                return false;
        }
    }

    /**
     * 获取方法名
     *
     * @param indexName
     * @param prefix
     * @return
     */
    protected String getMethodNameByIndexName(String indexName, String prefix) {
        String humpIndex = GeneralUtil.underLineToHump(indexName);
        humpIndex = humpIndex.substring(0, 1).toUpperCase() + humpIndex.substring(1);
        return String.format("%sBy%s", prefix, humpIndex);
    }

    /**
     * 生成查询方法
     *
     * @param method
     * @param interfaze
     * @param introspectedTable
     */
    private void generateSelectMethod(Method method, Interface interfaze, IntrospectedTable introspectedTable) {

        // 查询
        TableInfo tableInfo = tblMap.get(introspectedTable.getTableConfiguration().getTableName());
        if (enableSelect && enableUniqueSelect && check(true)) {
            if (enablePrimaryKey) {
                for (IndexInfo indexInfo : tableInfo.getPrimaryKeyList()) {
                    generateSelectMethodByIndex(method, interfaze, introspectedTable, indexInfo,
                            StatementType.NOLOCK_RECORD_A);
                    generateSelectMethodByIndex(method, interfaze, introspectedTable, indexInfo,
                            StatementType.NOLOCK_RECORD_0);
                    generateSelectMethodByIndex(method, interfaze, introspectedTable, indexInfo,
                            StatementType.NOLOCK_RECORD_1);
                    if (enableForUpdate) {
                        generateSelectMethodByIndex(method, interfaze, introspectedTable, indexInfo,
                                StatementType.LOCK_RECORD_A);
                        generateSelectMethodByIndex(method, interfaze, introspectedTable, indexInfo,
                                StatementType.LOCK_RECORD_0);
                        generateSelectMethodByIndex(method, interfaze, introspectedTable, indexInfo,
                                StatementType.LOCK_RECORD_1);
                    }
                }
            }
            for (IndexInfo indexInfo : tableInfo.getUniqueIndexList()) {
                generateSelectMethodByIndex(method, interfaze, introspectedTable, indexInfo,
                        StatementType.NOLOCK_RECORD_A);
                generateSelectMethodByIndex(method, interfaze, introspectedTable, indexInfo,
                        StatementType.NOLOCK_RECORD_0);
                generateSelectMethodByIndex(method, interfaze, introspectedTable, indexInfo,
                        StatementType.NOLOCK_RECORD_1);
                if (enableForUpdate) {
                    generateSelectMethodByIndex(method, interfaze, introspectedTable, indexInfo,
                            StatementType.LOCK_RECORD_A);
                    generateSelectMethodByIndex(method, interfaze, introspectedTable, indexInfo,
                            StatementType.LOCK_RECORD_0);
                    generateSelectMethodByIndex(method, interfaze, introspectedTable, indexInfo,
                            StatementType.LOCK_RECORD_1);
                }
            }

        }
        if (enableSelect && enableNonUniqueSelect && check(false)) {
            for (IndexInfo indexInfo : tableInfo.getNonUniqueIndexList()) {
                generateSelectMethodByIndex(method, interfaze, introspectedTable, indexInfo,
                        StatementType.NOLOCK_RECORD_A);
                generateSelectMethodByIndex(method, interfaze, introspectedTable, indexInfo,
                        StatementType.NOLOCK_RECORD_0);
                generateSelectMethodByIndex(method, interfaze, introspectedTable, indexInfo,
                        StatementType.NOLOCK_RECORD_1);
                if (enableForUpdate) {
                    generateSelectMethodByIndex(method, interfaze, introspectedTable, indexInfo,
                            StatementType.LOCK_RECORD_A);
                    generateSelectMethodByIndex(method, interfaze, introspectedTable, indexInfo,
                            StatementType.LOCK_RECORD_0);
                    generateSelectMethodByIndex(method, interfaze, introspectedTable, indexInfo,
                            StatementType.LOCK_RECORD_1);
                }
            }
        }
    }

    /**
     * 生成更新方法
     *
     * @param method
     * @param interfaze
     * @param introspectedTable
     */
    private void generateUpdateMethod(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        // 更新
        TableInfo tableInfo = tblMap.get(introspectedTable.getTableConfiguration().getTableName());
        if (enableUpdate && enablePrimaryKey && check(true)) {
            if (enableSingleUpdate) {
                for (IndexInfo indexInfo : tableInfo.getPrimaryKeyList()) {
                    generateSingleUpdateMethod(method, interfaze, introspectedTable, indexInfo,
                            StatementType.RECORD_SINGLE_A);
                    generateSingleUpdateMethod(method, interfaze, introspectedTable, indexInfo,
                            StatementType.RECORD_SINGLE_0);
                    generateSingleUpdateMethod(method, interfaze, introspectedTable, indexInfo,
                            StatementType.RECORD_SINGLE_1);
                }
            }
            if (enableBatchUpdate) {
                for (IndexInfo indexInfo : tableInfo.getPrimaryKeyList()) {
                    generateBatchUpdateMethod(method, interfaze, introspectedTable, indexInfo,
                            StatementType.RECORD_MULTI_A);
                    generateBatchUpdateMethod(method, interfaze, introspectedTable, indexInfo,
                            StatementType.RECORD_MULTI_0);
                    generateBatchUpdateMethod(method, interfaze, introspectedTable, indexInfo,
                            StatementType.RECORD_MULTI_1);
                }
            }
        }
        if (enableUpdate && enableSingleUpdate && check(true)) {
            for (IndexInfo indexInfo : tableInfo.getUniqueIndexList()) {
                generateSingleUpdateMethod(method, interfaze, introspectedTable, indexInfo,
                        StatementType.RECORD_SINGLE_A);
                generateSingleUpdateMethod(method, interfaze, introspectedTable, indexInfo,
                        StatementType.RECORD_SINGLE_0);
                generateSingleUpdateMethod(method, interfaze, introspectedTable, indexInfo,
                        StatementType.RECORD_SINGLE_1);
            }
        }
        if (enableUpdate && enableBatchUpdate && check(true)) {
            for (IndexInfo indexInfo : tableInfo.getUniqueIndexList()) {
                generateBatchUpdateMethod(method, interfaze, introspectedTable, indexInfo,
                        StatementType.RECORD_MULTI_A);
                generateBatchUpdateMethod(method, interfaze, introspectedTable, indexInfo,
                        StatementType.RECORD_MULTI_0);
                generateBatchUpdateMethod(method, interfaze, introspectedTable, indexInfo,
                        StatementType.RECORD_MULTI_1);
            }
        }
    }

    /**
     * 生成删除方法
     *
     * @param method
     * @param interfaze
     * @param introspectedTable
     */
    private void generateDeleteMethod(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        //删除
        TableInfo tableInfo = tblMap.get(introspectedTable.getTableConfiguration().getTableName());
        if (enableDelete && enablePrimaryKey && check(true)) {
            if (enableSingleDelete) {
                for (IndexInfo indexInfo : tableInfo.getPrimaryKeyList()) {
                    generateSingleDeleteMethod(method, interfaze, introspectedTable, indexInfo,
                            StatementType.RECORD_SINGLE_A);
                    generateSingleDeleteMethod(method, interfaze, introspectedTable, indexInfo,
                            StatementType.RECORD_SINGLE_0);
                    generateSingleDeleteMethod(method, interfaze, introspectedTable, indexInfo,
                            StatementType.RECORD_SINGLE_1);
                }
            }
            if (enableBatchDelete) {
                for (IndexInfo indexInfo : tableInfo.getPrimaryKeyList()) {
                    generateBatchDeleteMethod(method, interfaze, introspectedTable, indexInfo,
                            StatementType.RECORD_MULTI_A);
                    generateBatchDeleteMethod(method, interfaze, introspectedTable, indexInfo,
                            StatementType.RECORD_MULTI_0);
                    generateBatchDeleteMethod(method, interfaze, introspectedTable, indexInfo,
                            StatementType.RECORD_MULTI_1);
                }
            }
        }
        if (enableDelete && enableSingleDelete && check(true)) {
            for (IndexInfo indexInfo : tableInfo.getUniqueIndexList()) {
                generateSingleDeleteMethod(method, interfaze, introspectedTable, indexInfo,
                        StatementType.RECORD_SINGLE_A);
                generateSingleDeleteMethod(method, interfaze, introspectedTable, indexInfo,
                        StatementType.RECORD_SINGLE_0);
                generateSingleDeleteMethod(method, interfaze, introspectedTable, indexInfo,
                        StatementType.RECORD_SINGLE_1);
            }
        }
        if (enableDelete && enableBatchDelete && check(true)) {
            for (IndexInfo indexInfo : tableInfo.getUniqueIndexList()) {
                generateBatchDeleteMethod(method, interfaze, introspectedTable, indexInfo,
                        StatementType.RECORD_MULTI_A);
                generateBatchDeleteMethod(method, interfaze, introspectedTable, indexInfo,
                        StatementType.RECORD_MULTI_0);
                generateBatchDeleteMethod(method, interfaze, introspectedTable, indexInfo,
                        StatementType.RECORD_MULTI_1);
            }
        }
        if (enableDelete && enablePrimaryKey && enableUniqueIndexDelete && check(true)) {
            for (IndexInfo indexInfo : tableInfo.getPrimaryKeyList()) {
                generateDeleteMethodByIndex(method, interfaze, introspectedTable, indexInfo,
                        StatementType.RECORD_SINGLE_A);
                generateDeleteMethodByIndex(method, interfaze, introspectedTable, indexInfo,
                        StatementType.RECORD_SINGLE_0);
                generateDeleteMethodByIndex(method, interfaze, introspectedTable, indexInfo,
                        StatementType.RECORD_SINGLE_1);
            }
        }
        if (enableDelete && enableUniqueIndexDelete && check(true)) {
            for (IndexInfo indexInfo : tableInfo.getUniqueIndexList()) {
                generateDeleteMethodByIndex(method, interfaze, introspectedTable, indexInfo,
                        StatementType.RECORD_SINGLE_A);
                generateDeleteMethodByIndex(method, interfaze, introspectedTable, indexInfo,
                        StatementType.RECORD_SINGLE_0);
                generateDeleteMethodByIndex(method, interfaze, introspectedTable, indexInfo,
                        StatementType.RECORD_SINGLE_1);
            }
        }
        if (enableDelete && enableNonUniqueIndexDelete && check(false)) {
            for (IndexInfo indexInfo : tableInfo.getNonUniqueIndexList()) {
                generateDeleteMethodByIndex(method, interfaze, introspectedTable, indexInfo,
                        StatementType.RECORD_MULTI_A);
                generateDeleteMethodByIndex(method, interfaze, introspectedTable, indexInfo,
                        StatementType.RECORD_MULTI_0);
                generateDeleteMethodByIndex(method, interfaze, introspectedTable, indexInfo,
                        StatementType.RECORD_MULTI_1);
            }
        }
    }

    /**
     * 生成新增方法
     *
     * @param method
     * @param interfaze
     * @param introspectedTable
     */
    private void generateInsertMethod(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        // 新增
        if (enableInsert) {
            generateInsertMethod(method, interfaze, introspectedTable, StatementType.RECORD_SINGLE_A);
            //generateInsertSelectiveMethod(method, interfaze, introspectedTable, StatementType.RECORD_SINGLE_A);
        }
        if (enableBatchInsert) {
            generateBatchInsertMethod(method, interfaze, introspectedTable, StatementType.RECORD_MULTI_A);
        }
    }


    /**
     * 生成查询方法
     *
     * @param element
     * @param introspectedTable
     * @param introspectedTable
     */
    private void generateSelectElement(XmlElement element, IntrospectedTable introspectedTable) {

        // 查询
        TableInfo tableInfo = tblMap.get(introspectedTable.getTableConfiguration().getTableName());
        if (enableSelect && enableUniqueSelect && check(true)) {
            if (enablePrimaryKey) {
                for (IndexInfo indexInfo : tableInfo.getPrimaryKeyList()) {
                    generateSelectElementByIndex(element, introspectedTable, indexInfo,
                            StatementType.NOLOCK_RECORD_A);
                    generateSelectElementByIndex(element, introspectedTable, indexInfo,
                            StatementType.NOLOCK_RECORD_0);
                    generateSelectElementByIndex(element, introspectedTable, indexInfo,
                            StatementType.NOLOCK_RECORD_1);
                    if (enableForUpdate) {
                        generateSelectElementByIndex(element, introspectedTable, indexInfo,
                                StatementType.LOCK_RECORD_A);
                        generateSelectElementByIndex(element, introspectedTable, indexInfo,
                                StatementType.LOCK_RECORD_0);
                        generateSelectElementByIndex(element, introspectedTable, indexInfo,
                                StatementType.LOCK_RECORD_1);
                    }
                }
            }
            for (IndexInfo indexInfo : tableInfo.getUniqueIndexList()) {
                generateSelectElementByIndex(element, introspectedTable, indexInfo,
                        StatementType.NOLOCK_RECORD_A);
                generateSelectElementByIndex(element, introspectedTable, indexInfo,
                        StatementType.NOLOCK_RECORD_0);
                generateSelectElementByIndex(element, introspectedTable, indexInfo,
                        StatementType.NOLOCK_RECORD_1);
                if (enableForUpdate) {
                    generateSelectElementByIndex(element, introspectedTable, indexInfo,
                            StatementType.LOCK_RECORD_A);
                    generateSelectElementByIndex(element, introspectedTable, indexInfo,
                            StatementType.LOCK_RECORD_0);
                    generateSelectElementByIndex(element, introspectedTable, indexInfo,
                            StatementType.LOCK_RECORD_1);
                }
            }

        }
        if (enableSelect && enableNonUniqueSelect && check(false)) {
            for (IndexInfo indexInfo : tableInfo.getNonUniqueIndexList()) {
                generateSelectElementByIndex(element, introspectedTable, indexInfo,
                        StatementType.NOLOCK_RECORD_A);
                generateSelectElementByIndex(element, introspectedTable, indexInfo,
                        StatementType.NOLOCK_RECORD_0);
                generateSelectElementByIndex(element, introspectedTable, indexInfo,
                        StatementType.NOLOCK_RECORD_1);
                if (enableForUpdate) {
                    generateSelectElementByIndex(element, introspectedTable, indexInfo,
                            StatementType.LOCK_RECORD_A);
                    generateSelectElementByIndex(element, introspectedTable, indexInfo,
                            StatementType.LOCK_RECORD_0);
                    generateSelectElementByIndex(element, introspectedTable, indexInfo,
                            StatementType.LOCK_RECORD_1);
                }
            }
        }
    }

    /**
     * 生成更新方法
     *
     * @param element
     * @param introspectedTable
     * @param introspectedTable
     */
    private void generateUpdateElement(XmlElement element, IntrospectedTable introspectedTable) {
        // 更新
        TableInfo tableInfo = tblMap.get(introspectedTable.getTableConfiguration().getTableName());
        if (enableUpdate && enablePrimaryKey && check(true)) {
            if (enableSingleUpdate) {
                for (IndexInfo indexInfo : tableInfo.getPrimaryKeyList()) {
                    generateSingleUpdateElement(element, introspectedTable, indexInfo,
                            StatementType.RECORD_SINGLE_A);
                    generateSingleUpdateElement(element, introspectedTable, indexInfo,
                            StatementType.RECORD_SINGLE_0);
                    generateSingleUpdateElement(element, introspectedTable, indexInfo,
                            StatementType.RECORD_SINGLE_1);
                }
            }
            if (enableBatchUpdate) {
                for (IndexInfo indexInfo : tableInfo.getPrimaryKeyList()) {
                    generateBatchUpdateElement(element, introspectedTable, indexInfo,
                            StatementType.RECORD_MULTI_A);
                    generateBatchUpdateElement(element, introspectedTable, indexInfo,
                            StatementType.RECORD_MULTI_0);
                    generateBatchUpdateElement(element, introspectedTable, indexInfo,
                            StatementType.RECORD_MULTI_1);
                }
            }
        }
        if (enableUpdate && enableSingleUpdate && check(true)) {
            for (IndexInfo indexInfo : tableInfo.getUniqueIndexList()) {
                generateSingleUpdateElement(element, introspectedTable, indexInfo,
                        StatementType.RECORD_SINGLE_A);
                generateSingleUpdateElement(element, introspectedTable, indexInfo,
                        StatementType.RECORD_SINGLE_0);
                generateSingleUpdateElement(element, introspectedTable, indexInfo,
                        StatementType.RECORD_SINGLE_1);
            }
        }
        if (enableUpdate && enableBatchUpdate && check(true)) {
            for (IndexInfo indexInfo : tableInfo.getUniqueIndexList()) {
                generateBatchUpdateElement(element, introspectedTable, indexInfo,
                        StatementType.RECORD_MULTI_A);
                generateBatchUpdateElement(element, introspectedTable, indexInfo,
                        StatementType.RECORD_MULTI_0);
                generateBatchUpdateElement(element, introspectedTable, indexInfo,
                        StatementType.RECORD_MULTI_1);
            }
        }
    }

    /**
     * 生成删除方法
     *
     * @param element
     * @param introspectedTable
     * @param introspectedTable
     */
    private void generateDeleteElement(XmlElement element, IntrospectedTable introspectedTable) {
        //删除
        TableInfo tableInfo = tblMap.get(introspectedTable.getTableConfiguration().getTableName());
        if (enableDelete && enablePrimaryKey && check(true)) {
            if (enableSingleDelete) {
                for (IndexInfo indexInfo : tableInfo.getPrimaryKeyList()) {
                    generateSingleDeleteElement(element, introspectedTable, indexInfo,
                            StatementType.RECORD_SINGLE_A);
                    generateSingleDeleteElement(element, introspectedTable, indexInfo,
                            StatementType.RECORD_SINGLE_0);
                    generateSingleDeleteElement(element, introspectedTable, indexInfo,
                            StatementType.RECORD_SINGLE_1);
                }
            }
            if (enableBatchDelete) {
                for (IndexInfo indexInfo : tableInfo.getPrimaryKeyList()) {
                    generateBatchDeleteElement(element, introspectedTable, indexInfo,
                            StatementType.RECORD_MULTI_A);
                    generateBatchDeleteElement(element, introspectedTable, indexInfo,
                            StatementType.RECORD_MULTI_0);
                    generateBatchDeleteElement(element, introspectedTable, indexInfo,
                            StatementType.RECORD_MULTI_1);
                }
            }
        }
        if (enableDelete && enableSingleDelete && check(true)) {
            for (IndexInfo indexInfo : tableInfo.getUniqueIndexList()) {
                generateSingleDeleteElement(element, introspectedTable, indexInfo,
                        StatementType.RECORD_SINGLE_A);
                generateSingleDeleteElement(element, introspectedTable, indexInfo,
                        StatementType.RECORD_SINGLE_0);
                generateSingleDeleteElement(element, introspectedTable, indexInfo,
                        StatementType.RECORD_SINGLE_1);
            }
        }
        if (enableDelete && enableBatchDelete && check(true)) {
            for (IndexInfo indexInfo : tableInfo.getUniqueIndexList()) {
                generateBatchDeleteElement(element, introspectedTable, indexInfo,
                        StatementType.RECORD_MULTI_A);
                generateBatchDeleteElement(element, introspectedTable, indexInfo,
                        StatementType.RECORD_MULTI_0);
                generateBatchDeleteElement(element, introspectedTable, indexInfo,
                        StatementType.RECORD_MULTI_1);
            }
        }
        if (enableDelete && enablePrimaryKey && enableUniqueIndexDelete && check(true)) {
            for (IndexInfo indexInfo : tableInfo.getPrimaryKeyList()) {
                generateDeleteElementByIndex(element, introspectedTable, indexInfo,
                        StatementType.RECORD_SINGLE_A);
                generateDeleteElementByIndex(element, introspectedTable, indexInfo,
                        StatementType.RECORD_SINGLE_0);
                generateDeleteElementByIndex(element, introspectedTable, indexInfo,
                        StatementType.RECORD_SINGLE_1);
            }
        }
        if (enableDelete && enableUniqueIndexDelete && check(true)) {
            for (IndexInfo indexInfo : tableInfo.getUniqueIndexList()) {
                generateDeleteElementByIndex(element, introspectedTable, indexInfo,
                        StatementType.RECORD_SINGLE_A);
                generateDeleteElementByIndex(element, introspectedTable, indexInfo,
                        StatementType.RECORD_SINGLE_0);
                generateDeleteElementByIndex(element, introspectedTable, indexInfo,
                        StatementType.RECORD_SINGLE_1);
            }
        }
        if (enableDelete && enableNonUniqueIndexDelete && check(false)) {

            for (IndexInfo indexInfo : tableInfo.getNonUniqueIndexList()) {
                generateDeleteElementByIndex(element, introspectedTable, indexInfo,
                        StatementType.RECORD_MULTI_A);
                generateDeleteElementByIndex(element, introspectedTable, indexInfo,
                        StatementType.RECORD_MULTI_0);
                generateDeleteElementByIndex(element, introspectedTable, indexInfo,
                        StatementType.RECORD_MULTI_1);
            }
        }
    }

    /**
     * 生成新增方法
     *
     * @param element
     * @param introspectedTable
     */
    private void generateInsertElement(XmlElement element, IntrospectedTable introspectedTable) {
        // 新增
        if (enableInsert) {
            generateInsertElement(element, introspectedTable, StatementType.RECORD_SINGLE_A);
            //generateInsertSelectiveMethod(method, interfaze, introspectedTable, StatementType.RECORD_SINGLE_A);
        }
        if (enableBatchInsert) {
            generateBatchInsertElement(element, introspectedTable, StatementType.RECORD_MULTI_A);
        }
    }

    @Override
    public void generateSelectMethodByIndex(Method method, Interface interfaze, IntrospectedTable introspectedTable, IndexInfo indexInfo, StatementType type) {

    }

    @Override
    public void generateSingleUpdateMethod(Method method, Interface interfaze, IntrospectedTable introspectedTable, IndexInfo indexInfo, StatementType type) {

    }

    @Override
    public void generateBatchUpdateMethod(Method method, Interface interfaze, IntrospectedTable introspectedTable, IndexInfo indexInfo, StatementType type) {

    }

    @Override
    public void generateSingleUpdateSelectiveMethod(Method method, Interface interfaze, IntrospectedTable introspectedTable, IndexInfo indexInfo, StatementType type) {

    }

    @Override
    public void generateBatchUpdateSelectiveMethod(Method method, Interface interfaze, IntrospectedTable introspectedTable, IndexInfo indexInfo, StatementType type) {

    }

    @Override
    public void generateDeleteMethodByIndex(Method method, Interface interfaze, IntrospectedTable introspectedTable, IndexInfo indexInfo, StatementType type) {

    }

    @Override
    public void generateSingleDeleteMethod(Method method, Interface interfaze, IntrospectedTable introspectedTable, IndexInfo indexInfo, StatementType type) {

    }

    @Override
    public void generateBatchDeleteMethod(Method method, Interface interfaze, IntrospectedTable introspectedTable, IndexInfo indexInfo, StatementType type) {

    }

    @Override
    public void generateInsertMethod(Method method, Interface interfaze, IntrospectedTable introspectedTable, StatementType type) {

    }

    @Override
    public void generateInsertSelectiveMethod(Method method, Interface interfaze, IntrospectedTable introspectedTable, IndexInfo indexInfo, StatementType type) {

    }

    @Override
    public void generateBatchInsertMethod(Method method, Interface interfaze, IntrospectedTable introspectedTable, StatementType type) {

    }

    @Override
    public void generateSelectElementByIndex(XmlElement element, IntrospectedTable introspectedTable, IndexInfo indexInfo, StatementType type) {

    }

    @Override
    public void generateSingleUpdateElement(XmlElement element, IntrospectedTable introspectedTable, IndexInfo indexInfo, StatementType type) {

    }

    @Override
    public void generateBatchUpdateElement(XmlElement element, IntrospectedTable introspectedTable, IndexInfo indexInfo, StatementType type) {

    }

    @Override
    public void generateSingleUpdateSelectiveElement(XmlElement element, IntrospectedTable introspectedTable, IndexInfo indexInfo, StatementType type) {

    }

    @Override
    public void generateBatchUpdateSelectiveElement(XmlElement element, IntrospectedTable introspectedTable, IndexInfo indexInfo, StatementType type) {

    }

    @Override
    public void generateDeleteElementByIndex(XmlElement element, IntrospectedTable introspectedTable, IndexInfo indexInfo, StatementType type) {

    }

    @Override
    public void generateSingleDeleteElement(XmlElement element, IntrospectedTable introspectedTable, IndexInfo indexInfo, StatementType type) {

    }

    @Override
    public void generateBatchDeleteElement(XmlElement element, IntrospectedTable introspectedTable, IndexInfo indexInfo, StatementType type) {

    }

    @Override
    public void generateInsertElement(XmlElement element, IntrospectedTable introspectedTable, StatementType type) {

    }

    @Override
    public void generateInsertSelectiveElement(XmlElement element, IntrospectedTable introspectedTable, IndexInfo indexInfo, StatementType type) {

    }

    @Override
    public void generateBatchInsertElement(XmlElement element, IntrospectedTable introspectedTable, StatementType type) {

    }

    /**
     * Mapper接口注释添加
     *
     * @param interfaze
     * @param introspectedTable
     */
    protected void addInterfazeComment(Interface interfaze, IntrospectedTable introspectedTable) {
        interfaze.addJavaDocLine("/**");
        interfaze.addJavaDocLine(" * Mapper接口");
        StringBuilder sb = new StringBuilder();
        sb.append(" * 表：");
        sb.append(introspectedTable.getTableConfiguration().getTableName());
        sb.append(" ");
        sb.append(introspectedTable.getRemarks());
        sb.append(".");
        interfaze.addJavaDocLine(sb.toString());
        interfaze.addJavaDocLine(" */");
    }

    /**
     * Mapper注释添加
     *
     * @param method
     * @param introspectedTable
     * @param indexInfo
     * @param crud
     * @param type
     */
    protected void addMapperMethodComment(Method method, IntrospectedTable introspectedTable, IndexInfo indexInfo,
                                          DatabaseCRUD crud,
                                          StatementType type) {
        String tableName = introspectedTable.getTableConfiguration().getTableName();
        List<Parameter> parameterList = method.getParameters();
        if (Optional.empty().equals(method.getReturnType()) && parameterList.size() == 0) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        method.addJavaDocLine("/**");
        sb.append(" * ");
        sb.append(crud.desc());
        String returnDesc = "line";
        if (null != indexInfo) {
            if (indexInfo.isUnique()) {
                // 单笔
                if (crud.equals(DatabaseCRUD.RETRIEVE)) {
                    returnDesc = "结果";
                }
            } else {
                // 多笔
                if (crud.equals(DatabaseCRUD.RETRIEVE)) {
                    returnDesc = "结果集";
                }
            }
        }

        sb.append(type.desc());
        sb.append(".");
        method.addJavaDocLine(sb.toString());
        method.addJavaDocLine(" *");
        sb.setLength(0);
        for (Parameter parameter : parameterList) {
            String paraName = GeneralUtil.humpToUnderLine(parameter.getName());
            Optional<IntrospectedColumn> optional = introspectedTable.getColumn(paraName);
            sb.append(" * @param ");
            sb.append(parameter.getName());
            if (optional.isPresent()) {
                sb.append(" ");
                sb.append(tableName);
                sb.append(".");
                sb.append(optional.get().getActualColumnName());
                sb.append(" ");
                sb.append(optional.get().getRemarks());
            } else {
                FullyQualifiedJavaType listType = FullyQualifiedJavaType.getNewListInstance();
                listType.addTypeArgument(javaType);
                if (parameter.getType().equals(javaType)) {
                    // 单笔
                    sb.append(" 实例");
                    sb.append(".");
                } else if (parameter.getType().equals(listType)) {
                    //多笔
                    sb.append(" 实例列表");
                    sb.append(".");
                } else {
                    LOG.warn("未找到列：{}，不生成参数注释", paraName);
                }
            }
            method.addJavaDocLine(sb.toString());
            sb.setLength(0);
        }
        if (!Optional.empty().equals(method.getReturnType())) {
            method.addJavaDocLine(" * @return " + returnDesc);
        }
        method.addJavaDocLine(" */");
    }
}
