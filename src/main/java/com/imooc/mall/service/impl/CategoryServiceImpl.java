package com.imooc.mall.service.impl;


import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.imooc.mall.common.ApiRestResponse;
import com.imooc.mall.exception.ImoocMallException;
import com.imooc.mall.exception.ImoocMallExceptionEnum;
import com.imooc.mall.model.dao.CategoryMapper;
import com.imooc.mall.model.pojo.Category;
import com.imooc.mall.model.request.AddCategoryReq;
import com.imooc.mall.model.vo.CategoryVO;
import com.imooc.mall.service.CategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 目录分类实现类
 */
@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;


    /**
     * 新增目录
     * @param addCategoryReq
     */
    @Override
    public void add(AddCategoryReq addCategoryReq){
        Category category = new Category();
        //将addCategoryReq的属性放到category中
        BeanUtils.copyProperties(addCategoryReq,category);
        Category categoryOld = categoryMapper.selectByName(category.getName());
        //不允许重名
        if(categoryOld != null){
            throw new ImoocMallException(ImoocMallExceptionEnum.NAME_EXISTED);
        }
        category.setCreateTime(new Date());
//        category.setOrderNum((int)Math.random());
        int count = categoryMapper.insertSelective(category);
        if (count == 0){
            throw new ImoocMallException(ImoocMallExceptionEnum.CREATE_FAILED);
        }
    }

    /**
     * 更新目录
     * @param updateCategory
     */
    @Override
    public void update(Category updateCategory){
        if(updateCategory.getName() != null){
            Category categoryOld = categoryMapper.selectByName(updateCategory.getName());
            //目录重名判断；从数据库里查询出来的id和传入的id不同，但是目录名相同，抛出异常，原因：admin可以修改自己的目录内容
            if(categoryOld != null && !categoryOld.getId().equals(updateCategory.getId())){
                throw new ImoocMallException(ImoocMallExceptionEnum.NAME_EXISTED);
            }
            //对数据进行更新的操作
            int count = categoryMapper.updateByPrimaryKeySelective(updateCategory);
            if(count == 0){
                throw new ImoocMallException(ImoocMallExceptionEnum.UPDATE_FAILED);
            }
        }
    }

    /**
     * 删除目录
     * @param id
     */
    @Override
    public void delete(Integer id){
        //根据id查找记录
        Category categoryOld = categoryMapper.selectByPrimaryKey(id);
        //如果查不到记录，无法删除
        if(categoryOld == null){
            throw new ImoocMallException(ImoocMallExceptionEnum.DELETE_FAILED);
        }
        //能查到，删除数据
        int count = categoryMapper.deleteByPrimaryKey(id);
        //更新数据为0，更新失败
        if(count == 0){
            throw new ImoocMallException(ImoocMallExceptionEnum.DELETE_FAILED);
        }
    }

    /**
     * 目录列表（给管理员看）
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public PageInfo listCategoryForAdmin(@RequestParam Integer pageNum, @RequestParam Integer pageSize){
        //PageHelper是mybatis 提供的分页插件
        PageHelper.startPage(pageNum,pageSize,"type,order_num");//引号里面的是排序，先按照type，再按照order_num
        List<Category> categoryList = categoryMapper.selectList();//selectList查询所有
        PageInfo pageInfo = new PageInfo(categoryList);//PageInfo是返回给前端的一个类型
        return pageInfo;
    }

    /**
     * 目录列表（给用户看）
     * @return
     */
    @Override
    public List<CategoryVO> listCategoryForCustomer(Integer parentId){
        ArrayList<CategoryVO> categoryVOList = new ArrayList<>();
        recursivelyFindCategories(categoryVOList,parentId);// parentId=0，查询所有目录
        return categoryVOList;
    }

    /**
     * 递归获取所有子类别，并组合成一个”目录树“
     * @param categoryVOList
     * @param parentId
     */
    private void recursivelyFindCategories(List<CategoryVO> categoryVOList,
                                           Integer parentId){
        //获取到parent_id = #{parentId}的目录列表，得到的类型是Category
        List<Category> categoryList = categoryMapper.selectCategoriesByParentId(parentId);
        //判空
        if(!CollectionUtils.isEmpty(categoryList)){
            for(int i = 0; i < categoryList.size(); i++){
                Category category = categoryList.get(i);//第i个
                CategoryVO categoryVO = new CategoryVO();
                //将categoryVO中除了childCategory字段赋值
                BeanUtils.copyProperties(category,categoryVO);
                categoryVOList.add(categoryVO);
                //将childCategory（是一个列表）赋值
                recursivelyFindCategories(categoryVO.getChildCategory(),categoryVO.getId());
            }
        }
    }
}
