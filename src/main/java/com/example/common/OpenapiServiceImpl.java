package com.example.common;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;


import java.util.Collection;

/**
 * 项目自定义的ServiceImpl
 */
public class OpenapiServiceImpl<M extends BaseMapper<T>, T extends BasePO> extends ServiceImpl<M, T> implements OpenapiService<T> {

    /**
     * 保存
     *
     * @param entity
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean save(T entity) {
        return super.save(entity);
    }


    /**
     * 批量保存
     *
     * @param entityList
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveBatch(Collection<T> entityList) {
        if (entityList == null || entityList.size() == 0) {
            return false;
        }
        return super.saveBatch(entityList);
    }

    /**
     * 更新(根据指定条件)
     *
     * @param entity
     * @param updateWrapper
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean update(T entity, Wrapper<T> updateWrapper) {
        return super.update(entity, updateWrapper);
    }


    /**
     * 更新(根据id)
     *
     * @param entity
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateById(T entity) {
        return super.updateById(entity);
    }


    /**
     * 批量更新
     *
     * @param entityList
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateBatchById(Collection<T> entityList) {
        if (entityList == null || entityList.size() == 0) {
            return false;
        }
        return super.updateBatchById(entityList);
    }



}


