package com.project.demo.controller;

import com.project.demo.controller.base.BaseController;
import com.project.demo.entity.CodeToken;
import com.project.demo.service.CodeTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 邮箱验证码：(CodeToken)表控制层
 */
@RestController
@RequestMapping("code_token")
public class CodeTokenController extends BaseController<CodeToken, CodeTokenService> {
    /**
     * 服务对象
     */
    @Autowired
    public CodeTokenController(CodeTokenService service) {
        setService(service);
    }

    /**
     * 验证邮箱验证码API
     */
    @GetMapping("verify_email_code")
    public Map<String, Object> verifyEmailCode(String token, String code) {
        if (StringUtils.isEmpty(token) || StringUtils.isEmpty(code)) {
            return error(30000, "参数错误");
        } else {
            Map<String, String> queryMap = new HashMap<>();
            queryMap.put("token", token);
            Map<String, String> configMap = new HashMap<>();
            configMap.put("like", "false");
            List resultList = service.selectBaseList(service.select(queryMap, configMap));
            if (resultList.size() > 0) {
                CodeToken obj = (CodeToken) resultList.get(0);
                Date now = new Date();
                if (obj.getExpire_time().after(now)) {
                    if (code.equals(obj.getCode())) {
                        return success(1);
                    } else {
                        return error(30000, "验证码错误");
                    }
                } else {
                    return error(30000, "验证码已失效");
                }
            } else {
                return error(30000, "token请求错误");
            }
        }
    }
}
