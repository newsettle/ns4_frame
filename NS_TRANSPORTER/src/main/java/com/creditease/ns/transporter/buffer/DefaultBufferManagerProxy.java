package com.creditease.ns.transporter.buffer;

import com.creditease.framework.pojo.ServiceMessage;

import java.util.Collections;
import java.util.Set;

/**
 * Created by liuyang on 2019-02-24.
 *
 * @author liuyang email
 */
public class DefaultBufferManagerProxy implements BufferManager {
    private BufferManager bufferManager;

    public DefaultBufferManagerProxy(BufferManager bufferManager) {
        this.bufferManager = bufferManager;
    }


    public Set<String> getAllQueueNamesForReceiving() {
        DefaultBufferManager defaultBufferManager = (DefaultBufferManager) bufferManager;
        return Collections.unmodifiableSet(defaultBufferManager.getQueueNameToReceiveBuffer().keySet());
    }

    public Set<String> getAllQueueNamesForSending() {
        DefaultBufferManager defaultBufferManager = (DefaultBufferManager) bufferManager;
        return Collections.unmodifiableSet(defaultBufferManager.getQueueNameToSendBuffer().keySet());
    }

    @Override
    public ServiceMessage getFromReceiveBuffer(String queueName, boolean isSync) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putInReceiveBuffer(String queueName, ServiceMessage message) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ServiceMessage getFromSendBuffer(String queueName, boolean isSync) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putInSendBuffer(String queueName, ServiceMessage message) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long sizeOfReceiveBufferOf(String queueName) {
        return bufferManager.sizeOfReceiveBufferOf(queueName);
    }

    @Override
    public long sizeOfSendBufferOf(String queueName) {
        return bufferManager.sizeOfSendBufferOf(queueName);
    }

}
