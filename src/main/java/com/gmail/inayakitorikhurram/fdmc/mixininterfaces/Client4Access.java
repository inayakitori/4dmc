package com.gmail.inayakitorikhurram.fdmc.mixininterfaces;

public interface Client4Access {
    boolean getScreenshotState();
    void setScreenshotState(boolean screenshotState);
    default void toggleScreenshotState(){
        setScreenshotState(!getScreenshotState());
    };

}
