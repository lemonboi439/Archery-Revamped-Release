package me.lemonboi439.archeryRevamped.item;

import me.lemonboi439.archeryRevamped.arrow.ArrowType;
import net.minecraft.world.item.Item;

public final class ShockwaveArrowItem extends SpecialArrowItem {
    public ShockwaveArrowItem(Item.Properties settings) {
        super(settings);
    }

    @Override
    protected ArrowType arrowType() {
        return ArrowType.SHOCKWAVE;
    }
}
