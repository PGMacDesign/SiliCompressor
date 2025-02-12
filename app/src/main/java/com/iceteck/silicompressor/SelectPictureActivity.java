package com.iceteck.silicompressor;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.iceteck.silicompressorr.CompressionException;
import com.iceteck.silicompressorr.FileUtils;
import com.iceteck.silicompressorr.SiliCompressor;
import com.iceteck.silicompressorr.VideoConversionProgressListener;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class SelectPictureActivity extends AppCompatActivity {

    public static final String LOG_TAG = SelectPictureActivity.class.getSimpleName();

    public static final String FILE_PROVIDER_AUTHORITY = ".silicompressor.provider";
    private static final int REQUEST_TAKE_CAMERA_PHOTO = 1;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_STORAGE = 1;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_STORAGE_VID = 2;
    private static final int REQUEST_TAKE_VIDEO = 200;
    private static final int REQUEST_GALLERY_VIDEO_RETRIEVE = 202;
    private static final int TYPE_IMAGE = 1;
    private static final int TYPE_VIDEO = 2;
    private static final int TYPE_VIDEO2 = 3;

    String mCurrentPhotoPath;
    Uri capturedUri = null;
    Uri compressUri = null;
    ImageView imageView;
    TextView picDescription;
    private ImageView videoImageView;
    private ImageView videoImageView2;
    LinearLayout compressionMsg;
    private EditText et;
    private ProgressBar progressBar;
    private TextView progress_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_picture);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.progressBar = (ProgressBar) findViewById(R.id.progressBar);
        this.progress_tv = (TextView) findViewById(R.id.progress_tv);
        imageView = (ImageView) findViewById(R.id.photo);
	    videoImageView2 = (ImageView) findViewById(R.id.videoImageView2);
        videoImageView = (ImageView) findViewById(R.id.videoImageView);
	    et = (EditText) findViewById(R.id.et);
        picDescription = (TextView) findViewById(R.id.pic_description);
        compressionMsg = (LinearLayout) findViewById(R.id.compressionMsg);

        this.et.setText("50.00");
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermissions(TYPE_IMAGE);
            }
        });

        videoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestPermissions(TYPE_VIDEO);
            }
        });
        
        this.videoImageView2.setOnClickListener(new View.OnClickListener() {
	        @Override
	        public void onClick(View v) {
				requestPermissions(TYPE_VIDEO2);
	        }
        });
    }

    /**
     * Request Permission for writing to External Storage in 6.0 and up
     */
    private void requestPermissions(int mediaType) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (mediaType == TYPE_IMAGE) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_STORAGE_VID);
            }

        } else {
            if (mediaType == TYPE_IMAGE) {
                // Want to compress an image
                dispatchTakePictureIntent();
            } else if (mediaType == TYPE_VIDEO) {
                // Want to compress a video
                dispatchTakeVideoIntent();
            } else if (mediaType == TYPE_VIDEO2){
            	openVideoGalleryIntent();
            }

        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    dispatchTakePictureIntent();
                } else {
                    Toast.makeText(this, "You need to enable the permission for External Storage Write" +
                            " to test out this library.", Toast.LENGTH_LONG).show();
                    return;
                }
                break;
            }
            case MY_PERMISSIONS_REQUEST_WRITE_STORAGE_VID: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    dispatchTakeVideoIntent();
                } else {
                    Toast.makeText(this, "You need to enable the permission for External Storage Write" +
                            " to test out this library.", Toast.LENGTH_LONG).show();
                    return;
                }
                break;
            }
            default:
        }
    }

    private File createMediaFile(int type) throws IOException {

        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = (type == TYPE_IMAGE) ? "JPEG_" + timeStamp + "_" : "VID_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                type == TYPE_IMAGE ? Environment.DIRECTORY_PICTURES : Environment.DIRECTORY_MOVIES);
        File file = File.createTempFile(
                fileName,  /* prefix */
                type == TYPE_IMAGE ? ".jpg" : ".mp4",         /* suffix */
                storageDir      /* directory */
        );

        // Get the path of the file created
        mCurrentPhotoPath = file.getAbsolutePath();
        Log.d(LOG_TAG, "mCurrentPhotoPath: " + mCurrentPhotoPath);
        return file;
    }

    private void dispatchTakePictureIntent() {
        /*Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");*/


        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createMediaFile(TYPE_IMAGE);
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.d(LOG_TAG, "Error occurred while creating the file");

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {

                // Get the content URI for the image file
                capturedUri = FileProvider.getUriForFile(this,
                        getPackageName() + FILE_PROVIDER_AUTHORITY,
                        photoFile);

                Log.d(LOG_TAG, "Log1: " + String.valueOf(capturedUri));

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, capturedUri);

                startActivityForResult(takePictureIntent, REQUEST_TAKE_CAMERA_PHOTO);

            }
        }
    }


    private void dispatchTakeVideoIntent() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        takeVideoIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            try {

                takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10);
                takeVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                capturedUri = FileProvider.getUriForFile(this,
                        getPackageName() + FILE_PROVIDER_AUTHORITY,
                        createMediaFile(TYPE_VIDEO));

                takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, capturedUri);
                Log.d(LOG_TAG, "VideoUri: " + capturedUri.toString());
                startActivityForResult(takeVideoIntent, REQUEST_TAKE_VIDEO);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


    }
	
	/**
	 * Open the intent to grab a video from the gallery
	 */
	private void openVideoGalleryIntent(){
		try {
			Intent intent2 = new Intent(Intent.ACTION_PICK);
			intent2.setType("video/*");
			intent2.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(Intent.createChooser(intent2, "Select Video"), REQUEST_GALLERY_VIDEO_RETRIEVE);
		} catch (Exception e){e.printStackTrace();}
    }
    
    // Method which will process the captured image
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
	
	    String str = et.getText().toString();
	    Float amountToSet = 0.5F;
	    try {
			amountToSet = Float.parseFloat(str);
			while (amountToSet > 1){
				amountToSet = amountToSet / 100;
			}
	    } catch (Exception e){}
	    if(amountToSet == null){
	    	amountToSet = 0.5F;
	    }
	    
        //verify if the image was gotten successfully
        if (requestCode == REQUEST_TAKE_CAMERA_PHOTO && resultCode == Activity.RESULT_OK) {


            new ImageCompressionAsyncTask(this).execute(mCurrentPhotoPath,
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Silicompressor/images");


        } else if (requestCode == REQUEST_TAKE_VIDEO && resultCode == RESULT_OK) {
        	if(data != null) {
		        if (data.getData() != null) {
			        //create destination directory
			        File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES) + "/Silicompressor/videos");
			        if (f.mkdirs() || f.isDirectory())
				        //compress and output new video specs
				        new VideoCompressAsyncTask(this).execute(mCurrentPhotoPath, f.getPath());
			
		        }
	        } else {
        		if(capturedUri != null){
			        //create destination directory
			        File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES) + "/Silicompressor/videos");
			        if (f.mkdirs() || f.isDirectory())
				        //compress and output new video specs
				        new VideoCompressAsyncTask(this, amountToSet).execute(mCurrentPhotoPath, f.getPath());
		        }
	        }
        } else if (requestCode == REQUEST_GALLERY_VIDEO_RETRIEVE && resultCode == RESULT_OK){
        	if(data != null){
		        Uri videoUri = data.getData();
		        
		        try {
			        String filePath = FileUtils.getPath(SelectPictureActivity.this, videoUri);
			        String newPath = filePath.replace(".mp4", "_Compressed.mp4");
	
			        new VideoCompressAsyncTask(this, amountToSet).execute(filePath, newPath);
		        } catch (Exception e){
		        	e.printStackTrace();
		        }
	        }
        }

    }

    class ImageCompressionAsyncTask extends AsyncTask<String, Void, String> {

        Context mContext;

        public ImageCompressionAsyncTask(Context context) {
            mContext = context;
        }

        @Override
        protected String doInBackground(String... params) {

            String filePath = SiliCompressor.with(mContext).compress(params[0], new File(params[1]));
            return filePath;


            /*
            Bitmap compressBitMap = null;
            try {
                compressBitMap = SiliCompressor.with(mContext).getCompressBitmap(params[0], true);
                return compressBitMap;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return compressBitMap;

            */
        }

        @Override
        protected void onPostExecute(String s) {
            /*
            if (null != s){
                imageView.setImageBitmap(s);
                int compressHieght = s.getHeight();
                int compressWidth = s.getWidth();
                float length = s.getByteCount() / 1024f; // Size in KB;

                String text = String.format("Name: %s\nSize: %fKB\nWidth: %d\nHeight: %d", "ff", length, compressWidth, compressHieght);
                picDescription.setVisibility(View.VISIBLE);
                picDescription.setText(text);
            }
            */

            File imageFile = new File(s);
            compressUri = Uri.fromFile(imageFile);
            //FileProvider.getUriForFile(mContext, mContext.getApplicationContext().getPackageName()+ FILE_PROVIDER_EXTENTION, imageFile);


            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), compressUri);
                imageView.setImageBitmap(bitmap);

                String name = imageFile.getName();
                float length = imageFile.length() / 1024f; // Size in KB
                int compressWidth = bitmap.getWidth();
                int compressHieght = bitmap.getHeight();
                String text = String.format(Locale.US, "Name: %s\nSize: %fKB\nWidth: %d\nHeight: %d", name, length, compressWidth, compressHieght);
                picDescription.setVisibility(View.VISIBLE);
                picDescription.setText(text);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
	
    class VideoCompressAsyncTask extends AsyncTask<String, Float, String> {

        Context mContext;
	    Float amountToCompressToLocal;

        public VideoCompressAsyncTask(Context context) {
            mContext = context;
            this.amountToCompressToLocal = null;
        }
	
	    public VideoCompressAsyncTask(Context context, float percentToCompressDownToLocal) {
		    mContext = context;
		    this.amountToCompressToLocal = percentToCompressDownToLocal;
	    }

	    
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            imageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_photo_camera_white_48px));
            compressionMsg.setVisibility(View.VISIBLE);
            picDescription.setVisibility(View.GONE);
        }
	
	    @Override
	    protected void onProgressUpdate(Float... values) {
        	if(values == null){
        		return;
	        }
        	if(values.length <= 0){
        		return;
	        }
        	if(true) { //Flip to false to stop printing here
		        progressBar.setIndeterminate(false);
		        try {
		        	if(values != null) {
		        		if(values[0] != null) {
		        			float flt = ((values[0]) * 100);
					        Log.d("SelectPictureActivity", "Progress Complete: " + (flt) + "%");
					        progressBar.setProgress((int) flt);
					        progress_tv.setText("Progress: " + ((int) flt) + "%");
					        
					        if(flt > 40){
					        	if(false) { //Flip this to true to foce manual cancel when progress hits 40%
							        Log.d("1", "Attempting manual cancel of progress conversion @ >= 40%");
							        SiliCompressor.with(this.mContext).cancelVideoCompression();
						        }
					        }
				        }
			        }
		        } catch (Exception e){
		        	e.printStackTrace();
		        }
	        } else {
		        progressBar.setIndeterminate(true);
	        }
		    super.onProgressUpdate(values);
	    }
	
	    @Override
        protected String doInBackground(String... paths) {
            String filePath = null;
            Log.d("d", "Do in background, amount to compress local == " + amountToCompressToLocal);
            if(amountToCompressToLocal == null){
            	amountToCompressToLocal = 0.5F;
            }
            if(amountToCompressToLocal < 0.0 || amountToCompressToLocal > 1.0){
            	amountToCompressToLocal = 0.5F;
            }
            try {
            	//Old Method
//                filePath = SiliCompressor.with(mContext).compressVideo(mContext, paths[0], paths[1]);
	            
	            //New Method
	            try {
		            filePath = SiliCompressor.with(mContext, true).compressVideo(new VideoConversionProgressListener() {
			            @Override
			            public void videoConversionProgressed(float progressPercentage, Long estimatedNumberOfMillisecondsLeft) {
				            publishProgress(progressPercentage);
				            triggerEstimatedMillisecondsLeft(estimatedNumberOfMillisecondsLeft);
			            }
		            }, paths[0], paths[1], this.amountToCompressToLocal);
	            } catch (CompressionException ce){
	            	ce.printStackTrace();
	            }
	            
            } catch (ArrayIndexOutOfBoundsException e) {
                e.printStackTrace();
            }
            return (filePath == null) ? "" : filePath;

        }


        @Override
        protected void onPostExecute(String compressedFilePath) {
            super.onPostExecute(compressedFilePath);
            File imageFile = new File(compressedFilePath);
            float length = imageFile.length() / 1024f; // Size in KB
            String value;
            if (length >= 1024)
                value = length / 1024f + " MB";
            else
                value = length + " KB";
            String text = String.format(Locale.US, "%s\nName: %s\nSize: %s", getString(R.string.video_compression_complete), imageFile.getName(), value);
            compressionMsg.setVisibility(View.GONE);
            picDescription.setVisibility(View.VISIBLE);
            picDescription.setText(text);
            Log.i("Silicompressor", "Path: " + compressedFilePath);
        }
    }

    private void triggerEstimatedMillisecondsLeft(Long est){
		if(est == null){
			return;
		}
		Log.d("1", "Estimated Number of MilliSeconds left: " + est);
    }

}
