# NeoForge Listener Migration Checklist

Baseline: `The-Aether` branch `1.21.1-develop` event listeners.

Status legend:
- `DONE`: Fabric equivalent is wired and active.
- `PARTIAL`: Some behavior is wired, but coverage is incomplete.
- `MISSING`: No Fabric-side hook yet.

## Common Listeners
| NeoForge listener point | Fabric equivalent / bridge | Status | Notes |
|---|---|---|---|
| `DimensionListener#onPlayerLogin` | `ServerPlayerEvents.JOIN` -> `DimensionHooks.startInAether` | DONE | Registered in `AetherFabricEvents`. |
| `DimensionListener#onInteractWithPortalFrame` | `UseBlockCallback` -> `DimensionHooks.createPortal` | DONE | Registered in `AetherFabric`. |
| `DimensionListener#onWaterExistsInsidePortalFrame` | `LevelMixin#setBlock(..., flags, recursion)` tail -> `DimensionHooks.detectWaterInFrame` | DONE | Server-side water frame detection bridge restored for portal auto-creation. |
| `DimensionListener#onWorldTick` | `ServerTickEvents.END_WORLD_TICK` -> `DimensionHooks.tickTime/checkEternalDayConfig` | DONE | Registered in `AetherFabricEvents`. |
| `DimensionListener#onEntityTravelToDimension` | `EntityMixin#teleport(TeleportTransition)` -> `DimensionHooks.dimensionTravel/removePlayerAerbunny` | DONE | Pre-transfer hook restored for entity/player travel flow. |
| `DimensionListener#onPlayerChangedDimension` | `ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD` | DONE | `remountPlayerAerbunny` + capability/time sync. |
| `DimensionListener#onPlayerTraveling` | `PlayerMixin#tick()` | DONE | Calls `DimensionHooks.travelling`. |
| `DimensionListener#onWorldLoad` | `ServerWorldEvents.LOAD` -> `DimensionHooks.initializeLevelData` | DONE | Registered in `AetherFabricEvents`. |
| `DimensionListener#onSleepFinish` | `ServerLevelMixin#wakeUpAllPlayers` tail -> `DimensionHooks.finishSleep` | DONE | Sleep-finish time rewrite parity restored for Aether dimensions. |
| `DimensionListener#onTriedToSleep` | `EntitySleepEvents.ALLOW_SLEEPING` -> `DimensionHooks.isEternalDay` | DONE | Eternal-day sleep blocking restored. |
| `DimensionListener#onAlterGround` | none | MISSING | Needs Fabric equivalent terrain-mod hook. |
| `EntityListener#onEntityJoin` | `ServerEntityEvents.ENTITY_LOAD` -> `EntityHooks.addGoals` | DONE | Registered in `AetherFabricEvents`. |
| `EntityListener#onMountEntity` | `PlayerMixin#rideTick()` custom flow | PARTIAL | Dismount prevention path exists; mount event parity not complete. |
| `EntityListener#onRiderTick` | `ClientTickEvents.END_CLIENT_TICK` -> `EntityHooks.launchMount` | DONE | Client-side rider launch behavior restored. |
| `EntityListener#onInteractWithEntity` | `UseEntityCallback` -> entity interaction hook chain | DONE | Milking/bucket/armor stand routing wired. |
| `EntityListener#onProjectileHitEntity` | `FishingHookMixin#onHitEntity` head cancel -> `EntityHooks.preventEntityHooked` | DONE | Fishing-hook impact cancellation parity restored for unhookable entities. |
| `EntityListener#onShieldBlock` | `LivingEntityMixin#applyItemBlocking` head override -> `EntityHooks.preventSliderShieldBlock` | DONE | Slider shield-block cancel behavior restored in damage-blocking pipeline. |
| `EntityListener#onLightningStrike` | `EntityMixin#thunderHit` head cancel -> lightning key/thunder-crystal guards | DONE | Lightning strike cancellation parity restored for protected Aether items. |
| `EntityListener#onPlayerDrops` | `LivingEntityMixin#dropAllDeathLoot + drop(...)` death-window tracking bridge | DONE | Player death drops now receive owner tracking on spawned `ItemEntity` drops. |
| `EntityListener#onDropExperience` | `LivingEntityMixin#getExperienceReward` return rewrite -> `EntityHooks.modifyExperience` | DONE | Experience drop modifier parity restored for accessory-equipped mobs. |
| `EntityListener#onEffectApply` | `ServerMobEffectEvents.ALLOW_ADD` -> `EntityHooks.preventInebriation` | DONE | Effect-application guard restored. |
| `EntityListener#onEntitySplit` | none | MISSING | Needs mob split/collision equivalent bridge. |
| `EntityListener#onLoadPlayerFile` | none | MISSING | Needs player data migration hook in Fabric flow. |
| `ItemListener#onTooltipAdd` | Nitrogen tooltip listener bridge in `AetherClient` | PARTIAL | Tooltip predicate chain exists; full parity needs audit. |
| `PerkListener#playerLoggedIn` | `ServerPlayerEvents.JOIN` -> `PerkHooks.refreshPerks` | DONE | Registered in `AetherFabricEvents`. |
| `RecipeListener` event chain | none | MISSING | Recipe placement/ban/freeze callbacks still need Fabric event bridges. |

## Capability Listeners
| NeoForge listener point | Fabric equivalent / bridge | Status | Notes |
|---|---|---|---|
| `AetherPlayerListener#onPlayerLogin` | `ServerPlayerEvents.JOIN` -> `CapabilityHooks.AetherPlayerHooks.login` | DONE | Registered in `AetherFabricEvents`. |
| `AetherPlayerListener#onPlayerLogout` | `ServerPlayerEvents.LEAVE` -> `CapabilityHooks.AetherPlayerHooks.logout` | DONE | Registered in `AetherFabricEvents`. |
| `AetherPlayerListener#onPlayerJoinLevel` | `ServerEntityEvents.ENTITY_LOAD` -> `CapabilityHooks.AetherPlayerHooks.joinLevel` | DONE | Registered in `AetherFabricEvents`. |
| `AetherPlayerListener#onPlayerUpdate` | `PlayerMixin#tick()` -> `CapabilityHooks.AetherPlayerHooks.update` | DONE | Existing mixin bridge. |
| `AetherPlayerListener#onPlayerClone` | `ServerPlayerEvents.COPY_FROM` -> `CapabilityHooks.AetherPlayerHooks.clone` | DONE | Registered in `AetherFabricEvents`. |
| `AetherPlayerListener#onPlayerChangeDimension` | `ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD` -> `CapabilityHooks.AetherPlayerHooks.changeDimension` | DONE | Registered in `AetherFabricEvents`. |
| `AetherTimeListener#onLogin` | `ServerPlayerEvents.JOIN` -> `CapabilityHooks.AetherTimeHooks.login` | DONE | Registered in `AetherFabricEvents`. |
| `AetherTimeListener#onChangeDimension` | `ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD` -> `CapabilityHooks.AetherTimeHooks.changeDimension` | DONE | Registered in `AetherFabricEvents`. |
| `AetherTimeListener#onPlayerRespawn` | `ServerPlayerEvents.AFTER_RESPAWN` -> `CapabilityHooks.AetherTimeHooks.respawn` | DONE | Registered in `AetherFabricEvents`. |

## Ability / Client Listener Groups
| NeoForge listener group | Fabric equivalent / bridge | Status | Notes |
|---|---|---|---|
| `abilities/*` listeners | scattered item/mixin direct calls | PARTIAL | Several hooks are direct-call; event parity not fully audited. |
| `client/event/listeners/*` | `AetherClient.registerClientCallbacks` + renderer hooks | PARTIAL | Core callbacks wired, full parity matrix pending. |
