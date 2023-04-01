package net.sundayu.code4j.generator.mybatis.plugin;

import net.sundayu.code4j.generator.mybatis.context.GeneratorContext;
import net.sundayu.code4j.generator.mybatis.util.GeneralUtil;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.config.TableConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * @author xiyusu
 * @datetime 2021年09月03 01:05
 * @description insert生成 UseGeneratedKeys 属性
 */
public class GenerateSqlMapUseGeneratedKeysPlugin extends PluginAdapter {
    private static final Logger LOG = LoggerFactory.getLogger(GenerateSqlMapUseGeneratedKeysPlugin.class);

    @Override
    public boolean validate(List<String> list) {
        return true;
    }

    @Override
    public boolean sqlMapInsertElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {

        GeneratorContext generatorContext = GeneratorContext.getInstance(context);
        try {
            DatabaseMetaData databaseMetaData = generatorContext.getConnection().getMetaData();
            String columnName = getAutoIncrementPrimaryKey(databaseMetaData, introspectedTable);
            if (!GeneralUtil.isEmpty(columnName)) {
                element.addAttribute(new Attribute("useGeneratedKeys", "true"));
                element.addAttribute(new Attribute("keyProperty", GeneralUtil.underLineToHump(columnName)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return super.sqlMapInsertElementGenerated(element, introspectedTable);
    }

    @Override
    public boolean sqlMapInsertSelectiveElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        return super.sqlMapInsertSelectiveElementGenerated(element, introspectedTable);
    }


    /**
     * 判断自增主键
     *
     * @param databaseMetaData  database
     * @param introspectedTable 表
     * @return 自增主键
     * @throws SQLException
     */
    private String getAutoIncrementPrimaryKey(DatabaseMetaData databaseMetaData, IntrospectedTable introspectedTable) throws SQLException {
        String primaryKey = "";
        TableConfiguration tableConfiguration = introspectedTable.getTableConfiguration();
        ResultSet primaryResultSet = databaseMetaData.getPrimaryKeys(tableConfiguration.getCatalog(), tableConfiguration.getSchema(), tableConfiguration.getTableName());
        while (primaryResultSet.next()) {
            String columnName = primaryResultSet.getString("COLUMN_NAME");
            Optional<IntrospectedColumn> optional = introspectedTable.getColumn(columnName);
            IntrospectedColumn introspectedColumn = optional.get();
            if (introspectedColumn.isAutoIncrement()) {
                LOG.debug("表：{}，自增主键：{}", tableConfiguration.getTableName(), columnName);
                primaryKey = columnName;
            }
        }
        return primaryKey;
    }

}
