package xyz.nucleoid.extras.lobby.tree;

import java.util.UUID;

import com.mojang.authlib.GameProfile;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import xyz.nucleoid.extras.util.ExtraCodecs;

public record Ornament(Item item, Vec3d offset, float yaw, float hookYaw, UUID owner) {
    private static final Text UNKNOWN_OWNER = Text.translatable("commands.banlist.entry.unknown");

    public static final Codec<Ornament> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
                Registries.ITEM.getCodec().fieldOf("item").forGetter(Ornament::item),
                Vec3d.CODEC.fieldOf("offset").forGetter(Ornament::offset),
                Codec.FLOAT.fieldOf("yaw").forGetter(Ornament::yaw),
                Codec.FLOAT.fieldOf("hook_yaw").forGetter(Ornament::hookYaw),
                ExtraCodecs.STRING_UUID.fieldOf("owner").forGetter(Ornament::owner)
        ).apply(instance, Ornament::new)
    );

    public Text getOwnerName(MinecraftServer server) {
        return server.getUserCache()
                .getByUuid(owner)
                .map(GameProfile::getName)
                .<Text>map(Text::literal)
                .orElse(UNKNOWN_OWNER);
    }

    public boolean canBeRemovedBy(ServerPlayerEntity player) {
        return player.getUuid().equals(this.owner) || player.isCreative();
    }
}
