package com.sloth.boot.example.service.entity;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sloth.boot.common.result.PageResult;
import com.sloth.boot.common.util.TreeUtil;
import com.sloth.boot.example.domain.entity.SysDept;
import com.sloth.boot.example.domain.entity.SysUser;
import com.sloth.boot.example.domain.mapper.SysDeptMapper;
import com.sloth.boot.example.domain.mapper.SysUserMapper;
import com.sloth.boot.example.model.dept.request.DeptCreateRequest;
import com.sloth.boot.example.model.dept.vo.DeptVO;
import com.sloth.boot.example.model.user.request.UserCreateRequest;
import com.sloth.boot.example.model.user.request.UserQuery;
import com.sloth.boot.example.model.user.vo.SysUserVO;
import com.sloth.boot.starter.mybatis.core.BaseMapperX;
import com.sloth.boot.starter.mybatis.core.LambdaQueryWrapperX;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 数据实体演示服务 - 展示 MyBatis-Plus 核心 ORM 能力
 * <p>
 * 合并用户和部门服务，演示：
 * <ul>
 *   <li>BaseMapperX — 基础 CRUD、分页查询</li>
 *   <li>LambdaQueryWrapperX — null-safe 条件拼接（likeIfPresent/eqIfPresent）</li>
 *   <li>EncryptTypeHandler — phone/idCard 字段 AES 自动加解密</li>
 *   <li>JsonTypeHandler — extraInfo 字段 JSON 自动序列化/反序列化</li>
 *   <li>insertBatch — 单语句批量插入</li>
 *   <li>@DataPermission — 增强型数据权限</li>
 *   <li>@Desensitize — VO 层数据脱敏</li>
 *   <li>TreeUtil — 构建部门树</li>
 *   <li>乐观锁 — @Version 版本号控制</li>
 *   <li>逻辑删除 — @TableLogic 软删除</li>
 *   <li>自动填充 — createBy/updateBy/createTime/updateTime 自动写入</li>
 * </ul>
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EntityDemoService {

    private final SysUserMapper userMapper;
    private final SysDeptMapper deptMapper;

    // ==================== 用户相关 ====================

    /**
     * 创建用户
     *
     * @param request 创建请求
     * @return 创建的用户实体（字段已解密）
     */
    public SysUser createUser(UserCreateRequest request) {
        SysUser user = new SysUser();
        user.setUsername(request.getUsername());
        user.setPhone(request.getPhone());
        user.setIdCard(request.getIdCard());
        user.setEmail(request.getEmail());
        user.setGender(request.getGender() != null ? request.getGender() : 0);
        user.setStatus(request.getStatus() != null ? request.getStatus() : 0);
        user.setDeptId(request.getDeptId());
        user.setExtraInfo(request.getExtraInfo());
        userMapper.insert(user);
        log.info("创建用户成功: id={}, username={}", user.getId(), user.getUsername());
        return user;
    }

    /**
     * 分页查询用户（动态条件过滤）
     *
     * @param query 查询条件
     * @return 分页结果
     */
    public PageResult<SysUser> pageUser(UserQuery query) {
        LambdaQueryWrapperX<SysUser> wrapper = new LambdaQueryWrapperX<SysUser>()
                .likeIfPresent(SysUser::getUsername, query.getUsername())
                .eqIfPresent(SysUser::getPhone, query.getPhone())
                .eqIfPresent(SysUser::getDeptId, query.getDeptId())
                .eqIfPresent(SysUser::getStatus, query.getStatus());
        wrapper.orderByDesc(SysUser::getCreateTime);
        return userMapper.selectPage(query, wrapper);
    }

    /**
     * 根据ID查询用户
     *
     * @param id 用户ID
     * @return 用户实体（敏感字段已解密）
     */
    public SysUser getUserById(Long id) {
        return userMapper.selectById(id);
    }

    /**
     * 查询用户脱敏视图
     *
     * @param id 用户ID
     * @return 脱敏后的用户 VO
     */
    public SysUserVO getUserVO(Long id) {
        SysUser user = userMapper.selectById(id);
        if (user == null) {
            return null;
        }
        return toUserVO(user);
    }

    /**
     * 更新用户（乐观锁）
     *
     * @param user 用户实体（必须包含 id 和 version）
     * @return 是否更新成功
     */
    public boolean updateUser(SysUser user) {
        int rows = userMapper.updateById(user);
        log.info("更新用户: id={}, affected={}", user.getId(), rows);
        return rows > 0;
    }

    /**
     * 删除用户（逻辑删除）
     *
     * @param id 用户ID
     * @return 是否删除成功
     */
    public boolean deleteUserById(Long id) {
        int rows = userMapper.deleteById(id);
        log.info("逻辑删除用户: id={}, affected={}", id, rows);
        return rows > 0;
    }

    /**
     * 批量导入用户
     *
     * @param users 用户列表
     * @return 插入行数
     */
    public int batchImportUsers(List<SysUser> users) {
        if (users == null || users.isEmpty()) {
            return 0;
        }
        int rows = userMapper.insertBatch(users);
        log.info("批量导入用户: count={}", rows);
        return rows;
    }

    /**
     * 带数据权限分页查询用户
     *
     * @param query 查询条件
     * @return 分页结果
     */
    public PageResult<SysUser> pageUserWithPermission(UserQuery query) {
        LambdaQueryWrapperX<SysUser> wrapper = new LambdaQueryWrapperX<SysUser>()
                .likeIfPresent(SysUser::getUsername, query.getUsername())
                .eqIfPresent(SysUser::getDeptId, query.getDeptId())
                .eqIfPresent(SysUser::getStatus, query.getStatus());
        Page<SysUser> page = new Page<>(query.getPageNum(), query.getPageSize());
        Page<SysUser> result = userMapper.selectPageWithPermission(page, wrapper);
        return BaseMapperX.toPageResult(result);
    }

    // ==================== 部门相关 ====================

    /**
     * 创建部门
     *
     * @param request 创建请求
     * @return 创建的部门实体
     */
    public SysDept createDept(DeptCreateRequest request) {
        SysDept dept = new SysDept();
        dept.setName(request.getName());
        dept.setParentId(request.getParentId() != null ? request.getParentId() : 0L);
        dept.setLeader(request.getLeader());
        dept.setSort(request.getSort() != null ? request.getSort() : 0);
        dept.setStatus(request.getStatus() != null ? request.getStatus() : 0);
        if (dept.getParentId() == 0) {
            dept.setAncestors("0");
        } else {
            SysDept parent = deptMapper.selectById(dept.getParentId());
            dept.setAncestors(parent != null ? parent.getAncestors() + "," + parent.getId() : "0");
        }
        deptMapper.insert(dept);
        log.info("创建部门成功: id={}, name={}", dept.getId(), dept.getName());
        return dept;
    }

    /**
     * 构建部门树
     * <p>
     * 查询全部部门后，使用 {@link TreeUtil#buildTree} 将平铺列表转为树形结构。
     *
     * @return 部门树（根节点列表）
     */
    public List<DeptVO> getDeptTree() {
        List<SysDept> depts = deptMapper.selectList(null);
        List<DeptVO> voList = depts.stream().map(this::toDeptVO).toList();
        return TreeUtil.buildTree(voList, 0L);
    }

    /**
     * 根据ID查询部门
     *
     * @param id 部门ID
     * @return 部门实体
     */
    public SysDept getDeptById(Long id) {
        return deptMapper.selectById(id);
    }

    /**
     * 更新部门（乐观锁）
     *
     * @param dept 部门实体（必须包含 id 和 version）
     * @return 是否更新成功
     */
    public boolean updateDept(SysDept dept) {
        int rows = deptMapper.updateById(dept);
        log.info("更新部门: id={}, affected={}", dept.getId(), rows);
        return rows > 0;
    }

    /**
     * 删除部门（逻辑删除）
     *
     * @param id 部门ID
     * @return 是否删除成功
     */
    public boolean deleteDeptById(Long id) {
        int rows = deptMapper.deleteById(id);
        log.info("逻辑删除部门: id={}, affected={}", id, rows);
        return rows > 0;
    }

    /**
     * 批量导入部门
     *
     * @param depts 部门列表
     * @return 插入行数
     */
    public int batchImportDepts(List<SysDept> depts) {
        if (depts == null || depts.isEmpty()) {
            return 0;
        }
        int rows = deptMapper.insertBatch(depts);
        log.info("批量导入部门: count={}", rows);
        return rows;
    }

    /**
     * 带数据权限查询部门列表
     *
     * @return 部门列表
     */
    public List<SysDept> listDeptWithScope() {
        return deptMapper.selectListWithScope(null);
    }

    // ==================== 私有转换方法 ====================

    private SysUserVO toUserVO(SysUser user) {
        SysUserVO vo = new SysUserVO();
        vo.setId(user.getId());
        vo.setDeptId(user.getDeptId());
        vo.setUsername(user.getUsername());
        vo.setPhone(user.getPhone());
        vo.setIdCard(user.getIdCard());
        vo.setEmail(user.getEmail());
        vo.setGender(user.getGender());
        vo.setStatus(user.getStatus());
        vo.setExtraInfo(user.getExtraInfo());
        vo.setCreateBy(user.getCreateBy());
        vo.setCreateTime(user.getCreateTime());
        vo.setUpdateBy(user.getUpdateBy());
        vo.setUpdateTime(user.getUpdateTime());
        return vo;
    }

    private DeptVO toDeptVO(SysDept dept) {
        DeptVO vo = new DeptVO();
        vo.setId(dept.getId());
        vo.setName(dept.getName());
        vo.setParentId(dept.getParentId());
        vo.setLeader(dept.getLeader());
        vo.setStatus(dept.getStatus());
        vo.setAncestors(dept.getAncestors());
        vo.setCreateBy(dept.getCreateBy());
        vo.setCreateTime(dept.getCreateTime());
        return vo;
    }
}
