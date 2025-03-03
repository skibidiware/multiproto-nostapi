package me.howard.multiprotonostapi.mixin.network.packet.login;

import me.howard.multiprotonostapi.MultiprotoNoStAPI;
import me.howard.multiprotonostapi.protocol.ProtocolVersion;
import me.howard.multiprotonostapi.protocol.ProtocolVersionManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.login.LoginHelloPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

@Mixin(LoginHelloPacket.class)
public abstract class MixinLoginHelloPacket extends Packet
{
    @Shadow public String username;
    @Shadow public int protocolVersion;

    @Unique public String password = "Password";

    @Redirect(method = "write", at = @At(value = "FIELD", target = "Lnet/minecraft/network/packet/login/LoginHelloPacket;protocolVersion:I"))
    private int redirectProtocolVersion(LoginHelloPacket instance)
    {
        return protocolVersion = ProtocolVersionManager.version().version;
    }

    @Inject(method = "read", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/packet/login/LoginHelloPacket;readString(Ljava/io/DataInputStream;I)Ljava/lang/String;", shift = At.Shift.AFTER))
    private void injectReadPassword(DataInputStream stream, CallbackInfo ci) throws IOException
    {
        if (ProtocolVersionManager.isBefore(ProtocolVersion.BETA_11)) password = stream.readUTF();
    }

    @Inject(method = "write", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/packet/login/LoginHelloPacket;writeString(Ljava/lang/String;Ljava/io/DataOutputStream;)V", shift = At.Shift.AFTER))
    private void injectWritePassword(DataOutputStream stream, CallbackInfo ci)
    {
        if (ProtocolVersionManager.isBefore(ProtocolVersion.BETA_11)) writeString(password, stream);
    }

    @Inject(method = "write", at = @At("TAIL"))
    private void injectLog(DataOutputStream stream, CallbackInfo ci)
    {
        MultiprotoNoStAPI.LOGGER.info("Logging in as {} with protocol version {}", username, protocolVersion);
    }

    @Inject(method = "size", at = @At("HEAD"), cancellable = true)
    private void size(CallbackInfoReturnable<Integer> cir)
    {
        if (ProtocolVersionManager.isBefore(ProtocolVersion.BETA_11))
        {
            cir.setReturnValue(4 + username.length() + password.length() + 4 + 5);
        }
    }
}
