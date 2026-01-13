package com.txttext.taczlabs.mixin.crosshair;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.client.event.RenderCrosshairEvent;
import com.tacz.guns.client.resource.index.ClientGunIndex;
import com.txttext.taczlabs.hud.crosshair.Crosshair;
import com.txttext.taczlabs.hud.crosshair.CrosshairType;
import com.txttext.taczlabs.config.fileconfig.HudConfig;
import com.txttext.taczlabs.util.TLUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.txttext.taczlabs.config.fileconfig.HudConfig.*;

@Mixin(RenderCrosshairEvent.class)
public class RenderCrosshairEventMixin {
    @Inject(
            method = "renderCrosshair",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/platform/Window;getGuiScaledWidth()I"),
            //remap = false,
            cancellable = true
    )
    private static void renderTlCrosshair(GuiGraphics graphics, Window window, CallbackInfo ci){

        //确认配置开启
        if(!HudConfig.ENABLE_TL_CROSSHAIR.get()) return;
        //获取GUI信息
        int width = window.getGuiScaledWidth();
        int height = window.getGuiScaledHeight();
        float x = width / 2f;
        float y = height / 2f;
        //获取玩家实例
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) ci.cancel();
        IGunOperator gunOperator = IGunOperator.fromLivingEntity(player);
        if(gunOperator.getSynIsAiming()) ci.cancel();//瞄准时不计算

        //获取玩家手持物品，根据枪的种类判断要渲染的准星
        ItemStack gun = player.getMainHandItem();//获取ItemStack
        //IGun igun = (IGun) gun.getItem();//mixin前的tacz代码就已经检查过，这里转换不再检查是否为枪
        ClientGunIndex gunIndex = TLUtil.getClientGunIndex(gun);
        if (gunIndex == null) ci.cancel();
        @SuppressWarnings("DataFlowIssue")
        String type = gunIndex.getType();//获取枪械类型
        CrosshairType currentType = switch (type){
            case "pistol"-> pistolCrosshair.get();//手枪
            case "smg"-> smgCrosshair.get();//冲锋枪 TODO：做出一个这样的括号准星给冲锋枪：( · )
            //case "rifle"-> rifleCrosshair.get();//步枪
            case "mg"-> mgCrosshair.get();//机枪
            case "shotgun"-> shotgunCrosshair.get();//霰弹枪 TODO：把霰弹枪的圆形准星做进去
            case "sniper"-> sniperCrosshair.get();//狙击枪
            case "rpg"-> rpgCrosshair.get();//重武器
            default-> rifleCrosshair.get();//步枪和未知情况，未知情况按说不可能出现
        };
        if(currentType == CrosshairType.TACZ) return;//原版准星则交回给tacz渲染
        Crosshair.renderCrosshair(graphics, currentType, x, y, gunIndex, player);//渲染准星
        ci.cancel();
    }

    //给命中叉擦屁股，爆头时由于 setShaderColor() 之后没改回来导致我的准星包括所有 HUD 也会一起红
    @Inject(
            method = "renderHitMarker",
            at = @At(value = "TAIL"),
            remap = false
    )
    private static void taczlabs$resetShaderColor(GuiGraphics graphics, Window window, CallbackInfo ci){
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);//渲染完成后重置着色器颜色
    }
}
