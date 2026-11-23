package dev.armorhud;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public class ArmorHudClient implements ClientModInitializer {
    private static final EquipmentSlot[] SLOTS = {
        EquipmentSlot.HEAD,
        EquipmentSlot.CHEST,
        EquipmentSlot.LEGS,
        EquipmentSlot.FEET
    };

    @Override
    public void onInitializeClient() {
        HudElementRegistry.attachElementBefore(
            VanillaHudElements.CHAT,
            Identifier.fromNamespaceAndPath("armorhud", "armor_panel"),
            ArmorHudClient::render
        );
    }

    private static void render(GuiGraphics graphics, net.minecraft.client.DeltaTracker tickCounter) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.options.hideGui) return;

        int x = graphics.guiWidth() - 42;
        int y = graphics.guiHeight() / 2 - 42;

        graphics.fill(x - 4, y - 5, x + 38, y + 83, 0x99000000);
        graphics.renderOutline(x - 4, y - 5, 42, 88, 0xFF777777);

        for (int i = 0; i < SLOTS.length; i++) {
            ItemStack stack = minecraft.player.getItemBySlot(SLOTS[i]);
            int slotY = y + i * 21;

            graphics.fill(x, slotY, x + 18, slotY + 18, 0x66555555);

            if (!stack.isEmpty()) {
                graphics.renderItem(stack, x + 1, slotY + 1);

                if (stack.isDamageableItem()) {
                    int max = stack.getMaxDamage();
                    int remaining = Math.max(0, max - stack.getDamageValue());
                    int barWidth = 16;
                    int filled = (int) Math.round(barWidth * (remaining / (double) max));
                    int color = remaining > max * 0.5 ? 0xFF55DD55
                              : remaining > max * 0.2 ? 0xFFFFCC33 : 0xFFFF4444;

                    graphics.fill(x + 1, slotY + 16, x + 17, slotY + 18, 0xFF222222);
                    if (filled > 0) {
                        graphics.fill(x + 1, slotY + 16, x + 1 + filled, slotY + 18, color);
                    }
                }
            } else {
                String mark = switch (SLOTS[i]) {
                    case HEAD -> "H";
                    case CHEST -> "C";
                    case LEGS -> "L";
                    case FEET -> "B";
                    default -> "?";
                };
                graphics.drawString(minecraft.font, mark, x + 6, slotY + 5, 0xFFAAAAAA, false);
            }
        }
    }
}
