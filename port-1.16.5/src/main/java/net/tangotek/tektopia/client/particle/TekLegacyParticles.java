package net.tangotek.tektopia.client.particle;

import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.client.world.ClientWorld;

public final class TekLegacyParticles {
    private TekLegacyParticles() {
    }

    public static Particle thought(
            ClientWorld level,
            double x,
            double y,
            double z,
            double xSpeed,
            double ySpeed,
            double zSpeed,
            IAnimatedSprite sprites
    ) {
        return new ThoughtParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, sprites, 40, 0.3F);
    }

    public static Particle itemThought(
            ClientWorld level,
            double x,
            double y,
            double z,
            double xSpeed,
            double ySpeed,
            double zSpeed,
            IAnimatedSprite sprites
    ) {
        return new ThoughtParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, sprites, 40, 0.24F);
    }

    public static Particle darkness(
            ClientWorld level,
            double x,
            double y,
            double z,
            double xSpeed,
            double ySpeed,
            double zSpeed,
            IAnimatedSprite sprites
    ) {
        return new SwirlParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, sprites, 34, 0.18F, true);
    }

    public static Particle skull(
            ClientWorld level,
            double x,
            double y,
            double z,
            double xSpeed,
            double ySpeed,
            double zSpeed,
            IAnimatedSprite sprites
    ) {
        return new SwirlParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, sprites, 28, 0.2F, false);
    }

    private static class ThoughtParticle extends SpriteTexturedParticle {
        private ThoughtParticle(
                ClientWorld level,
                double x,
                double y,
                double z,
                double xSpeed,
                double ySpeed,
                double zSpeed,
                IAnimatedSprite sprites,
                int lifetime,
                float size
        ) {
            super(level, x, y, z, xSpeed, ySpeed, zSpeed);
            this.pickSprite(sprites);
            this.hasPhysics = false;
            this.lifetime = lifetime;
            this.quadSize = size;
            this.xd = xSpeed;
            this.yd = ySpeed == 0.0D ? 0.012D : ySpeed;
            this.zd = zSpeed;
        }

        @Override
        public void tick() {
            super.tick();
            this.alpha = Math.max(0.0F, 1.0F - (float) this.age / (float) this.lifetime);
        }

        @Override
        public IParticleRenderType getRenderType() {
            return IParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
        }

        @Override
        protected int getLightColor(float partialTick) {
            return 15728880;
        }

        @Override
        public boolean shouldCull() {
            return false;
        }
    }

    private static final class SwirlParticle extends SpriteTexturedParticle {
        private final double originX;
        private final double originZ;
        private final double motionY;
        private final double radiusGrow;
        private final double torque;
        private double radius;
        private double angle;

        private SwirlParticle(
                ClientWorld level,
                double x,
                double y,
                double z,
                double xSpeed,
                double ySpeed,
                double zSpeed,
                IAnimatedSprite sprites,
                int lifetime,
                float size,
                boolean dark
        ) {
            super(level, x, y, z, 0.0D, 0.0D, 0.0D);
            this.pickSprite(sprites);
            this.hasPhysics = false;
            this.lifetime = lifetime;
            this.quadSize = size + this.random.nextFloat() * 0.08F;
            this.originX = x;
            this.originZ = z;
            this.motionY = ySpeed == 0.0D ? 0.018D : ySpeed;
            this.radiusGrow = xSpeed == 0.0D ? 0.012D : xSpeed;
            this.torque = zSpeed == 0.0D ? 0.22D : zSpeed;
            this.radius = this.random.nextDouble() * 0.12D;
            this.angle = this.random.nextDouble() * Math.PI * 2.0D;
            float shade = dark ? this.random.nextFloat() * 0.35F + 0.25F : this.random.nextFloat() * 0.4F + 0.6F;
            this.rCol = shade;
            this.gCol = shade;
            this.bCol = shade;
        }

        @Override
        public void tick() {
            this.xo = this.x;
            this.yo = this.y;
            this.zo = this.z;
            if (this.age++ >= this.lifetime) {
                this.remove();
                return;
            }
            this.radius += this.radiusGrow;
            this.angle += this.torque;
            this.setPos(this.originX + Math.cos(this.angle) * this.radius, this.y + this.motionY, this.originZ + Math.sin(this.angle) * this.radius);
            this.alpha = Math.max(0.0F, 1.0F - (float) this.age / (float) this.lifetime);
        }

        @Override
        public IParticleRenderType getRenderType() {
            return IParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
        }

        @Override
        protected int getLightColor(float partialTick) {
            return 15728880;
        }

        @Override
        public boolean shouldCull() {
            return false;
        }
    }
}
