package com.nova.fnfjava.lwjgl3.utils;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class Utils {

    public static URI toCodeSourceURI(URL url, String internalClassName) {
        if (url == null) return null;

        String urlPath = url.getPath();
        if (urlPath.endsWith(".class")) {
            String urlProtocol = url.getProtocol();
            if (urlProtocol.equals("jar")) {
                int index0 = urlPath.indexOf('!');
                if (index0 >= 0) {
                    try {
                        return new URI(urlPath.substring(0, index0));
                    } catch (URISyntaxException e) {
                        System.out.println("[Utils] Unable to assimilate jar-protocol-URL: " + url + "\n" + e);
                    }
                }
            } else if (urlProtocol.equals("file")) {
                String expectedSuffix = internalClassName.replace('.', '/') + ".class";
                if (urlPath.endsWith(expectedSuffix)) {
                    try {
                        return new URI("file", null, url.getHost(), url.getPort(), urlPath.substring(0, urlPath.length() - expectedSuffix.length()), null, null);
                    } catch (URISyntaxException e) {
                        System.out.println("[Utils] Unable to assimilate file-protocol-URL: " + url + "\n" + e);
                    }
                }
            }
        }

        try {
            return url.toURI();
        } catch (URISyntaxException e) {
            System.out.println("[Utils] Cannot convert URL " + url + " to a URI." + "\n" + e);
            return null;
        }
    }
}
