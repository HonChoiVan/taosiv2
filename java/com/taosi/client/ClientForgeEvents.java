package com.taosi.client;

import com.taosi.TaosiMod;
import com.taosi.net.KeyPacket;
import com.taosi.net.Net;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TaosiMod.MODID, value = Dist.CLIENT)
public class ClientForgeEvents {
    @SubscribeEvent
    public static void tick(TickEvent.ClientTickEvent e) {
        if (e.phase != TickEvent.Phase.END) return;
        if (Minecraft.getInstance().player == null) return;
        while (ClientKeys.PLACE.consumeClick()) Net.CH.sendToServer(new KeyPacket(0));
        while (ClientKeys.DETONATE.consumeClick()) Net.CH.sendToServer(new KeyPacket(1));
    }
}
