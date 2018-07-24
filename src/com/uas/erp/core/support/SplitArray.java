package com.uas.erp.core.support;

import java.util.ArrayList;
import java.util.List;

/**
 * 数组拆分
 * 
 * @author hejq
 * @time 创建时间：2017年10月18日
 */
public class SplitArray{

    /**
     * SQL查询in表达式中最大的数量
     */
    public final static int QUERY_MAX_NUMBER = 1000;

    /**
     * 集合大小拆分
     * @param ary
     * @param subSize
     * @return
     */
    public static <T> List<List<T>> splitAry(List<T> ary, int subSize) {
        int count = ary.size() % subSize == 0 ? ary.size() / subSize: ary.size() / subSize + 1;
        List<List<T>> subAryList = new ArrayList<List<T>>();
        for (int i = 0; i < count; i++) {
            int index = i * subSize;
            List<T> list = new ArrayList<T>();
            int j = 0;
            while (j < subSize && index < ary.size()) {
                list.add(ary.get(index++));
                j++;
            }
            subAryList.add(list);
        }
        return subAryList;
    }
}
