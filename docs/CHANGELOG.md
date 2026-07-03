# The Aether Fabric Branch Changelog

This branch tracks Fabric-specific development notes for the current codebase.

## Fabric Native Refactor

- Removed old save migration support for Curios data, Forge capability data, and `neoforge:attachments` data.
- Removed legacy boss room NBT fallback reads for the old nested `Dungeon`/`OriginX` format.
- Kept current Fabric attachment storage, accessory synchronization, networking, dimensions, entities, blocks, items, and world generation behavior intact.
- Replaced reflective registry bootstrap paths with explicit registration bootstrap calls where safe.
- Moved client packet effects for custom boss bars and the Sun Altar screen into the Fabric client networking entrypoint.
- Moved block-state recipe support classes into the Aether recipe namespace.
- Replaced block-state recipe mcfunction reflection with direct Minecraft function manager calls.
- Renamed the attachment sync interface away from its old NBT-oriented name.
- Normalized attachment sync API method names to the current `Synced` spelling.

Historical release notes for other platform branches are not duplicated in this Fabric branch changelog.
