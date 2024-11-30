package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DishFlavorMapper {

    /**
     * 批量插入口味数据
     * @param flavors
     */
    void insertBatch(List<DishFlavor> flavors);

    /**
     * 修改口味信息
     * @param flavors
     */
    void updateBatch(List<DishFlavor> flavors);

    /**
     * 根据dishId查询
     * @param id
     * @return
     */
    List<DishFlavor> getByDishId(Long id);

    /**
     * 单条插入口味信息
     * @param flavor
     */
    void insert(DishFlavor flavor);

    /**
     * 根据id删除口味
     * @param id
     */
    void delete(Long id);
}
