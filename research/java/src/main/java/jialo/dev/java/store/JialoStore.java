package jialo.dev.java.store;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JialoStore {

    // 数据存储实例
    private static Object store;

    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /**
     * 初始化内存状态
     */
    static {
        try (InputStream oriIn = new FileInputStream(new File("./origin.json"))) {

            String ori = new String(oriIn.readAllBytes());
            System.out.println();
            System.out.println("ori data = ");
            System.out.println(ori);
            System.out.println();
            store = gson.fromJson(ori, Object.class);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-2);
        }
    }

    public static Object store() {
        return store;
    }

    /**
     * 全量更新（内存）数据实例
     * 
     * @param newData 新数据
     */
    public static void store(Object newData) {
        store = newData;
    }

    /**
     * 数据持久化
     */
    public static void flush() {
        try (OutputStream os = new FileOutputStream(new File("./origin.json"))) {
            os.write(gson.toJson(store).getBytes()); // 持久化
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-3);
        }
    }

}
