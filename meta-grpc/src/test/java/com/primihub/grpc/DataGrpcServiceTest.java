package com.primihub.grpc;

import com.primihub.entity.DataSet;
import com.primihub.service.StorageService;
import io.grpc.stub.StreamObserver;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DataGrpcServiceTest {

    @Mock
    private StorageService storageService;

    @Mock
    private StreamObserver<NewDatasetResponse> newDatasetObserver;

    @Mock
    private StreamObserver<GetDatasetResponse> getDatasetObserver;

    @InjectMocks
    private DataGrpcService dataGrpcService;

    @Captor
    private ArgumentCaptor<NewDatasetResponse> newDatasetCaptor;

    @Captor
    private ArgumentCaptor<GetDatasetResponse> getDatasetCaptor;

    private MetaInfo validMetaInfo;

    @Before
    public void setUp() {
        validMetaInfo = MetaInfo.newBuilder()
                .setId("ds-001")
                .setAccessInfo("jdbc:mysql://localhost:3306/test")
                .setDriver("mysql")
                .setAddress("localhost:3306")
                .setVisibility(MetaInfo.Visibility.PUBLIC)
                .build();
    }

    @Test
    public void testNewDataset_Register_Success() {
        NewDatasetRequest request = NewDatasetRequest.newBuilder()
                .setOpType(NewDatasetRequest.Operator.REGISTER)
                .setMetaInfo(validMetaInfo)
                .build();

        dataGrpcService.newDataset(request, newDatasetObserver);

        verify(storageService).saveDataSet(any(DataSet.class));
        verify(newDatasetObserver).onNext(newDatasetCaptor.capture());
        verify(newDatasetObserver).onCompleted();

        NewDatasetResponse response = newDatasetCaptor.getValue();
        assertEquals(NewDatasetResponse.ResultCode.SUCCESS, response.getRetCode());
        assertTrue(response.getDatasetUrl().contains("ds-001"));
    }

    @Test
    public void testNewDataset_Update_Success() {
        MetaInfo metaInfo = MetaInfo.newBuilder()
                .setId("ds-001")
                .setAccessInfo("jdbc:mysql://localhost:3306/test")
                .setDriver("mysql")
                .setAddress("localhost:3306")
                .setVisibility(MetaInfo.Visibility.PRIVATE)
                .build();

        NewDatasetRequest request = NewDatasetRequest.newBuilder()
                .setOpType(NewDatasetRequest.Operator.UPDATE)
                .setMetaInfo(metaInfo)
                .build();

        dataGrpcService.newDataset(request, newDatasetObserver);

        verify(storageService).updateDataSet(any(DataSet.class));
        verify(newDatasetObserver).onNext(newDatasetCaptor.capture());
        verify(newDatasetObserver).onCompleted();

        NewDatasetResponse response = newDatasetCaptor.getValue();
        assertEquals(NewDatasetResponse.ResultCode.SUCCESS, response.getRetCode());
    }

    @Test
    public void testNewDataset_Unregister_Success() {
        NewDatasetRequest request = NewDatasetRequest.newBuilder()
                .setOpType(NewDatasetRequest.Operator.UNREGISTER)
                .setMetaInfo(validMetaInfo)
                .build();

        dataGrpcService.newDataset(request, newDatasetObserver);

        verify(storageService).deleteDataSet(any(DataSet.class));
        verify(newDatasetObserver).onNext(newDatasetCaptor.capture());
        verify(newDatasetObserver).onCompleted();

        NewDatasetResponse response = newDatasetCaptor.getValue();
        assertEquals(NewDatasetResponse.ResultCode.SUCCESS, response.getRetCode());
    }

    @Test
    public void testNewDataset_EmptyId_ReturnsFail() {
        MetaInfo noIdMeta = MetaInfo.newBuilder()
                .setId("")
                .build();

        NewDatasetRequest request = NewDatasetRequest.newBuilder()
                .setOpType(NewDatasetRequest.Operator.REGISTER)
                .setMetaInfo(noIdMeta)
                .build();

        dataGrpcService.newDataset(request, newDatasetObserver);

        verify(storageService, never()).saveDataSet(any());
        verify(newDatasetObserver).onNext(newDatasetCaptor.capture());
        verify(newDatasetObserver).onCompleted();

        NewDatasetResponse response = newDatasetCaptor.getValue();
        assertEquals(NewDatasetResponse.ResultCode.FAIL, response.getRetCode());
        assertTrue(response.getRetMsg().contains("id"));
    }

    @Test
    public void testNewDataset_NullId_ReturnsFail() {
        MetaInfo nullIdMeta = MetaInfo.newBuilder().build();

        NewDatasetRequest request = NewDatasetRequest.newBuilder()
                .setOpType(NewDatasetRequest.Operator.REGISTER)
                .setMetaInfo(nullIdMeta)
                .build();

        dataGrpcService.newDataset(request, newDatasetObserver);

        verify(newDatasetObserver).onNext(newDatasetCaptor.capture());
        NewDatasetResponse response = newDatasetCaptor.getValue();
        assertEquals(NewDatasetResponse.ResultCode.FAIL, response.getRetCode());
    }

    @Test
    public void testNewDataset_Register_MissingAccessInfo_ReturnsFail() {
        MetaInfo metaInfo = MetaInfo.newBuilder()
                .setId("ds-001")
                .setDriver("mysql")
                .setAddress("localhost")
                .build();

        NewDatasetRequest request = NewDatasetRequest.newBuilder()
                .setOpType(NewDatasetRequest.Operator.REGISTER)
                .setMetaInfo(metaInfo)
                .build();

        dataGrpcService.newDataset(request, newDatasetObserver);

        verify(storageService, never()).saveDataSet(any());
        verify(newDatasetObserver).onNext(newDatasetCaptor.capture());
        NewDatasetResponse response = newDatasetCaptor.getValue();
        assertEquals(NewDatasetResponse.ResultCode.FAIL, response.getRetCode());
        assertTrue(response.getRetMsg().contains("accessInfo"));
    }

    @Test
    public void testNewDataset_Register_MissingDriver_ReturnsFail() {
        MetaInfo metaInfo = MetaInfo.newBuilder()
                .setId("ds-001")
                .setAccessInfo("jdbc:mysql://localhost:3306/test")
                .setAddress("localhost")
                .build();

        NewDatasetRequest request = NewDatasetRequest.newBuilder()
                .setOpType(NewDatasetRequest.Operator.REGISTER)
                .setMetaInfo(metaInfo)
                .build();

        dataGrpcService.newDataset(request, newDatasetObserver);

        verify(storageService, never()).saveDataSet(any());
        verify(newDatasetObserver).onNext(newDatasetCaptor.capture());
        NewDatasetResponse response = newDatasetCaptor.getValue();
        assertEquals(NewDatasetResponse.ResultCode.FAIL, response.getRetCode());
        assertTrue(response.getRetMsg().contains("driver"));
    }

    @Test
    public void testNewDataset_Register_MissingAddress_ReturnsFail() {
        MetaInfo metaInfo = MetaInfo.newBuilder()
                .setId("ds-001")
                .setAccessInfo("jdbc:mysql://localhost:3306/test")
                .setDriver("mysql")
                .build();

        NewDatasetRequest request = NewDatasetRequest.newBuilder()
                .setOpType(NewDatasetRequest.Operator.REGISTER)
                .setMetaInfo(metaInfo)
                .build();

        dataGrpcService.newDataset(request, newDatasetObserver);

        verify(storageService, never()).saveDataSet(any());
        verify(newDatasetObserver).onNext(newDatasetCaptor.capture());
        NewDatasetResponse response = newDatasetCaptor.getValue();
        assertEquals(NewDatasetResponse.ResultCode.FAIL, response.getRetCode());
        assertTrue(response.getRetMsg().contains("address"));
    }

    @Test
    public void testNewDataset_Update_MissingAddress_ReturnsFail() {
        MetaInfo metaInfo = MetaInfo.newBuilder()
                .setId("ds-001")
                .setAccessInfo("jdbc:mysql://localhost:3306/test")
                .setDriver("mysql")
                .build();

        NewDatasetRequest request = NewDatasetRequest.newBuilder()
                .setOpType(NewDatasetRequest.Operator.UPDATE)
                .setMetaInfo(metaInfo)
                .build();

        dataGrpcService.newDataset(request, newDatasetObserver);

        verify(storageService, never()).updateDataSet(any());
        verify(newDatasetObserver).onNext(newDatasetCaptor.capture());
        NewDatasetResponse response = newDatasetCaptor.getValue();
        assertEquals(NewDatasetResponse.ResultCode.FAIL, response.getRetCode());
        assertTrue(response.getRetMsg().contains("address"));
    }

    @Test
    public void testNewDataset_Register_SetsFields() {
        DataTypeInfo dataType = DataTypeInfo.newBuilder()
                .setName("age")
                .setType(DataTypeInfo.PlainDataType.INT32)
                .build();
        MetaInfo metaInfo = MetaInfo.newBuilder()
                .setId("ds-003")
                .setAccessInfo("jdbc:mysql://localhost:3306/test")
                .setDriver("mysql")
                .setAddress("localhost")
                .addDataType(dataType)
                .build();

        NewDatasetRequest request = NewDatasetRequest.newBuilder()
                .setOpType(NewDatasetRequest.Operator.REGISTER)
                .setMetaInfo(metaInfo)
                .build();

        dataGrpcService.newDataset(request, newDatasetObserver);

        ArgumentCaptor<DataSet> dataSetCaptor = ArgumentCaptor.forClass(DataSet.class);
        verify(storageService).saveDataSet(dataSetCaptor.capture());
        DataSet captured = dataSetCaptor.getValue();
        assertEquals("ds-003", captured.getId());
        assertEquals("age,INT32", captured.getFields());
    }

    @Test
    public void testGetDataset_ByIds() {
        List<DataSet> dataSets = new ArrayList<>();
        dataSets.add(new DataSet("ds-001", "access1", "mysql", "addr1", "PUBLIC"));
        dataSets.add(new DataSet("ds-002", "access2", "postgresql", "addr2", "PRIVATE"));
        when(storageService.getByIds(anySet())).thenReturn(dataSets);

        GetDatasetRequest request = GetDatasetRequest.newBuilder()
                .addId("ds-001")
                .addId("ds-002")
                .build();

        dataGrpcService.getDataset(request, getDatasetObserver);

        verify(storageService).getByIds(anySet());
        verify(storageService, never()).getAll();
        verify(getDatasetObserver).onNext(getDatasetCaptor.capture());
        verify(getDatasetObserver).onCompleted();

        GetDatasetResponse response = getDatasetCaptor.getValue();
        assertEquals(GetDatasetResponse.ResultCode.SUCCESS, response.getRetCode());
        assertEquals(2, response.getDataSetCount());
    }

    @Test
    public void testGetDataset_All() {
        List<DataSet> dataSets = Collections.singletonList(
                new DataSet("ds-001", "access1", "mysql", "addr1", "PUBLIC")
        );
        when(storageService.getAll()).thenReturn(dataSets);

        GetDatasetRequest request = GetDatasetRequest.newBuilder().build();

        dataGrpcService.getDataset(request, getDatasetObserver);

        verify(storageService).getAll();
        verify(storageService, never()).getByIds(anySet());
        verify(getDatasetObserver).onNext(getDatasetCaptor.capture());
        verify(getDatasetObserver).onCompleted();

        GetDatasetResponse response = getDatasetCaptor.getValue();
        assertEquals(GetDatasetResponse.ResultCode.SUCCESS, response.getRetCode());
        assertEquals(1, response.getDataSetCount());
    }

    @Test
    public void testGetDataset_EmptyResult() {
        when(storageService.getAll()).thenReturn(new ArrayList<>());

        GetDatasetRequest request = GetDatasetRequest.newBuilder().build();

        dataGrpcService.getDataset(request, getDatasetObserver);

        verify(getDatasetObserver).onNext(getDatasetCaptor.capture());
        GetDatasetResponse response = getDatasetCaptor.getValue();
        assertEquals(0, response.getDataSetCount());
    }

    @Test
    public void testDataModelConvertVo_WithFields() {
        DataSet dataSet = new DataSet("ds-001", "access1", "mysql", "addr1", "PUBLIC");
        dataSet.setFields("name,STRING;age,INT32;score,DOUBLE");
        dataSet.setAvailable("Unavailable");

        DatasetData datasetData = DataGrpcService.dataModelConvertVo(dataSet);

        assertNotNull(datasetData);
        MetaInfo metaInfo = datasetData.getMetaInfo();
        assertEquals("ds-001", metaInfo.getId());
        assertEquals("access1", metaInfo.getAccessInfo());
        assertEquals("mysql", metaInfo.getDriver());
        assertEquals("addr1", metaInfo.getAddress());
        assertEquals(MetaInfo.Visibility.PUBLIC, metaInfo.getVisibility());
        assertEquals(DatasetData.Status.Unavailable, datasetData.getAvailable());
        assertEquals(3, metaInfo.getDataTypeCount());
        assertEquals("name", metaInfo.getDataType(0).getName());
        assertEquals(DataTypeInfo.PlainDataType.STRING, metaInfo.getDataType(0).getType());
        assertEquals("age", metaInfo.getDataType(1).getName());
        assertEquals(DataTypeInfo.PlainDataType.INT32, metaInfo.getDataType(1).getType());
    }

    @Test
    public void testDataModelConvertVo_WithoutFields() {
        DataSet dataSet = new DataSet("ds-001", "access1", "mysql", "addr1", null);

        DatasetData datasetData = DataGrpcService.dataModelConvertVo(dataSet);

        assertNotNull(datasetData);
        assertEquals(0, datasetData.getMetaInfo().getDataTypeCount());
        assertEquals(DatasetData.Status.Available, datasetData.getAvailable());
    }
}
