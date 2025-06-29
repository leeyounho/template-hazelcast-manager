package com.younho.hazelcast;

import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class DCOLDataHistDbRepository implements DCOLDataHistRepository {
    // TODO 기존 DCOLDataHistManager 로직 이관 필요

    @Override
    public DCOLDataHist get(Serializable id) {
        return null;
    }

    @Override
    public List<DCOLDataHist> getAll() {
        return Collections.emptyList();
    }

    @Override
    public List<DCOLDataHist> getByAttribute(String attrName, Object attrValue) {
        return Collections.emptyList();
    }

    @Override
    public List<DCOLDataHist> getByAttributes(Map<String, Object> attributes) {
        return Collections.emptyList();
    }

    @Override
    public void update(DCOLDataHist dcolDataHist) {

    }

    @Override
    public void delete(DCOLDataHist data) {

    }

    @Override
    public void deleteAll(Collection<DCOLDataHist> records) {

    }

    @Override
    public void deleteByAttribute(String attrName, Object attrValue) {

    }

    @Override
    public void deleteByAttributes(Map<String, Object> attributes) {

    }

    @Override
    public void deleteByEqpWork(String eqpId, String workId) {

    }

    @Override
    public void deleteByEqpWorkProcJob(String eqpId, String workId, String processJobId) {

    }

    @Override
    public void save(DCOLDataHist dcolDataHist) {

    }

    @Override
    public void saveOrUpdate(DCOLDataHist dcolDataHist) {

    }

    @Override
    public void saveOrUpdateAll(Collection<DCOLDataHist> records) {

    }
}
