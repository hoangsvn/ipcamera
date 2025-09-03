package com.hoangicloudvn.device;


public record OnvifDevice(String ip, String username, String password) {

    @Override
    public String toString() {
        return String.format("Device(ip:%s-u:%s-p:%s)", ip, username, password);
    }
}
