package me.lemonboi439.archeryRevamped.item;

import me.lemonboi439.archeryRevamped.arrow.ArrowType;
import net.minecraft.item.Item;

public final class ShockwaveArrowItem extends SpecialArrowItem {
    public ShockwaveArrowItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    protected ArrowType arrowType() {
        return ArrowType.SHOCKWAVE;
    }
}
