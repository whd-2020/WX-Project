package com.project.demo.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.project.demo.entity.AccessToken;
import com.project.demo.entity.User;
import com.project.demo.entity.UserGroup;
import com.project.demo.entity.Gamer;
import com.project.demo.service.UserGroupService;
import com.project.demo.service.UserService;
import com.project.demo.service.GamerService;

import com.project.demo.controller.base.BaseController;
import com.project.demo.util.RsaUtils;
import com.project.demo.util.WechatUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.Duration;
import java.util.*;

/**
 * 用户账户：用于保存用户登录信息(User)表控制层
 */
@Slf4j
@RestController
@RequestMapping("user")
public class UserController extends BaseController<User, UserService> {
    /**
     * 服务对象
     */
    @Autowired
    public UserController(UserService service) {
        setService(service);
    }

    @Autowired
    private UserGroupService userGroupService;

    @Autowired
    private GamerService gamerService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private WechatUtil wechatUtil;

    /**
     * 注册
     * @param user
     * @return
     */
    @PostMapping("register")
    public Map<String, Object> signUp(@RequestBody User user) {
        // 查询用户
        Map<String, String> query = new HashMap<>();
        Map<String,Object> map = JSON.parseObject(JSON.toJSONString(user));
        query.put("username",user.getUsername());
        List list = service.selectBaseList(service.select(query, new HashMap<>()));
        if (list.size()>0){
            return error(30000, "用户已存在");
        }
				map.put("password",String.valueOf(map.get("password")));
		        service.insert(map);
        return success(1);
    }

    /**
     * 找回密码
     * @param form
     * @return
     */
    @PostMapping("forget_password")
    public Map<String, Object> forgetPassword(@RequestBody User form,HttpServletRequest request) {
        JSONObject ret = new JSONObject();
        String username = form.getUsername();
        String code = form.getCode();
        String password = form.getPassword();
        // 判断条件
        if(code == null || code.length() == 0){
            return error(30000, "验证码不能为空");
        }
        if(username == null || username.length() == 0){
            return error(30000, "用户名不能为空");
        }
        if(password == null || password.length() == 0){
            return error(30000, "密码不能为空");
        }

        // 查询用户
        Map<String, String> query = new HashMap<>();
        query.put("username",username);
        List list = service.selectBaseList(service.select(query, service.readConfig(request)));
        if (list.size() > 0) {
            User o = (User) list.get(0);
            JSONObject query2 = new JSONObject();
            JSONObject form2 = new JSONObject();
            // 修改用户密码
            query2.put("user_id",o.getUserId());
						form2.put("password",password);
			            service.update(query, service.readConfig(request), form2);
            return success(1);
        }
        return error(70000,"用户不存在");
    }

    /**
     * 登录
     * @param data
     * @param httpServletRequest
     * @return
     */
    @PostMapping("login")
    public Map<String, Object> login(@RequestBody Map<String, String> data, HttpServletRequest httpServletRequest) {
        log.info("[执行登录接口]");

        String username = data.get("username");
        String email = data.get("email");
        String phone = data.get("phone");
        String password = data.get("password");

        try {
            password = RsaUtils.decryptByPrivateKey(password);
        }catch (Exception e){
            return error(30000,"解密失败");
        }

        List resultList = null;
        Map<String, String> map = new HashMap<>();
        if(username != null && "".equals(username) == false){
            map.put("username", username);
            resultList = service.selectBaseList(service.select(map, new HashMap<>()));
        }
        else if(email != null && "".equals(email) == false){
            map.put("email", email);
            resultList = service.selectBaseList(service.select(map, new HashMap<>()));
        }
        else if(phone != null && "".equals(phone) == false){
            map.put("phone", phone);
            resultList = service.selectBaseList(service.select(map, new HashMap<>()));
        }else{
            return error(30000, "账号或密码不能为空");
        }
        if (resultList == null || password == null) {
            return error(30000, "账号或密码不能为空");
        }
        //判断是否有这个用户
        if (resultList.size()<=0){
            return error(30000,"用户不存在");
        }

        User byUsername = (User) resultList.get(0);


        Map<String, String> groupMap = new HashMap<>();
        groupMap.put("name",byUsername.getUserGroup());
        List groupList = userGroupService.selectBaseList(userGroupService.select(groupMap, new HashMap<>()));
        if (groupList.size()<1){
            return error(30000,"用户组不存在");
        }

        UserGroup userGroup = (UserGroup) groupList.get(0);

        //查询用户审核状态
        if (!StringUtils.isEmpty(userGroup.getSourceTable())){
            String res = service.selectExamineState(userGroup.getSourceTable(),byUsername.getUserId());
            if (res==null){
                return error(30000,"用户不存在");
            }
            if (!res.equals("已通过")){
                return error(30000,"该用户审核未通过");
            }
        }

        //查询用户状态
        if (byUsername.getState()!=1){
            return error(30000,"用户非可用状态，不能登录");
        }
						String md5password = password;
				            if (byUsername.getPassword().equals(md5password)) {
                // 存储Token到数据库
                AccessToken accessToken = new AccessToken();
                accessToken.setToken(UUID.randomUUID().toString().replaceAll("-", ""));
                accessToken.setUser_id(byUsername.getUserId());

                try {
                    Duration duration = Duration.ofSeconds(7200L);
                    redisTemplate.opsForValue().set(accessToken.getToken(), accessToken,duration);
                } catch (Exception e) {
                    log.warn("Redis连接失败，Token存储到数据库失败: {}", e.getMessage());
                    // 即使Redis失败也继续登录流程
                }

                // 返回用户信息
                JSONObject user = JSONObject.parseObject(JSONObject.toJSONString(byUsername));
                user.put("token", accessToken.getToken());
                JSONObject ret = new JSONObject();
                ret.put("obj",user);
                return success(ret);
            } else {
                return error(30000, "账号或密码不正确");
            }
		    }


    /**
     * 修改密码
     * @param data
     * @param request
     * @return
     */
    @PostMapping("change_password")
    public Map<String, Object> change_password(@RequestBody Map<String, String> data, HttpServletRequest request){
        // 根据Token获取UserId
        String token = request.getHeader("x-auth-token");
        Integer userId = tokenGetUserId(token);

        String newPassword = data.get("password");
        if (StringUtils.isEmpty(newPassword)) {
            return error(10000, "新密码不能为空");
        }

        // 直接修改密码，不需要验证原密码
        Map<String, String> query = new HashMap<>();
        query.put("user_id", String.valueOf(userId));

        Map<String, Object> form = new HashMap<>();
        form.put("password", newPassword);
        service.update(query, service.readConfig(request), form);

        log.info("[修改密码成功] userId={}", userId);
        return success(1);
    }


    /**
     * 登录态
     * @param request
     * @return
     */
    @GetMapping("state")
    public Map<String, Object> state(HttpServletRequest request) {
        JSONObject ret = new JSONObject();
        // 获取状态
        String token = request.getHeader("x-auth-token");

        // 根据登录态获取用户ID
        Integer userId = tokenGetUserId(token);

        log.info("[返回userId] {}",userId);
        if(userId == null || userId == 0){
            return error(10000,"用户未登录!");
        }

        // 根据用户ID获取用户
        Map<String,String> query = new HashMap<>();
        query.put("user_id" ,String.valueOf(userId));

        // 根据用户ID获取
        List resultList = service.selectBaseList(service.select(query,service.readConfig(request)));
        if (resultList.size() > 0) {
            JSONObject user = JSONObject.parseObject(JSONObject.toJSONString(resultList.get(0)));
            user.put("token",token);
            ret.put("obj",user);
            return success(ret);
        } else {
            return error(10000,"用户未登录!");
        }
    }

    /**
     * 登录态
     * @param request
     * @return
     */
    @GetMapping("quit")
    public Map<String, Object> quit(HttpServletRequest request) {
        String token = request.getHeader("x-auth-token");
        try{
            redisTemplate.delete(token);
        }catch (Exception e){
            e.printStackTrace();
        }
        return success("退出登录成功！");
    }

    /**
     * 获取登录用户ID
     * @param token
     * @return
     */
    public Integer tokenGetUserId(String token) {
        log.info("[获取的token] {}",token);
        // 根据登录态获取用户ID
        if(token == null || "".equals(token)){
            return 0;
        }
        try{
            AccessToken byToken = (AccessToken) redisTemplate.opsForValue().get(token);
            if(byToken == null){
                return 0;
            }
            return byToken.getUser_id();
        }catch (Exception e){
            // Redis 超时/连接异常时，不要把接口打成 500，降级为未登录
            log.error("[tokenGetUserId] redis读取失败，降级为未登录，token={}", token, e);
            return 0;
        }
    }

    /**
     * 微信登录
     * @param data 包含code和用户信息的Map
     * @param httpServletRequest
     * @return
     */
    @PostMapping("wechat/login")
    public Map<String, Object> wechatLogin(@RequestBody Map<String, Object> data, HttpServletRequest httpServletRequest) {
        log.info("[执行微信登录接口] 收到的原始请求体: {}", JSON.toJSONString(data));

        String code = (String) data.get("code");
        String nickName = (String) data.get("nickName");
        String avatarUrl = (String) data.get("avatarUrl");
        log.info("[微信登录参数] code={}, nickName={}, avatarUrl={}", code, nickName, avatarUrl);

        // 验证code
        if (code == null || code.isEmpty()) {
            return error(30000, "微信登录code不能为空");
        }

        // 调用微信API获取openid
        Map<String, String> wechatInfo = wechatUtil.getOpenIdByCode(code);
        if (wechatInfo == null || wechatInfo.get("openid") == null) {
            return error(30000, "微信登录失败，请重试");
        }

        String openid = wechatInfo.get("openid");
        log.info("[微信登录] 通过code换取的 openid={}", openid);

        // 根据openid查询用户是否存在
        Map<String, String> query = new HashMap<>();
        query.put("open_id", openid);
        List resultList = service.selectBaseList(service.select(query, new HashMap<>()));

        User user;
        boolean isNewUser = false;

        if (resultList != null && resultList.size() > 0) {
            // 用户已存在，更新用户信息
            user = (User) resultList.get(0);

            // 更新用户信息（每次登录都更新微信昵称和头像）
            Map<String, Object> updateMap = new HashMap<>();
            boolean needUpdate = false;

            // 如果前端传来了有效的昵称，就更新
            if (nickName != null && !nickName.isEmpty() && !nickName.equals("微信用户")) {
                // 昵称和用户名都同步为微信昵称
                updateMap.put("nickname", nickName);
                updateMap.put("username", nickName);
                needUpdate = true;
                log.info("[微信登录] 更新昵称: {}", nickName);
            } else if (nickName == null || nickName.isEmpty()) {
                // 如果昵称为null或空，保持原有昵称不变
                log.info("[微信登录] 昵称为null或空，保持原有昵称不变");
            }

            // 头像始终使用微信头像（如果有的话）
            if (avatarUrl != null && !avatarUrl.isEmpty()) {
                updateMap.put("avatar", avatarUrl);
                needUpdate = true;
                log.info("[微信登录] 更新头像: {}", avatarUrl);
            } else if (avatarUrl == null || avatarUrl.isEmpty()) {
                // 如果头像为null或空，保持原有头像不变
                log.info("[微信登录] 头像为null或空，保持原有头像不变");
            }

            if (needUpdate) {
                Map<String, String> updateQuery = new HashMap<>();
                updateQuery.put("user_id", String.valueOf(user.getUserId()));
                service.update(updateQuery, service.readConfig(httpServletRequest), updateMap);
                log.info("[微信登录] 更新用户信息: userId={}, updateMap={}", user.getUserId(), JSON.toJSONString(updateMap));
            }

            // 重新查询获取最新数据
            resultList = service.selectBaseList(service.select(query, new HashMap<>()));
            if (resultList != null && resultList.size() > 0) {
                user = (User) resultList.get(0);
                log.info("[微信登录] 重新查询用户信息: userId={}, nickname={}, avatar={}", user.getUserId(), user.getNickname(), user.getAvatar());
            }
        } else {
            // 用户不存在，创建新用户
            isNewUser = true;
            Map<String, Object> insertMap = new HashMap<>();

            // 生成用户名：优先使用微信昵称；如果没有昵称再使用 openid 尾号规则
            String username;
            if (nickName != null && !nickName.isEmpty() && !nickName.equals("微信用户")) {
                username = nickName;
            } else {
                username = "wx_" + openid.substring(Math.max(0, openid.length() - 8)) + "_" + System.currentTimeMillis() % 10000;
            }
            insertMap.put("username", username);

            // 设置密码（微信登录用户不需要密码，但数据库字段可能非空，设置一个默认值）
            insertMap.put("password", "wechat_login_no_password");

            // 设置昵称：优先使用微信昵称，否则使用生成的用户名
            String finalNickname = (nickName != null && !nickName.isEmpty() && !nickName.equals("微信用户")) ? nickName : username;
            insertMap.put("nickname", finalNickname);

            // 设置头像
            if (avatarUrl != null && !avatarUrl.isEmpty()) {
                insertMap.put("avatar", avatarUrl);
                log.info("[微信登录] 新用户设置头像: {}", avatarUrl);
            }

            // 设置openid
            insertMap.put("open_id", openid);

            // 设置用户组（默认设置为"游戏玩家"）
            insertMap.put("user_group", "游戏玩家");

            // 设置状态为可用
            insertMap.put("state", 1);

            // 插入新用户
            log.info("[微信登录] 准备创建新用户, username={}, nickname={}, avatar={}", username, finalNickname, avatarUrl);
            service.insert(insertMap);

            // 重新查询获取新创建的用户
            resultList = service.selectBaseList(service.select(query, new HashMap<>()));
            if (resultList == null || resultList.size() == 0) {
                return error(30000, "创建用户失败");
            }
            user = (User) resultList.get(0);
            log.info("[微信登录] 新用户创建成功: userId={}, nickname={}, avatar={}", user.getUserId(), user.getNickname(), user.getAvatar());
        }

        // 检查用户组是否存在
        Map<String, String> groupMap = new HashMap<>();
        groupMap.put("name", user.getUserGroup());
        List groupList = userGroupService.selectBaseList(userGroupService.select(groupMap, new HashMap<>()));
        if (groupList.size() < 1) {
            return error(30000, "用户组不存在");
        }

        UserGroup userGroup = (UserGroup) groupList.get(0);

        // 查询用户审核状态
        if (!StringUtils.isEmpty(userGroup.getSourceTable())) {
            String res = service.selectExamineState(userGroup.getSourceTable(), user.getUserId());

            // 如果查询结果为 null，说明 gamer 表中没有记录，需要创建
            if (res == null) {
                log.warn("[微信登录] 用户 userId={} 在 gamer 表中没有记录，自动创建", user.getUserId());
                try {
                    Gamer gamer = new Gamer();
                    String screenName = (user.getNickname() != null && !user.getNickname().isEmpty())
                        ? user.getNickname() + "_" + user.getUserId()
                        : user.getUsername() + "_" + user.getUserId();
                    gamer.setPlayer_screen_name(screenName);
                    gamer.setUserId(user.getUserId());
                    gamer.setExamine_state("已通过");
                    gamer.setGold_coin_balance(0.0);
                    gamerService.save(gamer);
                    log.info("[微信登录] 为用户自动创建 gamer 记录成功: userId={}, playerScreenName={}", user.getUserId(), screenName);
                } catch (Exception e) {
                    log.error("[微信登录] 自动创建 gamer 记录失败: userId={}, error={}", user.getUserId(), e.getMessage(), e);
                    return error(30000, "创建游戏玩家信息失败，请联系管理员");
                }
            } else if (!res.equals("已通过")) {
                return error(30000, "该用户审核未通过");
            }
        }

        // 查询用户状态
        if (user.getState() != 1) {
            return error(30000, "用户非可用状态，不能登录");
        }

        // 生成Token
        AccessToken accessToken = new AccessToken();
        accessToken.setToken(UUID.randomUUID().toString().replaceAll("-", ""));
        accessToken.setUser_id(user.getUserId());

        try {
            Duration duration = Duration.ofSeconds(7200L);
            redisTemplate.opsForValue().set(accessToken.getToken(), accessToken, duration);
        } catch (Exception e) {
            log.warn("Redis连接失败，Token存储到数据库失败: {}", e.getMessage());
        }

        // 返回用户信息
        JSONObject userJson = JSONObject.parseObject(JSONObject.toJSONString(user));
        userJson.put("token", accessToken.getToken());
        userJson.put("isNewUser", isNewUser);
        JSONObject ret = new JSONObject();
        ret.put("obj", userJson);

        log.info("[微信登录成功] userId={}, openid={}, isNewUser={}", user.getUserId(), openid, isNewUser);
        return success(ret);
    }

    /**
     * 更新用户个人信息
     */
    @PostMapping("/update_profile")
    @Transactional
    public Map<String, Object> updateProfile(HttpServletRequest request) throws IOException {
        try {
            // 获取当前登录用户ID
            String token = request.getHeader("x-auth-token");
            Integer userId = tokenGetUserId(token);
            if (userId == null || userId == 0) {
                log.error("[更新个人信息] 用户未登录");
                return error(10000, "用户未登录");
            }

            Map<String, Object> map = service.readBody(request.getReader());
            String nickname = (String) map.get("nickname");
            String password = (String) map.get("password");
            String avatar = (String) map.get("avatar");

            log.info("[更新个人信息] userId={}, nickname={}, hasPassword={}, hasAvatar={}",
                userId, nickname, password != null, avatar != null);

            // 查询用户
            Map<String, String> query = new HashMap<>();
            query.put("user_id", String.valueOf(userId));
            List<?> resultList = service.selectBaseList(service.select(query, new HashMap<>()));
            if (resultList == null || resultList.size() == 0) {
                log.error("[更新个人信息] 用户不存在 userId={}", userId);
                return error(10000, "用户不存在");
            }

            // 准备更新数据
            Map<String, Object> updateMap = new HashMap<>();
            if (!StringUtils.isEmpty(nickname)) {
                updateMap.put("nickname", nickname.trim());
            }
            if (!StringUtils.isEmpty(password)) {
                updateMap.put("password", password);
            }
            if (!StringUtils.isEmpty(avatar)) {
                updateMap.put("avatar", avatar);
            }

            // 保存到数据库
            service.update(query, service.readConfig(request), updateMap);

            // 重新查询获取最新数据
            resultList = service.selectBaseList(service.select(query, new HashMap<>()));
            User user = (User) resultList.get(0);

            log.info("[更新个人信息成功] userId={}, nickname={}, avatar={}",
                userId, user.getNickname(), user.getAvatar());

            JSONObject userJson = JSONObject.parseObject(JSONObject.toJSONString(user));
            JSONObject ret = new JSONObject();
            ret.put("obj", userJson);
            return success(ret);
        } catch (Exception e) {
            log.error("[更新个人信息异常]", e);
            return error(10000, "更新失败，请重试");
        }
    }

    /**
     * 重写add
     * @return
     */
    @PostMapping("/add")
    @Transactional
    public Map<String, Object> add(HttpServletRequest request) throws IOException {
        Map<String,Object> map = service.readBody(request.getReader());
				map.put("password",String.valueOf(map.get("password")));
		        service.insert(map);
        return success(1);
    }

}
