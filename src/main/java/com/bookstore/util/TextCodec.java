package com.bookstore.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * Small helper that makes free text (author, description) safe to store
 * in the comma separated books.txt file. Commas and new lines become %XX codes.
 */
public final class TextCodec {

    private TextCodec() {
        // utility class - no objects
    }

    public static String encode(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        try {
            return URLEncoder.encode(text, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }

    public static String decode(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        try {
            return URLDecoder.decode(text, "UTF-8");
        } catch (UnsupportedEncodingException | IllegalArgumentException e) {
            return text;
        }
    }
}
