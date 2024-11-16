package buaa.oop.landlords.common.handler;

import buaa.oop.landlords.common.entities.ClientEnd;
import buaa.oop.landlords.common.entities.Msg;
import buaa.oop.landlords.common.enums.ClientEventCode;
import buaa.oop.landlords.common.enums.ClientStatus;
import buaa.oop.landlords.common.enums.ServerEventCode;
import buaa.oop.landlords.common.utils.ChannelUtil;
import buaa.oop.landlords.server.ServerContainer;
import buaa.oop.landlords.server.event.ServerEventListener;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServerHandler extends SimpleChannelInboundHandler<Msg> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Msg msg) throws Exception {
        ServerEventCode code = ServerEventCode.valueOf(msg.getCode());

        if(code == ServerEventCode.CODE_CLIENT_HEART_BEAT){
            return;
        }

        ClientEnd client = ServerContainer.CLIENT_END_MAP.get(getId(ctx.channel()));
        log.info("Client {} | {} do: {}", client.getId(), client.getNickName(), code.getMsg());
        ServerEventListener.get(code).call(client, msg.getData());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if(cause instanceof java.io.IOException) {
//            clientOfflineEvent(ctx.channel());
        }else{
            log.error("Server error: {}", cause.getMessage());
            cause.printStackTrace();
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        clientOfflineEvent(ctx.channel());
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof IdleStateEvent event) {
            if(event.state() == IdleState.READER_IDLE) {
                clientOfflineEvent(ctx.channel());
                ctx.channel().close();
            }
        }else{
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ClientEnd client = new ClientEnd(getId(ctx.channel()), ctx.channel(), ClientStatus.IDLE);
        client.setNickName("Client " + client.getId());

        ServerContainer.CLIENT_END_MAP.put(client.getId(), client);
        log.info("Client {} | {} online", client.getId(), client.getNickName());

        new Thread(() -> {
            try {
                Thread.sleep(1000L);
                ChannelUtil.pushToClient(ctx.channel(), ClientEventCode.CODE_CLIENT_CONNECT, String.valueOf(client.getId()));
                ChannelUtil.pushToClient(ctx.channel(), ClientEventCode.CODE_CLIENT_NICKNAME_SET, null);
            } catch (InterruptedException ignored) {
            }
        }).start();
    }

    private int getId(Channel channel) {
        Integer clientId = ServerContainer.CHANNEL_ID_MAP.get(channel.id().asLongText());
        if (clientId == null) {
            clientId = ServerContainer.getNewClientId();
            ServerContainer.CHANNEL_ID_MAP.put(channel.id().asLongText(), clientId);
        }
        return clientId;
    }

    private void clientOfflineEvent(Channel channel) {
        int clientId = getId(channel);
        ClientEnd client = ServerContainer.CLIENT_END_MAP.get(clientId);
        if (client != null) {
//            log.info("Client {} | {} offline", client.getId(), client.getNickName());
            ServerEventListener.get(ServerEventCode.CODE_CLIENT_OFFLINE).call(client, null);
        }
    }
}
