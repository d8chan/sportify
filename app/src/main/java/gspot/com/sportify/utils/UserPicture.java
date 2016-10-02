package gspot.com.sportify.utils;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidObjectException;

/**
 * Author: Anshul with help from stack overflow
 *
 * Creates resized images without exploding memory. Uses the method described in android
 * documentation concerning bitmap allocation, which is to subsample the image to a smaller size,
 * close to some expected size. This is required because the android standard library is unable to
 * create a reduced size image from an image file using memory comparable to the final size (and
 * loading a full sized multi-megapixel picture for processing may exceed application memory budget).
 *
 * implementation by user @hdante
 * http://stackoverflow.com/users/1797000/hdante
 */

public class UserPicture {

    private static final String LOG_TAG = UserPicture.class.getSimpleName();

    //TODO: These will have to be dynamically created for the profile image view
    static int MAX_WIDTH = 250;
    static int MAX_HEIGHT = 250;

    Uri uri;
    ContentResolver resolver;
    String path;
    // Think of this as a 3x3 matrix that can control how pictures are flipped,
    // rotated, etc. It is mainly used to maintain the original orientation in
    // this class.
    Matrix orientation;

    // These will hold the width and height of the picture
    int storedHeight;
    int storedWidth;

    // Gets the uri and resolved passed in
    public UserPicture(Uri uri, ContentResolver resolver) {
        this.uri = uri;
        this.resolver = resolver;
    }

    /* getInformation()
     * gets orientation in 2 different ways depending on which "Gallery" was used
     */
    private boolean getInformation() throws IOException {
        if (getInformationFromMediaDatabase())
            return true;

        if (getInformationFromFileSystem())
            return true;

        return false;
    }

    /* Support for gallery apps and remote ("picasa") images */
    private boolean getInformationFromMediaDatabase() {
        // Creates a projection to be used when querying
        String[] fields = {MediaStore.Images.Media.DATA, MediaStore.Images.ImageColumns.ORIENTATION};
        // Attempt to find the picture by uri and get the fields specified
        Cursor cursor = resolver.query(uri, fields, null, null, null);

        // Only continue if the uri was actually found in this type of gallery
        if (cursor == null)
            return false;

        // Move the cursor to the beginning
        cursor.moveToFirst();

        // Get the orientation of the picture
        path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        int orientation = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.ImageColumns.ORIENTATION));

        // Set this.orientation to a new matrix and set the values to correspond
        // to the picture's orientation
        this.orientation = new Matrix();
        this.orientation.setRotate(orientation);

        cursor.close();

        return true;
    }

    /* Support for file managers and dropbox */
    private boolean getInformationFromFileSystem() throws IOException {
        // Get the path's uri
        path = uri.getPath();

        // there is no path...
        if (path == null)
            return false;

        // ExifInterfaces are needed to interact/get data from galleries that
        // are formatted like file managers
        // I don't have a full understanding of it but I think that's all you
        // need to know
        // Creates an Exif that connects to the path
        ExifInterface exif = new ExifInterface(path);

        // Gets the orientation of the picture using the picture's data and
        // set it to a default value otherwise
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL);

        // New matrix
        this.orientation = new Matrix();

        /*
         * Switch statement based upon the orientation that flips, rotates, etc
         * the Matrix to reflect the picture's orientation
         */
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                /* Identity matrix */
                break;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                this.orientation.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                this.orientation.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                this.orientation.setScale(1, -1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                this.orientation.setRotate(90);
                this.orientation.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                this.orientation.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                this.orientation.setRotate(-90);
                this.orientation.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                this.orientation.setRotate(-90);
                break;
        }

        return true;
    }

    /* getStoredDimensions()
     * Gets the height and width of the picture
     * */
    private boolean getStoredDimensions() throws IOException {
        // Opens up an input stream using the uri
        InputStream input = resolver.openInputStream(uri);

        // Create some BitmapFactory options and use it to decode the stream
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(resolver.openInputStream(uri), null, options);

        /* The input stream could be reset instead of closed and reopened if it were possible
           to reliably wrap the input stream on a buffered stream, but it's not possible because
           decodeStream() places an upper read limit of 1024 bytes for a reset to be made (it calls
           mark(1024) on the stream). */
        if (input != null) {
            input.close();
        }

        // We have an issue if the height or width is not >0
        if (options.outHeight <= 0 || options.outWidth <= 0)
            return false;

        // Get the height and width
        storedHeight = options.outHeight;
        storedWidth = options.outWidth;

        return true;
    }

    /* getBitmap()
     * Gets, shrinks down, and returns the user's picture.
     * */
    public Bitmap getBitmap() throws IOException {
        if (!getInformation())
            throw new FileNotFoundException();

        if (!getStoredDimensions())
            throw new InvalidObjectException(null);

        // Create a rectangle with the picture's dimensions
        RectF rect = new RectF(0, 0, storedWidth, storedHeight);
        // Ensure the rectangle's orientaiton is the same as picture's
        orientation.mapRect(rect);

        // Get the rectangle's height and width
        double width = (double) rect.width();
        double height = (double) rect.height();
        // This will help scale the picture
        double subSample = 1;

        /* Shrink the picture by scaling the width and height down and
         * increasing the subsample. Using 1.2 to try to make the picture as
         * big as possible within the size of the image view.
         */
        while (width > (double)MAX_WIDTH || height > (double)MAX_HEIGHT) {
            width /= 1.2;
            height /= 1.2;
            subSample *= 1.2;
        }

        // We have an issue if the width or height is 0, basically the picture is
        // non-existant
        if (width == 0 || height == 0)
            throw new InvalidObjectException(null);

        // Create a BitmapFactory.Options object that changes the sample size to
        // the size that we just determined
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = (int)subSample;

        // Get the bitmap (picture), scaled down appropriately
        Bitmap subSampled = BitmapFactory.decodeStream(resolver.openInputStream(uri),
                null,
                options);

        Bitmap picture;

        // If the orientation is incorrect, create a new bitmap with the orientation
        // corrected
        if (!orientation.isIdentity()) {
            picture = Bitmap.createBitmap(subSampled, 0, 0, options.outWidth, options.outHeight,
                    orientation, false);
            subSampled.recycle();
        } else {
            // No re-orientation required, the picture is the subSampled bitmap
            picture = subSampled;
        }

        Log.v(LOG_TAG, picture.toString());
        return picture;
    }

    /*
     * Takes in a bitmap and converts it to a base 64 string, so we
     * can store it in firebase
     */
    public static String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    /**
     * Takes a base 64 string and converts it to a bitmap
     * @param encodedString
     * @return bitmap (from given string)
     */
    public static Bitmap StringToBitMap(String encodedString){
        try {
            byte [] encodeByte=Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap=BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }


}
