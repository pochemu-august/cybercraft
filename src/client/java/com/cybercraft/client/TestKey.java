public class TestKey {
    public static void test() {
        try {
            Class.forName("net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper");
            System.out.println("KeyMappingHelper found!");
        } catch (Exception e) {}
    }
}
