package com.iceteck.silicompressorr;

import android.support.annotation.FloatRange;

public interface VideoConversionProgressListener {
	/**
	 * Progress percentage ranging from 0-1 where 0 == 0% and 1 == 100%
	 * @param progressPercentage
	 */
	public void videoConversionProgressed(@FloatRange(from = 0.0, to = 1.0) float progressPercentage);
	
}
