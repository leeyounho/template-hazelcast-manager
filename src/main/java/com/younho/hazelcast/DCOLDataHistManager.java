package com.younho.hazelcast;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.flakeidgen.FlakeIdGenerator;
import com.hazelcast.map.IMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DCOLDataHistManager {
    private final HazelcastInstance hazelcastInstance;
    private final IMap<Long, DCOLDataHist> dcolHistMap;
    private final FlakeIdGenerator idGenerator;

    @Autowired
    public DCOLDataHistManager(HazelcastManager hazelcastManager) {
        this.hazelcastInstance = hazelcastManager.getInstance();
        this.dcolHistMap = hazelcastInstance.getMap("dcolHist");
        this.idGenerator = hazelcastInstance.getFlakeIdGenerator("dcolHistId");
    }

    public void saveData(DCOLDataHist data) {
        long newId = idGenerator.newId();
        data.setId(newId);
        dcolHistMap.put(newId, data);
    }

    public void deleteDataById(long id) {
        dcolHistMap.delete(id);
    }
}
