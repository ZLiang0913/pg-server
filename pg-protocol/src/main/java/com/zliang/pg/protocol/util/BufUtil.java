package com.zliang.pg.protocol.util;

import com.zliang.pg.common.enums.ErrorKind;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.CodecException;

import java.nio.charset.Charset;

/**
 * @author: zliang
 * @Description Little Endian 小端字节序
 * @date: 2021/8/3 10:04
 */
public class BufUtil {
    public static final int NULL_VALUE = 0xfb;
    public static final int SHORT_VALUE = 0xfc;
    public static final int MEDIUM_VALUE = 0xfd;
    public static final int LONG_VALUE = 0xfe;

    public static int readUB2(ByteBuf buf) {
        return buf.readUnsignedShort();
    }

    public static int readUB3(ByteBuf buf) {
        return buf.readUnsignedMedium();
    }

    public static long readUB4(ByteBuf buf) {
        return buf.readUnsignedInt();
    }

    public static long readLenEncInteger(ByteBuf buf) {
        long firstByte = buf.readByte() & 0xff;
        if (firstByte < NULL_VALUE) {
            return firstByte;
        }
        if (firstByte == NULL_VALUE) {
            return -1;
        }
        if (firstByte == SHORT_VALUE) {
            return buf.readUnsignedShort();
        }
        if (firstByte == MEDIUM_VALUE) {
            return buf.readUnsignedMedium();
        }
        if (firstByte == LONG_VALUE) {
            final long length = buf.readLong();
            if (length < 0) {
                throw new CodecException("Received a length value too large to handle: " + Long.toHexString(length));
            }
            return length;
        }
        throw new CodecException("Received an invalid length value " + firstByte);
    }

    public static String readLenEncString(ByteBuf buf) {
        return readLenEncString(buf, Charset.defaultCharset());
    }

    public static String readLenEncString(ByteBuf buf, String charsetName) {
        return readLenEncString(buf, Charset.forName(charsetName));
    }

    public static String readLenEncString(ByteBuf buf, Charset charset) {
        long strLen = readLenEncInteger(buf);
        String str = buf.toString(buf.readerIndex(), (int)strLen, charset);
        buf.skipBytes((int)strLen);
        return str;
    }

    public static String readNullTerminatedString(ByteBuf buf) {
        return readNullTerminatedString(buf, Charset.defaultCharset());
    }

    public static String readNullTerminatedString(ByteBuf buf, Charset charset) {
        return new String(readNullTerminatedBytes(buf), charset);
    }

    public static byte[] readNullTerminatedBytes(ByteBuf buf) {
        int nullIndex = buf.indexOf(buf.readerIndex(), buf.capacity(), (byte)0);
        byte[] bytes = new byte[nullIndex - buf.readerIndex()];
        buf.readBytes(bytes);
        buf.skipBytes(1);//skip null
        return bytes;
    }

    public static String readEofString(ByteBuf buf) {
        return readEofString(buf, Charset.defaultCharset());
    }

    public static String readEofString(ByteBuf buf, Charset charset) {
        return new String(readEofBytes(buf), charset);
    }

    public static byte[] readEofBytes(ByteBuf buf) {
        byte[] bytes = new byte[buf.writerIndex() - buf.readerIndex()];
        buf.readBytes(bytes);
        return bytes;
    }

    public static void writeLenEncInt(ByteBuf buf, Long n) {
        if (n == null) {
            buf.writeByte(NULL_VALUE);
        } else if (n < 0) {
            throw new IllegalArgumentException("Cannot encode a negative length: " + n);
        } else if (n < NULL_VALUE) {
            buf.writeByte(n.intValue());
        } else if (n < 0xffff) {
            buf.writeByte(SHORT_VALUE);
            buf.writeShort(n.intValue());
        } else if (n < 0xffffff) {
            buf.writeByte(MEDIUM_VALUE);
            buf.writeMedium(n.intValue());
        } else {
            buf.writeByte(LONG_VALUE);
            buf.writeLong(n);
        }
    }

    public static void writeLenEncString(ByteBuf buf, String str) {
        writeLenEncString(buf, str, Charset.defaultCharset());
    }

    public static void writeLenEncString(ByteBuf buf, String str, String charset) {
        writeLenEncString(buf, str, Charset.forName(charset));
    }

    public static void writeLenEncString(ByteBuf buf, String str, Charset charset) {
        if (str == null) {
            buf.writeByte(0xfb);
        } else {
            byte[] data = str.getBytes(charset);
            long strLen = data.length;
            writeLenEncInt(buf, strLen);
            buf.writeBytes(data);
        }
    }

    public static void writeNullTerminatedString(ByteBuf buf, String data) {
        writeNullTerminatedString(buf, data, Charset.defaultCharset());
    }

    public static void writeNullTerminatedString(ByteBuf buf, String data, Charset charset) {
        buf.writeCharSequence(data, charset);
        buf.writeByte(0x00);
    }

    public static void read_contents(ByteBuf buf, short messageTag) {
        // protocol defines length for all types of messages
        long length = readUB4(buf);
        if(length < 4) {
            return Err(Error::new(
                    ErrorKind::Other,
                    "Unexpectedly small (<0) message size",
        ));
        }

        trace!(
                "[pg] Receive package {:X?} with length {}",
                message_tag,
                length
    );

        let length = usize::try_from(length - 4).map_err(|_| {
                Error::new(
                ErrorKind::OutOfMemory,
                "Unable to convert message length to a suitable memory size",
        )
    })?;

        let buffer = if length == 0 {
            vec![0; 0]
        } else {
            let mut buffer = vec![0; length];
            reader.read_exact(&mut buffer).await?;

            buffer
        };

        let cursor = Cursor::new(buffer);

        Ok(cursor)
    }
}