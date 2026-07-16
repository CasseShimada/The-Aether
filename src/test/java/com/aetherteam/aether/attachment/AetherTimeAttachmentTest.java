package com.aetherteam.aether.attachment;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AetherTimeAttachmentTest {
    @Test
    void appliesSynchronizedDayTimeAsLong() {
        AetherTimeAttachment attachment = new AetherTimeAttachment();

        attachment.executeSynced(
                AetherTimeAttachment.DAY_TIME_SYNC_KEY,
                AttachmentSyncable.ValueType.LONG,
                39_000L);

        assertEquals(39_000L, attachment.getDayTime());
        assertEquals(
                AttachmentSyncable.ValueType.LONG,
                attachment.getSyncFields().get(AetherTimeAttachment.DAY_TIME_SYNC_KEY).valueType());
    }
}
