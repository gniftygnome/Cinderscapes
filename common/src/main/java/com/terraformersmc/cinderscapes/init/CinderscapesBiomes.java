package com.terraformersmc.cinderscapes.init;

import com.terraformersmc.cinderscapes.Cinderscapes;
import com.terraformersmc.cinderscapes.biome.AshyShoalsBiome;
import com.terraformersmc.cinderscapes.biome.BlackstoneShalesBiome;
import com.terraformersmc.cinderscapes.biome.LuminousGroveBiome;
import com.terraformersmc.cinderscapes.biome.QuartzCavernBiome;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;

public class CinderscapesBiomes {
    public static final RegistryKey<Biome> ASHY_SHOALS = add("ashy_shoals", AshyShoalsBiome.create());
    public static final RegistryKey<Biome> BLACKSTONE_SHALES = add("blackstone_shales", BlackstoneShalesBiome.create());
    public static final RegistryKey<Biome> LUMINOUS_GROVE = add("luminous_grove", LuminousGroveBiome.create());
    public static final RegistryKey<Biome> QUARTZ_CAVERN = add("quartz_cavern", QuartzCavernBiome.create());

    // Required because something referencing this class has to be called in order to force the
    // static initializer to run (Minecraft's Bootstrap class does similar things)
    public static void init() {
    }

    /**
     * Adds a biome to Cinderscapes local biome registry
     *
     * @param s  A string representing the path of the biome under the cinderscapes namespace
     * @param b  The biome to be added to the local registry
     * @return The biome that was registered with the local biome registry
     */
    private static RegistryKey<Biome> add(String s, Biome b) {
        Identifier id = Cinderscapes.id(s);
        RegistryKey<Biome> key = RegistryKey.of(Registry.BIOME_KEY, id);
        Registry.register(BuiltinRegistries.BIOME, key, b);
        return key;
    }
}
