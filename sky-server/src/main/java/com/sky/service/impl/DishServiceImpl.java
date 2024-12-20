package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;


    /**
     * 新增菜品和口味
     * @param dishDTO
     */
    @Transactional
    @Override
    public void saveWithFlavor(DishDTO dishDTO) {
        //插入一条菜品数据
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.insert(dish);

        //获取dish插入的主键值
        Long dishId = dish.getId();

        //插入多条口味数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && flavors.size() > 0) {
            flavors.forEach(flavor -> flavor.setDishId(dishId));
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        //使用PageHelper完成分页查询，传入页数和每页大小
        //原理：存储页数和每页大小，当执行SQL查询时拦截
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        //拦截SQL查询,将limit和offset等拼接上去，再将查询结果包装成Page类
        Page<Dish> page = dishMapper.pageQuery(dishPageQueryDTO);

        PageResult pageResult = new PageResult();
        //查询结果赋值
        pageResult.setTotal(page.getTotal());
        pageResult.setRecords(page.getResult());
        return pageResult;
    }

    /**
     * 修改菜品
     * @param dishDTO
     */
    @Override
    @Transactional
    public void update(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);

        //更新菜品信息
        if (dish.getId() != null) {
            dishMapper.update(dish);
        }


        //获取数据库与更新口味信息，进行比较
        List<DishFlavor> flavors = dishDTO.getFlavors();
        List<DishFlavor> existingFlavors = dishFlavorMapper.getByDishId(dish.getId());

        //已存在的口味更新，不存在的口味插入，去掉的口味删除
        if (flavors != null && !flavors.isEmpty()) {
            //获取插入集合
            List<DishFlavor> newFlavors = flavors.stream()
                    .filter(flavor -> flavor.getId() == null)
                    .collect(Collectors.toList());

            //获取更新集合
            List<DishFlavor> updateFlavors = flavors.stream()
                    .filter(flavor -> flavor.getId() != null)
                    .collect(Collectors.toList());

            //获取更新集合的id
            Set<Long> newFlavorIds = updateFlavors.stream()
                    .map(DishFlavor::getId)
                    .collect(Collectors.toSet());

            //删除不需要的口味
            for (DishFlavor flavor : existingFlavors) {
                if (!newFlavorIds.contains(flavor.getId())) {
                    dishFlavorMapper.delete(flavor.getId());
                }
            }

            //获取dish插入的主键值
            Long dishId = dish.getId();

            //批量插入
            if (!newFlavors.isEmpty()) {
                newFlavors.forEach(flavor -> flavor.setDishId(dishId));
                dishFlavorMapper.insertBatch(newFlavors);
            }

            //批量更新
            if (!updateFlavors.isEmpty()) {
                updateFlavors.forEach(flavor -> dishFlavorMapper.update(flavor));
            }
        }
    }

    /**
     * 根据id查询菜品
     * @param id
     * @return
     */
    @Override
    public DishVO getDishById(Long id) {
        //查询菜品信息
        Dish dish = dishMapper.getDishById(id);
        DishVO dishVO = new DishVO();
        //dish赋值给dishVO
        BeanUtils.copyProperties(dish, dishVO);

        List<DishFlavor> list = dishFlavorMapper.getByDishId(id);

        //口味信息赋值给dishVO
        dishVO.setFlavors(list);
        return dishVO;
    }

    /**
     * 批量删除菜品
     * @param ids
     */
    @Override
    public void deleteBatch(String ids) {
        //将字符串提取为集合
        String[] str = ids.split(",");
        List<Long> idsList = Arrays.stream(str)
                .map(Long::parseLong)
                .collect(Collectors.toList());

        //批量删除菜品
        dishMapper.deleteBatch(idsList);

        //根据dishId删除口味
        idsList.forEach(id -> dishFlavorMapper.deleteByDishId(id));
    }

    /**
     * 更改菜品售卖状态
     * @param status
     * @param id
     */
    @Override
    public void updateStatus(int status, String id) {
        Dish dish = new Dish();
        dish.setStatus(status);
        //将字符串转换为Long
        dish.setId(Long.valueOf(id));
        dishMapper.update(dish);
    }

    /**
     * 条件查询菜品和口味
     * @param dish
     * @return
     */
    public List<DishVO> listWithFlavor(Dish dish) {
        List<Dish> dishList = dishMapper.list(dish);

        List<DishVO> dishVOList = new ArrayList<>();

        for (Dish d : dishList) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(d,dishVO);

            //根据菜品id查询对应的口味
            List<DishFlavor> flavors = dishFlavorMapper.getByDishId(d.getId());

            dishVO.setFlavors(flavors);
            dishVOList.add(dishVO);
        }

        return dishVOList;
    }

    /**
     * 根据分类id查询菜品
     * @param categoryId
     * @return
     */
    @Override
    public List<Dish> getByCategoryId(Long categoryId) {

        List<Dish> list = dishMapper.getByCategoryId(categoryId);
        return list;
    }
}
