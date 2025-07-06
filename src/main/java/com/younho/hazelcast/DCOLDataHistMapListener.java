package com.younho.hazelcast;

import com.hazelcast.core.EntryEvent;
import com.hazelcast.map.listener.EntryEvictedListener;
import com.hazelcast.map.listener.EntryExpiredListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.younho.hazelcast.HazelcastManager.DCOL_HIST;

@Component
public class DCOLDataHistMapListener implements EntryEvictedListener<Long, DCOLDataHist>, EntryExpiredListener<Long, DCOLDataHist> {
    private static final Logger logger = LoggerFactory.getLogger(DCOLDataHistMapListener.class);
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    @Override
    public void entryEvicted(EntryEvent<Long, DCOLDataHist> event) {
        logDetails("EVICTION_BY_LRU", event);
    }

    @Override
    public void entryExpired(EntryEvent<Long, DCOLDataHist> event) {
        logDetails("EVICTION_BY_TTL_EXPIRED", event);
    }

    private void logDetails(String cause, EntryEvent<Long, DCOLDataHist> event) {
        Long key = event.getKey();
        DCOLDataHist oldValue = event.getOldValue();

        if (oldValue == null) {
            logger.warn("{} DETECTED on map [{}]: Key={}, Details=\"Old value not available\"", cause, DCOL_HIST, key);
            return;
        }

        StringBuilder details = new StringBuilder();
        details.append("ID=" + key);
        details.append("EQP_ID=" + oldValue.getEqpId() + " ");
        details.append("WORK_ID=" + oldValue.getWorkId() + " ");
        details.append("CONTROL_JOB_ID=" + oldValue.getControlJobId() + " ");
        details.append("PROCESS_JOB_ID=" + oldValue.getProcessJobId() + " ");
        details.append("DCOL_NAME=" + oldValue.getDcolName() + " ");
        details.append("DCOL_ORDER=" + oldValue.getDcolOrder() + " ");

        Date dcolDate = oldValue.getDcolDate();
        details.append("DCOL_DATE=" + (dcolDate != null ? dateFormat.format(dcolDate) : "null") + " ");
        details.append("DCOL_VALUE=" + oldValue.getDcolValue());

        logger.warn("{} DETECTED on map [{}]: {}", cause, DCOL_HIST, details);
    }
}
