package com.younho.hazelcast;

import com.hazelcast.cluster.MembershipEvent;
import com.hazelcast.cluster.MembershipListener;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.LifecycleEvent;
import com.hazelcast.core.LifecycleListener;
import com.hazelcast.partition.PartitionLostEvent;
import com.hazelcast.partition.PartitionLostListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class HazelcastEventListener {
    private static final Logger logger = LoggerFactory.getLogger(HazelcastEventListener.class);

    private final HazelcastInstance hazelcastInstance;

    @Autowired
    public HazelcastEventListener(HazelcastInstance hazelcastInstance) {
        this.hazelcastInstance = hazelcastInstance;
    }

    @PostConstruct
    public void registerListeners() {
        hazelcastInstance.getCluster().addMembershipListener(new MyMembershipListener());
        hazelcastInstance.getLifecycleService().addLifecycleListener(new MyLifecycleListener());
        hazelcastInstance.getPartitionService().addPartitionLostListener(new MyPartitionLostListener());
        logger.info("Hazelcast event listener registered");
    }

    private static class MyMembershipListener implements MembershipListener {
        @Override
        public void memberAdded(MembershipEvent membershipEvent) {
            logger.warn("[Hazelcast] addedMember={} totalMember={}", membershipEvent.getMember(), membershipEvent.getMembers().size());
        }

        @Override
        public void memberRemoved(MembershipEvent membershipEvent) {
            logger.warn("[Hazelcast] removedMember={} totalMember={}", membershipEvent.getMember(), membershipEvent.getMembers().size());
        }
    }

    private static class MyLifecycleListener implements LifecycleListener {
        @Override
        public void stateChanged(LifecycleEvent event) {
            logger.warn("[Hazelcast] Local Hazelcast instance state changed={}", event.getState());
        }
    }

    private static class MyPartitionLostListener implements PartitionLostListener {
        @Override
        public void partitionLost(PartitionLostEvent event) {
            logger.error("[Hazelcast] Partition Lost! PartitionId: {}, Lost backup count: {}", event.getPartitionId(), event.getLostBackupCount());
        }
    }
}