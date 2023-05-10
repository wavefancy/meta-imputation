package com.imputation.jobs;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
class ImputationJobsApplicationTests {

    @Test
    void contextLoads() {
        JSONObject jsonObject = new JSONObject();
        for (String uuid:"a,b,c".split(",")) {
            Map<String,String> resMsg = new HashMap<>();
            resMsg.put("cromwellId",uuid);
            resMsg.put("status","h");
            resMsg.put("resPath","");
            jsonObject.put(uuid,resMsg);
        }
        System.out.println(jsonObject.toString());
        JSONObject msgJson= JSON.parseObject(jsonObject.toString());
        for (Map.Entry<String, Object> stringObjectEntry : msgJson.entrySet()) {
            String uuid = stringObjectEntry.getKey();
            JSONObject resMap=  (JSONObject) stringObjectEntry.getValue() ;
            resMap.get(uuid);
        }
        System.out.println(msgJson);
    }

}
