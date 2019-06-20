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

5) Adjusted the Sample Activity, SelectPictureActivity, to add an option to select from Gallery. This will also allow larger files to be converted as opposed to limiting it to 10 second clips.  

6) Added in a boolean flag to allow the option to turn off / on logging so that the dev can ignore it if they don't want to see it (or flip it to false on production)  



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

Usage
--------
To effectively use this library, you must make sure you have added the following permission to your project.
```java
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
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
```groovy
implementation 'com.iceteck.silicompressorr:silicompressor:2.2.2'
```

##### Maven
```xml
<dependency>
  <groupId>com.iceteck.silicompressorr</groupId>
  <artifactId>silicompressor</artifactId>
  <version>2.2.2</version>
  <type>aar</type>
</dependency>
```
Snapshots of the development version are available in [Sonatype's `snapshots` repository][snap].

License
--------
Copyright 2016 [Teyou Toure Nathan][toure]

Licensed under the Apache License, Version 2.0 (the "License") and GNU General Public License v2.0;

you may not use this file except in compliance with the Licenses.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0 and https://www.gnu.org/licenses/old-licenses/gpl-2.0.en.html

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.


[snap]:  https://oss.sonatype.org/content/repositories/snapshots
[toure]:  https://www.linkedin.com/in/toure-nathan/
[Void Canvas]: http://voidcanvas.com/whatsapp-like-image-compression-in-android/
[Jorge E. Hernandez (@lalongooo)]: https://github.com/lalongooo
