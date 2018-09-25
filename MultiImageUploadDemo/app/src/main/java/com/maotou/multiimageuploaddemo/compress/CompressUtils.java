package com.maotou.multiimageuploaddemo.compress;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.util.Log;


import com.maotou.multiimageuploaddemo.CompressImageTask;
import com.maotou.multiimageuploaddemo.FileUtils;
import com.maotou.multiimageuploaddemo.MultiTaskUtil;
import com.maotou.multiimageuploaddemo.OnUploadListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by wuchundu on 2018/7/3.
 * 图片压缩工具
 */
public class CompressUtils {

    public static final long MAX_BYTE_SIZE = 200 * 1024;

    public static void asyncCompress(@NonNull final List<String> paths, @NonNull final OnCompressListener<List<File>> listener){
        if (paths.size() == 0){
            return;
        }
        listener.onStart();
        MultiTaskUtil<File> fileMultiTaskUtil = new MultiTaskUtil<>(new CompressImageTask());
        final List<File> list = new ArrayList<>(paths.size());
        fileMultiTaskUtil.setOnUploadListener(new OnUploadListener<File>() {
            @Override
            public void onAllSuccess() {
                if (paths.size() == list.size()){
                    listener.onSuccess(list);
                }else{
                    for (File f : list) {
                        FileUtils.delFile(f);
                    }
                    listener.onError(new Exception("压缩失败"));
                }
            }

            @Override
            public void onAllFailed() {
                listener.onError(new Exception("压缩失败"));
            }

            @Override
            public void onThreadStart(int position) {
                Log.d("Compress", "onThreadStart --- currentPosition - " + position+1 + "; total - " + paths.size());
            }

            @Override
            public void onThreadProgress(int percent, int position) {}

            @Override
            public void onThreadFinish(int position, File file) {
                list.add(file);
                Log.d("Compress", "onThreadFinish --- currentPosition - " + position+1 + ": path - " + file.getAbsolutePath());
            }

            @Override
            public void onThreadInterrupted(Exception e, int position) {
                listener.onError(e);
            }
        });
        fileMultiTaskUtil.submitAll(paths);
    }

    public static void asyncCompress(final String filePath, final OnCompressListener<File> listener) {
        if (listener != null) {
            listener.onStart();
        }
        Flowable.just(filePath)
                .map(new Function<String, File>() {
                    @Override
                    public File apply(@io.reactivex.annotations.NonNull String path) throws Exception {
                        return compressByLuban(path);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull Throwable throwable) throws Exception {
                        if (listener != null) {
                            listener.onError(throwable);
                        }
                    }
                })
                .onErrorResumeNext(Flowable.<File>empty())
                .filter(new Predicate<File>() {
                    @Override
                    public boolean test(@io.reactivex.annotations.NonNull File file) throws Exception {
                        return file != null;
                    }
                })
                .subscribe(new Consumer<File>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull File file) throws Exception {
                        if (listener != null) {
                            listener.onSuccess(file);
                        }
                    }
                });
    }

    /**
     * 鲁班算法压缩
     *
     * @param filePath
     * @return
     * @throws Exception
     */
    public static File compressByLuban(final String filePath) throws IOException {
        return compressByLuban(getBitmap(filePath));
    }

    /**
     * 鲁班算法压缩
     *
     * @param src
     * @return
     */
    public static File compressByLuban(Bitmap src) throws IOException {
        if (isEmptyBitmap(src)) return null;
        return compressByQuality(compressBySampleSize(src, lubanComputeSize(src)), 60);
    }

    /**
     * 指定质量比例压缩
     *
     * @param filePath The path of file.
     * @param quality  The quality.
     * @return the compressed bitmap
     */
    public static File compressByQuality(final String filePath, @IntRange(from = 0, to = 100) int quality) throws IOException {
        return compressByQuality(getBitmap(filePath), quality);
    }

    /**
     * 指定质量比例压缩
     *
     * @param src     The source of bitmap.
     * @param quality The quality.
     * @return the compressed bitmap
     */
    public static File compressByQuality(Bitmap src, @IntRange(from = 0, to = 100) int quality) throws IOException {
        if (isEmptyBitmap(src)) return null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, quality, baos);

        FileOutputStream fos = null;
        try {
            File file = new File(FileUtils.SDPATH + FileUtils.getRandomFileName() + ".jpg");
            fos = new FileOutputStream(file);
            fos.write(baos.toByteArray());
            fos.flush();
            return file;
        } finally {
            FileUtils.IOUtils.close(fos);
            FileUtils.IOUtils.close(baos);
        }
    }

    /**
     * 指定图片大小质量压缩
     *
     * @param filePath The path of file.
     * @return the compressed bitmap
     */
    public static File compressByQuality(final String filePath) {
        return compressByQuality(getBitmap(filePath), MAX_BYTE_SIZE);
    }

    /**
     * 指定图片大小质量压缩
     *
     * @param filePath    The path of file.
     * @param maxByteSize The maximum size of byte.
     * @return the compressed bitmap
     */
    public static File compressByQuality(final String filePath, long maxByteSize) {
        return compressByQuality(getBitmap(filePath), maxByteSize);
    }

    /**
     * 指定图片大小质量压缩
     *
     * @param src         The source of bitmap.
     * @param maxByteSize The maximum size of byte.
     * @return the compressed bitmap
     */
    public static File compressByQuality(Bitmap src, long maxByteSize) {
        if (isEmptyBitmap(src) || maxByteSize <= 0) return null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        if (baos.size() >= maxByteSize) {
            baos.reset();
            src.compress(Bitmap.CompressFormat.JPEG, 0, baos);
            if (baos.size() >= maxByteSize) {

            } else {
                // 二分查找指定大小内最优的质量压缩
                int st = 0;
                int end = 100;
                int mid = 0;
                while (st < end) {
                    mid = (st + end) / 2;
                    baos.reset();
                    src.compress(Bitmap.CompressFormat.JPEG, mid, baos);
                    int len = baos.size();
                    if (len == maxByteSize) {
                        break;
                    } else if (len > maxByteSize) {
                        end = mid - 1;
                    } else {
                        st = mid + 1;
                    }
                }
                if (end == mid - 1) {
                    baos.reset();
                    src.compress(Bitmap.CompressFormat.JPEG, st, baos);
                }
            }
        }

        FileOutputStream fos = null;
        try {
            File file = new File(FileUtils.SDPATH + FileUtils.getRandomFileName() + ".jpg");
            fos = new FileOutputStream(file);
            fos.write(baos.toByteArray());
            fos.flush();
            return file;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            FileUtils.IOUtils.close(fos);
            FileUtils.IOUtils.close(baos);
        }
        return null;
    }

    /**
     * 按比例压缩图片尺寸
     *
     * @param filePath   The path of file.
     * @param sampleSize The sample size.
     * @return the compressed bitmap
     */
    public static Bitmap compressBySampleSize(final String filePath, final int sampleSize) {
        Bitmap src = getBitmap(filePath);
        src = compressBySampleSize(src, sampleSize);
        return src;
    }

    /**
     * 按比例压缩图片尺寸
     *
     * @param src        The source of bitmap.
     * @param sampleSize The sample size.
     * @return the compressed bitmap
     */
    public static Bitmap compressBySampleSize(Bitmap src, int sampleSize) {
        if (isEmptyBitmap(src)) return null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = sampleSize;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] bytes = baos.toByteArray();
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
    }

    /**
     * 按指定尺寸压缩
     *
     * @param filePath  The path of file.
     * @param maxWidth  The maximum width.
     * @param maxHeight The maximum height.
     * @return the compressed bitmap
     */
    public static Bitmap compressBySampleSize(final String filePath, final int maxWidth, final int maxHeight) {
        Bitmap src = getBitmap(filePath);
        src = compressBySampleSize(src, maxWidth, maxHeight);
        return src;
    }

    /**
     * 按指定尺寸压缩
     *
     * @param src       The source of bitmap.
     * @param maxWidth  The maximum width.
     * @param maxHeight The maximum height.
     * @return the compressed bitmap
     */
    public static Bitmap compressBySampleSize(Bitmap src, int maxWidth, int maxHeight) {
        if (isEmptyBitmap(src)) return null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] bytes = baos.toByteArray();
        BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
        options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
    }

    private static boolean isEmptyBitmap(final Bitmap src) {
        return src == null || src.getWidth() == 0 || src.getHeight() == 0;
    }

    /**
     * 计算压缩比例
     *
     * @param options   The options.
     * @param maxWidth  The maximum width.
     * @param maxHeight The maximum height.
     * @return the sample size
     */
    private static int calculateInSampleSize(BitmapFactory.Options options, int maxWidth, int maxHeight) {
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;
        while ((width >>= 1) >= maxWidth && (height >>= 1) >= maxHeight) {
            inSampleSize <<= 1;
        }
        return inSampleSize;
    }

    /**
     * 文件获取Bitmap
     *
     * @param filePath The path of file.
     * @return bitmap
     */
    public static Bitmap getBitmap(final String filePath) {
        if (isSpace(filePath)) return null;
        return BitmapFactory.decodeFile(filePath);
    }

    private static boolean isSpace(final String s) {
        if (s == null) return true;
        for (int i = 0, len = s.length(); i < len; ++i) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 鲁班图片尺寸比例压缩算法
     *
     * @param filePath
     * @return
     */
    private static int lubanComputeSize(final String filePath) {
        return lubanComputeSize(getBitmap(filePath));
    }

    /**
     * 鲁班图片尺寸比例压缩算法
     *
     * @param src
     * @return
     */
    private static int lubanComputeSize(Bitmap src) {
        if (isEmptyBitmap(src)) return 1;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] bytes = baos.toByteArray();
        BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);

        int srcWidth = options.outWidth;
        int srcHeight = options.outHeight;
        srcWidth = srcWidth % 2 == 1 ? srcWidth + 1 : srcWidth;
        srcHeight = srcHeight % 2 == 1 ? srcHeight + 1 : srcHeight;

        int longSide = Math.max(srcWidth, srcHeight);
        int shortSide = Math.min(srcWidth, srcHeight);

        options.inJustDecodeBounds = false;

        float scale = ((float) shortSide / longSide);
        if (scale <= 1 && scale > 0.5625) {
            if (longSide < 1664) {
                return 1;
            } else if (longSide < 4990) {
                return 2;
            } else if (longSide > 4990 && longSide < 10240) {
                return 4;
            } else {
                return longSide / 1280 == 0 ? 1 : longSide / 1280;
            }
        } else if (scale <= 0.5625 && scale > 0.5) {
            return longSide / 1280 == 0 ? 1 : longSide / 1280;
        } else {
            return (int) Math.ceil(longSide / (1280.0 / scale));
        }
    }
}
