package elocindev.tierify.server;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public final class SetBonusTickHandler {
    private static final int BASE_TICK_INTERVAL = 20;
    private static final int FULL_SWEEP_INTERVAL = 200;
    private static final Map<Object, Integer> TICKS =
            Collections.synchronizedMap(new WeakHashMap<>());
    private static final Map<Object, Boolean> LAST_ENABLED =
            Collections.synchronizedMap(new WeakHashMap<>());
    private static final Map<Object, Boolean> DIRTY =
            Collections.synchronizedMap(new WeakHashMap<>());

    private SetBonusTickHandler() {}

    public static <P> void endServerTick(
            Object serverKey,
            Iterable<P> players,
            BooleanSupplier enabled,
            Consumer<P> apply,
            Consumer<P> remove
    ) {
        int tick = TICKS.merge(serverKey, 1, Integer::sum);
        if (tick % BASE_TICK_INTERVAL != 0) return;

        boolean on = enabled.getAsBoolean();
        Boolean last = LAST_ENABLED.put(serverKey, on);
        if (last == null || last != on || tick % FULL_SWEEP_INTERVAL == 0) {
            for (P p : players) {
                if (on) apply.accept(p);
                else remove.accept(p);
                DIRTY.remove(p);
            }
            return;
        }

        if (DIRTY.isEmpty()) return;

        for (P p : players) {
            if (!Boolean.TRUE.equals(DIRTY.remove(p))) continue;
            if (on) apply.accept(p);
            else remove.accept(p);
        }
    }

    public static void markDirty(Object playerKey) {
        if (playerKey == null) return;
        DIRTY.put(playerKey, Boolean.TRUE);
    }

    /** Optional: call on server stopping to reset tick counter early. */
    public static void clearForServer(Object serverKey) {
        TICKS.remove(serverKey);
        LAST_ENABLED.remove(serverKey);
    }
}
