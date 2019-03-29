package com.creditease.ns.mq.model.serialize;


import java.nio.charset.Charset;

public class StringSerialize {
    private String origString;
    private byte[] data;
    private int length;
    private int capacity;

    public StringSerialize(String origString) {
        this.origString = origString;
        if (origString == null) {
            length = -1;
            capacity = 4;
        } else {
            data = origString.getBytes(Charset.forName("UTF-8"));
            length = data.length;
            capacity = 4 + length;
        }
    }

    public String getOrigString() {
        return origString;
    }

    public void setOrigString(String origString) {
        this.origString = origString;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
}
