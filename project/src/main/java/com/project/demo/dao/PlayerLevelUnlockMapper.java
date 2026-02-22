package com.project.demo.dao;

import com.project.demo.dao.base.BaseMapper;
import com.project.demo.entity.PlayerLevelUnlock;
import org.apache.ibatis.annotations.Mapper;

/**
 * 玩家关卡解锁状态：(PlayerLevelUnlock)Mapper接口
 */
@Mapper
public interface PlayerLevelUnlockMapper extends BaseMapper<PlayerLevelUnlock> {

}
