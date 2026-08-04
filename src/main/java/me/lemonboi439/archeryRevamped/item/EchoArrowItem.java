package me.lemonboi439.archeryRevamped.item;

import me.lemonboi439.archeryRevamped.arrow.ArrowType;
import net.minecraft.item.Item;

public final class EchoArrowItem extends SpecialArrowItem {
    public EchoArrowItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    protected ArrowType arrowType() {
        return ArrowType.ECHO;
    }
}
