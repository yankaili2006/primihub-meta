package com.primihub.service;

import com.primihub.entity.base.BaseResultEntity;
import com.primihub.entity.base.BaseResultEnum;
import com.primihub.entity.fusion.FusionOrgan;
import com.primihub.repository.FusionRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class OrganServiceTest {

    @Mock
    private FusionRepository fusionRepository;

    @InjectMocks
    private OrganService organService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testOrganData_NewOrgan() {
        when(fusionRepository.getFusionOrganByGlobalId("org-1")).thenReturn(null);

        BaseResultEntity result = organService.organData("org-1", "TestOrgan");

        assertEquals(Integer.valueOf(0), result.getCode());
        verify(fusionRepository).insertFusionOrgan(any(FusionOrgan.class));
        verify(fusionRepository, never()).updateFusionOrganSpeByGlobalId(any());
    }

    @Test
    public void testOrganData_ExistingOrgan() {
        FusionOrgan existing = new FusionOrgan();
        existing.setGlobalId("org-1");
        existing.setGlobalName("OldName");
        when(fusionRepository.getFusionOrganByGlobalId("org-1")).thenReturn(existing);

        BaseResultEntity result = organService.organData("org-1", "NewName");

        assertEquals(Integer.valueOf(0), result.getCode());
        assertEquals("NewName", existing.getGlobalName());
        verify(fusionRepository, never()).insertFusionOrgan(any());
        verify(fusionRepository).updateFusionOrganSpeByGlobalId(existing);
    }

    @Test
    public void testOrganData_NullOrganName() {
        when(fusionRepository.getFusionOrganByGlobalId("org-1")).thenReturn(null);

        BaseResultEntity result = organService.organData("org-1", null);

        assertEquals(Integer.valueOf(0), result.getCode());
        verify(fusionRepository).insertFusionOrgan(any(FusionOrgan.class));
    }
}
