package elocindev.tierify.forge.mixin.compat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.Coerce;

import elocindev.tierify.util.StarApexUtils;
import net.minecraft.world.item.ItemStack;

import java.awt.Point;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Deque;
import java.util.Random;

@Pseudo
@Mixin(targets = "dev.xylonity.tooltipoverhaul.client.style.effect.StarsEffect", remap = false)
public class TooltipOverhaulStarsEffectMixin {
    private static final long STAR_SPAWN_INTERVAL_MS = 320L;
    private static final int STAR_CAP = 2;
    private static final int STAR_SPAWN_PER_TICK = 2;
    private static final int MIN_LIFE = 520;
    private static final int MAX_LIFE = 1200;
    private static final float MIN_SIZE = 8.0f;
    private static final float MAX_SIZE = 16.0f;
    private static final float STAR_MARGIN = 8.0f;
    private static final int TINT_RGB = 0xF4B13A;
    private static final float TINT_BLEND = 0.9f;

    @Shadow
    private Deque<Object> stars;

    @Shadow
    private int[] colors;

    @Shadow
    private long lastSpawn;

    @Inject(method = "render", at = @At("HEAD"))
    private void tierify$render(@Coerce Object depth,
                                @Coerce Object ctx,
                                @Coerce Object pos,
                                Point size,
                                CallbackInfo ci) {
        if (size == null || pos == null) return;
        int w = size.x;
        int h = size.y;
        if (w <= 2 || h <= 2 || stars == null) return;

        ItemStack stack = getStack(ctx);
        if (stack != null && !stack.isEmpty() && StarApexUtils.isApex(stack)) {
            stars.clear();
            lastSpawn = System.currentTimeMillis();
            return;
        }

        long now = System.currentTimeMillis();
        if (now - lastSpawn < STAR_SPAWN_INTERVAL_MS) return;

        int spawnCount = Math.max(0, Math.min(STAR_SPAWN_PER_TICK, STAR_CAP - stars.size()));
        if (spawnCount == 0) {
            lastSpawn = now;
            return;
        }

        Float fx = readFloat(pos, "field_1343", "x", "getX");
        Float fy = readFloat(pos, "field_1342", "y", "getY");
        if (fx == null || fy == null) return;

        Random random = new Random();
        for (int i = 0; i < spawnCount; i++) {
            spawnStar(fx, fy, w, h, now, random);
        }
        lastSpawn = now;

        while (stars.size() > STAR_CAP) {
            stars.pollFirst();
        }
    }

    private void spawnStar(float x, float y, int w, int h, long now, Random random) {
        float cx = x + STAR_MARGIN + random.nextFloat() * Math.max(1.0f, w - 2.0f * STAR_MARGIN);
        float cy = y + STAR_MARGIN + random.nextFloat() * Math.max(1.0f, h - 2.0f * STAR_MARGIN);
        float rot = random.nextFloat() * (float) Math.PI;
        float size = MIN_SIZE + (MAX_SIZE - MIN_SIZE) * random.nextFloat();
        int life = MIN_LIFE + random.nextInt(Math.max(1, MAX_LIFE - MIN_LIFE + 1));
        int baseColor = (colors != null && colors.length > 0) ? colors[random.nextInt(colors.length)] : 0xFFFFFFFF;
        int color = applyTint(baseColor, TINT_RGB, TINT_BLEND);

        try {
            Class<?> starClass = Class.forName("dev.xylonity.tooltipoverhaul.client.style.effect.StarsEffect$Star");
            Constructor<?> ctor = starClass.getDeclaredConstructor(
                    float.class,
                    float.class,
                    float.class,
                    long.class,
                    int.class,
                    int.class,
                    float.class
            );
            ctor.setAccessible(true);
            Object star = ctor.newInstance(cx, cy, size, now, life, color, rot);
            stars.addLast(star);
        } catch (Throwable ignored) {
        }
    }

    private static Float readFloat(Object obj, String... names) {
        for (String name : names) {
            try {
                Method m = obj.getClass().getMethod(name);
                m.setAccessible(true);
                Object out = m.invoke(obj);
                if (out instanceof Number number) return number.floatValue();
            } catch (Throwable ignored) {
            }
            try {
                Field f = obj.getClass().getField(name);
                f.setAccessible(true);
                Object out = f.get(obj);
                if (out instanceof Number number) return number.floatValue();
            } catch (Throwable ignored) {
            }
        }
        return null;
    }

    private static ItemStack getStack(Object ctx) {
        if (ctx == null) return null;
        try {
            Method m = ctx.getClass().getMethod("stack");
            Object out = m.invoke(ctx);
            if (out instanceof ItemStack stack) return stack;
        } catch (Throwable ignored) {
        }
        try {
            Method m = ctx.getClass().getMethod("getStack");
            Object out = m.invoke(ctx);
            if (out instanceof ItemStack stack) return stack;
        } catch (Throwable ignored) {
        }
        try {
            Field f = ctx.getClass().getDeclaredField("stack");
            f.setAccessible(true);
            Object out = f.get(ctx);
            if (out instanceof ItemStack stack) return stack;
        } catch (Throwable ignored) {
        }
        return null;
    }

    private static int applyTint(int argb, int tintRgb, float blend) {
        float t = Math.max(0.0f, Math.min(1.0f, blend));
        int a = (argb >>> 24) & 0xFF;
        int r = (argb >>> 16) & 0xFF;
        int g = (argb >>> 8) & 0xFF;
        int b = argb & 0xFF;
        int tr = (tintRgb >>> 16) & 0xFF;
        int tg = (tintRgb >>> 8) & 0xFF;
        int tb = tintRgb & 0xFF;
        int nr = Math.round(r * (1.0f - t) + tr * t);
        int ng = Math.round(g * (1.0f - t) + tg * t);
        int nb = Math.round(b * (1.0f - t) + tb * t);
        return (a << 24) | (nr << 16) | (ng << 8) | nb;
    }
}
