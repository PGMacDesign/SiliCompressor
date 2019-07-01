
 [![JitPack](https://jitpack.io/v/pgmacdesign/silicompressor.svg)](https://jitpack.io/#pgmacdesign/silicompressor)


# SiliCompressor
A powerful, flexible and easy to use Video and Image compression library for Android.

## Updates

### 2019 Update

This code was updated by PGMacDesign in June of 2019. Quite a few new features were added and bug fixes were included. The full list can be found below:

1) Removed the requirement that forced videos to be resized into a 360x640 frame if no height and width is specified. Removed the requirement to force a default bitrate of 450,000 on videos where no bitrate is specified. This is fixed via overloaded methods described in the next point.
I left the original methods intact that still utilize the previous / older approach, but my newer methods do not. 

2) Added in multiple overloaded methods that allow for passing in a % to decrease the video size by.   

These are the original 2 methods:
```java

    public String compressVideo(String videoFilePath, String destinationDir) {
    	...
    }
    
    public String compressVideo(String videoFilePath, String destinationDir,
                                int outWidth, int outHeight, int bitrate) {
    	...
    }
```

These are the new methods; each with an overloaded option to pass in the listener. 

```java      
  
    public String compressVideo(@Nullable VideoConversionProgressListener listener,
                                String videoFilePath, String destinationDir) {
        ...
    }

    public String compressVideo(@Nullable VideoConversionProgressListener listener,
                                String videoFilePath, String destinationDir,
                                int outWidth, int outHeight, int bitrate) {
        ...
    }
        
    public String compressVideo(String videoFilePath, String destinationDir,
                                @FloatRange(from = 0.01, to = 0.99) float reduceVideoQualityToPercent)  {
    	...
    }

    public String compressVideo(@Nullable VideoConversionProgressListener listener,
                                String videoFilePath, String destinationDir,
                                @FloatRange(from = 0.01, to = 0.99) float reduceVideoQualityToPercent)  {
    	...
    }

    public String compressVideo(String videoFilePath, String destinationDir,
                                @FloatRange(from = 0.01, to = 0.99) float reduceVideoQualityToPercent,
                                @FloatRange(from = 0.01, to = 0.99) float reduceHeightWidthToPercent) {
    	...
    }
    
    /**
     * @param reduceVideoQualityToPercent Float (0-1), this will reduce the video quality percent to
     *                                    the amount passed. IE, if you pass 0.5, it will halve the
     *                                    bitrate and reduce quality by around 50%. If you pass 0.9,
     *                                    it will reduce the bitrate by 10% and reduce the quality
     *                                    by roughly 10%. If you pass in 0.277 it will reduce the
     *                                    bitrate to 27.7% of the original value and reduce the
     *                                    quality to roughly the same 27.7%.
     *                                    Note that this will maintain the same
     *                                    height and width ratio of the original video
     * @param reduceHeightWidthToPercent  Float (0-1), this will reduce the height / width of the video to
     *                                    the amount passed. IE, if you pass 0.5 and the size of the
     *                                    video is 1080x720, it will halve the sizing and reduce it
     *                                    to 540x360 and reduce quality by around 50%. if you pass 0.9 and the size of the
     *                                    video is 1080x720, it will reduce it
     *                                    to 972x648 and reduce quality by around 10%. if you pass 0.277 and the size of the
     *                                    video is 1080x720, it will reduce it
     *                                    to 299x199 and reduce quality to roughly the same 27.7%.
     *                                    Note that this will maintain the same
     *                                    height and width ratio of the original video
     * @return The Path of the compressed video file
     */
    public String compressVideo(@Nullable VideoConversionProgressListener listener,
                                String videoFilePath, String destinationDir,
                                @FloatRange(from = 0.01, to = 0.99) float reduceVideoQualityToPercent,
                                @FloatRange(from = 0.01, to = 0.99) float reduceHeightWidthToPercent) {
    	...
    }
    
```

3) Added in a callback listener [VideoConversionProgressListener](linktbd) that can be passed into the video conversion process that will pass back progress update value (in floats ranging from 0 - 1) to indicate the progress of the video conversion. 

4) Removed the forced name change that utilized the previous coder's `"VIDEO_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date()) + ".mp4"` String. It will now use whatever you send as the output directory + file.
I left the original methods intact that still utilize the previous / older approach, but my newer methods do not. 
<b> Note! </b> Keep in mind that if you do not have permission to write in said location or are not using a [FileProvider](https://developer.android.com/reference/android/support/v4/content/FileProvider) to define a writing space, this will fail. [Here is a Tutorial](https://www.journaldev.com/23219/android-capture-image-camera-gallery-using-fileprovider) if you are not familiar with it.  

5) Fixed bugs relating to incorrectly flipping Height and Width on videos depending on phone maker (Known with Google Pixel Phones) 

6) Adjusted the Sample Activity, SelectPictureActivity, to add an option to select from Gallery. This will also allow larger files to be converted as opposed to limiting it to 10 second clips.  

7) Added in a boolean flag to allow the option to turn off / on logging so that the dev can ignore it if they don't want to see it (or flip it to false on production)  

8) I changed the core MP4 parser from the original one to [My Custom Forked Version](https://github.com/PGMacDesign/mp4parser) so as to allow for less cluttered logging and more customization. 

#### Misc

I timed a few of the conversions using a Galaxy S9 for reference. 

1) Compressing a 3.70gb mp4 down to 1.85gb (50%) took 600000 milliseconds (600 seconds / 10 minutes). 
This was also true when converting it down to 70mb (2% of total, but passing in 1%) so the total percent to compress to does not adjust the time taken for the compression to occur.   

2) Compressing a 5.00mb mp4 down to 2.50mb (50%) took 2456 milliseconds (2.5 seconds). 
This was also true when converting it down to 500kb (1% of total) so the total percent to compress to does not adjust the time taken for the compression to occur.  

If you are running an older device with less processing power, it may take longer whereas a newer device may convert faster. This is merely here as an example for reference. 

#### Samples

The [SelectPictureActivity](https://github.com/PGMacDesign/SiliCompressor/blob/master/app/src/main/java/com/iceteck/silicompressor/SelectPictureActivity.java) class has samples of how to both take and convert images + videos. 
It also includes an edit text to determine the exact amount to compress a video by as well as a progress bar to demonstrate how to use the progress callback. 
  

Description
--------
#### Image 
It's usually said that "A picture is worth a thousand words". Images adds flair and beauty to our android apps, but we usaully have problems with these images due to thier large size. With SiliCompressor you can now compress and use your images more smoothly.

#### Video
Due to the high resolution of our Smartphone cameras and cameras from other devices, Video files have become large in size and thus difficult for it to be shared with others on social apps, social media and even when we need to upload it on our server. With SiliCompressor you can now compress you video file while maintaining it quality.

Credit
--------
The image compressor part of this project is inspired from [Void Canvas] blog from which the core part of the compressor was done.
For the Video Compression part of this project, credit goes to [Jorge E. Hernandez (@lalongooo)] whose codes was used for the core part of the video compressor module.
The original fork of this library came from [Teyou Toure Nathan](https://github.com/Tourenathan-G5organisation) who wrote nearly all of the [Silicompressor code](https://github.com/Tourenathan-G5organisation/SiliCompressor). I added in a large number of bugfixes as well as updates / improvements, but his code is what most of this is made of. 

Usage
--------
To effectively use this library, you must make sure you have added the following permission to your project.
```java
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
```

#### Compress a video file down using %s
Compress a video file down to 1% of it's original bitrate to reduce the quality but maintain original height and width params
```java
SiliCompressor.with(mContext, true).compressVideo(new VideoConversionProgressListener() {
        @Override
        public void videoConversionProgressed(float progressPercentage) {
			//Log, handle, or use the progress percentage here. Will be between 0.0 and 1.0
        }
}, videoPath, destinationFile, 0.01F);
```

#### Compress a video file down using %s
Compress a video file down to 1% of it's original bitrate and half of its original height * width ratio to reduce the quality + height and width params
```java
SiliCompressor.with(mContext, true).compressVideo(new VideoConversionProgressListener() {
        @Override
        public void videoConversionProgressed(float progressPercentage) {
			//Log, handle, or use the progress percentage here. Will be between 0.0 and 1.0
        }
}, videoPath, destinationFile, 0.01F, 0.5F);
```

#### Compress a video file and return the file path of the new video
```java
String filePath = SiliCompressor.with(Context).compressVideo(videoPath, destinationDirectory);
```

#### Compress an image and return the file path of the new image
```java
String filePath = SiliCompressor.with(Context).compress(imagePath, destinationDirectory);
```

#### Compress an image and return the file path of the new image while deleting the source image
```java
String filePath = SiliCompressor.with(Context).compress(imagePath, destinationDirectory, true);
```

#### Compress an image drawable and return the file path of the new image
```java
String filePath = SiliCompressor.with(Context).compress(R.drawable.icon);
```

#### Compress an image and return the bitmap data of the new image
```java
Bitmap imageBitmap = SiliCompressor.with(Context).getCompressBitmap(imagePath);
```

#### Compress an image and return the bitmap data of the new image while deleting the source image
```java
Bitmap imageBitmap = SiliCompressor.with(Context).getCompressBitmap(imagePath, true);
```


Download
--------
#### Gradle

Include this in your Project-level build.gradle file:
```groovy
allprojects {
    repositories {
        .
        .
        .
        maven { url 'https://jitpack.io' }
    }
}
```

Include this in your Module-level build.gradle file:

```groovy
implementation 'com.github.pgmacdesign:silicompressor:3.0.3'
```


License
--------
Copyright 2019 PGMacDesign

Licensed under the Apache License, Version 2.0 (the "License") and GNU General Public License v2.0;

you may not use this file except in compliance with the Licenses.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0 and https://www.gnu.org/licenses/old-licenses/gpl-2.0.en.html

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.


