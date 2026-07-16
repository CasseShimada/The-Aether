# Aether Accessory Core Audit (Stage 1)

This checklist tracks the self-written Aether accessory core built on Fabric attachments. Current slot resolution lives under the Aether accessory slot boundary, and current effect bridging lives under the Aether accessory effect boundary.

## Legacy Migration Decision

The earlier decision to remove Curios, Accessories, `ForgeCaps`, and `neoforge:attachments` migration was superseded on 2026-07-15 because released Forge/NeoForge player saves are a compatibility requirement. `LegacyPlayerDataMigration` now reads those names only as old NBT keys and introduces no Forge, NeoForge, Accessories, or Curios code/runtime dependency.

Before conversion, the complete legacy root compounds are copied into the persistent Fabric attachment `aether:legacy_data_archive`. This prevents unknown third-party capability/attachment data, unsupported Curios/Accessories slots, overflow items, and undecodable item payloads from being erased by the next vanilla player save. Migration version `1` is idempotent. Current `fabric:attachments` player state and accessory inventory each take precedence only over their corresponding old domain, so one current attachment does not suppress migration of the other.

Released Aether Curios slot identifiers and their generic override equivalents map to the seven current `aether:*_slot` identifiers. The historical `AccessoriesEncoded` flag is honored to avoid importing an already-converted Curios payload twice. Released Accessories `1.1.0-beta.48+1.21.1` data is read from `accessories:inventory_holder`; both official loader builds use `accessories_containers` with per-slot `items`, `cosmetics`, `render_options`, and size fields. Arbitrary third-party slot names are retained by the current attachment.

Item stacks pass through vanilla player DFU using the source `DataVersion` before the current `ItemStack.CODEC` decodes them, preserving count and upgraded components. Cosmetic stacks and render flags are imported when present; occupied, unsupported, overflow, invalid, and missing-mod entries remain in the archive and produce a warning instead of being silently discarded. The synthetic released-format fixture is covered by `LegacyPlayerDataMigrationTest`; real player-file upgrade verification remains a release blocker in `FABRIC_NATIVE_MIGRATION_AUDIT.md`.

The nonfunctional alternative Accessories menu switch remains removed. Current runtime storage is still `AccessoryInventoryAttachment`; the archive and migration service are isolated persistence boundaries, not capability-shaped facades. Existing `commands.aether.capability.*` translation keys remain stable resource identifiers.

Mob accessory drop chances still seed only the current `aether:gloves_slot` and `aether:pendant_slot` identifiers. The obsolete unnamespaced `hand` and `necklace` defaults are not recreated at runtime; their names are recognized only by the legacy data mapper.

## Current Attachment Sync Boundary

Current attachment field synchronization uses explicit server, all-client, player, and dimension send methods. The former target enum and untyped context arguments have been removed; packet payload types, field keys, codecs, and Fabric attachment persistence remain unchanged. Accessory inventory snapshots continue to use their dedicated tracking-and-self dispatcher.

## Third-party Implementation Hooks

Implementation-specific Twilight Forest pseudo-mixins, target class names, item IDs, component IDs, and NBT handling have been intentionally removed. Current slot matching remains data-driven through Aether tags, generic `accessories:*` tags, vanilla item components, and common `c:*` tags.

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
| Server-authoritative sync state | No dedicated accessory payload sync path | Add client sync packet for accessory slot snapshots, server dirty-flush path, and Fabric tracking dispatch at the networking boundary | DONE |
| Residual state cleanup | No guaranteed cleanup on entity unload/logout | Add runtime cleanup hooks for entity unload/logout | DONE |

## Stage 2: Current Compatibility Matrix

| Trigger Path | Bridge Entry | Status |
|---|---|---|
| Vanilla/Fabric `LivingEntity#isHolding(Predicate<ItemStack>)` checks | `AccessoryEffectBridge.isHoldingEquivalent` via `LivingEntityMixin` return-augment | DONE |
| Vanilla/Fabric inventory tick for held/equipped passive items | Accessory runtime dispatches equipped stacks through `Item#inventoryTick` | DONE |
