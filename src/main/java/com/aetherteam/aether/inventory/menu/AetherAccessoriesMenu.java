package com.aetherteam.aether.inventory.menu;

import com.aetherteam.aether.inventory.AetherAccessorySlots;
import com.aetherteam.aether.accessories.api.AccessoriesCapability;
import com.aetherteam.aether.accessories.api.menu.AccessoriesBasedSlot;
import com.aetherteam.aether.mixin.mixins.common.accessor.AbstractContainerMenuAccessor;
import com.aetherteam.aether.mixin.mixins.common.accessor.CraftingMenuAccessor;
import com.aetherteam.aether.accessories.api.menu.AccessoriesSlotGenerator;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;

import java.util.Map;

public class AetherAccessoriesMenu extends InventoryMenu {
    private static final int RESULT_SLOT = 0;
    private static final int CRAFTING_SLOT_START = 1;
    private static final int CRAFTING_SLOT_END = CRAFTING_SLOT_START + 4;
    private static final int ACCESSORY_LEFT_X = 77;
    private static final int ACCESSORY_TOP_Y = 8;
    public static final int ACCESSORY_SLOT_BACKGROUND_X = ACCESSORY_LEFT_X;
    public static final int ACCESSORY_SLOT_BACKGROUND_Y = ACCESSORY_TOP_Y + 54;
    public static final int BACK_SLOT_X = 134;
    public static final int BACK_SLOT_Y = ACCESSORY_SLOT_BACKGROUND_Y;

    private static final Map<EquipmentSlot, Identifier> TEXTURE_EMPTY_SLOTS = Map.of(
        EquipmentSlot.FEET,
        InventoryMenu.EMPTY_ARMOR_SLOT_BOOTS,
        EquipmentSlot.LEGS,
        InventoryMenu.EMPTY_ARMOR_SLOT_LEGGINGS,
        EquipmentSlot.CHEST,
        InventoryMenu.EMPTY_ARMOR_SLOT_CHESTPLATE,
        EquipmentSlot.HEAD,
        InventoryMenu.EMPTY_ARMOR_SLOT_HELMET
    );
    private static final EquipmentSlot[] SLOT_IDS = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
    private final CraftingContainer craftSlots = new TransientCraftingContainer(this, 2, 2);
    private final ResultContainer resultSlots = new ResultContainer();
    private final Player owner;
    private final int accessoryStart;
    private final int accessoryEnd;
    private final int armorStart;
    private final int armorEnd;
    private final int inventoryStart;
    private final int hotbarStart;
    private final int hotbarEnd;
    private final int offhandSlot;

    public final boolean hasButton;

    public AetherAccessoriesMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, true);
    }

    public AetherAccessoriesMenu(int containerId, Inventory playerInventory, boolean hasButton) {
        super(playerInventory, playerInventory.player.level().isClientSide(), playerInventory.player);
        this.owner = playerInventory.player;

        this.slots.clear();

        AbstractContainerMenuAccessor abstractContainerMenuAccessor = (AbstractContainerMenuAccessor) this;
        abstractContainerMenuAccessor.aether$setMenuType(AetherMenuTypes.ACCESSORIES);
        abstractContainerMenuAccessor.aether$setContainerId(containerId);
        abstractContainerMenuAccessor.aether$getRemoteSlots().clear();
        abstractContainerMenuAccessor.aether$getLastSlots().clear();

        this.addSlot(new ResultSlot(playerInventory.player, this.craftSlots, this.resultSlots, 0, 154, 28));

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                this.addSlot(new Slot(this.craftSlots, j + i * 2, 116 + j * 18, 18 + i * 18));
            }
        }

        int x = ACCESSORY_LEFT_X, y = ACCESSORY_TOP_Y;

        this.accessoryStart = this.slots.size();
        AccessoriesSlotGenerator.of(this::addSlot, x, y, this.owner, AetherAccessorySlots.getPendantSlotType(), AetherAccessorySlots.getCapeSlotType(), AetherAccessorySlots.getShieldSlotType()).column();
        AccessoriesSlotGenerator.of(this::addSlot, x + 18, y, this.owner, AetherAccessorySlots.getRingSlotType(), AetherAccessorySlots.getGlovesSlotType()).column();
        AccessoriesSlotGenerator.of(this::addSlot, ACCESSORY_SLOT_BACKGROUND_X, ACCESSORY_SLOT_BACKGROUND_Y, this.owner, AetherAccessorySlots.getAccessorySlotType()).row();
        AccessoriesSlotGenerator.of(this::addSlot, BACK_SLOT_X, BACK_SLOT_Y, this.owner, AetherAccessorySlots.getBackSlotType()).row();
        this.accessoryEnd = this.slots.size();

        this.hasButton = hasButton;

        this.armorStart = this.slots.size();
        for (int k = 0; k < 4; k++) {
            EquipmentSlot equipmentslot = SLOT_IDS[k];
            Identifier resourcelocation = TEXTURE_EMPTY_SLOTS.get(equipmentslot);
            this.addSlot(new Slot(playerInventory, 36 + (3 - k), 59, 8 + k * 18) {
                @Override
                public void setByPlayer(ItemStack oldStack, ItemStack newStack) {
                    AetherAccessoriesMenu.this.owner.onEquipItem(equipmentslot, newStack, oldStack);
                    super.setByPlayer(oldStack, newStack);
                }

                @Override
                public int getMaxStackSize() {
                    return 1;
                }

                @Override
                public boolean mayPlace(ItemStack stack) {
                    return AetherAccessoriesMenu.this.owner.getEquipmentSlotForItem(stack) == equipmentslot;
                }

                @Override
                public Identifier getNoItemIcon() {
                    return resourcelocation;
                }
            });
        }
        this.armorEnd = this.slots.size();

        this.inventoryStart = this.slots.size();
        for (int l = 0; l < 3; l++) {
            for (int j1 = 0; j1 < 9; j1++) {
                this.addSlot(new Slot(playerInventory, j1 + (l + 1) * 9, 8 + j1 * 18, 84 + l * 18));
            }
        }

        this.hotbarStart = this.slots.size();
        for (int i1 = 0; i1 < 9; i1++) {
            this.addSlot(new Slot(playerInventory, i1, 8 + i1 * 18, 142));
        }
        this.hotbarEnd = this.slots.size();

        this.offhandSlot = this.slots.size();
        this.addSlot(new Slot(playerInventory, 40, 116, 62) {
            @Override
            public void setByPlayer(ItemStack p_270969_, ItemStack p_299918_) {
                AetherAccessoriesMenu.this.owner.onEquipItem(EquipmentSlot.OFFHAND, p_299918_, p_270969_);
                super.setByPlayer(p_270969_, p_299918_);
            }

            @Override
            public Identifier getNoItemIcon() {
                return InventoryMenu.EMPTY_ARMOR_SLOT_SHIELD;
            }
        });
    }

    /**
     * Callback for when the crafting matrix is changed.
     */
    @Override
    public void slotsChanged(Container inventory) {
        if (this.owner.level() instanceof ServerLevel serverLevel) {
            CraftingMenuAccessor.callSlotChangedCraftingGrid(this, serverLevel, this.owner, this.craftSlots, this.resultSlots, null);
        }
    }

    /**
     * Called when the container is closed.
     */
    @Override
    public void removed(Player player) {
        super.removed(player);
        this.resultSlots.clearContent();
        if (!player.level().isClientSide()) {
            this.clearContainer(player, this.craftSlots);
        }
    }

    /**
     * Determines whether supplied player can use this container
     */
    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    /**
     * Handle when the stack in slot {@code index} is shift-clicked. Normally this moves the stack between the player inventory and the other inventory(s).
     */
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        if (index < 0 || index >= this.slots.size()) {
            return itemStack;
        }

        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack itemStack1 = slot.getItem();
            itemStack = itemStack1.copy();
            EquipmentSlot equipmentSlot = player.getEquipmentSlotForItem(itemStack);
            if (index == RESULT_SLOT) {
                if (!this.moveItemStackToInventory(itemStack1, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(itemStack1, itemStack);
            } else if (this.isCraftingSlot(index) || this.isAccessorySlot(index) || this.isArmorSlot(index) || index == this.offhandSlot) {
                if (!this.moveItemStackToInventory(itemStack1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (this.isPlayerInventorySlot(index)) {
                int accessorySlotIndex = this.getFirstEmptyAccessorySlot(player, itemStack1);
                if (accessorySlotIndex >= 0) {
                    if (!this.moveItemStackTo(itemStack1, accessorySlotIndex, accessorySlotIndex + 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (equipmentSlot.getType() == EquipmentSlot.Type.HUMANOID_ARMOR && this.canMoveToArmorSlot(equipmentSlot)) {
                    int armorSlotIndex = this.getArmorSlotIndex(equipmentSlot);
                    if (!this.moveItemStackTo(itemStack1, armorSlotIndex, armorSlotIndex + 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (equipmentSlot == EquipmentSlot.OFFHAND && !this.slots.get(this.offhandSlot).hasItem()) {
                    if (!this.moveItemStackTo(itemStack1, this.offhandSlot, this.offhandSlot + 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (this.isMainInventorySlot(index)) {
                    if (!this.moveItemStackTo(itemStack1, this.hotbarStart, this.hotbarEnd, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (this.isMenuHotbarSlot(index)) {
                    if (!this.moveItemStackTo(itemStack1, this.inventoryStart, this.hotbarStart, false)) {
                        return ItemStack.EMPTY;
                    }
                } else {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackToInventory(itemStack1, false)) {
                return ItemStack.EMPTY;
            }
            if (itemStack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
            if (itemStack1.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTake(player, itemStack1);
            if (index == 0) {
                player.drop(itemStack1, false);
            }
        }
        return itemStack;
    }

    private boolean moveItemStackToInventory(ItemStack stack, boolean reverseDirection) {
        return this.moveItemStackTo(stack, this.inventoryStart, this.hotbarEnd, reverseDirection);
    }

    private boolean isCraftingSlot(int index) {
        return index >= CRAFTING_SLOT_START && index < CRAFTING_SLOT_END;
    }

    private boolean isAccessorySlot(int index) {
        return index >= this.accessoryStart && index < this.accessoryEnd;
    }

    private boolean isArmorSlot(int index) {
        return index >= this.armorStart && index < this.armorEnd;
    }

    private boolean isPlayerInventorySlot(int index) {
        return index >= this.inventoryStart && index < this.hotbarEnd;
    }

    private boolean isMainInventorySlot(int index) {
        return index >= this.inventoryStart && index < this.hotbarStart;
    }

    private boolean isMenuHotbarSlot(int index) {
        return index >= this.hotbarStart && index < this.hotbarEnd;
    }

    private boolean canMoveToArmorSlot(EquipmentSlot equipmentSlot) {
        int armorSlotIndex = this.getArmorSlotIndex(equipmentSlot);
        return armorSlotIndex >= 0 && !this.slots.get(armorSlotIndex).hasItem();
    }

    private int getArmorSlotIndex(EquipmentSlot equipmentSlot) {
        for (int i = 0; i < SLOT_IDS.length; i++) {
            if (SLOT_IDS[i] == equipmentSlot) {
                return this.armorStart + i;
            }
        }
        return -1;
    }

    private int getFirstEmptyAccessorySlot(Player player, ItemStack stack) {
        AccessoriesCapability accessories = AccessoriesCapability.get(player);
        if (accessories == null) {
            return -1;
        }

        var equipReference = accessories.canEquipAccessory(stack, true);
        if (equipReference == null) {
            return -1;
        }

        return this.getMenuIndexForAccessorySlot(equipReference.first().slotName(), equipReference.first().slot());
    }

    private int getMenuIndexForAccessorySlot(String slotName, int slotIndex) {
        for (int i = this.accessoryStart; i < this.accessoryEnd; i++) {
            Slot slot = this.slots.get(i);
            if (slot instanceof AccessoriesBasedSlot accessorySlot
                    && accessorySlot.slotName().equals(slotName)
                    && accessorySlot.slotIndex() == slotIndex) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Called to determine if the current slot is valid for the stack merging (double-click) code. The stack passed in is null for the initial slot that was double-clicked.
     */
    @Override
    public boolean canTakeItemForPickAll(ItemStack stack, Slot slot) {
        return slot.container != this.resultSlots && super.canTakeItemForPickAll(stack, slot);
    }
}
