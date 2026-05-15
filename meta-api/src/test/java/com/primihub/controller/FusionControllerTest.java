package com.primihub.controller;

import com.primihub.entity.base.BaseResultEntity;
import com.primihub.entity.base.BaseResultEnum;
import com.primihub.service.OrganService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(FusionController.class)
public class FusionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrganService organService;

    @Test
    public void testHealthConnection() throws Exception {
        mockMvc.perform(request(HttpMethod.GET, "/fusion/healthConnection"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    public void testHealthConnection_ReturnsTimestamp() throws Exception {
        mockMvc.perform(request(HttpMethod.GET, "/fusion/healthConnection"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.result").isNumber());
    }

    @Test
    public void testOrganData() throws Exception {
        when(organService.organData(anyString(), anyString())).thenReturn(BaseResultEntity.success());

        mockMvc.perform(request(HttpMethod.GET, "/fusion/organData")
                .param("organId", "org-1")
                .param("organName", "TestOrg"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    public void testOrganData_OnlyOrganId() throws Exception {
        when(organService.organData(anyString(), isNull())).thenReturn(BaseResultEntity.success());

        mockMvc.perform(request(HttpMethod.GET, "/fusion/organData")
                .param("organId", "org-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    public void testOrganData_OnlyOrganName() throws Exception {
        when(organService.organData(isNull(), anyString())).thenReturn(BaseResultEntity.success());

        mockMvc.perform(request(HttpMethod.GET, "/fusion/organData")
                .param("organName", "TestOrg"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    public void testOrganData_NoParams() throws Exception {
        when(organService.organData(isNull(), isNull())).thenReturn(BaseResultEntity.success());

        mockMvc.perform(request(HttpMethod.GET, "/fusion/organData"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    public void testOrganData_ServiceReturnsFailure() throws Exception {
        when(organService.organData(anyString(), anyString()))
                .thenReturn(BaseResultEntity.failure(BaseResultEnum.FAILURE, "organ error"));

        mockMvc.perform(request(HttpMethod.GET, "/fusion/organData")
                .param("organId", "org-1")
                .param("organName", "TestOrg"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(-1));
    }

    @Test
    public void testOrganData_WithOrganIdOnlyAndServiceSuccess() throws Exception {
        when(organService.organData(eq("org-42"), isNull())).thenReturn(BaseResultEntity.success());

        mockMvc.perform(request(HttpMethod.GET, "/fusion/organData")
                .param("organId", "org-42"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }
}
