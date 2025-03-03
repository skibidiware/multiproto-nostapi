package me.howard.multiprotonostapi.mixin.network.packet.c2s.play;

import me.howard.multiprotonostapi.protocol.ProtocolVersion;
import me.howard.multiprotonostapi.protocol.ProtocolVersionManager;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

@Mixin(PlayerInteractBlockC2SPacket.class)
public class MixinPlayerInteractBlockC2SPacket
{
    @Redirect(method = "read", at = @At(value = "INVOKE", target = "Ljava/io/DataInputStream;readShort()S"), slice = @Slice(from = @At(value = "INVOKE", target = "Ljava/io/DataInputStream;readByte()B")))
    private short redirectReadDamage(DataInputStream stream) throws IOException
    {
        return ProtocolVersionManager.isBefore(ProtocolVersion.BETA_8) ? stream.readByte() : stream.readShort();
    }

    @Redirect(method = "write", at = @At(value = "INVOKE", target = "Ljava/io/DataOutputStream;writeShort(I)V"), slice = @Slice(from = @At(value = "INVOKE", target = "Ljava/io/DataOutputStream;writeByte(I)V")))
    private void redirectWriteDamage(DataOutputStream stream, int i) throws IOException
    {
        if (ProtocolVersionManager.isBefore(ProtocolVersion.BETA_8)) stream.writeByte(i);
        else stream.writeShort(i);
    }

    @Inject(method = "size", at = @At("HEAD"), cancellable = true)
    private void size(CallbackInfoReturnable<Integer> cir)
    {
        if (ProtocolVersionManager.isBefore(ProtocolVersion.BETA_8)) cir.setReturnValue(14);
    }
}
