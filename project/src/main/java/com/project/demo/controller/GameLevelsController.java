package com.project.demo.controller;

import com.project.demo.entity.GameLevels;
import com.project.demo.service.GameLevelsService;
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
 * 游戏关卡：(GameLevels)表控制层
 *
 */
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


}
