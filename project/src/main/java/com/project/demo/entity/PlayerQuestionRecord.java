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
 * 玩家答题记录：(PlayerQuestionRecord)表实体类
 */
@TableName("`player_question_record`")
@Data
@EqualsAndHashCode(callSuper = false)
public class PlayerQuestionRecord implements Serializable {

    // 记录ID
    @TableId(value = "record_id", type = IdType.AUTO)
    private Integer record_id;

    // 玩家ID
    @TableField(value = "`gamer_id`")
    private Integer gamer_id;

    // 题目ID
    @TableField(value = "`question_id`")
    private Integer question_id;

    // 关卡ID
    @TableField(value = "`level_id`")
    private Integer level_id;

    // 赛道ID
    @TableField(value = "`track_id`")
    private Integer track_id;

    // 玩家答案（JSON格式）
    @TableField(value = "`user_answer`")
    private String user_answer;

    // 是否正确
    @TableField(value = "`is_correct`")
    private Integer is_correct;

    // 答题耗时（秒）
    @TableField(value = "`answer_time`")
    private Double answer_time;

    // 答题时间
    @TableField(value = "`create_time`")
    private Timestamp create_time;

    // 更新时间
    @TableField(value = "`update_time`")
    private Timestamp update_time;
}
