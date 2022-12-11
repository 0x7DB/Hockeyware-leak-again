package org.hockey.hockeyware.client.events.player;

import net.minecraft.entity.Entity;
import net.minecraft.client.model.ModelBase;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class EventModelPlayerRender extends Event
{
    public ModelBase modelBase;
    public Entity entity;
    public float limbSwing;
    public float limbSwingAmount;
    public float ageInTicks;
    public float netHeadYaw;
    public float headPitch;
    public float scaleFactor;
    public int type;

    public EventModelPlayerRender(final ModelBase modelBaseIn, final Entity entityIn, final float limbSwingIn, final float limbSwingAmountIn, final float ageInTicksIn, final float netHeadYawIn, final float headPitchIn, final float scaleFactorIn, final int type) {
        this.modelBase = modelBaseIn;
        this.entity = entityIn;
        this.limbSwing = limbSwingIn;
        this.limbSwingAmount = limbSwingAmountIn;
        this.ageInTicks = ageInTicksIn;
        this.netHeadYaw = netHeadYawIn;
        this.headPitch = headPitchIn;
        this.scaleFactor = scaleFactorIn;
        this.type = type;
    }
}
