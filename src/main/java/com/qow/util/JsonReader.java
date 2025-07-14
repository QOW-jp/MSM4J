package com.qow.util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * jsonファイルを読み込む
 *
 * @version 2025/07/14
 * @since 1.0.0
 */
public class JsonReader {
    private JSONObject jsonObj;

    /**
     * jsonファイルをすべて読み込み{@link JSONObject}に変換する
     *
     * @param path jsonファイルのパス
     */
    public JsonReader(String path) {
        jsonObj = null;
        FileReader fr = null;
        BufferedReader br = null;
        try {
            File file = new File(path);
            fr = new FileReader(file);
            br = new BufferedReader(fr);

            String strLine;
            StringBuilder sbSentence = new StringBuilder();
            while ((strLine = br.readLine()) != null) {
                sbSentence.append(strLine);
            }

            // JSONオブジェクトのインスタンス作成
            jsonObj = new JSONObject(sbSentence.toString());
        } catch (IOException e) {
            System.out.println(e);
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
                if (fr != null) {
                    fr.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * jsonファイルの文法に従って{@param key}のパスを絶対値で返す
     *
     * @param HOME_PATH ホームディレクトリ
     * @param json      対象の{@link JSONObject}
     * @param key       対象のkey
     * @return keyの絶対値
     */
    public static String getAbsolutePath(String HOME_PATH, JSONObject json, String key) {
        if (json.getBoolean(key + "-relative")) {
            return HOME_PATH + json.getString(key);
        } else {
            return json.getString(key);
        }
    }

    /**
     * jsonファイルの文法に従って{@param key}の複数のパスを絶対値で返す
     *
     * @param HOME_PATH ホームディレクトリ
     * @param json      対象の{@link JSONObject}
     * @param key       対象のkey
     * @return keyの絶対値の配列
     */
    public static String[] getAbsolutePaths(String HOME_PATH, JSONObject json, String key) {
        JSONArray jsonArray = json.getJSONArray(key);
        String[] paths = new String[jsonArray.length()];
        for (int i = 0; i < jsonArray.length(); i++) {
            if (json.getBoolean(key + "-relative")) {
                paths[i] = HOME_PATH + jsonArray.getString(i);
            } else {
                paths[i] = jsonArray.getString(i);
            }
        }
        return paths;
    }

    /**
     * @return コンストラクタで指定されたパスの最上位のオブジェクト
     */
    public JSONObject getJSONObject() {
        return jsonObj;
    }

    /**
     * @param key 対象のkey
     * @return keyに対応したオブジェクト
     */
    public JSONObject getJSONObject(String key) {
        return jsonObj.getJSONObject(key);
    }
}
