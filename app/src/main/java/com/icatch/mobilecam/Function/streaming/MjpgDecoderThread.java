package com.icatch.mobilecam.Function.streaming;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.view.SurfaceHolder;

import com.icatch.mobilecam.Listener.VideoFramePtsChangedListener;
import com.icatch.mobilecam.Log.AppLog;
import com.icatch.mobilecam.SdkApi.StreamProvider;
import com.icatch.mobilecam.data.Mode.PreviewLaunchMode;
import com.icatch.mobilecam.utils.ScaleTool;
import com.icatchtek.reliant.customer.exception.IchTryAgainException;
import com.icatchtek.reliant.customer.type.ICatchAudioFormat;
import com.icatchtek.reliant.customer.type.ICatchFrameBuffer;
import com.icatchtek.reliant.customer.type.ICatchVideoFormat;

import java.nio.ByteBuffer;


public class MjpgDecoderThread {
    private static final String TAG = "MjpgDecoderThread";
    private StreamProvider streamProvider;
    private Bitmap videoFrameBitmap;
    private int frameWidth;
    private int frameHeight;
    private SurfaceHolder surfaceHolder;
    private AudioThread audioThread;
    private VideoThread videoThread;
    private int previewLaunchMode;
    private Rect drawFrameRect;
    private ICatchVideoFormat videoFormat;
    private int viewWidth;
    private int viewHeight;
    private VideoFramePtsChangedListener framePtsChangedListener;

    public MjpgDecoderThread(StreamProvider streamProvider, SurfaceHolder holder, int previewLaunchMode, int viewWidth, int viewHeight) {
        this.streamProvider = streamProvider;
        this.surfaceHolder = holder;
        this.previewLaunchMode = previewLaunchMode;
        this.videoFormat = streamProvider.getVideoFormat();
        frameWidth = videoFormat.getVideoW();
        frameHeight = videoFormat.getVideoH();
        this.viewWidth = viewWidth;
        this.viewHeight = viewHeight;
        AppLog.i(TAG, "start frameHeight=" + frameHeight + " frameWidth=" + frameWidth);
//        holder.setFormat(PixelFormat.RGBA_8888);
    }

    public void setframePtsChangedListener(VideoFramePtsChangedListener framePtsChangedListener) {
        this.framePtsChangedListener = framePtsChangedListener;
    }

    public void start(boolean enableAudio, boolean enableVideo) {
        AppLog.i(TAG, "start");
        if (enableAudio) {
            audioThread = new AudioThread();
            audioThread.start();
        }
        if (enableVideo) {
            videoThread = new VideoThread();
            videoThread.start();
        }
    }

    public boolean isAlive() {
        if (videoThread != null && videoThread.isAlive() == true) {
            return true;
        }
        if (audioThread != null && audioThread.isAlive() == true) {
            return true;
        }
        return false;
    }

    public void stop() {
        if (audioThread != null) {
            audioThread.requestExitAndWait();
        }
        if (videoThread != null) {
            videoThread.requestExitAndWait();
        }
    }

    private class VideoThread extends Thread {
        private boolean done;
        private ByteBuffer bmpBuf;
        private byte[] pixelBuf;

        VideoThread() {
            super();
            done = false;
            pixelBuf = new byte[frameWidth * frameHeight * 4];
            bmpBuf = ByteBuffer.wrap(pixelBuf);
            // Trigger onDraw with those initialize parameters
            videoFrameBitmap = Bitmap.createBitmap(frameWidth, frameHeight, Bitmap.Config.ARGB_8888);
            drawFrameRect = new Rect(0, 0, frameWidth, frameHeight);
        }

        @Override
        public void run() {
            AppLog.i(TAG, "start running video thread");
            ICatchFrameBuffer buffer = new ICatchFrameBuffer(frameWidth * frameHeight * 4);
            buffer.setBuffer(pixelBuf);
            boolean ret = false;
            boolean isSaveBitmapToDb = false;
            boolean isFirstFrame = true;
            boolean isStartGet = true;
            long lastTime = System.currentTimeMillis();
            long testTime;
            while (!done) {
                ret = false;
                try {
                    ret = streamProvider.getNextVideoFrame(buffer);
                } catch (IchTryAgainException e) {
//                    AppLog.e(TAG, "IchTryAgainException");
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    continue;
                } catch (Exception ex) {
                    AppLog.e(TAG, "getNextVideoFrame " + ex.getClass().getSimpleName());
                    ex.printStackTrace();
                    return;
                }
                if (ret == false) {
//                    AppLog.e(TAG,"getNextVideoFrame failed\n");
                    continue;
                }
                if (buffer == null || buffer.getFrameSize() == 0) {
                    AppLog.e(TAG, "getNextVideoFrame buffer == null\n");
                    continue;
                }

                bmpBuf.rewind();
                if (videoFrameBitmap == null) {
                    continue;
                }

                if (isFirstFrame) {
                    isFirstFrame = false;
                    AppLog.i(TAG, "get first Frame");
                }
                videoFrameBitmap.copyPixelsFromBuffer(bmpBuf);
//                Test.saveImage(videoFrameBitmap,System.currentTimeMillis());
                if (!isSaveBitmapToDb) {
                    if (videoFrameBitmap != null && previewLaunchMode == PreviewLaunchMode.RT_PREVIEW_MODE) {
                        isSaveBitmapToDb = true;
                    }
                }
                Canvas canvas = surfaceHolder.lockCanvas();
                if (canvas == null) {
                    continue;
                }
                drawFrameRect = ScaleTool.getScaledPosition(frameWidth, frameHeight, viewWidth, viewHeight);
                canvas.drawBitmap(videoFrameBitmap, null, drawFrameRect, null);
                surfaceHolder.unlockCanvasAndPost(canvas);
                if (framePtsChangedListener != null) {
                    framePtsChangedListener.onFramePtsChanged(buffer.getPresentationTime());
                }
            }
            AppLog.i(TAG, "stopMPreview video thread");
        }

        public void requestExitAndWait() {
            // 把这个线程标记为完成，并合并到主程序线程
            done = true;
            try {
                join();
            } catch (InterruptedException ex) {
            }
        }
    }

    private class AudioThread extends Thread {
        private boolean done = false;
        private AudioTrack audioTrack;

        public void run() {
            AppLog.i(TAG, "Run AudioThread");
            ICatchAudioFormat audioFormat;
            audioFormat = streamProvider.getAudioFormat();
            if (audioFormat == null) {
                AppLog.e(TAG, "Run AudioThread audioFormat is null!");
                return;
            }
            int bufferSize = AudioTrack.getMinBufferSize(audioFormat.getFrequency(), audioFormat.getNChannels() == 2 ? AudioFormat.CHANNEL_IN_STEREO
                    : AudioFormat.CHANNEL_IN_LEFT, audioFormat.getSampleBits() == 16 ? AudioFormat.ENCODING_PCM_16BIT : AudioFormat.ENCODING_PCM_8BIT);

            audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, audioFormat.getFrequency(), audioFormat.getNChannels() == 2 ? AudioFormat.CHANNEL_IN_STEREO
                    : AudioFormat.CHANNEL_IN_LEFT, audioFormat.getSampleBits() == 16 ? AudioFormat.ENCODING_PCM_16BIT : AudioFormat.ENCODING_PCM_8BIT,
                    bufferSize, AudioTrack.MODE_STREAM);

            audioTrack.play();
            AppLog.i(TAG, "Run AudioThread 3");
            byte[] audioBuffer = new byte[1024 * 50];
            ICatchFrameBuffer icatchBuffer = new ICatchFrameBuffer(1024 * 50);
            icatchBuffer.setBuffer(audioBuffer);
            boolean ret = false;
            while (!done) {
                ret = false;
                try {
                    ret = streamProvider.getNextAudioFrame(icatchBuffer);
                } catch (IchTryAgainException e) {
                    //AppLog.e(TAG, "getNextAudioFrame IchTryAgainException");
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    continue;
                } catch (Exception ex) {
                    AppLog.e(TAG, "getNextVideoFrame " + ex.getClass().getSimpleName());
                    ex.printStackTrace();
                    return;
                }
                if (false == ret) {
                    continue;
                }
                audioTrack.write(icatchBuffer.getBuffer(), 0, icatchBuffer.getFrameSize());
            }
            audioTrack.stop();
            audioTrack.release();
            AppLog.i(TAG, "stopMPreview audio thread");

        }

        public void requestExitAndWait() {
            done = true;
            try {
                join();
            } catch (InterruptedException ex) {
            }
        }
    }

    public void redrawBitmap(SurfaceHolder holder, int w, int h) {
        SurfaceHolder surfaceHolder = holder;
        AppLog.d(TAG, "redrawBitmap w=" + w + " h=" + h);
        AppLog.d(TAG, "redrawBitmap frameWidth=" + frameWidth + " frameHeight=" + frameHeight);
        AppLog.d(TAG, "redrawBitmap videoFrameBitmap=" + videoFrameBitmap);
        viewWidth = w;
        viewHeight = h;
        if (videoFrameBitmap != null) {
            Canvas canvas = surfaceHolder.lockCanvas();
            if (canvas != null) {
                drawFrameRect = ScaleTool.getScaledPosition(frameWidth, frameHeight, w, h);
                canvas.drawBitmap(videoFrameBitmap, null, drawFrameRect, null);
                surfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
    }

}

