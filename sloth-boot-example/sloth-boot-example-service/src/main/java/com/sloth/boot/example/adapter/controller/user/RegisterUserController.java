package com.sloth.boot.example.adapter.controller.user;

import com.sloth.boot.common.log.annotation.OperateLog;
import com.sloth.boot.common.log.annotation.OperateTypeEnum;
import com.sloth.boot.common.result.R;
import com.sloth.boot.example.application.command.user.RegisterUserCommand;
import com.sloth.boot.example.application.model.form.user.UserCreateForm;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 创建用户接口。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Tag(name = "用户管理")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class RegisterUserController {

    private final RegisterUserCommand createUserCommand;

    @Operation(summary = "创建用户")
    @OperateLog(module = "用户管理", description = "创建用户", type = OperateTypeEnum.CREATE)
    @PostMapping
    public R<Long> registerUser(@Valid @RequestBody UserCreateForm form) {
        return R.ok(createUserCommand.execute(form));
    }
}
