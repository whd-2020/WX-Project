package com.project.demo.controller;

import com.project.demo.entity.GameLevels;
import com.project.demo.service.GameLevelsService;
import com.project.demo.controller.base.BaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.*;


/**
 * 游戏关卡：(GameLevels)表控制层
 */
@Slf4j
@RestController
@RequestMapping("/game_levels")
public class GameLevelsController extends BaseController<GameLevels, GameLevelsService> {

    /**
     * 游戏关卡对象
     */
    @Autowired
    public GameLevelsController(GameLevelsService service) {
        setService(service);
    }

    @PostMapping("/add")
    @Transactional
    public Map<String, Object> add(HttpServletRequest request) throws IOException {
        Map<String,Object> paramMap = service.readBody(request.getReader());
        this.addMap(paramMap);
        return success(1);
    }

    /**
     * 获取玩家在指定赛道的关卡进度
     * GET /game_levels/get_progress?gamerId=1&trackId=1
     */
    @GetMapping("/get_progress")
    public Map<String, Object> getProgress(
            @RequestParam(required = false) Integer gamerId,
            @RequestParam(required = false) Integer trackId) {
        try {
            if (gamerId == null || gamerId <= 0) {
                return error(400, "玩家ID不能为空且必须大于0");
            }
            if (trackId == null || trackId <= 0) {
                return error(400, "赛道ID不能为空且必须大于0");
            }

            List<Map<String, Object>> levels = service.getLevelProgress(gamerId, trackId);

            Map<String, Object> result = new HashMap<>();
            result.put("levels", levels);

            return success(result);
        } catch (Exception e) {
            log.error("获取关卡进度失败: {}", e.getMessage(), e);
            return error(500, "获取关卡进度失败: " + e.getMessage());
        }
    }
}
