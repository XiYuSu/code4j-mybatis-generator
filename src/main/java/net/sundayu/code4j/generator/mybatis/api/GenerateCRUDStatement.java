package net.sundayu.code4j.generator.mybatis.api;

import net.sundayu.code4j.generator.mybatis.constant.StatementType;
import net.sundayu.code4j.generator.mybatis.pojo.IndexInfo;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.xml.XmlElement;

/**
 * @author xiyusu
 * @datetime 2021年05月24 23:03
 * @description 简述功能
 */
public interface GenerateCRUDStatement {
    /**
     * 根据索引生成select
     *
     * @param method
     * @param interfaze
     * @param introspectedTable
     */
    void generateSelectMethodByIndex(Method method, Interface interfaze, IntrospectedTable introspectedTable, IndexInfo indexInfo, StatementType type);

    /**
     * 单笔更新
     *
     * @param method
     * @param interfaze
     * @param introspectedTable
     */
    void generateSingleUpdateMethod(Method method, Interface interfaze, IntrospectedTable introspectedTable, IndexInfo indexInfo, StatementType type);

    /**
     * 批量更新
     *
     * @param method
     * @param interfaze
     * @param introspectedTable
     */
    void generateBatchUpdateMethod(Method method, Interface interfaze, IntrospectedTable introspectedTable, IndexInfo indexInfo, StatementType type);

    /**
     * 更新Selective(single)
     *
     * @param method
     * @param interfaze
     * @param introspectedTable
     */
    void generateSingleUpdateSelectiveMethod(Method method, Interface interfaze, IntrospectedTable introspectedTable, IndexInfo indexInfo,
                                             StatementType type);

    /**
     * 更新Selective(bat)
     *
     * @param method
     * @param interfaze
     * @param introspectedTable
     */
    void generateBatchUpdateSelectiveMethod(Method method, Interface interfaze, IntrospectedTable introspectedTable,
                                            IndexInfo indexInfo,
                                            StatementType type);

    /**
     * 根据索引删除
     *
     * @param method
     * @param interfaze
     * @param introspectedTable
     */
    void generateDeleteMethodByIndex(Method method, Interface interfaze, IntrospectedTable introspectedTable, IndexInfo indexInfo, StatementType type);

    /**
     * 单笔删除
     *
     * @param method
     * @param interfaze
     * @param introspectedTable
     */
    void generateSingleDeleteMethod(Method method, Interface interfaze, IntrospectedTable introspectedTable, IndexInfo indexInfo, StatementType type);

    /**
     * 批量删除
     *
     * @param method
     * @param interfaze
     * @param introspectedTable
     */
    void generateBatchDeleteMethod(Method method, Interface interfaze, IntrospectedTable introspectedTable, IndexInfo indexInfo, StatementType type);

    /**
     * 单笔新增
     *
     * @param method
     * @param interfaze
     * @param introspectedTable
     */
    void generateInsertMethod(Method method, Interface interfaze, IntrospectedTable introspectedTable, StatementType type);

    /**
     * 单笔新增Selective
     *
     * @param method
     * @param interfaze
     * @param introspectedTable
     */
    void generateInsertSelectiveMethod(Method method, Interface interfaze, IntrospectedTable introspectedTable, IndexInfo indexInfo, StatementType type);

    /**
     * 批量新增
     *
     * @param method
     * @param interfaze
     * @param introspectedTable
     */
    void generateBatchInsertMethod(Method method, Interface interfaze, IntrospectedTable introspectedTable,
                                   StatementType type);


    /**
     * 根据索引生成select
     *
     * @param element
     * @param introspectedTable
     * @param type
     */
    void generateSelectElementByIndex(XmlElement element, IntrospectedTable introspectedTable, IndexInfo indexInfo, StatementType type);

    /**
     * 单笔更新
     *
     * @param element
     * @param introspectedTable
     * @param type
     */
    void generateSingleUpdateElement(XmlElement element, IntrospectedTable introspectedTable, IndexInfo indexInfo, StatementType type);

    /**
     * 批量更新
     *
     * @param element
     * @param introspectedTable
     * @param type
     */
    void generateBatchUpdateElement(XmlElement element, IntrospectedTable introspectedTable, IndexInfo indexInfo, StatementType type);

    /**
     * 更新Selective(single)
     *
     * @param element
     * @param introspectedTable
     * @param type
     */
    void generateSingleUpdateSelectiveElement(XmlElement element, IntrospectedTable introspectedTable, IndexInfo indexInfo, StatementType type);

    /**
     * 更新Selective(bat)
     *
     * @param element
     * @param introspectedTable
     * @param type
     */
    void generateBatchUpdateSelectiveElement(XmlElement element, IntrospectedTable introspectedTable, IndexInfo indexInfo, StatementType type);

    /**
     * 根据索引删除
     *
     * @param element
     * @param introspectedTable
     * @param type
     */
    void generateDeleteElementByIndex(XmlElement element, IntrospectedTable introspectedTable, IndexInfo indexInfo, StatementType type);

    /**
     * 单笔删除
     *
     * @param element
     * @param introspectedTable
     * @param type
     */
    void generateSingleDeleteElement(XmlElement element, IntrospectedTable introspectedTable, IndexInfo indexInfo, StatementType type);

    /**
     * 批量删除
     *
     * @param element
     * @param introspectedTable
     * @param type
     */
    void generateBatchDeleteElement(XmlElement element, IntrospectedTable introspectedTable, IndexInfo indexInfo, StatementType type);

    /**
     * 单笔新增
     *
     * @param element
     * @param introspectedTable
     * @param type
     */
    void generateInsertElement(XmlElement element, IntrospectedTable introspectedTable, StatementType type);

    /**
     * 单笔新增Selective
     *
     * @param element
     * @param introspectedTable
     * @param type
     */
    void generateInsertSelectiveElement(XmlElement element, IntrospectedTable introspectedTable, IndexInfo indexInfo, StatementType type);

    /**
     * 批量新增
     *
     * @param element
     * @param introspectedTable
     * @param type
     */
    void generateBatchInsertElement(XmlElement element, IntrospectedTable introspectedTable, StatementType type);


}
