package jialo.dev.java.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import jialo.dev.java.exception.InvalidUriException;

/**
 * Jialuo 工具
 */
public class JialoUtils {

    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /**
     * 
     * 提交数据，数据内容来自data.json
     * 
     * @param uri 数据URI
     * @return 0：更新数据内容；1：创建数据内容。
     * @throws InvalidUriException URI错误时抛出，例如：试图以数组的方式操作一个非数组元素。
     * @throws IOException
     */
    public static int postData(String uri) throws InvalidUriException, IOException {
        int st = -1;

        InputStream oriIn = new FileInputStream(new File("./origin.json"));
        String ori = new String(oriIn.readAllBytes());
        System.out.println("ori=" + ori);
        System.out.println();
        Object oriObj = gson.fromJson(ori, Object.class);
        oriIn.close();

        InputStream dataIn = new FileInputStream(new File("./data.json"));
        String data = new String(dataIn.readAllBytes());
        System.out.println("data=" + data);
        System.out.println();
        Object dataObj = gson.fromJson(data, Object.class);
        dataIn.close();

        // 全量更新
        if (uri == null || uri.length() == 0 || uri.equals("/")) {
            ori = data;
            st = 0;
        }

        // 部分更新
        else {
            // 以 / 开头的URI去掉开头
            if (uri.startsWith("/")) {
                uri = uri.substring(1);
            }

            List<String> parts = Arrays.asList(uri.split("/"));

            Object foundObj = oriObj;

            for (int i = 0; i < parts.size(); i++) {
                String p = parts.get(i);
                int index = -1;
                if (p.endsWith(")")) { // 数组成员
                    index = Integer.parseInt(p.substring(p.lastIndexOf("(") + 1, p.lastIndexOf(")")));
                    p = p.substring(0, p.lastIndexOf("("));
                    System.out.println("p=" + p + "; index=" + index);
                    System.out.println();
                    if (!(foundObj instanceof Map) && !(((Map<String, Object>) foundObj).get(p) instanceof List)) { // 试图以数组方式访问非数组数据
                        throw new InvalidUriException();
                    }
                }
                if (foundObj instanceof Map) { // 有下层节点
                    if (((Map<String, Object>) foundObj).get(p) == null) { // 没找到，新建
                        ((Map<String, Object>) foundObj).put(p, dataObj);
                        break;
                    } else { // 找到
                        if (i == parts.size() - 1) { // 最后一个部分
                            if (index >= 0) {
                                ((List) ((Map<String, Object>) foundObj).get(p)).set(index, dataObj);
                            } else {
                                ((Map<String, Object>) foundObj).put(p, dataObj);
                            }
                            break;
                        } else { // 下一层
                            foundObj = ((Map<String, Object>) foundObj).get(p);
                        }
                    }
                } else { // 没有下层节点，直接替换
                    foundObj = new HashMap<String, Object>();
                    ((Map<String, Object>) foundObj).put(p, foundObj);
                }
            }
        }

        System.out.println("new=" + gson.toJson(oriObj));
        System.out.println();

        // OutputStream os = new FileOutputStream(new File("./origin.json"));
        // os.write(gson.toJson(oriObj).getBytes()); // 持久化
        // os.flush();
        // os.close();

        return st;
    }

    public static void main(String... args) {
    }

}
