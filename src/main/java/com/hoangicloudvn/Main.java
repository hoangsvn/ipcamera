package com.hoangicloudvn;

import com.hoangicloudvn.device.OnvifDevice;
import com.hoangicloudvn.ptz.OnvifPtz;
import com.hoangicloudvn.rtsp.PlayGrabber;
import com.hoangicloudvn.rtsp.RTSPGrabber;
import com.hoangicloudvn.stream.BaseAudio;
import com.hoangicloudvn.stream.BaseStream;

import javax.swing.*;
import java.awt.*;


public class Main {

    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        String ip;
        String username;
        String password;

        // Nếu không đủ 3 tham số → hiện form nhập
        if (args.length < 3) {
            JTextField ipField = new JTextField(15);
            JTextField userField = new JTextField(10);
            JPasswordField passField = new JPasswordField(10);

            JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
            panel.add(new JLabel("IP:"));
            panel.add(ipField);
            panel.add(new JLabel("Username:"));
            panel.add(userField);
            panel.add(new JLabel("Password:"));
            panel.add(passField);

            int result = JOptionPane.showConfirmDialog(
                    null,
                    panel,
                    "Enter Camera Credentials",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE
            );

            if (result != JOptionPane.OK_OPTION) {
                return;
            }

            ip = ipField.getText().trim();
            username = userField.getText().trim();
            password = new String(passField.getPassword());
        } else {
            ip = args[0];
            username = args[1];
            password = args[2];
        }

        int init = 50;
        OnvifDevice camera = new OnvifDevice(ip, username, password);
        RTSPGrabber grabber = new RTSPGrabber(camera, 554);
        grabber.run();

        OnvifPtz ptzClient = new OnvifPtz(camera, 5000);
        BaseStream stream = new BaseStream(camera, ptzClient, grabber, 16 * init, 9 * init);
        BaseAudio audio = new BaseAudio();

        PlayGrabber view = new PlayGrabber(grabber, stream, audio);
        view.run();
    }
}
