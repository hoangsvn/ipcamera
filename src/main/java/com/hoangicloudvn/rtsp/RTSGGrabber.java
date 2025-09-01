package com.hoangicloudvn.rtsp;

import com.hoangicloudvn.device.OnvifDevice;
import com.hoangicloudvn.stream.Audio;
import com.hoangicloudvn.stream.Video;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;

import javax.sound.sampled.AudioFormat;
import javax.swing.*;
import java.nio.ShortBuffer;

public class RTSGGrabber {

    FFmpegFrameGrabber grabber;
    OnvifDevice device;
    int port;


    public RTSGGrabber(OnvifDevice device, int port) {
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
        grabber.start();
    }

    public FFmpegFrameGrabber getGrabber() {
        return grabber;
    }
}
