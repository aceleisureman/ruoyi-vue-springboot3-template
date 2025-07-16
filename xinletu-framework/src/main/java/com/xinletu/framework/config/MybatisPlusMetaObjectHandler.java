package com.xinletu.framework.config;

import java.util.Date;

import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.xinletu.common.utils.SecurityUtils;

/**
 * MyBatis Plus 自动填充处理器
 * 
 * @author xinletu
 */
@Component
public class MybatisPlusMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        try {
            // 创建时间
            if (metaObject.hasSetter("createTime")) {
                this.strictInsertFill(metaObject, "createTime", Date.class, new Date());
            }
            
            // 创建者
            if (metaObject.hasSetter("createBy")) {
                this.strictInsertFill(metaObject, "createBy", String.class, getUsername());
            }
            
            // 更新时间
            if (metaObject.hasSetter("updateTime")) {
                this.strictInsertFill(metaObject, "updateTime", Date.class, new Date());
            }
            
            // 更新者
            if (metaObject.hasSetter("updateBy")) {
                this.strictInsertFill(metaObject, "updateBy", String.class, getUsername());
            }
        } catch (Exception e) {
            // 忽略异常，确保正常执行
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        try {
            // 更新时间
            if (metaObject.hasSetter("updateTime")) {
                this.strictUpdateFill(metaObject, "updateTime", Date.class, new Date());
            }
            
            // 更新者
            if (metaObject.hasSetter("updateBy")) {
                this.strictUpdateFill(metaObject, "updateBy", String.class, getUsername());
            }
        } catch (Exception e) {
            // 忽略异常，确保正常执行
        }
    }
    
    /**
     * 获取当前登录用户名
     */
    private String getUsername() {
        try {
            return SecurityUtils.getUsername();
        } catch (Exception e) {
            return "";
        }
    }
} 