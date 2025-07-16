package com.xinletu.web.controller.system;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xinletu.common.constant.Constants;
import com.xinletu.common.constant.HttpStatus;
import com.xinletu.common.core.domain.AjaxResult;
import com.xinletu.common.core.domain.entity.SysMenu;
import com.xinletu.common.core.domain.entity.SysUser;
import com.xinletu.common.core.domain.model.LoginBody;
import com.xinletu.common.core.domain.model.LoginUser;
import com.xinletu.common.core.text.Convert;
import com.xinletu.common.exception.ServiceException;
import com.xinletu.common.utils.DateUtils;
import com.xinletu.common.utils.MessageUtils;
import com.xinletu.common.utils.SecurityUtils;
import com.xinletu.common.utils.StringUtils;
import com.xinletu.framework.web.service.SysLoginService;
import com.xinletu.framework.web.service.SysPermissionService;
import com.xinletu.framework.web.service.TokenService;
import com.xinletu.system.service.ISysConfigService;
import com.xinletu.system.service.ISysMenuService;

/**
 * 登录验证
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/admin")
public class SysLoginController
{
    private static final Logger log = LoggerFactory.getLogger(SysLoginController.class);
    
    @Autowired
    private SysLoginService loginService;

    @Autowired
    private ISysMenuService menuService;

    @Autowired
    private SysPermissionService permissionService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private ISysConfigService configService;

    /**
     * 登录方法
     * 
     * @param loginBody 登录信息
     * @return 结果
     */
    @PostMapping("/login")
    public AjaxResult login(@RequestBody LoginBody loginBody)
    {
        AjaxResult ajax = AjaxResult.success();
        // 生成令牌
        String token = loginService.login(loginBody.getUsername(), loginBody.getPassword(), loginBody.getCode(),
                loginBody.getUuid());
        ajax.put(Constants.TOKEN, token);
        return ajax;
    }

    /**
     * 获取用户信息
     * 
     * @return 用户信息
     */
    @GetMapping("/getInfo")
    public AjaxResult getInfo()
    {
        try {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            if (loginUser == null) {
                log.warn("获取用户信息失败：未登录或登录状态已过期");
                throw new ServiceException("获取用户信息异常，未登录或登录状态已过期", HttpStatus.UNAUTHORIZED);
            }
            
            SysUser user = loginUser.getUser();
            // 角色集合
            Set<String> roles = permissionService.getRolePermission(user);
            // 权限集合
            Set<String> permissions = permissionService.getMenuPermission(user);
            if (!loginUser.getPermissions().equals(permissions))
            {
                loginUser.setPermissions(permissions);
                tokenService.refreshToken(loginUser);
            }
            AjaxResult ajax = AjaxResult.success();
            ajax.put("user", user);
            ajax.put("roles", roles);
            ajax.put("permissions", permissions);
            ajax.put("isDefaultModifyPwd", initPasswordIsModify(user.getPwdUpdateDate()));
            ajax.put("isPasswordExpired", passwordIsExpiration(user.getPwdUpdateDate()));
            return ajax;
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            log.error("获取用户信息异常", e);
            throw new ServiceException("获取用户信息异常，请联系管理员", HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * 获取路由信息
     * 
     * @return 路由信息
     */
    @GetMapping("/getRouters")
    public AjaxResult getRouters()
    {
        Long userId = SecurityUtils.getUserId();
        List<SysMenu> menus = menuService.selectMenuTreeByUserId(userId);
        return AjaxResult.success(menuService.buildMenus(menus));
    }
    
    /**
     * 退出登录
     * 
     * @return 结果
     */
    @PostMapping("/logout")
    public AjaxResult logout()
    {
        try {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            if (StringUtils.isNotNull(loginUser))
            {
                String userName = loginUser.getUsername();
                // 删除用户缓存记录
                tokenService.delLoginUser(loginUser.getToken());
            }
        } catch (Exception e) {
            log.warn("退出登录时发生异常，但将继续执行退出操作", e);
        }
        return AjaxResult.success(MessageUtils.message("user.logout.success"));
    }

    // 检查初始密码是否提醒修改
    public boolean initPasswordIsModify(Date pwdUpdateDate)
    {
        Integer initPasswordModify = Convert.toInt(configService.selectConfigByKey("sys.account.initPasswordModify"));
        return initPasswordModify != null && initPasswordModify == 1 && pwdUpdateDate == null;
    }

    // 检查密码是否过期
    public boolean passwordIsExpiration(Date pwdUpdateDate)
    {
        Integer passwordValidateDays = Convert.toInt(configService.selectConfigByKey("sys.account.passwordValidateDays"));
        if (passwordValidateDays != null && passwordValidateDays > 0)
        {
            if (StringUtils.isNull(pwdUpdateDate))
            {
                // 如果从未修改过初始密码，直接提醒过期
                return true;
            }
            Date nowDate = DateUtils.getNowDate();
            return DateUtils.differentDaysByMillisecond(nowDate, pwdUpdateDate) > passwordValidateDays;
        }
        return false;
    }
}
