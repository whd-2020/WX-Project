package com.project.demo.controller;

import com.project.demo.entity.Achievements;
import com.project.demo.service.AchievementsService;
import com.project.demo.controller.base.BaseController;
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
 * 玩家成就：(Achievements)表控制层
 *
 */
@RestController
@RequestMapping("/achievements")
public class AchievementsController extends BaseController<Achievements, AchievementsService> {

    /**
     * 玩家成就对象
     */
    @Autowired
    public AchievementsController(AchievementsService service) {
        setService(service);
    }



    @PostMapping("/add")
    @Transactional
    public Map<String, Object> add(HttpServletRequest request) throws IOException {
        Map<String,Object> paramMap = service.readBody(request.getReader());
        this.addMap(paramMap);
        String sql = "SELECT MAX(achievements_id) AS max FROM "+"`achievements`";
        Integer max = service.selectBaseCount(sql);
        sql = "UPDATE `gamer` INNER JOIN `achievements` ON gamer.player_screen_name=achievements.player_screen_name SET gamer.gold_coin_balance= gamer.gold_coin_balance + achievements.gold_coin_rewards WHERE achievements.achievements_id="+max;
        service.updateBaseSql(sql);
        return success(1);
    }


}
