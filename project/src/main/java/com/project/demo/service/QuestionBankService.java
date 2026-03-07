package com.project.demo.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.project.demo.dao.QuestionBankMapper;
import com.project.demo.dao.PlayerQuestionRecordMapper;
import com.project.demo.dao.LevelStarRecordMapper;
import com.project.demo.dao.PlayerLevelUnlockMapper;
import com.project.demo.dao.GameLevelsMapper;
import com.project.demo.entity.QuestionBank;
import com.project.demo.entity.PlayerQuestionRecord;
import com.project.demo.entity.LevelStarRecord;
import com.project.demo.entity.PlayerLevelUnlock;
import com.project.demo.entity.GameLevels;
import com.project.demo.service.base.BaseService;
import com.project.demo.util.ValidationResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 题目库服务类
 */
@Slf4j
@Service
public class QuestionBankService extends BaseService<QuestionBank> {

    @Autowired
    private QuestionBankMapper questionBankMapper;

    @Autowired
    private PlayerQuestionRecordMapper playerQuestionRecordMapper;

    @Autowired
    private LevelStarRecordMapper levelStarRecordMapper;

    @Autowired
    private PlayerLevelUnlockMapper playerLevelUnlockMapper;

    @Autowired
    private GameLevelsMapper gameLevelsMapper;

    /**
     * 校验必填字段（收集所有错误）
     */
    private ValidationResult validateRequiredFields(Integer gamerId, Integer levelId, Integer trackId) {
        ValidationResult result = new ValidationResult();
        
        if (gamerId == null || gamerId <= 0) {
            result.addError("玩家ID不能为空且必须大于0");
        }
        if (levelId == null || levelId <= 0) {
            result.addError("关卡ID不能为空且必须大于0");
        }
        if (trackId == null || trackId <= 0) {
            result.addError("赛道ID不能为空且必须大于0");
        }
        
        return result;
    }

    /**
     * 校验提交答案的必填字段
     */
    private ValidationResult validateAnswerFields(Integer gamerId, Integer questionId, Integer levelId, 
                                                   Integer trackId, String userAnswer, Double answerTime) {
        ValidationResult result = validateRequiredFields(gamerId, levelId, trackId);
        
        if (questionId == null || questionId <= 0) {
            result.addError("题目ID不能为空且必须大于0");
        }
        if (userAnswer == null || userAnswer.trim().isEmpty()) {
            result.addError("玩家答案不能为空");
        }
        if (answerTime == null || answerTime < 0) {
            result.addError("答题耗时不能为空且必须大于等于0");
        }
        
        return result;
    }

    /**
     * 校验完成关卡的必填字段
     */
    private ValidationResult validateCompleteLevelFields(Integer gamerId, Integer levelId, Integer trackId,
                                                          Double totalTime, Integer correctCount) {
        ValidationResult result = validateRequiredFields(gamerId, levelId, trackId);
        
        if (totalTime == null || totalTime < 0) {
            result.addError("总耗时不能为空且必须大于等于0");
        }
        if (correctCount == null || correctCount < 0) {
            result.addError("答对题数不能为空且必须大于等于0");
        }
        
        return result;
    }

    /**
     * 获取关卡题目
     *
     * 规则说明（结合前端“结绳计数”等关卡的需求）:
     * 1. 先看玩家最近一次在本关卡的作答记录：
     *    - 如果这道题拿到的星级 < 3，则认为玩家还在“挑战这道题”，再次进入关卡仍返回这道题；
     *    - 如果这道题已经拿到 3 星，则从题库中抽取一题新的题目。
     * 2. 抽新题时，会排除掉所有已经拿到 3 星的题目，保证“满星题目不再出现”。
     *
     * 星级规则（单题）：
     *   - 回答错误             -> 0 星
     *   - 回答正确且耗时 <= 60s -> 3 星
     *   - 回答正确且耗时 <=120s -> 2 星
     *   - 回答正确且耗时  >120s -> 1 星
     *
     * @param gamerId 玩家ID
     * @param levelId 关卡ID
     * @param trackId 赛道ID
     * @return 题目列表（当前只会返回 0 或 1 道题，前端再做本地方向的随机和兜底）
     */
    public List<QuestionBank> getLevelQuestions(Integer gamerId, Integer levelId, Integer trackId) {
        // 校验必填字段
        ValidationResult validation = validateRequiredFields(gamerId, levelId, trackId);
        if (!validation.isValid()) {
            throw new IllegalArgumentException(validation.getErrorMessage());
        }

        // 1. 查询玩家最近一次在本关卡、本赛道的作答记录（用于"继续当前这道题"的进度）
        QueryWrapper<PlayerQuestionRecord> lastRecordWrapper = new QueryWrapper<>();
        lastRecordWrapper.eq("gamer_id", gamerId);
        lastRecordWrapper.eq("level_id", levelId);
        lastRecordWrapper.eq("track_id", trackId);
        lastRecordWrapper.orderByDesc("create_time");
        lastRecordWrapper.last("LIMIT 1");
        PlayerQuestionRecord lastRecord = playerQuestionRecordMapper.selectOne(lastRecordWrapper);

        if (lastRecord != null) {
            int lastStars = calculateStarsForSingleQuestion(lastRecord);
            log.info("最近一次答题记录: questionId={}, isCorrect={}, answerTime={}, 计算星级={}", 
                    lastRecord.getQuestion_id(), lastRecord.getIs_correct(), lastRecord.getAnswer_time(), lastStars);
            
            // 修改逻辑：
            // - lastStars == 0（还没答对）：继续当前这道题，方便孩子再练一次
            // - lastStars >= 1（已经答对拿到 1/2/3 星）：从未满3星的题目中随机抽取新题
            if (lastStars == 0) {
                QuestionBank currentQuestion = questionBankMapper.selectById(lastRecord.getQuestion_id());
                if (currentQuestion != null && currentQuestion.getIs_enabled() != null && currentQuestion.getIs_enabled() == 1) {
                    log.info("继续当前题目（最近一次未拿到星，答题还未通过）: questionId={}", currentQuestion.getQuestion_id());
                    return Collections.singletonList(currentQuestion);
                }
            } else {
                log.info("最近一次答题已获得星星（{} 星），开始从未满3星的题目中随机抽取新题", lastStars);
            }
        }

        // 2. 抽取新的题目：先查出本关卡所有可用题目
        QueryWrapper<QuestionBank> questionWrapper = new QueryWrapper<>();
        questionWrapper.eq("level_id", levelId);
        questionWrapper.eq("track_id", trackId);
        questionWrapper.eq("is_enabled", 1);
        questionWrapper.orderByAsc("sort_order");
        List<QuestionBank> allQuestions = questionBankMapper.selectList(questionWrapper);

        if (allQuestions.isEmpty()) {
            return Collections.emptyList();
        }

        // 3. 找出已经拿到 3 星的题目（这些题目不再出现）
        QueryWrapper<PlayerQuestionRecord> threeStarWrapper = new QueryWrapper<>();
        threeStarWrapper.eq("gamer_id", gamerId);
        threeStarWrapper.eq("level_id", levelId);
        threeStarWrapper.eq("track_id", trackId);
        threeStarWrapper.eq("is_correct", 1);
        threeStarWrapper.le("answer_time", 60);
        List<PlayerQuestionRecord> threeStarRecords = playerQuestionRecordMapper.selectList(threeStarWrapper);

        Set<Integer> finishedQuestionIds = threeStarRecords.stream()
                .map(PlayerQuestionRecord::getQuestion_id)
                .collect(Collectors.toSet());
        
        log.info("已满3星的题目ID列表: {}", finishedQuestionIds);
        log.info("本关卡总题目数: {}, 已满3星题目数: {}", allQuestions.size(), finishedQuestionIds.size());

        // 4. 过滤掉已满星题目
        List<QuestionBank> availableQuestions = allQuestions.stream()
                .filter(q -> !finishedQuestionIds.contains(q.getQuestion_id()))
                .collect(Collectors.toList());

        if (availableQuestions.isEmpty()) {
            // 所有题目都已经拿到 3 星，认为本关题目已刷完。
            log.info("所有题目都已满3星，本关卡已完成");
            // 返回空列表，但会在 Controller 层添加特殊标识
            return Collections.emptyList();
        }

        // 5. 从剩余题目中随机抽取 1 道
        Collections.shuffle(availableQuestions);
        QuestionBank selectedQuestion = availableQuestions.get(0);
        log.info("抽取新题: questionId={}, 剩余可选题目数: {}", selectedQuestion.getQuestion_id(), availableQuestions.size());
        return Collections.singletonList(selectedQuestion);
    }

    /**
     * 根据单次作答记录计算这道题的星级（不写库，只在内存里算）
     */
    private int calculateStarsForSingleQuestion(PlayerQuestionRecord record) {
        if (record == null) {
            return 0;
        }
        boolean isCorrect = record.getIs_correct() != null && record.getIs_correct() == 1;
        Double time = record.getAnswer_time();
        if (!isCorrect || time == null) {
            return 0;
        }
        if (time <= 60) {
            return 3;
        } else if (time <= 120) {
            return 2;
        } else {
            return 1;
        }
    }

    /**
     * 更新关卡星星记录，累计星星数，并在累计星星数>20时解锁下一关
     * @param gamerId 玩家ID
     * @param levelId 关卡ID
     * @param trackId 赛道ID
     * @param stars 本次获得的星星数
     * @param answerTime 答题耗时（秒）
     */
    @Transactional
    private void updateLevelStarRecord(Integer gamerId, Integer levelId, Integer trackId, int stars, Double answerTime) {
        if (stars <= 0) {
            return; // 没有获得星星，不更新
        }

        // 查询或创建关卡星星记录
        QueryWrapper<LevelStarRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("gamer_id", gamerId);
        wrapper.eq("level_id", levelId);
        LevelStarRecord starRecord = levelStarRecordMapper.selectOne(wrapper);

        java.sql.Timestamp now = new java.sql.Timestamp(System.currentTimeMillis());

        if (starRecord == null) {
            // 创建新记录
            starRecord = new LevelStarRecord();
            starRecord.setGamer_id(gamerId);
            starRecord.setLevel_id(levelId);
            starRecord.setTrack_id(trackId);
            starRecord.setTotal_stars(stars);
            starRecord.setBest_time(answerTime);
            starRecord.setBest_stars(stars);
            starRecord.setComplete_count(1);
            starRecord.setLast_complete_time(now);
            levelStarRecordMapper.insert(starRecord);
            log.info("创建关卡星星记录: gamerId={}, levelId={}, stars={}, totalStars={}", 
                    gamerId, levelId, stars, stars);
        } else {
            // 更新记录：累计星星数
            int newTotalStars = (starRecord.getTotal_stars() != null ? starRecord.getTotal_stars() : 0) + stars;
            starRecord.setTotal_stars(newTotalStars);
            
            // 更新最佳时间
            if (answerTime != null && (starRecord.getBest_time() == null || answerTime < starRecord.getBest_time())) {
                starRecord.setBest_time(answerTime);
            }
            
            // 更新最佳星星数
            if (stars > (starRecord.getBest_stars() != null ? starRecord.getBest_stars() : 0)) {
                starRecord.setBest_stars(stars);
            }
            
            // 更新完成次数
            starRecord.setComplete_count((starRecord.getComplete_count() != null ? starRecord.getComplete_count() : 0) + 1);
            starRecord.setLast_complete_time(now);
            levelStarRecordMapper.updateById(starRecord);
            log.info("更新关卡星星记录: gamerId={}, levelId={}, 本次获得={}, 累计总数={}", 
                    gamerId, levelId, stars, newTotalStars);
        }

        // 检查累计星星数是否>=20，如果是则解锁下一关
        int totalStars = starRecord.getTotal_stars() != null ? starRecord.getTotal_stars() : 0;
        if (totalStars >= 20) {
            unlockNextLevel(gamerId, levelId, trackId);
        }
    }

    /**
     * 解锁下一关（当累计星星数>=20时调用）
     * 
     * 注意：
     * 1. 只解锁同一赛道（track_id）内的下一关，不同赛道之间完全隔离
     * 2. 通过 level_order 排序找到下一个关卡，支持新建关卡插入到中间的情况
     * 3. 如果新建关卡插入到当前关卡之后，不会影响当前关卡的解锁逻辑
     * 
     * @param gamerId 玩家ID
     * @param levelId 当前关卡ID（game_levels_id）
     * @param trackId 赛道ID（确保只解锁同一赛道内的关卡）
     */
    @Transactional
    private void unlockNextLevel(Integer gamerId, Integer levelId, Integer trackId) {
        // 查询该赛道所有关卡，找到当前关卡的下一个关卡
        QueryWrapper<GameLevels> levelWrapper = new QueryWrapper<>();
        levelWrapper.eq("track_id", trackId);
        levelWrapper.orderByAsc("level_order");
        List<GameLevels> allLevels = gameLevelsMapper.selectList(levelWrapper);

        // 找到当前关卡在列表中的位置
        int currentIndex = -1;
        for (int i = 0; i < allLevels.size(); i++) {
            if (allLevels.get(i).getGame_levels_id().equals(levelId)) {
                currentIndex = i;
                break;
            }
        }

        // 如果找到当前关卡，且不是最后一关，则解锁下一关
        if (currentIndex >= 0 && currentIndex < allLevels.size() - 1) {
            GameLevels nextLevel = allLevels.get(currentIndex + 1);
            Integer nextLevelId = nextLevel.getGame_levels_id();

            // 查询下一关的解锁记录
            QueryWrapper<PlayerLevelUnlock> nextWrapper = new QueryWrapper<>();
            nextWrapper.eq("gamer_id", gamerId);
            nextWrapper.eq("game_levels_id", nextLevelId);
            PlayerLevelUnlock nextUnlock = playerLevelUnlockMapper.selectOne(nextWrapper);

            if (nextUnlock == null) {
                // 创建下一关解锁记录
                nextUnlock = new PlayerLevelUnlock();
                nextUnlock.setGamer_id(gamerId);
                nextUnlock.setGame_levels_id(nextLevelId);
                nextUnlock.setTrack_id(trackId);
                nextUnlock.setIs_unlocked(1);
                nextUnlock.setIs_completed(0);
                nextUnlock.setComplete_times(0);
                playerLevelUnlockMapper.insert(nextUnlock);
                log.info("累计星星数>20，解锁下一关: gamerId={}, currentLevelId={}, nextLevelId={}, trackId={}", 
                        gamerId, levelId, nextLevelId, trackId);
            } else if (nextUnlock.getIs_unlocked() == null || nextUnlock.getIs_unlocked() == 0) {
                // 如果下一关未解锁，则解锁它
                nextUnlock.setIs_unlocked(1);
                playerLevelUnlockMapper.updateById(nextUnlock);
                log.info("累计星星数>20，解锁下一关: gamerId={}, currentLevelId={}, nextLevelId={}, trackId={}", 
                        gamerId, levelId, nextLevelId, trackId);
            }
        }
    }

    /**
     * 检查题目是否已答完
     * @param gamerId 玩家ID
     * @param levelId 关卡ID
     * @return true-已答完，false-未答完
     */
    public boolean isLevelCompleted(Integer gamerId, Integer levelId) {
        ValidationResult validation = new ValidationResult();
        if (gamerId == null || gamerId <= 0) {
            validation.addError("玩家ID不能为空且必须大于0");
        }
        if (levelId == null || levelId <= 0) {
            validation.addError("关卡ID不能为空且必须大于0");
        }
        if (!validation.isValid()) {
            throw new IllegalArgumentException(validation.getErrorMessage());
        }
        // 查询该关卡所有题目
        QueryWrapper<QuestionBank> questionWrapper = new QueryWrapper<>();
        questionWrapper.eq("level_id", levelId);
        questionWrapper.eq("is_enabled", 1);
        int totalQuestions = questionBankMapper.selectCount(questionWrapper);

        // 查询玩家已答对的题目数
        QueryWrapper<PlayerQuestionRecord> recordWrapper = new QueryWrapper<>();
        recordWrapper.eq("gamer_id", gamerId);
        recordWrapper.eq("level_id", levelId);
        recordWrapper.eq("is_correct", 1);
        int correctCount = playerQuestionRecordMapper.selectCount(recordWrapper);

        return correctCount >= totalQuestions;
    }

    /**
     * 获取关卡题目总数
     * @param levelId 关卡ID
     * @param trackId 赛道ID
     * @return 题目总数
     */
    public int getLevelQuestionCount(Integer levelId, Integer trackId) {
        QueryWrapper<QuestionBank> questionWrapper = new QueryWrapper<>();
        questionWrapper.eq("level_id", levelId);
        questionWrapper.eq("track_id", trackId);
        questionWrapper.eq("is_enabled", 1);
        return questionBankMapper.selectCount(questionWrapper);
    }

    /**
     * 保存答题记录
     * @param gamerId 玩家ID
     * @param questionId 题目ID
     * @param levelId 关卡ID
     * @param trackId 赛道ID
     * @param userAnswer 玩家答案
     * @param answerTime 答题耗时（秒）
     * @return 是否正确
     */
    @Transactional
    public boolean saveAnswerRecord(Integer gamerId, Integer questionId, Integer levelId, 
                                   Integer trackId, String userAnswer, Double answerTime) {
        // 校验必填字段
        ValidationResult validation = validateAnswerFields(gamerId, questionId, levelId, trackId, userAnswer, answerTime);
        if (!validation.isValid()) {
            throw new IllegalArgumentException(validation.getErrorMessage());
        }
        
        // 1. 获取题目正确答案
        QuestionBank question = questionBankMapper.selectById(questionId);
        if (question == null) {
            log.error("题目不存在: questionId={}", questionId);
            return false;
        }

        // 2. 判断答案是否正确（前端主导，后端兜底）
        boolean isCorrect = checkAnswer(question.getCorrect_answer(), userAnswer);

        // 3. 保存答题记录
        PlayerQuestionRecord record = new PlayerQuestionRecord();
        record.setGamer_id(gamerId);
        record.setQuestion_id(questionId);
        record.setLevel_id(levelId);
        record.setTrack_id(trackId);
        record.setUser_answer(userAnswer);
        record.setIs_correct(isCorrect ? 1 : 0);
        record.setAnswer_time(answerTime);
        playerQuestionRecordMapper.insert(record);

        // 4. 如果答对了，计算星星数并累计到关卡星星记录
        if (isCorrect) {
            int stars = calculateStarsForSingleQuestion(record);
            updateLevelStarRecord(gamerId, levelId, trackId, stars, answerTime);
        }

        return isCorrect;
    }

    /**
     * 检查答案是否正确（后端兜底校验）
     * @param correctAnswer 正确答案（JSON字符串）
     * @param userAnswer 玩家答案（JSON字符串）
     * @return 是否正确
     */
    private boolean checkAnswer(String correctAnswer, String userAnswer) {
        try {
            JSONObject correctJson = JSON.parseObject(correctAnswer);
            JSONObject userJson = JSON.parseObject(userAnswer);

            // 比较答案
            String correctValue = correctJson.getString("answer");
            String userValue = userJson.getString("answer");

            // 如果是数组类型（多选题）
            if (correctJson.containsKey("answer") && correctJson.get("answer") instanceof JSONArray) {
                JSONArray correctArray = correctJson.getJSONArray("answer");
                JSONArray userArray = userJson.getJSONArray("answer");
                if (correctArray == null || userArray == null) {
                    return false;
                }
                // 比较数组内容（不考虑顺序）
                Set<Object> correctSet = new HashSet<>(correctArray);
                Set<Object> userSet = new HashSet<>(userArray);
                return correctSet.equals(userSet);
            }

            // 字符串或数字比较
            return correctValue != null && correctValue.equals(userValue);
        } catch (Exception e) {
            log.error("答案校验失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 计算并保存关卡星级
     * @param gamerId 玩家ID
     * @param levelId 关卡ID
     * @param trackId 赛道ID
     * @param totalTime 总耗时（秒）
     * @param correctCount 答对题数
     * @return 获得的星级
     */
    @Transactional
    public int calculateAndSaveStars(Integer gamerId, Integer levelId, Integer trackId, 
                                     Double totalTime, Integer correctCount) {
        // 校验必填字段
        ValidationResult validation = validateCompleteLevelFields(gamerId, levelId, trackId, totalTime, correctCount);
        if (!validation.isValid()) {
            throw new IllegalArgumentException(validation.getErrorMessage());
        }
        // 计算星级（根据规则：1分钟内3星，1分半2星，2分钟1星）
        int stars = 0;
        if (totalTime <= 60) {
            stars = 3;
        } else if (totalTime <= 90) {
            stars = 2;
        } else if (totalTime <= 120) {
            stars = 1;
        }

        // 如果答对题数不足10道，降星
        if (correctCount < 10) {
            stars = Math.max(0, stars - (10 - correctCount));
        }

        // 查询或创建星级记录
        QueryWrapper<LevelStarRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("gamer_id", gamerId);
        wrapper.eq("level_id", levelId);
        LevelStarRecord starRecord = levelStarRecordMapper.selectOne(wrapper);

        if (starRecord == null) {
            // 创建新记录
            starRecord = new LevelStarRecord();
            starRecord.setGamer_id(gamerId);
            starRecord.setLevel_id(levelId);
            starRecord.setTrack_id(trackId);
            starRecord.setTotal_stars(stars);
            starRecord.setBest_time(totalTime);
            starRecord.setBest_stars(stars);
            starRecord.setComplete_count(1);
            starRecord.setLast_complete_time(new java.sql.Timestamp(System.currentTimeMillis()));
            levelStarRecordMapper.insert(starRecord);
        } else {
            // 更新记录
            int newTotalStars = starRecord.getTotal_stars() + stars;
            if (totalTime < starRecord.getBest_time()) {
                starRecord.setBest_time(totalTime);
            }
            if (stars > starRecord.getBest_stars()) {
                starRecord.setBest_stars(stars);
            }
            starRecord.setTotal_stars(newTotalStars);
            starRecord.setComplete_count(starRecord.getComplete_count() + 1);
            starRecord.setLast_complete_time(new java.sql.Timestamp(System.currentTimeMillis()));
            levelStarRecordMapper.updateById(starRecord);
        }

        // 更新关卡解锁状态：标记当前关已完成，并解锁下一关
        updateLevelUnlockStatus(gamerId, levelId, trackId, stars > 0);

        return stars;
    }

    /**
     * 更新关卡解锁状态：标记当前关已完成，并解锁下一关
     * @param gamerId 玩家ID
     * @param levelId 当前关卡ID（game_levels_id）
     * @param trackId 赛道ID
     * @param isCompleted 是否完成（拿到至少1颗星）
     */
    @Transactional
    public void updateLevelUnlockStatus(Integer gamerId, Integer levelId, Integer trackId, boolean isCompleted) {
        if (!isCompleted) {
            // 如果没有完成（没拿到星星），不更新解锁状态
            return;
        }

        // 1. 更新当前关卡的解锁状态为已完成
        QueryWrapper<PlayerLevelUnlock> currentWrapper = new QueryWrapper<>();
        currentWrapper.eq("gamer_id", gamerId);
        currentWrapper.eq("game_levels_id", levelId);
        PlayerLevelUnlock currentUnlock = playerLevelUnlockMapper.selectOne(currentWrapper);

        java.sql.Timestamp now = new java.sql.Timestamp(System.currentTimeMillis());
        
        if (currentUnlock == null) {
            // 创建当前关卡解锁记录
            currentUnlock = new PlayerLevelUnlock();
            currentUnlock.setGamer_id(gamerId);
            currentUnlock.setGame_levels_id(levelId);
            currentUnlock.setTrack_id(trackId);
            currentUnlock.setIs_unlocked(1);
            currentUnlock.setIs_completed(1);
            currentUnlock.setComplete_times(1);
            currentUnlock.setFirst_complete_time(now);
            currentUnlock.setLast_complete_time(now);
            playerLevelUnlockMapper.insert(currentUnlock);
        } else {
            // 更新当前关卡解锁记录
            currentUnlock.setIs_unlocked(1);
            currentUnlock.setIs_completed(1);
            if (currentUnlock.getFirst_complete_time() == null) {
                currentUnlock.setFirst_complete_time(now);
            }
            currentUnlock.setLast_complete_time(now);
            if (currentUnlock.getComplete_times() == null) {
                currentUnlock.setComplete_times(1);
            } else {
                currentUnlock.setComplete_times(currentUnlock.getComplete_times() + 1);
            }
            playerLevelUnlockMapper.updateById(currentUnlock);
        }

        // 2. 解锁下一关
        // 查询该赛道所有关卡，找到当前关卡的下一个关卡
        QueryWrapper<GameLevels> levelWrapper = new QueryWrapper<>();
        levelWrapper.eq("track_id", trackId);
        levelWrapper.orderByAsc("level_order");
        List<GameLevels> allLevels = gameLevelsMapper.selectList(levelWrapper);

        // 找到当前关卡在列表中的位置
        int currentIndex = -1;
        for (int i = 0; i < allLevels.size(); i++) {
            if (allLevels.get(i).getGame_levels_id().equals(levelId)) {
                currentIndex = i;
                break;
            }
        }

        // 如果找到当前关卡，且不是最后一关，则解锁下一关
        if (currentIndex >= 0 && currentIndex < allLevels.size() - 1) {
            GameLevels nextLevel = allLevels.get(currentIndex + 1);
            Integer nextLevelId = nextLevel.getGame_levels_id();

            // 查询下一关的解锁记录
            QueryWrapper<PlayerLevelUnlock> nextWrapper = new QueryWrapper<>();
            nextWrapper.eq("gamer_id", gamerId);
            nextWrapper.eq("game_levels_id", nextLevelId);
            PlayerLevelUnlock nextUnlock = playerLevelUnlockMapper.selectOne(nextWrapper);

            if (nextUnlock == null) {
                // 创建下一关解锁记录
                nextUnlock = new PlayerLevelUnlock();
                nextUnlock.setGamer_id(gamerId);
                nextUnlock.setGame_levels_id(nextLevelId);
                nextUnlock.setTrack_id(trackId);
                nextUnlock.setIs_unlocked(1);
                nextUnlock.setIs_completed(0);
                nextUnlock.setComplete_times(0);
                playerLevelUnlockMapper.insert(nextUnlock);
                log.info("解锁下一关: gamerId={}, levelId={}, trackId={}", gamerId, nextLevelId, trackId);
            } else if (nextUnlock.getIs_unlocked() == null || nextUnlock.getIs_unlocked() == 0) {
                // 如果下一关未解锁，则解锁它
                nextUnlock.setIs_unlocked(1);
                playerLevelUnlockMapper.updateById(nextUnlock);
                log.info("解锁下一关: gamerId={}, levelId={}, trackId={}", gamerId, nextLevelId, trackId);
            }
        }
    }

    /**
     * 获取关卡星级记录
     * @param gamerId 玩家ID
     * @param levelId 关卡ID
     * @return 星级记录
     */
    public LevelStarRecord getLevelStarRecord(Integer gamerId, Integer levelId) {
        ValidationResult validation = new ValidationResult();
        if (gamerId == null || gamerId <= 0) {
            validation.addError("玩家ID不能为空且必须大于0");
        }
        if (levelId == null || levelId <= 0) {
            validation.addError("关卡ID不能为空且必须大于0");
        }
        if (!validation.isValid()) {
            throw new IllegalArgumentException(validation.getErrorMessage());
        }
        QueryWrapper<LevelStarRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("gamer_id", gamerId);
        wrapper.eq("level_id", levelId);
        return levelStarRecordMapper.selectOne(wrapper);
    }
}
