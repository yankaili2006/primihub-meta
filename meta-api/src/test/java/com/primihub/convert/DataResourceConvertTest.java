package com.primihub.convert;

import com.primihub.entity.DataSet;
import com.primihub.entity.copy.dto.CopyResourceDto;
import com.primihub.entity.copy.dto.CopyResourceFieldDto;
import com.primihub.entity.resource.enumeration.AuthTypeEnum;
import com.primihub.entity.resource.po.FusionResource;
import com.primihub.entity.resource.po.FusionResourceField;
import com.primihub.entity.resource.vo.FusionResourceVo;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.Assert.*;

public class DataResourceConvertTest {

    @Test
    public void testCopyResourceDtoConvertPo() {
        CopyResourceDto dto = new CopyResourceDto();
        dto.setResourceId("res-1");
        dto.setResourceName("Test Resource");
        dto.setResourceDesc("A test resource");
        dto.setResourceType(1);
        dto.setResourceAuthType(2);
        dto.setResourceRowsCount(100);
        dto.setResourceColumnCount(10);
        dto.setResourceColumnNameList("col1,col2");
        dto.setResourceContainsY(0);
        dto.setResourceYRowsCount(50);
        dto.setResourceYRatio(new BigDecimal("0.5"));
        dto.setResourceTag("tag1,tag2");
        dto.setOrganId("org-1");
        dto.setIsDel(0);
        dto.setResourceState(0);
        dto.setResourceHashCode("abc123");
        dto.setUserName("user1");

        FusionResource po = DataResourceConvert.CopyResourceDtoConvertPo(dto);

        assertEquals("res-1", po.getResourceId());
        assertEquals("Test Resource", po.getResourceName());
        assertEquals("A test resource", po.getResourceDesc());
        assertEquals(Integer.valueOf(1), po.getResourceType());
        assertEquals(Integer.valueOf(2), po.getResourceAuthType());
        assertEquals(Integer.valueOf(100), po.getResourceRowsCount());
        assertEquals(Integer.valueOf(10), po.getResourceColumnCount());
        assertEquals("col1,col2", po.getResourceColumnNameList());
        assertEquals(Integer.valueOf(0), po.getResourceContainsY());
        assertEquals(Integer.valueOf(50), po.getResourceYRowsCount());
        assertEquals(new BigDecimal("0.5"), po.getResourceYRatio());
        assertEquals("tag1,tag2", po.getResourceTag());
        assertEquals("org-1", po.getOrganId());
        assertEquals(Integer.valueOf(0), po.getIsDel());
        assertEquals(Integer.valueOf(0), po.getResourceState());
        assertEquals("abc123", po.getResourceHashCode());
        assertEquals("user1", po.getUserName());
    }

    @Test
    public void testFusionResourceConvertCopyResourceDto() {
        FusionResource po = createFusionResource(1L, "res-1", "org-1", "0,1,2");
        po.setAuthOrgans("org-2,org-3");
        po.setResourceTag("tag1");

        List<CopyResourceFieldDto> fieldDtos = new ArrayList<>();
        CopyResourceFieldDto fieldDto = new CopyResourceFieldDto();
        fieldDto.setFieldName("name");
        fieldDto.setFieldAs("alias");
        fieldDto.setFieldType(0);
        fieldDto.setFieldDesc("description");
        fieldDtos.add(fieldDto);

        DataSet dataSet = new DataSet("ds-1", "access", "mysql", "localhost", "public");

        CopyResourceDto result = DataResourceConvert.FusionResourceConvertCopyResourceDto(po, fieldDtos, dataSet);

        assertEquals("res-1", result.getResourceId());
        assertEquals("org-1", result.getOrganId());
        assertNotNull(result.getAuthOrganList());
        assertEquals(2, result.getAuthOrganList().size());
        assertTrue(result.getAuthOrganList().contains("org-2"));
        assertTrue(result.getAuthOrganList().contains("org-3"));
        assertEquals(1, result.getFieldList().size());
        assertEquals("name", result.getFieldList().get(0).getFieldName());
        assertNotNull(result.getDataSet());
        assertEquals("ds-1", result.getDataSet().getId());
        assertEquals("", result.getDataSet().getAccessInfo());
    }

    @Test
    public void testFusionResourceConvertCopyResourceDtoWithNullAuthOrgans() {
        FusionResource po = createFusionResource(1L, "res-1", "org-1", null);
        po.setAuthOrgans(null);

        CopyResourceDto result = DataResourceConvert.FusionResourceConvertCopyResourceDto(po, new ArrayList<>(), new DataSet());

        assertNull(result.getAuthOrganList());
    }

    @Test
    public void testFusionResourcePoConvertVo_Basic() {
        FusionResource po = createFusionResource(1L, "res-1", "org-1", "tag1,tag2");
        po.setIsDel(0);
        po.setResourceState(0);
        po.setResourceAuthType(AuthTypeEnum.PUBLIC.getAuthType());

        FusionResourceVo vo = DataResourceConvert.fusionResourcePoConvertVo(po, "OrgName", null, "global-1");

        assertEquals("res-1", vo.getResourceId());
        assertEquals("OrgName", vo.getOrganName());
        assertEquals(Integer.valueOf(0), vo.getAvailable());
        assertEquals(Arrays.asList("tag1", "tag2"), vo.getResourceTag());
    }

    @Test
    public void testFusionResourcePoConvertVo_NotAvailableWhenDeleted() {
        FusionResource po = createFusionResource(1L, "res-1", "org-1", "tag1");
        po.setIsDel(1);
        po.setResourceState(0);

        FusionResourceVo vo = DataResourceConvert.fusionResourcePoConvertVo(po, "Org", null, "global-1");

        assertEquals(Integer.valueOf(1), vo.getAvailable());
    }

    @Test
    public void testFusionResourcePoConvertVo_NotAvailableWhenOffline() {
        FusionResource po = createFusionResource(1L, "res-1", "org-1", "tag1");
        po.setIsDel(0);
        po.setResourceState(1);

        FusionResourceVo vo = DataResourceConvert.fusionResourcePoConvertVo(po, "Org", null, "global-1");

        assertEquals(Integer.valueOf(1), vo.getAvailable());
    }

    @Test
    public void testFusionResourcePoConvertVo_OwnResource() {
        FusionResource po = createFusionResource(1L, "res-1", "org-1", "tag1");
        po.setIsDel(0);
        po.setResourceState(0);
        po.setResourceAuthType(AuthTypeEnum.PRIVATE.getAuthType());

        FusionResourceVo vo = DataResourceConvert.fusionResourcePoConvertVo(po, "Org", null, "org-1");

        assertEquals(Integer.valueOf(0), vo.getAvailable());
    }

    @Test
    public void testFusionResourcePoConvertVo_PublicResource() {
        FusionResource po = createFusionResource(1L, "res-1", "org-1", "tag1");
        po.setIsDel(0);
        po.setResourceState(0);
        po.setResourceAuthType(AuthTypeEnum.PUBLIC.getAuthType());

        FusionResourceVo vo = DataResourceConvert.fusionResourcePoConvertVo(po, "Org", null, "org-2");

        assertEquals(Integer.valueOf(0), vo.getAvailable());
    }

    @Test
    public void testFusionResourcePoConvertVo_VisibilityGranted() {
        FusionResource po = createFusionResource(1L, "res-1", "org-1", "tag1");
        po.setIsDel(0);
        po.setResourceState(0);
        po.setResourceAuthType(AuthTypeEnum.VISIBILITY.getAuthType());
        po.setAuthOrgans("org-2,org-3");

        FusionResourceVo vo = DataResourceConvert.fusionResourcePoConvertVo(po, "Org", null, "org-2");

        assertEquals(Integer.valueOf(0), vo.getAvailable());
    }

    @Test
    public void testFusionResourcePoConvertVo_VisibilityNotGranted() {
        FusionResource po = createFusionResource(1L, "res-1", "org-1", "tag1");
        po.setIsDel(0);
        po.setResourceState(0);
        po.setResourceAuthType(AuthTypeEnum.VISIBILITY.getAuthType());
        po.setAuthOrgans("org-2,org-3");

        FusionResourceVo vo = DataResourceConvert.fusionResourcePoConvertVo(po, "Org", null, "org-4");

        assertEquals(Integer.valueOf(1), vo.getAvailable());
    }

    @Test
    public void testFusionResourcePoConvertVo_ResourceTagNull() {
        FusionResource po = createFusionResource(1L, "res-1", "org-1", null);
        po.setIsDel(0);
        po.setResourceState(0);
        po.setResourceAuthType(AuthTypeEnum.PUBLIC.getAuthType());

        FusionResourceVo vo = DataResourceConvert.fusionResourcePoConvertVo(po, "Org", null, "global-1");

        assertEquals(new ArrayList<>(), vo.getResourceTag());
    }

    @Test
    public void testFusionResourcePoConvertVo_WithFieldList() {
        FusionResource po = createFusionResource(1L, "res-1", "org-1", "tag1");
        po.setIsDel(0);
        po.setResourceState(0);
        po.setResourceAuthType(AuthTypeEnum.PUBLIC.getAuthType());

        List<FusionResourceField> fields = new ArrayList<>();
        FusionResourceField field = new FusionResourceField();
        field.setFieldName("column1");
        field.setFieldAs("col1_alias");
        fields.add(field);

        FusionResourceVo vo = DataResourceConvert.fusionResourcePoConvertVo(po, "Org", fields, "global-1");

        assertEquals("col1_alias", vo.getOpenColumnNameList());
        assertEquals(fields, vo.getFieldList());
    }

    @Test
    public void testFusionResourcePoConvertVo_WithFieldListFallbackToFieldName() {
        FusionResource po = createFusionResource(1L, "res-1", "org-1", "tag1");
        po.setIsDel(0);
        po.setResourceState(0);
        po.setResourceAuthType(AuthTypeEnum.PUBLIC.getAuthType());

        List<FusionResourceField> fields = new ArrayList<>();
        FusionResourceField field = new FusionResourceField();
        field.setFieldName("column1");
        field.setFieldAs(null);
        fields.add(field);

        FusionResourceVo vo = DataResourceConvert.fusionResourcePoConvertVo(po, "Org", fields, "global-1");

        assertEquals("column1", vo.getOpenColumnNameList());
    }

    @Test
    public void testCopyResourceFieldDtoConvertPo() {
        CopyResourceFieldDto dto = new CopyResourceFieldDto();
        dto.setFieldName("name");
        dto.setFieldAs("alias");
        dto.setFieldType(1);
        dto.setFieldDesc("desc");

        FusionResourceField result = DataResourceConvert.copyResourceFieldDtoConvertPo(dto, 100L, 200L);

        assertEquals(Long.valueOf(200), result.getFieldId());
        assertEquals(Long.valueOf(100), result.getResourceId());
        assertEquals("name", result.getFieldName());
        assertEquals("alias", result.getFieldAs());
        assertEquals(Integer.valueOf(1), result.getFieldType());
        assertEquals("desc", result.getFieldDesc());
    }

    @Test
    public void testCopyResourceFieldDtoConvertPoWithNullFieldId() {
        CopyResourceFieldDto dto = new CopyResourceFieldDto();
        dto.setFieldName("name");

        FusionResourceField result = DataResourceConvert.copyResourceFieldDtoConvertPo(dto, 100L, null);

        assertNull(result.getFieldId());
        assertEquals(Long.valueOf(100), result.getResourceId());
    }

    @Test
    public void testFusionResourceFieldConvertCopyResourceFieldDto() {
        FusionResourceField po = new FusionResourceField();
        po.setFieldId(1L);
        po.setResourceId(100L);
        po.setFieldName("name");
        po.setFieldAs("alias");
        po.setFieldType(1);
        po.setFieldDesc("desc");

        CopyResourceFieldDto result = DataResourceConvert.fusionResourceFieldConvertCopyResourceFieldDto(po);

        assertEquals("name", result.getFieldName());
        assertEquals("alias", result.getFieldAs());
        assertEquals(Integer.valueOf(1), result.getFieldType());
        assertEquals("desc", result.getFieldDesc());
        assertEquals(Long.valueOf(100), result.getResourceId());
    }

    private FusionResource createFusionResource(Long id, String resourceId, String organId, String resourceTag) {
        FusionResource po = new FusionResource();
        po.setId(id);
        po.setResourceId(resourceId);
        po.setResourceName("Resource " + resourceId);
        po.setOrganId(organId);
        po.setResourceTag(resourceTag);
        po.setResourceType(1);
        po.setResourceRowsCount(100);
        po.setResourceColumnCount(10);
        po.setResourceColumnNameList("col1,col2");
        po.setResourceContainsY(0);
        po.setResourceYRowsCount(50);
        po.setResourceYRatio(new BigDecimal("0.5"));
        po.setResourceHashCode("hash123");
        po.setResourceState(0);
        po.setIsDel(0);
        po.setUserName("test-user");
        po.setResourceAuthType(AuthTypeEnum.PUBLIC.getAuthType());
        po.setCTime(new Date());
        return po;
    }
}
