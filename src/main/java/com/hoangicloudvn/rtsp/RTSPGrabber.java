package com.hoangicloudvn.rtsp;

import com.hoangicloudvn.device.OnvifDevice;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.FFmpegFrameGrabber;

import javax.swing.*;

public class RTSPGrabber {

    FFmpegFrameGrabber grabber;
    OnvifDevice device;
    int port;


    public RTSPGrabber(OnvifDevice device, int port) {
        this.device = device;
        this.port = port;

    }

    public void run() throws Exception {
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
        boolean connected = false;
        while (!connected) {
            try {
                grabber.start();
                connected = true;
            } catch (Exception e) {
                JOptionPane.showMessageDialog(
                        null,
                        String.format("Failed to connect to camera %s.", device.ip()),
                        "Connection Error",
                        JOptionPane.ERROR_MESSAGE
                );

                System.exit(0);

            }
        }
    }

    public FFmpegFrameGrabber getGrabber() {
        return grabber;
    }
}
