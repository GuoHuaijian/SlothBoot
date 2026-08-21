package com.sloth.boot.generator.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.sloth.boot.generator.config.GeneratorConfig;
import com.sloth.boot.generator.metadata.ColumnDefinition;
import com.sloth.boot.generator.metadata.TableDefinition;
import com.sloth.boot.generator.naming.JdbcTypeMapper;
import com.sloth.boot.generator.naming.NamingRules;

/**
 * 视图模型工厂：将表定义 + 配置转换为模板可直接渲染的 {@link TableModel}。
 * <p>
 * 所有命名推导、字段过滤、import 收集都在此完成，保证模板零逻辑。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public class ModelFactory {

    /**
     * 基类已包含的审计/框架列，继承基类时从 PO 中排除
     */
    private static final Set<String> BASE_ENTITY_COLUMNS = Set.of(
        "id", "create_by", "create_time", "update_by", "update_time", "deleted", "version");

    /**
     * VO 展示的时间列（其余审计列不对外暴露）
     */
    private static final Set<String> VO_DISPLAY_COLUMNS = Set.of("create_time", "update_time");

    /**
     * Java 简单类型名 → 全限定名（需要 import 的类型）
     */
    private static final Map<String, String> TYPE_IMPORTS = Map.of(
        "BigDecimal", "java.math.BigDecimal",
        "LocalDate", "java.time.LocalDate",
        "LocalDateTime", "java.time.LocalDateTime",
        "LocalTime", "java.time.LocalTime",
        "OffsetDateTime", "java.time.OffsetDateTime");

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");
    private static final String MYBATIS_ANNOTATIONS = "com.baomidou.mybatisplus.annotation.";
    private static final String SWAGGER_SCHEMA = "io.swagger.v3.oas.annotations.media.Schema";
    private static final String SWAGGER_OPERATION = "io.swagger.v3.oas.annotations.Operation";
    private static final String SWAGGER_TAG = "io.swagger.v3.oas.annotations.tags.Tag";
    private static final String VALIDATION = "jakarta.validation.constraints.";
    private static final String JAKARTA_VALID = "jakarta.validation.Valid";
    private static final String LOMBOK = "lombok.";
    private static final String SPRING_WEB = "org.springframework.web.bind.annotation.";

    /**
     * 构建单表视图模型。
     *
     * @param table             表定义
     * @param config            生成配置
     * @param errorCodeSequence 错误码序号（每张表占两个号）
     * @return 视图模型
     */
    public TableModel create(TableDefinition table, GeneratorConfig config, int errorCodeSequence) {
        String className = NamingRules.toClassName(table.getTableName(),
            config.getTablePrefixes().toArray(String[]::new));
        String variableName = NamingRules.toVariableName(className);
        boolean extendsBaseEntity = config.isExtendsBaseEntity() && notBlank(config.getBaseEntityFqcn());
        List<FieldModel> poFields = buildFields(table, extendsBaseEntity);
        List<FieldModel> formFields = buildBusinessFields(table);
        String moduleDisplayName = resolveModuleDisplayName(table, className);

        return TableModel.builder()
            .tableName(table.getTableName())
            .tableComment(table.getRemark())
            .className(className)
            .variableName(variableName)
            .apiPath(buildApiPath(config, className))
            .controllerPackage(fullPackage(config.getRootPackage(), config.getControllerPackage(), config.getModuleName()))
            .commandPackage(fullPackage(config.getRootPackage(), config.getCommandPackage(), config.getModuleName()))
            .queryPackage(fullPackage(config.getRootPackage(), config.getQueryPackage(), config.getModuleName()))
            .formPackage(fullPackage(config.getRootPackage(), config.getFormPackage(), config.getModuleName()))
            .qryPackage(fullPackage(config.getRootPackage(), config.getQueryModelPackage(), config.getModuleName()))
            .voPackage(fullPackage(config.getRootPackage(), config.getVoPackage(), config.getModuleName()))
            .convertPackage(fullPackage(config.getRootPackage(), config.getConvertPackage(), config.getModuleName()))
            .errorCodePackage(fullPackage(config.getRootPackage(), config.getErrorCodePackage(), config.getModuleName()))
            .poPackage(fullPackage(config.getRootPackage(), config.getPoPackage(), config.getModuleName()))
            .mapperPackage(fullPackage(config.getRootPackage(), config.getMapperPackage(), config.getModuleName()))
            .mapperClassName(className + "Mapper")
            .controllerClassName(className + "Controller")
            .saveCommandClassName(className + "SaveCommand")
            .updateCommandClassName(className + "UpdateCommand")
            .deleteCommandClassName(className + "DeleteCommand")
            .getQueryClassName(className + "GetQuery")
            .pageQueryClassName(className + "PageQuery")
            .formClassName(className + "Form")
            .qryClassName(className + "Qry")
            .voClassName(className + "VO")
            .convertClassName(className + "Convert")
            .errorCodeClassName(className + "ErrorCode")
            .saveCommandVariableName(variableName + "SaveCommand")
            .updateCommandVariableName(variableName + "UpdateCommand")
            .deleteCommandVariableName(variableName + "DeleteCommand")
            .getQueryVariableName(variableName + "GetQuery")
            .pageQueryVariableName(variableName + "PageQuery")
            .mapperVariableName(variableName + "Mapper")
            .convertVariableName(variableName + "Convert")
            .extendsBaseEntity(extendsBaseEntity)
            .baseEntitySimpleName(simpleName(config.getBaseEntityFqcn()))
            .baseMapperSimpleName(simpleName(orDefault(config.getBaseMapperFqcn(),
                "com.baomidou.mybatisplus.core.mapper.BaseMapper")))
            .baseQuerySimpleName(simpleName(config.getBaseQueryFqcn()))
            .wrapperSimpleName(simpleName(config.getQueryWrapperFqcn()))
            .resultSimpleName(simpleName(config.getResultFqcn()))
            .pageResultSimpleName(simpleName(config.getPageResultFqcn()))
            .operateLogEnabled(notBlank(config.getOperateLogFqcn()))
            .operateLogSimpleName(simpleName(config.getOperateLogFqcn()))
            .operateTypeSimpleName(simpleName(config.getOperateTypeFqcn()))
            .saveOperateLogAnnotation(buildOperateLogAnnotation(config, moduleDisplayName, "新增", "CREATE"))
            .updateOperateLogAnnotation(buildOperateLogAnnotation(config, moduleDisplayName, "更新", "UPDATE"))
            .deleteOperateLogAnnotation(buildOperateLogAnnotation(config, moduleDisplayName, "删除", "DELETE"))
            .swaggerAnnotations(config.isSwaggerAnnotations())
            .validationAnnotations(config.isValidationAnnotations())
            .generateCommand(config.isGenerateCommand())
            .generateQuery(config.isGenerateQuery())
            .generateQry(config.isGenerateQry())
            .generateForm(config.isGenerateForm())
            .generateVo(config.isGenerateVo())
            .generateErrorCode(config.isGenerateErrorCode())
            .poFields(poFields)
            .formFields(formFields)
            .qryFields(formFields)
            .voFields(buildVoFields(table))
            .allFields(buildAllFields(table))
            .baseColumnList(buildBaseColumnList(table))
            .wrapperConditionLines(buildWrapperConditionLines(className, formFields))
            .pkFieldName(pkFieldName(table))
            .pkType(pkType(table))
            .pkGetterName("get" + NamingRules.capitalize(pkFieldName(table)))
            .moduleDisplayName(moduleDisplayName)
            .usesVo(config.isGenerateVo() && config.isGenerateConvert())
            .usesForm(config.isGenerateForm() && config.isGenerateConvert())
            .notFoundErrorCode(errorCodeValue(config, errorCodeSequence))
            .duplicateErrorCode(table.getUniqueColumns().isEmpty() ? null
                : errorCodeValue(config, errorCodeSequence + 1))
            .uniqueFieldName(firstUniqueFieldName(table))
            .author(config.getAuthor())
            .sinceVersion(config.getSinceVersion())
            .date(LocalDate.now().format(DATE_FORMATTER))
            .importsByArtifact(buildImports(table, config, className, extendsBaseEntity))
            .build();
    }

    // ==================== 字段构建 ====================

    private List<FieldModel> buildFields(TableDefinition table, boolean extendsBaseEntity) {
        List<FieldModel> fields = new ArrayList<>();
        for (ColumnDefinition column : table.getColumns()) {
            if (extendsBaseEntity && isBaseEntityColumn(column)) {
                continue;
            }
            fields.add(toFieldModel(column));
        }
        return fields;
    }

    private List<FieldModel> buildBusinessFields(TableDefinition table) {
        List<FieldModel> fields = new ArrayList<>();
        for (ColumnDefinition column : table.getColumns()) {
            if (column.isPrimaryKey() || isBaseEntityColumn(column)) {
                continue;
            }
            fields.add(toFieldModel(column));
        }
        return fields;
    }

    private List<FieldModel> buildVoFields(TableDefinition table) {
        List<FieldModel> fields = new ArrayList<>();
        for (ColumnDefinition column : table.getColumns()) {
            boolean excluded = !column.isPrimaryKey() && isAuditColumn(column) && !isVoDisplayColumn(column);
            if (!excluded) {
                fields.add(toFieldModel(column));
            }
        }
        return fields;
    }

    private FieldModel toFieldModel(ColumnDefinition column) {
        String javaType = JdbcTypeMapper.toJavaType(column.getJdbcTypeCode());
        String fieldName = NamingRules.toFieldName(column.getColumnName());
        return FieldModel.builder()
            .columnName(column.getColumnName())
            .fieldName(fieldName)
            .javaType(javaType)
            .comment(column.getRemark())
            .displayName(notBlank(column.getRemark()) ? column.getRemark() : fieldName)
            .required(!column.isNullable() && !column.isPrimaryKey())
            .length(JdbcTypeMapper.isStringType(javaType) ? column.getColumnSize() : 0)
            .primaryKey(column.isPrimaryKey())
            .stringType(JdbcTypeMapper.isStringType(javaType))
            .getterName("get" + NamingRules.capitalize(fieldName))
            .auditRole(resolveAuditRole(column))
            .build();
    }

    private String resolveAuditRole(ColumnDefinition column) {
        if (column.isPrimaryKey()) {
            return "id";
        }
        return switch (column.getColumnName()) {
            case "create_time", "create_by" -> "insert_fill";
            case "update_time", "update_by" -> "insert_update_fill";
            case "deleted" -> "logic_delete";
            case "version" -> "version";
            default -> "";
        };
    }

    private List<FieldModel> buildAllFields(TableDefinition table) {
        List<FieldModel> fields = new ArrayList<>(table.getColumns().size());
        for (ColumnDefinition column : table.getColumns()) {
            fields.add(toFieldModel(column));
        }
        return fields;
    }

    private String buildBaseColumnList(TableDefinition table) {
        return String.join(", ", table.getColumns().stream()
            .map(ColumnDefinition::getColumnName)
            .toList());
    }

    /**
     * 构建分页查询条件链式调用行：字符串列用 likeIfPresent，其余用 eqIfPresent。
     */
    private List<String> buildWrapperConditionLines(String className, List<FieldModel> qryFields) {
        List<String> lines = new ArrayList<>(qryFields.size());
        for (FieldModel field : qryFields) {
            String method = field.isStringType() ? "likeIfPresent" : "eqIfPresent";
            lines.add("." + method + "(" + className + "::" + field.getGetterName()
                + ", query." + field.getFieldName() + ")");
        }
        return lines;
    }

    private String pkFieldName(TableDefinition table) {
        return table.getColumns().stream()
            .filter(ColumnDefinition::isPrimaryKey)
            .findFirst()
            .map(column -> NamingRules.toFieldName(column.getColumnName()))
            .orElse("id");
    }

    private String pkType(TableDefinition table) {
        return table.getColumns().stream()
            .filter(ColumnDefinition::isPrimaryKey)
            .findFirst()
            .map(column -> JdbcTypeMapper.toJavaType(column.getJdbcTypeCode()))
            .orElse("Long");
    }

    /**
     * 模块显示名：表注释去掉"表"后缀；无注释时回退为类名。
     */
    private String resolveModuleDisplayName(TableDefinition table, String className) {
        String remark = table.getRemark();
        if (!notBlank(remark)) {
            return className;
        }
        return remark.endsWith("表") ? remark.substring(0, remark.length() - 1) : remark;
    }

    /**
     * 预构建操作日志注解（注解实参含 "="，不能在模板中动态拼接，否则被解析为方法调用）。
     */
    private String buildOperateLogAnnotation(GeneratorConfig config, String moduleDisplayName,
                                             String action, String operateType) {
        if (!notBlank(config.getOperateLogFqcn())) {
            return null;
        }
        return "@" + simpleName(config.getOperateLogFqcn())
            + "(module = \"" + moduleDisplayName + "管理\", "
            + "description = \"" + action + moduleDisplayName + "\", "
            + "type = " + simpleName(config.getOperateTypeFqcn()) + "." + operateType + ")";
    }

    private boolean isBaseEntityColumn(ColumnDefinition column) {
        return BASE_ENTITY_COLUMNS.contains(column.getColumnName());
    }

    private boolean isAuditColumn(ColumnDefinition column) {
        return BASE_ENTITY_COLUMNS.contains(column.getColumnName()) && !"id".equals(column.getColumnName());
    }

    private boolean isVoDisplayColumn(ColumnDefinition column) {
        return VO_DISPLAY_COLUMNS.contains(column.getColumnName());
    }

    // ==================== import 收集 ====================

    private Map<String, List<String>> buildImports(TableDefinition table, GeneratorConfig config,
                                                   String className, boolean extendsBaseEntity) {
        Map<String, List<String>> imports = new HashMap<>();
        List<FieldModel> poFields = buildFields(table, extendsBaseEntity);
        List<FieldModel> businessFields = buildBusinessFields(table);
        List<FieldModel> voFields = buildVoFields(table);

        imports.put("po", poImports(config, extendsBaseEntity, table, poFields));
        imports.put("mapper", mapperImports(config, className));
        imports.put("controller", controllerImports(config, className));
        imports.put("save_command", writeCommandImports(config, className, true));
        imports.put("update_command", merge(writeCommandImports(config, className, true),
            bizErrorImports(config, className)));
        imports.put("delete_command", writeCommandImports(config, className, false));
        imports.put("get_query", merge(readQueryImports(config, className, false),
            bizErrorImports(config, className)));
        imports.put("page_query", readQueryImports(config, className, true));
        imports.put("form", formImports(config, businessFields));
        imports.put("qry", qryImports(config, businessFields));
        imports.put("vo", voImports(config, voFields));
        imports.put("convert", convertImports(config, className));
        imports.put("error_code", errorCodeImports());
        return imports;
    }

    private List<String> poImports(GeneratorConfig config, boolean extendsBaseEntity, TableDefinition table,
                                   List<FieldModel> poFields) {
        Set<String> imports = new TreeSet<>();
        imports.add(MYBATIS_ANNOTATIONS + "TableName");
        addIf(imports, extendsBaseEntity, config.getBaseEntityFqcn());
        if (!extendsBaseEntity) {
            addIf(imports, table.getPrimaryKeyColumn() != null, MYBATIS_ANNOTATIONS + "IdType");
            addIf(imports, table.getPrimaryKeyColumn() != null, MYBATIS_ANNOTATIONS + "TableId");
            addIf(imports, hasAuditRole(table, "insert_fill", "insert_update_fill"),
                MYBATIS_ANNOTATIONS + "TableField");
            addIf(imports, hasAuditRole(table, "insert_fill", "insert_update_fill"), MYBATIS_ANNOTATIONS + "FieldFill");
            addIf(imports, hasAuditRole(table, "logic_delete"), MYBATIS_ANNOTATIONS + "TableLogic");
            addIf(imports, hasAuditRole(table, "version"), MYBATIS_ANNOTATIONS + "Version");
            imports.add("java.io.Serializable");
        }
        imports.add(LOMBOK + "Data");
        imports.add(LOMBOK + "EqualsAndHashCode");
        addTypeImports(imports, poFields);
        return new ArrayList<>(imports);
    }

    /**
     * 将字段列表中需要 import 的类型（BigDecimal/时间类型等）加入集合。
     */
    private void addTypeImports(Set<String> imports, List<FieldModel> fields) {
        for (FieldModel field : fields) {
            String fqcn = TYPE_IMPORTS.get(field.getJavaType());
            if (fqcn != null) {
                imports.add(fqcn);
            }
        }
    }

    private boolean hasAuditRole(TableDefinition table, String... roles) {
        Set<String> roleSet = Set.of(roles);
        return table.getColumns().stream().anyMatch(column -> roleSet.contains(resolveAuditRole(column)));
    }

    private List<String> mapperImports(GeneratorConfig config, String className) {
        Set<String> imports = new TreeSet<>();
        imports.add("org.apache.ibatis.annotations.Mapper");
        imports.add(orDefault(config.getBaseMapperFqcn(), "com.baomidou.mybatisplus.core.mapper.BaseMapper"));
        imports.add(poFqcnOf(config, className));
        return new ArrayList<>(imports);
    }

    private List<String> controllerImports(GeneratorConfig config, String className) {
        Set<String> imports = new TreeSet<>();
        addIf(imports, config.isSwaggerAnnotations(), SWAGGER_TAG);
        addIf(imports, config.isSwaggerAnnotations(), SWAGGER_OPERATION);
        addIf(imports, config.isValidationAnnotations() && config.isGenerateForm(), JAKARTA_VALID);
        imports.add(SPRING_WEB + "RestController");
        imports.add(SPRING_WEB + "RequestMapping");
        boolean writeEndpoints = config.isGenerateCommand() && config.isGenerateForm() && config.isGenerateConvert();
        addIf(imports, writeEndpoints, SPRING_WEB + "PostMapping");
        addIf(imports, writeEndpoints, SPRING_WEB + "PutMapping");
        addIf(imports, writeEndpoints, SPRING_WEB + "DeleteMapping");
        addIf(imports, writeEndpoints, SPRING_WEB + "RequestBody");
        if (config.isGenerateQuery() || writeEndpoints) {
            imports.add(SPRING_WEB + "GetMapping");
            imports.add(SPRING_WEB + "PathVariable");
        }
        imports.add(LOMBOK + "RequiredArgsConstructor");
        addIf(imports, notBlank(config.getResultFqcn()), config.getResultFqcn());
        addIf(imports, config.isGenerateCommand() && config.isGenerateQry()
            && notBlank(config.getPageResultFqcn()), config.getPageResultFqcn());
        boolean operateLogEnabled = notBlank(config.getOperateLogFqcn());
        addIf(imports, operateLogEnabled, config.getOperateLogFqcn());
        addIf(imports, operateLogEnabled, config.getOperateTypeFqcn());
        addIf(imports, config.isGenerateCommand(), commandFqcnOf(config, className, "SaveCommand"));
        addIf(imports, config.isGenerateCommand(), commandFqcnOf(config, className, "UpdateCommand"));
        addIf(imports, config.isGenerateCommand(), commandFqcnOf(config, className, "DeleteCommand"));
        addIf(imports, config.isGenerateQuery(), queryFqcnOf(config, className, "GetQuery"));
        addIf(imports, config.isGenerateQuery() && config.isGenerateQry(),
            queryFqcnOf(config, className, "PageQuery"));
        addIf(imports, config.isGenerateForm(), formFqcnOf(config, className));
        addIf(imports, config.isGenerateQry(), qryFqcnOf(config, className));
        addIf(imports, config.isGenerateVo(), voFqcnOf(config, className));
        return new ArrayList<>(imports);
    }

    private List<String> writeCommandImports(GeneratorConfig config, String className, boolean withForm) {
        Set<String> imports = new TreeSet<>();
        imports.add(LOMBOK + "Slf4j");
        imports.add(LOMBOK + "RequiredArgsConstructor");
        imports.add("org.springframework.stereotype.Component");
        imports.add("org.springframework.transaction.annotation.Transactional");
        imports.add(mapperFqcnOf(config, className));
        imports.add(poFqcnOf(config, className));
        addIf(imports, withForm && config.isGenerateForm(), formFqcnOf(config, className));
        addIf(imports, withForm && config.isGenerateConvert(), convertFqcnOf(config, className));
        return new ArrayList<>(imports);
    }

    private List<String> readQueryImports(GeneratorConfig config, String className, boolean page) {
        Set<String> imports = new TreeSet<>();
        imports.add(LOMBOK + "RequiredArgsConstructor");
        imports.add("org.springframework.stereotype.Component");
        imports.add(mapperFqcnOf(config, className));
        imports.add(poFqcnOf(config, className));
        addIf(imports, config.isGenerateConvert(), convertFqcnOf(config, className));
        addIf(imports, config.isGenerateVo(), voFqcnOf(config, className));
        if (page) {
            imports.add("java.util.List");
            imports.add(config.getQueryWrapperFqcn());
            imports.add(config.getPageResultFqcn());
            addIf(imports, config.isGenerateQry(), qryFqcnOf(config, className));
        }
        return new ArrayList<>(imports);
    }

    /**
     * 业务异常 + 错误码枚举 import（查询/更新失败路径使用）。
     */
    private List<String> bizErrorImports(GeneratorConfig config, String className) {
        Set<String> imports = new TreeSet<>();
        if (!config.isGenerateErrorCode()) {
            return List.of();
        }
        imports.add("com.sloth.boot.common.exception.BizException");
        imports.add(fullPackage(config.getRootPackage(), config.getErrorCodePackage(), config.getModuleName())
            + "." + className + "ErrorCode");
        return new ArrayList<>(imports);
    }

    private List<String> formImports(GeneratorConfig config, List<FieldModel> formFields) {
        Set<String> imports = new TreeSet<>();
        addIf(imports, config.isSwaggerAnnotations(), SWAGGER_SCHEMA);
        addIf(imports, config.isValidationAnnotations(), VALIDATION + "NotBlank");
        addIf(imports, config.isValidationAnnotations(), VALIDATION + "NotNull");
        addIf(imports, config.isValidationAnnotations(), VALIDATION + "Size");
        imports.add(LOMBOK + "Data");
        addTypeImports(imports, formFields);
        return new ArrayList<>(imports);
    }

    private List<String> qryImports(GeneratorConfig config, List<FieldModel> qryFields) {
        Set<String> imports = new TreeSet<>();
        addIf(imports, config.isSwaggerAnnotations(), SWAGGER_SCHEMA);
        imports.add(config.getBaseQueryFqcn());
        imports.add(LOMBOK + "Data");
        imports.add(LOMBOK + "EqualsAndHashCode");
        addTypeImports(imports, qryFields);
        return new ArrayList<>(imports);
    }

    private List<String> voImports(GeneratorConfig config, List<FieldModel> voFields) {
        Set<String> imports = new TreeSet<>();
        addIf(imports, config.isSwaggerAnnotations(), SWAGGER_SCHEMA);
        imports.add("com.fasterxml.jackson.databind.annotation.JsonSerialize");
        imports.add("com.fasterxml.jackson.databind.ser.std.ToStringSerializer");
        imports.add(LOMBOK + "Data");
        addTypeImports(imports, voFields);
        return new ArrayList<>(imports);
    }

    private List<String> convertImports(GeneratorConfig config, String className) {
        Set<String> imports = new TreeSet<>();
        imports.add("org.mapstruct.Mapper");
        imports.add("org.mapstruct.MappingTarget");
        imports.add("org.mapstruct.ReportingPolicy");
        imports.add(poFqcnOf(config, className));
        addIf(imports, config.isGenerateForm(), formFqcnOf(config, className));
        addIf(imports, config.isGenerateVo(), voFqcnOf(config, className));
        return new ArrayList<>(imports);
    }

    private List<String> errorCodeImports() {
        Set<String> imports = new TreeSet<>();
        imports.add("com.sloth.boot.common.exception.ErrorCode");
        imports.add(LOMBOK + "AllArgsConstructor");
        imports.add(LOMBOK + "Getter");
        return new ArrayList<>(imports);
    }

    // ==================== 辅助方法 ====================

    private void addIf(Set<String> imports, boolean condition, String fqcn) {
        if (condition && notBlank(fqcn)) {
            imports.add(fqcn);
        }
    }

    /**
     * 合并两组 import 并排序去重。
     */
    private List<String> merge(List<String> primary, List<String> secondary) {
        Set<String> merged = new TreeSet<>(primary);
        merged.addAll(secondary);
        return new ArrayList<>(merged);
    }

    private String fullPackage(String rootPackage, String subPackage, String moduleName) {
        return rootPackage + "." + subPackage + "." + moduleName;
    }

    private String simpleName(String fqcn) {
        if (!notBlank(fqcn)) {
            return "";
        }
        int dotIndex = fqcn.lastIndexOf('.');
        return dotIndex < 0 ? fqcn : fqcn.substring(dotIndex + 1);
    }

    private String orDefault(String value, String defaultValue) {
        return notBlank(value) ? value : defaultValue;
    }

    private boolean notBlank(String value) {
        return value != null && !value.isBlank();
    }

    private String poFqcnOf(GeneratorConfig config, String className) {
        return fullPackage(config.getRootPackage(), config.getPoPackage(), config.getModuleName()) + "." + className;
    }

    private String mapperFqcnOf(GeneratorConfig config, String className) {
        return fullPackage(config.getRootPackage(), config.getMapperPackage(), config.getModuleName())
            + "." + className + "Mapper";
    }

    private String commandFqcnOf(GeneratorConfig config, String className, String suffix) {
        return fullPackage(config.getRootPackage(), config.getCommandPackage(), config.getModuleName())
            + "." + className + suffix;
    }

    private String queryFqcnOf(GeneratorConfig config, String className, String suffix) {
        return fullPackage(config.getRootPackage(), config.getQueryPackage(), config.getModuleName())
            + "." + className + suffix;
    }

    private String formFqcnOf(GeneratorConfig config, String className) {
        return fullPackage(config.getRootPackage(), config.getFormPackage(), config.getModuleName())
            + "." + className + "Form";
    }

    private String qryFqcnOf(GeneratorConfig config, String className) {
        return fullPackage(config.getRootPackage(), config.getQueryModelPackage(), config.getModuleName())
            + "." + className + "Qry";
    }

    private String voFqcnOf(GeneratorConfig config, String className) {
        return fullPackage(config.getRootPackage(), config.getVoPackage(), config.getModuleName())
            + "." + className + "VO";
    }

    private String convertFqcnOf(GeneratorConfig config, String className) {
        return fullPackage(config.getRootPackage(), config.getConvertPackage(), config.getModuleName())
            + "." + className + "Convert";
    }

    private String buildApiPath(GeneratorConfig config, String className) {
        String segment = NamingRules.toResourceSegment(className);
        String prefix = config.getApiPrefix().endsWith("/")
            ? config.getApiPrefix().substring(0, config.getApiPrefix().length() - 1)
            : config.getApiPrefix();
        return prefix + "/" + segment;
    }

    private int errorCodeValue(GeneratorConfig config, int sequence) {
        int prefix = Integer.parseInt(config.getErrorCodePrefix()) * 100;
        return prefix + config.getErrorCodeStart() + sequence;
    }

    private String firstUniqueFieldName(TableDefinition table) {
        return table.getUniqueColumns().stream()
            .findFirst()
            .map(NamingRules::toFieldName)
            .orElse(null);
    }
}
