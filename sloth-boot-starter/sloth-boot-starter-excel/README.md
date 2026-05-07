# Sloth Boot Starter Excel

Excel 导入导出工具组件，基于 EasyExcel 封装，提供单/多 Sheet 导出、文件导入、模板下载及读取异常收集能力。

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
| `ExcelUtil` | 静态工具类，提供 export / importExcel / downloadTemplate 方法 |
| `ExcelReadListener<T>` | 通用读取监听器，支持分批处理、缓存数据、异常收集 |
| `ExcelResponseWrapper` | 响应头包装器，设置 Content-Type 和文件名编码 |
| `SheetData` | 多 Sheet 导出数据载体 |

## 导出示例

```java
// 定义表头
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
```

## 导入示例

```java
@PostMapping("/import")
public Result<Void> importExcel(@RequestParam("file") MultipartFile file) throws IOException {
    ExcelReadListener<UserExcelVO> listener = new ExcelReadListener<>(batch -> {
        // 分批处理，每 1000 条执行一次
        userService.batchInsert(batch);
    });
    List<UserExcelVO> list = ExcelUtil.importExcel(file, UserExcelVO.class, listener);
    if (!listener.getErrorRows().isEmpty()) {
        return Result.fail("存在错误行: " + listener.getErrorRows());
    }
    return Result.ok();
}
```

## 模板下载

```java
@GetMapping("/template")
public void downloadTemplate(HttpServletResponse response) throws IOException {
    ExcelUtil.downloadTemplate(response, "用户导入模板", UserExcelVO.class);
}
```

## 多 Sheet 导出

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

## FAQ

**Q: 导入数据量过大怎么办？**
A: `ExcelReadListener` 默认每 1000 条触发一次 `consumer` 回调，可在回调中分批写入数据库。

**Q: 如何处理导入校验失败的行？**
A: 在 `consumer` 回调中校验数据，校验失败调用 `listener.addInvalidRow(msg)` 记录，读取完成后通过 `listener.getInvalidRows()` 获取。

**Q: 导出文件名乱码？**
A: `ExcelResponseWrapper` 已处理 UTF-8 编码，无需额外配置。
