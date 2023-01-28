package ztp.labs.integrityvalidator.dto;

import lombok.Builder;
import lombok.Data;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;

@Data
@Builder
public class CrawlingContext {

    URL baseUrl;
    URL checksumUrl;
    String resolvedSHA256Checksum;
    String resolvedSHA512Checksum;
    Boolean requiresFurtherProcessing;
    Boolean canResolveSHA256Checksum;
    Boolean canResolveSHA512Checksum;
    Boolean checksumFromFile;
    Boolean checksumFromHtml;
    List<URL> urlsToCrawl;
    List<String> fileContent;

    public void addUrlsToCrawl(List<URL> url) {
        urlsToCrawl.addAll(url);
    }

    public static CrawlingContext init(URL baseUrl) {
        return CrawlingContext.builder()
                .baseUrl(baseUrl)
                .requiresFurtherProcessing(true)
                .canResolveSHA256Checksum(false)
                .canResolveSHA512Checksum(false)
                .checksumFromFile(false)
                .checksumFromFile(false)
                .urlsToCrawl(new LinkedList<>())
                .resolvedSHA256Checksum(null)
                .resolvedSHA512Checksum(null)
                .build();
    }

}
