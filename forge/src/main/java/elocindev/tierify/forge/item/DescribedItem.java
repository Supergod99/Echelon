package elocindev.tierify.forge.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DescribedItem extends Item {
    private final String titleKey;
    private final String descKey;

    public DescribedItem(Properties properties, String titleKey, String descKey) {
        super(properties);
        this.titleKey = titleKey;
        this.descKey = descKey;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable(titleKey).withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.literal(" - ").withStyle(ChatFormatting.GRAY)
                .append(Component.translatable(descKey).withStyle(ChatFormatting.DARK_GRAY)));
    }
}
