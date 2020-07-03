package com.example.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.common.BasePO;
import lombok.Data;

/**
 */
@Data
@TableName(value = "blog")
public class BlogPO extends BasePO {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String name; // 文章标题
    private String authorName; // 文章作者ID
}
