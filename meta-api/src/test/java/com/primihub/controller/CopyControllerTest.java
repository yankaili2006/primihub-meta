package com.primihub.controller;

import com.primihub.entity.base.BaseResultEntity;
import com.primihub.service.CopyService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(CopyController.class)
public class CopyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CopyService copyService;

    @Test
    public void testBatchSave_MissingTableName() throws Exception {
        mockMvc.perform(post("/copy/batchSave")
                .param("globalId", "global-1")
                .param("copyPart", "{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(100));
    }

    @Test
    public void testBatchSave_MissingMaxOffset() throws Exception {
        mockMvc.perform(post("/copy/batchSave")
                .param("globalId", "global-1")
                .param("copyPart", "{\"tableName\":\"test\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(100));
    }

    @Test
    public void testBatchSave_MissingCopyPart() throws Exception {
        mockMvc.perform(post("/copy/batchSave")
                .param("globalId", "global-1")
                .param("copyPart", "{\"tableName\":\"test\",\"maxOffset\":100}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(100));
    }

    @Test
    public void testBatchSave_Success() throws Exception {
        when(copyService.batchSave(anyString(), any())).thenReturn(BaseResultEntity.success());

        mockMvc.perform(post("/copy/batchSave")
                .param("globalId", "global-1")
                .param("copyPart", "{\"tableName\":\"data_resource\",\"maxOffset\":100,\"copyPart\":\"[]\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }
}
