package com.project.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 题目库：(QuestionBank)表实体类
 */
@TableName("`question_bank`")
@Data
@EqualsAndHashCode(callSuper = false)
public class QuestionBank implements Serializable {

    // 题目ID
    @TableId(value = "question_id", type = IdType.AUTO)
    private Integer question_id;

    // 所属赛道ID
    @TableField(value = "`track_id`")
    private Integer track_id;

    // 所属关卡ID
    @TableField(value = "`level_id`")
    private Integer level_id;


    // 题目标题/描述
    @TableField(value = "`question_title`")
    private String question_title;

    // 题目内容（JSON格式）
    @TableField(value = "`question_content`")
    private String question_content;

    // 正确答案（JSON格式）
    @TableField(value = "`correct_answer`")
    private String correct_answer;


    // 难度等级
    @TableField(value = "`difficulty`")
    private Integer difficulty;

    // 排序顺序
    @TableField(value = "`sort_order`")
    private Integer sort_order;

    // 是否启用
    @TableField(value = "`is_enabled`")
    private Integer is_enabled;

    // 创建时间
    @TableField(value = "`create_time`")
    private Timestamp create_time;

    // 更新时间
    @TableField(value = "`update_time`")
    private Timestamp update_time;
}
