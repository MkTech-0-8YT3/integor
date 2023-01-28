package ztp.labs.integrityvalidator.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import java.util.stream.Stream;

public class HashUtils {

    private static final String HEX_REGEX = "-?[0-9a-fA-F]+";

    public enum HashingAlgo {
        SHA256("SHA-256", 64),
        SHA512("SHA-512", 128);

        public final String digest;
        public final Integer hexStringLength;

        HashingAlgo(String digest, Integer hexStringLength) {
            this.digest = digest;
            this.hexStringLength = hexStringLength;
        }
    }

    public static String getFileHash(File file, HashingAlgo hashingAlgo) {
        try {
            var fileInputStream = new FileInputStream(file);
            var digest = MessageDigest.getInstance(hashingAlgo.digest);
            var byteArray = new byte[1024];
            var bytesCount = 0;

            while((bytesCount = fileInputStream.read(byteArray)) != -1) {
                digest.update(byteArray, 0, bytesCount);
            }

            fileInputStream.close();
            var resultDigest = digest.digest();
            return bytesToHex(resultDigest);
        } catch (NoSuchAlgorithmException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Optional<String> tryResolveSHA256ChecksumFromLine(String line, Boolean lineHasWhiteSpaceSeparators) {
        return lineHasWhiteSpaceSeparators
                ? tryResolveChecksumFromLineWithSpaceSeparators(line, HashingAlgo.SHA256)
                : tryResolveChecksumFromLine(line, HashingAlgo.SHA256);
    }

    public static Optional<String> tryResolveSHA512ChecksumFromLine(String line, Boolean lineHasWhiteSpaceSeparators) {
        return lineHasWhiteSpaceSeparators
                ? tryResolveChecksumFromLineWithSpaceSeparators(line, HashingAlgo.SHA512)
                : tryResolveChecksumFromLine(line, HashingAlgo.SHA512);
    }

    private static Optional<String> tryResolveChecksumFromLineWithSpaceSeparators(String line, HashingAlgo algorithm) {
        var lineParts = line.split("\\s");

        return Stream.of(lineParts)
                .filter(x -> x.length() == algorithm.hexStringLength && x.matches(HEX_REGEX))
                .findAny();
    }

    private static Optional<String> tryResolveChecksumFromLine(String line, HashingAlgo algorithm) {
        return line.length() == algorithm.hexStringLength && line.matches(HEX_REGEX)
                ? Optional.of(line)
                : Optional.empty();
    }

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

}
