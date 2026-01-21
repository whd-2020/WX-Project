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
 * 道具商店：(Props)表实体类
 *
 */
@TableName("`props`")
@Data
@EqualsAndHashCode(callSuper = false)
public class Props implements Serializable {

    // Props编号
    @TableId(value = "props_id", type = IdType.AUTO)
    private Integer props_id;

    // 道具名称
    @TableField(value = "`prop_name`")
    private String prop_name;
    // 道具图片
    @TableField(value = "`props_picture`")
    private String props_picture;
    // 所需金币
    @TableField(value = "`gold_coins_required`")
    private String gold_coins_required;
    // 道具用途
    @TableField(value = "`props_use`")
    private String props_use;



















		// 购买限制次数
	@TableField(value = "purchase_record_limit_times")
	private String purchase_record_limit_times;
	
			


	// 更新时间
    @TableField(value = "update_time")
    private Timestamp update_time;

    // 创建时间
    @TableField(value = "create_time")
    private Timestamp create_time;

}
