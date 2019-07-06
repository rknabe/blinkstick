package com.knabesoft.blinkstick;

public interface BlinkStick {
    Mode getMode();

    void setMode(Mode mode);

    void setColor(int r, int g, int b);

    int getColor();

    void setColor(int value);

    void setColors(byte[] colorData);

    void setAllColors(byte count, byte r, byte g, byte b);

    void setAllColors(byte count, int rgb);

    void setColors(Channel channel, byte[] colorData);

    void setIndexedColor(int channel, int index, int r, int g, int b);

    void setIndexedColor(int channel, int index, int value);

    void setRandomColor();

    void turnOff();

    String getInfoBlock1();

    void setInfoBlock1(String value);

    String getInfoBlock2();

    void setInfoBlock2(String value);

    String getManufacturer();

    String getProductDescription();

    String getSerial();
}
