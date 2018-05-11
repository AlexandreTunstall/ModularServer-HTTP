package atunstall.server.http.impl;

import atunstall.server.io.api.InputStream;

import java.util.HashMap;
import java.util.Map;

abstract class MessageBuilder<T> {
    Map<String, String> fields;

    MessageBuilder() {
        fields = new HashMap<>();
    }

    void addField(String key, String value) {
        fields.put(key, value);
    }

    abstract T build(InputStream body);
}
