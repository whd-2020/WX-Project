package com.project.demo.dao;

import com.project.demo.dao.base.BaseMapper;
import com.project.demo.entity.QuestionBank;
import org.apache.ibatis.annotations.Mapper;

/**
 * 题目库：(QuestionBank)Mapper接口
 */
@Mapper
public interface QuestionBankMapper extends BaseMapper<QuestionBank> {

}
