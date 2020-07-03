package com.example.service.impl;

import com.example.common.OpenapiServiceImpl;
import com.example.domain.BlogPO;
import com.example.mapper.BlogMapper;
import com.example.service.BlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * @ClassName BlogServiceImpl
 **/
@Service
public class BlogServiceImpl extends OpenapiServiceImpl<BlogMapper, BlogPO> implements BlogService {

    @Autowired
    private BlogMapper blogMapper;



}
