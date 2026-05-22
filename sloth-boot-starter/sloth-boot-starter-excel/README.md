# Sloth Boot Starter Excel

Excel 导入导出工具组件，基于 EasyExcel 封装，提供单/多 Sheet 导出、流式导入、自定义样式、模板下载及错误收集能力。

## Maven 依赖

```xml
<dependency>
    <groupId>com.sloth.boot</groupId>
    <artifactId>sloth-boot-starter-excel</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

## 核心组件

| 组件 | 说明 |
| --- | --- |
| `ExcelUtil` | 静态工具类，提供 export / importExcel / downloadTemplate 等方法 |
| `ExcelReadListener<T>` | 通用读取监听器，支持分批处理、缓存数据、异常收集（可配置批次大小） |
| `StreamingExcelReadListener<T>` | 流式读取监听器，不缓存所有数据，适用于大文件 |
| `ExcelExportBuilder<T>` | 导出构建器，提供流式 API 自定义样式、列宽、排除字段等 |
| `ExcelResponseWrapper` | 响应头包装器，设置 Content-Type 和文件名编码 |
| `SheetData` | 多 Sheet 导出数据载体 |
| `ExcelProperties` | 配置属性，统一 `sloth.excel.*` 前缀 |
| `ExcelImportResult<T>` | 导入结果，封装成功数据与错误详情 |
| `ExcelErrorDetail` | 细粒度错误详情，包含错误类型、行列信息等 |

## 配置项

```yaml
sloth:
  excel:
    enabled: true                    # 是否启用（默认 true）
    import-config:
      batch-size: 1000               # 每批处理的行数
      ignore-errors: false           # 是否忽略解析异常
      validate: false                # 是否启用 Bean Validation
      head-row-number: 1             # 表头行号（从 1 开始）
    export-config:
      default-sheet-name: "Sheet1"   # 默认 Sheet 名称
      auto-size-column: true         # 是否自动调整列宽
      style:
        header-background-color: "#F2F2F2"  # 表头背景色
        header-font-color: "#000000"        # 表头字体颜色
        header-font-bold: true              # 表头字体加粗
        header-font-size: 11                # 表头字体大小
```

> 配置项在 `spring-configuration-metadata-additional.json` 中注册了元数据，IDE 可获得自动补全。

## 导出示例

### 基本导出

```java
@Data
public class UserExcelVO {
    @ExcelProperty("用户ID")
    private Long userId;
    @ExcelProperty("用户名")
    private String username;
    @ExcelProperty("邮箱")
    private String email;
}

// 单 Sheet 导出
@GetMapping("/export")
public void export(HttpServletResponse response) throws IOException {
    List<UserExcelVO> data = userService.listExcelData();
    ExcelUtil.export(response, "用户列表", UserExcelVO.class, data);
}

// 指定 Sheet 名称导出
@GetMapping("/export-named")
public void exportNamed(HttpServletResponse response) throws IOException {
    List<UserExcelVO> data = userService.listExcelData();
    ExcelUtil.export(response, "用户列表", "用户信息", UserExcelVO.class, data);
}
```

### 多 Sheet 导出

```java
@GetMapping("/export-multi")
public void exportMultiSheet(HttpServletResponse response) throws IOException {
    List<SheetData> sheets = List.of(
        new SheetData("用户", UserExcelVO.class, userList),
        new SheetData("订单", OrderExcelVO.class, orderList)
    );
    ExcelUtil.export(response, "数据报表", sheets);
}
```

### 自定义样式导出（构建器模式）

```java
// 使用导出构建器自定义表头颜色和字体
@GetMapping("/export-styled")
public void exportStyled(HttpServletResponse response) throws IOException {
    List<UserExcelVO> data = userService.listExcelData();
    ExcelExportBuilder<UserExcelVO> builder = ExcelUtil.exportBuilder("styled-users", UserExcelVO.class, data)
        .sheetName("用户")
        .headerBackgroundColor("#4472C4")   // 蓝色表头
        .headerFontColor("#FFFFFF")          // 白色文字
        .headerFontBold(true)                // 加粗
        .autoSizeColumn(true);               // 自动列宽
    ExcelUtil.export(response, builder);
}
```

## 导入示例

### 基本导入（全量缓存）

```java
@PostMapping("/import")
public Result<Void> importExcel(@RequestParam("file") MultipartFile file) throws IOException {
    // 全量缓存，每 1000 条触发一次 consumer
    ExcelReadListener<UserExcelVO> listener = new ExcelReadListener<>(batch -> {
        userService.batchInsert(batch);
    });
    List<UserExcelVO> list = ExcelUtil.importExcel(file, UserExcelVO.class, listener);
    if (!listener.getErrorRows().isEmpty()) {
        return Result.fail("存在错误行: " + listener.getErrorRows());
    }
    return Result.ok();
}
```

### 快速导入（返回导入结果）

```java
@PostMapping("/import-simple")
public Result<?> importSimple(@RequestParam("file") MultipartFile file) throws IOException {
    ExcelImportResult<UserExcelVO> result = ExcelUtil.importExcel(file, UserExcelVO.class);
    if (!result.isAllSuccess()) {
        return Result.fail("导入完成，成功 " + result.getSuccessRows()
            + " 行，失败 " + result.getFailedRows() + " 行");
    }
    return Result.ok();
}
```

### 指定批次大小和表头行号

```java
@PostMapping("/import-custom")
public Result<Void> importCustom(@RequestParam("file") MultipartFile file) throws IOException {
    // 从第 3 行开始读取（跳过前 2 行），每 500 条处理一次
    List<UserExcelVO> list = ExcelUtil.importExcel(file, UserExcelVO.class, 500, 3, batch -> {
        userService.batchInsert(batch);
    });
    return Result.ok();
}
```

### 流式导入（大文件友好，不缓存全量数据）

```java
@PostMapping("/import-streaming")
public Result<Void> importStreaming(@RequestParam("file") MultipartFile file) throws IOException {
    StreamingExcelReadListener<UserExcelVO> listener = ExcelUtil.importExcelStreaming(
        file, UserExcelVO.class,
        2000,       // 每批 2000 行
        1,          // 表头在第 1 行
        batch -> {
            userService.batchInsert(batch);  // 每批直接入库
        }
    );
    if (listener.hasErrors()) {
        log.warn("导入过程中存在错误: {}", listener.getErrorDetails());
    }
    return Result.ok();
}
```

## 模板下载

```java
// 空白模板（仅表头）
@GetMapping("/template")
public void downloadTemplate(HttpServletResponse response) throws IOException {
    ExcelUtil.downloadTemplate(response, "用户导入模板", UserExcelVO.class);
}

// 带示例数据的模板
@GetMapping("/template-with-sample")
public void downloadTemplateWithSample(HttpServletResponse response) throws IOException {
    UserExcelVO sample = new UserExcelVO();
    sample.setUserId(1001L);
    sample.setUsername("张三");
    sample.setEmail("zhangsan@example.com");
    ExcelUtil.downloadTemplate(response, "用户导入模板", UserExcelVO.class, List.of(sample));
}
```

## 错误处理

```java
@PostMapping("/import-with-errors")
public Result<?> importWithErrors(@RequestParam("file") MultipartFile file) throws IOException {
    ExcelReadListener<UserExcelVO> listener = new ExcelReadListener<>(batch -> {
        for (UserExcelVO row : batch) {
            if (row.getUsername() == null) {
                // 记录自定义校验错误
                listener.addErrorDetail(ExcelErrorDetail.builder()
                    .errorType(ExcelErrorDetail.ErrorType.VALIDATION)
                    .rowIndex(0)
                    .columnName("username")
                    .message("用户名不能为空")
                    .build());
                continue;
            }
            userService.insert(row);
        }
    });

    ExcelUtil.importExcel(file, UserExcelVO.class, listener);

    if (!listener.getErrorDetails().isEmpty()) {
        // 可导出错误报告
        return Result.fail("存在 " + listener.getErrorDetails().size() + " 条错误");
    }
    return Result.ok();
}
```

## 核心 API

### ExcelUtil

| 方法 | 说明 |
|------|------|
| `export(response, fileName, head, data)` | 单 Sheet 导出 |
| `export(response, fileName, sheetName, head, data)` | 单 Sheet 导出（指定名称） |
| `export(response, fileName, sheets)` | 多 Sheet 导出 |
| `export(response, builder)` | 使用构建器导出（支持样式） |
| `importExcel(file, clazz, listener)` | 导入（自定义监听器） |
| `importExcel(file, clazz)` | 导入（快速，返回导入结果） |
| `importExcel(file, clazz, batchSize, consumer)` | 导入（指定批次大小） |
| `importExcel(file, clazz, batchSize, headRowNumber, consumer)` | 导入（指定批次和跳行） |
| `importExcelStreaming(file, clazz, batchSize, headRowNumber, consumer)` | 流式导入 |
| `downloadTemplate(response, fileName, head)` | 下载空白模板 |
| `downloadTemplate(response, fileName, head, sampleData)` | 下载带示例模板 |
| `exportBuilder(fileName, head, data)` | 创建导出构建器 |

### ExcelExportBuilder

| 方法 | 说明 |
|------|------|
| `sheetName(String)` | 设置 Sheet 名称 |
| `autoSizeColumn(boolean)` | 设置自动列宽 |

| `headerBackgroundColor(String)` | 设置表头背景色 |
| `headerFontColor(String)` | 设置表头字体颜色 |
| `headerFontBold(Boolean)` | 设置表头字体加粗 |
| `headerFontSize(Short)` | 设置表头字体大小 |
| `addWriteHandler(WriteHandler)` | 添加自定义写入处理器 |

## FAQ

**Q: 导入数据量过大怎么办？**
A: 使用 `StreamingExcelReadListener` 或 `ExcelReadListener` 的批次处理，每批处理完直接写入数据库，避免全量缓存。超大文件推荐使用 `importExcelStreaming` 流式导入。

**Q: 如何处理导入校验失败的行？**
A: 使用 `ExcelReadListener` 的 `addErrorDetail()` 记录细粒度错误，或通过 `addInvalidRow()` 记录简单错误。读取完成后通过 `getErrorDetails()` 获取完整错误列表。

**Q: 导出文件名乱码？**
A: `ExcelResponseWrapper` 已处理 UTF-8 编码，无需额外配置。

**Q: 如何自定义导出样式？**
A: 使用 `ExcelExportBuilder` 构建器，支持链式调用设置表头背景色、字体颜色、加粗等样式，也支持通过 `addWriteHandler` 注册自定义处理器。

**Q: 如何跳过文件开头的非数据行？**
A: 导入时指定 `headRowNumber` 参数，例如 `headRowNumber = 3` 表示从第 3 行开始读取数据（第 1-2 行为标题/说明行）。
