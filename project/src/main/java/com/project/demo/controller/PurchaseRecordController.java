package com.project.demo.controller;

import com.project.demo.entity.PurchaseRecord;
import com.project.demo.service.PurchaseRecordService;
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
 * 购买记录：(PurchaseRecord)表控制层
 *
 */
@RestController
@RequestMapping("/purchase_record")
public class PurchaseRecordController extends BaseController<PurchaseRecord, PurchaseRecordService> {

    /**
     * 购买记录对象
     */
    @Autowired
    public PurchaseRecordController(PurchaseRecordService service) {
        setService(service);
    }



    @PostMapping("/add")
    @Transactional
    public Map<String, Object> add(HttpServletRequest request) throws IOException {
        Map<String,Object> paramMap = service.readBody(request.getReader());
        this.addMap(paramMap);
        String sql = "SELECT MAX(purchase_record_id) AS max FROM "+"`purchase_record`";
        Integer max = service.selectBaseCount(sql);
        sql = ("SELECT count(*) count FROM `gamer` INNER JOIN `purchase_record` ON gamer.player_screen_name=purchase_record.player_screen_name WHERE gamer.gold_coin_balance < purchase_record.gold_coins_required AND purchase_record.purchase_record_id="+max).replaceAll("&#60;","<");
        Integer count = service.selectBaseCount(sql);
        if(count>0){
            sql = "delete from "+"purchase_record"+" WHERE "+"purchase_record_id"+" ="+max;
            service.deleteBaseSql(sql);
            return error(30000,"你的金币不够啦！");
        }
        sql = "UPDATE `gamer` INNER JOIN `purchase_record` ON gamer.player_screen_name=purchase_record.player_screen_name SET gamer.gold_coin_balance= gamer.gold_coin_balance - purchase_record.gold_coins_required WHERE purchase_record.purchase_record_id="+max;
        service.updateBaseSql(sql);
        return success(1);
    }


}
