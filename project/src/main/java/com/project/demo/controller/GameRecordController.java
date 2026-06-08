package com.project.demo.controller;

import com.project.demo.entity.GameRecord;
import com.project.demo.service.GameRecordService;
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
 * 游戏记录：(GameRecord)表控制层
 *
 */
@RestController
@RequestMapping("/game_record")
public class GameRecordController extends BaseController<GameRecord, GameRecordService> {

    /**
     * 游戏记录对象
     */
    @Autowired
    public GameRecordController(GameRecordService service) {
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
