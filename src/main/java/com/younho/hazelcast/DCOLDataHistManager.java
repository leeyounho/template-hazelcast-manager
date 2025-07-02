package com.younho.hazelcast;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Component // TODO
public class DCOLDataHistManager {
    private final DCOLDataHistRepository delegate;

    @Autowired
    public DCOLDataHistManager(HazelcastManager hazelcastManager, DCOLDataHistDbRepository dbRepository, DCOLDataHistHazelcastRepository cacheRepository) {
        if (hazelcastManager.isInitialized()) this.delegate = cacheRepository;
        else this.delegate = dbRepository;
    }

    public DCOLDataHist get(Serializable id) {
        return delegate.get(id);
    }

    public List<DCOLDataHist> getAll() {
        return delegate.getAll();
    }

    public List<DCOLDataHist> getByAttribute(String attrName, Object attrValue) {
        return delegate.getByAttribute(attrName, attrValue);
    }

    public List<DCOLDataHist> getByAttributes(Map<String, Object> attributes) {
        return delegate.getByAttributes(attributes);
    }

    public void update(DCOLDataHist dcolDataHist) {
        delegate.update(dcolDataHist);
    }

    public void delete(DCOLDataHist data) {
        delegate.delete(data);
    }

    public void deleteAll(Collection<DCOLDataHist> records) {
        delegate.deleteAll(records);
    }

    public void deleteByAttribute(String attrName, Object attrValue) {
        delegate.deleteByAttribute(attrName, attrValue);
    }

    public void deleteByAttributes(Map<String, Object> attributes) {
        delegate.deleteByAttributes(attributes);
    }

    public void deleteByEqpWork(String eqpId, String workId) {
        delegate.deleteByEqpWork(eqpId, workId);
    }

    public void deleteByEqpWorkProcJob(String eqpId, String workId, String processJobId) {
        delegate.deleteByEqpWorkProcJob(eqpId, workId, processJobId);
    }

    public void save(DCOLDataHist dcolDataHist) {
        delegate.save(dcolDataHist);
    }

    public void saveOrUpdate(DCOLDataHist dcolDataHist) {
        delegate.saveOrUpdate(dcolDataHist);
    }

    public void saveOrUpdateAll(Collection<DCOLDataHist> records) {
        delegate.saveOrUpdateAll(records);
    }
}