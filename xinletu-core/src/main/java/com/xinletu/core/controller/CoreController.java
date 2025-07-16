package com.xinletu.core.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xinletu.common.annotation.Log;
import com.xinletu.common.core.controller.BaseController;
import com.xinletu.common.core.domain.AjaxResult;
import com.xinletu.common.core.page.TableDataInfo;
import com.xinletu.common.enums.BusinessType;
import com.xinletu.core.domain.CoreEntity;
import com.xinletu.core.service.ICoreService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 核心业务Controller
 * 
 * @author xinletu
 */
@Tag(name = "核心业务管理", description = "核心业务管理接口")
@RestController
@RequestMapping("/admin/core")
public class CoreController extends BaseController {
    @Autowired
    private ICoreService coreService;

    /**
     * 查询核心业务列表
     */
    @Operation(summary = "查询核心业务列表", description = "查询核心业务列表")
    @PreAuthorize("@ss.hasPermi('core:core:list')")
    @GetMapping("/list")
    public TableDataInfo list(CoreEntity core) {
        startPage();
        List<CoreEntity> list = coreService.selectCoreList(core);
        return getDataTable(list);
    }

    /**
     * 获取核心业务详细信息
     */
    @Operation(summary = "获取核心业务详情", description = "获取核心业务详情")
    @PreAuthorize("@ss.hasPermi('core:core:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(coreService.selectCoreById(id));
    }

    /**
     * 新增核心业务
     */
    @Operation(summary = "新增核心业务", description = "新增核心业务")
    @PreAuthorize("@ss.hasPermi('core:core:add')")
    @Log(title = "核心业务", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CoreEntity core) {
        return toAjax(coreService.insertCore(core));
    }

    /**
     * 修改核心业务
     */
    @Operation(summary = "修改核心业务", description = "修改核心业务")
    @PreAuthorize("@ss.hasPermi('core:core:edit')")
    @Log(title = "核心业务", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CoreEntity core) {
        return toAjax(coreService.updateCore(core));
    }

    /**
     * 删除核心业务
     */
    @Operation(summary = "删除核心业务", description = "删除核心业务")
    @PreAuthorize("@ss.hasPermi('core:core:remove')")
    @Log(title = "核心业务", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(coreService.deleteCoreByIds(ids));
    }
} 