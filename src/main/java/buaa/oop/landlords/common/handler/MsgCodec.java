package buaa.oop.landlords.common.handler;

import buaa.oop.landlords.common.entities.Msg;
import buaa.oop.landlords.common.utils.JsonUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
public class MsgCodec extends MessageToMessageCodec<ByteBuf, Msg> {
    public static final byte[] MagicNum = {'b', 'u', 'a', 'a'};

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Msg msg, List<Object> list) throws Exception {
        ByteBuf byteBuf = channelHandlerContext.alloc().buffer();
        byteBuf.writeBytes(MagicNum);

        byte[] bytes = JsonUtil.toJson(msg).getBytes(StandardCharsets.UTF_8);

        assert bytes.length < 4088: "Msg too long";

        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);

        list.add(byteBuf);
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        byte[] magicNum = new byte[4];
        for (int i = 0; i < 4; i++) {
            magicNum[i] = byteBuf.readByte();
            if(magicNum[i] != MagicNum[i]){
                log.error("MagicNum error");
                return;
            }
        }

        int length = byteBuf.readInt();
        byte[] bytes = new byte[length];
        byteBuf.readBytes(bytes);

        Msg msg = JsonUtil.fromJson(new String(bytes, StandardCharsets.UTF_8), Msg.class);
        list.add(msg);
    }
}
