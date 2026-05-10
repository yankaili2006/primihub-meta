package com.primihub.simple.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.primihub.entity.DataSet;
import com.primihub.service.StorageService;
import com.primihub.simple.repository.DataSetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class DataSetService implements StorageService {
    @Autowired
    private DataSetRepository dataSetRepository;
    @Autowired
    private AsyncService asyncService;

    @Override
    public void saveDataSet(DataSet dataSet) {
        DataSet byId = getById(dataSet.getId());
        if (byId!=null){
            dataSetRepository.updateById(dataSet);
        }else {
            dataSetRepository.insert(dataSet);
        }
        if (dataSet.getHolder() == 0){
            asyncService.syncOne(dataSet);
        }
    }

    @Override
    public void updateDataSet(DataSet dataSet) {
        dataSetRepository.updateById(dataSet);
        if (dataSet.getHolder() == 0){
            asyncService.syncOne(dataSet);
        }
    }

    @Override
    public void deleteDataSet(DataSet dataSet) {
        dataSetRepository.deleteById(dataSet.getId());
        if (dataSet.getHolder() == 0){
            asyncService.syncDelete(dataSet);
        }

    }

    @Override
    public List<DataSet> getAll() {
        QueryWrapper<DataSet> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("holder", 0);
        return dataSetRepository.selectList(queryWrapper);
    }

    @Override
    public List<DataSet> getByIds(Set<String> ids) {
        return dataSetRepository.selectBatchIds(ids);
    }

    @Override
    public void saveBatch(List<DataSet> list) {
        for (DataSet dataSet : list) {
            DataSet byId = getById(dataSet.getId());
            if (byId!=null){
                dataSetRepository.updateById(dataSet);
            }else {
                dataSetRepository.insert(dataSet);
            }
        }
    }

    public DataSet getById(String id){
        return dataSetRepository.selectById(id);
    }
}
