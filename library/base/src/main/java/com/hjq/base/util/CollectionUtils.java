package com.hjq.base.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * <pre>
 *      author:         wangweixu
 *      date:           2022-07-15 09:33:26
 *      description:    集合工具类
 *      version:        v1.0
 * </pre>
 */
public class CollectionUtils {
    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static <E> ArrayList<E> asList(E... e) {
        return new ArrayList<>(Arrays.asList(e));
    }
}