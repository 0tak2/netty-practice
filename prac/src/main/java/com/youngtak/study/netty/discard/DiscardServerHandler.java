/**
 * Netty의 User guide 실습 코드
 * https://netty.io/wiki/user-guide-for-4.x.html
 */
package com.youngtak.study.netty.discard;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

/**
 * 요청을 핸들링한다.
 * ChannelInboundHandler를 구현한 ChannelInboundHandlerAdapter를 상속하였다.
 * ChannelInboundHandler를 직접 구현하여 사용할 수도 있다.
 */
public class DiscardServerHandler extends ChannelInboundHandlerAdapter {
    /**
     * 클라이언트로부터 메시지를 수신하였을 때 호출되며, 인자로 수신한 메시지를 받는다.
     * 여기서는 ByteBuf 타입의 메시지를 받게 된다.
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // Discard 서버이므로 수신한 데이터를 가공하지 않고 바로 release한다.
        ByteBuf in = (ByteBuf) msg;

        try {
            while (in.isReadable()) {
                System.out.print((char) in.readByte());
                System.out.flush();
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }

        // 문서에 따르면 ByteBuf 객체는 참조 카운트된 객체(Reference counted)이므로 반드시 핸들러에서 release 되어야 한다.
        // 참조 카운트된 객체(Reference counted)에 대해서는 자세히 알아봐야할 것 같다.
        // https://netty.io/wiki/reference-counted-objects.html
    }

//    일반적인 경우 channelRead 메서드의 기본꼴
//    @Override
//    public void channelRead(ChannelHandlerContext ctx, Object msg) {
//        try {
//            // Do something with msg
//        } finally {
//            ReferenceCountUtil.release(msg);
//        }
//    }

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
