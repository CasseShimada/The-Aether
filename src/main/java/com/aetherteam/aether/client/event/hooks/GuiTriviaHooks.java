package com.aetherteam.aether.client.event.hooks;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.AetherConfig;
import com.aetherteam.aether.event.hooks.DimensionTravelState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.GenericMessageScreen;
import net.minecraft.client.gui.screens.LevelLoadingScreen;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.ProgressScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import javax.annotation.Nullable;

final class GuiTriviaHooks {
    private static boolean generateTrivia = true;
    @Nullable
    private static Screen lastScreen;

    private GuiTriviaHooks() {
    }

    static void drawTrivia(Screen screen, GuiGraphicsExtractor guiGraphics) {
        generateTrivia(screen);
        drawTriviaLine(screen, guiGraphics);
        if (screenChangedForTrivia(screen) && !Aether.TRIVIA_READER.getTrivia().isEmpty()) {
            Aether.TRIVIA_READER.randomizeTriviaIndex();
        }
        lastScreen = screen;
    }

    static void drawAetherTravelMessage(Screen screen, GuiGraphicsExtractor guiGraphics) {
        if (!(screen instanceof LevelLoadingScreen || screen instanceof ProgressScreen)) {
            DimensionTravelState.displayAetherTravel = false;
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || !DimensionTravelState.displayAetherTravel) {
            return;
        }

        Component message = DimensionTravelState.playerLeavingAether
                ? Component.translatable("gui.aether.descending")
                : Component.translatable("gui.aether.ascending");
        guiGraphics.centeredText(minecraft.font, message, screen.width / 2, AetherConfig.CLIENT.portal_text_y.get(), 16777215);
    }

    private static void drawTriviaLine(Screen screen, GuiGraphicsExtractor guiGraphics) {
        if (!(screen instanceof GenericMessageScreen || screen instanceof LevelLoadingScreen)) {
            return;
        }

        Component triviaLine = Aether.TRIVIA_READER.getTriviaLine();
        if (triviaLine == null || !AetherConfig.STARTUP.enable_trivia.get()) {
            return;
        }

        Font font = Minecraft.getInstance().font;
        int y = (screen.height - 7) - font.wordWrapHeight(triviaLine, screen.width);
        for (FormattedCharSequence sequence : font.split(triviaLine, screen.width)) {
            guiGraphics.centeredText(font, sequence, screen.width / 2, y, 16777113);
            y += 9;
        }
    }

    private static boolean screenChangedForTrivia(Screen screen) {
        return (screen instanceof TitleScreen && !(lastScreen instanceof TitleScreen))
                || (screen instanceof PauseScreen && !(lastScreen instanceof PauseScreen));
    }

    private static void generateTrivia(Screen screen) {
        if (!(screen instanceof TitleScreen || screen instanceof LevelLoadingScreen) || !generateTrivia) {
            return;
        }

        if (Aether.TRIVIA_READER.getTrivia().isEmpty()) {
            Aether.TRIVIA_READER.generateTriviaList();
            generateTrivia = false;
        }
    }
}
