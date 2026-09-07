package com.primihub.service;

import com.primihub.entity.DataSet;
import com.primihub.entity.base.BaseResultEntity;
import com.primihub.entity.base.BaseResultEnum;
import com.primihub.entity.base.PageDataEntity;
import com.primihub.entity.copy.dto.CopyResourceDto;
import com.primihub.entity.copy.dto.CopyResourceFieldDto;
import com.primihub.entity.resource.enumeration.AuthTypeEnum;
import com.primihub.entity.resource.po.FusionResource;
import com.primihub.entity.resource.po.FusionResourceField;
import com.primihub.entity.resource.po.FusionResourceTag;
import com.primihub.entity.resource.vo.FusionResourceVo;
import com.primihub.repository.DataSetRepository;
import com.primihub.repository.FusionRepository;
import com.primihub.repository.FusionResourceRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class ResourceServiceTest {

    @Mock
    private FusionResourceRepository resourceRepository;
    @Mock
    private GrpcDataSetService dataSetService;
    @Mock
    private DataSetRepository dataSetRepository;
    @Mock
    private FusionRepository fusionRepository;

    @InjectMocks
    private ResourceService resourceService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetOrganShortCode() {
        String globalId = "000000000000000000000000123456789012";
        String code = resourceService.getOrganShortCode(globalId);
        assertEquals("123456789012", code);
        assertEquals(12, code.length());
    }

    @Test
    public void testGetResourceTagList() {
        List<String> tags = Arrays.asList("tag1", "tag2");
        when(resourceRepository.selectFusionResourceTag()).thenReturn(tags);

        BaseResultEntity result = resourceService.getResourceTagList();

        assertEquals(tags, result.getResult());
    }

    @Test
    public void testGetTestDataSet_WithoutId() {
        List<DataSet> list = new ArrayList<>();
        list.add(new DataSet("id1", "acc", "mysql", "addr", "public"));
        when(dataSetRepository.getTestDataSet()).thenReturn(list);

        BaseResultEntity result = resourceService.getTestDataSet(null);

        assertTrue(result.getResult() instanceof List);
        assertEquals(1, ((List) result.getResult()).size());
    }

    @Test
    public void testGetTestDataSet_WithId() {
        DataSet ds = new DataSet("id1", "acc", "mysql", "addr", "public");
        when(dataSetRepository.getDataSetById("id1")).thenReturn(ds);

        BaseResultEntity result = resourceService.getTestDataSet("id1");

        List<DataSet> list = (List<DataSet>) result.getResult();
        assertEquals(1, list.size());
        assertSame(ds, list.get(0));
    }

    @Test
    public void testGetDataSets() {
        Set<String> ids = new HashSet<>(Arrays.asList("id1", "id2"));
        List<DataSet> list = new ArrayList<>();
        when(dataSetRepository.getDataSetByIds(ids)).thenReturn(list);

        BaseResultEntity result = resourceService.getDataSets(ids);

        assertSame(list, result.getResult());
    }

    @Test
    public void testBatchSaveTestDataSet_Insert() {
        DataSet ds = new DataSet("id1", "acc", "mysql", "addr", "public");
        when(dataSetRepository.getDataSetById("id1")).thenReturn(null);

        BaseResultEntity result = resourceService.batchSaveTestDataSet(Arrays.asList(ds));

        assertEquals(Integer.valueOf(0), result.getCode());
        verify(dataSetRepository).insertDataSet(ds);
        assertEquals(Integer.valueOf(1), ds.getHolder());
        assertEquals("", ds.getAccessInfo());
    }

    @Test
    public void testBatchSaveTestDataSet_Update() {
        DataSet existing = new DataSet("id1", "old", "mysql", "old", "public");
        DataSet ds = new DataSet("id1", "new", "mysql", "new", "public");
        when(dataSetRepository.getDataSetById("id1")).thenReturn(existing);

        BaseResultEntity result = resourceService.batchSaveTestDataSet(Arrays.asList(ds));

        assertEquals(Integer.valueOf(0), result.getCode());
        verify(dataSetRepository).updateDataSet(ds);
        assertEquals(Integer.valueOf(1), ds.getHolder());
    }

    @Test
    public void testBatchSaveTestDataSet_Exception() {
        when(dataSetRepository.getDataSetById(anyString())).thenThrow(new RuntimeException("DB error"));

        BaseResultEntity result = resourceService.batchSaveTestDataSet(
            Arrays.asList(new DataSet("id1", "acc", "mysql", "addr", "public"))
        );

        assertEquals(BaseResultEnum.FAILURE.getReturnCode(), result.getCode());
        assertTrue(result.getMsg().contains("DB error"));
    }

    @Test
    public void testBatchSaveResource_OwnOrganPrefixAccepted() {
        // 回归：前缀校验曾截 11 位与 12 位短码比对，任何本方资源都被判「条件检验未通过」，
        // fusion_resource 恒空、协作方资源列表恒空。
        String globalId = "8395908c-14c2-434e-80e6-bdbbccdc9201";
        CopyResourceDto dto = new CopyResourceDto();
        dto.setResourceId("bdbbccdc9201-cb916df4-0480-47ea-9161-fa76ca219288");
        dto.setResourceAuthType(AuthTypeEnum.PUBLIC.getAuthType());
        when(resourceRepository.selectFusionResourceById(anySet())).thenReturn(new ArrayList<>());

        BaseResultEntity result = resourceService.batchSaveResource(globalId, Arrays.asList(dto));

        assertEquals(BaseResultEnum.SUCCESS.getReturnCode(), result.getCode());
        verify(resourceRepository).saveFusionResource(any(FusionResource.class));
    }

    @Test
    public void testBatchSaveResource_ForeignPrefixRejected() {
        String globalId = "8395908c-14c2-434e-80e6-bdbbccdc9201";
        CopyResourceDto dto = new CopyResourceDto();
        dto.setResourceId("aaaabbbbcccc-ea05957b-5024-4cf1-a67d-aee95355a672");
        dto.setResourceAuthType(AuthTypeEnum.PUBLIC.getAuthType());

        BaseResultEntity result = resourceService.batchSaveResource(globalId, Arrays.asList(dto));

        assertEquals(BaseResultEnum.DATA_EXECUTE_TASK_FAIL.getReturnCode(), result.getCode());
        verify(resourceRepository, never()).saveFusionResource(any(FusionResource.class));
    }
}
