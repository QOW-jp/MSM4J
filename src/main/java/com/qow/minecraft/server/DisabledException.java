package com.qow.minecraft.server;

/**
 * 有効化されていない機能をしようとした場合
 */
public class DisabledException extends Exception {
    /**
     * @param message 例外メッセージ
     */
    public DisabledException(String message) {
        super(message);
    }
}
