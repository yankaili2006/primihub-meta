package com.primihub.simple.controller;

import com.primihub.entity.DataSet;
import com.primihub.simple.base.BaseResultEntity;
import com.primihub.simple.service.FrontDataSetService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
public class DataSetControllerTest {

    private MockMvc mockMvc;

    @Mock
    private FrontDataSetService frontDataSetService;

    @InjectMocks
    private DataSetController dataSetController;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(dataSetController).build();
    }

    @Test
    public void testOne_Success() throws Exception {
        when(frontDataSetService.one(any())).thenReturn(BaseResultEntity.success());
        DataSet ds = new DataSet("id1", "acc", "mysql", "addr", "public");

        mockMvc.perform(post("/dataset/one")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\":\"id1\",\"driver\":\"mysql\",\"address\":\"addr\",\"visibility\":\"public\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    public void testOne_MissingBody() throws Exception {
        mockMvc.perform(post("/dataset/one")
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testOne_MissingId() throws Exception {
        mockMvc.perform(post("/dataset/one")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(100));
    }

    @Test
    public void testMany_Success() throws Exception {
        when(frontDataSetService.many(anyList())).thenReturn(BaseResultEntity.success());

        mockMvc.perform(post("/dataset/many")
                .contentType(MediaType.APPLICATION_JSON)
                .content("[{\"id\":\"id1\"},{\"id\":\"id2\"}]"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    public void testMany_EmptyList() throws Exception {
        mockMvc.perform(post("/dataset/many")
                .contentType(MediaType.APPLICATION_JSON)
                .content("[]"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(100));
    }

    @Test
    public void testDelete_Success() throws Exception {
        when(frontDataSetService.delete(any())).thenReturn(BaseResultEntity.success());

        mockMvc.perform(post("/dataset/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\":\"id1\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    public void testDelete_MissingId() throws Exception {
        mockMvc.perform(post("/dataset/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(100));
    }

    @Test
    public void testDelete_NullBody() throws Exception {
        mockMvc.perform(post("/dataset/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testHealth() throws Exception {
        mockMvc.perform(get("/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.result").value("success"));
    }
}
