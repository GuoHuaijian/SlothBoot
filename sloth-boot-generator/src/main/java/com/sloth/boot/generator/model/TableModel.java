package com.sloth.boot.generator.model;

import java.util.List;
import java.util.Map;

import lombok.Builder;
import lombok.Getter;

/**
 * 单表生成视图模型，模板渲染的根对象。
 * <p>
 * 聚合了某张表对应的全部类名、包名、字段列表与按产物分类的 import 列表，
 * 模板只做渲染，不做任何业务判断。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Getter
@Builder
public class TableModel {

    // ==================== 表信息 ====================

    /**
     * 表名（如 sys_user）
     */
    private final String tableName;

    /**
     * 表注释（可能为空）
     */
    private final String tableComment;

    /**
     * 实体类名（如 User）
     */
    private final String className;

    /**
     * 实体变量名（如 user）
     */
    private final String variableName;

    /**
     * REST 资源路径（如 /api/users）
     */
    private final String apiPath;

    // ==================== 包名 ====================

    private final String controllerPackage;
    private final String commandPackage;
    private final String queryPackage;
    private final String formPackage;
    private final String qryPackage;
    private final String voPackage;
    private final String convertPackage;
    private final String errorCodePackage;
    private final String poPackage;
    private final String mapperPackage;

    // ==================== 类名 ====================

    private final String mapperClassName;
    private final String controllerClassName;
    private final String saveCommandClassName;
    private final String updateCommandClassName;
    private final String deleteCommandClassName;
    private final String getQueryClassName;
    private final String pageQueryClassName;
    private final String formClassName;
    private final String qryClassName;
    private final String voClassName;
    private final String convertClassName;
    private final String errorCodeClassName;

    // ==================== 变量名 ====================

    /**
     * 各依赖 Bean 的字段变量名（如 userSaveCommand），供构造器注入使用
     */
    private final String saveCommandVariableName;
    private final String updateCommandVariableName;
    private final String deleteCommandVariableName;
    private final String getQueryVariableName;
    private final String pageQueryVariableName;
    private final String mapperVariableName;
    private final String convertVariableName;

    // ==================== 基础类与开关 ====================

    /**
     * PO 是否继承实体基类
     */
    private final boolean extendsBaseEntity;

    private final String baseEntitySimpleName;
    private final String baseMapperSimpleName;
    private final String baseQuerySimpleName;
    private final String wrapperSimpleName;
    private final String resultSimpleName;
    private final String pageResultSimpleName;

    /**
     * 是否生成操作日志注解
     */
    private final boolean operateLogEnabled;

    private final String operateLogSimpleName;
    private final String operateTypeSimpleName;

    /**
     * 预计算的三个操作日志注解（含 module/description/type 实参），未启用时为 null
     */
    private final String saveOperateLogAnnotation;
    private final String updateOperateLogAnnotation;
    private final String deleteOperateLogAnnotation;

    /**
     * 是否生成 springdoc 注解
     */
    private final boolean swaggerAnnotations;

    /**
     * 是否生成 jakarta 校验注解
     */
    private final boolean validationAnnotations;

    // ==================== 功能开关（镜像自配置，供模板判断） ====================

    private final boolean generateCommand;
    private final boolean generateQuery;
    private final boolean generateQry;
    private final boolean generateForm;
    private final boolean generateVo;
    private final boolean generateErrorCode;

    // ==================== 字段列表 ====================

    /**
     * 全部字段（含基类继承列，供 Mapper XML resultMap 使用）
     */
    private final List<FieldModel> allFields;

    /**
     * PO 字段（已排除基类继承字段）
     */
    private final List<FieldModel> poFields;

    /**
     * Form 字段（排除主键与审计字段）
     */
    private final List<FieldModel> formFields;

    /**
     * 分页查询字段（同 Form 字段）
     */
    private final List<FieldModel> qryFields;

    /**
     * VO 字段（主键 + 业务字段 + 时间展示字段）
     */
    private final List<FieldModel> voFields;

    /**
     * 全部列名逗号串（供 Mapper XML Base_Column_List 使用，如 "id, username"）
     */
    private final String baseColumnList;

    /**
     * 分页查询条件链式调用行（如 ".likeIfPresent(User::getUsername, qry.getUsername())"，不含 orderBy）
     */
    private final List<String> wrapperConditionLines;

    // ==================== 主键与展示名 ====================

    /**
     * 主键字段名（如 id，无主键默认 id）
     */
    private final String pkFieldName;

    /**
     * 主键 Java 类型简单名（如 Long）
     */
    private final String pkType;

    /**
     * 主键 getter 方法名（如 getId）
     */
    private final String pkGetterName;

    /**
     * 模块显示名（取表注释去掉"表"后缀，用于 @Tag/@OperateLog/错误信息）
     */
    private final String moduleDisplayName;

    // ==================== 组合降级开关 ====================

    /**
     * 查询链路是否使用 VO（生成 VO 且生成 Convert 时为 true，否则直接返回 PO）
     */
    private final boolean usesVo;

    /**
     * 写入链路是否使用 Form + Convert（两者都开启时为 true，否则 Command 直接操作 PO）
     */
    private final boolean usesForm;

    // ==================== 错误码 ====================

    /**
     * "不存在"错误码值
     */
    private final int notFoundErrorCode;

    /**
     * "已存在"错误码值（表无唯一索引时为 null）
     */
    private final Integer duplicateErrorCode;

    /**
     * 唯一索引字段名（用于"已存在"错误提示，可能为空）
     */
    private final String uniqueFieldName;

    // ==================== 文档信息 ====================

    private final String author;
    private final String sinceVersion;

    /**
     * 生成日期（yyyy/MM/dd）
     */
    private final String date;

    /**
     * 按产物 ID 分类的 import 列表。
     *
     * @param artifactId 产物标识（见 {@code Artifact} 枚举）
     * @return 有序 import 全限定类名列表
     */
    public List<String> importsFor(String artifactId) {
        return importsByArtifact.getOrDefault(artifactId, List.of());
    }

    /**
     * import 注册表，由 {@link com.sloth.boot.generator.model.ModelFactory} 构建。
     */
    private final Map<String, List<String>> importsByArtifact;
}
