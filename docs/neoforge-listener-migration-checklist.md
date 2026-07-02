# Fabric Listener Migration Record

This historical record documents the original listener migration from NeoForge to Fabric. It is kept for maintenance context only; Fabric is the runtime target, and removed old-save migration entries are not active compatibility commitments.

Old Curios, ForgeCaps, and `neoforge:attachments` save migration support is intentionally removed. Current Fabric attachment storage remains supported.

Baseline: `The-Aether` branch `1.21.1-develop` event listeners.

Status legend:
- `DONE`: Fabric equivalent is wired and active.
- `PARTIAL`: Some behavior is wired, but coverage is incomplete.
- `MISSING`: No Fabric-side hook yet.
- `REMOVED`: Intentionally not carried forward in Fabric.

## Common Listeners
| NeoForge listener point | Fabric equivalent / bridge | Status | Notes |
|---|---|---|---|
| `DimensionListener#onPlayerLogin` | `ServerPlayerEvents.JOIN` -> `DimensionHooks.startInAether` | DONE | Registered in `AetherFabricEvents`. |
| `DimensionListener#onInteractWithPortalFrame` | `UseBlockCallback` -> `DimensionHooks.createPortal` | DONE | Registered in `AetherFabric`. |
| `DimensionListener#onWaterExistsInsidePortalFrame` | `LevelMixin#setBlock(..., flags, recursion)` tail -> `DimensionHooks.detectWaterInFrame` | DONE | Server-side water frame detection bridge restored for portal auto-creation. |
| `DimensionListener#onWorldTick` | `ServerTickEvents.END_WORLD_TICK` -> `DimensionHooks.tickTime/checkEternalDayConfig` | DONE | Registered in `AetherFabricEvents`. |
| `DimensionListener#onEntityTravelToDimension` | `EntityMixin#teleport(TeleportTransition)` -> `DimensionHooks.dimensionTravel/removePlayerAerbunny` | DONE | Pre-transfer hook restored for entity/player travel flow. |
| `DimensionListener#onPlayerChangedDimension` | `ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD` | DONE | `remountPlayerAerbunny` + attachment/time sync. |
| `DimensionListener#onPlayerTraveling` | `PlayerMixin#tick()` | DONE | Calls `DimensionHooks.travelling`. |
| `DimensionListener#onWorldLoad` | `ServerWorldEvents.LOAD` -> `DimensionHooks.initializeLevelData` | DONE | Registered in `AetherFabricEvents`. |
| `DimensionListener#onSleepFinish` | `ServerLevelMixin#wakeUpAllPlayers` tail -> `DimensionHooks.finishSleep` | DONE | Sleep-finish time rewrite parity restored for Aether dimensions. |
| `DimensionListener#onTriedToSleep` | `EntitySleepEvents.ALLOW_SLEEPING` -> `DimensionHooks.isEternalDay` | DONE | Eternal-day sleep blocking restored. |
| `DimensionListener#onAlterGround` | `AlterGroundDecoratorMixin#placeBlockAt` podzol replacement guard | DONE | Aether Dirt is preserved when tree decorators attempt podzol replacement. |
| `EntityListener#onEntityJoin` | `ServerEntityEvents.ENTITY_LOAD` -> `EntityHooks.addGoals` | DONE | Registered in `AetherFabricEvents`. |
| `EntityListener#onMountEntity` | `EntityMixin#startRiding/stopRiding` + `PlayerMixin#rideTick` dismount intent sync | DONE | Mount tracking and dismount prevention are now bridged in the entity ride lifecycle. |
| `EntityListener#onRiderTick` | `ClientTickEvents.END_CLIENT_TICK` -> `EntityHooks.launchMount` | DONE | Client-side rider launch behavior restored. |
| `EntityListener#onInteractWithEntity` | `UseEntityCallback` -> entity interaction hook chain | DONE | Milking/bucket/armor stand routing wired. |
| `EntityListener#onProjectileHitEntity` | `FishingHookMixin#onHitEntity` head cancel -> `EntityHooks.preventEntityHooked` | DONE | Fishing-hook impact cancellation parity restored for unhookable entities. |
| `EntityListener#onShieldBlock` | `LivingEntityMixin#applyItemBlocking` head override -> `EntityHooks.preventSliderShieldBlock` | DONE | Slider shield-block cancel behavior restored in damage-blocking pipeline. |
| `EntityListener#onLightningStrike` | `EntityMixin#thunderHit` head cancel -> lightning key/thunder-crystal guards | DONE | Lightning strike cancellation parity restored for protected Aether items. |
| `EntityListener#onPlayerDrops` | `LivingEntityMixin#dropAllDeathLoot + drop(...)` death-window tracking bridge | DONE | Player death drops now receive owner tracking on spawned `ItemEntity` drops. |
| `EntityListener#onDropExperience` | `LivingEntityMixin#getExperienceReward` return rewrite -> `EntityHooks.modifyExperience` | DONE | Experience drop modifier parity restored for accessory-equipped mobs. |
| `EntityListener#onEffectApply` | `ServerMobEffectEvents.ALLOW_ADD` -> `EntityHooks.preventInebriation` | DONE | Effect-application guard restored. |
| `EntityListener#onEntitySplit` | `SlimeMixin#remove` split-spawn `addFreshEntity` guard -> `EntityHooks.preventSplit` | DONE | Swet split cancellation restored without altering base removal flow. |
| `EntityListener#onLoadPlayerFile` | Not ported | REMOVED | Legacy `ForgeCaps`/`neoforge:attachments` Curios save migration was intentionally removed; current Fabric attachment storage remains supported. |
| `ItemListener#onTooltipAdd` | Fabric `ItemTooltipCallback` + Nitrogen tooltip override bridge in `AetherClient` | DONE | Dungeon tooltips and Aether tooltip override predicates are registered from the client bootstrap. |
| `PerkListener#playerLoggedIn` | `ServerPlayerEvents.JOIN` -> `PerkHooks.refreshPerks` | DONE | Registered in `AetherFabricEvents`. |
| `RecipeListener` event chain | `UseBlockCallback` + `LevelMixin#neighborChanged` + direct `RecipeHooks` ban/convert/freeze guards | DONE | Placement-ban checks, neighbor-based ban/convert checks, freeze guard, and ban/convert particle hooks restored. |

## Attachment Listeners
| NeoForge listener point | Fabric equivalent / bridge | Status | Notes |
|---|---|---|---|
| `AetherPlayerListener#onPlayerLogin` | `ServerPlayerEvents.JOIN` -> `AttachmentHooks.AetherPlayerHooks.login` | DONE | Registered in `AetherFabricEvents`. |
| `AetherPlayerListener#onPlayerLogout` | `ServerPlayerEvents.LEAVE` -> `AttachmentHooks.AetherPlayerHooks.logout` | DONE | Registered in `AetherFabricEvents`. |
| `AetherPlayerListener#onPlayerJoinLevel` | `ServerEntityEvents.ENTITY_LOAD` -> `AttachmentHooks.AetherPlayerHooks.joinLevel` | DONE | Registered in `AetherFabricEvents`. |
| `AetherPlayerListener#onPlayerUpdate` | `PlayerMixin#tick()` -> `AttachmentHooks.AetherPlayerHooks.update` | DONE | Existing mixin bridge. |
| `AetherPlayerListener#onPlayerClone` | `ServerPlayerEvents.COPY_FROM` -> `AttachmentHooks.AetherPlayerHooks.clone` | DONE | Registered in `AetherFabricEvents`. |
| `AetherPlayerListener#onPlayerChangeDimension` | `ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD` -> `AttachmentHooks.AetherPlayerHooks.changeDimension` | DONE | Registered in `AetherFabricEvents`. |
| `AetherTimeListener#onLogin` | `ServerPlayerEvents.JOIN` -> `AttachmentHooks.AetherTimeHooks.login` | DONE | Registered in `AetherFabricEvents`. |
| `AetherTimeListener#onChangeDimension` | `ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD` -> `AttachmentHooks.AetherTimeHooks.changeDimension` | DONE | Registered in `AetherFabricEvents`. |
| `AetherTimeListener#onPlayerRespawn` | `ServerPlayerEvents.AFTER_RESPAWN` -> `AttachmentHooks.AetherTimeHooks.respawn` | DONE | Registered in `AetherFabricEvents`. |

## Ability / Client Listener Groups
| NeoForge listener group | Fabric equivalent / bridge | Status | Notes |
|---|---|---|---|
| `abilities/*` listeners | scattered item/mixin direct calls | DONE | Accessory/armor/tool/weapon hooks are bridged by `BlockMixin`, `PlayerMixin`, `LivingEntityMixin`, `ProjectileMixin`, and item-tool mixins. |
| `client/event/listeners/*` | `AetherClient.registerClientCallbacks` + renderer hooks + `FogRendererMixin` + `SoundEngineMixin` + `AvatarRendererMixin` | PARTIAL | Core callbacks, tooltip interception, play-sound interception, and invisibility-cloak player/arm render suppression are wired; remaining status is in-world validation for sky rendering and AI behavior. |
