package com.younho.hazelcast;

import com.hazelcast.config.*;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Component
public class HazelcastManager {
    private static final Logger logger = LoggerFactory.getLogger(HazelcastManager.class);

    private HazelcastInstance hazelcastInstance;

    @Autowired
    public HazelcastManager() {
        // TODO 필요 시 의존성 주입
    }

    @PostConstruct
    public void initialize() {
        String serverName = System.getProperty("serverName");
        String appName = System.getProperty("appName");
        String clusterName = System.getProperty("msgGroup");
        String instanceName = serverName + "-" + appName;
        logger.info("Resolved Hazelcast Names -> Cluster: [{}], Instance: [{}]", clusterName, instanceName);

        Config config = new Config();
        config.setClusterName(clusterName);
        config.setInstanceName(instanceName);
        config.setProperty("hazelcast.logging.type", "slf4j"); // Setting Logging Type.

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
        logger.info("Hazelcast instance '{}' initialized successfully and joined cluster '{}'.", instanceName, clusterName);
    }

    private void configureDcolHistMap(Config config) {
        MapConfig mapConfig = config.getMapConfig("dcolHist");
        mapConfig.setBackupCount(1)
                // If the majority of your cluster operations are reads (get) and writes (put), leaving the data in BINARY format is most efficient.
                .setInMemoryFormat(InMemoryFormat.BINARY)
                .setTimeToLiveSeconds(259200); // 3 days

        EvictionConfig evictionConfig = new EvictionConfig();
        evictionConfig.setEvictionPolicy(EvictionPolicy.LRU);
        evictionConfig.setMaxSizePolicy(MaxSizePolicy.PER_NODE);
        evictionConfig.setSize(1000000); // TODO size 고민
        mapConfig.setEvictionConfig(evictionConfig);

        // partial attribute prefixes may be matched for the ordered composite indexes
        IndexConfig indexConfig = new IndexConfig(IndexType.SORTED, "eqpId", "workId", "controlJobId", "processJobId");
        mapConfig.addIndexConfig(indexConfig);
    }

    /**
     * Initiates a graceful shutdown of this Hazelcast member.
     * The cluster will automatically promote backup partitions to primaries for any data
     * owned by this node, ensuring no data loss during planned shutdowns or failovers.
     */
    @PreDestroy
    public void shutdown() {
        // TODO Manager 를 통해 application 을 종료했을 때 shutdown 메소드가 동작하는지 확인 필요.
        if (this.hazelcastInstance != null) {
            logger.info("Shutting down Hazelcast instance...");
            this.hazelcastInstance.shutdown();
            logger.info("Hazelcast instance shut down successfully.");
        }
    }

    public HazelcastInstance getInstance() {
        if (this.hazelcastInstance == null) {
            throw new IllegalStateException("HazelcastManager is not initialized yet.");
        }
        return this.hazelcastInstance;
    }
}
