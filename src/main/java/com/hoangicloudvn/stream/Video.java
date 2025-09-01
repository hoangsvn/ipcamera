package com.hoangicloudvn.stream;

import org.bytedeco.javacv.Frame;

import java.awt.image.BufferedImage;

public interface Video {
    void init();

    void update(Frame buff);

    void stop();
}
