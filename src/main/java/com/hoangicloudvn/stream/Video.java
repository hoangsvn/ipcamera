package com.hoangicloudvn.stream;

import org.bytedeco.javacv.Frame;


public interface Video {
    void init();

    void update(Frame buff);

    void stop();
}
