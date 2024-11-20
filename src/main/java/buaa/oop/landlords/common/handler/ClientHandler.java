package buaa.oop.landlords.common.handler;

import buaa.oop.landlords.client.event.ClientEventListener;
import buaa.oop.landlords.common.entities.Msg;
import buaa.oop.landlords.common.enums.ClientEventCode;
import buaa.oop.landlords.common.enums.ServerEventCode;
import buaa.oop.landlords.common.print.SimplePrinter;
import buaa.oop.landlords.common.utils.ChannelUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ChannelHandler.Sharable
public class ClientHandler extends SimpleChannelInboundHandler<Msg> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Msg msg) throws Exception {
        if(msg.getInfo() != null && !msg.getInfo().isEmpty()){
            System.out.println(msg.getInfo());
        }
        ClientEventCode code = ClientEventCode.valueOf(msg.getCode());

        ClientEventListener.get(code).call(ctx.channel(), msg.getData());
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof IdleStateEvent event){
            if(event.state() == IdleState.WRITER_IDLE){
                ChannelUtil.pushToServer(ctx.channel(), ServerEventCode.CODE_CLIENT_HEART_BEAT, "heartbeat");
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        SimplePrinter.printNotice("Connection lost, please restart the client");
        System.exit(1);
    }
}
