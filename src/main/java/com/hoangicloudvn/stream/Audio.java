package com.hoangicloudvn.stream;

import javax.sound.sampled.AudioFormat;
import java.awt.image.BufferedImage;

public interface Audio {
    void init(AudioFormat audioFormat);

    void update(byte[] buff);

    void stop();
}
