package com.hoangicloudvn.utils;

import com.hoangicloudvn.device.OnvifDevice;

import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

public class PreferencesManager {
    private static final String NODE_NAME = "ipcamera";
    private static final String ENTRY_KEY = "cam";

    private final Preferences prefs = Preferences.userRoot().node(NODE_NAME);

    public List<OnvifDevice> getEntries() {
        List<OnvifDevice> devices = new ArrayList<>();

        try {
            String[] keys = prefs.keys();
            for (String key : keys) {
                if (key.startsWith(ENTRY_KEY)) {
                    String value = prefs.get(key, "");
                    if (!value.isEmpty()) {
                        String[] parts = value.split("::");
                        if (parts.length == 3) {
                            devices.add(new OnvifDevice(parts[0], parts[1], parts[2]));
                        }
                    }
                }
            }
        } catch (Exception ignored) {
        }
        return devices;
    }

    public void addEntry(OnvifDevice device) {
        String entry = String.format("%s::%s::%s", device.ip(), device.username(), device.password());
        prefs.put(ENTRY_KEY.concat(String.format("ip:%S-u:%s", device.ip(), device.username())), entry);
    }

    public void removeEntry(OnvifDevice device) {

        prefs.remove(ENTRY_KEY.concat(String.format("ip:%S-u:%s", device.ip(), device.username())));
    }
}