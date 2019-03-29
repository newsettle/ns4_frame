package com.creditease.ns.transporter.plugin;

import com.creditease.framework.ext.plugin.NSEnvironment;
import com.creditease.ns.transporter.buffer.BufferManager;

/**
 * Created by liuyang on 2019-02-24.
 *
 * @author liuyang
 */
public class TransporterEnvironment implements NSEnvironment {
    private BufferManager bufferManager;

    public TransporterEnvironment(BufferManager bufferManager) {
        this.bufferManager = bufferManager;
    }

    @Override
    public BufferManager getEnvironmentInfo() {
        return bufferManager;
    }
}
