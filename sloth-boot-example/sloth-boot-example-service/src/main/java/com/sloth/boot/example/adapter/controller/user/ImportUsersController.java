package com.sloth.boot.example.adapter.controller.user;

import com.sloth.boot.common.result.R;
import com.sloth.boot.example.application.command.user.ImportUsersCommand;
import com.sloth.boot.example.application.model.form.user.UserCreateForm;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 批量导入用户接口。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Tag(name = "用户管理")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class ImportUsersController {

    private final ImportUsersCommand batchImportUsersCommand;

    @Operation(summary = "批量导入用户")
    @PostMapping("/import")
    public R<Integer> importUsers(@Valid @RequestBody List<UserCreateForm> users) {
        return R.ok(batchImportUsersCommand.execute(users));
    }
}
