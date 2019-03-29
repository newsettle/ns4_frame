package com.creditease.ns.dispatcher.community.local.impl;

import com.creditease.framework.exception.NSException;
import com.creditease.framework.pojo.ServiceMessage;
import com.creditease.framework.scope.SystemOutKey;
import com.creditease.framework.scope.SystemRetInfo;
import com.creditease.framework.work.ActionWorker;
import com.creditease.ns.dispatcher.community.local.LocalActionMapping;
import com.creditease.ns.dispatcher.core.ConfigCenter;

import java.io.File;

@LocalActionMapping(server = "dispatcher_check")
public class FileCheckLActionWorker extends ActionWorker {
    private FileCheck fileCheck = new FileCheck(ConfigCenter.getConfig.getDispatcherFileCheck());

    @Override
    public void doWork(ServiceMessage serviceMessage) throws NSException {
        if (fileCheck.checkFile()) {
            serviceMessage.setOut(SystemOutKey.RETURN_CODE, SystemRetInfo.NORMAL);
        } else {
            serviceMessage.setOut(SystemOutKey.RETURN_CODE, SystemRetInfo.CTRL_NOT_FOUND_SEVICE_ERROR);
        }
    }

    private class FileCheck {

        private String filePath;

        public FileCheck(String filePath){
            this.filePath = filePath;
        }

        public String getFilePath() {
            return filePath;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }

        public boolean checkFile() {
            if (filePath == null) {
                return false;
            }
            File file = new File(filePath);
            return file.exists();
        }
    }
}
