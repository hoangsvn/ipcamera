package com.hoangicloudvn.video;

import java.awt.image.BufferedImage;

public interface Render {
    void init();

    void update(BufferedImage buff);

    void stop();
}
