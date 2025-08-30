package com.hoangicloudvn;

import com.hoangicloudvn.device.DeviceNetworkInterface;
import com.hoangicloudvn.device.DeviceOnvifInformation;
import com.hoangicloudvn.device.OnvifDevice;
import com.hoangicloudvn.ptz.OnvifPtz;
import com.hoangicloudvn.rtsp.DeviceRTSPGrabber;
import com.hoangicloudvn.video.BasePanel;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Main {

    public static void main(String[] args) throws Exception {
        if (args.length < 3) {
            return;
        }

        OnvifDevice camera = new OnvifDevice(args[0], args[1], args[2]);

        OnvifPtz ptzClient = new OnvifPtz(camera, 5000);
        BasePanel panel = new BasePanel();
        DeviceRTSPGrabber grabber = new DeviceRTSPGrabber(camera, 554, 1920, 1080, panel);
        panel.getFrame().addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                        ptzClient.move(0.0f, 0.5f, 0.0f);
                        break;
                    case KeyEvent.VK_DOWN:
                        ptzClient.move(0.0f, -0.5f, 0.0f);
                        break;
                    case KeyEvent.VK_LEFT:
                        ptzClient.move(-0.5f, 0.0f, 0.0f);
                        break;
                    case KeyEvent.VK_RIGHT:
                        ptzClient.move(0.5f, 0.0f, 0.0f);
                        break;
                }
            }
        });
        JMenuBar menuBar = getJMenuBar(panel, ptzClient);
        panel.getFrame().setJMenuBar(menuBar);
        panel.getFrame().setVisible(true);
        grabber.run();

    }

    private static JMenuBar getJMenuBar(BasePanel panel, OnvifPtz ptzClient) {
        JMenuBar menuBar = new JMenuBar();

        DeviceOnvifInformation info = ptzClient.getDeviceonvifInfo();
        DeviceNetworkInterface net = ptzClient.getNetWorkInterface();
        JMenuItem infoItem = new JMenuItem("Info "+info.model());
        JMenuItem wifiItem = new JMenuItem("Wifi "+net.name());


        infoItem.addActionListener(e ->
                JOptionPane.showMessageDialog(
                        panel.getFrame(),
                        info.toString(),
                        "Device Info",
                        JOptionPane.INFORMATION_MESSAGE
                )
        );

        wifiItem.addActionListener(e ->
                JOptionPane.showMessageDialog(
                        panel.getFrame(),
                        net.toString(),
                        "WiFi Info",
                        JOptionPane.INFORMATION_MESSAGE
                )
        );
        menuBar.add(infoItem);
        menuBar.add(wifiItem);
        return menuBar;
    }
}
