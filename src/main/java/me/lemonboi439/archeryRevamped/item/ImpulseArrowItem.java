package me.lemonboi439.archeryRevamped.item;

import me.lemonboi439.archeryRevamped.arrow.ArrowType;
import net.minecraft.world.item.Item;

public final class ImpulseArrowItem extends SpecialArrowItem {
    public ImpulseArrowItem(Item.Properties settings) {
        super(settings);
    }

    @Override
    protected ArrowType arrowType() {
        return ArrowType.IMPULSE;
    }
}
