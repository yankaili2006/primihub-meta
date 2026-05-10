package com.primihub.entity.copy.enumeration;

import com.primihub.entity.copy.dto.CopyResourceDto;
import org.junit.Test;
import static org.junit.Assert.*;

public class CopyEnumTest {

    @Test
    public void testRSOURCEValues() {
        CopyEnum enu = CopyEnum.RSOURCE;
        assertEquals("data_resource", enu.getTableName());
        assertEquals(CopyResourceDto.class, enu.getClazz());
        assertEquals("resourceService", enu.getBeanName());
        assertEquals("batchSaveResource", enu.getFunctionName());
    }

    @Test
    public void testFusionCopyMap() {
        CopyEnum result = CopyEnum.FUSION_COPY_MAP.get("data_resource");
        assertNotNull(result);
        assertSame(CopyEnum.RSOURCE, result);
    }

    @Test
    public void testFusionCopyMapUnknown() {
        assertNull(CopyEnum.FUSION_COPY_MAP.get("unknown_table"));
    }
}
