package com.atguigu.gulimall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.constant.RedissonLockConstant;
import com.atguigu.gulimall.product.service.CategoryBrandRelationService;
import com.atguigu.gulimall.product.vo.Catalog2Vo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.product.dao.CategoryDao;
import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.CategoryService;
import org.springframework.transaction.annotation.Transactional;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

//    @Autowired
//    CategoryDao categoryDao;

    @Autowired
    CategoryBrandRelationService categoryBrandRelationService;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private RedissonClient redissonClient ;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {
        //1、查出所有分类
        List<CategoryEntity> entities = baseMapper.selectList(null);

        //2、组装成父子的树形结构

        //2.1）、找到所有的一级分类
        List<CategoryEntity> level1Menus = entities.stream().filter(categoryEntity ->
                categoryEntity.getParentCid() == 0
        ).map((menu) -> {
            menu.setChildren(getChildrens(menu, entities));
            return menu;
        }).sorted((menu1, menu2) -> {
            return (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
        }).collect(Collectors.toList());


        return level1Menus;
    }

    @Override
    public void removeMenuByIds(List<Long> asList) {
        //TODO  1、检查当前删除的菜单，是否被别的地方引用

        //逻辑删除
        baseMapper.deleteBatchIds(asList);
    }

    //[2,25,225]
    @Override
    public Long[] findCatelogPath(Long catelogId) {
        List<Long> paths = new ArrayList<>();
        List<Long> parentPath = findParentPath(catelogId, paths);

        Collections.reverse(parentPath);


        return parentPath.toArray(new Long[parentPath.size()]);
    }

    /**
     * 级联更新所有关联的数据
     *
     * @param category
     */
    @Transactional
    @Override
    public void updateCascade(CategoryEntity category) {
        this.updateById(category);
        categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());
    }

    @Override
    public List<CategoryEntity> getLevel1CategoryCategory() {
        return baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("cat_level", 1));
    }

    @Override
    public Map<Long, List<Catalog2Vo>> getCatalogJson() {

        // 优化数据查询，首先从数据库中查询所有的cateGory数据
        // 先从缓存中数据数据,分布式锁的颗粒度一定要保持的比较小
        RLock lock = redissonClient.getLock(RedissonLockConstant.CATALOG_JSON_LOCK) ;
        try {
            lock.lock() ;
            String category = redisTemplate.opsForValue().get("categorys");
            if (StringUtils.isEmpty(category)) {
                Map<Long, List<Catalog2Vo>> cat = getDataFormDB();
                redisTemplate.opsForValue().set("categorys", JSON.toJSONString(cat), 1, TimeUnit.DAYS);
                return cat;
            } else {
                Map<Long, List<Catalog2Vo>> longListMap = JSON.parseObject(category,
                        new TypeReference<Map<Long, List<Catalog2Vo>>>() {
                        });
                return longListMap;
            }
        }finally {
            lock.unlock();
        }



    }

    private Map<Long, List<Catalog2Vo>> getDataFormDB() {
        List<CategoryEntity> entities = baseMapper.selectList(new QueryWrapper<CategoryEntity>());
        //1:先获取所有一级分类的信息
        List<CategoryEntity> level1Cat = getLevel1CategoryCategory();
        Map<Long, List<Catalog2Vo>> map = null;
        if (CollectionUtils.isNotEmpty(level1Cat)) {
            map = level1Cat.stream().collect(Collectors.toMap(k -> k.getCatId(), v -> {
                List<CategoryEntity> level2Cat = getCataGoryByParentCid(entities, v.getCatId()); //性能优化
                //baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", v.getCatId()));
                List<Catalog2Vo> collect = null;
                if (CollectionUtils.isNotEmpty(level2Cat)) {
                    collect = level2Cat.stream().map(item -> {
                        Catalog2Vo catalog2Vo = new Catalog2Vo();
                        catalog2Vo.setCatalog1Id(v.getCatId().toString());
                        catalog2Vo.setId(item.getCatId().toString());
                        catalog2Vo.setName(item.getName());
                        //获取三级分类的信息
                        List<CategoryEntity> level3Cat = getCataGoryByParentCid(entities, item.getCatId());
                        //baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid",item.getCatId())) ;
                        if (CollectionUtils.isNotEmpty(level3Cat)) {
                            List<Catalog2Vo.Catalog3Vo> collect3 = level3Cat.stream().map((entity) -> {
                                Catalog2Vo.Catalog3Vo catalog3Vo = new Catalog2Vo.Catalog3Vo();
                                catalog3Vo.setCatalog2Id(entity.getCatId().toString());
                                catalog3Vo.setId(entity.getCatId().toString());
                                catalog3Vo.setName(entity.getName());
                                return catalog3Vo;
                            }).collect(Collectors.toList());
                            catalog2Vo.setCatalog3List(collect3);
                        }
                        return catalog2Vo;
                    }).collect(Collectors.toList());
                }
                return collect;
            }));
        }

        return map;
    }

    /**
     * 根据parentCid获取分类信息
     *
     * @param entities
     * @param parentCid
     * @return
     */
    private List<CategoryEntity> getCataGoryByParentCid(List<CategoryEntity> entities, Long parentCid) {
        return entities.stream().filter(item -> item.getParentCid().equals(parentCid)).collect(Collectors.toList());
    }

    //225,25,2
    private List<Long> findParentPath(Long catelogId, List<Long> paths) {
        //1、收集当前节点id
        paths.add(catelogId);
        CategoryEntity byId = this.getById(catelogId);
        if (byId.getParentCid() != 0) {
            findParentPath(byId.getParentCid(), paths);
        }
        return paths;

    }


    //递归查找所有菜单的子菜单
    private List<CategoryEntity> getChildrens(CategoryEntity root, List<CategoryEntity> all) {

        List<CategoryEntity> children = all.stream().filter(categoryEntity -> {
            return categoryEntity.getParentCid() == root.getCatId();
        }).map(categoryEntity -> {
            //1、找到子菜单
            categoryEntity.setChildren(getChildrens(categoryEntity, all));
            return categoryEntity;
        }).sorted((menu1, menu2) -> {
            //2、菜单的排序
            return (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
        }).collect(Collectors.toList());

        return children;
    }


}