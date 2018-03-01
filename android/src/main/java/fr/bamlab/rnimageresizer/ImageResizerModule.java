package fr.bamlab.rnimageresizer;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;

import java.io.File;
import java.io.IOException;

/**
 * Created by almouro on 19/11/15.
 */
class ImageResizerModule extends ReactContextBaseJavaModule {
    private Context context;

    public ImageResizerModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.context = reactContext;
    }

    /**
     * @return the name of this module. This will be the name used to {@code require()} this module
     * from javascript.
     */
    @Override
    public String getName() {
        return "ImageResizerAndroid";
    }

    @ReactMethod
    public void resizeImageToCertainSize(String imagePath, int targetSizeInBytes, int newWidth, String compressFormatString,
                                         int quality, int rotation, String outputPath, final Callback successCb, final Callback failureCb) {

        Uri imageUri = Uri.parse(imagePath);
        Bitmap bitmap = ImageResizer.getIMGSizeFromUri(imageUri);
        Bitmap.CompressFormat compressFormat = Bitmap.CompressFormat.valueOf(compressFormatString);
        int numOfBytes = bitmap.getByteCount();

        WritableMap response = Arguments.createMap();

        //if the existing image is smaller than the target size, just return the image
        if (numOfBytes < targetSizeInBytes) {
            response.putBoolean("isResized", false);
            response.putDouble("size", numOfBytes);
            // Invoke success
            successCb.invoke(response);
        } else {
            //if the file is bigger than the target size

            //calculate the ratio
            int ratio = bitmap.getWidth() / newWidth;
            //calculate the new Height
            int newHeight = bitmap.getHeight() / ratio;

            try {
                File resizedImage = ImageResizer.createResizedImage(this.context, imageUri, newWidth,
                        newHeight, compressFormat, quality, rotation, outputPath);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    @ReactMethod
    public void createResizedImage(String imagePath, int newWidth, int newHeight, String compressFormat,
                                   int quality, int rotation, String outputPath, final Callback successCb, final Callback failureCb) {
        try {
            createResizedImageWithExceptions(imagePath, newWidth, newHeight, compressFormat, quality,
                    rotation, outputPath, successCb, failureCb);
        } catch (IOException e) {
            failureCb.invoke(e.getMessage());
        }
    }

    private void createResizedImageWithExceptions(String imagePath, int newWidth, int newHeight,
                                                  String compressFormatString, int quality, int rotation, String outputPath,
                                                  final Callback successCb, final Callback failureCb) throws IOException {
        Bitmap.CompressFormat compressFormat = Bitmap.CompressFormat.valueOf(compressFormatString);
        Uri imageUri = Uri.parse(imagePath);

        File resizedImage = ImageResizer.createResizedImage(this.context, imageUri, newWidth,
                newHeight, compressFormat, quality, rotation, outputPath);

        // If resizedImagePath is empty and this wasn't caught earlier, throw.
        if (resizedImage.isFile()) {
            WritableMap response = Arguments.createMap();
            response.putString("path", resizedImage.getAbsolutePath());
            response.putString("uri", Uri.fromFile(resizedImage).toString());
            response.putString("name", resizedImage.getName());
            response.putDouble("size", resizedImage.length());
            // Invoke success
            successCb.invoke(response);
        } else {
            failureCb.invoke("Error getting resized image path");
        }
    }
}
