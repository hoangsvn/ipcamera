package com.hoangicloudvn.stream;

import javax.sound.sampled.AudioFormat;

public interface Audio {
    void init(AudioFormat audioFormat);

    void update(byte[] buff);

    void stop();
}
