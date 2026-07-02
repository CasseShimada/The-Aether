package com.aetherteam.aether.accessories.api;

interface AccessoriesContainerOwner extends AccessoriesStorage {
    void onContainerChanged(String slotName);
}
