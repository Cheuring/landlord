package buaa.oop.landlords.server;

import buaa.oop.landlords.common.handler.MsgCodec;
import buaa.oop.landlords.common.handler.ProtocolFrameDecoder;
import buaa.oop.landlords.server.handler.ServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public class SimpleServer {
    public static void main(String[] args) {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();

        try {
            new ServerBootstrap()
                    .group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline()
                                    .addLast(new IdleStateHandler(60 * 10, 0, 0, TimeUnit.SECONDS))
                                    .addLast(new ProtocolFrameDecoder())
                                    .addLast(new LoggingHandler())
                                    .addLast(new MsgCodec())
                                    .addLast(new ServerHandler());
                        }
                    })
                    .bind(ServerContainer.port).sync()
                    .channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.debug("Server error: {}", e.getMessage());
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }
}
