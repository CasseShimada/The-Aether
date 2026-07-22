package com.aetherteam.aether.integration.viewer;

import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/** Real-client smoke and screenshot coverage for optional Jade/WTHIT viewer combinations. */
@SuppressWarnings("UnstableApiUsage")
public final class AetherViewerClientGameTest implements FabricClientGameTest {
    private static final BlockPos TARGET = new BlockPos(0, 65, -3);

    @Override
    public void runTest(ClientGameTestContext context) {
        String expected = System.getProperty("aether.viewerCompatTest", "wthit");
        boolean jadeLoaded = FabricLoader.getInstance().isModLoaded("jade");
        boolean wthitLoaded = FabricLoader.getInstance().isModLoaded("wthit");
        assertExpectedViewers(expected, jadeLoaded, wthitLoaded);

        try (TestSingleplayerContext singleplayer = context.worldBuilder().create()) {
            singleplayer.getClientLevel().waitForChunksDownload();
            singleplayer.getServer().runCommand("fill -4 63 -6 4 63 3 minecraft:bedrock");
            singleplayer.getServer().runCommand("fill -4 64 -6 4 68 3 minecraft:air");
            singleplayer.getServer().runCommand("setblock 0 64 -3 minecraft:stone");
            singleplayer.getServer().runCommand("setblock 0 65 -3 aether:chest_mimic");
            singleplayer.getServer().runCommand("gamemode survival @p");
            singleplayer.getServer().runCommand("tp @p 0.5 64 0.5 180 0");

            context.waitTicks(40);
            context.waitFor(client -> client.hitResult instanceof BlockHitResult hit
                    && TARGET.equals(hit.getBlockPos()));
            assertScreenshotWritten(context.takeScreenshot(expected + "-mimic-survival"));

            singleplayer.getServer().runCommand("gamemode creative @p");
            context.waitTicks(20);
            context.waitFor(client -> client.hitResult instanceof BlockHitResult hit
                    && TARGET.equals(hit.getBlockPos()));
            assertScreenshotWritten(context.takeScreenshot(expected + "-mimic-creative"));
        }
    }

    private static void assertExpectedViewers(String expected, boolean jadeLoaded, boolean wthitLoaded) {
        boolean expectedJade = expected.equals("jade") || expected.equals("both");
        boolean expectedWthit = expected.equals("wthit") || expected.equals("both");
        if (jadeLoaded != expectedJade || wthitLoaded != expectedWthit) {
            throw new AssertionError("Expected viewer mode " + expected
                    + " but loaded jade=" + jadeLoaded + ", wthit=" + wthitLoaded);
        }
    }

    private static void assertScreenshotWritten(Path screenshot) {
        try {
            if (!Files.isRegularFile(screenshot) || Files.size(screenshot) == 0L) {
                throw new AssertionError("Client compatibility screenshot was not written: " + screenshot);
            }
        } catch (IOException exception) {
            throw new AssertionError("Could not inspect client compatibility screenshot: " + screenshot, exception);
        }
    }
}
