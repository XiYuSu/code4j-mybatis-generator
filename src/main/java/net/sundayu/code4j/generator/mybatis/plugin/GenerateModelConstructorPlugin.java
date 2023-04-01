package net.sundayu.code4j.generator.mybatis.plugin;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import java.util.List;
import java.util.Optional;

/**
 * @author xiyusu
 * @datetime 2021年04月13 23:17
 * @description 生成Model类构造函数
 */
public class GenerateModelConstructorPlugin extends PluginAdapter {
    @Override
    public boolean validate(List<String> list) {
        return true;
    }


    @Override
    public boolean modelPrimaryKeyClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        generateConstructor(topLevelClass, introspectedTable);
        return super.modelPrimaryKeyClassGenerated(topLevelClass, introspectedTable);
    }

    @Override
    public boolean modelRecordWithBLOBsClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        generateConstructor(topLevelClass, introspectedTable);
        return super.modelRecordWithBLOBsClassGenerated(topLevelClass, introspectedTable);
    }

    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        generateConstructor(topLevelClass, introspectedTable);
        return super.modelBaseRecordClassGenerated(topLevelClass, introspectedTable);
    }

    private void generateConstructor(TopLevelClass topLevelClass, IntrospectedTable introspectedTable){
        for (Method method: topLevelClass.getMethods()) {
            // 无参构造方法
            if(method.getName().equals(topLevelClass.getType().getShortName()) && method.getParameters().size()==0){
                for (Field field: topLevelClass.getFields()) {
                    switch (field.getType().getFullyQualifiedName()){
                        case "int":
                        case "java.lang.Integer":
                            method.addBodyLine("this."+field.getName() + " = 0;");
                            break;
                        case "boolean":
                            method.addBodyLine("this."+field.getName() + " = false;");
                            break;
                        case "java.util.Map":
                        case "java.util.HashMap":
                            method.addBodyLine("this."+field.getName() + " = new HashMap();");
                            break;
                        case "java.lang.Long":
                            method.addBodyLine("this."+field.getName() + " = 0L;");
                            break;
                        case "java.util.List":
                        case "java.util.ArrayList":
                            method.addBodyLine("this."+field.getName() + " = new ArrayList();");
                            break;
                        case "java.lang.String":
                            method.addBodyLine("this."+field.getName() + " = \"\";");
                            break;
                        case "java.util.Date":
                            method.addBodyLine("this."+field.getName() + " = new Date();");
                            break;
                        case "java.math.BigDecimal":
                            method.addBodyLine(getBigDecimalInitCodeLine(field, introspectedTable));
                            break;
                        default:
                            method.addBodyLine("this."+field.getName() + " = null;");
                            break;

                    }
                }
            }
        }
    }

    private String getBigDecimalInitCodeLine(Field field, IntrospectedTable introspectedTable){
        Optional<IntrospectedColumn> introspectedColumnOptional = introspectedTable.getColumn(humpToUnderline(field.getName()));
        IntrospectedColumn introspectedColumn = introspectedColumnOptional.get();
        int num = introspectedColumn.getScale();
        if (num==0){
            return "this."+field.getName() + " = BigDecimal.ZERO;";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("this.");
        sb.append(field.getName());
        sb.append(" = new BigDecimal(");
        int scaleCount = 1;
        sb.append("0.");
        for (int i = 0; i < num; i++) {
            sb.append("0");
        }
        sb.append(");");
        return sb.toString();
    }

    private String humpToUnderline(String fieldName){
        StringBuilder sb = new StringBuilder(fieldName);
        int tmp = 0;
        for (int i = 0; i < fieldName.length(); i++) {
            if(Character.isUpperCase(fieldName.charAt(i))){
                sb.insert(i+tmp, "_");
                tmp+=1;
            }
        }
        return sb.toString().toUpperCase();
    }
}
