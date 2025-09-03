package com.hoangicloudvn.rtsp;

import com.hoangicloudvn.device.OnvifDevice;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.FFmpegFrameGrabber;

import javax.swing.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class RTSPGrabber {

    FFmpegFrameGrabber grabber;
    OnvifDevice device;
    int port;


    public RTSPGrabber(OnvifDevice device, int port) {
        this.device = device;
        this.port = port;

    }

    public void run() {
        grabber = new FFmpegFrameGrabber(String.format(
                "rtsp://%s:%s@%s:%d/onvif1",
                device.username(),
                device.password(),
                device.ip(),
                port
        ));
        grabber.setAudioChannels(1);
        grabber.setSampleRate(8000);
        grabber.setAudioCodec(avcodec.AV_CODEC_ID_PCM_ALAW);

        try (ExecutorService executor = Executors.newSingleThreadExecutor()) {
            Future<?> future = executor.submit(() -> {
                try {
                    grabber.start();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            try {
                future.get(10, TimeUnit.SECONDS);
            } catch (Exception e) {
                future.cancel(true);
                JOptionPane.showMessageDialog(
                        null,
                        String.format("Failed to connect to camera:%s", device.ip()),
                        "Connection",
                        JOptionPane.ERROR_MESSAGE
                );
                System.exit(0);
            } finally {
                executor.shutdownNow();
            }
        }
    }

    public FFmpegFrameGrabber getGrabber() {
        return grabber;
    }
}
