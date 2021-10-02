package jialo.dev.java.store;

import java.util.LinkedHashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JialoStore {

    // 数据存储：key-URI；value-内容。
    private static Map<String, Object> store = new LinkedHashMap<>();

    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static Map<String, Object> store() {
        return store;
    }

    public static Gson gson() {
        return gson;
    }

}
