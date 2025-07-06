package com.younho.hazelcast;

import com.hazelcast.config.*;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Component
public class HazelcastManager {
    private static final Logger logger = LoggerFactory.getLogger(HazelcastManager.class);
    public static final String DCOL_HIST = "dcolHist";

    private final DCOLDataHistMapListener dcolDataHistMapListener;
    private final MeterRegistry meterRegistry;
    private HazelcastInstance hazelcastInstance;

    @Autowired
    public HazelcastManager(DCOLDataHistMapListener dcolDataHistMapListener, MeterRegistry meterRegistry) {
        this.dcolDataHistMapListener = dcolDataHistMapListener;
        this.meterRegistry = meterRegistry;
    }

    @PostConstruct
    public void initialize() {
        // TODO 라인 파라미터가 꺼져있으면 바로 return 하는 코드 추가

        String serverName = System.getProperty("serverName");
        String appName = System.getProperty("appName");
        String clusterName = System.getProperty("msgGroup");
        String instanceName = serverName + "-" + appName;
        logger.info("[Hazelcast] Resolved Hazelcast Names -> Cluster: [{}], Instance: [{}]", clusterName, instanceName);

        Config config = new Config();
        config.setClusterName(clusterName);
        config.setInstanceName(instanceName);
        config.setProperty("hazelcast.logging.type", "slf4j"); // Setting Logging Type.

        // For metrics
        config.setProperty("hazelcast.metrics.enabled", "true");
        config.setProperty("hazelcast.metrics.micrometer.enabled", "true");

        // Serialization
        config.getSerializationConfig()
                .getCompactSerializationConfig()
                .addSerializer(new DCOLDataHistSerializer());

        // Discovering Members by TCP
        NetworkConfig networkConfig = config.getNetworkConfig();
        networkConfig.setPort(8000)
                .setPortAutoIncrement(true)
                .setPortCount(100);
        // Allows the socket to bind to an address that is in the TIME_WAIT state, enabling a fast restart of the member.
        networkConfig.setReuseAddress(true);
        networkConfig.getInterfaces()
                .setEnabled(true)
                .addInterface("127.0.0.1"); // TODO 내 server IP 추가 필요
        networkConfig.getJoin().getTcpIpConfig()
                .addMember("127.0.0.1") // TODO IP 변경 필요
                // TODO 동일 msgGroup의 server IP를 addMember 필요
                .setEnabled(true);

        // Partition Group Configuration
        PartitionGroupConfig partitionGroupConfig = config.getPartitionGroupConfig();
        partitionGroupConfig.setEnabled(true)
                // That means backups are created in the other host and each host is accepted as one partition group.
                .setGroupType(PartitionGroupConfig.MemberGroupType.HOST_AWARE);

        configureDcolHistMap(config);

        this.hazelcastInstance = Hazelcast.newHazelcastInstance(config);
        logger.info("[Hazelcast] Hazelcast instance '{}' initialized successfully and joined cluster '{}'.", instanceName, clusterName);

        addMapListeners();
    }

    private void addMapListeners() {
        try {
            IMap<Long, DCOLDataHist> dcolHistMap = this.hazelcastInstance.getMap(DCOL_HIST);
            dcolHistMap.addEntryListener(this.dcolDataHistMapListener, true);

            logger.info("[Hazelcast] Successfully added DCOLDataHistEvictionListener to '{}' map.", DCOL_HIST);
        } catch (Exception e) {
            logger.error("[Hazelcast] Failed to add listener to '{}' map.", DCOL_HIST, e);
        }
    }

    private void configureDcolHistMap(Config config) {
        MapConfig mapConfig = config.getMapConfig(DCOL_HIST);
        mapConfig.setBackupCount(1)
                // If the majority of your cluster operations are reads (get) and writes (put), leaving the data in BINARY format is most efficient.
                .setInMemoryFormat(InMemoryFormat.BINARY)
                .setTimeToLiveSeconds(259200); // 3 days

        EvictionConfig evictionConfig = new EvictionConfig();
        evictionConfig.setEvictionPolicy(EvictionPolicy.LRU);
        evictionConfig.setMaxSizePolicy(MaxSizePolicy.PER_NODE);
        evictionConfig.setSize(1000_0000);
        mapConfig.setEvictionConfig(evictionConfig);

        // partial attribute prefixes may be matched for the ordered composite indexes
        IndexConfig indexConfig = new IndexConfig(IndexType.SORTED, "eqpId", "workId", "controlJobId", "processJobId");
        mapConfig.addIndexConfig(indexConfig);
    }

    @PreDestroy
    public void shutdown() {
        if (this.hazelcastInstance != null) {
            logger.info("[Hazelcast] Shutting down Hazelcast instance...");
            this.hazelcastInstance.shutdown();
            logger.info("[Hazelcast] Hazelcast instance shut down successfully.");
        }
    }

    public HazelcastInstance getInstance() {
        if (this.hazelcastInstance == null) {
            throw new IllegalStateException("HazelcastManager is not initialized yet.");
        }
        return this.hazelcastInstance;
    }

    public boolean isInitialized() {
        return this.hazelcastInstance != null;
    }
}
