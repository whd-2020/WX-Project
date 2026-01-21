package com.project.demo.controller;

import com.project.demo.entity.Gamer;
import com.project.demo.service.GamerService;
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
 * 游戏玩家：(Gamer)表控制层
 *
 */
@RestController
@RequestMapping("/gamer")
public class GamerController extends BaseController<Gamer, GamerService> {

    /**
     * 游戏玩家对象
     */
    @Autowired
    public GamerController(GamerService service) {
        setService(service);
    }



    @PostMapping("/add")
    @Transactional
    public Map<String, Object> add(HttpServletRequest request) throws IOException {
        Map<String,Object> paramMap = service.readBody(request.getReader());
        Map<String, String> mapplayer_screen_name = new HashMap<>();
        mapplayer_screen_name.put("player_screen_name",String.valueOf(paramMap.get("player_screen_name")));
        List listplayer_screen_name = service.selectBaseList(service.select(mapplayer_screen_name, new HashMap<>()));
        if (listplayer_screen_name.size()>0){
            return error(30000, "字段玩家网名内容不能重复");
        }
        this.addMap(paramMap);
        return success(1);
    }


}
