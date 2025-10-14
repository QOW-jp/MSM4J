package com.qow.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Property {
    private final List<String> targetKeyList;
    private boolean parsed;
    private Map<Boolean, String> booleanMap;
    private Map<Byte, String> byteMap;
    private Map<Character, String> characterMap;
    private Map<Short, String> shortMap;
    private Map<Integer, String> integerMap;
    private Map<Float, String> floatMap;
    private Map<Long, String> longMap;
    private Map<Double, String> doubleMap;
    private Map<String, String> stringMap;

    public Property() {
        parsed = false;
        targetKeyList = new ArrayList<>();
    }

    public boolean parse() {
        boolean[] targetExist = new boolean[targetKeyList.size()];
        for (int i = 0; i < targetKeyList.size(); i++) {
            String target = targetKeyList.get(i);
            if (booleanMap.containsKey(target) || byteMap.containsKey(target) || characterMap.containsKey(target) || shortMap.containsKey(target) || integerMap.containsKey(target) || floatMap.containsKey(target) || longMap.containsKey(target) || doubleMap.containsKey(target) || stringMap.containsKey(target)) {
                targetExist[i] = true;
            }
        }

        for (int i = 0; i < targetExist.length; i++) {
            if (!targetExist[i]) return false;
        }

        parsed = true;
        return true;
    }

    public final void addTargetKey(String key) {
        targetKeyList.add(key);
    }

    public final void removeTargetKey(String key) {
        targetKeyList.remove(key);
    }
}
