package com.qow.util;

import com.qow.util.qon.QONArray;
import com.qow.util.qon.QONObject;

/**
 * qonファイルを読み込む
 *
 * @version 2025/08/20
 * @since 1.1.0
 */
public class QonReader {
    /**
     * qonファイルの文法に従って{@param key}のパスを絶対値で返す
     *
     * @param HOME_PATH ホームディレクトリ
     * @param qon       対象の{@link QONObject}
     * @param key       対象のkey
     * @return keyの絶対値
     */
    public static String getAbsolutePath(String HOME_PATH, QONObject qon, String key) {
        if (Boolean.parseBoolean(qon.get(key + "-relative"))) {
            return HOME_PATH + qon.get(key);
        } else {
            return qon.get(key);
        }
    }

    /**
     * qonファイルの文法に従って{@param key}の複数のパスを絶対値で返す
     *
     * @param HOME_PATH ホームディレクトリ
     * @param qon       対象の{@link QONObject}
     * @param key       対象のkey
     * @return keyの絶対値の配列
     */
    public static String[] getAbsolutePaths(String HOME_PATH, QONObject qon, String key) {
        QONArray qonArray = qon.getQONArray(key);
        String[] paths = new String[qonArray.list().length];
        for (int i = 0; i < qonArray.list().length; i++) {
            if (Boolean.parseBoolean(qon.get(key + "-relative"))) {
                paths[i] = HOME_PATH + qonArray.get(i);
            } else {
                paths[i] = qonArray.get(i);
            }
        }
        return paths;
    }
}
