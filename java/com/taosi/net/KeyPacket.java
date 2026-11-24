package com.taosi.net;

import com.taosi.logic.TalismanEffects;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class KeyPacket {
    public final int action; // 0 = dat bua, 1 = kich no/sam tat ca bua da dat

    public KeyPacket(int action) {
        this.action = action;
    }

    public static void encode(KeyPacket m, FriendlyByteBuf buf) {
        buf.writeByte(m.action);
    }

    public static KeyPacket decode(FriendlyByteBuf buf) {
        return new KeyPacket(buf.readByte());
    }

    public static void handle(KeyPacket m, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context c = ctx.get();
        c.enqueueWork(() -> {
            ServerPlayer p = c.getSender();
            if (p == null) return;
            if (m.action == 0) TalismanEffects.placeHeld(p);
            else TalismanEffects.detonateAll(p);
        });
        c.setPacketHandled(true);
    }
}
