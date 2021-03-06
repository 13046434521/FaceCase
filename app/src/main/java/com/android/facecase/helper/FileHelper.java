package com.android.facecase.helper;

import android.graphics.Bitmap;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;
import android.util.Log;

import com.imi.sdk.faceid.BuildConfig;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

public class FileHelper {
    private static final String TAG = FileHelper.class.getSimpleName();
    private static String mSDCardFolderPath;
    private static String mFaceImageFolderPath;
    private static String mFaceModelFolderPath;
    private static String mFaceImiBinFolderPath;
    private static String mFaceTestFolderPath;

    private FileHelper() {
        init();
    }

    public static FileHelper getInstance() {
        return FileHelperHolder.sFileHelper;
    }

    public void init() {
        mSDCardFolderPath = getSDCardFolderPath();
        mFaceImageFolderPath = getFaceImageFolderPath();
        mFaceModelFolderPath = getFaceModelFolderPath();
        mFaceImiBinFolderPath = getFaceImiBinFolderPath();
        mFaceTestFolderPath = getFaceTestFolderPath();
    }


    public String getSDCardFolderPath() {
        if (TextUtils.isEmpty(mSDCardFolderPath)) {
            mSDCardFolderPath = Environment.getExternalStorageDirectory().getPath() + "/IMIFace/";
        }
        mkdirs(mSDCardFolderPath);

        return mSDCardFolderPath;
    }

    public String getFaceImageFolderPath() {
        if (TextUtils.isEmpty(mFaceImageFolderPath)) {
            mFaceImageFolderPath = getSDCardFolderPath() + "FaceImage/";
        }
        mkdirs(mFaceImageFolderPath);

        return mFaceImageFolderPath;
    }

    public String getFaceModelFolderPath() {
        if (TextUtils.isEmpty(mFaceModelFolderPath)) {
            mFaceModelFolderPath = getSDCardFolderPath() + "FaceModel/" + BuildConfig.SDK_VERSION + "/";
        }
        mkdirs(mFaceModelFolderPath);

        return mFaceModelFolderPath;
    }

    public String getFaceImiBinFolderPath() {
        if (TextUtils.isEmpty(mFaceImiBinFolderPath)) {
            mFaceImiBinFolderPath = getSDCardFolderPath() + "FaceImiBin/";
        }
        mkdirs(mFaceImiBinFolderPath);

        return mFaceImiBinFolderPath;
    }

    public String getFaceTestFolderPath() {
        if (TextUtils.isEmpty(mFaceTestFolderPath)) {
            mFaceTestFolderPath = getSDCardFolderPath() + "FaceTest/";
        }
        mkdirs(mFaceTestFolderPath);

        return mFaceTestFolderPath;
    }

    public File mkdirs(@NonNull String path) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }

        return file;
    }

    //true????????????false????????????
    public boolean mkdir(String path) {
        File file = new File(path);

        return file.exists();
    }

    public void deleteFile(String videoPath) {
        File file = new File(videoPath);
        if (file.exists()) {
            file.delete();
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // ????????????
    ///////////////////////////////////////////////////////////////////////////

    /**
     * ????????????
     *
     * @param srcFile     ???????????????
     * @param desFile     ??????????????????
     * @param forceUpdate true: ??????????????????????????????????????????????????? false???????????????????????????
     * @throws IOException
     */
    public void copy(String srcFile, String desFile, boolean forceUpdate) throws IOException {
        InputStream srcInputStream = new FileInputStream(srcFile);
        copy(srcInputStream, desFile, forceUpdate);
    }

    /**
     * ????????????
     *
     * @param srcInput    ??????????????????
     * @param desFile     ??????????????????
     * @param forceUpdate true: ??????????????????????????????????????????????????? false???????????????????????????
     * @throws IOException
     */
    public void copy(InputStream srcInput, String desFile, boolean forceUpdate) throws IOException {
        File file = new File(desFile);
        if (file.exists()) {
            if (!forceUpdate) {
                srcInput.close();
                return;
            } else {
                file.delete();
            }
        }

        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(desFile);
            byte[] buffer = new byte[1024];
            int length = -1;
            while ((length = srcInput.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.flush();
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
            srcInput.close();
        }
    }


    /**
     * @param memorySize
     * @return ????????? @param memorySize ???????????????
     */
    public boolean isExceedMemorySize(int memorySize) {
        if (memorySize > 0) {
            long size = getAvailableExternalMemorySize();
            Log.e(TAG, "" + size / (1024 * 1024) + "MB");
            return size / (1024 * 1024) >= memorySize;
        }
        return false;
    }


    /**
     * @return ??????????????????
     */
    public long getAvailableExternalMemorySize() {
        boolean isExternalMemoryAvailable = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (isExternalMemoryAvailable) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            return availableBlocks * blockSize;
        } else {
            return -1;
        }
    }

    /**
     * ??????????????????????????????
     *
     * @param bytes    ?????????
     * @param path     ??????
     * @param fileName ?????????
     */
    public boolean saveFileWithByte(byte[] bytes, String path, String fileName) {
        File file = new File(path + fileName);
        FileOutputStream fileOutputStream = null;
        BufferedOutputStream bufferedOutputStream = null;
        boolean saveSuccess;
        if (file.exists()) {
            file.delete();
        }
        try {
            file.createNewFile();
            fileOutputStream = new FileOutputStream(file);
            bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
            bufferedOutputStream.write(bytes);
            bufferedOutputStream.flush();
            saveSuccess = true;
        } catch (IOException e) {
            saveSuccess = false;
            e.printStackTrace();
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
                if (bufferedOutputStream != null) {
                    bufferedOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return saveSuccess;
    }

    public ByteBuffer readLocalFileByteBuffer(@NonNull String path, int length) {
        File file = new File(path);
        ByteBuffer byteBuffer = null;
        try {
            // ???????????????
            FileInputStream inputStream = new FileInputStream(file);
            // ???????????????
            byte[] buf = new byte[length];
            // ??????????????????
            inputStream.read(buf);
            // ?????????ByteBuffer
            byteBuffer = ByteBuffer.allocateDirect(length).order(ByteOrder.nativeOrder());
            byteBuffer.put(buf);
            byteBuffer.position(0);
            // ???????????????
            inputStream.close();
            // ????????????
            return byteBuffer;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.w(TAG, "FileNotFoundException:" + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            Log.w(TAG, "IOException:" + e.getMessage());
        }

        return byteBuffer;
    }

    public ByteBuffer readLocalFileByteBuffer(@NonNull String path, int length, ByteBuffer byteBuffer) {
        File file = new File(path);
        try {
            // ???????????????
            FileInputStream inputStream = new FileInputStream(file);
            // ???????????????
            byte[] buf = new byte[length];
            inputStream.read(buf);
            // ??????????????????
            // ?????????ByteBuffer
            byteBuffer.put(buf, 0, length);
            byteBuffer.position(0);
            // ???????????????
            inputStream.close();
            // ????????????
            return byteBuffer;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.w(TAG, "FileNotFoundException:" + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            Log.w(TAG, "IOException:" + e.getMessage());
        }

        return byteBuffer;
    }

    /**
     * @param path ??????????????????????????????jpg???png?????????
     * @return
     */
    public List<String> getFiles(@NonNull String path) {
        File file = new File(path);
        File[] files = file.listFiles();
        if (files == null) {
            Log.e(TAG, "?????????");
            return null;
        }
        List<String> fileList = new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            boolean isAdd = files[i].getAbsolutePath().endsWith(".jpg") || files[i].getAbsolutePath().endsWith(".JPG")
                    || files[i].getAbsolutePath().endsWith(".png") || files[i].getAbsolutePath().endsWith(".PNG");
            if (isAdd) {
                fileList.add(files[i].getAbsolutePath());
            }
        }
        return fileList;
    }

    /**
     * @param path
     * @return
     */
    public List<String> getFiles(@NonNull String path, String... names) {
        File file = new File(path);
        File[] files = file.listFiles();
        if (files == null) {
            Log.e(TAG, "?????????");
            return null;
        }
        List<String> fileList = new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            for (String name : names) {
                boolean isAdd = files[i].getAbsolutePath().endsWith(name);
                if (isAdd) {
                    fileList.add(files[i].getAbsolutePath());
                }
            }
        }
        return fileList;
    }

    private void saveData(ByteBuffer dataBuffer, String dataName) {
        boolean save = false;
        if (dataBuffer != null) {
            if (FileHelper.getInstance().isExceedMemorySize(100)) {
                byte[] data = new byte[dataBuffer.remaining()];
                dataBuffer.get(data);
                dataBuffer.position(0);
                save = FileHelper.getInstance().saveFileWithByte(data, FileHelper.getInstance().getFaceImageFolderPath(), dataName);
            }
        }

        Log.d(TAG, save ? "????????????" : "????????????" + " " + dataName);
    }
    public void saveBitmap(Bitmap bitmap,String name) {
        File f = new File(mFaceImageFolderPath, name);
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();

            Log.d(TAG, "????????????" + " :" + f.getName());
        } catch (IOException e) {
            Log.d(TAG, "????????????" + " :" + e.toString());
        }
    }

    public void saveBitmap(Bitmap bitmap) {
        saveBitmap(bitmap,System.currentTimeMillis() + ".png");
    }

    private static class FileHelperHolder {
        private static final FileHelper sFileHelper = new FileHelper();
    }
}
