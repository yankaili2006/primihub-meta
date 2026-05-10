package com.primihub.simple.service;

import com.primihub.entity.DataSet;
import com.primihub.simple.base.BaseResultEntity;
import com.primihub.simple.base.BaseResultEnum;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class FrontDataSetServiceTest {

    @Mock
    private DataSetService dataSetService;

    @InjectMocks
    private FrontDataSetService frontDataSetService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testOne_Success() {
        DataSet ds = new DataSet("id1", "acc", "mysql", "addr", "public");

        BaseResultEntity result = frontDataSetService.one(ds);

        assertEquals(Integer.valueOf(0), result.getCode());
        assertEquals(Integer.valueOf(1), ds.getHolder());
        verify(dataSetService).saveDataSet(ds);
    }

    @Test
    public void testOne_Exception() {
        doThrow(new RuntimeException("save failed")).when(dataSetService).saveDataSet(any());

        BaseResultEntity result = frontDataSetService.one(new DataSet("id1", "acc", "mysql", "addr", "public"));

        assertEquals(BaseResultEnum.FAILURE.getReturnCode(), result.getCode());
    }

    @Test
    public void testDelete_Success() {
        DataSet ds = new DataSet("id1", "acc", "mysql", "addr", "public");

        BaseResultEntity result = frontDataSetService.delete(ds);

        assertEquals(Integer.valueOf(0), result.getCode());
        assertEquals(Integer.valueOf(1), ds.getHolder());
        verify(dataSetService).deleteDataSet(ds);
    }

    @Test
    public void testDelete_Exception() {
        doThrow(new RuntimeException("delete failed")).when(dataSetService).deleteDataSet(any());

        BaseResultEntity result = frontDataSetService.delete(new DataSet("id1", "acc", "mysql", "addr", "public"));

        assertEquals(BaseResultEnum.FAILURE.getReturnCode(), result.getCode());
    }

    @Test
    public void testMany_Success() {
        DataSet ds1 = new DataSet("id1", "acc", "mysql", "addr", "public");
        DataSet ds2 = new DataSet("id2", "acc", "mysql", "addr", "public");

        BaseResultEntity result = frontDataSetService.many(Arrays.asList(ds1, ds2));

        assertEquals(Integer.valueOf(0), result.getCode());
        assertEquals(Integer.valueOf(1), ds1.getHolder());
        assertEquals(Integer.valueOf(1), ds2.getHolder());
        verify(dataSetService, times(2)).saveDataSet(any());
    }

    @Test
    public void testMany_EmptyList() {
        BaseResultEntity result = frontDataSetService.many(Arrays.asList());

        assertEquals(Integer.valueOf(0), result.getCode());
        verify(dataSetService, never()).saveDataSet(any());
    }

    @Test
    public void testMany_Exception() {
        doThrow(new RuntimeException("batch save failed")).when(dataSetService).saveDataSet(any());

        BaseResultEntity result = frontDataSetService.many(
            Arrays.asList(new DataSet("id1", "acc", "mysql", "addr", "public"))
        );

        assertEquals(BaseResultEnum.FAILURE.getReturnCode(), result.getCode());
    }
}
