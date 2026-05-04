package com.project.demo.controller;

import com.project.demo.entity.QuestionBank;
import com.project.demo.entity.LevelStarRecord;
import com.project.demo.service.QuestionBankService;
import com.project.demo.controller.base.BaseController;
import com.project.demo.util.ValidationResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 题目库控制器
 */
@Slf4j
@RestController
@RequestMapping("/question_bank")
public class QuestionBankController extends BaseController<QuestionBank, QuestionBankService> {

    private static final int NEXT_LEVEL_UNLOCK_STARS = 9;

    @Autowired
    public QuestionBankController(QuestionBankService service) {
        setService(service);
    }

    /**
     * 获取关卡题目列表（排除已答对的题目）
     * GET /question_bank/get_level_questions?gamerId=1&levelId=1&trackId=1
     * 
     * 必填参数：
     * - gamerId: 玩家ID（必填，必须>0）
     * - levelId: 关卡ID（必填，必须>0）
     * - trackId: 赛道ID（必填，必须>0）
     */
    @GetMapping("/get_level_questions")
    public Map<String, Object> getLevelQuestions(
            @RequestParam(required = false) Integer gamerId,
            @RequestParam(required = false) Integer levelId,
            @RequestParam(required = false) Integer trackId) {
        try {
            // 统一收集所有校验错误
            ValidationResult validation = new ValidationResult();
            if (gamerId == null || gamerId <= 0) {
                validation.addError("玩家ID不能为空且必须大于0");
            }
            if (levelId == null || levelId <= 0) {
                validation.addError("关卡ID不能为空且必须大于0");
            }
            if (trackId == null || trackId <= 0) {
                validation.addError("赛道ID不能为空且必须大于0");
            }
            
            // 如果有校验错误，统一返回
            if (!validation.isValid()) {
                return error(400, validation.getErrorMessage());
            }
            
            List<QuestionBank> questions = service.getLevelQuestions(gamerId, levelId, trackId);
            
            // 检查是否所有题目都已答完
            boolean isCompleted = service.isLevelCompleted(gamerId, levelId, trackId);
            
            // 检查题库中是否有题目（用于区分"题库无题目"和"达到解锁条件"）
            int totalQuestionCount = service.getLevelQuestionCount(levelId, trackId);
            LevelStarRecord starRecord = service.getLevelStarRecord(gamerId, levelId, trackId);
            int totalStars = 0;
            if (starRecord != null && starRecord.getTotal_stars() != null) {
                totalStars = starRecord.getTotal_stars();
            }

            Map<String, Object> result = new HashMap<>();
            result.put("questions", questions);
            result.put("isCompleted", isCompleted);
            result.put("questionCount", questions.size());
            result.put("totalQuestionCount", totalQuestionCount);
            result.put("totalStars", totalStars);

            boolean allThreeStars = totalQuestionCount > 0 && questions.isEmpty();
            result.put("allThreeStars", allThreeStars);

            log.info("关卡题目查询结果: levelId={}, trackId={}, totalQuestionCount={}, returnedQuestions={}, allThreeStars={}",
                    levelId, trackId, totalQuestionCount, questions.size(), allThreeStars);

            if (allThreeStars) {
                result.put("message", "小朋友你也太厉害了，前往下一关吧！");
                log.info("关卡{}累计星星数已达{}，提示前往下一关", levelId, NEXT_LEVEL_UNLOCK_STARS);
            }
            
            return success(result);
        } catch (IllegalArgumentException e) {
            log.warn("参数校验失败: {}", e.getMessage());
            return error(400, e.getMessage());
        } catch (Exception e) {
            log.error("获取关卡题目失败: {}", e.getMessage(), e);
            return error(500, "获取题目失败: " + e.getMessage());
        }
    }

    /**
     * 提交答案
     * POST /question_bank/submit_answer
     * Body: {
     *   "gamerId": 1,          // 必填，必须>0
     *   "questionId": 1,       // 必填，必须>0
     *   "levelId": 1,          // 必填，必须>0
     *   "trackId": 1,          // 必填，必须>0
     *   "userAnswer": "{\"answer\":\"系一片鹿皮\"}",  // 必填，不能为空
     *   "answerTime": 5.5      // 必填，必须>=0
     * }
     */
    @PostMapping("/submit_answer")
    @Transactional
    public Map<String, Object> submitAnswer(HttpServletRequest request) throws IOException {
        try {
            Map<String, Object> paramMap = service.readBody(request.getReader());
            
            // 统一收集所有校验错误
            ValidationResult validation = new ValidationResult();
            
            // 检查参数是否存在
            if (paramMap.get("gamerId") == null) {
                validation.addError("玩家ID不能为空");
            }
            if (paramMap.get("questionId") == null) {
                validation.addError("题目ID不能为空");
            }
            if (paramMap.get("levelId") == null) {
                validation.addError("关卡ID不能为空");
            }
            if (paramMap.get("trackId") == null) {
                validation.addError("赛道ID不能为空");
            }
            if (paramMap.get("userAnswer") == null || paramMap.get("userAnswer").toString().trim().isEmpty()) {
                validation.addError("玩家答案不能为空");
            }
            if (paramMap.get("answerTime") == null) {
                validation.addError("答题耗时不能为空");
            }
            
            // 如果有校验错误，统一返回
            if (!validation.isValid()) {
                return error(400, validation.getErrorMessage());
            }
            
            // 解析参数并校验格式
            Integer gamerId = null;
            Integer questionId = null;
            Integer levelId = null;
            Integer trackId = null;
            Double answerTime = null;
            
            try {
                gamerId = Integer.parseInt(paramMap.get("gamerId").toString());
                if (gamerId <= 0) {
                    validation.addError("玩家ID必须大于0");
                }
            } catch (NumberFormatException e) {
                validation.addError("玩家ID格式错误");
            }
            
            try {
                questionId = Integer.parseInt(paramMap.get("questionId").toString());
                if (questionId <= 0) {
                    validation.addError("题目ID必须大于0");
                }
            } catch (NumberFormatException e) {
                validation.addError("题目ID格式错误");
            }
            
            try {
                levelId = Integer.parseInt(paramMap.get("levelId").toString());
                if (levelId <= 0) {
                    validation.addError("关卡ID必须大于0");
                }
            } catch (NumberFormatException e) {
                validation.addError("关卡ID格式错误");
            }
            
            try {
                trackId = Integer.parseInt(paramMap.get("trackId").toString());
                if (trackId <= 0) {
                    validation.addError("赛道ID必须大于0");
                }
            } catch (NumberFormatException e) {
                validation.addError("赛道ID格式错误");
            }
            
            try {
                answerTime = Double.parseDouble(paramMap.get("answerTime").toString());
                if (answerTime < 0) {
                    validation.addError("答题耗时必须大于等于0");
                }
            } catch (NumberFormatException e) {
                validation.addError("答题耗时格式错误");
            }
            
            String userAnswer = paramMap.get("userAnswer").toString();
            
            // 如果格式校验有错误，统一返回
            if (!validation.isValid()) {
                return error(400, validation.getErrorMessage());
            }
            
            boolean isCorrect = service.saveAnswerRecord(gamerId, questionId, levelId, trackId, userAnswer, answerTime);
            
            Map<String, Object> result = new HashMap<>();
            result.put("isCorrect", isCorrect);
            result.put("message", isCorrect ? "回答正确！" : "回答错误，请再想想～");
            
            return success(result);
        } catch (IllegalArgumentException e) {
            log.warn("参数校验失败: {}", e.getMessage());
            return error(400, e.getMessage());
        } catch (Exception e) {
            log.error("提交答案失败: {}", e.getMessage(), e);
            return error(500, "提交答案失败: " + e.getMessage());
        }
    }

    /**
     * 完成关卡并计算星级
     * POST /question_bank/complete_level
     * Body: {
     *   "gamerId": 1,        // 必填，必须>0
     *   "levelId": 1,         // 必填，必须>0
     *   "trackId": 1,         // 必填，必须>0
     *   "totalTime": 65.5,     // 必填，必须>=0
     *   "correctCount": 10    // 必填，必须>=0
     * }
     */
    @PostMapping("/complete_level")
    @Transactional
    public Map<String, Object> completeLevel(HttpServletRequest request) throws IOException {
        try {
            Map<String, Object> paramMap = service.readBody(request.getReader());
            
            // 统一收集所有校验错误
            ValidationResult validation = new ValidationResult();
            
            // 检查参数是否存在
            if (paramMap.get("gamerId") == null) {
                validation.addError("玩家ID不能为空");
            }
            if (paramMap.get("levelId") == null) {
                validation.addError("关卡ID不能为空");
            }
            if (paramMap.get("trackId") == null) {
                validation.addError("赛道ID不能为空");
            }
            if (paramMap.get("totalTime") == null) {
                validation.addError("总耗时不能为空");
            }
            if (paramMap.get("correctCount") == null) {
                validation.addError("答对题数不能为空");
            }
            
            // 如果有校验错误，统一返回
            if (!validation.isValid()) {
                return error(400, validation.getErrorMessage());
            }
            
            // 解析参数并校验格式
            Integer gamerId = null;
            Integer levelId = null;
            Integer trackId = null;
            Double totalTime = null;
            Integer correctCount = null;
            
            try {
                gamerId = Integer.parseInt(paramMap.get("gamerId").toString());
                if (gamerId <= 0) {
                    validation.addError("玩家ID必须大于0");
                }
            } catch (NumberFormatException e) {
                validation.addError("玩家ID格式错误");
            }
            
            try {
                levelId = Integer.parseInt(paramMap.get("levelId").toString());
                if (levelId <= 0) {
                    validation.addError("关卡ID必须大于0");
                }
            } catch (NumberFormatException e) {
                validation.addError("关卡ID格式错误");
            }
            
            try {
                trackId = Integer.parseInt(paramMap.get("trackId").toString());
                if (trackId <= 0) {
                    validation.addError("赛道ID必须大于0");
                }
            } catch (NumberFormatException e) {
                validation.addError("赛道ID格式错误");
            }
            
            try {
                totalTime = Double.parseDouble(paramMap.get("totalTime").toString());
                if (totalTime < 0) {
                    validation.addError("总耗时必须大于等于0");
                }
            } catch (NumberFormatException e) {
                validation.addError("总耗时格式错误");
            }
            
            try {
                correctCount = Integer.parseInt(paramMap.get("correctCount").toString());
                if (correctCount < 0) {
                    validation.addError("答对题数必须大于等于0");
                }
            } catch (NumberFormatException e) {
                validation.addError("答对题数格式错误");
            }
            
            // 如果格式校验有错误，统一返回
            if (!validation.isValid()) {
                return error(400, validation.getErrorMessage());
            }
            
            int stars = service.calculateAndSaveStars(gamerId, levelId, trackId, totalTime, correctCount);
            
            // 获取星级记录
            LevelStarRecord starRecord = service.getLevelStarRecord(gamerId, levelId, trackId);
            
            Map<String, Object> result = new HashMap<>();
            result.put("stars", stars);
            result.put("totalStars", starRecord != null ? starRecord.getTotal_stars() : stars);
            result.put("bestTime", starRecord != null ? starRecord.getBest_time() : totalTime);
            result.put("bestStars", starRecord != null ? starRecord.getBest_stars() : stars);
            result.put("completeCount", starRecord != null ? starRecord.getComplete_count() : 1);
            
            // 根据星级给出反馈
            String feedback = "";
            if (stars == 3) {
                feedback = "太厉害了！1分钟内完成，获得3颗星！";
            } else if (stars == 2) {
                feedback = "不错！1分半内完成，获得2颗星！";
            } else if (stars == 1) {
                feedback = "很好！2分钟内完成，获得1颗星！";
            } else {
                feedback = "继续努力，争取获得更多星星！";
            }
            result.put("feedback", feedback);
            
            return success(result);
        } catch (IllegalArgumentException e) {
            log.warn("参数校验失败: {}", e.getMessage());
            return error(400, e.getMessage());
        } catch (Exception e) {
            log.error("完成关卡失败: {}", e.getMessage(), e);
            return error(500, "完成关卡失败: " + e.getMessage());
        }
    }

    /**
     * 获取关卡星级记录
     * GET /question_bank/get_star_record?gamerId=1&levelId=1
     * 
     * 必填参数：
     * - gamerId: 玩家ID（必填，必须>0）
     * - levelId: 关卡ID（必填，必须>0）
     * - trackId: 赛道ID（必填，必须>0）
     */
    @GetMapping("/get_star_record")
    public Map<String, Object> getStarRecord(
            @RequestParam(required = false) Integer gamerId,
            @RequestParam(required = false) Integer levelId,
            @RequestParam(required = false) Integer trackId) {
        try {
            // 统一收集所有校验错误
            ValidationResult validation = new ValidationResult();
            if (gamerId == null || gamerId <= 0) {
                validation.addError("玩家ID不能为空且必须大于0");
            }
            if (levelId == null || levelId <= 0) {
                validation.addError("关卡ID不能为空且必须大于0");
            }
            if (trackId == null || trackId <= 0) {
                validation.addError("赛道ID不能为空且必须大于0");
            }
            
            // 如果有校验错误，统一返回
            if (!validation.isValid()) {
                return error(400, validation.getErrorMessage());
            }
            
            LevelStarRecord starRecord = service.getLevelStarRecord(gamerId, levelId, trackId);
            
            if (starRecord == null) {
                Map<String, Object> emptyResult = new HashMap<>();
                emptyResult.put("totalStars", 0);
                emptyResult.put("bestTime", 0);
                emptyResult.put("bestStars", 0);
                emptyResult.put("completeCount", 0);
                return success(emptyResult);
            }
            
            return success(starRecord);
        } catch (IllegalArgumentException e) {
            log.warn("参数校验失败: {}", e.getMessage());
            return error(400, e.getMessage());
        } catch (Exception e) {
            log.error("获取星级记录失败: {}", e.getMessage(), e);
            return error(500, "获取星级记录失败: " + e.getMessage());
        }
    }

    /**
     * 检查关卡是否已完成所有题目
     * GET /question_bank/check_completed?gamerId=1&levelId=1&trackId=1
     *
     * 必填参数：
     * - gamerId: 玩家ID（必填，必须>0）
     * - levelId: 关卡ID（必填，必须>0）
     * - trackId: 赛道ID（可选，传了会按赛道精确判断题库）
     */
    @GetMapping("/check_completed")
    public Map<String, Object> checkCompleted(
            @RequestParam(required = false) Integer gamerId,
            @RequestParam(required = false) Integer levelId,
            @RequestParam(required = false) Integer trackId) {
        try {
            // 统一收集所有校验错误
            ValidationResult validation = new ValidationResult();
            if (gamerId == null || gamerId <= 0) {
                validation.addError("玩家ID不能为空且必须大于0");
            }
            if (levelId == null || levelId <= 0) {
                validation.addError("关卡ID不能为空且必须大于0");
            }
            if (trackId != null && trackId <= 0) {
                validation.addError("赛道ID必须大于0");
            }

            // 如果有校验错误，统一返回
            if (!validation.isValid()) {
                return error(400, validation.getErrorMessage());
            }

            boolean isCompleted = service.isLevelCompleted(gamerId, levelId, trackId);

            Map<String, Object> result = new HashMap<>();
            result.put("isCompleted", isCompleted);
            if (isCompleted) {
                result.put("message", "你已经很厉害了，前往下一关吧！");
            }

            return success(result);
        } catch (IllegalArgumentException e) {
            log.warn("参数校验失败: {}", e.getMessage());
            return error(400, e.getMessage());
        } catch (Exception e) {
            log.error("检查完成状态失败: {}", e.getMessage(), e);
            return error(500, "检查完成状态失败: " + e.getMessage());
        }
    }

    /**
     * 获取下一题（忽略当前未完成的题目，用于换一题功能）
     * GET /question_bank/get_next_question?gamerId=1&levelId=1&trackId=1
     * 
     * 必填参数：
     * - gamerId: 玩家ID（必填，必须>0）
     * - levelId: 关卡ID（必填，必须>0）
     * - trackId: 赛道ID（必填，必须>0）
     */
    @GetMapping("/get_next_question")
    public Map<String, Object> getNextQuestion(
            @RequestParam(required = false) Integer gamerId,
            @RequestParam(required = false) Integer levelId,
            @RequestParam(required = false) Integer trackId) {
        try {
            // 统一收集所有校验错误
            ValidationResult validation = new ValidationResult();
            if (gamerId == null || gamerId <= 0) {
                validation.addError("玩家ID不能为空且必须大于0");
            }
            if (levelId == null || levelId <= 0) {
                validation.addError("关卡ID不能为空且必须大于0");
            }
            if (trackId == null || trackId <= 0) {
                validation.addError("赛道ID不能为空且必须大于0");
            }
            
            // 如果有校验错误，统一返回
            if (!validation.isValid()) {
                return error(400, validation.getErrorMessage());
            }
            
            List<QuestionBank> questions = service.getNextQuestion(gamerId, levelId, trackId);
            
            // 检查是否所有题目都已答完
            boolean isCompleted = service.isLevelCompleted(gamerId, levelId, trackId);
            
            // 检查题库中是否有题目（用于区分"题库无题目"和"达到解锁条件"）
            int totalQuestionCount = service.getLevelQuestionCount(levelId, trackId);
            LevelStarRecord starRecord = service.getLevelStarRecord(gamerId, levelId, trackId);
            int totalStars = 0;
            if (starRecord != null && starRecord.getTotal_stars() != null) {
                totalStars = starRecord.getTotal_stars();
            }

            Map<String, Object> result = new HashMap<>();
            result.put("questions", questions);
            result.put("isCompleted", isCompleted);
            result.put("questionCount", questions.size());
            result.put("totalQuestionCount", totalQuestionCount);
            result.put("totalStars", totalStars);

            boolean allThreeStars = totalQuestionCount > 0 && questions.isEmpty();
            result.put("allThreeStars", allThreeStars);

            log.info("下一题查询结果: levelId={}, trackId={}, totalQuestionCount={}, returnedQuestions={}, allThreeStars={}",
                    levelId, trackId, totalQuestionCount, questions.size(), allThreeStars);

            if (allThreeStars) {
                result.put("message", "小朋友你也太厉害了，前往下一关吧！");
                log.info("关卡{}累计星星数已达{}，提示前往下一关", levelId, NEXT_LEVEL_UNLOCK_STARS);
            }
            
            return success(result);
        } catch (IllegalArgumentException e) {
            log.warn("参数校验失败: {}", e.getMessage());
            return error(400, e.getMessage());
        } catch (Exception e) {
            log.error("获取下一题失败: {}", e.getMessage(), e);
            return error(500, "获取题目失败: " + e.getMessage());
        }
    }
}
