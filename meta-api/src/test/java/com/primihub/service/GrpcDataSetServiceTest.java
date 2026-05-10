package com.primihub.service;

import com.primihub.entity.DataSet;
import com.primihub.entity.base.BaseResultEntity;
import com.primihub.repository.DataSetRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class GrpcDataSetServiceTest {

    @Mock
    private DataSetRepository dataSetRepository;

    @Mock
    private AsyncService asyncService;

    @InjectMocks
    private GrpcDataSetService grpcDataSetService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSaveDataSet_New() {
        DataSet ds = new DataSet("id1", "access", "mysql", "localhost", "public");
        when(dataSetRepository.getDataSetById("id1")).thenReturn(null);

        grpcDataSetService.saveDataSet(ds);

        verify(dataSetRepository).insertDataSet(ds);
        verify(dataSetRepository, never()).updateDataSet(any());
        verify(asyncService).noticeResource(null, ds);
    }

    @Test
    public void testSaveDataSet_Existing() {
        DataSet existing = new DataSet("id1", "old", "mysql", "old", "public");
        DataSet ds = new DataSet("id1", "new", "mysql", "new", "public");
        when(dataSetRepository.getDataSetById("id1")).thenReturn(existing);

        grpcDataSetService.saveDataSet(ds);

        verify(dataSetRepository, never()).insertDataSet(any());
        verify(dataSetRepository).updateDataSet(ds);
        verify(asyncService).noticeResource(existing, ds);
    }

    @Test
    public void testUpdateDataSet() {
        DataSet existing = new DataSet("id1", "old", "mysql", "old", "public");
        DataSet ds = new DataSet("id1", "new", "mysql", "new", "public");
        when(dataSetRepository.getDataSetById("id1")).thenReturn(existing);

        grpcDataSetService.updateDataSet(ds);

        verify(dataSetRepository).updateDataSet(ds);
        verify(asyncService).noticeResource(existing, ds);
    }

    @Test
    public void testDeleteDataSet() {
        DataSet ds = new DataSet("id1", "access", "mysql", "localhost", "public");

        grpcDataSetService.deleteDataSet(ds);

        verify(dataSetRepository).deleteDataSet(ds);
    }

    @Test
    public void testGetAll() {
        List<DataSet> list = new ArrayList<>();
        list.add(new DataSet("id1", "access", "mysql", "localhost", "public"));
        when(dataSetRepository.getDataSetOwnAll()).thenReturn(list);

        List<DataSet> result = grpcDataSetService.getAll();

        assertEquals(1, result.size());
        verify(dataSetRepository).getDataSetOwnAll();
    }

    @Test
    public void testGetByIds() {
        Set<String> ids = new HashSet<>(Arrays.asList("id1", "id2"));
        List<DataSet> list = new ArrayList<>();
        when(dataSetRepository.getDataSetByIds(ids)).thenReturn(list);

        List<DataSet> result = grpcDataSetService.getByIds(ids);

        assertSame(list, result);
        verify(dataSetRepository).getDataSetByIds(ids);
    }

    @Test
    public void testSaveBatch() {
        List<DataSet> list = new ArrayList<>();
        list.add(new DataSet("id1", "access", "mysql", "localhost", "public"));

        grpcDataSetService.saveBatch(list);

        verify(dataSetRepository).insertBatchDataSet(list);
    }
}
