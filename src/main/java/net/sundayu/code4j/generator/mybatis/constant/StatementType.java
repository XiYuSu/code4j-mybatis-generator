package net.sundayu.code4j.generator.mybatis.constant;

/**
 * @author xiyusu
 * @datetime 2021年05月24 22:33
 * @description 简述功能
 */
public enum StatementType {
    // 查询
    NOLOCK_RECORD_A("AnL", "所有记录，无锁"),
    NOLOCK_RECORD_0("0nL", "正常记录，无锁"),
    NOLOCK_RECORD_1("1nL", "失效记录，无锁"),
    LOCK_RECORD_A("AL", "所有记录，有锁"),
    LOCK_RECORD_0("0L", "正常记录，有锁"),
    LOCK_RECORD_1("1L", "失效记录，有锁"),
    // 更新、删除、插入
    RECORD_MULTI_A("MA", "多笔记录"),
    RECORD_MULTI_0("0A", "多笔正常记录"),
    RECORD_MULTI_1("1A", "多笔失效记录"),
    RECORD_SINGLE_A("SA", "单笔记录"),
    RECORD_SINGLE_0("S0", "单笔正常记录"),
    RECORD_SINGLE_1("S1", "单笔失效记录");

    private String keyword;

    private String desc;

    public String get() {
        return keyword;
    }

    public String desc() {
        return desc;
    }

    StatementType(String keyword, String desc) {
        this.keyword = keyword;
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "StatementType{" +
                "keyword='" + keyword + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }
}
