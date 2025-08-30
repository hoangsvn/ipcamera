package com.hoangicloudvn.device;

public record DeviceNetworkInterface (String token, boolean enabled, String name, String hwAddress,
                                  boolean ipv4Enabled, String ipAddress, int prefixLength, boolean dhcp){

    @Override
    public String toString() {
        return String.format("""
                Token: %s
                Enabled: %s
                Name: %s
                HW Address: %s
                IPv4 Enabled: %s
                IP Address: %s
                Prefix Length: %d
                DHCP: %s
                """, token, enabled, name, hwAddress, ipv4Enabled, ipAddress, prefixLength, dhcp);
    }
}
