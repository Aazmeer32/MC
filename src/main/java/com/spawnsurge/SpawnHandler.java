package com.spawnsurge;

import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.slf4j.Logger;

public class SpawnHandler {
    private static final Logger LOGGER = LogUtils.getLogger();

    /** True while we are creating copies, so copies are never multiplied again. */
    private boolean busy = false;

    @SubscribeEvent
    public void onFinalizeSpawn(MobSpawnEvent.FinalizeSpawn event) {
        if (busy || event.isSpawnCancelled()) return;

        int total = SurgeConfig.MULTIPLIER.get();
        if (total <= 1) return;

        Mob original = event.getEntity();
        EntityType<?> type = original.getType();
        if (!categoryAllowed(type.getCategory())) return;
        if (!SurgeConfig.SPAWN_TYPES.get().contains(event.getSpawnType().name())) return;
        if (isBlacklisted(type)) return;

        ServerLevelAccessor accessor = event.getLevel();
        ServerLevel level = accessor.getLevel();

        double x = event.getX(), y = event.getY(), z = event.getZ();

        int cap = SurgeConfig.NEARBY_CAP.get();
        if (cap > 0) {
            AABB box = new AABB(x - 64, y - 64, z - 64, x + 64, y + 64, z + 64);
            if (level.getEntities(type, box, e -> true).size() >= cap) return;
        }

        busy = true;
        try {
            int radius = SurgeConfig.SPREAD_RADIUS.get();
            RandomSource rng = level.getRandom();
            MobSpawnType reason = event.getSpawnType();

            for (int i = 1; i < total; i++) {
                if (!(type.create(level) instanceof Mob copy)) break;

                placeCopy(level, copy, x, y, z, radius, rng);
                copy.finalizeSpawn(accessor, accessor.getCurrentDifficultyAt(copy.blockPosition()),
                        reason, null, null);
                level.addFreshEntityWithPassengers(copy);
            }
        } catch (Exception ex) {
            LOGGER.error("Failed to create extra mobs for {}", type, ex);
        } finally {
            busy = false;
        }
    }

    private static void placeCopy(ServerLevel level, Mob copy, double x, double y, double z,
                                  int radius, RandomSource rng) {
        float yaw = rng.nextFloat() * 360f;
        // Try a few random spots; keep the first one that is not inside a block.
        for (int attempt = 0; attempt < 6 && radius > 0; attempt++) {
            double px = x + (rng.nextDouble() * 2 - 1) * radius;
            double pz = z + (rng.nextDouble() * 2 - 1) * radius;
            copy.moveTo(px, y, pz, yaw, 0f);
            if (level.hasChunkAt(copy.blockPosition()) && level.noCollision(copy)) return;
        }
        copy.moveTo(x, y, z, yaw, 0f);
    }

    private static boolean categoryAllowed(MobCategory c) {
        return switch (c) {
            case MONSTER -> SurgeConfig.HOSTILE.get();
            case CREATURE, AMBIENT -> SurgeConfig.PASSIVE.get();
            case WATER_CREATURE, WATER_AMBIENT, UNDERGROUND_WATER_CREATURE, AXOLOTLS -> SurgeConfig.WATER.get();
            default -> false;
        };
    }

    private static boolean isBlacklisted(EntityType<?> type) {
        ResourceLocation id = BuiltInRegistries.ENTITY_TYPE.getKey(type);
        String full = id.toString();
        String wildcard = id.getNamespace() + ":*";
        for (String s : SurgeConfig.BLACKLIST.get()) {
            if (s.equals(full) || s.equals(wildcard)) return true;
        }
        return false;
    }
}
