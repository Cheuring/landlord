package buaa.oop.landlords.client;

import buaa.oop.landlords.common.handler.ClientHandler;
import buaa.oop.landlords.common.handler.MsgCodec;
import buaa.oop.landlords.common.handler.ProtocolFrameDecoder;
import buaa.oop.landlords.common.print.SimplePrinter;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SimpleClient {
    public static ChatRoom chatRoom = null;

    public static void main(String[] args) {
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                SimplePrinter.ServerLog("Client shutdown");

                group.shutdownGracefully();
                if(chatRoom != null){
                    chatRoom.shutdown();
                    chatRoom = null;
                }
            }));

            new Bootstrap()
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline()
                                    .addLast(new ProtocolFrameDecoder())
                                    .addLast(new LoggingHandler())
                                    .addLast(new MsgCodec())
//                                    .addLast(new IdleStateHandler(0, 8, 0, TimeUnit.SECONDS))
                                    .addLast(new ClientHandler());
                        }
                    })
                    .connect("localhost", 8080).sync()
                    .channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.debug("Client error: {}", e.getMessage());
        } finally {
            if(chatRoom != null){
                chatRoom.shutdown();
                chatRoom = null;
            }
            group.shutdownGracefully();
        }
    }
}
