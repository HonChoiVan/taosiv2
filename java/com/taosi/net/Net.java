package com.taosi.net;

import com.taosi.TaosiMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class Net {
    private static final String VERSION = "1";
    public static final SimpleChannel CH = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(TaosiMod.MODID, "main"), () -> VERSION, VERSION::equals, VERSION::equals);

    public static void init() {
        CH.registerMessage(0, KeyPacket.class, KeyPacket::encode, KeyPacket::decode, KeyPacket::handle);
    }
}
