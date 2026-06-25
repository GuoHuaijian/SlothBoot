package com.sloth.boot.example.adapter.controller.user;

import com.sloth.boot.common.log.annotation.OperateLog;
import com.sloth.boot.common.log.annotation.OperateTypeEnum;
import com.sloth.boot.common.result.R;
import com.sloth.boot.example.application.command.user.RemoveUserCommand;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 删除用户接口。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Tag(name = "用户管理")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class RemoveUserController {

    private final RemoveUserCommand deleteUserCommand;

    @Operation(summary = "删除用户")
    @Parameter(name = "id", description = "用户ID", required = true, example = "1")
    @OperateLog(module = "用户管理", description = "删除用户", type = OperateTypeEnum.DELETE)
    @DeleteMapping("/{id}")
    public R<Void> removeUser(@PathVariable Long id) {
        deleteUserCommand.execute(id);
        return R.ok();
    }
}
