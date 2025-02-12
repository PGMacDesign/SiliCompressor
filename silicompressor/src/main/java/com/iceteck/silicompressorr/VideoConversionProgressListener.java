package com.iceteck.silicompressorr;

import android.support.annotation.FloatRange;
import android.support.annotation.Nullable;

public interface VideoConversionProgressListener {
	
	/**
	 * Callback to update on progress of the video conversion
	 * @param progressPercentage Progress percentage from 0.0 to 1.0 where 0 == 0% and 1 == 100%
	 * @param estimatedNumberOfMillisecondsLeft Estimated number of milliseconds left; note that
	 *                                          this may be zero depending on the number of iterations
	 *                                          that have been processed.
	 */
	public void videoConversionProgressed(@FloatRange(from = 0.0, to = 1.0) float progressPercentage,
	                                      @Nullable Long estimatedNumberOfMillisecondsLeft);
	
}
