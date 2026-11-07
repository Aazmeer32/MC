package com.spawnsurge;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

public final class SurgeConfig {
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.IntValue MULTIPLIER;
    public static final ForgeConfigSpec.IntValue SPREAD_RADIUS;
    public static final ForgeConfigSpec.IntValue NEARBY_CAP;
    public static final ForgeConfigSpec.BooleanValue HOSTILE;
    public static final ForgeConfigSpec.BooleanValue PASSIVE;
    public static final ForgeConfigSpec.BooleanValue WATER;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> SPAWN_TYPES;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> BLACKLIST;

    static {
        ForgeConfigSpec.Builder b = new ForgeConfigSpec.Builder();

        MULTIPLIER = b.comment("Total mobs per spawn (100 = the original plus 99 copies). 1 disables the mod.")
                .defineInRange("multiplier", 100, 1, 1000);

        SPREAD_RADIUS = b.comment("Copies are placed randomly within this many blocks of the original.",
                        "Keep it big enough: more than 24 mobs in one block trigger maxEntityCramming damage.")
                .defineInRange("spreadRadius", 8, 0, 32);

        NEARBY_CAP = b.comment("Skip multiplying if this many mobs of the same type already exist within 64 blocks.",
                        "Protects against lag. 0 = no cap.")
                .defineInRange("nearbyCap", 600, 0, 100000);

        HOSTILE = b.comment("Multiply hostile mobs (zombies, creepers, modded monsters...)")
                .define("hostile", true);
        PASSIVE = b.comment("Multiply animals and ambient mobs (cows, bats, modded creatures...)")
                .define("passive", true);
        WATER = b.comment("Multiply water mobs (fish, squid, dolphins...)")
                .define("water", true);

        SPAWN_TYPES = b.comment("Which spawn reasons are multiplied. Valid values:",
                        "NATURAL, CHUNK_GENERATION, SPAWNER, STRUCTURE, BREEDING, MOB_SUMMONED, JOCKEY,",
                        "EVENT, CONVERSION, REINFORCEMENT, TRIGGERED, BUCKET, SPAWN_EGG, COMMAND, DISPENSER, PATROL",
                        "CHUNK_GENERATION is off by default because it can make new chunks very slow.")
                .defineList("spawnTypes", List.of("NATURAL", "SPAWNER", "PATROL"), o -> o instanceof String);

        BLACKLIST = b.comment("Entity IDs that are never multiplied. 'modid:*' matches a whole mod.")
                .defineList("blacklist", List.of(
                        "minecraft:ender_dragon", "minecraft:wither", "minecraft:warden",
                        "minecraft:elder_guardian", "minecraft:iron_golem", "minecraft:snow_golem",
                        "minecraft:villager", "minecraft:wandering_trader"
                ), o -> o instanceof String);

        SPEC = b.build();
    }

    private SurgeConfig() {}
}
