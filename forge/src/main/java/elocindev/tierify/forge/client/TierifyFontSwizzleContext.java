package elocindev.tierify.forge.client;

public final class TierifyFontSwizzleContext {
    private TierifyFontSwizzleContext() {}
    public static final ThreadLocal<Boolean> APEX_DRAW = ThreadLocal.withInitial(() -> false);
}
