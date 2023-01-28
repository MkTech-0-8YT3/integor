package ztp.labs.fileresolver.util;

import java.net.URL;

public class UrlUtils {
    public static String getFilenameFromUrl(String url) {
        try {
            var processedUrlFileName = new URL(url).getFile();
            if(processedUrlFileName.contains("?"))
                processedUrlFileName = processedUrlFileName.substring(0, processedUrlFileName.indexOf("?"));
            if(processedUrlFileName.endsWith("/"))
                processedUrlFileName = processedUrlFileName.substring(0, processedUrlFileName.length() - 1);
            if(processedUrlFileName.startsWith("/"))
                processedUrlFileName = processedUrlFileName.substring(1);

            return processedUrlFileName;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
