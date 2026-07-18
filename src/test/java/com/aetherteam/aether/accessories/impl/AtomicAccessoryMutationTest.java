package com.aetherteam.aether.accessories.impl;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AtomicAccessoryMutationTest {
    @Test
    void consumingStackLargerThanOneRemovesExactlyOneAndPersistsBeforeSync() {
        AtomicInteger liveCount = new AtomicInteger(3);
        AtomicInteger attachmentSnapshotCount = new AtomicInteger(3);
        AtomicInteger unequipCalls = new AtomicInteger();
        AtomicBoolean dirty = new AtomicBoolean();
        AtomicBoolean synced = new AtomicBoolean();
        int replacementCount = AtomicAccessoryMutation.consumeOneCount(liveCount.get());

        assertTrue(AtomicAccessoryMutation.commit(true, false,
            unequipCalls::incrementAndGet,
            () -> liveCount.set(replacementCount),
            () -> attachmentSnapshotCount.set(liveCount.get()),
            () -> {
                dirty.set(true);
                assertEquals(2, attachmentSnapshotCount.get());
                synced.set(true);
                dirty.set(false);
            }));

        assertEquals(2, liveCount.get());
        assertEquals(0, unequipCalls.get());
        assertTrue(synced.get());
        assertFalse(dirty.get());
    }

    @Test
    void consumingLastItemClearsSnapshotAndUnequipsExactlyOnce() {
        AtomicInteger liveCount = new AtomicInteger(1);
        AtomicInteger attachmentSnapshotCount = new AtomicInteger(1);
        AtomicInteger unequipCalls = new AtomicInteger();
        AtomicInteger syncCalls = new AtomicInteger();
        int replacementCount = AtomicAccessoryMutation.consumeOneCount(liveCount.get());

        AtomicAccessoryMutation.commit(true, true,
            unequipCalls::incrementAndGet,
            () -> liveCount.set(replacementCount),
            () -> attachmentSnapshotCount.set(liveCount.get()),
            syncCalls::incrementAndGet);

        assertEquals(0, liveCount.get());
        assertEquals(0, attachmentSnapshotCount.get());
        assertEquals(1, unequipCalls.get());
        assertEquals(1, syncCalls.get());
    }
}
