package com.hoangicloudvn.stream;

import javax.sound.sampled.*;


public class BaseAudio implements Audio {


    SourceDataLine soundLine;
    DataLine.Info info;

    @Override
    public void init(AudioFormat audioFormat) {
        info = new DataLine.Info(SourceDataLine.class, audioFormat);

        try {
            soundLine = (SourceDataLine) AudioSystem.getLine(info);
            soundLine.open(audioFormat);
            soundLine.start();
        } catch (LineUnavailableException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(byte[] buff) {
        soundLine.write(buff, 0, buff.length);
    }

    @Override
    public void stop() {
        soundLine.stop();
    }
}
