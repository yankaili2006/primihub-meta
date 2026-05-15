package com.primihub.controller;

import com.alibaba.fastjson.JSON;
import com.primihub.entity.DataSet;
import com.primihub.entity.base.BaseResultEntity;
import com.primihub.entity.copy.dto.CopyResourceDto;
import com.primihub.entity.resource.param.ResourceParam;
import com.primihub.service.ResourceService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(FusionResourceController.class)
public class FusionResourceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ResourceService resourceService;

    @Test
    public void testGetResourceList() throws Exception {
        when(resourceService.getResourceList(any())).thenReturn(BaseResultEntity.success());

        ResourceParam param = new ResourceParam();
        mockMvc.perform(request(HttpMethod.GET, "/fusionResource/getResourceList")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSON.toJSONString(param)))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetResourceListById_MissingParam() throws Exception {
        mockMvc.perform(request(HttpMethod.GET, "/fusionResource/getResourceListById"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(100));
    }

    @Test
    public void testGetResourceListById_Success() throws Exception {
        when(resourceService.getResourceListById(any(), anyString())).thenReturn(BaseResultEntity.success());

        mockMvc.perform(request(HttpMethod.GET, "/fusionResource/getResourceListById")
                .param("resourceIdArray", "res-1,res-2")
                .param("globalId", "global-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    public void testGetResourceListById_EmptyArray() throws Exception {
        mockMvc.perform(request(HttpMethod.GET, "/fusionResource/getResourceListById")
                .param("resourceIdArray", "")
                .param("globalId", "global-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(100));
    }

    @Test
    public void testGetCopyResource_MissingParam() throws Exception {
        mockMvc.perform(request(HttpMethod.GET, "/fusionResource/getCopyResource"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetCopyResource_Success() throws Exception {
        when(resourceService.getCopyResource(any())).thenReturn(BaseResultEntity.success());

        mockMvc.perform(request(HttpMethod.GET, "/fusionResource/getCopyResource")
                .param("resourceIds", "res-1,res-2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    public void testGetResourceTagList() throws Exception {
        when(resourceService.getResourceTagList()).thenReturn(BaseResultEntity.success(Arrays.asList("tag1", "tag2")));

        mockMvc.perform(request(HttpMethod.GET, "/fusionResource/getResourceTagList"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    public void testGetDataResource_MissingParam() throws Exception {
        mockMvc.perform(request(HttpMethod.GET, "/fusionResource/getDataResource"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(100));
    }

    @Test
    public void testGetDataResource_Success() throws Exception {
        when(resourceService.getDataResource(anyString(), anyString())).thenReturn(BaseResultEntity.success());

        mockMvc.perform(request(HttpMethod.GET, "/fusionResource/getDataResource")
                .param("resourceId", "res-1")
                .param("globalId", "global-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    public void testGetDataResource_EmptyResourceId() throws Exception {
        mockMvc.perform(request(HttpMethod.GET, "/fusionResource/getDataResource")
                .param("resourceId", "")
                .param("globalId", "global-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(100));
    }

    @Test
    public void testSaveResource_MissingGlobalId() throws Exception {
        List<CopyResourceDto> list = new ArrayList<>();
        list.add(new CopyResourceDto());
        mockMvc.perform(post("/fusionResource/saveResource")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSON.toJSONString(list)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(100));
    }

    @Test
    public void testSaveResource_MissingBody() throws Exception {
        mockMvc.perform(post("/fusionResource/saveResource")
                .param("globalId", "global-1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("[]"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(100));
    }

    @Test
    public void testSaveResource_Success() throws Exception {
        when(resourceService.batchSaveResource(anyString(), anyList())).thenReturn(BaseResultEntity.success());
        List<CopyResourceDto> list = new ArrayList<>();
        list.add(new CopyResourceDto());

        mockMvc.perform(post("/fusionResource/saveResource")
                .param("globalId", "global-1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSON.toJSONString(list)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    public void testGetTestDataSet() throws Exception {
        when(resourceService.getTestDataSet(anyString())).thenReturn(BaseResultEntity.success());

        mockMvc.perform(request(HttpMethod.GET, "/fusionResource/getTestDataSet")
                .param("id", "test-1"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetTestDataSet_NoParam() throws Exception {
        when(resourceService.getTestDataSet(isNull())).thenReturn(BaseResultEntity.success());

        mockMvc.perform(request(HttpMethod.GET, "/fusionResource/getTestDataSet"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetDataSets() throws Exception {
        when(resourceService.getDataSets(anySet())).thenReturn(BaseResultEntity.success());
        Set<String> ids = new HashSet<>(Arrays.asList("id1", "id2"));

        mockMvc.perform(request(HttpMethod.GET, "/fusionResource/getDataSets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSON.toJSONString(ids)))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetDataSets_EmptySet() throws Exception {
        when(resourceService.getDataSets(anySet())).thenReturn(BaseResultEntity.success());

        mockMvc.perform(request(HttpMethod.GET, "/fusionResource/getDataSets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSON.toJSONString(new HashSet<>())))
                .andExpect(status().isOk());
    }

    @Test
    public void testBatchSaveTestDataSet_MissingBody() throws Exception {
        mockMvc.perform(post("/fusionResource/batchSaveTestDataSet")
                .contentType(MediaType.APPLICATION_JSON)
                .content("[]"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(100));
    }

    @Test
    public void testBatchSaveTestDataSet_Success() throws Exception {
        when(resourceService.batchSaveTestDataSet(anyList())).thenReturn(BaseResultEntity.success());
        List<DataSet> list = new ArrayList<>();
        list.add(new DataSet("id1", "acc", "mysql", "addr", "public"));

        mockMvc.perform(post("/fusionResource/batchSaveTestDataSet")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSON.toJSONString(list)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    public void testBatchSaveTestDataSet_WithMultipleItems() throws Exception {
        when(resourceService.batchSaveTestDataSet(anyList())).thenReturn(BaseResultEntity.success());
        List<DataSet> list = new ArrayList<>();
        list.add(new DataSet("id1", "acc", "mysql", "addr", "public"));
        list.add(new DataSet("id2", "acc2", "postgresql", "addr2", "private"));

        mockMvc.perform(post("/fusionResource/batchSaveTestDataSet")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSON.toJSONString(list)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    public void testGetResourceList_WithServiceReturningData() throws Exception {
        Map<String, Object> result = new HashMap<>();
        result.put("total", 10);
        when(resourceService.getResourceList(any())).thenReturn(BaseResultEntity.success(result));

        ResourceParam param = new ResourceParam();
        mockMvc.perform(request(HttpMethod.GET, "/fusionResource/getResourceList")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSON.toJSONString(param)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.total").value(10));
    }
}
