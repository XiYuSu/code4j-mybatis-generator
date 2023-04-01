package net.sundayu.code4j.generator.mybatis.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author xiyusu
 * @datetime 2021年04月08 00:44
 * @description 属性加载
 */
public class PropertyLoader {

    private static final Logger LOG = LoggerFactory.getLogger(PropertyLoader.class);

    private static Map<String, Object> dataMap = new HashMap<>();

    static {
        loadConfig("/generator.properties");
    }

    private static void loadConfig(String path) {
        Properties properties = new Properties();
        InputStream in = null;

        try {
            in = PropertyLoader.class.getResourceAsStream(path);
            if (null != in) {
                properties.load(in);
                dataMap = (Map)properties;
            } else {
                LOG.error("配置文件读取异常：{}", path);
            }
        } catch (IOException var12) {
            LOG.error("配置文件读取异常：{}", path);
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException var11) {
                    LOG.error(var11.getMessage());
                }
            }

        }

    }

    public static Map<String, Object> getData(){
        return dataMap;
    }

    public static Object getField(String key){
        return dataMap.get(key);
    }
}
