package com.primihub.service;

import com.alibaba.fastjson.JSONObject;
import com.primihub.entity.DataSet;
import com.primihub.entity.base.BaseResultEntity;
import com.primihub.service.feign.NoticeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AsyncService {

    @Autowired
    private NoticeService noticeService;

    @Async
    public void noticeResource(DataSet dataSet, DataSet newDataSet) {
        BaseResultEntity baseResultEntity = noticeService.noticeResource(newDataSet.getId());
        log.info("{} - {}", newDataSet.getId(), JSONObject.toJSONString(baseResultEntity));
        if (!Integer.valueOf(0).equals(baseResultEntity.getCode())) {
            log.info("进入{}", newDataSet.getId());
            baseResultEntity = noticeService.testDataSet(newDataSet.getId());
            log.info("{} - {}", newDataSet.getId(), JSONObject.toJSONString(baseResultEntity));
        }
    }

}
