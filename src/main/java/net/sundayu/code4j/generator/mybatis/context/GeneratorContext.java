package net.sundayu.code4j.generator.mybatis.context;

import net.sundayu.code4j.generator.mybatis.util.PropertyLoader;
import org.mybatis.generator.api.ConnectionFactory;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.internal.JDBCConnectionFactory;
import org.mybatis.generator.internal.ObjectFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author sxy19
 * @datetime 2021年04月11 22:47
 * @description 简述功能
 */
public class GeneratorContext {
    /**
     * 属性集合
     */
    public static Map<String, Object> propDataMap;

    private static Context context;

    private static ConnectionFactory connectionFactory;

    private static Connection connection;

    private static GeneratorContext generatorContext;

    static {
        propDataMap = PropertyLoader.getData();
    }

    /**
     * 模板集合
     */
    public static Map<String, String> templateMap = new HashMap<>();

    private GeneratorContext() {
    }

    private GeneratorContext(Context context) {
        GeneratorContext.context = context;
    }

    public static GeneratorContext getInstance(Context context) {
        if (null == generatorContext) {
            generatorContext = new GeneratorContext(context);
        }
        return generatorContext;
    }

    public static GeneratorContext getInstance() {
        if (null == generatorContext) {
            generatorContext = new GeneratorContext(context);
        }
        return generatorContext;
    }

    public  Connection getConnection() throws SQLException {
        if (null == connection) {
            if (null == connectionFactory) {
                if (null != context.getJdbcConnectionConfiguration()) {
                    connectionFactory = new JDBCConnectionFactory(context.getJdbcConnectionConfiguration());
                } else {
                    connectionFactory = ObjectFactory.createConnectionFactory(context);
                }
            }
            connection = connectionFactory.getConnection();
        }
        return connection;
    }

    public void closeConnection() {
        if (null != connection) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
