package com.aetherteam.aether.client.gui.screen.inventory;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.client.gui.component.inventory.LorePageButton;
import com.aetherteam.aether.inventory.menu.LoreBookMenu;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoreBookScreen extends AbstractContainerScreen<LoreBookMenu> {
    private static final Identifier TEXTURE_LORE_BACKING = Identifier.fromNamespaceAndPath(Aether.MODID, "textures/gui/menu/lore_backing.png");
    private static final Identifier TEXTURE_LORE_BOOK = Identifier.fromNamespaceAndPath(Aether.MODID, "textures/gui/menu/lore_book.png");

    private final Map<Integer, List<String>> pages = new HashMap<>();

    private LorePageButton previousButton, nextButton;
    private int currentPageNumber;
    private ItemStack currentLoreStack = ItemStack.EMPTY;
    private boolean currentLoreExists;
    private String currentLoreEntryKey = "";
    private String currentLoreText = "";
    private String lastLoggedLoreState;
    private String lastLorePageState;
    private String lastLoggedRenderState;

    public LoreBookScreen(LoreBookMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, 256, 199);
    }

    @Override
    protected void init() {
        super.init();
        int xPos = (this.width - this.imageWidth) / 2;
        int yPos = (this.height - this.imageHeight) / 2;
        this.previousButton = this.addRenderableWidget(new LorePageButton(new Button.Builder(Component.literal("<"), (button) -> {
            if (this.currentPageNumber > 0) {
                this.currentPageNumber--;
            }
        }).bounds(xPos + 14, yPos + 169, 20, 20)));
        this.nextButton = this.addRenderableWidget(new LorePageButton(new Button.Builder(Component.literal(">"), (button) -> {
            if (this.currentPageNumber < this.pages.size() - 1) {
                this.currentPageNumber++;
            }
        }).bounds(xPos + 221, yPos + 169, 20, 20)));
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.updateLoreContent();
        super.extractRenderState(guiGraphics, mouseX, mouseY, partialTicks);
        guiGraphics.nextStratum();
        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().translate(this.leftPos, this.topPos);
        this.renderLoreContent(guiGraphics);
        guiGraphics.pose().popMatrix();
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor guiGraphics, int x, int y) {
        // Draws text for the page switching buttons.
        Component previous = Component.translatable("gui.aether.book_of_lore.previous");
        Component next = Component.translatable("gui.aether.book_of_lore.next");
        this.drawNormalBookText(guiGraphics, this.font, previous, 13, 158);
        this.drawNormalBookText(guiGraphics, this.font, next, 221, 158);

        // Draws "Book of Lore" text.
        Component book = Component.translatable("gui.aether.book_of_lore.book");
        Component ofLore = Component.translatable("gui.aether.book_of_lore.of_lore");
        this.drawCenteredBookText(guiGraphics, this.font, book, 75, 20);
        this.drawCenteredBookText(guiGraphics, this.font, ofLore, 75, 20 + 10);

        // Draws "Item:" text.
        Component item = Component.translatable("gui.aether.book_of_lore.item");
        this.drawRightBookText(guiGraphics, this.font, item, 78, 67);

        // Determines when the page switching buttons can be clicked.
        this.previousButton.active = this.currentPageNumber > 0;
        this.nextButton.active = this.currentPageNumber < this.pages.size() - 1;
    }

    private void updateLoreContent() {
        ItemStack itemStack = this.getMenu().getSlot(0).getItem();
        if (itemStack.isEmpty()) {
            this.currentLoreStack = ItemStack.EMPTY;
            this.currentLoreExists = false;
            this.currentLoreEntryKey = "";
            this.currentLoreText = "";
            this.pages.clear();
            this.currentPageNumber = 0;
            this.lastLorePageState = "";
        } else {
            String entryKey = this.getMenu().getLoreEntryKey(itemStack);
            boolean exists = this.getMenu().loreEntryKeyExists(itemStack);
            String resolvedText = exists ? this.getMenu().resolveLoreEntryText(entryKey) : "";
            String state = LoreBookMenu.describeStack(itemStack) + "|" + entryKey + "|" + exists + "|" + resolvedText;

            this.currentLoreStack = itemStack.copy();
            this.currentLoreExists = exists;
            this.currentLoreEntryKey = entryKey;
            this.currentLoreText = resolvedText;
            this.logLoreState(itemStack, entryKey, exists, resolvedText);

            if (!state.equals(this.lastLorePageState)) {
                this.lastLorePageState = state;
                this.pages.clear();
                this.currentPageNumber = 0;
                if (exists) {
                    this.createPages(Component.literal(resolvedText));
                }
            }
            this.currentPageNumber = Math.min(this.currentPageNumber, Math.max(this.pages.size() - 1, 0));
        }

        if (this.previousButton != null && this.nextButton != null) {
            this.previousButton.active = this.currentPageNumber > 0;
            this.nextButton.active = this.currentPageNumber < this.pages.size() - 1;
        }
    }

    private void renderLoreContent(GuiGraphicsExtractor guiGraphics) {
        if (this.currentLoreStack.isEmpty() || !this.currentLoreExists || this.pages.isEmpty()) {
            return;
        }

        int titleX = 136;
        int titleY = 10;
        int bodyY = 32;
        List<String> currentPage = this.pages.get(this.currentPageNumber);
        int lineCount = currentPage != null ? currentPage.size() : 0;
        String renderState = LoreBookMenu.describeStack(this.currentLoreStack) + "|page=" + this.currentPageNumber + "|pages=" + this.pages.size() + "|lines=" + lineCount + "|x=" + titleX + "|titleY=" + titleY + "|bodyY=" + bodyY;
        if (!renderState.equals(this.lastLoggedRenderState)) {
            this.lastLoggedRenderState = renderState;
            Aether.LOGGER.info("Book of Lore render pass: stack={}, page={}, pageCount={}, lineCount={}, localTitlePos=({}, {}), globalTitlePos=({}, {}), globalBodyY={}",
                    LoreBookMenu.describeStack(this.currentLoreStack), this.currentPageNumber, this.pages.size(), lineCount, titleX, titleY, this.leftPos + titleX, this.topPos + titleY, this.topPos + bodyY);
        }

        if (this.currentPageNumber == 0) {
            this.createText(guiGraphics, wrapText(this.currentLoreStack.getHoverName().getString(), 98), titleX, titleY);
            this.createText(guiGraphics, this.pages.get(0), titleX, bodyY);
        } else {
            this.createText(guiGraphics, this.pages.get(this.currentPageNumber), titleX, titleY);
        }
    }

    /**
     * Splits lore entry text into lines and pages in the Book of Lore.
     *
     * @param loreEntry The raw {@link Component} for a lore entry.
     */
    private void createPages(Component loreEntry) {
        List<String> formattedText = wrapText(loreEntry.getString(), 98); // Split entry text into lines that break at a width of 98.
        List<String> firstPage;
        if (formattedText.size() < 6) { // Check if there are less than 6 lines; there can only be 6 lines of text on the first page.
            firstPage = formattedText.subList(0, formattedText.size());
            this.pages.put(0, firstPage); // Set up the first page with text.
        } else { // If there are more than 6 lines.
            firstPage = formattedText.subList(0, 6); // 6 lines for the first page.
            this.pages.put(0, firstPage); // Set up the first page with text.

            List<String> remainingPages = formattedText.subList(6, formattedText.size()); // Gets the text for the remaining pages.

            for (int start = 0, page = 1; start < remainingPages.size(); start += 8, page++) {
                this.pages.put(page, remainingPages.subList(start, Math.min(start + 8, remainingPages.size()))); // Sets up the remaining pages with text.
            }
        }
    }

    /**
     * Draws the given lines of text on a book page.
     *
     * @param guiGraphics          The rendering {@link GuiGraphicsExtractor}.
     * @param lines                The wrapped lines to render.
     * @param x                    The {@link Integer} for the text x-position.
     * @param y                    The {@link Integer} for the text y-position.
     */
    private void createText(GuiGraphicsExtractor guiGraphics, List<String> lines, int x, int y) {
        int length = 0;
        for (String line : lines) {
            this.drawBookText(guiGraphics, this.font, line, x, y + (length * 10));
            length++;
        }
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor guiGraphics, int x, int y, float partialTicks) {
        int xPos = (this.width - this.imageWidth) / 2;
        int yPos = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE_LORE_BACKING, xPos, yPos - 4, 0, 0, this.imageWidth, this.imageHeight + 56, 256, 256); // Draws the grey GUI backing.
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE_LORE_BOOK, xPos + 12, yPos + 2, 0, 0, this.imageWidth, this.imageHeight + 56, 256, 256); // Draws the book GUI on top of backing.
    }

    private void drawNormalBookText(GuiGraphicsExtractor guiGraphics, Font fontRenderer, Component component, int x, int y) {
        this.drawBookText(guiGraphics, fontRenderer, component.getString(), x, y);
    }

    private void drawRightBookText(GuiGraphicsExtractor guiGraphics, Font fontRenderer, Component component, int x, int y) {
        String text = component.getString();
        this.drawBookText(guiGraphics, fontRenderer, text, x - fontRenderer.width(text), y);
    }

    private void drawCenteredBookText(GuiGraphicsExtractor guiGraphics, Font fontRenderer, Component component, int x, int y) {
        String text = component.getString();
        this.drawBookText(guiGraphics, fontRenderer, text, x - fontRenderer.width(text) / 2, y);
    }

    private void drawBookText(GuiGraphicsExtractor guiGraphics, Font fontRenderer, String text, int x, int y) {
        guiGraphics.text(fontRenderer, text, x, y, 4210752, false);
    }

    private List<String> wrapText(String text, int width) {
        List<String> lines = new ArrayList<>();
        for (String paragraph : text.split("\\n", -1)) {
            String remaining = paragraph;
            if (remaining.isEmpty()) {
                lines.add("");
                continue;
            }
            while (!remaining.isEmpty()) {
                String line = this.font.plainSubstrByWidth(remaining, width);
                if (line.isEmpty()) {
                    break;
                }
                lines.add(line);
                remaining = remaining.substring(line.length());
            }
        }
        return lines;
    }

    private void logLoreState(ItemStack itemStack, String entryKey, boolean exists, String resolvedText) {
        String state = LoreBookMenu.describeStack(itemStack) + "|" + entryKey + "|" + exists + "|" + resolvedText;
        if (!state.equals(this.lastLoggedLoreState)) {
            this.lastLoggedLoreState = state;
            Aether.LOGGER.info("Book of Lore screen state: stack={}, key='{}', exists={}, textLength={}, preview='{}'",
                    LoreBookMenu.describeStack(itemStack), entryKey, exists, resolvedText.length(),
                    resolvedText.length() > 80 ? resolvedText.substring(0, 80) + "..." : resolvedText);
        }
    }
}
