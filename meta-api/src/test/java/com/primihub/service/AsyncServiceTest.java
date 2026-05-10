package com.primihub.service;

import com.primihub.entity.base.BaseResultEntity;
import com.primihub.service.feign.NoticeService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

public class AsyncServiceTest {

    @Mock
    private NoticeService noticeService;

    @InjectMocks
    private AsyncService asyncService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testNoticeResource_Success() {
        when(noticeService.noticeResource("id1")).thenReturn(BaseResultEntity.success());

        asyncService.noticeResource(null, createDataSet("id1"));

        verify(noticeService).noticeResource("id1");
        verify(noticeService, never()).testDataSet(anyString());
    }

    @Test
    public void testNoticeResource_FailureThenTest() {
        BaseResultEntity failureResult = new BaseResultEntity();
        failureResult.setCode(-1);
        when(noticeService.noticeResource("id1")).thenReturn(failureResult);
        when(noticeService.testDataSet("id1")).thenReturn(BaseResultEntity.success());

        asyncService.noticeResource(null, createDataSet("id1"));

        verify(noticeService).noticeResource("id1");
        verify(noticeService).testDataSet("id1");
    }

    private com.primihub.entity.DataSet createDataSet(String id) {
        com.primihub.entity.DataSet ds = new com.primihub.entity.DataSet();
        ds.setId(id);
        return ds;
    }
}
