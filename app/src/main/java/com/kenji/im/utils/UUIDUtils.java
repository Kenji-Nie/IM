package com.kenji.im.utils;

import java.util.UUID;

public class UUIDUtils {

    public static String generte() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString().replace("-", "");
    }
}
