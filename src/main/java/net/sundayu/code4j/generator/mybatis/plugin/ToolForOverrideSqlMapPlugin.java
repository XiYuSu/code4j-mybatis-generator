package net.sundayu.code4j.generator.mybatis.plugin;

import org.mybatis.generator.api.GeneratedXmlFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;

import java.util.List;

/**
 * @author xiyusu
 * @datetime 2021年04月14 23:25
 * @description 重写sqlmap
 */
public class ToolForOverrideSqlMapPlugin extends PluginAdapter {
    @Override
    public boolean validate(List<String> list) {
        return false;
    }

    @Override
    public boolean sqlMapGenerated(GeneratedXmlFile sqlMap, IntrospectedTable introspectedTable) {
        sqlMap.setMergeable(false);
        return super.sqlMapGenerated(sqlMap, introspectedTable);
    }
}
