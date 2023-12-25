package com.templatemela.smartpdfreader.util;

import android.content.SharedPreferences;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

public class RecentUtil {

    private static class SingletonHolder {
        static final RecentUtil INSTANCE = new RecentUtil();

        private SingletonHolder() {
        }
    }

    public static RecentUtil getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public LinkedHashMap<String, Map<String, String>> getList(SharedPreferences sharedPreferences) throws JSONException {
        LinkedHashMap<String, Map<String, String>> linkedHashMap = new LinkedHashMap<>();
        if (sharedPreferences.contains(Constants.RECENT_PREF)) {
            JSONObject jSONObject = new JSONObject(sharedPreferences.getString(Constants.RECENT_PREF, ""));
            Iterator<String> keys = jSONObject.keys();
            while (keys.hasNext()) {
                String next = keys.next();
                JSONObject jSONObject2 = jSONObject.getJSONObject(next);
                HashMap hashMap = new HashMap();
                String next2 = jSONObject2.keys().next();
                hashMap.put(next2, jSONObject2.getString(next2));
                linkedHashMap.put(next, hashMap);
            }
        }
        return linkedHashMap;
    }

    public void addFeatureInRecentList(SharedPreferences sharedPreferences, int i, Map<String, String> map) throws JSONException {
        LinkedHashMap<String, Map<String, String>> list = getList(sharedPreferences);
        if (list.size() == 7 && list.remove(String.valueOf(i)) == null) {
            list.remove(list.keySet().iterator().next());
        }
        list.put(String.valueOf(i), map);
        sharedPreferences.edit().putString(Constants.RECENT_PREF, new JSONObject(list).toString()).apply();
    }
}
