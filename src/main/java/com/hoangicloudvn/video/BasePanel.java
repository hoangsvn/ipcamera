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
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        frame.setSize(853, 480);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setFocusable(true);
        frame.requestFocusInWindow();
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
