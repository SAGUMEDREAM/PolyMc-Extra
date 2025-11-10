package cc.thonly.polymc_extra.entity;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class PacketHandler {
    public static boolean emulateHandleStatus(Entity entity, byte status) {
        var world = entity.level();
        if (entity instanceof Animal && status == 18) {
            for (int i = 0; i < 7; ++i) {
                double d = entity.getRandom().nextGaussian() * 0.02;
                double e = entity.getRandom().nextGaussian() * 0.02;
                double f = entity.getRandom().nextGaussian() * 0.02;
                addParticleClient(world, ParticleTypes.HEART, entity.getRandomX(1.0), entity.getRandomY() + 0.5, entity.getRandomZ(1.0), d, e, f);
            }
            return true;
        }

        if (entity instanceof Mob && status == 20) {
            for (int i = 0; i < 20; ++i) {
                double d = entity.getRandom().nextGaussian() * 0.02;
                double e = entity.getRandom().nextGaussian() * 0.02;
                double f = entity.getRandom().nextGaussian() * 0.02;
                addParticleClient(world, ParticleTypes.POOF, entity.getRandomX(1.0) - d * 10.0, entity.getRandomY() - e * 10.0, entity.getRandomZ(1.0) - f * 10.0, d, e, f);
            }
            return true;
        }

        if (entity instanceof LivingEntity livingEntity) {
            switch (status) {
                case 46:
                    for (int j = 0; j < 128; ++j) {
                        double d = (double) j / 127.0;
                        float f = (livingEntity.getRandom().nextFloat() - 0.5F) * 0.2F;
                        float g = (livingEntity.getRandom().nextFloat() - 0.5F) * 0.2F;
                        float h = (livingEntity.getRandom().nextFloat() - 0.5F) * 0.2F;
                        double e = Mth.lerp(d, livingEntity.xo, livingEntity.getX()) + (livingEntity.getRandom().nextDouble() - 0.5) * (double) livingEntity.getBbWidth() * 2.0;
                        double k = Mth.lerp(d, livingEntity.yo, livingEntity.getY()) + livingEntity.getRandom().nextDouble() * (double) livingEntity.getBbHeight();
                        double l = Mth.lerp(d, livingEntity.zo, livingEntity.getZ()) + (livingEntity.getRandom().nextDouble() - 0.5) * (double) livingEntity.getBbWidth() * 2.0;
                        addParticleClient(world, ParticleTypes.PORTAL, e, k, l, f, g, h);
                    }
                    return true;
                case 47:
                    playEquipmentBreakEffects(livingEntity, livingEntity.getItemBySlot(EquipmentSlot.MAINHAND));
                    return true;
                case 48:
                    playEquipmentBreakEffects(livingEntity, livingEntity.getItemBySlot(EquipmentSlot.OFFHAND));
                    return true;
                case 49:
                    playEquipmentBreakEffects(livingEntity, livingEntity.getItemBySlot(EquipmentSlot.HEAD));
                    return true;
                case 50:
                    playEquipmentBreakEffects(livingEntity, livingEntity.getItemBySlot(EquipmentSlot.CHEST));
                    return true;
                case 51:
                    playEquipmentBreakEffects(livingEntity, livingEntity.getItemBySlot(EquipmentSlot.LEGS));
                    return true;
                case 52:
                    playEquipmentBreakEffects(livingEntity, livingEntity.getItemBySlot(EquipmentSlot.FEET));
                    return true;
                case 54:
                    BlockState blockState = Blocks.HONEY_BLOCK.defaultBlockState();

                    for (int i = 0; i < 10; ++i) {
                        addParticleClient(world, new BlockParticleOption(ParticleTypes.BLOCK, blockState), entity.getX(), entity.getY(), entity.getZ(), 0.0, 0.0, 0.0);
                    }
                    return true;
                case 60:
                    for (int i = 0; i < 20; ++i) {
                        double d = entity.getRandom().nextGaussian() * 0.02;
                        double e = entity.getRandom().nextGaussian() * 0.02;
                        double f = entity.getRandom().nextGaussian() * 0.02;
                        double g = 10.0;
                        addParticleClient(world, ParticleTypes.POOF, entity.getRandomX(1.0) - d * 10.0, entity.getRandomY() - e * 10.0, entity.getRandomZ(1.0) - f * 10.0, d, e, f);
                    }
                    return true;
                case 65:
                    playEquipmentBreakEffects(livingEntity, livingEntity.getItemBySlot(EquipmentSlot.BODY));
                    return true;
                case 67:
                    Vec3 vec3d = entity.getDeltaMovement();

                    for (int i = 0; i < 8; ++i) {
                        double d = entity.getRandom().triangle(0.0, 1.0);
                        double e = entity.getRandom().triangle(0.0, 1.0);
                        double f = entity.getRandom().triangle(0.0, 1.0);
                        addParticleClient(world, ParticleTypes.BUBBLE, entity.getX() + d, entity.getY() + e, entity.getZ() + f, vec3d.x, vec3d.y, vec3d.z);
                    }
                    return true;
                case 68:
                    playEquipmentBreakEffects(livingEntity, livingEntity.getItemBySlot(EquipmentSlot.SADDLE));
                    return true;
            }
        }

        if (status == 53) {
            BlockState blockState = Blocks.HONEY_BLOCK.defaultBlockState();

            for (int i = 0; i < 5; ++i) {
                addParticleClient(world, new BlockParticleOption(ParticleTypes.BLOCK, blockState), entity.getX(), entity.getY(), entity.getZ(), 0.0, 0.0, 0.0);
            }
            return true;
        }
        return false;
    }

    private static void addParticleClient(Level world, ParticleOptions parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        ((ServerLevel) world).sendParticles(parameters, x, y, z, 0, velocityX, velocityY, velocityZ, 1);
    }

    private static void playEquipmentBreakEffects(LivingEntity entity, ItemStack stack) {
        if (!stack.isEmpty()) {
            Holder<SoundEvent> registryEntry = stack.get(DataComponents.BREAK_SOUND);
            if (registryEntry != null && !entity.isSilent()) {
                entity.level().playSound(entity, entity.getX(), entity.getY(), entity.getZ(), registryEntry.value(), entity.getSoundSource(), 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F);
            }

            spawnItemParticles(entity, stack, 5);
        }
    }

    private static void spawnItemParticles(LivingEntity entity, ItemStack stack, int count) {
        for (int i = 0; i < count; ++i) {
            Vec3 vec3d = new Vec3(((double) entity.getRandom().nextFloat() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, 0.0);
            vec3d = vec3d.xRot(-entity.getXRot() * 0.017453292F);
            vec3d = vec3d.yRot(-entity.getYRot() * 0.017453292F);
            double d = (double) (-entity.getRandom().nextFloat()) * 0.6 - 0.3;
            Vec3 vec3d2 = new Vec3(((double) entity.getRandom().nextFloat() - 0.5) * 0.3, d, 0.6);
            vec3d2 = vec3d2.xRot(-entity.getXRot() * 0.017453292F);
            vec3d2 = vec3d2.yRot(-entity.getYRot() * 0.017453292F);
            vec3d2 = vec3d2.add(entity.getX(), entity.getEyeY(), entity.getZ());
            addParticleClient(entity.level(), new ItemParticleOption(ParticleTypes.ITEM, stack), vec3d2.x, vec3d2.y, vec3d2.z, vec3d.x, vec3d.y + 0.05, vec3d.z);
        }

    }
}
