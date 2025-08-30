package com.hoangicloudvn.ptz;

import com.hoangicloudvn.device.DeviceOnvifInformation;
import com.hoangicloudvn.device.DeviceNetworkInterface;
import com.hoangicloudvn.device.OnvifDevice;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public record OnvifPtz(OnvifDevice device, int port) implements Ptz {

    private String sendSoap(String body, String path) {
        try {
            String soapXml = String.format("""
                    <?xml version="1.0" encoding="UTF-8"?>
                    <s:Envelope xmlns:s="http://www.w3.org/2003/05/soap-envelope">
                        <s:Body>
                            %s
                        </s:Body>
                    </s:Envelope>
                    """, body);
            URL url = new URL(String.format("http://%s:%d%s", device.ip(), port, path));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/soap+xml; charset=utf-8");
            String encodedAuth = Base64.getEncoder().encodeToString(device.username().concat(":").concat(device.password()).getBytes(StandardCharsets.UTF_8));
            conn.setRequestProperty("Authorization", "Basic " + encodedAuth);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(soapXml.getBytes(StandardCharsets.UTF_8));
                os.flush();
            }
            int code = conn.getResponseCode();
            InputStream is = (code == 200) ? conn.getInputStream() : conn.getErrorStream();
            StringBuilder response = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
            }
            if (code < 200 || code >= 300) {
                throw new RuntimeException(response.toString());
            }
            return response.toString();

        } catch (Exception e) {
            throw new RuntimeException("Not connect to ip");
        }
    }

    public void move(float pan, float tilt, float zoom) {
        String PROFILE_TOKEN = "CusTom";
        String body = String.format("""
                <ContinuousMove xmlns="http://www.onvif.org/ver20/ptz/wsdl">
                    <ProfileToken>%s</ProfileToken>
                    <Velocity>
                        <PanTilt x="%s" y="%s" xmlns="http://www.onvif.org/ver10/schema"/>
                        <Zoom x="%s" xmlns="http://www.onvif.org/ver10/schema"/>
                    </Velocity>
                </ContinuousMove>
                """, PROFILE_TOKEN, pan, tilt, zoom);
        sendSoap(body, "/onvif/ptz_service");
    }


    public DeviceNetworkInterface getNetWorkInterface() {
        try {
            String rp = sendSoap("<tds:GetNetworkInterfaces/>", "/onvif/device_service");
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new ByteArrayInputStream(rp.getBytes(StandardCharsets.UTF_8)));
            XPath xpath = XPathFactory.newInstance().newXPath();

            String token = xpath.evaluate("//*[local-name()='NetworkInterfaces']/@token", doc);
            boolean enabled = Boolean.parseBoolean(xpath.evaluate("//*[local-name()='NetworkInterfaces']/*[local-name()='Enabled']", doc));
            String name = xpath.evaluate("//*[local-name()='NetworkInterfaces']/*[local-name()='Info']/*[local-name()='Name']", doc);
            String hwAddress = xpath.evaluate("//*[local-name()='NetworkInterfaces']/*[local-name()='Info']/*[local-name()='HwAddress']", doc);
            boolean ipv4Enabled = Boolean.parseBoolean(xpath.evaluate("//*[local-name()='NetworkInterfaces']/*[local-name()='IPv4']/*[local-name()='Enabled']", doc));
            String ipAddress = xpath.evaluate("//*[local-name()='NetworkInterfaces']/*[local-name()='IPv4']/*[local-name()='Config']/*[local-name()='FromDHCP']/*[local-name()='Address']", doc);
            int prefixLength = Integer.parseInt(xpath.evaluate("//*[local-name()='NetworkInterfaces']/*[local-name()='IPv4']/*[local-name()='Config']/*[local-name()='FromDHCP']/*[local-name()='PrefixLength']", doc));
            boolean dhcp = Boolean.parseBoolean(xpath.evaluate("//*[local-name()='NetworkInterfaces']/*[local-name()='IPv4']/*[local-name()='Config']/*[local-name()='DHCP']", doc));

            return new DeviceNetworkInterface(token, enabled, name, hwAddress, ipv4Enabled, ipAddress, prefixLength, dhcp);

        } catch (Exception e) {
            throw new RuntimeException("Cannot get NetworkInterface");
        }
    }

    public DeviceOnvifInformation getDeviceonvifInfo() {
        try {
            String rp = sendSoap("<tds:GetDeviceInformation/>", "/onvif/device_service");
            // Tạo Document từ chuỗi XML
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true); // Quan trọng khi có namespace như tds:
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new ByteArrayInputStream(rp.getBytes(StandardCharsets.UTF_8)));

            // Tạo XPath
            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xpath = xPathFactory.newXPath();

            // Lấy các giá trị
            String manufacturer = xpath.evaluate("//*[local-name()='Manufacturer']", doc);
            String model = xpath.evaluate("//*[local-name()='Model']", doc);
            String firmware = xpath.evaluate("//*[local-name()='FirmwareVersion']", doc);
            String serial = xpath.evaluate("//*[local-name()='SerialNumber']", doc);
            String hardware = xpath.evaluate("//*[local-name()='HardwareId']", doc);

            return new DeviceOnvifInformation(manufacturer, model, firmware, serial, hardware);

        } catch (Exception e) {
            throw new RuntimeException("Cannot get DeviceInformation");
        }
    }

    @Override
    public void send() {

    }
}
