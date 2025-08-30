package com.hoangicloudvn.device;

public record DeviceOnvifInformation(String manufacturer, String model,
                                     String firmwareVersion, String serialNumber,
                                     String hardwareId) {

    @Override
    public String toString() {
        return String.format("""
                Manufacturer: %s
                Model: %s
                Firmware: %s
                Serial: %s
                Hardware: %s
                """, manufacturer, model, firmwareVersion, serialNumber, hardwareId);
    }
}
