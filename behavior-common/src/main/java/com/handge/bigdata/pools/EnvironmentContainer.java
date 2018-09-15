/*
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.handge.bigdata.pools;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class EnvironmentContainer {

    private static Map<String, String> getModifiableEnvironmentMap() {
        try {
            Map<String, String> unmodifiableEnv = System.getenv();
            Class<?> cl = unmodifiableEnv.getClass();
            Field field = cl.getDeclaredField("m");
            field.setAccessible(true);
            Map<String, String> modifiableEnv = (Map<String, String>) field.get(unmodifiableEnv);
            return modifiableEnv;
        } catch (Exception e) {
            throw new RuntimeException("Unable to access writable environment variable map.");
        }
    }

    private static Map<String, String> getModifiableEnvironmentMap2() {
        try {
            Class<?> processEnvironmentClass = Class.forName("java.lang.ProcessEnvironment");
            Field theUnmodifiableEnvironmentField = processEnvironmentClass.getDeclaredField("theUnmodifiableEnvironment");
            theUnmodifiableEnvironmentField.setAccessible(true);
            Map<String, String> theUnmodifiableEnvironment = (Map<String, String>) theUnmodifiableEnvironmentField.get(null);

            Class<?> theUnmodifiableEnvironmentClass = theUnmodifiableEnvironment.getClass();
            Field theModifiableEnvField = theUnmodifiableEnvironmentClass.getDeclaredField("m");
            theModifiableEnvField.setAccessible(true);
            Map<String, String> modifiableEnv = (Map<String, String>) theModifiableEnvField.get(theUnmodifiableEnvironment);
            return modifiableEnv;
        } catch (Exception e) {
            throw new RuntimeException("Unable to access writable environment variable map.");
        }
    }

    private static Map<String, String> clearEnvironmentVars(String[] keys) {

        Map<String, String> modifiableEnv = getModifiableEnvironmentMap();

        HashMap<String, String> savedVals = new HashMap<String, String>();

        for (String k : keys) {
            String val = modifiableEnv.remove(k);
            if (val != null) {
                savedVals.put(k, val);
            }
        }
        return savedVals;
    }

    private static void setEnvironmentVars(Map<String, String> varMap) {
        getModifiableEnvironmentMap().putAll(varMap);
    }


    public static void setENV() {
        String hook = System.getenv("CODE_HOOK");
        if ((hook == null) || (Boolean.valueOf(hook) == true)) {
            Map map = new HashMap();
            map.put("CONF_DB_HOST", "172.20.31.108");
            map.put("CONF_DB_PORT", "5432");
            map.put("CONF_DB_DATABASE", "hr");
            map.put("CONF_DB_TABLE", "behavior_config_common");
            map.put("CONF_DB_USER", "postgres");
            map.put("CONF_DB_PASSWORD", "postgres");
            setEnvironmentVars(map);
        }
    }

}
