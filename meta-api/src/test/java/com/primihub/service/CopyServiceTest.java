package com.primihub.service;

import com.alibaba.fastjson.JSON;
import com.primihub.entity.base.BaseResultEntity;
import com.primihub.entity.base.BaseResultEnum;
import com.primihub.entity.copy.dto.CopyResourceDto;
import com.primihub.entity.copy.dto.DataFusionCopyDto;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CopyServiceTest {

    @InjectMocks
    private CopyService copyService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testBatchSave_UnknownTableName() {
        DataFusionCopyDto dto = new DataFusionCopyDto();
        dto.setTableName("unknown_table");
        dto.setMaxOffset(100L);
        dto.setCopyPart("[]");

        BaseResultEntity result = copyService.batchSave("global-1", dto);

        assertEquals(BaseResultEnum.FAILURE.getReturnCode(), result.getCode());
        assertTrue(result.getMsg().contains("unknown_table"));
    }

    @Test
    public void testBatchSave_NullTableName() {
        DataFusionCopyDto dto = new DataFusionCopyDto();
        dto.setTableName(null);

        BaseResultEntity result = copyService.batchSave("global-1", dto);

        assertEquals(BaseResultEnum.FAILURE.getReturnCode(), result.getCode());
    }
}
