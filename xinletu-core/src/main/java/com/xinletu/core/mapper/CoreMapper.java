package com.xinletu.core.mapper;

import java.util.List;

import com.xinletu.core.domain.CoreEntity;

/**
 * Core数据层
 * 
 * @author xinletu
 */
public interface CoreMapper {
    /**
     * 查询核心业务数据列表
     * 
     * @param coreEntity 核心业务数据
     * @return 核心业务数据集合
     */
    public List<CoreEntity> selectCoreList(CoreEntity coreEntity);
    
    /**
     * 查询核心业务数据
     *
     * @param id 核心业务数据主键
     * @return 核心业务数据
     */
    public CoreEntity selectCoreById(Long id);
    
    /**
     * 新增核心业务数据
     * 
     * @param coreEntity 核心业务数据
     * @return 结果
     */
    public int insertCore(CoreEntity coreEntity);
    
    /**
     * 修改核心业务数据
     * 
     * @param coreEntity 核心业务数据
     * @return 结果
     */
    public int updateCore(CoreEntity coreEntity);
    
    /**
     * 删除核心业务数据
     * 
     * @param id 核心业务数据主键
     * @return 结果
     */
    public int deleteCoreById(Long id);
    
    /**
     * 批量删除核心业务数据
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCoreByIds(Long[] ids);
} 