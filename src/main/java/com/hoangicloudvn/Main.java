package com.hoangicloudvn;

import com.hoangicloudvn.device.OnvifDevice;
import com.hoangicloudvn.ptz.OnvifPtz;
import com.hoangicloudvn.rtsp.PlayGrabber;
import com.hoangicloudvn.rtsp.RTSPGrabber;
import com.hoangicloudvn.stream.BaseAudio;
import com.hoangicloudvn.stream.BaseStream;
import com.hoangicloudvn.utils.PreferencesManager;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        OnvifDevice camera;
        PreferencesManager preferencesManager = new PreferencesManager();
        String ip;
        String username;
        String password;

        List<OnvifDevice> cameras = preferencesManager.getEntries();

        if (args.length < 3) {
            JComboBox<OnvifDevice> historyBox = new JComboBox<>(cameras.toArray(new OnvifDevice[0]));
            historyBox.setEditable(false);

            JButton deleteButton = new JButton("Delete");

            JTextField ipField = new JTextField(15);
            JTextField userField = new JTextField(10);
            JPasswordField passField = new JPasswordField(10);

            deleteButton.addActionListener(e -> {
                OnvifDevice selected = (OnvifDevice) historyBox.getSelectedItem();
                if (selected != null) {
                    preferencesManager.removeEntry(selected);
                    historyBox.removeItem(selected);
                }
            });


            JPanel panel = new JPanel(new GridLayout(5, 2, 5, 5));
            panel.add(new JLabel("History:"));
            panel.add(historyBox);
            panel.add(new JLabel("IP:"));
            panel.add(ipField);
            panel.add(new JLabel("Username:"));
            panel.add(userField);
            panel.add(new JLabel("Password:"));
            panel.add(passField);
            panel.add(new JLabel("Action"));
            panel.add(deleteButton);
            panel.setPreferredSize(new Dimension(600, 150));

            historyBox.addActionListener(e -> {
                OnvifDevice selected = (OnvifDevice) historyBox.getSelectedItem();
                if (selected != null) {
                    ipField.setText(selected.ip());
                    userField.setText(selected.username());
                    passField.setText(selected.password());
                }
            });
            Object[] options = {"OK", "Cancel", "Delete"};

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
            password = new String(passField.getPassword()).trim();

            camera = new OnvifDevice(ip, username, password);
            preferencesManager.addEntry(camera);
        } else {
            ip = args[0];
            username = args[1];
            password = args[2];
            camera = new OnvifDevice(ip, username, password);
            preferencesManager.addEntry(camera);
        }

        int init = 50;

        RTSPGrabber grabber = new RTSPGrabber(camera, 554);
        System.out.println(camera);
        grabber.run();

        OnvifPtz ptzClient = new OnvifPtz(camera, 5000);
        BaseStream stream = new BaseStream(camera, ptzClient, grabber, 16 * init, 9 * init);
        BaseAudio audio = new BaseAudio();

        PlayGrabber view = new PlayGrabber(grabber, stream, audio);
        view.run();
    }
}