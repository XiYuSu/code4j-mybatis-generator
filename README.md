# code4j-mybatis-generator

#### 介绍
基于mybatis的代码生成组件，作为学习指引。

#### 软件架构
基于mybatis-generator-core 1.4.0。  
注1：1.4.0和1.3.7有兼容性问题。  
注2：oracle和mysql不同，表名需要大写，否则无法找到索引。

#### 安装教程

暂不涉及。目前可以通过使用CodeGenerator运行main函数简单使用。

#### 插件说明

##### Mapper接口生成插件 GenerateMapperMethodByIndexPlugin

依赖：Table标签enableInsert=true 
可选属性：
1. indexScope  
    all-所有  
    unique-唯一索引  
    non-unique-非唯一索引  
    默认值  all  
2. enablePrimaryKey  
    true：允许按主键生成方法   
    false：不允许按主键生成方法
    默认值：false 有重复方法，默认false  
3. enableSelect  
    true：允许生成Select方法   
    false：不允许生成Select方法
    默认值：true  
4. enableUpdate  
    true：允许生成Update方法   
    false：不允许生成Update方法
    默认值：true  
5. enableDelete  
    true：允许生成Delete方法  
    false：不允许生成Delete方法
    默认值：true
6. enableUniqueSelect  
    true：允许按唯一索引生成Select方法  
    false：不允许按唯一索引生成Select方法  
    默认值：true  
7. enableNonUniqueSelect  
    true：允许按非唯一索引生成Select方法  
    false：不允许按非唯一索引生成Select方法  
    默认值：true  
8. enableSingleUpdate  
    true：允许按唯一索引生成单笔Update方法    
    false：不允许按唯一索引生成单笔Update方法  
    默认值：true  
9. enableBatchUpdate  
    true：允许按唯一索引生成多笔Update方法 
    false：不允许按唯一索引生成多笔Update方法
    默认值：true  
10. enableUniqueIndexDelete  
    true：允许按唯一索引生成Delete方法    
    false：不允许按唯一索引生成Delete方法  
    默认值：true  
11. enableNonUniqueIndexDelete  
    true：允许按非唯一索引生成Delete方法  
    false：不允许按非唯一索引生成Delete方法  
    默认值：true  
12. enableSingleDelete  
    true：允许按唯一索引生成单笔Delete方法  
    false：不允许唯一索引生成单笔Delete方法  
    默认值：true
13. enableBatchDelete  
    true：允许按唯一索引生成多笔Delete方法  
    false：不允许唯一索引生成多笔Delete方法  
    默认值：true  
14. enableSingleInsert
    true：允许生成单笔Insert方法  
    false：不允许生成单笔Insert方法  
    默认值：true 不可改  
15. enableBatchInsert  
    true：允许生成多笔Insert方法  
    false：不允许生成多笔Insert方法  
    默认值：true
16. enableForUpdate  
    true：允许生成Select锁表方法  
    false：不允许生成Select锁表方法    
    默认值：false  

示例：
```xml
<plugin type="net.sundayu.code4j.generator.mybatis.plugin.GenerateMapperMethodByIndexPlugin">
    <property name="indexScope" value="all"/>
    <property name="enableBatchInsert" value="true"/>
</plugin>
```

##### SqlMap语句生成插件 GenerateSqlMapElementByIndexPlugin

同 GenerateMapperMethodByIndexPlugin 插件。  
示例：
```xml
<plugin type="net.sundayu.code4j.generator.mybatis.plugin.GenerateSqlMapElementByIndexPlugin">
    <property name="indexScope" value="all"/>
    <property name="enableBatchInsert" value="true"/>
</plugin>
```

##### Model构造方法生成插件 GenerateModelConstructorPlugin

依赖：javaModelGenerator标签属性constructorBased=true  
示例：  
```xml
<plugin type="net.sundayu.code4j.generator.mybatis.plugin.GenerateModelConstructorPlugin"/>
```

##### Model重写toString插件 GenerateModelOverrideToStringPlugin

示例：
```xml
<plugin type="net.sundayu.code4j.generator.mybatis.plugin.GenerateModelOverrideToStringPlugin"/>
```

##### SqlMap覆盖插件 ToolForOverrideSqlMapPlugin

示例：
```xml
<plugin type="net.sundayu.code4j.generator.mybatis.plugin.ToolForOverrideSqlMapPlugin"/>
```

#### 功能探索

1.基于javafx的页面操作；  
2.基于freemarker模板的代码生成；  
3.其他文档导出归档功能等。  