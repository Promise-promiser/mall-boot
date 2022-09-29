package com.imooc.mall.controller;

import com.github.pagehelper.PageInfo;
import com.imooc.mall.common.ApiRestResponse;
import com.imooc.mall.common.Constant;
import com.imooc.mall.exception.ImoocMallExceptionEnum;
import com.imooc.mall.model.pojo.Category;
import com.imooc.mall.model.pojo.User;
import com.imooc.mall.model.request.AddCategoryReq;
import com.imooc.mall.model.request.UpdateCategoryReq;
import com.imooc.mall.model.vo.CategoryVO;
import com.imooc.mall.service.CategoryService;
import com.imooc.mall.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Date;
import java.util.List;

@Controller
//@RequestMapping("admin")
public class CategoryController {

    @Autowired
    private UserService userService;
    @Autowired
    private CategoryService categoryService;

    /**
     * 后台添加商品分类；需要检验 1.是否登录 2.是否为管理员
     * @param session
     * @param addCategoryReq name type parentId orderNum
     * @return
     */
    @ApiOperation("后台添加商品分类")
    @PostMapping("admin/category/add")
    @ResponseBody
    //此处使用AddCategoryReq里的@Size,@NotNull,@Max;和上面的@Valid
    public ApiRestResponse addCategory(HttpSession session,
                                       @Valid @RequestBody AddCategoryReq addCategoryReq) {
        //1.从session中获取用户信息
        User currentUser = (User) session.getAttribute(Constant.IMOOC_MALL_USER);
        if (currentUser == null) {
            return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_LOGIN);
        }
        //2.检查是否是管理员
        boolean adminRole = userService.checkAdminRole(currentUser);
        if (adminRole) {
            //是管理员执行添加目录操作
            categoryService.add(addCategoryReq);
            return ApiRestResponse.success();
        }else
            {
            //不是管理员，提醒需要登录管理员
            return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_ADMIN);
        }

    }

    @ApiOperation("后台更改商品分类")
    @PostMapping("admin/category/update")
    @ResponseBody
    public ApiRestResponse updateCategory(@Valid @RequestBody UpdateCategoryReq updateCategoryReq,HttpSession session){
        //1.从session中获取用户信息
        User currentUser = (User)session.getAttribute(Constant.IMOOC_MALL_USER);
        if (currentUser == null){
            return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_LOGIN);
        }
        //2.检查是否是管理员
        boolean adminRole = userService.checkAdminRole(currentUser);
        if(adminRole){
            //是管理员执行添加目录操作
            Category category = new Category();
            //将updateCategoryReq里的对应属性拷贝到category中（小-->大）
            BeanUtils.copyProperties(updateCategoryReq,category);
            category.setUpdateTime(new Date());
            categoryService.update(category);
            return ApiRestResponse.success();
        }else{
            //不是管理员，提醒需要登录管理员
            return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_ADMIN);
        }
    }

    @ApiOperation("后台删除商品分类")
    @PostMapping("admin/category/delete")
    @ResponseBody
    public ApiRestResponse deleteCategory(@RequestParam Integer id ){
        categoryService.delete(id);
        return ApiRestResponse.success();
    }

    @ApiOperation("后台目录列表")//给管理员看的
    @PostMapping("admin/category/list")
    @ResponseBody
    public ApiRestResponse listCategoryForAdmin(@RequestParam Integer pageNum,
                                                @RequestParam Integer pageSize){
        PageInfo pageInfo = categoryService.listCategoryForAdmin(pageNum,pageSize);
        return ApiRestResponse.success(pageInfo);
    }

    @ApiOperation("前台目录列表")
    @PostMapping("category/list")
    @ResponseBody
    public ApiRestResponse listCategoryForCustomer(){
        //CategoryVO是返回前端的实体类
        List<CategoryVO> categoryVOList = categoryService.listCategoryForCustomer(0);
        return ApiRestResponse.success(categoryVOList);
    }
}
