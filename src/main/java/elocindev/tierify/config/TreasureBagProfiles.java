public final class TreasureBagProfiles {
    public record Entry(float chance, int[] weights) {}
    public static void reload();
    public static Entry get(net.minecraft.util.Identifier bagItemId);
}
