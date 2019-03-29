package com.creditease.ns.mq.model;

import com.creditease.ns.mq.exception.MQConnectionException;

import java.nio.ByteBuffer;

/**
 * The Message for transport through the mq, and contain the header and body
 */
public class Message {
    Header header;
    byte[] body;

    public Message() {
    }
    public Message(String msgId){
        header = new Header(msgId);
    }
    public Message(String msgId,DeliveryMode deliveryMode) {
        header = new Header(msgId,deliveryMode);
    }
    public Message(DeliveryMode deliveryMode) {
        header = new Header(deliveryMode);
    }


    public Message(byte[] data){
        ByteBuffer byteBuffer = ByteBuffer.wrap(data);
        int headLength = byteBuffer.getInt();
        byte[] headerBytes = new byte[headLength];
        byteBuffer.get(headerBytes);
        this.header = Header.toHeader(headerBytes);
        int bodyLength = byteBuffer.getInt();
        this.body = new byte[bodyLength];
        byteBuffer.get(this.body);
    }

    public byte[] toBytes() throws MQConnectionException{
        byte[] headerBytes = Header.toBytes(header);
        int arrayLength = 8;
        arrayLength += headerBytes.length;
        if (body != null) {
            arrayLength += body.length;
        }
        ByteBuffer byteBuffer = ByteBuffer.allocate(arrayLength);
        byteBuffer.putInt(headerBytes.length);
        byteBuffer.put(headerBytes);
        if (body == null) {
            byteBuffer.putInt(-1);
        } else {
            byteBuffer.putInt(body.length);
            byteBuffer.put(body);
        }

        return byteBuffer.array();
    }

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }
}
