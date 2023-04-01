package net.sundayu.code4j.generator.mybatis.constant;

/**
 * @author xiyusu
 * @datetime 2021年05月24 22:25
 * @description 简述功能
 */
public enum DatabaseCRUD {

    CREATE("create", "创建"),
    INSERT("insert", "新增"),
    RETRIEVE("select", "查询"),
    UPDATE("update", "更新"),
    DELETE("delete", "删除");

    private String keyword;

    private String desc;

    public String get() {
        return keyword;
    }

    public String desc() {
        return desc;
    }

    DatabaseCRUD(String keyword, String desc) {
        this.keyword = keyword;
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "DatabaseCRUD{" +
                "keyword='" + keyword + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }
}
