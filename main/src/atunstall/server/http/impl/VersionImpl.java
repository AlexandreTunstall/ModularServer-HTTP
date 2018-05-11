package atunstall.server.http.impl;

import atunstall.server.core.api.Version;

import java.lang.annotation.Annotation;
import java.nio.charset.Charset;

@SuppressWarnings("ClassExplicitlyAnnotation")
class VersionImpl implements Version {
    static final Charset HEADER_CHARSET = Charset.forName("ISO-8859-1");
    static final byte[] CRLF = "\r\n".getBytes(HEADER_CHARSET);
    static final byte[] CRLF_CRLF = "\r\n\r\n".getBytes(HEADER_CHARSET);
    static final byte[] KV_SEPARATOR = ": ".getBytes(HEADER_CHARSET);
    static final byte[] SP = " ".getBytes(HEADER_CHARSET);
    static final String VERSION_PREFIX = "HTTP/";

    private int major;
    private int minor;

    VersionImpl(int major, int minor) {
        this.major = major;
        this.minor = minor;
    }

    @Override
    public int major() {
        return major;
    }

    @Override
    public int minor() {
        return minor;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return Version.class;
    }

    static boolean fieldContains(String value, String check) {
        for (String v : value.split(", ")) {
            if (v.equalsIgnoreCase(check)) {
                return true;
            }
        }
        return false;
    }
}
