package com.terraformersmc.cinderscapes.util;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.JsonOps;
import com.terraformersmc.cinderscapes.mixin.MultiNoiseBiomeSourceAccessor;
import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.MultiNoiseBiomeSource;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class NoiseCollisionChecker {
    public static void init() {
        check();

        RegistryEntryAddedCallback.event(BuiltinRegistries.BIOME).register(((i, identifier, biome) -> {
            check();
        }));
    }

    private static void check() {
        MultiNoiseBiomeSource biomeSource = MultiNoiseBiomeSource.Preset.NETHER.getBiomeSource(BuiltinRegistries.BIOME, false);

        List<Pair<MultiNoiseUtil.NoiseValuePoint, Supplier<Biome>>> points = ((MultiNoiseBiomeSourceAccessor) biomeSource).getBiomePoints();

        // Build a reverse-map from noise point to Biome
        Map<MultiNoiseUtil.NoiseValuePoint, Biome> noisePoints = new HashMap<>();
        for (Pair<MultiNoiseUtil.NoiseValuePoint, Supplier<Biome>> point : points) {
            Biome biome = point.getSecond().get();
            MultiNoiseUtil.NoiseValuePoint noisePoint = point.getFirst();
            if (biome == null) {
                System.out.println("WARNING: Found null-biome for noise-point " + toString(noisePoint));
                continue;
            }

            Biome prev = noisePoints.put(noisePoint, biome);
            if (prev != null) {
                System.out.println("WARNING: " + biome + " and " + prev + " have the same mixed noise point " + toString(noisePoint) + "! They won't generate properly!!!!");
            }
        }
    }

    private static String toString(MultiNoiseUtil.NoiseValuePoint noisePoint) {
        return MultiNoiseUtil.NoiseValuePoint.CODEC.encodeStart(JsonOps.INSTANCE, noisePoint).get().left().get().toString();
    }

}
