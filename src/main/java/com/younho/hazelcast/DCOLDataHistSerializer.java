package com.younho.hazelcast;

import com.hazelcast.nio.serialization.compact.CompactReader;
import com.hazelcast.nio.serialization.compact.CompactSerializer;
import com.hazelcast.nio.serialization.compact.CompactWriter;

import java.util.Date;

public class DCOLDataHistSerializer implements CompactSerializer<DCOLDataHist> {
    @Override
    public DCOLDataHist read(CompactReader reader) {
        Long id = reader.readNullableInt64("id");
        String eqpId = reader.readString("eqpId");
        String workId = reader.readString("workId");
        String controlJobId = reader.readString("controlJobId");
        String processJobId = reader.readString("processJobId");
        Date dcolDate = new Date(reader.readInt64("dcolDate"));
        String dcolName = reader.readString("dcolName");
        Long dcolOrder = reader.readNullableInt64("dcolOrder");
        String dcolValue = reader.readString("dcolValue");

        DCOLDataHist dcolDataHist = new DCOLDataHist();
        dcolDataHist.setId(id);
        dcolDataHist.setEqpId(eqpId);
        dcolDataHist.setWorkId(workId);
        dcolDataHist.setControlJobId(controlJobId);
        dcolDataHist.setProcessJobId(processJobId);
        dcolDataHist.setDcolDate(dcolDate);
        dcolDataHist.setDcolName(dcolName);
        dcolDataHist.setDcolOrder(dcolOrder);
        dcolDataHist.setDcolValue(dcolValue);

        return dcolDataHist;
    }

    @Override
    public void write(CompactWriter writer, DCOLDataHist data) {
        writer.writeNullableInt64("id", data.getId());
        writer.writeString("eqpId", data.getEqpId());
        writer.writeString("workId", data.getWorkId());
        writer.writeString("controlJobId", data.getControlJobId());
        writer.writeString("processJobId", data.getProcessJobId());
        writer.writeInt64("dcolDate", data.getDcolDate().getTime());
        writer.writeString("dcolName", data.getDcolName());
        writer.writeNullableInt64("dcolOrder", data.getDcolOrder());
        writer.writeString("dcolValue", data.getDcolValue());
    }

    @Override
    public Class<DCOLDataHist> getCompactClass() {
        return DCOLDataHist.class;
    }

    @Override
    public String getTypeName() {
        return "dcolHist";
    }
}
