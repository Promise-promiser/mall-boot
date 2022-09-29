package com.imooc.mall.controller;

import com.imooc.mall.common.ApiRestResponse;
import com.imooc.mall.filter.UserFilter;
import com.imooc.mall.model.vo.CartVO;
import com.imooc.mall.service.CartService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController//返回json格式
@RequestMapping("cart") //相当于再所有url前面加上cart路径
public class CartController {

    @Autowired
    CartService cartService;

    @GetMapping("/list")
    @ApiOperation("购物车列表")
    public ApiRestResponse list(){//@RequestParam方便与url进行绑定
        //内部获取用户ID，防止横向越权
        List<CartVO> cartList = cartService.list(UserFilter.currentUser.getId());
        return ApiRestResponse.success(cartList);
    }


    @PostMapping("/add")
    @ApiOperation("添加商品到购物车")
    public ApiRestResponse add(@RequestParam Integer productId,@RequestParam Integer count){//@RequestParam方便与url进行绑定
        List<CartVO> cartVOList = cartService.add(UserFilter.currentUser.getId(),productId,count);
        return ApiRestResponse.success(cartVOList);
    }

    @PostMapping("/update")
    @ApiOperation("更改购物车")
    public ApiRestResponse update(@RequestParam Integer productId,@RequestParam Integer count){//@RequestParam方便与url进行绑定
        List<CartVO> cartVOList = cartService.update(UserFilter.currentUser.getId(),productId,count);
        return ApiRestResponse.success(cartVOList);
    }

    @PostMapping("/delete")
    @ApiOperation("删除购物车")
    public ApiRestResponse delete(@RequestParam Integer productId){//@RequestParam方便与url进行绑定
        //不能 **传入** userId,cartId,否则可以删除别人的购物车
        List<CartVO> cartVOList = cartService.delete(UserFilter.currentUser.getId(),productId);
        return ApiRestResponse.success(cartVOList);
    }

    @PostMapping("/select")
    @ApiOperation("选择/不选择购物车的某商品")
    public ApiRestResponse select(@RequestParam Integer productId,@RequestParam Integer selected){//@RequestParam方便与url进行绑定
        List<CartVO> cartVOList = cartService.selectOrNot(UserFilter.currentUser.getId(),productId,selected);
        return ApiRestResponse.success(cartVOList);
    }

    @PostMapping("/selectAll")
    @ApiOperation("全择/全不选购物车的某商品")
    public ApiRestResponse selectAll(@RequestParam Integer selected){//@RequestParam方便与url进行绑定
        List<CartVO> cartVOList = cartService.selectAllOrNot(UserFilter.currentUser.getId(),selected);
        return ApiRestResponse.success(cartVOList);
    }
}
