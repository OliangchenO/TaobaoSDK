package com.module.taobao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DataUtils {

	public static String toString(List<Long> list) {
		StringBuffer buf = new StringBuffer();
		Iterator<Long> i = list.iterator();
		boolean hasNext = i.hasNext();
		while (hasNext) {
			Long o = i.next();
			buf.append(String.valueOf(o));
			hasNext = i.hasNext();
			if (hasNext)
				buf.append(", ");
		}
		return buf.toString();
	}

	public static void main(String[] args) {
		List<Long> list = new ArrayList<Long>();
		list.add(1l);
		list.add(2l);
		list.add(3l);
		System.out.println(DataUtils.toString(list));
	}
}
