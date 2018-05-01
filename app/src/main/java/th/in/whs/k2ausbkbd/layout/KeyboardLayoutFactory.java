package th.in.whs.k2ausbkbd.layout;

import java.util.HashMap;
import java.util.Map;

public class KeyboardLayoutFactory {
    private static KeyboardLayoutFactory instance;
    private Map<String, Class<? extends Layout>> registry = new HashMap<>();

    private KeyboardLayoutFactory(){
        register("QWERTY", QwertyLayout.class);
        register("QWERTZ", QwertzLayout.class);
        register("AZERTY", AzertyLayout.class);
        register("QWERTZ-CZ-LINUX", QwertzLayoutCZLinux.class);
    }

    private void register(String name, Class<? extends Layout> layout) {
        registry.put(name, layout);
    }

    private Layout get(String layout) throws IllegalAccessException, InstantiationException {
        return registry.get(layout).newInstance();
    }

    public static Layout getLayout(String name) throws InstantiationException, IllegalAccessException {
        if(instance == null){
            instance = new KeyboardLayoutFactory();
        }

        return instance.get(name);
    }
}
