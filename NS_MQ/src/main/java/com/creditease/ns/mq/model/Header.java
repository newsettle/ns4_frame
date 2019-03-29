package com.creditease.ns.mq.model;

import com.creditease.ns.mq.model.serialize.StringSerialize;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.UUID;

public class Header {
    int version;
    //The type for delivery
    //1:rpc ,2: async,3: poison
    int deliveryMode;
    //The type for the body , such as json,xml ..
    //1:html，2:json,3:plaintext
    int contentType;
    // The encoding for the body
    //1:utf-8
    int contentEncoding;
    //是否终止流程 1:continue 2：stop
    int stopable;

    //Commonly used to name a callback queue.
    String replyTo;
    //Useful to corralte RPC responses with requests.
    String correlationID;
    //The unique id for every message
    String messageID;
    //ServerName for directory
    String serverName;
    //异常信息
    String exceptionContent;

    public Header() {
        this.version = 2;
        this.contentEncoding = HeaderConstant.CONTENT_ENCODING_UTF_8;
        this.contentType = HeaderConstant.CONTENT_TYPE_JSON;
        this.stopable = HeaderConstant.CHAIN_CONTINUE;
        messageID = UUID.randomUUID().toString();
    }

    public Header(String msgId) {
        this.version = 2;
        this.contentEncoding = HeaderConstant.CONTENT_ENCODING_UTF_8;
        this.contentType = HeaderConstant.CONTENT_TYPE_JSON;
        this.stopable = HeaderConstant.CHAIN_CONTINUE;
        messageID = msgId;
    }
    public Header(String msgId,DeliveryMode mode) {
        this.version = 2;
        this.contentEncoding = HeaderConstant.CONTENT_ENCODING_UTF_8;
        this.contentType = HeaderConstant.CONTENT_TYPE_JSON;
        this.stopable = HeaderConstant.CHAIN_CONTINUE;
        messageID = msgId;
        if (mode == DeliveryMode.SYNC) {
            replyTo = UUID.randomUUID().toString();
            correlationID = UUID.randomUUID().toString();
            deliveryMode = HeaderConstant.DELIVERY_MODE_SYNC;
        } else {
            deliveryMode = HeaderConstant.DELIVERY_MODE_ASYNC;
        }
    }

    public Header(DeliveryMode mode) {
        this.version = 2;
        this.contentEncoding = HeaderConstant.CONTENT_ENCODING_UTF_8;
        this.contentType = HeaderConstant.CONTENT_TYPE_JSON;
        this.stopable = HeaderConstant.CHAIN_CONTINUE;
        messageID = UUID.randomUUID().toString();
        if (mode == DeliveryMode.SYNC) {
            replyTo = UUID.randomUUID().toString();
            correlationID = UUID.randomUUID().toString();
            deliveryMode = HeaderConstant.DELIVERY_MODE_SYNC;
        } else {
            deliveryMode = HeaderConstant.DELIVERY_MODE_ASYNC;
        }
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(String replyTo) {
        this.replyTo = replyTo;
    }

    public String getCorrelationID() {
        return correlationID;
    }

    public void setCorrelationID(String correlationID) {
        this.correlationID = correlationID;
    }

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    public int getContentType() {
        return contentType;
    }

    public void setContentType(int contentType) {
        this.contentType = contentType;
    }

    public int getContentEncoding() {
        return contentEncoding;
    }

    public void setContentEncoding(int contentEncoding) {
        this.contentEncoding = contentEncoding;
    }

    public int getDeliveryMode() {
        return deliveryMode;
    }

    public void setDeliveryMode(int deliveryMode) {
        this.deliveryMode = deliveryMode;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public int getStopable() {
        return stopable;
    }

    public void setStopable(int stopable) {
        this.stopable = stopable;
    }

    public String getExceptionContent() {
        return exceptionContent;
    }

    public void setExceptionContent(String exceptionContent) {
        this.exceptionContent = exceptionContent;
    }

    //4bytes(length)|bytes|
    //1.0 version
    //int version |
    public static byte[] toBytes(Header header) {
        int version = header.getVersion();
        switch (version) {
            case 1: {
                int capacity = 4 * 4;
                int deliveryMode = header.getDeliveryMode();
                int contentType = header.getContentType();
                int contentEncoding = header.getContentEncoding();

                StringSerialize replyTo = new StringSerialize(header.getReplyTo());
                capacity += replyTo.getCapacity();
                StringSerialize correlationID = new StringSerialize(header.getCorrelationID());
                capacity += correlationID.getCapacity();
                StringSerialize messageID = new StringSerialize(header.getMessageID());
                capacity += messageID.getCapacity();

                StringSerialize serverName = new StringSerialize(header.getServerName());
                capacity += serverName.getCapacity();

                ByteBuffer byteBuffer = ByteBuffer.allocate(capacity);
                byteBuffer.putInt(version);
                byteBuffer.putInt(deliveryMode);
                byteBuffer.putInt(contentType);
                byteBuffer.putInt(contentEncoding);

                byteBuffer.putInt(replyTo.getLength());
                if (replyTo.getData() != null) {
                    byteBuffer.put(replyTo.getData());
                }

                byteBuffer.putInt(correlationID.getLength());
                if (correlationID.getData() != null) {
                    byteBuffer.put(correlationID.getData());
                }

                byteBuffer.putInt(messageID.getLength());
                if (messageID.getData() != null) {
                    byteBuffer.put(messageID.getData());
                }

                byteBuffer.putInt(serverName.getLength());
                if (serverName.getData() != null) {
                    byteBuffer.put(serverName.getData());
                }
                return byteBuffer.array();
            }
            case 2: {
                int capacity = 4 * 5;
                int deliveryMode = header.getDeliveryMode();
                int contentType = header.getContentType();
                int contentEncoding = header.getContentEncoding();
                int isStop = header.getStopable();

                StringSerialize replyTo = new StringSerialize(header.getReplyTo());
                capacity += replyTo.getCapacity();
                StringSerialize correlationID = new StringSerialize(header.getCorrelationID());
                capacity += correlationID.getCapacity();
                StringSerialize messageID = new StringSerialize(header.getMessageID());
                capacity += messageID.getCapacity();

                StringSerialize serverName = new StringSerialize(header.getServerName());
                capacity += serverName.getCapacity();

                StringSerialize exceptionContent = new StringSerialize(header.getExceptionContent());
                capacity += exceptionContent.getCapacity();

                ByteBuffer byteBuffer = ByteBuffer.allocate(capacity);
                byteBuffer.putInt(version);
                byteBuffer.putInt(deliveryMode);
                byteBuffer.putInt(contentType);
                byteBuffer.putInt(contentEncoding);
                byteBuffer.putInt(isStop);

                byteBuffer.putInt(replyTo.getLength());
                if (replyTo.getData() != null) {
                    byteBuffer.put(replyTo.getData());
                }

                byteBuffer.putInt(correlationID.getLength());
                if (correlationID.getData() != null) {
                    byteBuffer.put(correlationID.getData());
                }

                byteBuffer.putInt(messageID.getLength());
                if (messageID.getData() != null) {
                    byteBuffer.put(messageID.getData());
                }

                byteBuffer.putInt(serverName.getLength());
                if (serverName.getData() != null) {
                    byteBuffer.put(serverName.getData());
                }

                byteBuffer.putInt(exceptionContent.getLength());
                if (exceptionContent.getData() != null) {
                    byteBuffer.put(exceptionContent.getData());
                }

                return byteBuffer.array();
            }
            default:
                return null;
        }
    }

    public static Header toHeader(byte[] data) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(data);
        Header header = new Header();
        int version = byteBuffer.getInt();
        header.setVersion(version);
        switch (version) {
            case 1: {
                header.setDeliveryMode(byteBuffer.getInt());
                header.setContentType(byteBuffer.getInt());
                header.setContentEncoding(byteBuffer.getInt());

                header.setReplyTo(getString(byteBuffer));
                header.setCorrelationID(getString(byteBuffer));
                header.setMessageID(getString(byteBuffer));
                header.setServerName(getString(byteBuffer));
                return header;
            }
            case 2: {
                header.setDeliveryMode(byteBuffer.getInt());
                header.setContentType(byteBuffer.getInt());
                header.setContentEncoding(byteBuffer.getInt());
                header.setStopable(byteBuffer.getInt());

                header.setReplyTo(getString(byteBuffer));
                header.setCorrelationID(getString(byteBuffer));
                header.setMessageID(getString(byteBuffer));
                header.setServerName(getString(byteBuffer));
                header.setExceptionContent(getString(byteBuffer));
                return header;
            }
            default:
                return null;
        }
    }

    private static String getString(ByteBuffer byteBuffer) {
        int length = byteBuffer.getInt();
        if (length == -1) {
            return null;
        } else {
            byte[] data = new byte[length];
            byteBuffer.get(data);
            String content = new String(data, Charset.forName("UTF-8"));
            return content;
        }
    }

    public boolean isStop() {
        return this.stopable == HeaderConstant.CHAIN_STOPABLE;
    }

    public void setStop() {
        this.setStopable(HeaderConstant.CHAIN_STOPABLE);
    }

    @Override
    public String toString() {
        return "Header{" +
                "version=" + version +
                ", deliveryMode=" + deliveryMode +
                ", contentType=" + contentType +
                ", contentEncoding=" + contentEncoding +
                ", stopable=" + stopable +
                ", replyTo='" + replyTo + '\'' +
                ", correlationID='" + correlationID + '\'' +
                ", messageID='" + messageID + '\'' +
                ", serverName='" + serverName + '\'' +
                ", exceptionContent='" + exceptionContent + '\'' +
                '}';
    }
}
