package com.project.demo.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * 微信工具类
 * 用于调用微信API
 */
@Slf4j
@Component
public class WechatUtil {

    @Value("${wechat.appid:}")
    private String appid;

    @Value("${wechat.secret:}")
    private String secret;

    private final RestTemplate restTemplate;

    public WechatUtil() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * 通过code获取微信openid和session_key
     * @param code 微信登录code
     * @return 包含openid和session_key的Map，如果失败返回null
     */
    public Map<String, String> getOpenIdByCode(String code) {
        if (appid == null || appid.isEmpty() || secret == null || secret.isEmpty()) {
            log.error("微信配置未设置：appid或secret为空");
            return null;
        }

        String url = "https://api.weixin.qq.com/sns/jscode2session?appid={appid}&secret={secret}&js_code={code}&grant_type=authorization_code";
        
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(
                url, 
                String.class, 
                appid, 
                secret, 
                code
            );

            String responseBody = response.getBody();
            log.info("微信API返回: {}", responseBody);

            JSONObject jsonObject = JSON.parseObject(responseBody);
            
            // 检查是否有错误
            if (jsonObject.containsKey("errcode")) {
                Integer errcode = jsonObject.getInteger("errcode");
                String errmsg = jsonObject.getString("errmsg");
                log.error("微信API调用失败: errcode={}, errmsg={}", errcode, errmsg);
                return null;
            }

            // 获取openid和session_key
            String openid = jsonObject.getString("openid");
            String sessionKey = jsonObject.getString("session_key");

            if (openid == null || openid.isEmpty()) {
                log.error("获取openid失败");
                return null;
            }

            Map<String, String> result = new HashMap<>();
            result.put("openid", openid);
            result.put("session_key", sessionKey);
            
            return result;
        } catch (Exception e) {
            log.error("调用微信API异常: ", e);
            return null;
        }
    }
}
