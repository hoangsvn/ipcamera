package com.hoangicloudvn.video;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class BasePanel implements Render {
    JFrame frame = new JFrame("Live");
    VideoPanel video = new VideoPanel();

    @Override
    public void init() {
        try {
            // Đặt Look and Feel về Windows
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        frame.setSize(853, 480);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setFocusable(true);
        frame.requestFocusInWindow();

        JMenuBar menuBar = new JMenuBar();
        // Menu Device
        JMenu menuDevice = new JMenu("Device");
        JMenuItem infoItem = new JMenuItem("Info");
        JMenuItem wifiItem = new JMenuItem("WiFi");

        infoItem.addActionListener(e ->
                JOptionPane.showMessageDialog(
                        frame,
                        "Thông tin thiết bị:\nIP: 192.168.0.199\nModel: ONVIF Camera",
                        "Device Info",
                        JOptionPane.INFORMATION_MESSAGE
                )
        );

        wifiItem.addActionListener(e ->
                JOptionPane.showMessageDialog(
                        frame,
                        "WiFi Status: Connected\nSSID: Camera-WiFi\nSignal: -65 dBm",
                        "WiFi Info",
                        JOptionPane.INFORMATION_MESSAGE
                )
        );

        menuDevice.add(infoItem);
        menuDevice.add(wifiItem);

        menuBar.add(menuDevice);

        // Gắn menu bar vào frame
        frame.setJMenuBar(menuBar);


        frame.add(video);
        frame.setVisible(true);
    }

    @Override
    public void update(BufferedImage buff) {
        video.updateImage(buff);

    }

    @Override
    public void stop() {
        frame.dispose();
    }

    public JFrame getFrame() {
        return frame;
    }

    static class VideoPanel extends JPanel {
        private BufferedImage image;

        public synchronized void updateImage(BufferedImage newImage) {
            this.image = newImage;
            repaint();
        }

        @Override
        protected synchronized void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (image != null) g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
        }
    }
}
