package com.txttext.taczlabs.event.shoot;

import com.tacz.guns.api.event.common.GunFireEvent;
import com.tacz.guns.resource.pojo.data.gun.GunData;
import com.tacz.guns.resource.pojo.data.gun.GunRecoil;
import com.tacz.guns.resource.pojo.data.gun.GunRecoilKeyFrame;
import com.txttext.taczlabs.config.fileconfig.HudConfig;
import com.txttext.taczlabs.util.DeltaTime;
import com.txttext.taczlabs.util.TLUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class PlayerFireHandler {
    private static float fireSpread;
    private static final DeltaTime deltaTime = new DeltaTime();

    public static void register() {
        MinecraftForge.EVENT_BUS.register(new PlayerFireHandler());
    }

    @SuppressWarnings("DataFlowIssue")//忽略getRecoil()的npe检测，TLUtil.getGunData()返回null的原因是手上非枪，而这个事件获取到的一定是枪
    @SubscribeEvent
    public void onPlayerFire(GunFireEvent event){
        //这个事件在所有玩家开枪时触发，因此须区分开枪者是否为本地玩家，只响应自己的事件避免多人游戏中同步到其他人的数据
        if(!event.getLogicalSide().isClient()) return;
        LocalPlayer player = Minecraft.getInstance().player;
        if(event.getShooter() != player) return;

        //获取枪械后坐力
        GunData gunData = TLUtil.getGunData(event.getGunItemStack());
        GunRecoil recoil = gunData.getRecoil();
        float kick = getRecoilKick(recoil, 1f);

        //加固定值，差值动画负责平滑过渡
        PlayerFireHandler.fireSpread += Math.max(kick, 1f) * HudConfig.shootingSpread.get();//有一个保底值，不然都看不出来动了
    }

    public static float getRecoilKick(GunRecoil recoil, float modifier) {
        GunRecoilKeyFrame[] frames = recoil.getPitch();
        float maxKick = 0f;
        if (frames != null) {
            for (GunRecoilKeyFrame frame : frames) {
                float[] v = frame.getValue();
                if (v != null && v.length == 2) {
                    maxKick = Math.max(maxKick, Math.max(v[0], v[1]) * modifier);
                }
            }
        }
        return maxKick;
    }

    public static float getFireSpread(){
        return fireSpread;
    }

    public static void setFireSpread(float spread){
        fireSpread = spread;
    }
//        // 获取 keyframe（用来取第一个时间点的实际后坐力）
//        GunRecoilKeyFrame[] pitchFrames = recoil.getPitch();
//        if (pitchFrames == null || pitchFrames.length == 0) return;
//
//        float[] pitchRange = pitchFrames[0].getValue(); // value 是 float[2]，表示最小值和最大值
//        float pitch = (float) (pitchRange[0] + Math.random() * (pitchRange[1] - pitchRange[0]));
//
//        // 简单缩放，映射为 fireSpread
//        float spreadBoost = pitch * 0.4f; // 自定义比例，0.4f 可根据实际感觉调节
//
//        // 添加到准星扩张值中（你已经在渲染函数中平滑处理 fireSpread）
//        PlayerFireHandler.fireSpread += spreadBoost;
}
