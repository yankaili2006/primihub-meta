package com.primihub.simple.service;

import com.primihub.entity.DataSet;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class DataSetServiceTest {

    @Mock
    private com.primihub.simple.repository.DataSetRepository dataSetRepository;

    @Mock
    private AsyncService asyncService;

    @InjectMocks
    private DataSetService dataSetService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSaveDataSet_Insert() {
        DataSet ds = new DataSet("id1", "acc", "mysql", "addr", "public", 0);
        when(dataSetRepository.selectById("id1")).thenReturn(null);

        dataSetService.saveDataSet(ds);

        verify(dataSetRepository).insert(ds);
        verify(dataSetRepository, never()).updateById(any());
        verify(asyncService).syncOne(ds);
    }

    @Test
    public void testSaveDataSet_Update() {
        DataSet ds = new DataSet("id1", "acc", "mysql", "addr", "public", 0);
        DataSet existing = new DataSet("id1", "old", "mysql", "old", "public");
        when(dataSetRepository.selectById("id1")).thenReturn(existing);

        dataSetService.saveDataSet(ds);

        verify(dataSetRepository).updateById(ds);
        verify(dataSetRepository, never()).insert(any());
        verify(asyncService).syncOne(ds);
    }

    @Test
    public void testSaveDataSet_HolderNotZero_NoSync() {
        DataSet ds = new DataSet("id1", "acc", "mysql", "addr", "public", 1);
        when(dataSetRepository.selectById("id1")).thenReturn(null);

        dataSetService.saveDataSet(ds);

        verify(dataSetRepository).insert(ds);
        verify(asyncService, never()).syncOne(any());
    }

    @Test
    public void testUpdateDataSet() {
        DataSet ds = new DataSet("id1", "acc", "mysql", "addr", "public", 0);

        dataSetService.updateDataSet(ds);

        verify(dataSetRepository).updateById(ds);
        verify(asyncService).syncOne(ds);
    }

    @Test
    public void testDeleteDataSet() {
        DataSet ds = new DataSet("id1", "acc", "mysql", "addr", "public", 0);

        dataSetService.deleteDataSet(ds);

        verify(dataSetRepository).deleteById("id1");
        verify(asyncService).syncDelete(ds);
    }

    @Test
    public void testGetAll() {
        List<DataSet> list = new ArrayList<>();
        list.add(new DataSet("id1", "acc", "mysql", "addr", "public", 0));
        when(dataSetRepository.selectList(any())).thenReturn(list);

        List<DataSet> result = dataSetService.getAll();

        assertEquals(1, result.size());
    }

    @Test
    public void testGetByIds() {
        Set<String> ids = new HashSet<>(Arrays.asList("id1", "id2"));
        List<DataSet> list = new ArrayList<>();
        when(dataSetRepository.selectBatchIds(ids)).thenReturn(list);

        List<DataSet> result = dataSetService.getByIds(ids);

        assertSame(list, result);
    }

    @Test
    public void testSaveBatch() {
        DataSet ds1 = new DataSet("id1", "acc", "mysql", "addr", "public");
        DataSet ds2 = new DataSet("id2", "acc", "mysql", "addr2", "public");
        when(dataSetRepository.selectById("id1")).thenReturn(null);
        when(dataSetRepository.selectById("id2")).thenReturn(new DataSet("id2", "old", "mysql", "old", "public"));

        dataSetService.saveBatch(Arrays.asList(ds1, ds2));

        verify(dataSetRepository).insert(ds1);
        verify(dataSetRepository).updateById(ds2);
    }

    @Test
    public void testGetById() {
        DataSet ds = new DataSet("id1", "acc", "mysql", "addr", "public");
        when(dataSetRepository.selectById("id1")).thenReturn(ds);

        DataSet result = dataSetService.getById("id1");

        assertSame(ds, result);
    }
}
