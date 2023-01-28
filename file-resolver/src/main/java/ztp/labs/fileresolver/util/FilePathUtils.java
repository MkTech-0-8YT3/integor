package ztp.labs.fileresolver.util;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public class FilePathUtils {

    public static Path generateFileLocation(String storageDirectory) {
        var fileId = UUID.randomUUID();

        return storageDirectory.endsWith("\\") || storageDirectory.endsWith("/")
                ? resolvePathFromStorageProperty(storageDirectory + fileId)
                : resolvePathFromStorageProperty(storageDirectory + File.separator + fileId);
    }

    private static Path resolvePathFromStorageProperty(String fileLocation) {
        return Paths.get(fileLocation);
    }

}
