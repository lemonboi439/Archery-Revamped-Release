package me.lemonboi439.archeryRevamped.client.render;

import net.minecraft.client.renderer.entity.state.ArrowRenderState;
import net.minecraft.resources.Identifier;

public final class ArcheryArrowEntityRenderState extends ArrowRenderState {
    public Identifier texture;
    public boolean tipped;
    public int potionColor;
    public boolean tidal;
    public float tidalSpin;
}
