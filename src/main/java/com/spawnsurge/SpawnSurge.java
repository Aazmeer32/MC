package com.spawnsurge;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

@Mod(SpawnSurge.MOD_ID)
public class SpawnSurge {
    public static final String MOD_ID = "spawnsurge";

    public SpawnSurge() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, SurgeConfig.SPEC, "spawnsurge-common.toml");
        MinecraftForge.EVENT_BUS.register(new SpawnHandler());
    }
}
