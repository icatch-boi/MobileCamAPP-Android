package com.icatch.mobilecam.Function.streaming;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.util.Log;
import android.view.Surface;

import com.icatch.mobilecam.Listener.VideoFramePtsChangedListener;
import com.icatch.mobilecam.Log.AppLog;
import com.icatch.mobilecam.SdkApi.StreamProvider;
import com.icatch.mobilecam.data.Mode.PreviewLaunchMode;
import com.icatchtek.reliant.customer.exception.IchTryAgainException;
import com.icatchtek.reliant.customer.type.ICatchAudioFormat;
import com.icatchtek.reliant.customer.type.ICatchFrameBuffer;
import com.icatchtek.reliant.customer.type.ICatchVideoFormat;

import java.io.IOException;
import java.nio.ByteBuffer;


public class H264DecoderThread {
    private static final String TAG = "H264DecoderThread";
    private StreamProvider streamProvider;
    private Surface surface;
    private VideoThread videoThread;
    private AudioThread audioThread;
    private boolean audioPlayFlag = false;
    private int BUFFER_LENGTH = 1280 * 720 * 4;
    //    private int timeout = 60000;// us
    private int timeout = 20000;// us
    private MediaCodec decoder;
    private int previewLaunchMode;
    private ICatchVideoFormat videoFormat;
    private int frameWidth;
    private int frameHeight;
    private VideoFramePtsChangedListener framePtsChangedListener;

    public H264DecoderThread(StreamProvider streamProvider, Surface surface, int previewLaunchMode) {
        this.surface = surface;
        this.streamProvider = streamProvider;
        this.previewLaunchMode = previewLaunchMode;
        this.videoFormat = streamProvider.getVideoFormat();
//        holder.setFormat(PixelFormat.RGBA_8888);
        if (videoFormat != null) {
            frameWidth = videoFormat.getVideoW();
            frameHeight = videoFormat.getVideoH();
        }
    }

    public void setframePtsChangedListener(VideoFramePtsChangedListener framePtsChangedListener) {
        this.framePtsChangedListener = framePtsChangedListener;
    }

    public void start(boolean enableAudio, boolean enableVideo) {
        AppLog.i(TAG, "start");
        setFormat();
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
        audioPlayFlag = false;
    }

    long videoShowtime = 0;
    double curVideoPts = 0;

    private class VideoThread extends Thread {

        private boolean done = false;
        private MediaCodec.BufferInfo info;
        long startTime = 0;
        int frameSize = 0;

        VideoThread() {
            super();
            done = false;
        }

        @Override
        public void run() {
            AppLog.i(TAG, "h264 run for gettting surface image");
            ByteBuffer[] inputBuffers = decoder.getInputBuffers();
            info = new MediaCodec.BufferInfo();
//            byte[] mPixel = new byte[BUFFER_LENGTH];
            byte[] mPixel = new byte[frameWidth * frameHeight * 4];
            ICatchFrameBuffer frameBuffer = new ICatchFrameBuffer(frameWidth * frameHeight * 4);
            frameBuffer.setBuffer(mPixel);
            int inIndex = -1;
            int sampleSize = 0;
            long pts = 0;
            boolean retvalue = true;
            boolean isFirst = true;
            long lastTime = System.currentTimeMillis();
            long endTime = System.currentTimeMillis();
            long currentTime;
            while (!done) {
                retvalue = false;
                curVideoPts = -1;

                try {
                    retvalue = streamProvider.getNextVideoFrame(frameBuffer);
                    if (!retvalue) {
                        continue;
                    }
                } catch (IchTryAgainException ex) {
                    ex.printStackTrace();
                    retvalue = false;
                    continue;
                } catch (Exception ex) {
                    AppLog.e(TAG, "getNextVideoFrame " + ex.getClass().getSimpleName());
                    ex.printStackTrace();
                    retvalue = false;
                    break;
                }
                if (frameBuffer.getFrameSize() <= 0 || frameBuffer == null) {
                    retvalue = false;
                    continue;
                }
                if (!retvalue) {
                    continue;
                }
                inIndex = decoder.dequeueInputBuffer(timeout);
                curVideoPts = frameBuffer.getPresentationTime();
                frameSize++;
                if (isFirst) {
                    isFirst = false;
                    startTime = System.currentTimeMillis();
                    AppLog.i(TAG, "get first Frame");
                }
                if (inIndex >= 0) {
                    sampleSize = frameBuffer.getFrameSize();
                    pts = (long) (frameBuffer.getPresentationTime() * 1000 * 1000); // (seconds
                    ByteBuffer buffer = inputBuffers[inIndex];
                    buffer.clear();
                    buffer.rewind();
                    buffer.put(frameBuffer.getBuffer(), 0, sampleSize);
                    decoder.queueInputBuffer(inIndex, 0, sampleSize, pts, 0);
                }
                int outBufId = decoder.dequeueOutputBuffer(info, timeout);
                if (outBufId >= 0) {
                    //AppLog.d( TAG, "do decoder and display....." );
                    decoder.releaseOutputBuffer(outBufId, true);
                    if (!audioPlayFlag) {
                        audioPlayFlag = true;
                        videoShowtime = System.currentTimeMillis();
                        AppLog.d(TAG, "ok show image!.....................startTime= " + (System.currentTimeMillis() - startTime) + " frameSize=" + frameSize
                                + " curVideoPts=" + curVideoPts);
                    }
                    if (framePtsChangedListener != null) {
                        framePtsChangedListener.onFramePtsChanged(frameBuffer.getPresentationTime());
                    }
                }
            }
            AppLog.i(TAG, "start decoder stop");
            decoder.stop();
            AppLog.i(TAG, "start decoder pancamGLRelease");
            decoder.release();
            AppLog.i(TAG, "stopMPreview video thread");
        }

        public boolean dequeueAndRenderOutputBuffer(int outtime) {
            int outIndex = decoder.dequeueOutputBuffer(info, outtime);

            switch (outIndex) {
                case MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED:
                    return false;
                case MediaCodec.INFO_OUTPUT_FORMAT_CHANGED:
                    return false;
                case MediaCodec.INFO_TRY_AGAIN_LATER:
                    return false;
                case MediaCodec.BUFFER_FLAG_SYNC_FRAME:
                    return false;

                default:
                    decoder.releaseOutputBuffer(outIndex, true);
                    if (!audioPlayFlag) {
                        audioPlayFlag = true;
                        videoShowtime = System.currentTimeMillis();
                        AppLog.d(TAG, "ok show image!.....................startTime= " + (System.currentTimeMillis() - startTime) + " frameSize=" + frameSize
                                + " curVideoPts=" + curVideoPts);
                    }
                    return true;
            }
        }

        public void requestExitAndWait() {
            // 把这个线程标记为完成，并合并到主程序线程
            AppLog.e(TAG, "H264Decoder requestExitAndWait isAlive=" + this.isAlive());
            done = true;
//            if (this.isAlive()) {
//                try {
//                    join();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
            AppLog.e(TAG, "end  H264Decoder requestExitAndWait");
        }
    }

    private void setFormat() {
        ICatchVideoFormat videoFormat = this.videoFormat;
        AppLog.i(TAG, "create  MediaFormat");
        int w = videoFormat.getVideoW();
        int h = videoFormat.getVideoH();
        String type = videoFormat.getMineType();
        MediaFormat format = MediaFormat.createVideoFormat(type, w, h);

        if (previewLaunchMode == PreviewLaunchMode.RT_PREVIEW_MODE) {
            format.setByteBuffer("csd-0", ByteBuffer.wrap(videoFormat.getCsd_0(), 0, videoFormat.getCsd_0_size()));
            format.setByteBuffer("csd-1", ByteBuffer.wrap(videoFormat.getCsd_1(), 0, videoFormat.getCsd_0_size()));
            format.setInteger("durationUs", videoFormat.getDurationUs());
            format.setInteger("max-input-size", videoFormat.getMaxInputSize());
        }

        String ret = videoFormat.getMineType();
        Log.i(TAG, "h264 videoFormat.getMineType()=" + ret);
        decoder = null;
        try {
            decoder = MediaCodec.createDecoderByType(ret);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.d(TAG, "MediaFormat format=" + format);
        decoder.configure(format, surface, null, 0);
        decoder.start();
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
}
