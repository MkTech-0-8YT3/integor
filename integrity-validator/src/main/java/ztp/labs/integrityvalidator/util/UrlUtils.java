package ztp.labs.integrityvalidator.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

public class UrlUtils {

    public static String getFileName(URL url) {
        return url.getFile();
    }

    public static URL getUrlFromString(String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<String> generatePotentialUrlsWithChecksumFile(URL baseFileDownloadUrl) {
        var stringUrl = baseFileDownloadUrl.toString();
        var potentialIntegrityChecksumFiles = new LinkedList<String>();

        potentialIntegrityChecksumFiles.add(stringUrl + ".sha256");
        potentialIntegrityChecksumFiles.add(stringUrl + ".sha512");

        return potentialIntegrityChecksumFiles;
    }

}
