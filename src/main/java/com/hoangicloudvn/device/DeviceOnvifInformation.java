package com.hoangicloudvn.device;

public record DeviceOnvifInformation(String manufacturer, String model,
                                     String firmwareVersion, String serialNumber,
                                     String hardwareId) {

    @Override
    public String toString() {
        return String.format("""
                Model: %s
                Serial: %s
                Firmware: %s
                Hardware: %s
                Manufacturer: %s
                """, model, serialNumber, firmwareVersion, hardwareId, manufacturer);
    }
}
