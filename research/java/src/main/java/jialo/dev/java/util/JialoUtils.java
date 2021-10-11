package jialo.dev.java.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import jialo.dev.java.store.JialoStore;

/**
 * Jialuo 工具
 */
public class JialoUtils {

    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /**
     * 标准化URI，去掉开头结尾的“/”
     * 
     * @param uri
     * @return
     */
    private static String purgeUri(String uri) {
        // 以 / 开头的URI去掉开头
        if (uri.startsWith("/")) {
            uri = uri.substring(1);
        }

        // 以 / 结尾的URI去掉结尾
        if (uri.endsWith("/")) {
            uri = uri.substring(0, uri.length() - 1);
        }

        return uri;
    }

    /**
     * 合并（新增或更新）数据<br>
     * 当处理数组时，如果指定的索引越界，则自动扩展数组，直到指定的索引为扩展后的数组的最后一个元素，扩展的元素用空Map填充。
     * 
     * @param uri  数据URI
     * @param data 待合并的数据
     * @param save 是否持久化数据。
     * @return 0：更新数据内容；1：创建数据内容。
     */
    public static int merge(String uri, String data, boolean save) {
        System.out.println();
        System.out.println("merge uri = " + uri);
        System.out.println();

        int st = -1; // 返回值

        Object dataObj = gson.fromJson(data, Object.class);

        if (dataObj == null) {
            dataObj = new HashMap<String, Object>(); // 空对象
        }

        // 全量更新
        if (uri == null || uri.length() == 0 || uri.equals("/")) {
            JialoStore.store(dataObj);
            st = 0;
        }

        // 部分更新
        else {
            uri = purgeUri(uri);

            List<String> parts = Arrays.asList(uri.split("/"));

            Object currentObj = JialoStore.store(); // 当前处理的数据
            Object parentObj = null; // 上级数据初始为null，因为当前数据初始状态为根。
            String parentKey = null; // 上级KEY初始为null，因为当前数据初始状态为根。
            int parentIndex = -1; // 当上级数据是数组时，保存上级数据在数组中的索引。

            for (int i = 0; i < parts.size(); i++) {
                String p = parts.get(i);
                boolean array = false; // 标识下一步处理的数据是否为数组
                int index = -1; // 如果用户访问了数组，并指定了索引，此变量会设置为索引值（>=0）
                boolean transToMap = false; // 标识是否将当前数据转化为Map（在原来并不是Map的情况下）

                // 修复数据：当前数据非Map：先将当前数据转换为Map（原数据以Key为“____”的形式作为Map成员
                // 需要考虑当前数据是数组成员的情况，这时应该判断路径对应的数组成员的数据类型是否为Map
                if (!(currentObj instanceof Map)) {
                    var map = new HashMap<String, Object>(); // 新建的MAP
                    map.put("____", currentObj); // 将原数据添加为新建的MAP的key为“____”的成员
                    if (parentIndex >= 0) { // 使用新建的对象代替原数据
                        ((List) (((Map<String, Object>) parentObj).get(parentKey))).set(parentIndex, map);
                    } else {
                        ((Map<String, Object>) parentObj).put(parentKey, map); // 使用新建的对象代替原数据
                    }
                    transToMap = true;
                    currentObj = map; // 更新currentObj引用
                }

                // 数组
                if (p.endsWith(")")) { // 数组成员
                    array = true;
                    String str = p.substring(p.lastIndexOf("(") + 1, p.lastIndexOf(")"));
                    if (str.length() > 0) {
                        index = Integer.parseInt(str);
                    }
                    p = p.substring(0, p.lastIndexOf("(")); // 去掉数组标识

                    // 修复数据：指定处理的数据非数组，当以数组方式访问非数组数据时，新建一个数组，把原对象添加为索引0的成员。
                    var tmpData = ((Map<String, Object>) currentObj).get(p); // 原数据
                    if (!(tmpData instanceof List)) {
                        var list = new ArrayList<>(); // 新建的数组
                        if (!transToMap) { // 没有对当前数据转化为Map，这时才添加原数据到数组（否则没有原数据）
                            list.add(tmpData); // 将原数据添加为新建的数组的第一个元素
                        }
                        ((Map<String, Object>) currentObj).put(p, list); // 使用新建的数组代替原数据
                    }
                }

                if (((Map<String, Object>) currentObj).get(p) == null) { // 没找到，新建路径
                    if (i == parts.size() - 1) { // 最后一个部分
                        if (array) {
                            var tempArray = new ArrayList<Object>();
                            if (index >= 0) { // 在数组的指定索引处添加元素
                                int addCount = index + 1; // 扩展元素数量
                                for (int t = 0; t < addCount; t++) {
                                    tempArray.add(new HashMap<String, Object>()); // 填充空Map
                                }
                                tempArray.set(index, dataObj);
                            } else { // 在数组尾部追加元素
                                tempArray.add(dataObj);
                            }
                            ((Map<String, Object>) currentObj).put(p, tempArray);
                        } else {
                            ((Map<String, Object>) currentObj).put(p, dataObj);
                        }
                    } else {
                        if (array) { // 数组
                            var tempArray = new ArrayList<Object>();
                            if (index >= 0) { // 在数组的指定索引处添加元素
                                int addCount = index - tempArray.size() + 1; // 扩展元素数量
                                for (int t = 0; t < addCount; t++) {
                                    tempArray.add(new HashMap<String, Object>()); // 填充空Map
                                }
                            } else { // 在数组尾部追加元素
                                tempArray.add(new HashMap<String, Object>());
                            }
                            ((Map<String, Object>) currentObj).put(p, tempArray);
                        } else {
                            ((Map<String, Object>) currentObj).put(p, new HashMap<String, Object>());
                        }
                    }
                    parentObj = currentObj; // 保留父对象
                    parentKey = p; // 保留父key
                    if (array) { // 数组
                        var list = ((List) ((Map<String, Object>) currentObj).get(p));
                        if (index >= 0) {
                            currentObj = list.get(index);
                            parentIndex = index;
                        } else {
                            currentObj = list.get(0);
                            parentIndex = 0;
                        }
                    } else {
                        currentObj = ((Map<String, Object>) currentObj).get(p);
                    }
                } else { // 找到
                    if (i == parts.size() - 1) { // 最后一个部分
                        if (array) {
                            var list = ((List) ((Map<String, Object>) currentObj).get(p));
                            if (index >= 0) { // 在指定位置添加
                                if (index >= list.size()) {
                                    int addCount = index - list.size() + 1; // 扩展元素数量
                                    for (int t = 0; t < addCount; t++) {
                                        list.add(new HashMap<String, Object>()); // 填充空Map
                                    }
                                }
                                list.set(index, dataObj);
                            } else { // 在数组尾部追加元素
                                list.add(dataObj);
                            }
                        } else {
                            ((Map<String, Object>) currentObj).put(p, dataObj);
                        }

                    } else { // 下一层
                        parentObj = currentObj; // 保留父对象
                        parentKey = p; // 保留父key
                        if (array) { // 数组
                            var list = ((List) ((Map<String, Object>) currentObj).get(p));
                            if (index >= 0) {
                                if (index >= list.size()) {
                                    int addCount = index - list.size() + 1; // 扩展元素数量
                                    for (int t = 0; t < addCount; t++) {
                                        list.add(new HashMap<String, Object>()); // 填充空Map
                                    }
                                }
                                currentObj = list.get(index);
                                parentIndex = index;
                            } else { // 添加到list结尾
                                list.add(new HashMap<String, Object>());
                                currentObj = list.get(list.size() - 1);
                                parentIndex = list.size() - 1;
                            }
                        } else { // 对象
                            currentObj = ((Map<String, Object>) currentObj).get(p);
                        }
                    }
                }

            }
        }

        System.out.println("new data = ");
        System.out.println(gson.toJson(JialoStore.store()));
        System.out.println();

        // 数据持久化
        if (save) {
            JialoStore.flush();
        }

        return st;
    }

    /**
     * 查询指定数据
     * 
     * @param uri 待查询数据的路径
     * @return 指定路径的数据，未找到时为null。
     */
    public static Object query(String uri) {

        // 全量查询
        if (uri == null || uri.length() == 0 || uri.equals("/")) {
            return JialoStore.store();
        }

        // 以 / 开头的URI去掉开头
        if (uri.startsWith("/")) {
            uri = uri.substring(1);
        }

        // 以 / 结尾的URI去掉结尾
        if (uri.endsWith("/")) {
            uri = uri.substring(0, uri.length() - 1);
        }

        List<String> parts = Arrays.asList(uri.split("/"));

        Object currentObj = JialoStore.store();

        for (int i = 0; i < parts.size(); i++) {
            String p = parts.get(i);
            int index = -1;
            boolean array = false;
            if (p.endsWith(")")) { // 数组成员
                array = true;
                String str = p.substring(p.lastIndexOf("(") + 1, p.lastIndexOf(")"));
                if (str.length() > 0) {
                    index = Integer.parseInt(str);
                }
                p = p.substring(0, p.lastIndexOf("("));
            }

            if (p.equals("____")) {
                if (!array) {
                    if (currentObj instanceof Map) {
                        var map = (Map<String, Object>) currentObj;
                        if (map.containsKey("____")) {
                            currentObj = map.get("____");
                        }
                    }
                } else {
                    if (index <= 0) { // 未指定索引，或索引0：如果当前数据不是数组，则直接返回当前数据，否则返回第一个成员。
                        if (currentObj instanceof List) {
                            currentObj = ((List) currentObj).get(0);
                        }
                    } else { // 指定索引位置（>0）的成员，如果当前数据不是数组，或者索引越界，则返回null。
                        if ((currentObj instanceof List) && (((List) currentObj).size() > index)) {
                            currentObj = ((List) currentObj).get(index);
                        } else {
                            currentObj = null;
                        }
                    }
                }
            }

            else {
                try {
                    if (array) {
                        var obj = ((Map<String, Object>) currentObj).get(p);
                        if (index == 0) { // 第一个成员，如果当前数据不是数组，则直接返回当前数据。
                            if (obj instanceof List) {
                                currentObj = ((List) obj).get(0);
                            } else {
                                currentObj = obj;
                            }
                        } else if (index > 0) { // 指定索引位置（>0）的成员，如果当前数据不是数组，或者索引越界，则返回null（异常）。
                            currentObj = ((List) ((Map<String, Object>) currentObj).get(p)).get(index);
                        } else { // 未指定索引，返回整个数组。
                            if (obj instanceof List) {
                                currentObj = obj;
                            } else {
                                Object[] a = { obj };
                                currentObj = Arrays.asList(a);
                            }
                        }
                    } else {
                        currentObj = ((Map<String, Object>) currentObj).get(p);
                    }

                    if (currentObj instanceof Map) {
                        var map = (Map<String, Object>) currentObj;

                        // 只有一个Key：____，这代表这个map曾经不是一个map；并且没有显式访问 ____ 。
                        if (map.containsKey("____") && map.size() == 1 && p.equals("____")) {
                            currentObj = map.get("____");
                        }
                    }

                } catch (Exception e) {
                    // cast exception, null
                    currentObj = null;
                }
            }

            // 没找到就中断
            if (currentObj == null) {
                break;
            }
        }

        return currentObj;
    }

    /**
     * TODO 待优化，参考merge方法！<br>
     * 删除指定数据
     * 
     * @param uri  待删除数据的路径
     * @param save 是否持久化
     * @return 0：路径未找到，数据未改变；1：指定数据已删除。
     */
    public static int remove(String uri, boolean save) {

        int st = 0; // 返回值

        // 全量删除
        if (uri == null || uri.length() == 0 || uri.equals("/")) {
            JialoStore.store(new HashMap<String, Object>()); // TODO or null?
            st = 1;
        }

        // 部分删除
        else {
            // 以 / 开头的URI去掉开头
            if (uri.startsWith("/")) {
                uri = uri.substring(1);
            }

            // 以 / 结尾的URI去掉结尾
            if (uri.endsWith("/")) {
                uri = uri.substring(0, uri.length() - 1);
            }

            List<String> parts = Arrays.asList(uri.split("/"));

            Object currentObj = JialoStore.store();

            for (int i = 0; i < parts.size(); i++) {
                String p = parts.get(i);
                int index = -1;
                if (p.endsWith(")")) { // 数组成员
                    index = Integer.parseInt(p.substring(p.lastIndexOf("(") + 1, p.lastIndexOf(")")));
                    p = p.substring(0, p.lastIndexOf("("));
                    // 修复数据：当以数组方式访问非数组数据时，新建一个数组，把原对象添加为索引0的成员。
                    if ((currentObj instanceof Map) && !(((Map<String, Object>) currentObj).get(p) instanceof List)) {
                        var tmpData = ((Map<String, Object>) currentObj).get(p); // 原数据
                        var list = new ArrayList<>(index); // 新建的数组
                        list.add(tmpData); // 将原数据添加为新建的数组的第一个元素
                        ((Map<String, Object>) currentObj).put(p, list); // 使用新建的数组代替原数据
                    }
                }
                if (currentObj instanceof Map) { // 有下层节点
                    if (((Map<String, Object>) currentObj).get(p) == null) { // 没找到
                        // do nothing: not found
                        break;
                    } else { // 找到
                        if (i == parts.size() - 1) { // 最后一个部分
                            if (index >= 0) {
                                var list = ((List) ((Map<String, Object>) currentObj).get(p));
                                if (index >= list.size()) {
                                    // do nothing: out of range
                                    break;
                                } else {
                                    list.remove(index);
                                    st = 1;
                                }
                            } else {
                                ((Map<String, Object>) currentObj).remove(p);
                                st = 1;
                            }
                        } else { // 下一层
                            if (index >= 0) { // 数组
                                var list = ((List) ((Map<String, Object>) currentObj).get(p));
                                if (index >= list.size()) {
                                    // do nothing: out of range
                                    break;
                                } else {
                                    currentObj = list.get(index);
                                }
                            } else { // 对象
                                currentObj = ((Map<String, Object>) currentObj).get(p);
                            }
                        }
                    }
                } else { // 没有下层节点，不删除任何数据。
                    // do nothing: not exsist
                    break;
                }
            }
        }

        // 数据持久化
        if (save) {
            JialoStore.flush();
        }

        return st;
    }

    public static void main(String... args) {
        String p = "aaaa()";
        String str = p.substring(p.lastIndexOf("(") + 1, p.lastIndexOf(")"));
        System.out.println("str=" + str + "; len=" + str.length());
        int index = Integer.parseInt(str);
        System.out.println(index);
    }

}
