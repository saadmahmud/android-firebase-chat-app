package com.surf.saad.app.appUtils;

import java.util.UUID;

public class ServerUtils {
    public static String getServerFilePathToUpload(String fileExtension) {
        return "images/file_" + UUID
                .randomUUID() + "." + fileExtension;
    }
}
