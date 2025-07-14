package com.qow.minecraft.server;

/**
 * 意図されたエディション以外が指定された場合呼び出される
 */
public class MinecraftEditionException extends Exception {
    /**
     * @param message 例外メッセージ
     */
    public MinecraftEditionException(String message) {
        super(message);
    }
}
