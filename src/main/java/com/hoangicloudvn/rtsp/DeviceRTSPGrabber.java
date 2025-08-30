package com.hoangicloudvn.rtsp;

import com.hoangicloudvn.device.OnvifDevice;
import com.hoangicloudvn.video.Render;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;

import java.awt.image.BufferedImage;

public class DeviceRTSPGrabber implements Grabber {

    OnvifDevice device;
    Render render;
    int port;
    int height;
    int wight;

    public DeviceRTSPGrabber(OnvifDevice device, int port, int height, int wight, Render render) {
        this.device = device;
        this.render = render;
        this.port = port;
        this.height = height;
        this.wight = wight;
    }

    @Override
    public void run() {
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber("rtsp://"
                .concat(device.username()).concat(":")
                .concat(device.password()).concat("@")
                .concat(device.ip()).concat(":")
                .concat(String.valueOf(port)).concat("/onvif1"));

        render.init();
        grabber.setImageWidth(wight);
        grabber.setImageHeight(height);
        Java2DFrameConverter converter = new Java2DFrameConverter();

        try {
            grabber.start();
            render.init();

            Thread grabThread = new Thread(() -> {
                try {
                    while (true) {
                        Frame frame = grabber.grabImage();
                        if (frame == null) continue;

                        BufferedImage img = converter.convert(frame);
                        if (img != null) render.update(img);

                        Thread.sleep(15); // ~60fps
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        grabber.stop();
                        grabber.release();
                    } catch (Exception ignored) {
                    }
                    render.stop();
                }
            });

            grabThread.start();

        } catch (Exception e) {
            e.printStackTrace();
            try {
                grabber.stop();
                grabber.release();
            } catch (Exception ignored) {
            }
        }
    }
}
