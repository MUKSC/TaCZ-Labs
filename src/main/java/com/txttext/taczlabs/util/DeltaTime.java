package com.txttext.taczlabs.util;

/// 一个时间间隔的记录器
public class DeltaTime {
    private float DeltaDec;
    private long lastTime;//上一次更新时记录的时间

    /// 初始化时记录第一次时间
    public DeltaTime(){
        lastTime = System.nanoTime();
    }

    /// 计算真实时间间隔（秒）
    public void updateTime(){

        long now = System.nanoTime();
        DeltaDec = (now - lastTime) / 1_000_000_000f;
        lastTime = now;
    }

    /// 更新时间 + 返回 DeltaDec
    public float updateTimeAndGetDeltaSec(){
        this.updateTime();
        return DeltaDec;
    }

    public float getDeltaDec() {
        return DeltaDec;
    }

    public long getLastTime() {
        return lastTime;
    }
}
