package me.howard.multiprotonostapi.mixin.network.packet.s2c.play;

import me.howard.multiprotonostapi.protocol.ProtocolVersion;
import me.howard.multiprotonostapi.protocol.ProtocolVersionManager;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.DataInputStream;
import java.io.DataOutputStream;

@Mixin(EntitySpawnS2CPacket.class)
public class MixinEntitySpawnS2CPacket
{
    @Inject(method = "read", at = @At(value = "FIELD", target = "Lnet/minecraft/network/packet/s2c/play/EntitySpawnS2CPacket;z:I", shift = At.Shift.AFTER), cancellable = true)
    private void cancelReadEntityData(DataInputStream stream, CallbackInfo ci)
    {
        if (ProtocolVersionManager.isBefore(ProtocolVersion.BETA_13)) ci.cancel();
    }

    @Inject(method = "write", at = @At(value = "FIELD", target = "Lnet/minecraft/network/packet/s2c/play/EntitySpawnS2CPacket;z:I", shift = At.Shift.AFTER), cancellable = true)
    private void cancelWriteEntityData(DataOutputStream stream, CallbackInfo ci)
    {
        if (ProtocolVersionManager.isBefore(ProtocolVersion.BETA_13)) ci.cancel();
    }

    @Inject(method = "size", at = @At("HEAD"), cancellable = true)
    private void size(CallbackInfoReturnable<Integer> cir)
    {
        if (ProtocolVersionManager.isBefore(ProtocolVersion.BETA_13)) cir.setReturnValue(17);
    }
}
