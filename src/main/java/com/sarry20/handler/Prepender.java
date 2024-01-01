package com.sarry20.handler;

import com.github.retrooper.packetevents.netty.buffer.ByteBufHelper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.EncoderException;
import io.netty.handler.codec.MessageToByteEncoder;

public class Prepender extends MessageToByteEncoder<ByteBuf> {
    public int getVarIntSize(int len) {
        for (int i = 1; i < 5; i++) {
            if ((len & -1 << i * 7) == 0)
                return i;
        }
        return 5;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) throws Exception {
        //Prefix the packet with its length
        //Structure:
        /*
         * Length = VarInt
         * ID = VarInt
         * Data = Bytes
         */
        int length = msg.readableBytes();
        int varIntSize = getVarIntSize(length);
        if (varIntSize > 3) {
            throw new IllegalStateException("Something went wrong in the Packet Formatter!");
        }
        out.ensureWritable(varIntSize + length);
        ByteBufHelper.writeVarInt(out, length);
        out.writeBytes(msg, msg.readerIndex(), length);
    }
}
