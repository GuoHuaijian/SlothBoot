package com.sloth.boot.example.adapter.controller.user;

import com.sloth.boot.common.log.annotation.OperateLog;
import com.sloth.boot.common.log.annotation.OperateTypeEnum;
import com.sloth.boot.common.result.R;
import com.sloth.boot.example.application.command.user.ModifyUserCommand;
import com.sloth.boot.example.application.model.form.user.UserUpdateForm;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 更新用户接口。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Tag(name = "用户管理")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class ModifyUserController {

    private final ModifyUserCommand updateUserCommand;

    @Operation(summary = "更新用户")
    @OperateLog(module = "用户管理", description = "更新用户", type = OperateTypeEnum.UPDATE)
    @PutMapping
    public R<Void> modifyUser(@Valid @RequestBody UserUpdateForm form) {
        updateUserCommand.execute(form);
        return R.ok();
    }
}
