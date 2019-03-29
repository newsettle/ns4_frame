package ch.qos.logback.core.rolling;

import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;

public class DynamicTimeBasedRollingPolicy<E> extends TimeBasedRollingPolicy<E>{

	@Override
	public String getActiveFileName() {
		return timeBasedFileNamingAndTriggeringPolicy
				.getCurrentPeriodsFileNameWithoutCompressionSuffix();
	}
}
