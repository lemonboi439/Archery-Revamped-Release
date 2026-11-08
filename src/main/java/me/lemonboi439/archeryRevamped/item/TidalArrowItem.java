package me.lemonboi439.archeryRevamped.item;

import me.lemonboi439.archeryRevamped.arrow.ArrowType;
import net.minecraft.world.item.Item;

public final class TidalArrowItem extends SpecialArrowItem {
    public TidalArrowItem(Item.Properties settings) {
        super(settings);
    }

    @Override
    protected ArrowType arrowType() {
        return ArrowType.TIDAL;
    }
}
