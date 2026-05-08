package com.sloth.boot.example.controller;

import com.alibaba.excel.annotation.ExcelProperty;
import com.sloth.boot.starter.excel.listener.ExcelReadListener;
import com.sloth.boot.starter.excel.util.ExcelUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Excel 导入导出示例控制器。
 * <p>
 * 展示 EasyExcel 封装的导入校验、流式导出功能。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@RestController
@RequestMapping("/excel")
@RequiredArgsConstructor
public class ExcelExampleController {

    /**
     * Excel 导出示例
     * <p>
     * 生成包含示例数据的 Excel 文件并直接下载。
     */
    @GetMapping("/export")
    public void export(HttpServletResponse response) throws IOException {
        List<UserExcelVO> data = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            UserExcelVO vo = new UserExcelVO();
            vo.setId((long) i);
            vo.setUsername("用户" + i);
            vo.setEmail("user" + i + "@example.com");
            vo.setPhone("1380000000" + i);
            data.add(vo);
        }
        ExcelUtil.export(response, "用户列表.xlsx", UserExcelVO.class, data);
    }

    /**
     * Excel 导入示例
     * <p>
     * 上传 Excel 文件，解析并返回数据。
     */
    @PostMapping("/import")
    public List<UserExcelVO> importExcel(@RequestParam("file") MultipartFile file) throws IOException {
        return ExcelUtil.importExcel(file, UserExcelVO.class, new ExcelReadListener<>(batch -> {
            // 分批处理逻辑（每 1000 条一批）
        }));
    }

    /**
     * Excel 导出数据模型
     */
    @Data
    public static class UserExcelVO {

        @ExcelProperty("用户ID")
        private Long id;

        @ExcelProperty("用户名")
        private String username;

        @ExcelProperty("邮箱")
        private String email;

        @ExcelProperty("手机号")
        private String phone;
    }
}
