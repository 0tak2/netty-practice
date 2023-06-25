/**
 * Netty의 User guide 실습 코드
 * https://netty.io/wiki/user-guide-for-4.x.html
 */
package com.youngtak.study.netty.echo;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

/**
 * 요청을 핸들링한다.
 * ChannelInboundHandler를 구현한 ChannelInboundHandlerAdapter를 상속하였다.
 * ChannelInboundHandler를 직접 구현하여 사용할 수도 있다.
 */
public class EchoServerHandler extends ChannelInboundHandlerAdapter {
    /**
     * 클라이언트로부터 메시지를 수신하였을 때 호출되며, 인자로 수신한 메시지를 받는다.
     * 여기서는 ByteBuf 타입의 메시지를 받게 된다.
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 수신받은 메시지를 서버 콘솔에 출력한다
        try {
            while (((ByteBuf)msg).isReadable()) {
                System.out.print((char) ((ByteBuf)msg).readByte());
                System.out.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        // 수신받은 메시지를 그대로 쓴다
        ctx.write(msg);
        ctx.flush();

        // * 이 경우 msg를 release하지 않는다. 메시지를 쓸 때 네티가 릴리즈한다고 한다.
    }

    /**
     * IO 과정이나 이벤트 처리 과정에 예외가 발생했을 때 호출된다
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // 예외가 발생한 경우 커넥션을 닫는다.
        cause.printStackTrace();
        ctx.close();
    }
}
