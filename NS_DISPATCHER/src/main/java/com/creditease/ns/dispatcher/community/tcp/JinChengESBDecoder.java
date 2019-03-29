package com.creditease.ns.dispatcher.community.tcp;

import com.creditease.ns.dispatcher.community.common.error.ErrorHandler;
import com.creditease.ns.dispatcher.community.common.error.ErrorHandlerException;
import com.creditease.ns.dispatcher.community.common.error.ErrorType;
import com.creditease.ns.dispatcher.community.common.tcp.TcpContext;
import com.creditease.ns.dispatcher.community.common.tcp.TcpRequest;
import com.creditease.ns.log.NsLog;
import com.google.common.base.Charsets;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;
import java.util.UUID;

public class JinChengESBDecoder extends ByteToMessageDecoder {
    private static NsLog flowLog = NsLog.getFlowLog("Dispatcher", "报文解析器");
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msgIn, List<Object> out) {
        //小于8个字节直接舍弃
        if (msgIn.readableBytes() < 8) {
            return;
        }
        //初始可读标志位
        int beginIndex = msgIn.readerIndex();
        byte[] pkgLenBytes = new byte[8];
        msgIn.readBytes(pkgLenBytes);
        Integer length = Integer.valueOf(new String(pkgLenBytes, Charsets.UTF_8));

        if ((msgIn.readableBytes() + 8) < length) {
            msgIn.readerIndex(beginIndex);
            return;
        }

        ByteBuf contentBytes = msgIn.readBytes(length);

        byte[] headerBytes = new byte[124];
        System.arraycopy(pkgLenBytes, 0, headerBytes, 0, 8);
        byte[] otherHeaderBytes = new byte[116];
        contentBytes.readBytes(otherHeaderBytes);
        System.arraycopy(otherHeaderBytes, 0, headerBytes, 8, 116);

        byte[] prcscd = new byte[8];
        System.arraycopy(headerBytes, 44, prcscd, 0, 8);
        int xmlLength = contentBytes.readableBytes();
        byte[] xmlBytes = new byte[xmlLength];
        contentBytes.getBytes(contentBytes.readerIndex(), xmlBytes);

        TcpContext tcpContext = new TcpContext();
        tcpContext.setId(UUID.randomUUID().toString());
        NsLog.setMsgId(tcpContext.getId());
        tcpContext.getRequest().setServerName(new String(prcscd, Charsets.UTF_8).trim());
        tcpContext.getRequest().setAccessTimeStamp(System.currentTimeMillis());
        ((TcpRequest) tcpContext.getRequest()).setHeadMessage(new String(headerBytes, Charsets.UTF_8));
        ((TcpRequest) tcpContext.getRequest()).setXMLContent(new String(xmlBytes, Charsets.UTF_8));

        out.add(tcpContext);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        flowLog.error("解析报文异常{}", cause);
        ErrorHandler.handle(new ErrorHandlerException(cause, ErrorType.MESSAGE_FORMAT), ctx.channel());
    }
}
