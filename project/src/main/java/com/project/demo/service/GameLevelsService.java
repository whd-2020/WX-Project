package com.project.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.project.demo.dao.GameLevelsMapper;
import com.project.demo.dao.PlayerLevelUnlockMapper;
import com.project.demo.dao.LevelStarRecordMapper;
import com.project.demo.entity.GameLevels;
import com.project.demo.entity.PlayerLevelUnlock;
import com.project.demo.entity.LevelStarRecord;
import com.project.demo.service.base.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 游戏关卡：(GameLevels)表服务接口
 */
@Slf4j
@Service
public class GameLevelsService extends BaseService<GameLevels> {

    @Autowired
    private GameLevelsMapper gameLevelsMapper;

    @Autowired
    private PlayerLevelUnlockMapper playerLevelUnlockMapper;

    @Autowired
    private LevelStarRecordMapper levelStarRecordMapper;

    /**
     * 获取玩家在指定赛道的关卡进度
     * 
     * 解锁规则：
     * 1. 第一关默认解锁
     * 2. 后续关卡：上一关已解锁 且 上一关累计星星数>=30 时，自动解锁当前关
     * 3. 不同赛道（track_id）之间的关卡完全隔离，互不影响
     * 4. 新建关卡：如果新建关卡插入到中间，只要上一关满足解锁条件，新建关卡也会自动解锁
     * 
     * 字段对应关系：
     * - LevelStarRecord.level_id 存储的是 GameLevels.game_levels_id
     * - PlayerLevelUnlock.game_levels_id 对应 GameLevels.game_levels_id
     * 
     * @param gamerId 玩家ID
     * @param trackId 赛道ID（不同赛道之间完全隔离）
     * @return 关卡进度列表，包含解锁状态、累计星星数等信息
     */
    public List<Map<String, Object>> getLevelProgress(Integer gamerId, Integer trackId) {
        // 1. 查询该赛道所有关卡（按顺序）
        QueryWrapper<GameLevels> levelWrapper = new QueryWrapper<>();
        levelWrapper.eq("track_id", trackId);
        levelWrapper.orderByAsc("level_order");
        List<GameLevels> allLevels = gameLevelsMapper.selectList(levelWrapper);

        // 2. 查询玩家在该赛道的解锁记录
        QueryWrapper<PlayerLevelUnlock> unlockWrapper = new QueryWrapper<>();
        unlockWrapper.eq("gamer_id", gamerId);
        unlockWrapper.eq("track_id", trackId);
        List<PlayerLevelUnlock> unlockRecords = playerLevelUnlockMapper.selectList(unlockWrapper);

        // 3. 构建解锁记录Map，key为game_levels_id
        Map<Integer, PlayerLevelUnlock> unlockMap = unlockRecords.stream()
                .collect(Collectors.toMap(
                        PlayerLevelUnlock::getGame_levels_id,
                        record -> record,
                        (existing, replacement) -> existing
                ));

        // 4. 查询玩家在该赛道的关卡星星记录（用于显示累计星星数）
        QueryWrapper<LevelStarRecord> starWrapper = new QueryWrapper<>();
        starWrapper.eq("gamer_id", gamerId);
        starWrapper.eq("track_id", trackId);
        List<LevelStarRecord> starRecords = levelStarRecordMapper.selectList(starWrapper);
        
        // 构建星星记录Map，key为level_id
        Map<Integer, LevelStarRecord> starMap = starRecords.stream()
                .collect(Collectors.toMap(
                        LevelStarRecord::getLevel_id,
                        record -> record,
                        (existing, replacement) -> existing
                ));
        
        // 5. 遍历关卡列表，计算解锁状态
        List<Map<String, Object>> result = new ArrayList<>();

        for (int i = 0; i < allLevels.size(); i++) {
            GameLevels level = allLevels.get(i);
            PlayerLevelUnlock unlockRecord = unlockMap.get(level.getGame_levels_id());
            LevelStarRecord starRecord = starMap.get(level.getGame_levels_id());

            boolean isUnlocked;
            boolean isCompleted = false;
            int totalStars = 0; // 累计星星数

            // 获取累计星星数
            if (starRecord != null && starRecord.getTotal_stars() != null) {
                totalStars = starRecord.getTotal_stars();
            }

            // 第一关默认解锁
            if (i == 0) {
                isUnlocked = true;
                // 如果数据库中没有记录，创建一条解锁记录
                if (unlockRecord == null) {
                    unlockRecord = new PlayerLevelUnlock();
                    unlockRecord.setGamer_id(gamerId);
                    unlockRecord.setGame_levels_id(level.getGame_levels_id());
                    unlockRecord.setTrack_id(trackId);
                    unlockRecord.setIs_unlocked(1);
                    unlockRecord.setIs_completed(0);
                    unlockRecord.setComplete_times(0);
                    playerLevelUnlockMapper.insert(unlockRecord);
                }
            } else {
                // 后续关卡：检查上一关的累计星星数是否>=30
                // 注意：这里使用 level.getGame_levels_id() 作为 key，因为 LevelStarRecord.level_id 存储的就是 game_levels_id
                GameLevels prevLevel = allLevels.get(i - 1);
                LevelStarRecord prevStarRecord = starMap.get(prevLevel.getGame_levels_id());
                int prevTotalStars = 0;
                if (prevStarRecord != null && prevStarRecord.getTotal_stars() != null) {
                    prevTotalStars = prevStarRecord.getTotal_stars();
                }
                
                // 检查上一关的解锁状态（兼容新建关卡的情况）
                PlayerLevelUnlock prevUnlockRecord = unlockMap.get(prevLevel.getGame_levels_id());
                boolean prevUnlocked = false;
                if (prevUnlockRecord != null && prevUnlockRecord.getIs_unlocked() != null && prevUnlockRecord.getIs_unlocked() == 1) {
                    prevUnlocked = true;
                } else if (i == 1) {
                    // 第二关的前一关是第一关，第一关默认解锁
                    prevUnlocked = true;
                }
                
                // 解锁条件：上一关已解锁 且 上一关累计星星数>=30
                // 这样可以兼容新建关卡的情况：如果新建关卡插入到中间，只要上一关满足条件就能解锁
                if (prevUnlocked && prevTotalStars >= 30) {
                    isUnlocked = true;
                    // 如果数据库中没有解锁记录，创建一条
                    if (unlockRecord == null) {
                        unlockRecord = new PlayerLevelUnlock();
                        unlockRecord.setGamer_id(gamerId);
                        unlockRecord.setGame_levels_id(level.getGame_levels_id());
                        unlockRecord.setTrack_id(trackId);
                        unlockRecord.setIs_unlocked(1);
                        unlockRecord.setIs_completed(0);
                        unlockRecord.setComplete_times(0);
                        playerLevelUnlockMapper.insert(unlockRecord);
                        log.info("根据累计星星数自动解锁关卡: gamerId={}, trackId={}, levelId={}, prevLevelId={}, prevTotalStars={}", 
                                gamerId, trackId, level.getGame_levels_id(), prevLevel.getGame_levels_id(), prevTotalStars);
                    } else if (unlockRecord.getIs_unlocked() == null || unlockRecord.getIs_unlocked() == 0) {
                        // 如果记录存在但未解锁，更新为已解锁
                        unlockRecord.setIs_unlocked(1);
                        playerLevelUnlockMapper.updateById(unlockRecord);
                        log.info("根据累计星星数更新解锁状态: gamerId={}, trackId={}, levelId={}, prevLevelId={}, prevTotalStars={}", 
                                gamerId, trackId, level.getGame_levels_id(), prevLevel.getGame_levels_id(), prevTotalStars);
                    }
                } else {
                    // 上一关未解锁或累计星星数<30，根据PlayerLevelUnlock表中的is_unlocked字段判断
                    // 这样可以兼容已经手动解锁或通过其他方式解锁的情况
                    if (unlockRecord != null && unlockRecord.getIs_unlocked() != null && unlockRecord.getIs_unlocked() == 1) {
                        isUnlocked = true;
                    } else {
                        isUnlocked = false;
                    }
                }
            }

            // 判断是否完成
            if (unlockRecord != null) {
                isCompleted = unlockRecord.getIs_completed() != null && unlockRecord.getIs_completed() == 1;
            }

            // 动态生成状态文本
            String status;
            if (isUnlocked) {
                if (isCompleted) {
                    status = "已完成";
                } else {
                    // 已解锁但未完成，显示累计星星数
                    if (totalStars > 0) {
                        status = totalStars + "星";
                    } else {
                        status = "已解锁";
                    }
                }
            } else {
                // 未解锁，显示当前累计星星数/30
                if (totalStars > 0) {
                    status = totalStars + "/30星";
                } else {
                    status = "未解锁";
                }
            }

            // 构建返回数据
            Map<String, Object> levelInfo = new HashMap<>();
            levelInfo.put("levelId", level.getGame_levels_id());
            levelInfo.put("levelName", level.getLevel_name());
            levelInfo.put("levelOrder", level.getLevel_order());
            levelInfo.put("isUnlocked", isUnlocked ? 1 : 0);
            levelInfo.put("isCompleted", isCompleted ? 1 : 0);
            levelInfo.put("totalStars", totalStars); // 添加累计星星数
            levelInfo.put("status", status); // 动态状态文本
            result.add(levelInfo);
        }

        return result;
    }
}
