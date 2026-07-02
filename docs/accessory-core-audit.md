# Aether Accessory Core Audit (Stage 1)

This checklist tracks the self-written Aether accessory core built on Fabric attachments before any vanilla/twilight held/equipped compatibility work.

## Stage 1: Core Attachment Matrix

| Module | Current State | Fabric-side Fix | Status |
|---|---|---|---|
| Slot storage model | `AccessoriesAPI` facade backed by per-entity accessory storage and Fabric attachment persistence | Persist inventory into Fabric attachment (`AttachmentType`) and load/write through containers | DONE |
| Serialization / deserialization | No persistent accessory serialization for player/mob accessory slots | Add codec-backed Fabric attachment for slot stacks, cosmetic stacks, render flags | DONE |
| Lifecycle robustness (join/rejoin/clone/dimension/respawn/menu reopen) | No dedicated accessory lifecycle sync/rehydration path | Add accessory sync hooks on join/respawn/dimension and deterministic runtime rehydrate | DONE |
| Equip/unequip/replace transition closure | No unified diff; many paths set slot items directly without lifecycle callbacks | Add centralized transition diff engine with guaranteed `onUnequip` rollback on replacement/removal | DONE |
| Stack mutation closure (count/NBT/durability changes) | Container callbacks miss in-place stack mutation; stale effects possible | Add per-tick stack fingerprint diff to detect in-place mutations and re-evaluate state | DONE |
| Unified equipped query API | Queries scattered via `EquipmentUtil` and raw container access | Add unified query API over accessory core snapshots | DONE |
| Unified accessory tick entry | No central runtime tick loop for all equipped accessories | Add central per-entity accessory runtime tick dispatcher | DONE |
| Dynamic attributes add/remove loop | `AccessoryAttributeBuilder` stores untyped modifiers and is not applied globally | Rework builder to typed entries and apply/remove deterministic transient modifiers per slot | DONE |
| Server-authoritative sync state | No dedicated accessory payload sync path | Add client sync packet for accessory slot snapshots and server dirty-flush path | DONE |
| Residual state cleanup | No guaranteed cleanup on entity unload/logout | Add runtime cleanup hooks for entity unload/logout | DONE |

## Stage 2: Compatibility Matrix (to start only after Stage 1 is stable)

| Trigger Path | Bridge Entry | Status |
|---|---|---|
| Vanilla/Fabric `LivingEntity#isHolding(Predicate<ItemStack>)` checks | `AccessoryEffectBridge.isHoldingEquivalent` via `LivingEntityMixin` return-augment | DONE |
| Vanilla/Fabric inventory tick for held/equipped passive items | Accessory runtime dispatches equipped stacks through `Item#inventoryTick` | DONE |
| Twilight charm/equipment-slot consumption path (`TFItemStackUtils.consumeEquipmentSlot`) | Optional twilight mixin `TFItemStackUtilsMixin` + `AccessoryEffectBridge.consumeAccessoryItem` | DONE |
| Twilight mystic-crown head-slot checks (scepter/wand family) | Optional twilight mixins (`TwilightWandItemMixin`, `ZombieWandItemMixin`, `LifedrainScepterItemMixin`) + `AccessoryEffectBridge.findFirstByEquipmentSlot` | DONE |
| Twilight temporary shield timer crown bonus (`FortificationShieldAttachment.checkLichCrownBonus`) | Optional twilight mixin `FortificationShieldAttachmentMixin` + `AccessoryEffectBridge.findFirstByEquipmentSlot` | DONE |
| Twilight armor-coverage checks (`EntityEvents.getGearCoverage`) | Optional twilight mixin `EntityEventsMixin` + `AccessoryEffectBridge.findFirstByEquipmentSlot` | DONE |
| Twilight armor-shrouding checks (`ArmorUtil.getShroudedArmorPercentage`) | Optional twilight mixin `ArmorUtilMixin` + `AccessoryEffectBridge.findFirstByEquipmentSlot` | DONE |
