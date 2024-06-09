package com.terraformersmc.cinderscapes.mixin;

import com.terraformersmc.cinderscapes.config.CinderscapesConfig;
import com.terraformersmc.cinderscapes.init.CinderscapesBiomes;
import com.terraformersmc.cinderscapes.init.CinderscapesBlocks;
import com.terraformersmc.cinderscapes.tag.CinderscapesBlockTags;
import net.minecraft.block.BlockState;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.function.Supplier;

@Mixin(ServerWorld.class)
abstract class MixinServerWorld extends World {
    private MixinServerWorld(MutableWorldProperties properties, RegistryKey<World> registryRef, DynamicRegistryManager registryManager, RegistryEntry<DimensionType> dimensionEntry, Supplier<Profiler> profiler, boolean isClient, boolean debugWorld, long biomeAccess, int maxChainedNeighborUpdates) {
        super(properties, registryRef, registryManager, dimensionEntry, profiler, isClient, debugWorld, biomeAccess, maxChainedNeighborUpdates);
    }

    /*
     * NOTE: Unlike this mixin, vanilla evaluates the tick only at the surface Y level.
     * This means there is no utility for us in reusing vanilla's calculations.
     */
    @Inject(method="tickIceAndSnow", at = @At(value = "HEAD"), locals = LocalCapture.NO_CAPTURE)
    private void cinderscapes$tickAsh(BlockPos tickPos, CallbackInfo ci) {
        // Ashy shoals only exists in the nether, why do this iteration on any other dimension
        if (!getDimensionEntry().matchesKey(DimensionTypes.THE_NETHER)) return;
        if (CinderscapesConfig.INSTANCE.enableAshFall) {
            BlockPos.Mutable pos = tickPos.mutableCopy();

            for (; pos.getY() < 127; pos.setY(pos.getY() + 1)) {
                BlockPos up = pos.up();
                if (!canPlace(up)) {
                    continue;
                } else if (!this.getBiome(up).matchesKey(CinderscapesBiomes.ASHY_SHOALS)) {
                    continue;
                }
                this.setBlockState(up, CinderscapesBlocks.ASH.getDefaultState());
                break;
            }
        }
    }

    @Unique
    private boolean canPlace(BlockPos pos){
        return this.getBlockState(pos).isAir() &&
                blockAbove(pos).isIn(CinderscapesBlockTags.ASH_PERMEABLE) &&
                CinderscapesBlocks.ASH.getDefaultState().canPlaceAt(this, pos);
    }

    @Unique
    private BlockState blockAbove(BlockPos pos) {
        BlockPos iPos = pos.mutableCopy();

        //up() makes new immutable blockpos per call. This uses the Mutable as a Mutable.
        //noinspection StatementWithEmptyBody
        for (; isAir(iPos) && iPos.getY() < 127; iPos.setY(iPos.getY() + 1));

        return getBlockState(iPos);
    }
}