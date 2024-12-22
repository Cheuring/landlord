package buaa.oop.landlords.client;

import buaa.oop.landlords.client.GUI.GameSettlement;
import buaa.oop.landlords.client.GUI.Loading;
import buaa.oop.landlords.client.GUI.Login;
import buaa.oop.landlords.client.handler.ClientHandler;
import buaa.oop.landlords.common.handler.MsgCodec;
import buaa.oop.landlords.common.handler.ProtocolFrameDecoder;
import buaa.oop.landlords.common.print.SimplePrinter;
import buaa.oop.landlords.common.print.SimpleWriter;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import javafx.application.Application;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.*;

import java.util.concurrent.TimeUnit;

@Slf4j
public class SimpleClient {
    public static void main(String[] args) {
        Options options = new Options();

        Option hostOption = new Option("h", "host", true, "server host");
        hostOption.setRequired(false);
        options.addOption(hostOption);

        Option portOption = new Option("p", "port", true, "server port");
        portOption.setRequired(false);
        options.addOption(portOption);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("java -jar landlord-client", options, true);

            System.exit(1);
            return;
        }

        String host = cmd.getOptionValue("host", "8.152.218.39");

        int port = Integer.parseInt(cmd.getOptionValue("port", "32112"));

        Loading.setConnectionDetails(host, port);
        Application.launch(Loading.class);
    }

    public static boolean connect(String host, int port) {
        log.info("Connecting to server {}:{}", host, port);
        boolean success = false;
        NioEventLoopGroup group = new NioEventLoopGroup(2);
        try {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                SimplePrinter.ServerLog("Client shutdown");

                group.shutdownGracefully();
            }));

            new Bootstrap()
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) {
                            socketChannel.pipeline()
                                    .addLast(new ProtocolFrameDecoder())
                                    .addLast(new MsgCodec())
                                    .addLast(new IdleStateHandler(0, 8, 0, TimeUnit.SECONDS))
                                    .addLast(new ClientHandler());
                        }
                    })
                    .connect(host, port).sync()
                    .channel().closeFuture().addListener((ChannelFutureListener) future -> {

                group.shutdownGracefully();
            });
            success = true;
        } catch (Exception e) {
            log.debug("Client error: {}", e.getMessage());
            group.shutdownGracefully();
        }
        return success;
    }
}
