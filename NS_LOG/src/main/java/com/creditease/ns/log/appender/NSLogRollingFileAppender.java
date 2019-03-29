package com.creditease.ns.log.appender;

import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.rolling.*;
import ch.qos.logback.core.rolling.helper.CompressionMode;
import ch.qos.logback.core.rolling.helper.FileNamePattern;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

import static ch.qos.logback.core.CoreConstants.CODES_URL;

public class NSLogRollingFileAppender<E> extends FileAppender<E> {
    File currentlyActiveFile;
    TriggeringPolicy<E> triggeringPolicy;
    RollingPolicy rollingPolicy;

    static private String RFA_NO_TP_URL = CODES_URL + "#rfa_no_tp";
    static private String RFA_NO_RP_URL = CODES_URL + "#rfa_no_rp";
    static private String COLLISION_URL = CODES_URL + "#rfa_collision";

    public void start() {
        if (triggeringPolicy == null) {
            addWarn("No TriggeringPolicy was set for the RollingFileAppender named "
                    + getName());
            addWarn("For more information, please visit " + RFA_NO_TP_URL);
            return;
        }

        // we don't want to void existing log files
        if (!append) {
            addWarn("Append mode is mandatory for RollingFileAppender");
            append = true;
        }

        if (rollingPolicy == null) {
            addError("No RollingPolicy was set for the RollingFileAppender named "
                    + getName());
            addError("For more information, please visit " + RFA_NO_RP_URL);
            return;
        }

        // sanity check for http://jira.qos.ch/browse/LOGBACK-796
        if (fileAndPatternCollide()) {
            addError("File property collides with fileNamePattern. Aborting.");
            addError("For more information, please visit " + COLLISION_URL);
            return;
        }

        if (isPrudent()) {
            if (rawFileProperty() != null) {
                addWarn("Setting \"File\" property to null on account of prudent mode");
                setFile(null);
            }
            if (rollingPolicy.getCompressionMode() != CompressionMode.NONE) {
                addError("Compression is not supported in prudent mode. Aborting");
                return;
            }
        }

        currentlyActiveFile = new File(getFile());
        addInfo("Active log file name: " + getFile());
    }

    public void realStart() {
        this.start();
        super.start();
    }

    private boolean fileAndPatternCollide() {
        if (triggeringPolicy instanceof RollingPolicyBase) {
            final RollingPolicyBase base = (RollingPolicyBase) triggeringPolicy;
            Field fileNamePatternField;
            FileNamePattern fileNamePattern = null;

            try {
            	Class cl = findRollingPolicyBase(base.getClass().getSuperclass());
                fileNamePatternField = cl.getDeclaredField("fileNamePattern");
                fileNamePatternField.setAccessible(true);
                fileNamePattern = (FileNamePattern) fileNamePatternField.get(base);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // no use checking if either fileName or  fileNamePattern are null
            if (fileNamePattern != null && fileName != null) {
                String regex = fileNamePattern.toRegex();
                return fileName.matches(regex);
            }
        }
        return false;
    }

    private Class findRollingPolicyBase(Class cl)
    {
    	if (cl.getCanonicalName().equals(Object.class.getCanonicalName())) 
		{
			return null;
		}
    	
    	if (!cl.getCanonicalName().equals(RollingPolicyBase.class.getCanonicalName())) 
		{
    		cl = cl.getSuperclass();
			return findRollingPolicyBase(cl);
		}
    	else 
		{
			return cl;
		}
    }
    
    @Override
    public void stop() {
        if (rollingPolicy != null) rollingPolicy.stop();
        if (triggeringPolicy != null) triggeringPolicy.stop();
        super.stop();
    }

    @Override
    public void setFile(String file) {
        // http://jira.qos.ch/browse/LBCORE-94
        // allow setting the file name to null if mandated by prudent mode
    	
        if (file != null && ((triggeringPolicy != null) || (rollingPolicy != null))) {
            addError("File property must be set before any triggeringPolicy or rollingPolicy properties");
            addError("Visit " + CODES_URL + "#rfa_file_after for more information");
        }
        super.setFile(file);
    }

    @Override
    public String getFile() {
    	String fileName = rollingPolicy.getActiveFileName();
        return fileName;
    }

    /**
     * Implemented by delegating most of the rollover work to a rolling policy.
     */
    public void rollover() {
        lock.lock();
        try {
            // Note: This method needs to be synchronized because it needs exclusive
            // access while it closes and then re-opens the target file.
            //
            // make sure to close the hereto active log file! Renaming under windows
            // does not work for open files.
        	
            this.closeOutputStream();
            attemptRollover();
            attemptOpenFile();
        } finally {
            lock.unlock();
        }
    }

    private void attemptOpenFile() {
        try {
            // update the currentlyActiveFile LOGBACK-64
            currentlyActiveFile = new File(rollingPolicy.getActiveFileName());

            // This will also close the file. This is OK since multiple close operations are safe.
            this.openFile(rollingPolicy.getActiveFileName());
        } catch (IOException e) {
            addError("setFile(" + fileName + ", false) call failed.", e);
        }
    }

    private void attemptRollover() {
        try {
            rollingPolicy.rollover();
        } catch (RolloverFailure rf) {
            addWarn("RolloverFailure occurred. Deferring roll-over.");
            // we failed to roll-over, let us not truncate and risk data loss
            this.append = true;
        }
    }

    /**
     * This method differentiates RollingFileAppender from its super class.
     */
    @Override
    protected void subAppend(E event) {
        // The roll-over check must precede actual writing. This is the
        // only correct behavior for time driven triggers.

        // We need to synchronize on triggeringPolicy so that only one rollover
        // occurs at a time
        synchronized (triggeringPolicy) {
            if (triggeringPolicy.isTriggeringEvent(currentlyActiveFile, event)) {
                rollover();
            }
        }

        super.subAppend(event);
    }

    public RollingPolicy getRollingPolicy() {
        return rollingPolicy;
    }

    public TriggeringPolicy<E> getTriggeringPolicy() {
        return triggeringPolicy;
    }

    /**
     * Sets the rolling policy. In case the 'policy' argument also implements
     * {@link TriggeringPolicy}, then the triggering policy for this appender is
     * automatically set to be the policy argument.
     *
     * @param policy
     */
    @SuppressWarnings("unchecked")
    public void setRollingPolicy(RollingPolicy policy) {
        rollingPolicy = policy;
        if (rollingPolicy instanceof TriggeringPolicy) {
            triggeringPolicy = (TriggeringPolicy<E>) policy;
        }

    }

    public void setTriggeringPolicy(TriggeringPolicy<E> policy) {
        triggeringPolicy = policy;
        if (policy instanceof RollingPolicy) {
            rollingPolicy = (RollingPolicy) policy;
        }
    }
}
