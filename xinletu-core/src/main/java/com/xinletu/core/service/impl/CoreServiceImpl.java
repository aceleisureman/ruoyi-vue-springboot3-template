package com.xinletu.core.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xinletu.core.domain.CoreEntity;
import com.xinletu.core.mapper.CoreMapper;
import com.xinletu.core.service.ICoreService;

/**
 * 核心业务服务实现
 * 
 * @author xinletu
 */
@Service
public class CoreServiceImpl implements ICoreService {
    @Autowired
    private CoreMapper coreMapper;

    /**
     * 查询核心业务数据列表
     * 
     * @param coreEntity 核心业务数据
     * @return 核心业务数据
     */
    @Override
    public List<CoreEntity> selectCoreList(CoreEntity coreEntity) {
        return coreMapper.selectCoreList(coreEntity);
    }

    /**
     * 查询核心业务数据
     * 
     * @param id 核心业务数据主键
     * @return 核心业务数据
     */
    @Override
    public CoreEntity selectCoreById(Long id) {
        return coreMapper.selectCoreById(id);
    }

    /**
     * 新增核心业务数据
     * 
     * @param coreEntity 核心业务数据
     * @return 结果
     */
    @Override
    public int insertCore(CoreEntity coreEntity) {
        return coreMapper.insertCore(coreEntity);
    }

    /**
     * 修改核心业务数据
     * 
     * @param coreEntity 核心业务数据
     * @return 结果
     */
    @Override
    public int updateCore(CoreEntity coreEntity) {
        return coreMapper.updateCore(coreEntity);
    }

    /**
     * 批量删除核心业务数据
     * 
     * @param ids 需要删除的核心业务数据主键
     * @return 结果
     */
    @Override
    public int deleteCoreByIds(Long[] ids) {
        return coreMapper.deleteCoreByIds(ids);
    }

    /**
     * 删除核心业务数据信息
     * 
     * @param id 核心业务数据主键
     * @return 结果
     */
    @Override
    public int deleteCoreById(Long id) {
        return coreMapper.deleteCoreById(id);
    }
} 