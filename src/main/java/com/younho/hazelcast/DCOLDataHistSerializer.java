package com.younho.hazelcast;

import com.hazelcast.nio.serialization.HazelcastSerializationException;
import com.hazelcast.nio.serialization.compact.CompactReader;
import com.hazelcast.nio.serialization.compact.CompactSerializer;
import com.hazelcast.nio.serialization.compact.CompactWriter;
import org.xerial.snappy.Snappy;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static com.younho.hazelcast.HazelcastManager.DCOL_HIST;

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

        byte[] compressedValue = reader.readArrayOfInt8("dcolValue");
        String dcolValue;

        try {
            dcolValue = Snappy.uncompressString(compressedValue, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new HazelcastSerializationException("Failed to decompress dcolValue with Snappy", e);
        }

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

        byte[] compressedValue;
        try {
            compressedValue = Snappy.compress(data.getDcolValue(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new HazelcastSerializationException("Failed to compress dcolValue with Snappy", e);
        }
        writer.writeArrayOfInt8("dcolValue", compressedValue);
    }

    @Override
    public Class<DCOLDataHist> getCompactClass() {
        return DCOLDataHist.class;
    }

    @Override
    public String getTypeName() {
        return DCOL_HIST;
    }
}
