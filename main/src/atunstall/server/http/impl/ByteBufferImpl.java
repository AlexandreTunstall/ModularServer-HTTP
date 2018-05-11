package atunstall.server.http.impl;

import atunstall.server.io.api.ByteBuffer;

import java.util.Arrays;
import java.util.function.Consumer;

public class ByteBufferImpl implements ByteBuffer {
    private byte[] bytes;
    private ByteBuffer body;

    ByteBufferImpl(byte[] bytes) {
        this.bytes = bytes;
    }

    @Override
    public long count() {
        if (body != null) {
            return bytes.length + body.count();
        } else {
            return bytes.length;
        }
    }

    @Override
    public byte get(long index) {
        return index < bytes.length ? bytes[(int) index] : body.get(index - bytes.length);
    }

    @Override
    public void get(long index, byte[] bytes, int offset, int count) {
        if (index <= this.bytes.length) {
            int count1 = Math.min(count, this.bytes.length - (int) index);
            System.arraycopy(this.bytes, (int) index, bytes, offset, count1);
            index = 0L;
            offset += count1;
            count -= count1;
        } else {
            index -= this.bytes.length;
        }
        if (count > 0) {
            body.get(index, bytes, offset, count);
        }
    }

    @Override
    public void apply(long index, long count, Consumer<byte[]> consumer) {
        if (index <= bytes.length) {
            int count1 = (int) Math.min(count, bytes.length - index);
            byte[] copy = new byte[count1];
            System.arraycopy(bytes, (int) index, copy, 0, count1);
            consumer.accept(copy);
            index = 0L;
            count -= count1;
        } else {
            index -= bytes.length;
        }
        body.apply(index, count, consumer);
    }

    @Override
    public ByteBuffer partition(long index, long count) {
        return null;
    }

    @Override
    public void clear(long index, long count) {
        if (index <= bytes.length) {
            int count1 = (int) Math.min(count, bytes.length - index);
            Arrays.fill(bytes, (int) index, (int) index + count1, (byte) 0);
            index = 0L;
            count -= count1;
        } else {
            index -= bytes.length;
        }
        if (count > 0) {
            body.clear(index, count);
        }
    }

    void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    ByteBuffer getBody() {
        return body;
    }

    void setBody(ByteBuffer buffer) {
        body = buffer;
    }
}
