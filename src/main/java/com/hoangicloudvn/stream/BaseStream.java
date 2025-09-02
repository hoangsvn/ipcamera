package com.hoangicloudvn.stream;

import com.hoangicloudvn.device.DeviceNetworkInterface;
import com.hoangicloudvn.device.DeviceOnvifInformation;
import com.hoangicloudvn.device.OnvifDevice;
import com.hoangicloudvn.ptz.OnvifPtz;
import com.hoangicloudvn.rtsp.RTSPGrabber;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class BaseStream implements Video {
    CanvasFrame videoFrame;
    OnvifDevice onvifDevice;
    OnvifPtz onvifPtz;
    RTSPGrabber grabber;
    int wight;
    int height;

    public BaseStream(OnvifDevice onvifDevice, OnvifPtz onvifPtz, RTSPGrabber grabber, int wight, int height) {
        this.onvifDevice = onvifDevice;
        this.grabber = grabber;
        this.onvifPtz = onvifPtz;
        this.wight = wight;
        this.height = height;
    }

    public static String getFullStreamInfo(FFmpegFrameGrabber grabber) {
        StringBuilder sb = new StringBuilder();

        // Format info
        sb.append("format: ").append(grabber.getFormat()).append("\n");
        sb.append("video_codec: ").append(grabber.getVideoCodecName()).append("\n");
        sb.append("audio_codec: ").append(grabber.getAudioCodecName()).append("\n");
        sb.append("resolution: ")
                .append(grabber.getImageWidth())
                .append("x")
                .append(grabber.getImageHeight())
                .append("\n");
        sb.append("frame_rate: ").append(grabber.getFrameRate()).append("\n");
        sb.append("audio_channels: ").append(grabber.getAudioChannels()).append("\n");
        sb.append("sample_rate: ").append(grabber.getSampleRate()).append("\n");

        // Format-level metadata
        grabber.getMetadata().forEach((k, v) -> sb.append(k).append(": ").append(v).append("\n"));

        // Video metadata
        grabber.getVideoMetadata().forEach((k, v) -> sb.append("video_").append(k).append(": ").append(v).append("\n"));

        // Audio metadata
        grabber.getAudioMetadata().forEach((k, v) -> sb.append("audio_").append(k).append(": ").append(v).append("\n"));

        return sb.toString();
    }

    @Override
    public void init() {

        videoFrame = new CanvasFrame(String.format("Stream IP:%s", onvifDevice.ip()), CanvasFrame.getDefaultGamma() / grabber.getGrabber().getGamma());
        videoFrame.setCanvasSize(wight, height);
        videoFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JMenuBar Bar = new JMenuBar();

        JMenu menu = new JMenu("Device");

        DeviceOnvifInformation info = onvifPtz.getDeviceonvifInfo();
        DeviceNetworkInterface net = onvifPtz.getNetWorkInterface();
        JMenuItem infoItem = new JMenuItem(String.format("Info:%s", onvifPtz.device().ip()));
        JMenuItem wifiItem = new JMenuItem(String.format("Wifi:%s", onvifPtz.device().ip()));
        JMenuItem metadata = new JMenuItem(String.format("Metadata:%s", onvifPtz.device().ip()));

        menu.add(infoItem);
        menu.add(wifiItem);
        menu.add(metadata);

        infoItem.addActionListener(e ->
                JOptionPane.showMessageDialog(
                        videoFrame,
                        info.toString(),
                        "Device Info",
                        JOptionPane.INFORMATION_MESSAGE
                )
        );

        wifiItem.addActionListener(e ->
                JOptionPane.showMessageDialog(
                        videoFrame,
                        net.toString(),
                        "WiFi Info",
                        JOptionPane.INFORMATION_MESSAGE
                )
        );

        metadata.addActionListener(e ->
                JOptionPane.showMessageDialog(
                        videoFrame,
                        getFullStreamInfo(grabber.getGrabber()),
                        "Metadata",
                        JOptionPane.INFORMATION_MESSAGE
                )
        );

        Bar.add(menu);
        JMenu ptz = new JMenu("PTZ");
        JMenuItem VK_UP = new JMenuItem("UP");
        JMenuItem VK_DOWN = new JMenuItem("DOWN");
        JMenuItem VK_LEFT = new JMenuItem("LEFT");
        JMenuItem VK_RIGHT = new JMenuItem("RIGHT");
        VK_UP.addActionListener(e -> onvifPtz.move(0.0f, 0.5f, 0.0f));
        VK_DOWN.addActionListener(e -> onvifPtz.move(0.0f, -0.5f, 0.0f));
        VK_LEFT.addActionListener(e -> onvifPtz.move(-0.5f, 0.0f, 0.0f));
        VK_RIGHT.addActionListener(e -> onvifPtz.move(0.5f, 0.5f, 0.0f));
        ptz.add(VK_UP);
        ptz.add(VK_DOWN);
        ptz.add(VK_LEFT);
        ptz.add(VK_RIGHT);
        Bar.add(ptz);
        videoFrame.setJMenuBar(Bar);
        videoFrame.getCanvas().addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                        onvifPtz.move(0.0f, 0.5f, 0.0f);
                        break;
                    case KeyEvent.VK_DOWN:
                        onvifPtz.move(0.0f, -0.5f, 0.0f);
                        break;
                    case KeyEvent.VK_LEFT:
                        onvifPtz.move(-0.5f, 0.0f, 0.0f);
                        break;
                    case KeyEvent.VK_RIGHT:
                        onvifPtz.move(0.5f, 0.0f, 0.0f);
                        break;
                }
            }
        });
        videoFrame.getCanvas().setFocusable(true);
        videoFrame.getCanvas().requestFocus();
        videoFrame.setVisible(true);
    }

    @Override
    public void update(Frame buff) {
        videoFrame.showImage(buff);

    }

    @Override
    public void stop() {
        videoFrame.dispose();
    }
}
