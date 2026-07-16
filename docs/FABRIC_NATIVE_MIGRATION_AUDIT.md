# Fabric Native Migration Audit

## 0. 接力摘要
- 最后更新时间：2026-07-16（Asia/Shanghai）
- 当前分支与 HEAD：`codex/fabric-native-refactor` / `8ceaaf2cd8e89dc1f8a14d1d2d5f552d8de7df8a`
- 当前阶段：MIGRATING
- 本轮正在处理：用户提供的真实 Fabric `26.1.2` 世界已在只读副本上完成 `26.2` 升级、维度往返、容器、旧 Moa、死亡复制和幂等重启；既有 Slider/Valkyrie Queen 现又完成玩家归因击杀、进度触发、房门/地牢解锁及重启持久化。新增源级稳定 ID 回归守卫（28 个 native 注册声明类、585 个标识符）和资源完整性测试（2,768 个 JSON、核心 model/texture/atlas/sound/particle 引用）。
- 下一步唯一优先任务：只继续 The Aether 自身在该 fixture 未出现的方块实体/物品行为、事件与动态世界生成矩阵；原档没有持久 Sun Spirit，当前实体的渲染/对话/激活/AI 已通过但不声称完整击杀。Forge/NeoForge fixture 与其他 Mod 明确不在用户验收范围内。
- 当前阻塞：用户验收范围内的真实 Fabric 旧存档不再阻塞；总体仍为 `MIGRATING`，因为严格全项目定义下仍有事件语义、其余方块实体/物品行为、动态数据/世界生成及部分 Mixin 场景未完成全量审计。
- 工作区未提交改动及归属：本轮开始前已有的天空、雾、HUD、地牢覆盖层客户端改动和近期 PCL2 drying-rack 工作必须保留；`docs/FABRIC_NATIVE_MIGRATION_PROMPT.md` 为用户提供；Boss/player migration、配置/pack/network、Aether time、HUD/atlas/tracker 修复、测试、审计与文档为迁移工作。未提交文件详见 `git status --short`，禁止清理未知改动或原始 PCL2 世界。
- 最近一次构建/客户端/服务端/旧存档验证结果：最终 required `clean build` 30s 与独立 `test` 7s 成功，32 tests/12 suites、0 failure/error/skip；`git diff --check` 退出 0；JAR 38,081,743 bytes，SHA-256 `E48BCAEF6B6FCBCB7C20D0C3991094567BFDBC2556C64337828E3C3D15C07104`。真实旧档 DataVersion `4790 -> 4903`，核心服首次/重启均 `Done` 并优雅停止，同 UUID 玩家进入既有 Aether 地形，完成维度往返、旧容器/Queen/Slider/Moa、当前 Sun Spirit 激活/AI 和死亡重生交互；旧 Slider/Queen 完整死亡、进度、解锁和重启持久化通过。配置同步/断线清理正常，原件 70-file manifest 前后完全一致。证据：`run/migration-evidence-world-upgrade-20260716/`、`run/migration-client-mixin-audit-20260716/`、`run/migration-common-mixin-audit-20260716/`、`run/migration-evidence-aether-content-20260716/`、`run/migration-evidence-aether-gameplay-20260716/`、`run/migration-evidence-registry-resource-boss-20260716/` 及既有 rendering/tracking/config 目录。

## 1. 不可变目标与完成定义

- 目标环境固定为 Minecraft `26.2`、Fabric Loader `0.19.3`、Fabric API `0.153.0+26.2`、Java `25`；Gradle/Loom 使用仓库锁定值。
- 生产代码只允许 Minecraft、Fabric Loader、必要 Fabric API，以及经过隔离的可选 Fabric 集成；不允许 Forge/NeoForge/跨加载器运行依赖或模拟其 API 形状的 facade。
- 必须保持稳定资源 ID、玩法、客户端表现、多人同步和配置语义。用户于 2026-07-16 明确将旧档验收范围收敛为其提供的 Fabric `26.1.2` 存档；本审计不再把未提供的 Forge/NeoForge fixture 当作本轮阻塞，也不据此声称 Forge/NeoForge 世界已经兼容。
- 识别 `ForgeCaps`、`neoforge:attachments`、`curios:inventory` 等字符串仅限隔离的数据迁移器；不得删除未知第三方数据。
- 只有所有矩阵无 `NOT_AUDITED`/`TODO`/`IN_PROGRESS`/`REGRESSION`、无中高风险阻塞，且完整构建、客户端、dedicated server、旧存档升级与幂等重启均有证据时，状态才能为 `COMPLETE`。

## 2. 基线、参考分支与证据

| 证据 | 位置/版本 | 结论 | 状态 |
|---|---|---|---|
| 当前构建元数据 | `gradle.properties`、`build.gradle`、`fabric.mod.json` | 目标版本与提示词一致；`runtimeClasspath` 只有 Minecraft/Fabric 与游戏运行库，JEI/Jade 为 `compileOnly` | DONE |
| 当前祖先与迁移历史 | `codex/fabric-native-refactor`，HEAD `8ceaaf2cd`；`git log` | 已长期迁移，不能重新生成；近期完成 keyed item/block/entity 注册与客户端 hook 重命名 | DONE |
| 官方 NeoForge 参考 | tag `1.21.1-1.5.10-neoforge`，merge-base `04875348b6`；Accessories `1.1.0-beta.48+1.21.1` sources | 证明 attachment ID `accessories:inventory_holder` 及 `accessories_containers`/`items`/`cosmetics`/`render_options` 格式 | DONE |
| 官方 Fabric 参考 | tag `1.21.1-1.5.10-fabric`、`upstream/1.21.1-develop-fabric`，merge-base `bad33a9cac` | 用于确认历史 Fabric 移植意图，不作为缺失功能免责依据 | IN_PROGRESS |
| 最近目标版本基线 | tag `26.1.2-fabric`，merge-base `9156c21150`；分支 `origin/1.21.11-fabric`，merge-base `7b1f763935` | 用于 26.x API 迁移差异 | IN_PROGRESS |
| 旧兼容删除证据 | `a9b141d5b`、`f85a7e904`、`7f91ea339`；`docs/accessory-core-audit.md` | 明确删除 Curios/旧 attachment 与 Boss NBT 回退，和当前目标冲突 | DONE |
| 用户提供真实旧档 | PCL2 导出的 `world`；Minecraft/Fabric `26.1.2`、DataVersion `4790`；Aether `1.0.0-beta` JAR SHA-256 `0A6EF80D...D6162C` | 70 files/29,711,647 bytes；包含 9 个 Aether chunk regions、三类地牢、Boss/生物、玩家和 Fabric attachments；原件前后 manifest 一致 | DONE |
| released Nitrogen 同步语义 | `nitrogen_internals-1.21.1-1.1.24-fabric.jar` 的 `INBTSynchable` bytecode | `javap -c -p` 显示各方向发送分支汇合后在 bytecode 289-317 调用本地 setter `Consumer.accept`；当前 `AttachmentSyncable` 恢复相同的 send-then-apply 顺序 | DONE |
| 仓库约束 | 未发现 `AGENTS.md`；用户提供 `docs/FABRIC_NATIVE_MIGRATION_PROMPT.md` | 以主提示词和现有代码约束为准 | DONE |

## 3. Forge/NeoForge/跨平台残留清单

| 责任域 | 旧实现证据 | 当前文件或实现 | 差异与风险 | 目标实现 | 验证方法 | 状态 |
|---|---|---|---|---|---|---|
| Gradle/运行依赖 | 官方 NeoForge tag 的 NeoGradle、Accessories/Cumulus | `build.gradle` | 解析图确认无 Forge/NeoForge/Accessories/Cumulus/Architectury | Fabric Loom + Fabric API；可选集成不得成为核心运行依赖 | `dependencies`、产物内容与纯净启动 | DONE |
| 直接框架引用 | 历史 Forge/NeoForge imports/API | 全生产源码/资源 | 活动源码无框架 import；旧 loader/Curios/Accessories 名称只存在于隔离迁移器的数据键和说明 | 仅迁移器可出现旧键名字符串 | 结构化残留扫描 | DONE |
| Forge 形状配置 facade | Forge `ModConfigSpec` | `AetherConfig`、`config/AetherConfigFile.java`、`ConfigSerializationUtil.java` | facade 已删除；64 项 released schema、非破坏读取、原子保存、per-world 生命周期、live client sync 与两个内置数据包消费者均有端到端证据 | Aether 原生配置 schema + 旧路径/键兼容读取 | 配置 fixture、启动/保存/范围测试、client sync | DONE |
| event/hooks 容器 | NeoForge listeners/hooks | `event/hooks`、残余 `*Hooks` 类 | 部分是领域规则，部分仅保留旧架构命名；需逐类判断 | Fabric callback 或清晰领域服务；只在无 API 时保留 Mixin | 调用图、行为测试、残留扫描 | TODO |
| attachment/capability 命名 | Capability/NeoForge attachment | `attachment/*Attachment.java` | 实现为 Fabric Attachment API；真实 Fabric 玩家已验证维度/重连，live 死亡重生保持 life shards、last ridden Moa、seen Sun Spirit 并重建 30 max health；未提供的跨 loader fixture 不在用户范围 | Fabric Attachment/vanilla SavedData，兼容读取用户 fixture | NBT fixture、死亡/维度/重连 | IN_PROGRESS |
| 注册封装 | DeferredRegister/RegistryObject | 各 `Aether*` 注册类；`AetherRegistryStabilityTest` | 28 个 native 注册声明类的 585 个标识符已按 released `26.1.2-fabric` 基线冻结 count + sorted SHA-256；显式 vanilla/Fabric 注册启动通过。动态 datapack key 仍在世界生成行单列 | 原版 Registry/Fabric 注册，显式稳定 key | 源级稳定 ID 快照、client/server 启动日志 | DONE |
| 网络 facade | NeoForge payload/context；released Nitrogen sync bytecode | `network/**`、`AetherNetworking*`、`AetherPacketSender` | 31 个 clientbound、17 个 serverbound 注册已逐项列出；handlers 显式切回 logical main thread；无 cross-loader context/facade。`LoreExistsPacket` 的客户端语言知识信任另列风险 | Fabric typed payload，服务端权威 | 注册方向扫描、focused handler tests、remote quick-play join | DONE |
| 数据生成/条件资源 | NeoForge datagen/load condition | `src/generated/resources`、内置 packs | 生成量大；尚未扫描条件语义和陈旧 NeoForge 输出 | vanilla/Fabric 数据与 load condition | datagen diff、资源加载日志 | TODO |
| 客户端 hook 命名 | 历史通用 hook 边界 | `AetherSkyRendering`、`AetherFogRendering`、`AetherOverlays`、`AetherStatusHudRendering`、`DungeonOverlay*` | 该批历史 `event/hooks` 已改为明确 renderer/level 领域边界；天空/雾/HUD/overlay 已 live 验证，地牢状态按 `ClientLevel` 隔离并在断线清理 | Fabric client API + 必要 Mixin，领域命名 | clean build、日/夜/天气/HUD/overlay 截图、重载/换维度/断线 | DONE |

## 4. 功能等价矩阵

| 责任域 | 旧实现证据 | 当前文件或实现 | 差异与风险 | 目标实现 | 验证方法 | 状态 |
|---|---|---|---|---|---|---|
| 初始化与 dedicated server 隔离 | 官方入口/事件监听 | `AetherFabric.java`、`AetherFabricClient.java`、`fabric.mod.json` | 核心 server 两次成功，未发现客户端类加载错误 | 客户端类不在 server 加载 | dedicated server smoke | DONE |
| 事件语义 | NeoForge listeners | Fabric callbacks + common Mixins | tick phase、取消传播、伤害/掉落/登录等未逐项比对 | 等价 Fabric callback/最小 Mixin | 逐事件行为/测试矩阵 | NOT_AUDITED |
| 实体与 AI | NeoForge tag + 当前资源 | `entity/**` | 旧 Slider 完成玩家归因击杀、`Like a Bossaru!`、铜房门清除与重启；旧 Queen 完成玩家归因击杀、`Dethroned`、完整 35-chunk 银地牢六类锁/门块清除与重启。旧 Moa 渲染/上鞍/骑乘/移动通过；当前 Sun Spirit 对话激活、boss bar、移动/水晶攻击通过。原 fixture 无持久 Sun Spirit，其他 AI 未全量回归 | 保持玩家可观察行为/NBT | GameTest + 游戏内场景 | IN_PROGRESS |
| 方块/方块实体/容器 | NeoForge tag + 当前资源 | `block/**`、`blockentity/**`、`inventory/**` | keyed block 注册已迁移；旧银地牢普通箱与自定义 treasure chest 已真实开菜单、生成战利品并跨重启持久化；其余容器/progress/比较器/update packet 未全量回归 | 保持状态、库存、进度、比较器语义 | NBT fixture + GameTest | IN_PROGRESS |
| 物品/装备/工具 | NeoForge tag + 当前资源 | `item/**` | keyed item 注册已迁移；组件/属性/耐久升级未回归 | 稳定 ID 与 Data Component 行为 | 注册快照 + 行为测试 | TODO |
| 饰品系统 | NeoForge Accessories/历史 Curios | `accessories/**`、`AccessoryInventoryAttachment`、`LegacyPlayerDataMigration` | 代码级迁移覆盖 released Accessories/Curios 与未知 slot；真实玩家文件、死亡/重连/同步尚未端到端验证 | 原生附件存储 + 隔离幂等旧格式迁移 | NBT 单测、死亡/重连/同步 | IN_PROGRESS |
| 命令与权限 | NeoForge command listeners | `command/**`、Fabric command callback | 注册存在，权限与反馈语义未审计 | 服务端权限一致 | 命令 smoke | NOT_AUDITED |
| 配置行为 | released Forge/NeoForge config 文件/键 | `AetherConfig` + `AetherConfigFile` + `ServerConfigSyncPacket` | 64 项 schema、global/per-world load/unload、join sync、disconnect cleanup 已实现；两项 world-restart 值及 27-entry live client packet 均通过新世界/重启/断线验证 | 原生 schema，兼容旧文件并保持作用域/同步/重启语义 | 24-test suite + server lifecycle + client sync + pack activation | DONE |
| 多人/单人 | NeoForge 网络与生命周期 | Fabric callbacks/payloads；`EntityTrackingEvents.START_TRACKING`；`AetherTravelController` | tracking start/重连已双客户端验证；真实玩家死亡重生保持 Aether attachment 并重建 life-shard max health。旅行 UI/500-tick guard 已按 UUID 隔离，但并发旅行尚未 live | 登录/重连/追踪/死亡一致且玩家状态互不串扰 | 双客户端 + 集成服测试 | IN_PROGRESS |
| 错误与损坏恢复 | 旧序列化/日志 | codecs、`ProblemReporter.DISCARDING` 多处 | 部分解析可能静默丢弃，迁移失败无统一保留/告警策略 | 保留原始数据并输出可操作日志 | 损坏 fixture、日志断言 | TODO |

## 5. 注册表与稳定标识符矩阵

| 责任域 | 旧实现证据 | 当前文件或实现 | 差异与风险 | 目标实现 | 验证方法 | 状态 |
|---|---|---|---|---|---|---|
| 方块/物品/实体 | released `26.1.2-fabric` + 当前源码 | `AetherBlocks`、`AetherItems`、`AetherEntityTypes` | 131 block、168 item、38 entity type ID 的 count 与 sorted SHA-256 精确匹配 released 基线；真实旧世界无 missing `aether:*` | vanilla Registry + 相同 `aether:*` ID | `AetherRegistryStabilityTest` + 旧档启动 | DONE |
| 方块实体/菜单 | released `26.1.2-fabric` | `AetherBlockEntityTypes`、`AetherMenuTypes` | 11 block entity + 5 menu ID 精确匹配；old silver treasure chest/loot container 菜单 live 通过。所有 BE 行为覆盖仍在功能矩阵单列 | 显式 key 与稳定 ID | 稳定 ID test + 容器 smoke | DONE |
| 配方/粒子/音效/效果 | released `26.1.2-fabric` | 对应 `Aether*` 注册类；`AetherResourceIntegrityTest` | 10 recipe types、11 serializers、9 particles、129 sounds、2 effects ID 精确匹配；sound/particle 定义与文件集合一致，client/server 数据加载通过 | 原生/Fabric 注册 | 稳定 ID 快照 + 资源定义/文件扫描 | DONE |
| 创造栏/统计/命令/触发器 | released `26.1.2-fabric` | 对应初始化类 | 10 creative tabs、7 recipe-book categories、2 advancement triggers、1 game event ID 已冻结；统计项与全部命令/触发行为仍未穷举 | Fabric/vanilla API | 稳定 ID test + 启动与触发 smoke | IN_PROGRESS |
| 数据组件/战利品类型 | released `26.1.2-fabric` | data component/loot 注册类 | 2 data components、1 loot condition、4 loot functions ID 已冻结；locked/dungeon-kind 与真实 dungeon unlock live，全部 codec round-trip 尚未穷举 | 原版 Codec/注册表 | stable IDs + codec/live behavior | IN_PROGRESS |
| 世界生成注册项 | released `26.1.2-fabric` + 当前 data | `data/resources/registries/**`、`world/**` | feature/placer/processor/structure type/piece 等 43 个 native type ID 精确匹配，旧 chunks/structures 可读；动态 datapack key 集合与固定 seed 新旧边界尚未穷举 | 稳定动态注册表 key | stable type IDs + datapack load + seed 对比 | IN_PROGRESS |
| 旧 ID 别名/缺失映射 | 用户提供 Fabric `26.1.2` 世界 + 当前注册 | 当前无集中别名策略 | 真实世界的现有 Aether palettes/entities/structures 在 26.2 首次和重启均可读，完整日志没有 missing/unrecognized `aether:*`；超出该 fixture 的历史 ID 不作推断 | 保持本 fixture 的现行 ID，不添加无证据别名 | 旧世界 missing registry 日志 + 6204/6199/6201 chunk-NBT scans | DONE |

## 6. 存档格式与数据迁移矩阵

| 责任域 | 旧实现证据 | 当前文件或实现 | 差异与风险 | 目标实现 | 验证方法 | 状态 |
|---|---|---|---|---|---|---|
| 玩家 Aether 状态 | 官方 NeoForge attachment/capability；当前 codec 字段 | `LegacyPlayerDataMigration`、`AetherPlayerAttachment.CODEC` | 已恢复 NeoForge codec 与 Forge CamelCase 字段读取；真实玩家文件未验证 | 隔离旧键转换，保留未知数据，版本/幂等标记 | 9-test migration suite + 玩家文件副本 | IN_PROGRESS |
| Curios 饰品 | commits `2f5d0c32e`、`09be519d2`、`9d67e2aed`、`d4b554a27` | `LegacyPlayerDataMigration` | 完整 stack 走 PLAYER DFU；恢复 `AccessoriesEncoded` 防重复；真实玩家文件未验证 | 完整 ItemStack 解码、槽映射、冲突保留、幂等写入当前附件 | 单元 fixture + 玩家文件副本 | IN_PROGRESS |
| Accessories `1.1.0-beta.48` | 官方 Fabric/NeoForge source artifact；tag `1.21.1-1.5.10-neoforge` | `LegacyPlayerDataMigration.readAccessoriesSlots` | 支持 released holder/container、cosmetic/render 和任意 slot；旧根保留在 archive | 写入当前 `AccessoryInventoryAttachment`，不引入运行依赖 | SNBT fixture + 真实玩家文件副本 | IN_PROGRESS |
| `ForgeCaps`/`neoforge:attachments` | 历史迁移器与官方 player NBT | `LegacyDataArchiveAttachment`、`LegacyPlayerDataMigration` | 完整根 compound 在转换前复制；真实前后玩家 NBT diff 尚缺 | 只读识别旧格式，不删除整个 compound 或未知键 | archive codec test + 结构化前后 NBT diff | IN_PROGRESS |
| 当前 Fabric 饰品附件 | 当前 attachment codec | `AccessoryInventoryAttachment` | 可保留任意 slot 名，但 `retainSlots` 与 runtime 注册交互需验证 | 未知槽位/物品不丢失 | codec round-trip + 重启 | TODO |
| 实体/Mob attachments | 官方 tag/current codecs | `MobAccessoryAttachment` 等 | 旧未命名 slot 与 attachment key 兼容未知 | 兼容映射并保留未知键 | 实体 NBT fixture | TODO |
| Boss/地牢实体 NBT | `a9b141d5b` 恢复，`257f75b77`/`7f91ea339` 删除 | `BossNbtCompatibility`、三类 Boss | current-first 回退已恢复；真实 Fabric fixture 的 Queen 对话与 Slider 苏醒/受伤成功，BossData/DungeonBounds/Slider `398` health 跨重启可读 | 只读回退；当前保存仍写新格式 | 3 tests + 真实 region scan/start/restart/live interaction | DONE |
| 方块实体/容器 | 官方 tag | 各 block entity `ValueInput`/`ValueOutput` | 旧银地牢普通箱与 `Kind:"aether:silver"` treasure chest 的 loot、Items、菜单和重启持久化已实测；其他 Aether 方块实体仍未覆盖 | 原版 DFU/Codec + 必要 Aether 回退 | fixture + 内容/数量断言 | IN_PROGRESS |
| 用户提供 Fabric 玩家状态 | 真实 `players/data/2e805ebf-...dat`，SHA-256 `F2B9B354...ACDD` | 当前 Fabric attachments/ItemStack DFU | 两次登录后位置/维度、四格物品、Aether player 字段和饰品槽语义稳定；26.2 schema 幂等补空 `aether:back_slot` | 保持当前 Fabric 数据并可重复保存 | 首次 join、跨维度、restart join、结构化 NBT diff | DONE |
| 世界/SavedData/维度 | 用户提供 Fabric `26.1.2` 世界 | dimensions/worldgen/time attachments | 官方 file-structure/DataVersion 升级完成；Aether time 从 `4790` 写为 `4903` 且保持 `18000/true/false`，首次与重启哈希一致；新旧区块边界尚未专门生成比较 | 保持已有 Aether 维度和 SavedData，可幂等重启 | 旧世界副本、真实玩家登录、往返、重启、NBT/region scan | DONE |
| 配置存档 | released Forge/NeoForge TOML | `AetherConfigFile`、released TOML fixtures | 64 项路径/默认值、quoted key、multiline list、invalid fallback、unknown/comment 保留、1 MiB/bounds、原子写入、integrated-server owner guard、remote sync/cleanup 均已验证 | 非破坏读取并写当前格式 | released fixture tests、server restart、remote client join/disconnect | DONE |

## 7. 服务端、客户端和网络矩阵

| 责任域 | 旧实现证据 | 当前文件或实现 | 差异与风险 | 目标实现 | 验证方法 | 状态 |
|---|---|---|---|---|---|---|
| dedicated server | NeoForge server behavior | Fabric common/client entrypoints | 指定版本隔离核心环境首次启动和同世界重启均通过 | Fabric API + Aether 可独立到达可连接状态 | PCL2 隔离 smoke + 重启 | DONE |
| payload 注册与方向 | NeoForge network wrapper | `AetherNetworking`、`AetherNetworkingClient` | 31 clientbound + 17 serverbound registrations 均有显式方向；Aether time/Phoenix arrow 仅 clientbound，Aether player attachment 仅允许 sender 自身的 5 个 client-writable key；server/client receiver 均调度到 logical main thread | typed payload、方向/线程/权限明确 | 注册扫描、`AetherPlayerSyncPacketTest`、remote quick-play join | DONE |
| serverbound 权限与边界 | 旧 handler 接受任意 entity/player ID 和 Hammer client launch | `packet/serverbound/**`、`HammerProjectile`、`SunAltarBlock` | Aerbunny/step-height 绑定当前骑乘者，clear-item/lore/perk 绑定 sender，NPC 要求当前对话者且 8-block 内；accessory slot 有 128-char/definition/index 校验；Hammer launch packet 已删除并保持 server-authoritative hit；sync key/string 分别限 128/1024 字符。Sun Altar 绑定 `BlockPos` 并校验 exact time scale/range、权限、维度、eternal-day、loaded altar 和 8-block 距离；live 已证实 nearby loaded accept、100-block distance reject 和 removed-block reject，其余拒绝分支仍只有代码/单测证据 | 客户端只能提出自己当前上下文允许的动作 | focused validation tests + live malicious/scenario smoke | IN_PROGRESS |
| 登录/重生/换维度/追踪 | NeoForge events | `AetherFabricEvents`、`AttachmentSyncPacketDispatcher`、`AccessorySyncPacketDispatcher` | remote join 配置同步顺序已证实；entity/player attachment 与 accessory 增量只发 tracking + self，tracking start 补发 player/Phoenix-arrow。双客户端已验证 tracking boundary/start-tracking/reconnect；Aether -> Overworld 已验证 `ClientLevel`/tracker 隔离；死亡重生已保持 life shard、last Moa、seen Sun Spirit 并重建 30 max health。旅行并发仍另列 | 服务端权威同步且无重复、无全维度泄漏 | 两客户端重连/追踪 + 换维度 + death/respawn | DONE |
| 玩家旅行状态隔离 | 历史 static shared travel state | `AetherTravelController`、`AetherTravelState` | server teleport timer 改为按 UUID map；旅行/离开 overlay 只发目标 player，不再 broadcast all；尚未双玩家并发验证 | 玩家间不共享计时器或 UI 状态 | 双玩家并发维度旅行 smoke | IN_PROGRESS |
| 客户端渲染 | 官方 Fabric/NeoForge表现 | `AetherSkyRendering`、`AetherFogRendering`、`AetherOverlays`、`DungeonOverlay*`、`AetherTimeAttachment` | custom `dayTime` 现随 join/command 显式同步；clear noon、midnight、thunder/fog 均 live 通过。测试发现并修复 Life Shard hearts 注册在 vanilla health 前导致被覆盖、以及 block atlas 使用 texture path 而非 atlas ID 导致 overlay crash；半心闪烁 sprite 拼写亦已修正 | Fabric client events + 有理由的 Mixin；显式同步非默认 Aether clock | 日/夜/雷雨/HUD/overlay 截图、`F3+T`、clean build | DONE |
| Mixin | 原事件/API缺口 | `aether.mixins.json`；client/common Mixins | 删除世界预览功能移除后无调用的 `EntityRendererAccessor`/`MinecraftAccessor`；20 个 client 行为注入显式 `require = 1`，全局 `defaultRequire = 1` 使 52 个 common 行为注入同样失败可见。强制 smoke 先后暴露并修复 Logo、Phoenix lava travel、Valkyrie Queen default knockback、Swet split/`AbstractCubeMob` 和 timeline/`ClockManager` 目标漂移；最终 dedicated server + client 进入真实 Aether 维度后正常退出。仍需逐项补全 common Mixin 的 API 缺口理由和针对性行为断言 | 最小范围、客户端隔离、失败可见、逐项理由 | config/source audit + pre-fix crashes + server/client Aether retest | IN_PROGRESS |
| 断线/资源重载清理 | 旧客户端事件 | `AetherClient.registerConnectionCallbacks`、`DungeonOverlayTracker` 等 client state managers | `F3+T` 后 121-block overlay 持续渲染且无资源错误；tracker 在新 `ClientLevel` identity 时清空，Aether -> Overworld 持锁定方块截图无陈旧 overlay；显式 kick 触发 tracker/config cleanup，日志确认 server config 回默认。music/tool reset callback 同路执行但尚无独立可观察断言 | 不泄漏跨世界状态 | 连入/换维度/断开/重载循环 | IN_PROGRESS |

## 8. 资源、数据包、数据生成和世界生成矩阵

| 责任域 | 旧实现证据 | 当前文件或实现 | 差异与风险 | 目标实现 | 验证方法 | 状态 |
|---|---|---|---|---|---|---|
| recipes/loot/tags/advancements | 官方 tag + 当前资源 | `src/generated/resources/data`、`src/main/resources` | 全部 packaged JSON 可解析，核心 server datapack load 通过，真实 bronze/silver Boss advancement 触发；集合/codec 与 released datagen 的语义差异仍未全量对比 | vanilla/Fabric 数据格式，语义等价 | JSON parse + live datapack/advancement + datagen diff | IN_PROGRESS |
| models/blockstates/textures/atlases | 官方资源 | `assets/aether/**`、内置 packs；`AetherResourceIntegrityTest` | 全部核心 Aether model/blockstate/item/particle/font 本地引用已扫描，包含 item-atlas directory 和 palette-generated glove trim；atlas/HUD typo 修复后 `F3+T` live 通过。可选 pack 独立行为仍在下一行 | 原版资源格式与实际存在的 sprite/atlas ID | 全量引用扫描 + client reload/log/JAR check | DONE |
| 内置资源包/数据包 | released Fabric `AddPackFindersEvent` 与 NeoForge `AddPackFindersEvent`；commit `eca6e8794` | `resource/AetherBuiltinPacks`、`CombinedPackResources`、repository mixins、`packs/**` | 9 个 released pack 和 exact slash ID 已恢复；classic 两 root 实际读取、JAR 10 目录、client repository startup、server config false/true/restart 已验证。CTM/Tips/Immersive Portals 条件有单测但尚无带 Mod live smoke | vanilla repository source，保持 `builtin/aether_*` ID；不恢复 cross-loader event/facade 或已删除 Accessories packs | catalog/combined tests、JAR 内容、client resource reload、server `datapack list`/restart、optional-Mod smoke | IN_PROGRESS |
| shaders/sounds/translations/pack metadata | 官方资源 | assets/packs；`AetherResourceIntegrityTest` | 2,768 个 packaged JSON 均可解析；129 registered sounds 与 `sounds.json`/本地 OGG 精确一致，client audio/resource reload 通过。翻译键集合/文本与 released 基线尚未逐项 diff | 原版资源格式和稳定 ID | JSON/resource scan + translation baseline diff | IN_PROGRESS |
| load conditions | NeoForge conditions/data maps | generated/main data | 已知部分 NeoForge data map 曾删除，仍需全扫 | Fabric/vanilla condition | JSON 结构扫描 + datapack load | TODO |
| datagen | NeoForge datagen vs Fabric providers | `data/**`、`src/generated/resources` | 任务/可重复性未验证 | Fabric datagen，无陈旧缓存 | 实际 datagen 任务 + clean diff | TODO |
| 维度/生物群系/noise/features | 官方 worldgen data + 用户真实 `26.1.2` regions | `data/resources/registries/**`、generated worldgen | 既有 Aether chunk palettes/biomes 跨首次与重启可读；固定 seed 新区块和边界一致性仍未专门比较 | 稳定动态注册表和可升级数据 | 真实 region scan + 固定 seed 新旧区块比较 | IN_PROGRESS |
| 结构/三类地牢/传送门 | 官方实现/tag + 用户真实 regions | `world/structure*`、portal | fixture 扫描确认 bronze/silver/gold pieces、两个 gold boss room、20 treasure chests、3 portals；旧 Slider 完整击杀/铜门清除/重启通过，旧 Queen 完整击杀/银地牢六类锁门全清/35-chunk 重启复核通过。原档无持久 Sun Spirit，当前实体已通过对话激活/AI但不声称完整击杀 | 已有结构、Boss、门行为等价 | fixture scan + 真实往返 + 三地牢/Boss smoke | IN_PROGRESS |
| 天空/雾/光照/时间天气 | 官方表现；1.21.1 `setDayTime` + vanilla time packet | 环境属性、`AetherSkyRendering`、`AetherTimeAttachment`、`AetherTimeController` | 26.2 vanilla clock packet 不携带 Aether custom clock；现用既有 typed level attachment payload 在 join、`aether time set/add` 和 eternal-day 更新时同步 `dayTime` anchor。26.2 timeline 改接 `ClockManager` 后，Aether-only 缩放改在 `EnvironmentAttributeSystem` 构建该维度 timeline layer 时提供。clear noon、midnight 和 thunder/fog 截图通过；早先 clear-after-thunder 暗场确认为 vanilla weather interpolation，等待后恢复 | 保持 custom clock，并显式同步非 vanilla clock | day/night/weather 截图；命令后 live 观察；`AetherTimeAttachmentTest` | DONE |

## 9. 依赖与可选集成矩阵

| 责任域 | 旧实现证据 | 当前文件或实现 | 差异与风险 | 目标实现 | 验证方法 | 状态 |
|---|---|---|---|---|---|---|
| Fabric Loader/API | 固定环境 | `build.gradle`、`fabric.mod.json` | `0.19.3`/`0.153.0+26.2` 解析并在 Java 25 服务端实际启动 | 保留 | 依赖解析 + 启动 | DONE |
| JEI | Fabric JEI API | `compileOnly` + `jei_mod_plugin` entrypoint | loader 对缺失 JEI entrypoint 的隔离需验证 | 可选软集成，缺失不影响核心 | 无 JEI 启动 + 有 JEI smoke | TODO |
| Jade | Fabric Jade artifact | `compileOnly`，代码集成待定位 | `fabric.mod.json` 未 suggests Jade；类加载隔离未知 | 可选软集成 | 无 Jade/有 Jade 启动 | NOT_AUDITED |
| 其他联动 Mod | 历史兼容 hooks | tags/少量 integration | Twilight 伪 mixin 已删除但全量未审计 | 数据驱动或明确软集成 | 无联动 Mod 核心启动 | TODO |
| 跨加载器/移植库 | 历史 Accessories/Cumulus/Porting Lib | 当前 build/产物 | 解析图无这些依赖；JAR 中 `com.aetherteam.aether.accessories` 是项目领域实现，不是外部库 | 生产图为 Minecraft/Fabric | runtimeClasspath + JAR scan | DONE |

## 10. 测试资产、命令与结果

| 命令/资产 | 时间 | 结果 | 日志/证据 | 状态 |
|---|---|---|---|---|
| `git status --short`、branch、HEAD、recent log | 2026-07-15 | 成功；确认脏客户端改动和 HEAD `8ceaaf2cd` | 本文件接力摘要 | DONE |
| 残留扫描（Forge/NeoForge/Curios/配置 facade/hooks） | 2026-07-15 | 生产源码无直接框架 import；旧格式名只在迁移器；仍发现 `ModConfigSpec` facade 与待审计 hooks | 本文件第 3、6 节 | IN_PROGRESS |
| `./gradlew.bat clean build --console=plain` | 2026-07-15 | 成功，21s；`compileTestJava/test NO-SOURCE`；2 个编译 warning | 历史命令输出；后续 required `clean` 已移除 raw baseline report | DONE |
| `./gradlew.bat test --console=plain` | 2026-07-15 | 成功，9s，但 `test NO-SOURCE`，不构成功能验证 | 历史命令输出；后续 required `clean` 已移除 raw baseline report | DONE |
| `./gradlew.bat clean build --console=plain`（迁移后） | 2026-07-16 | 最新成功，9 tasks/30s；32 tests/12 suites；仅 2 个既有编译 warning | Gradle 输出、`build/test-results/test/` | DONE |
| `./gradlew.bat test --console=plain`（迁移后） | 2026-07-16 | 最新独立运行成功，7s；32 tests，0 failure/error/skip | `build/test-results/test/TEST-*.xml` | DONE |
| `runtimeClasspath` 与产物残留扫描 | 2026-07-15 | 最新解析图仅含 Minecraft、Fabric Loader/API 和正常 transitive libraries；无 Forge/NeoForge/Accessories/Curios/Architectury 运行依赖或类，Aether 自有 `accessories` 包除外 | Gradle dependency output；required `clean` 已移除旧 raw report | DONE |
| `git diff --check` | 2026-07-16 | 退出 0；仅 Git 的 LF/CRLF 工作区提示，无 whitespace error | 命令输出 | DONE |
| 配置 schema/file tests | 2026-07-15 | owner guard 前全量 19 tests 通过；guard 后 focused config 8 tests 通过；覆盖 64 项、released TOML、unknown 保留、原子写入、bounds、同步状态和 integrated-server ownership | `AetherConfigFileTest`、`AetherConfigSchemaTest` | DONE |
| 配置 dedicated server 重启 | 2026-07-15 | 生成 global/per-world TOML；同世界重启读取 `Beds explode = true`、到达 `Done`、保存 Aether 维度并退出 0 | `run/migration-evidence-config-sync-20260715-final/server.log`、`aether-server.toml` | DONE |
| built-in pack tests | 2026-07-15 | `AetherBuiltinPacksTest` 3 + `CombinedPackResourcesTest` 1；exact 9 IDs、10 目录、条件、required/config activation、classic release/shared 实际读取均通过 | JUnit XML；全量 24 tests、0 failure/error/skip | DONE |
| 最终 `clean build`/JAR/残留 | 2026-07-16 | 9 tasks/30s 成功；JAR 含最新 Logo/HUD/atlas/time/migration/Mixin/Moa 修复；runtime graph 只有 Minecraft/Fabric 与正常 transitive libraries；`git diff --check` 仅 line-ending notices；29 个 client Mixin files、20 个显式 required client injections、52 个受全局 required 策略保护的 common behavior injectors，两个死 accessor 与两个过时 common mixin target 不再存在 | `build/libs/aether-26.2-fabric-1.0.0-beta.jar`，38,081,743 bytes，SHA-256 `E48BCAEF6B6FCBCB7C20D0C3991094567BFDBC2556C64337828E3C3D15C07104` | DONE |
| built-in pack dedicated server | 2026-07-15 | config false：两个 exact ID available；config true 新世界：两 pack 自动启用；最终 fresh first start/restart 均为 5 enabled、`Done`、graceful exit 0、四维度保存 | 历史隔离 run；后续 required `clean` 已移除 raw integration directory，catalog/JAR tests 仍可复现 | DONE |
| 合成旧玩家 NBT fixture | 2026-07-15 | released NeoForge Accessories holder 结构；保留第三方 attachment/slot；SHA-256 `AAC0CD49...C90D8F` | `src/test/resources/.../released_neoforge_accessories_player.snbt` | DONE |
| 用户提供真实 Fabric 玩家文件 | 2026-07-16 | 离线 UUID 对应 `CasseShimada`；首次/重启两次实际 join，位置/维度、4 个 stack、Aether player/accessory attachments 均保持；DataVersion `4790 -> 4903` | `run/migration-evidence-world-upgrade-20260716/*nbt-summary.txt`、client/server logs | DONE |
| 用户提供真实 Fabric 世界 | 2026-07-16 | 原件 70 files 前后 manifest 完全相同；副本首次官方升级、往返、保存、重启、再次 join 均通过；Aether time SavedData 首次/重启哈希相同，日志无 missing/unrecognized `aether:*` | `run/migration-evidence-world-upgrade-20260716/RESULTS.md` 及 manifests/logs/screenshots | DONE |
| Forge/NeoForge fixture 验收范围 | 用户 2026-07-16 明确说明无此类存档且只要求适配当前 Fabric 旧档 | 本轮不再等待或生成 Forge/NeoForge fixture，也不声称已经验证 Forge/NeoForge 世界兼容 | 用户明确范围决定 | DONE |
| 指定 PCL2 dedicated server | 2026-07-15 | Java 25，Fabric API + Aether 核心；历史首次及重启到达 `Done`、保存四维度并退出 0；required `clean` 已移除旧 raw report，current-JAR 隔离 first-start 证据重新生成 | `build/reports/migration/network-security/server.log`、`server.exitcode` | DONE |
| 客户端 smoke | 2026-07-16 | dev client 到 OpenGL 3.3、Aether resource reload/OpenAL/atlas；登录 isolated remote Aether 并完成日/夜/天气/HUD/overlay/重载/换维度/断线。最终日志无 pack/Mixin/renderer/resource/classloading error，只有 offline session 预期 401/Realms error；client 正常退出 | `run/migration-evidence-client-rendering-20260715-final/client-overlay.stdout.log`、`client-overlay.stderr.log`、`screenshots/` | DONE |
| Aether time/天空/雾 live | 2026-07-16 | explicit `dayTime` sync 后 `aether time set` 在 remote client 生效；clear noon、midnight、thunder/fog 画面均正确，天气清除后的亮度按 vanilla interpolation 恢复 | `screenshots/2026-07-15_22.07.12.png`、`22.15.07.png`、`22.38.12.png`；`AetherTimeAttachmentTest` | DONE |
| Life Shard HUD live | 2026-07-16 | 初测暴露 silver hearts 在 vanilla health 前绘制而被覆盖；改为 `attachElementAfter(VanillaHudElements.HEALTH_BAR, ...)` 后 5 颗 silver hearts 正确显示；并修复 normal half-blinking sprite ID | `screenshots/2026-07-15_23.26.33.png`；`AetherOverlays`、`AetherOverlayLifeShardHooks` | DONE |
| 地牢覆盖层/reload/清理 live | 2026-07-16 | 首次运行因错误 atlas ID `minecraft:textures/atlas/blocks.png` crash，改用 `AtlasIds.BLOCKS` 后 121-block lock overlay 持续渲染，`F3+T` 后仍正确且无错误；Aether -> Overworld 后持同方块无陈旧 overlay，显式断线触发 cleanup | `screenshots/2026-07-15_23.50.28.png`、`23.59.58.png`、`2026-07-16_01.12.21.png`；pre-fix `crash-2026-07-15_23.30.19-client.txt`；client/server final logs | DONE |
| server config join/disconnect | 2026-07-15 | durable run 把 `Beds explode` 从 `false` 覆盖为 server `true` 并在 disconnect 清回 `false`；server 保存四维度并退出 0 | `run/migration-evidence-config-sync-20260715-final/client.log`、`server.log`、`server-restart.exitcode` | DONE |
| network/time authority tests | 2026-07-16 | `AttachmentSyncableTest`、`AetherPlayerSyncPacketTest`、`SunAltarUpdatePacketTest`、`AetherTimeAttachmentTest` 各 1；覆盖 send-then-local-apply、sender/key allowlist、exact scale/range rejection、`dayTime` sync field setter；全量 28 tests 通过 | `build/test-results/test/TEST-*.xml` | DONE |
| current-JAR quick-play multiplayer | 2026-07-15 | 先前已运行的 current-JAR isolated server 接受 client `Player523` 登录、发送 27 config 并记录断开；同目录 first-start server 与 client exitcode files 为 0。wrapper 的额外 restart 因 world lock 失败，随后对已关闭 pipe 重复写 `stop` 而退出 1；该 harness 错误不作为 restart 成功证据。最终复查无匹配 Java 进程或端口 `25582` listener | `build/reports/migration/network-security/client.log`、`server.log`、`server-restart.log`、`*.exitcode` | DONE |
| 双客户端 attachment tracking/reconnect | 2026-07-15 | `TrackingA` 从 life shard `1 -> 2 -> 3 -> 4`；`TrackingB` nearby 收到 `1`，移到 1000 blocks 外未收到当时的 `2`，返回时立即补 `2`，随后收到 `3`；断线重连立即补 `3`，随后收到 `4`。A/B/reconnect/server 四个 exitcode 均为 0，server graceful stop 保存四维度 | `run/migration-evidence-tracking-sun-altar-20260715-final/client-*-tracking.stdout.log`、`client-b-*.stdout.log`、`server-tracking.latest.log` | DONE |
| Sun Altar 真实交互 | 2026-07-15 | remote client 通过实际 altar screen/slider 发包；loaded `BlockPos{x=0,y=100,z=2}` 且 nearby 时 `accepted=true`，同 screen 下传送至 `(100.5,100,100.5)` 后 `accepted=false`，返回并把 altar 替换为空气后再次 `accepted=false`。client/server exitcode 均为 0；临时诊断随后从源码移除 | `run/migration-evidence-tracking-sun-altar-20260715-final/sun-altar-live-*.log` | DONE |
| client Mixin 强制注入 smoke | 2026-07-16 | 移除 2 个死 accessor；20 个行为注入 `require = 1`。首次暴露旧 Logo target 并 crash；修复为 `extractRenderState` 后完整客户端启动/资源加载且正常退出 | `run/migration-client-mixin-audit-20260716/` | DONE |
| common Mixin 强制注入 + Aether 联合 smoke | 2026-07-16 | 全局 `defaultRequire = 1`；52 个 common behavior injector annotations 失败可见。四次 pre-fix startup 依次暴露 Phoenix lava travel、Valkyrie Queen knockback、Swet split 和 timeline API 漂移；修复后 dedicated server `Done`，quick-play client 完成 OpenGL/resource/OpenAL/atlas、进入并稳定停留 `aether:the_aether`，kick 清理 config，双方正常退出。无 Mixin/Aether/resource/renderer/classloading error | `run/migration-common-mixin-audit-20260716/RESULTS.md`、pre-fix/final logs、RCON transcript | DONE |
| 用户旧档 Aether 内容行为 | 2026-07-16 | 核心-only 副本中，旧银地牢箱和 silver treasure chest 实际开菜单/解 loot，20-slot 自定义奖励与 Kind 跨重启保持；既有 Queen 渲染并打开对话，既有 Slider 从 idle 苏醒、显示 boss bar、受伤 `400 -> 398` 且生命值跨重启保持。首次/重启均 `Done`，无 Aether/Mixin/resource/renderer/classloading error，原件 manifest 仍零差异 | `run/migration-evidence-aether-content-20260716/RESULTS.md`、locator SNBT、client/server logs、6 screenshots | DONE |
| 用户旧档 Aether Moa/Sun/death | 2026-07-16 | 旧野生 Moa 触发并归档 `containsKey(null)` renderer crash；六个皮肤/层 lookup 修复后，同一旧 Moa 渲染、真实上鞍/骑乘及 W 移动通过。当前 Sun Spirit 真实右键激活 boss bar 与水晶 AI；玩家死亡重生保持 `life_shard_count:5`、last-ridden、seen-Sun-Spirit，max health 为 30。post-fix client/server 正常清理退出，原件仍零差异 | `run/migration-evidence-aether-gameplay-20260716/RESULTS.md`、pre-fix crash、post-fix logs、7 screenshots | DONE |
| native registry/resource focused tests | 2026-07-16 | 4 tests 通过：28 个 native 注册声明类共 585 个 released ID 的 count + sorted SHA-256 守卫；2,768 个 JSON 解析；核心 model/blockstate/item/particle/font 引用、atlas virtual sprites、129 sounds/OGG 与 9 particles 定义完整 | `AetherRegistryStabilityTest`、`AetherResourceIntegrityTest`；`run/migration-evidence-registry-resource-boss-20260716/RESULTS.md` | DONE |
| 用户旧档 Slider/Queen 完整击杀 | 2026-07-16 | 旧 Slider 玩家归因击杀触发 `Like a Bossaru!`，铜房 20 个 doorway blocks 清除且重启保持；旧 Queen 玩家归因击杀触发 `Dethroned`，完整 35-chunk `DungeonBounds` 内六类锁/陷阱/门块清零且重启保持。Queen 本轮只为 death/unlock flow 在副本注入 `Ready:1b`，不把它算作第二次真实 medal hand-in | `run/migration-evidence-registry-resource-boss-20260716/RESULTS.md`、client/server/restart logs、2 screenshots | DONE |

## 11. 风险、阻塞和待确认事项

- **已解除的真实旧档阻塞**：用户提供的 Fabric `26.1.2` 世界/玩家已完成首次升级、真实 join、跨维度、保存、重启和再次 join；原件保持不变。此证据只支持该 fixture 与 Aether 域，不外推 Forge/NeoForge 或未出现的历史 ID。
- **高风险已下降**：真实 fixture 已覆盖 Aether terrain/structure/entity ID、玩家附件/SavedData、两类银箱、旧 Slider/Queen 完整击杀与地牢解锁、旧 Moa 骑乘、当前 Sun Spirit 激活/AI 和死亡复制；原档没有持久 Sun Spirit，因此未验证“旧金 Boss 实体升级后完整击杀”。其余方块实体/AI 及固定 seed 新旧区块边界仍不能从当前 smoke 外推。
- **中风险**：20 个 client 行为注入和 52 个 common 行为注入现均失败可见，强制 smoke 已实际发现/修复五处目标漂移；但部分 target class/分支只在特定实体或交互出现，且 common Mixins 的逐项 API 缺口理由与行为断言仍未全部完成。风险已从“静默失效”收敛为“未触发场景仍可能在首次类加载时硬失败”。
- **中风险**：内置 pack 核心恢复已通过 client/server，但 CTM、Tips 和 Immersive Portals 条件分支只由 catalog 单测覆盖，尚未在安装对应 Fabric Mod 时 live smoke。
- **中风险**：当前工作区含未提交客户端天空、雾、HUD、地牢覆盖层改动；核心 model/blockstate/item/particle/font/sound 引用现已自动扫描且 live reload 通过，但 GUI 行为、翻译集合、可选 pack 和数据语义仍未完成客户端/资源域全量审计。
- **中风险**：Sun Altar 的 loaded `BlockPos`、nearby accept、distance reject 和 removed-block reject 已 live 验证；exact scale/range 有单测，但权限、dimension、eternal-day 与 malformed-client 组合仍只有代码/单测证据。
- **中风险**：tracking + self dispatch 和 tracking-start/reconnect resync 已通过双客户端验证；Aether -> Overworld level/tracker cleanup 与 life-shard/last-Moa/seen-Sun-Spirit 死亡复制均已 live，剩余风险是每玩家旅行 timer/UI 并发场景。
- **低风险已知限制**：`LoreExistsPacket` 已限制为 sender 自身且要求当前 `LoreBookMenu`，但 advancement boolean 仍依赖客户端语言/资源包是否含 lore key；服务端无法独立判断客户端资源知识。
- **已接受的范围限制**：用户明确只验收 The Aether 和所提供的 Fabric 存档，不要求 Forge/NeoForge 存档，也忽略其他 Mod。因此 Twilight Forest/Farmer's Delight/Kaleidoscope/FTB 的 pack、recipe、advancement、game rule 与 attachment 诊断均不计入结果；若以后需要这些 Mod，必须从仍未修改的原件重新复制并安装对应 26.2 版本。

## 12. 决策日志

- 2026-07-15：建立本审计作为唯一实时迁移状态入口。依据：主提示词要求可脱离对话接力。影响：以后每次代码/验证/决策变化同步更新；审计结论可随新证据回退。
- 2026-07-15：否决 `docs/accessory-core-audit.md` 中“主动移除旧存档迁移”的完成结论。依据：当前目标明确把有效 Forge/NeoForge 存档兼容列为发布阻断项；Git 历史证明读取器和 Boss 回退确实被删除。影响：相关项回到 `IN_PROGRESS`；仅恢复数据格式读取，不恢复任何 Forge/NeoForge/Curios运行依赖或 facade。
- 2026-07-15：优先恢复持久化兼容，再继续客户端命名清理。依据：存档丢失不可逆且位于服务端/网络/客户端依赖链上游；当前客户端改动归属未知。可撤销条件：证据证明旧格式从未发布或 Minecraft 官方升级链完全无法到达 26.2。
- 2026-07-15：历史 Curios 转换器不能原样恢复。依据：它只按 item ID 创建数量 1 的新 stack、仅迁移 Aether namespace、未处理冲突/未知第三方数据，也没有可靠幂等写入。影响：使用隔离迁移模块并补 fixture 测试。
- 2026-07-15：以 Accessories 官方 `1.1.0-beta.48+1.21.1` Fabric/NeoForge source artifact 为 released 格式证据。两 loader 均注册 `accessories:inventory_holder`；holder 写 `accessories_containers`，container 写 `current_size`、`items`、`cosmetics`、`render_options`。影响：直接解析稳定 NBT，不引入 Accessories 运行依赖；任意 slot 与完整旧根进入 archive。
- 2026-07-15：当前 Fabric 玩家状态和饰品附件采用逐附件优先级，而不是任一当前数据阻止全部转换。依据：玩家可能已有 `aether:aether_player` 但仍只有旧 Accessories inventory，反之亦然。影响：各自存在时只覆盖对应旧域。
- 2026-07-15：PCL2 验证必须隔离。依据：指定目录含活动 `world` 与四个 Mod；本轮在 `build/integration/pcl2-core-server-20260715` 使用复制的 launcher/API/Aether、新世界与端口 0，未修改原世界或 mods。
- 2026-07-15：内置 pack 不直接改用 `ResourceLoader.registerBuiltinPack`。依据：Fabric API 26.2 将 `Identifier.toString()` 固定作为 pack ID 且只读取 `resourcepacks/<path>`，无法保持 released `builtin/aether_*` slash ID，也无法表达 classic 两目录合并；released Fabric `1.21.1-1.5.10-fabric` 确认使用 slash ID。影响：使用范围最小的 vanilla `PackRepository` source 注入并补 ID/catalog 测试，不恢复跨加载器 `AddPackFindersEvent` facade。
- 2026-07-15：将 `CombinedPackResources` 从 `client` 移到 common `resource` 域。依据：它只依赖 vanilla pack API，repository source 在 dedicated server 也会被类加载；客户端包名会制造错误的环境边界。影响：client/server 均可安全加载 catalog，classic assets 仍保持 release root 优先、shared root 回退。
- 2026-07-15：为 server config join/leave 增加一次性 INFO 诊断。依据：单元测试不能证明真实 remote payload 与 disconnect callback 已执行；日志同时记录 entry count 和一个刻意非默认值。影响：每次 remote join/leave 各一行可操作日志；integrated server 仍由 path-owner guard 保持本地文件所有权。
- 2026-07-15：attachment `setSynced*` 在 packet dispatch 后立即执行本地 setter。依据：released Nitrogen `1.1.24` 的 `INBTSynchable` bytecode 在各发送分支汇合后调用同步字段 `Consumer`；旧当前实现只发包，造成发送端状态延迟或永久不更新。影响：恢复 released 行为并由 `AttachmentSyncableTest` 固定 send-before-apply 顺序。
- 2026-07-15：网络同步采用显式最小方向与 tracking scope。依据：Aether time/Phoenix arrow 不存在合法 client write；旧 Hammer launch 和任意 entity/UUID serverbound payload 可伪造；附件 broadcast-all 泄漏无关实体状态。影响：删除 Hammer serverbound payload，player attachment 用 sender/key allowlist，其余 handler 绑定 sender 当前上下文，entity sync 发 tracking + self 并在 start-tracking 补发。
- 2026-07-15：双客户端与 Sun Altar live smoke 使用 JVM property gated 的最小 INFO 诊断，只记录 life-shard 同步接收值、altar screen 位置和服务端 accept/reject，不改变 payload 或权限语义。依据：需要区分 tracking boundary、start-tracking/reconnect 补发和多个服务端拒绝分支。影响：原始证据复制到 `run/migration-evidence-tracking-sun-altar-20260715-final/` 后，诊断代码已全部删除；最终产物不含 property 或 marker。
- 2026-07-16：Aether custom `dayTime` 必须通过既有 typed level attachment payload 显式同步。依据：Minecraft 26.2 vanilla time packet 不携带 Aether 自有 clock anchor，remote client 在 `aether time set/add` 后仍使用旧值。影响：join、命令和 eternal-day update 同步 `setDayTime`；`AetherTimeAttachmentTest` 固定 sync field setter，live day/night/weather 通过。
- 2026-07-16：Life Shard hearts 注册在 `VanillaHudElements.HEALTH_BAR` 之后。依据：Fabric HUD element 顺序实测表明注册在 health 前会被 vanilla hearts 覆盖。影响：silver hearts 恢复可见且保持 vanilla health layout 之后叠加；5-heart screenshot 通过。
- 2026-07-16：地牢覆盖层通过 `AtlasIds.BLOCKS` 查询 atlas，并按 `ClientLevel` identity 隔离跟踪列表。依据：26.2 atlas manager 拒绝 texture path `TextureAtlas.LOCATION_BLOCKS`；static positions 跨维度会引用旧 level。影响：overlay 通过 `F3+T`，换维度和断线均清理；pre-fix crash 保留为回归证据。
- 2026-07-16：用户将旧存档验收范围明确收敛为其提供的 Fabric `26.1.2` 存档，并允许忽略其他 Mod。依据：用户没有 Forge/NeoForge 存档且只要求 The Aether 正常。影响：不再将未提供的 Forge/NeoForge fixture 作为当前阻塞，也不据此作兼容声明；核心-only 测试中的第三方数据诊断明确列为范围限制。
- 2026-07-16：所有 client 行为注入必须显式 `require = 1`，死 accessor 直接删除。依据：`defaultRequire: 0` 会让签名漂移静默退化；启用强制注入立即暴露 26.2 已删除的 Logo target。影响：20 个注入点失败可见，Logo 改挂 `extractRenderState` 并通过完整 client resource smoke；common Mixins 随后同法完成强制失败策略审计。
- 2026-07-16：`aether.mixins.json` 全局 `defaultRequire` 改为 `1`，common behavior injectors 不允许静默跳过。依据：该域没有声明可选注入，且首次强制启动连续暴露四处真实 26.2 语义漂移。影响：Phoenix vertical lava swim 改挂 `travelInLava` 的 `move` 后，Valkyrie Queen 抑制改包 `dealDefaultKnockback`，Swet 分裂规则迁至 `AbstractCubeMob.remove`，Aether timeline 缩放迁至 dimension-specific `ClockManager` wrapper；obsolete `SlimeMixin`/`TimelineMixin` 删除。
- 2026-07-16：真实旧存档只在 byte-identical 副本上升级，且用同离线 UUID 实际登录。依据：server 空载不能验证玩家 NBT，直接打开原件不可回滚。影响：原件两个 manifest 完全一致；证据包含首次/重启 NBT scan、客户端截图和完整日志。
- 2026-07-16：native registry 稳定性以用户存档来源对应的 released `26.1.2-fabric` 源码冻结 count + sorted SHA-256，而不是在 vanilla registry freeze 之后尝试重新注册。依据：目标是守住公开 ID 集合，源级声明快照可在普通 JUnit 中稳定复现。影响：28 个声明类/585 个 ID 有自动回归守卫；动态 datapack registry keys 仍在世界生成矩阵单列。
- 2026-07-16：原档没有持久 Sun Spirit，不把人工生成的当前实体完整击杀当作“旧实体迁移”必需证据。依据：用户要求适配其提供的存档；当前 Sun Spirit 已完成渲染、末段对话、激活、boss bar 与水晶 AI。影响：旧 Slider/Queen 完整死亡/解锁路径必须通过；Sun Spirit full kill 留在严格全项目矩阵但不阻塞此 fixture 验收。
- 2026-07-16：Queen 的本轮 death/unlock 验证只在 disposable 副本注入 `Ready:1b`。依据：之前已真实打开旧 Queen 对话，本轮目标是独立验证 combat death、advancement 和地牢解锁/持久化。影响：证据不会把这次测试写成 Victory Medal hand-in；完整 35-chunk unlock scan 仍是有效结果。

## 13. 工作日志与下一步

- 2026-07-15：读取工作区、分支、HEAD、最近提交、Gradle/Fabric 元数据、Mixin 配置、现有两份迁移文档与 refs；确认无 `AGENTS.md`。
- 2026-07-15：结构化扫描 845 个主 Java 文件、1326 个主资源和 2554 个生成资源；发现本地 `ModConfigSpec` facade、旧迁移缺失和大量待逐项说明的 Mixins。
- 2026-07-15：定位旧兼容证据：`2f5d0c32e` 初始 Forge Curios 转换、`09be519d2` NeoForge attachment 支持、`9d67e2aed` Fabric join 恢复、`a9b141d5b` 删除、`7f91ea339` 删除 Boss NBT 回退。
- 2026-07-15：基线 `clean build` 成功；独立 `test` 成功但无测试源。可用 Loom 运行任务只有 `runClient`/`runServer`，仓库没有 datagen/GameTest 任务；后续必须补充测试资产。
- 2026-07-15：新增 `BossNbtCompatibility` 与 3 个测试，恢复 `BossData` current-first、旧 `Dungeon`/`OriginX`/`RoomBounds*` 及 Valkyrie flattened bounds 只读回退。
- 2026-07-15：新增 archive-first 玩家迁移：Forge/NeoForge Aether state、Curios、released Accessories holder、完整 ItemStack DFU、cosmetic/render、独立当前附件优先级和 `AccessoriesEncoded` 防重复；9 tests 通过。
- 2026-07-15：迁移后 `clean build`、全量 12 tests、runtimeClasspath、残留扫描和 `git diff --check` 通过；产物 SHA-256 `E4E544114789DFDFBD4B987112526D2A60595946D78DB270FD5A14A6F3C249D9`。
- 2026-07-15：PCL2 隔离核心 server 首次启动和同世界重启通过，`aether:the_aether` 均保存；未留下 Java 进程。客户端与真实旧存档仍未验证。
- 2026-07-15：删除 `ModConfigSpec` facade，新增 Aether 原生 64-entry schema/TOML 文件、per-world server 生命周期、server config payload、owner guard 和 released fixture；配置 server 同世界重启通过。
- 2026-07-15：确认 `packs/classic_125`、`classic_b173`、`classic_base`、`colorblind`、`ctm_fix`、`tips`、`tooltips`、`imm_ptl_compat`、`temporary_freezing`、`ruined_portal` 均无 `pack.mcmeta` 且当前无注册消费者；released Fabric/NeoForge 历史共定义 9 个对外 pack。
- 2026-07-15：新增 `AetherBuiltinPacks`、common `CombinedPackResources`、repository/accessor mixin 和 4 tests；最终 24 tests、clean build、JAR/residual/diff checks 通过。
- 2026-07-15：隔离 PCL2 core server 验证 config false/true、fresh world 和 same-world restart；exact `builtin/aether_ruined_portal`/`builtin/aether_temporary_freezing` 选择持久。dev client 完成资源/音频/atlas 启动；未留下 Java 进程。
- 2026-07-15：隔离 server + dev client 实际连接完成：27-entry packet 把 `Beds explode` 从默认 `false` 覆盖为 server `true`，server graceful stop 触发 disconnect cleanup 回 `false`；同世界 server 保存四维度并退出 0，client `runClient` 正常结束。随后 `clean build`、独立 `test`、24 tests、JAR/residual/diff checks 全部通过。
- 2026-07-15：完成 31 clientbound/17 serverbound registration 与 handler 审计；限制 player/entity ownership、NPC/altar 距离、slot/string/value bounds，删除 Hammer launch，缩小 tracking dispatch，恢复 start-tracking resync，并把旅行状态隔离到单玩家。新增 3 tests，最终 27 tests/9 suites 全绿。
- 2026-07-15：current-JAR isolated server first start 保存四维度；quick-play client 随后登录一个仍在运行的 isolated server、同步配置并断开。额外 restart 因 world lock 失败，wrapper 又对 closed pipe 写 stop 而退出 1；因此只采信 first-start 和 client join，不采信该 restart。最终无匹配进程/port listener。
- 2026-07-15：双客户端 tracking/reconnect live smoke 通过：nearby `1`、far 时不接收 `2`、返回补 `2`、共同接收 `3`、重连补 `3`、共同接收 `4`。Sun Altar 实际 screen/slider smoke 通过 loaded nearby accept、100-block reject、removed-block reject；六个进程退出 0。复制持久证据、移除临时诊断后，最终 `clean build`/独立 `test` 为 27 tests/9 suites 全绿，JAR SHA-256 回到 `E7B69B...39261FD`，`git diff --check` 无 whitespace error，端口 `25583` 已释放。
- 2026-07-16：isolated core server/client 完成 Aether client rendering audit。显式 `dayTime` sync 后 clear noon/midnight/thunder-fog 正确；修复 HUD element 顺序并显示 5 silver hearts；修复 dungeon atlas crash 后 121-block overlay 在 `F3+T` 前后正确。Aether -> Overworld `ClientLevel` replacement 无陈旧 overlay，kick 日志确认 config cleanup；server 保存四维度并优雅停止。
- 2026-07-16：将七张关键截图、最终 client/server logs、server properties 和 pre-fix atlas crash 复制到 `run/migration-evidence-client-rendering-20260715-final/`。随后 `clean build` 29s、独立 `test` 6s、28 tests/10 suites 全绿，`git diff --check` 无 whitespace error，JAR SHA-256 `F069A0E7...84DE6D`；audit client/server 均已退出。
- 2026-07-16：client Mixin 批次删除 2 个世界预览遗留 accessor，为 20 个行为注入添加 `require = 1`；首次 smoke 暴露并归档旧 `LogoRenderer.renderLogo` target crash，改挂 `extractRenderState` 后 OpenGL/resource/OpenAL/全部 atlas 和正常退出通过。
- 2026-07-16：用户真实 Fabric `26.1.2` 世界原件建立 70-file manifest 后复制；扫描 6204 个 chunk/entity NBT，确认大量 Aether terrain、三类地牢、portal、Boss/生物和 SavedData。当前核心服完成 DataVersion `4790 -> 4903`、同 UUID 两次 join、Overworld/Aether 往返、两次保存与优雅停止；Aether time 和玩家语义幂等，原件复核未变。持久证据在 `run/migration-evidence-world-upgrade-20260716/`。
- 2026-07-16：common Mixin 强制启动依次归档四份 pre-fix failure；最终 core server 到 `Done`，dev client 进入并稳定停留 Aether，配置同步/断线清理、正常客户端退出和服务端四维度保存均通过。持久证据在 `run/migration-common-mixin-audit-20260716/`。
- 2026-07-16：移除重复 mixin config entry 后执行最终 required `clean build` 30s 与独立 `test` 7s，28 tests/10 suites 全绿；`git diff --check` 退出 0；最终 JAR SHA-256 `B4BB35B0...DA5A09`。测试进程与端口 `25584-25591` 均已释放。
- 2026-07-16：从用户旧档离线定位真实银地牢箱、silver treasure chest、Valkyrie Queen 和 Slider，在核心-only 副本用旧 NBT 完成菜单/loot/对话/苏醒/伤害与重启持久化；六张截图和完整日志归档于 `run/migration-evidence-aether-content-20260716/`，原件 70-file manifest 仍零差异。
- 2026-07-16：旧野生 Moa live 暴露 renderer 对 null LastRider 的 `containsKey` crash；为基础纹理及五个 skin layer 全部加 guard，随后同一旧实体上鞍/骑乘/W 移动通过。当前 Sun Spirit 对话激活/AI 与玩家 life-shard/last-Moa/seen-Sun-Spirit 死亡复制通过；pre-fix crash、post-fix logs 与七张截图归档于 `run/migration-evidence-aether-gameplay-20260716/`。
- 2026-07-16：Moa 修复后最终 required `clean build` 33s、独立 `test` 7s 通过，28 tests/10 suites 全绿；`git diff --check` 无 whitespace error，JAR SHA-256 `E48BCAEF...C07104`，Aether audit ports `25584-25595` 全部释放。
- 2026-07-16：新增 stable registry/resource tests，focused 4 tests 通过；在核心-only 旧档副本完成既有 Slider/Queen 玩家归因击杀、进度、房门/地牢解锁与重启复核，证据归档于 `run/migration-evidence-registry-resource-boss-20260716/`，原件 70-file manifest 再次零差异。
- 2026-07-16：证据固化后执行最终 `clean build` 30s 与独立 `test` 7s，32 tests/12 suites、0 failure/error/skip；`git diff --check` 退出 0；JAR 38,081,743 bytes、SHA-256 `E48BCAEF...C07104`。原件再次复算 `70 files / 0 diffs`，证据日志无新增 Aether/Mixin/renderer/resource/classloading error，端口 `25594/25595` 与 Aether 审计进程均已释放。
- 下一步唯一优先任务：仅继续 The Aether 在该 fixture 未出现的方块实体/物品行为、事件、动态数据/世界生成和剩余 Mixin 场景；其他 Mod 及未提供的 Forge/NeoForge fixture 不作为阻塞。
