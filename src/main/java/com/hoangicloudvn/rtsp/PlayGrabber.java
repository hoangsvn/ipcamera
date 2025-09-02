package com.hoangicloudvn.rtsp;

import com.hoangicloudvn.stream.Audio;
import com.hoangicloudvn.stream.Video;
import org.bytedeco.javacv.Frame;

import javax.sound.sampled.AudioFormat;
import java.nio.ShortBuffer;

public class PlayGrabber implements Grabber {

    RTSPGrabber grabber;
    Video render;
    Audio audio;


    public PlayGrabber(RTSPGrabber grabber, Video render, Audio audio) {
        this.grabber = grabber;
        this.render = render;
        this.audio = audio;

    }

    @Override
    public void run() throws Exception {
        render.init();
        AudioFormat audioFormat = new AudioFormat(grabber.getGrabber().getSampleRate(), 16, grabber.getGrabber().getAudioChannels(), true, false);
        audio.init(audioFormat);
        Frame frame;

        while ((frame = grabber.getGrabber().grab()) != null) {
            if (frame.image != null) {
                render.update(frame);
            } else if (frame.samples != null) {
                ShortBuffer buffer = (ShortBuffer) frame.samples[0];
                int length = buffer.remaining();
                byte[] bytes = new byte[length * 2];
                for (int i = 0; i < length; i++) {
                    short sample = buffer.get();
                    bytes[i * 2] = (byte) (sample & 0xFF);
                    bytes[i * 2 + 1] = (byte) (sample >> 8);
                }
                audio.update(bytes);
            }
        }
        audio.stop();
        render.stop();
        grabber.getGrabber().stop();
    }


}
