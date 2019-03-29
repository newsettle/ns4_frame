package com.creditease.ns.log.converter;

import ch.qos.logback.classic.pattern.ThrowableProxyConverter;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.StackTraceElementProxy;
import ch.qos.logback.classic.spi.ThrowableProxyUtil;
import ch.qos.logback.core.CoreConstants;
import com.creditease.ns.log.LogConstants;
import com.creditease.ns.log.LogKey;


public class NSThrowableConvert extends ThrowableProxyConverter {

    @Override
    protected String throwableProxyToString(IThrowableProxy tp) {
        StringBuilder sb = new StringBuilder(BUILDER_CAPACITY);

        recursiveAppend(sb, null, ThrowableProxyUtil.REGULAR_EXCEPTION_INDENT, tp);

        return sb.toString();
    }

    private void recursiveAppend(StringBuilder sb, String prefix, int indent, IThrowableProxy tp) {
        if(tp == null)
            return;
        subjoinFirstLine(sb, prefix, indent, tp);
        sb.append(CoreConstants.LINE_SEPARATOR);
        subjoinSTEPArray(sb, indent, tp);
        IThrowableProxy[] suppressed = tp.getSuppressed();
        if(suppressed != null) {
            for(IThrowableProxy current : suppressed) {
                recursiveAppend(sb, CoreConstants.SUPPRESSED, indent + ThrowableProxyUtil.SUPPRESSED_EXCEPTION_INDENT, current);
            }
        }
        recursiveAppend(sb, CoreConstants.CAUSED_BY, indent, tp.getCause());
    }


    private void subjoinFirstLine(StringBuilder buf, String prefix, int indent, IThrowableProxy tp) {
        ThrowableProxyUtil.indent(buf, indent - 1);


        buf.append(LogConstants.PART_SPLIT);
        if (prefix != null) {
            buf.append(prefix);
        }
        subjoinExceptionMessage(buf, tp);
    }

    private void subjoinExceptionMessage(StringBuilder buf, IThrowableProxy tp) {
        buf.append(tp.getClassName()).append(": ").append(tp.getMessage());
    }

    @Override
    public void subjoinSTEPArray(StringBuilder buf, int indent, IThrowableProxy tp) {
        String totalKey = LogKey.getTotalKey();
        StackTraceElementProxy[] stepArray = tp.getStackTraceElementProxyArray();
        int commonFrames = tp.getCommonFrames();

        boolean unrestrictedPrinting = Integer.MAX_VALUE > stepArray.length;


        int maxIndex = (unrestrictedPrinting) ? stepArray.length : Integer.MAX_VALUE;
        if (commonFrames > 0 && unrestrictedPrinting) {
            maxIndex -= commonFrames;
        }

        for (int i = 0; i < maxIndex; i++) {
            buf.append(totalKey);
            ThrowableProxyUtil.indent(buf, indent);
            buf.append(stepArray[i]);
            extraData(buf, stepArray[i]); // allow other data to be added
            buf.append(CoreConstants.LINE_SEPARATOR);
        }

        if (commonFrames > 0 && unrestrictedPrinting) {
            buf.append(totalKey);
            ThrowableProxyUtil.indent(buf, indent);
            buf.append("... ").append(tp.getCommonFrames()).append(
                    " common frames omitted").append(CoreConstants.LINE_SEPARATOR);
        }
    }

}
