package com.project.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.project.demo.dao.GamerMapper;
import com.project.demo.entity.Gamer;
import com.project.demo.service.GamerService;
import com.project.demo.controller.base.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;


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

    @Autowired
    private GamerMapper gamerMapper;

    @PostMapping("/add")
    @Transactional
    public Map<String, Object> add(HttpServletRequest request) throws IOException {
        Map<String,Object> paramMap = service.readBody(request.getReader());

        // 使用 user_id 保证一个用户只对应一条 gamer 记录，避免重复插入导致 500
        Object userIdObj = paramMap.get("user_id");
        if (userIdObj != null) {
            Integer userId = Integer.valueOf(String.valueOf(userIdObj));
            // 查询是否已经存在 gamer 记录（基于 user_id）
            QueryWrapper<Gamer> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId);
            Gamer existingGamer = gamerMapper.selectOne(queryWrapper);
            if (existingGamer != null) {
                // 已存在则直接返回成功，不再重复插入
                return success(1);
            }
        }

        // 如果 user_id 不存在记录，检查 player_screen_name 是否已存在
        Object screenNameObj = paramMap.get("player_screen_name");
        if (screenNameObj != null && userIdObj != null) {
            String screenName = String.valueOf(screenNameObj);
            QueryWrapper<Gamer> nameWrapper = new QueryWrapper<>();
            nameWrapper.eq("player_screen_name", screenName);
            Gamer existingByName = gamerMapper.selectOne(nameWrapper);
            if (existingByName != null) {
                // 如果 player_screen_name 已存在，生成一个新的唯一名称
                String newScreenName = screenName + "_" + userIdObj;
                paramMap.put("player_screen_name", newScreenName);
            }
        }

        // 尝试插入，如果仍然失败（理论上不应该），捕获异常
        try {
            this.addMap(paramMap);
            return success(1);
        } catch (DuplicateKeyException e) {
            // 如果仍然有唯一性冲突，再次检查 user_id，如果存在就返回成功
            if (userIdObj != null) {
                Integer userId = Integer.valueOf(String.valueOf(userIdObj));
                QueryWrapper<Gamer> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("user_id", userId);
                Gamer existingGamer = gamerMapper.selectOne(queryWrapper);
                if (existingGamer != null) {
                    return success(1);
                }
            }
            // 否则抛出异常
            throw e;
        }
    }


}
