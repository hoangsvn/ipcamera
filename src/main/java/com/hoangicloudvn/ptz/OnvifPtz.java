package com.hoangicloudvn.ptz;

import com.hoangicloudvn.device.OnvifDevice;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class OnvifPtz implements Ptz {


    private final OnvifDevice device;
    private final int port;

    public OnvifPtz(OnvifDevice device, int port) {
        this.device = device;
        this.port = port;
    }

    private void sendSoap(String body) {
        try {
            String soapXml =
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                            + "<s:Envelope xmlns:s=\"http://www.w3.org/2003/05/soap-envelope\">"
                            + "<s:Body>"
                            + body
                            + "</s:Body>"
                            + "</s:Envelope>";


            URL url = new URL("http://".concat(device.ip()).concat(":").concat(String.valueOf(port)).concat("/onvif/PTZ"));
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

            int responseCode = conn.getResponseCode();
            System.out.println("Response Code: " + responseCode);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void move(float pan, float tilt, float zoom) {
        String PROFILE_TOKEN = "CusTom";
        String body = "<ContinuousMove xmlns=\"http://www.onvif.org/ver20/ptz/wsdl\">"
                + "<ProfileToken>" + PROFILE_TOKEN + "</ProfileToken>"
                + "<Velocity>"
                + "<PanTilt x=\"" + pan + "\" y=\"" + tilt + "\" xmlns=\"http://www.onvif.org/ver10/schema\"/>"
                + "<Zoom x=\"" + zoom + "\" xmlns=\"http://www.onvif.org/ver10/schema\"/>"
                + "</Velocity>"
                + "</ContinuousMove>";
        sendSoap(body);
    }

    @Override
    public void send() {

    }
}
