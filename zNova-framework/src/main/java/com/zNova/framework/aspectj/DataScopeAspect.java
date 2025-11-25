package com.zNova.framework.aspectj;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import com.zNova.common.annotation.DataScope;
import com.zNova.common.constant.UserConstants;
import com.zNova.common.core.domain.BaseEntity;
import com.zNova.common.core.domain.entity.SysRole;
import com.zNova.common.core.domain.entity.SysUser;
import com.zNova.common.core.domain.model.LoginUser;
import com.zNova.common.core.text.Convert;
import com.zNova.common.utils.SecurityUtils;
import com.zNova.common.utils.StringUtils;
import com.zNova.framework.security.context.PermissionContextHolder;
import org.springframework.stereotype.Service;

/**
 * 数据过滤处理
 *
 * @author ruoyi
 */
@Aspect
@Component
public class DataScopeAspect
{
    /**
     * 数据权限过滤关键字
     */
    public static final String DATA_SCOPE = "dataScope";

    @Before("@annotation(controllerDataScope)")
    public void doBefore(JoinPoint point, DataScope controllerDataScope) throws Throwable
    {
        clearDataScope(point);
        handleDataScope(point, controllerDataScope);
    }

    protected void handleDataScope(final JoinPoint joinPoint, DataScope controllerDataScope)
    {
        // 获取当前的用户
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (StringUtils.isNotNull(loginUser))
        {
            // 生成数据权限SQL
            String dataScopeSql = getDataScopeSql(controllerDataScope, loginUser);

            if (StringUtils.isNotBlank(dataScopeSql))
            {
                Object params = joinPoint.getArgs()[0];
                if (StringUtils.isNotNull(params) && params instanceof BaseEntity)
                {
                    BaseEntity baseEntity = (BaseEntity) params;
                    baseEntity.getParams().put(DATA_SCOPE, dataScopeSql);
                }
            }
        }
    }

    /**
     * 获取数据权限过滤SQL
     */
    private String getDataScopeSql(DataScope dataScope, LoginUser loginUser)
    {
        StringBuilder sqlString = new StringBuilder();
        SysUser currentUser = loginUser.getUser();

        // 调试日志
        System.out.println("DataScopeAspect - 当前用户ID: " + currentUser.getUserId() + ", 是否管理员: " + currentUser.isAdmin() + ", deptId: " + currentUser.getDeptId());

        // 1. 平台管理员，拥有所有权限，直接返回空字符串，不添加任何过滤条件
        if (currentUser.isAdmin())
        {
            System.out.println("DataScopeAspect - 管理员用户，不添加过滤条件");
            return "";
        }

        // 2. 获取数据权限注解的参数
        String deptAlias = dataScope.deptAlias(); // 部门表别名（默认）
        String userAlias = dataScope.userAlias();

        System.out.println("DataScopeAspect - deptAlias: " + deptAlias);

        // 3. 针对部门隔离的场景（如商品表），使用部门ID过滤
        if (StringUtils.isNotBlank(deptAlias))
        {
            Long deptId = currentUser.getDeptId();
            System.out.println("DataScopeAspect - 部门过滤，deptId: " + deptId);
            if (deptId != null)
            {
                // 拼接部门ID过滤条件：AND 别名.dept_id = 当前用户的部门ID
                String filterSql = " AND " + deptAlias + ".dept_id = " + deptId;
                sqlString.append(filterSql);
                System.out.println("DataScopeAspect - 添加部门过滤SQL: " + filterSql);
                return sqlString.toString(); // 部门隔离场景，直接返回
            }
        }

        // 4. 原有部门隔离逻辑（非商家场景保留）
        Set<String> deptIds = new HashSet<>();
        // 获取用户角色对应的部门权限（原有逻辑）
        for (SysRole role : loginUser.getUser().getRoles())
        {
            if (StringUtils.equals(role.getStatus(), UserConstants.ROLE_NORMAL))
            {
                deptIds.addAll(Arrays.asList(StringUtils.split(role.getDataScope(), ",")));
            }
        }

        // 部门ID过滤（仅当配置了deptAlias时才拼接）
        if (StringUtils.isNotBlank(deptAlias) && !deptIds.isEmpty())
        {
            sqlString.append(" AND ").append(deptAlias).append(".dept_id IN (");
            sqlString.append(StringUtils.join(deptIds, ","));
            sqlString.append(")");
        }

        return sqlString.toString();
    }

    /**
     * 拼接权限sql前先清空params.dataScope参数防止注入
     */
    private void clearDataScope(final JoinPoint joinPoint)
    {
        Object params = joinPoint.getArgs()[0];
        if (StringUtils.isNotNull(params) && params instanceof BaseEntity)
        {
            BaseEntity baseEntity = (BaseEntity) params;
            baseEntity.getParams().put(DATA_SCOPE, "");
        }
    }
}

