package com.darksune.althera.common.academy;

import com.darksune.althera.common.attachment.AltheraAttachments;
import com.darksune.althera.common.attachment.SyncableAttachment;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class AcademyData extends SyncableAttachment {

    public record ReturnPoint(ResourceKey<Level> dimension, Vec3 position, float yaw) {

        public static final Codec<ReturnPoint> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Level.RESOURCE_KEY_CODEC.fieldOf("dimension").forGetter(ReturnPoint::dimension),
                Vec3.CODEC.fieldOf("position").forGetter(ReturnPoint::position),
                Codec.FLOAT.optionalFieldOf("yaw", 0.0F).forGetter(ReturnPoint::yaw)
        ).apply(instance, ReturnPoint::new));
    }

    private boolean invited;

    private boolean firstJoinHandled;

    @Nullable
    private ReturnPoint returnPoint;

    public static final Codec<AcademyData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("invited", false).forGetter(data -> data.invited),
            Codec.BOOL.optionalFieldOf("firstJoinHandled", false).forGetter(data -> data.firstJoinHandled),
            ReturnPoint.CODEC.optionalFieldOf("returnPoint").forGetter(data -> Optional.ofNullable(data.returnPoint))
    ).apply(instance, (invited, firstJoinHandled, returnPoint) -> {
        AcademyData data = new AcademyData();
        data.invited = invited;
        data.firstJoinHandled = firstJoinHandled;
        data.returnPoint = returnPoint.orElse(null);
        return data;
    }));

    public static final StreamCodec<FriendlyByteBuf, AcademyData> STREAM_CODEC = StreamCodec.of(
            (buf, data) -> buf.writeBoolean(data.invited),
            buf -> {
                AcademyData data = new AcademyData();
                data.invited = buf.readBoolean();
                return data;
            });

    public static AcademyData get(Player player) {
        return player.getData(AltheraAttachments.ACADEMY.get());
    }

    public boolean isInvited() {
        return invited;
    }

    public void setInvited(boolean invited) {
        this.invited = invited;
        markDirty();
    }

    public boolean handleFirstJoin() {

        if (firstJoinHandled) {
            return false;
        }

        firstJoinHandled = true;
        return true;
    }

    @Nullable
    public ReturnPoint getReturnPoint() {
        return returnPoint;
    }

    public void setReturnPoint(@Nullable ReturnPoint returnPoint) {
        this.returnPoint = returnPoint;
    }

    @Override
    public void sync(ServerPlayer player) {
        player.setData(AltheraAttachments.ACADEMY.get(), this);
        clearDirty();
    }
}
