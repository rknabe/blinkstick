package com.knabesoft.blinkstick;

public interface BlinkStick {
    Mode getMode();

    void setMode(Mode mode);

    void setColor(int r, int g, int b);

    void setColors(byte[] colorData);

    void setAllColors(byte count, byte r, byte g, byte b);

    void setColors(Channel channel, byte[] colorData);

    void setIndexedColor(int channel, int index, int r, int g, int b);

    void setIndexedColor(int channel, int index, int value);

    void setIndexedColor(int index, Color color);

    void setRandomColor();

    void turnOff();

    int getColor();

    void setColor(int value);

    void setColor(Color color);

    String getInfoBlock1();

    void setInfoBlock1(String value);

    String getInfoBlock2();

    void setInfoBlock2(String value);

    String getManufacturer();

    String getProductDescription();

    String getSerial();
}
