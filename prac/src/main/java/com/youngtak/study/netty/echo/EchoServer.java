/**
 * Netty의 User guide 실습 코드
 * https://netty.io/wiki/user-guide-for-4.x.html
 */
package com.youngtak.study.netty.echo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * 수신한 데이터를 버린다
 */
public class EchoServer {

    private final int port;

    public EchoServer(int port) {
        this.port = port;
    }

    /**
     * 서버를 세팅하고 연결을 기다린다
     */
    public void run() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap(); // 서버 세팅을 도와주는 헬퍼 클래스
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new EchoServerHandler());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            // 서버를 포트에 바인딩하고 연결을 기다린다
            ChannelFuture f = b.bind(port).sync();

            // 서버 소켓이 닫힐때까지 기다린다.
            // 이 예제에는 따로 서버를 종료하는 로직은 없다.
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    /**
     * 텔넷을 이용하여 테스트 가능
     * $ telnet 127.0.0.1 8080
     */
    public static void main(String[] args) throws Exception {
        int portConfig = 8080;
        if (args.length > 0) {
            portConfig = Integer.parseInt(args[0]);
        }

        new EchoServer(portConfig).run();
    }
}
