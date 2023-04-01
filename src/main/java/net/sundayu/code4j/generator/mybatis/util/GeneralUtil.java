package net.sundayu.code4j.generator.mybatis.util;

import org.mybatis.generator.api.PluginAdapter;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author xiyusu
 * @datetime 2021年04月12 00:06
 * @description 简述功能
 */
public class GeneralUtil {

    private static final Logger LOG = LoggerFactory.getLogger(GeneralUtil.class);

    /**
     * 默认扫描路径
     */
    private static final String[] PLUGIN_PACKAGES = {"org.mybatis.generator.plugins", "net.sundayu.mybatis.plugin"};

    /**
     * 默认commentGenerator扫描路径
     */
    private static final String[] COMMENT_PACKAGES = {"org.mybatis.generator.internal", "net.sundayu.mybatis.generator"};

    private GeneralUtil() {

    }

    public static String convertPropertyKey(String key) {
        String[] args = key.split(".");
        StringBuilder stringBuilder = new StringBuilder();
        if (args.length == 1) {
            return key;
        }
        for (int i = 0; i < args.length; i++) {
            String arg = args[i].substring(0, 1).toUpperCase() + args[i].substring(1);
            stringBuilder.append(arg);
        }
        return stringBuilder.toString();
    }

    /**
     * 获取插件
     *
     * @param params
     * @return
     */
    public static List<String> getGeneratorPlugins(Object... params) {
        List<String> pluginNames = new ArrayList<>();
        if (params.length == 0) {
            params = PLUGIN_PACKAGES;
        }
        Reflections reflections = new Reflections(params);
        Set<Class<? extends PluginAdapter>> objectSet = reflections.getSubTypesOf(PluginAdapter.class);
        for (Class<? extends PluginAdapter> plugin : objectSet) {
            pluginNames.add(plugin.getName());
        }
        Collections.sort(pluginNames);
        for (String name : pluginNames) {
            LOG.debug("检测到插件:{}", name);
        }
        return pluginNames;
    }

    public static boolean isEmpty(Object obj) {
        return null == obj;
    }

    /**
     * 驼峰转下划线
     *
     * @param fieldName
     * @return
     */
    public static String humpToUnderLine(String fieldName) {
        if (null == fieldName || "".equals(fieldName)) {
            return fieldName;
        }
        StringBuilder sb = new StringBuilder(fieldName);
        int tmp = 0;
        for (int i = 0; i < fieldName.length(); i++) {
            if (Character.isUpperCase(fieldName.charAt(i))) {
                sb.insert(i + tmp, "_");
                tmp += 1;
            }
        }
        return sb.toString().toUpperCase();
    }

    /**
     * 下划线转驼峰
     *
     * @param fieldName
     * @return
     */
    public static String underLineToHump(String fieldName) {
        if (null == fieldName || "".equals(fieldName)) {
            return fieldName;
        }
        fieldName = fieldName.toLowerCase();
        StringBuilder sb = new StringBuilder();
        boolean nextUperCase = false;
        for (int i = 0; i < fieldName.length(); i++) {
            if ("_".equals(String.valueOf(fieldName.charAt(i)))) {
                nextUperCase = true;
                continue;
            }
            if (nextUperCase) {
                sb.append(String.valueOf(fieldName.charAt(i)).toUpperCase());
                nextUperCase = false;
            } else {
                sb.append(fieldName.charAt(i));
            }
        }
        return sb.toString();
    }

    public static boolean isEmpty(String value) {
        return null == value || "".equals(value);
    }

    /**
     * 获取当前日期
     *
     * @return 当前日期
     */
    public static String getCurrentDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        return simpleDateFormat.format(new Date());
    }

}
