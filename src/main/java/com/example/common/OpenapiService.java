package com.example.common;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Collection;

public interface OpenapiService<T extends BasePO> extends IService<T> {


    /**
     * 保存
     *
     * @param entity
     * @return
     */
    @Override
    boolean save(T entity);


    /**
     * 批量保存
     *
     * @param entityList
     * @return
     */
    @Override
    boolean saveBatch(Collection<T> entityList);


    @Override
    boolean update(T t, Wrapper<T> wrapper);

    /**
     * 更新(根据id)
     *
     * @param entity
     * @return
     */
    @Override
    boolean updateById(T entity);


    /**
     * 批量更新
     *
     * @param entityList
     * @return
     */
    @Override
    boolean updateBatchById(Collection<T> entityList);


}
