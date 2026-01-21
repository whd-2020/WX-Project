package com.project.demo.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.io.Serializable;
import java.sql.Timestamp;


/**
 * 购买记录：(PurchaseRecord)表实体类
 *
 */
@TableName("`purchase_record`")
@Data
@EqualsAndHashCode(callSuper = false)
public class PurchaseRecord implements Serializable {

    // PurchaseRecord编号
    @TableId(value = "purchase_record_id", type = IdType.AUTO)
    private Integer purchase_record_id;

    // 购买用户
    @TableField(value = "`purchase_user`")
    private Integer purchase_user;
    // 玩家网名
    @TableField(value = "`player_screen_name`")
    private String player_screen_name;
    // 道具名称
    @TableField(value = "`prop_name`")
    private String prop_name;
    // 道具图片
    @TableField(value = "`props_picture`")
    private String props_picture;
    // 道具用途
    @TableField(value = "`props_use`")
    private String props_use;
    // 所需金币
    @TableField(value = "`gold_coins_required`")
    private String gold_coins_required;
    // 使用状态
    @TableField(value = "`use_status`")
    private String use_status;




















		// 来源表
	@TableField(value = "source_table")
	private String source_table;
	
	// 来源ID
	@TableField(value = "source_id")
	private Integer source_id;
	
	// 来源用户ID
	@TableField(value = "source_user_id")
	private Integer source_user_id;
			


	// 更新时间
    @TableField(value = "update_time")
    private Timestamp update_time;

    // 创建时间
    @TableField(value = "create_time")
    private Timestamp create_time;

}
