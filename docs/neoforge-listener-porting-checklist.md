# Fabric Listener Porting Record

This historical record maps legacy NeoForge listener points from `The-Aether` 1.21.1 to the current Fabric-side implementation in `The-Aether-fabric` 26.2. It is retained as porting documentation only; it is not an active compatibility or old-save migration contract.

Old Curios, ForgeCaps, and `neoforge:attachments` save migration support is intentionally removed. Current Fabric attachment storage remains supported.

## Dimension Listener

| NeoForge listener point | Fabric equivalent / bridge | Status |
| --- | --- | --- |
| `onPlayerLogin` (`startInAether`) | `AetherFabricEvents` `ServerPlayerEvents.JOIN` | Done |
| `onInteractWithPortalFrame` | `UseBlockCallback` in `AetherFabric` + `AetherFabricEvents` | Done |
| `onWaterExistsInsidePortalFrame` | `LevelMixin#setBlock` hook calling `DimensionHooks.detectWaterInFrame` | Done |
| `onWorldTick` | `ServerTickEvents.END_WORLD_TICK` in `AetherFabricEvents` | Done |
| `onEntityTravelToDimension` | `EntityMixin#teleport` hook calling `dimensionTravel/removePlayerAerbunny` | Done |
| `onPlayerChangedDimension` | `ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD` | Done |
| `onPlayerTraveling` | `PlayerMixin#tick` calling `DimensionHooks.travelling` | Done |
| `onWorldLoad` | `ServerWorldEvents.LOAD` | Done |
| `onSleepFinish` | `ServerLevelMixin#wakeUpAllPlayers` calling `finishSleep` | Done |
| `onTriedToSleep` | `EntitySleepEvents.ALLOW_SLEEPING` | Done |
| `onAlterGround` | `AlterGroundDecoratorMixin` preserving Aether dirt | Done |

## Entity Listener

| NeoForge listener point | Fabric equivalent / bridge | Status |
| --- | --- | --- |
| `onEntityJoin` | `ServerEntityEvents.ENTITY_LOAD` | Done |
| `onMountEntity` / dismount prevention | `EntityMixin#startRiding` + `EntityMixin#stopRiding` | Done |
| `onRiderTick` (`launchMount`) | `AetherClient` end tick + shared hook logic | Done |
| `onInteractWithEntity` | `UseEntityCallback` in `AetherFabricEvents` | Done |
| `onProjectileHitEntity` (hook prevention) | `FishingHookMixin` | Done |
| `onShieldBlock` | `LivingEntityMixin#applyItemBlocking` | Done |
| `onLightningStrike` | `EntityMixin#thunderHit` | Done |
| `onPlayerDrops` owner tracking | `LivingEntityMixin` death-drop tracking bridge | Done |
| `onDropExperience` | `LivingEntityMixin#getExperienceReward` return hook | Done |
| `onEffectApply` (Inebriation prevention) | `ServerMobEffectEvents.ALLOW_ADD` | Done |
| `onEntitySplit` | `SlimeMixin` | Done |
| `onLoadPlayerFile` (legacy Curios migration) | Not ported; old Curios save migration was intentionally removed while current Fabric attachment storage remains supported | Removed |
| accessory spawn equip on mob spawn | `MobMixin#finalizeSpawn` calling `spawnWithAccessories` | Done |
| accessory drop handling on death | `LivingEntityMixin#dropCustomDeathLoot` bridge | Done |

## Item / Perk / Recipe / Attachment

| NeoForge listener point | Fabric equivalent / bridge | Status |
| --- | --- | --- |
| `ItemTooltipEvent` dungeon tooltips | `ItemTooltipCallback` in `AetherClient` | Done |
| `PerkListener#playerLoggedIn` | `ServerPlayerEvents.JOIN` (`refreshPerks`, `MoaSkins.registerMoaSkins`, `RegisterMoaSkinsPacket`) | Done |
| `RecipeListener#checkBanned` | `UseBlockCallback` recipe ban check | Done |
| neighbor notify recipe checks | `LevelMixin#neighborChanged` | Done |
| freeze prevention hook | `AetherEventDispatch#onBlockFreezeFluid` -> `RecipeHooks.preventBlockFreezing` | Done |
| placement convert / ban particle hooks | `AetherEventDispatch` bridge methods | Done |
| `AetherPlayer` login/logout/clone/update/change-dim | `ServerPlayerEvents`, `PlayerMixin#tick`, and world-change callbacks | Done |
| `AetherTime` login/respawn/change-dim sync | `ServerPlayerEvents` and world-change callbacks | Done |

## Ability Listeners

| NeoForge listener point | Fabric equivalent / bridge | Status |
| --- | --- | --- |
| Accessory block break damage hooks | `BlockMixin#playerDestroy` | Done |
| Accessory mining speed hooks | `PlayerMixin#getDestroySpeed` return modifier | Done |
| Accessory targeting visibility hooks | `LivingEntityMixin#getVisibilityPercent` return modifier | Done |
| Shield of Repulsion projectile deflection | `ProjectileMixin#onHit` | Done |
| magma damage prevention | `LivingEntityMixin#hurtServer` pre-check | Done |
| armor tick abilities (Valkyrie/Neptune/Phoenix) | `LivingEntityMixin#tick` | Done |
| Gravitite jump hook | `LivingEntityMixin#jumpFromGround` | Done |
| fall cancellation | `LivingEntityMixin#causeFallDamage` | Done |
| dart sticking + weapon/armor damage modifiers | `LivingEntityMixin#hurtServer` bridge (`stickDart`, damage adjust) | Done |
| phoenix arrow impact burn | `ProjectileMixin#onHit` | Done |
| tool action bridges (strip/flatten/till) | `AxeItemMixin` / `ShovelItemMixin` / `HoeItemMixin` | Done |
| Holystone extra drop hook | `BlockMixin#playerDestroy` | Done |
| tool debuff sync on login | `ServerPlayerEvents.JOIN` calling `setDebuffToolsState` | Done |
| invisibility cloak render suppression (`RenderPlayerEvent.Pre` / `RenderArmEvent`) | `AvatarRendererMixin` render-state and hand-render cancellation | Done |

## Remaining Runtime Validation Focus

| Module | Validation target | Status |
| --- | --- | --- |
| Client audio listener parity | `AudioListener#onPlaySound` is bridged through `SoundEngineMixin` -> `AudioHooks` | Done |
| Aether sky render hook parity | `SkyRendererMixin` + `LevelRendererMixin` + `LightTextureMixin` restore sky state, celestial fade, cloud toggle, and colder lightmap hooks | Done (pending in-world visual confirmation) |
| Client resources/rendering | no Aether missing model/texture/material warnings in 26.2 Fabric client resource-load log | Done |
| Aether sky rendering | sky sphere and dimension sky not black in-world | Pending in-game validation |
| Sky mob natural spawning | Fabric sky-spawn bridge owns Zephyr/Aerwhale generation when custom mob categories are unavailable; packaged biome data no longer duplicates them under vanilla categories | Bridge/data validated on isolated 26.2 server; pending player-present natural spawn observation |
| Aether cliff pathfinding | `FallPathNavigation` now uses 26.2 vanilla path following and only overrides vertical waypoint tolerance with Aether fall-distance behavior | Code/build validated; pending in-world mob movement observation |
