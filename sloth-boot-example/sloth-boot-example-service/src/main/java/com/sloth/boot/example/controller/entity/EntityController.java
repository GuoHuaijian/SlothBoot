package com.sloth.boot.example.controller.entity;

import com.sloth.boot.common.log.annotation.OperateLog;
import com.sloth.boot.common.log.annotation.OperateTypeEnum;
import com.sloth.boot.common.result.PageResult;
import com.sloth.boot.common.result.R;
import com.sloth.boot.example.domain.entity.SysDept;
import com.sloth.boot.example.domain.entity.SysUser;
import com.sloth.boot.example.model.dept.request.DeptCreateRequest;
import com.sloth.boot.example.model.dept.vo.DeptVO;
import com.sloth.boot.example.model.user.request.UserCreateRequest;
import com.sloth.boot.example.model.user.request.UserQuery;
import com.sloth.boot.example.model.user.vo.SysUserVO;
import com.sloth.boot.example.service.entity.EntityDemoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 数据实体演示接口
 * <p>
 * 合并用户管理和部门管理，演示 MyBatis-Plus 高级 ORM 能力：
 * EncryptTypeHandler 加解密、JsonTypeHandler JSON 存储、
 * LambdaQueryWrapperX 动态条件、@DataPermission 数据权限、
 * @Desensitize 数据脱敏、@Phone/@IdCard 自定义校验、insertBatch 批量导入、
 * TreeUtil 树结构、乐观锁、逻辑删除、自动填充
 */
@Tag(name = "数据实体", description = "演示 MyBatis-Plus ORM 能力：用户与部门的 CRUD、加密、脱敏、数据权限、树结构、批量导入等")
@RestController
@RequestMapping("/api/entity")
@RequiredArgsConstructor
public class EntityController {

    private final EntityDemoService entityDemoService;

    // ==================== 用户接口 ====================

    @Operation(summary = "创建用户", description = "创建用户，手机号和身份证号自动AES加密存储，扩展信息自动JSON序列化存储")
    @OperateLog(module = "用户管理", description = "创建用户", type = OperateTypeEnum.CREATE)
    @PostMapping("/user")
    public R<SysUser> createUser(@Valid @RequestBody UserCreateRequest request) {
        return R.ok(entityDemoService.createUser(request));
    }

    @Operation(summary = "分页查询用户", description = "支持按用户名/手机号/部门/状态动态过滤，条件均可选")
    @GetMapping("/user/page")
    public R<PageResult<SysUser>> pageUser(UserQuery query) {
        return R.ok(entityDemoService.pageUser(query));
    }

    @Operation(summary = "查询用户详情", description = "查询用户，手机号和身份证号自动从数据库AES解密返回明文")
    @Parameter(name = "id", description = "用户ID", required = true, example = "1")
    @GetMapping("/user/{id}")
    public R<SysUser> getUserById(@PathVariable Long id) {
        return R.ok(entityDemoService.getUserById(id));
    }

    @Operation(summary = "查询用户（脱敏）", description = "返回脱敏后的用户信息：手机号138****8000，身份证110101********1234")
    @Parameter(name = "id", description = "用户ID", required = true, example = "1")
    @GetMapping("/user/{id}/desensitize")
    public R<SysUserVO> getUserVO(@PathVariable Long id) {
        return R.ok(entityDemoService.getUserVO(id));
    }

    @Operation(summary = "更新用户", description = "更新用户信息，使用乐观锁防止并发更新冲突")
    @OperateLog(module = "用户管理", description = "更新用户", type = OperateTypeEnum.UPDATE)
    @PutMapping("/user")
    public R<Boolean> updateUser(@RequestBody SysUser user) {
        return R.ok(entityDemoService.updateUser(user));
    }

    @Operation(summary = "删除用户", description = "逻辑删除用户（标记 deleted=1）")
    @Parameter(name = "id", description = "用户ID", required = true, example = "1")
    @OperateLog(module = "用户管理", description = "删除用户", type = OperateTypeEnum.DELETE)
    @DeleteMapping("/user/{id}")
    public R<Boolean> deleteUserById(@PathVariable Long id) {
        return R.ok(entityDemoService.deleteUserById(id));
    }

    @Operation(summary = "批量导入用户", description = "使用 insertBatch 单条SQL批量插入，敏感字段自动加密")
    @PostMapping("/user/import")
    public R<Integer> batchImportUsers(@RequestBody List<SysUser> users) {
        return R.ok(entityDemoService.batchImportUsers(users));
    }

    @Operation(summary = "数据权限查询（用户）", description = "演示 @DataPermission 数据权限：需先登录，不同dataScope返回不同范围")
    @GetMapping("/user/scope")
    public R<PageResult<SysUser>> pageUserWithPermission(UserQuery query) {
        return R.ok(entityDemoService.pageUserWithPermission(query));
    }

    // ==================== 部门接口 ====================

    @Operation(summary = "创建部门", description = "创建部门，自动填充创建人和创建时间，使用雪花算法生成ID")
    @OperateLog(module = "部门管理", description = "创建部门", type = OperateTypeEnum.CREATE)
    @PostMapping("/dept")
    public R<SysDept> createDept(@Valid @RequestBody DeptCreateRequest request) {
        return R.ok(entityDemoService.createDept(request));
    }

    @Operation(summary = "获取部门树", description = "查询全部部门并构建树形结构返回")
    @GetMapping("/dept/tree")
    public R<List<DeptVO>> getDeptTree() {
        return R.ok(entityDemoService.getDeptTree());
    }

    @Operation(summary = "查询部门详情", description = "根据部门ID查询部门信息")
    @Parameter(name = "id", description = "部门ID", required = true, example = "1")
    @GetMapping("/dept/{id}")
    public R<SysDept> getDeptById(@PathVariable Long id) {
        return R.ok(entityDemoService.getDeptById(id));
    }

    @Operation(summary = "更新部门", description = "更新部门信息，使用乐观锁防止并发更新冲突，需传入正确的 version 值")
    @OperateLog(module = "部门管理", description = "更新部门", type = OperateTypeEnum.UPDATE)
    @PutMapping("/dept")
    public R<Boolean> updateDept(@RequestBody SysDept dept) {
        return R.ok(entityDemoService.updateDept(dept));
    }

    @Operation(summary = "删除部门", description = "逻辑删除部门（数据不会物理删除，标记 deleted=1）")
    @Parameter(name = "id", description = "部门ID", required = true, example = "1")
    @OperateLog(module = "部门管理", description = "删除部门", type = OperateTypeEnum.DELETE)
    @DeleteMapping("/dept/{id}")
    public R<Boolean> deleteDeptById(@PathVariable Long id) {
        return R.ok(entityDemoService.deleteDeptById(id));
    }

    @Operation(summary = "批量导入部门", description = "使用 insertBatch 单条SQL批量插入，性能优于循环逐条insert")
    @PostMapping("/dept/import")
    public R<Integer> batchImportDepts(@RequestBody List<SysDept> depts) {
        return R.ok(entityDemoService.batchImportDepts(depts));
    }

    @Operation(summary = "数据权限查询（部门）", description = "演示 @DataScope 数据权限：需先登录，不同 dataScope 返回不同范围数据")
    @GetMapping("/dept/scope")
    public R<List<SysDept>> listDeptWithScope() {
        return R.ok(entityDemoService.listDeptWithScope());
    }
}
