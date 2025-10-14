package com.qow.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Property {
    private final List<String> targetKeyList;
    private final Map<String, String> targetMap;
    private boolean parsed;

    public Property() {
        parsed = false;
        targetKeyList = new ArrayList<>();
        targetMap = new HashMap<>();
    }

    public boolean parse() {
        for (String target : targetKeyList) {
            if (!targetMap.containsKey(target)) {
                return false;
            }
        }

        parsed = true;
        return true;
    }

    public final void putMap(String key, String value) {
        targetMap.put(key, value);
    }

    public final void removeMap(String key) {
        targetMap.remove(key);
    }

    public final void addTargetKey(String key) {
        targetKeyList.add(key);
    }

    public final void removeTargetKey(String key) {
        targetKeyList.remove(key);
    }
}
