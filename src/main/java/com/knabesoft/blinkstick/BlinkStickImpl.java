package com.knabesoft.blinkstick;

import org.hid4java.HidDevice;
import org.hid4java.event.HidServicesEvent;

import java.util.Random;

public class BlinkStickImpl implements BlinkStick {
    private final HidDevice device;

    BlinkStickImpl(HidDevice device) throws Exception {
        this.device = device;
        boolean isOpen = device.open();
        if (!isOpen) {
            throw new Exception("Could not open device");
        }
    }

    /**
     * Set the color of the device with separate r, g and b byte values
     *
     * @param r red byte color value 0..255
     * @param g green byte color value 0..255
     * @param b blue byte color value 0..255
     */
    @Override
    public void setColor(int r, int g, int b) {
        try {
            device.sendFeatureReport(new byte[]{(byte) r, (byte) g, (byte) b}, (byte) 1);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Set indexed color of the device with separate r, g and b byte values for channel and LED index
     *
     * @param channel Channel (0 - R, 1 - G, 2 - B)
     * @param index   Index of the LED
     * @param r       red byte color value 0..255
     * @param g       gree byte color value 0..255
     * @param b       blue byte color value 0..255
     */
    @Override
    public void setIndexedColor(int channel, int index, int r, int g, int b) {
        try {
            device.sendFeatureReport(new byte[]{(byte) channel, (byte) index, (byte) r, (byte) g, (byte) b}, (byte) 5);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Set the indexed color of BlinkStick Pro with Processing color value
     *
     * @param channel Channel (0 - R, 1 - G, 2 - B)
     * @param index   Index of the LED
     * @param value   color as int
     */
    @Override
    public void setIndexedColor(int channel, int index, int value) {
        int r = (value >> 16) & 0xFF;
        int g = (value >> 8) & 0xFF;
        int b = value & 0xFF;

        setIndexedColor(channel, index, r, g, b);
    }

    @Override
    public void setRandomColor() {
        Random random = new Random();
        setColor(random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }

    @Override
    public void turnOff() {
        setColor(0, 0, 0);
    }

    @Override
    public int getColor() {
        byte[] data = new byte[32];
        //data[0] = 1;// First byte is ReportID

        try {
            int read = device.getFeatureReport(data, (byte) 1);
            if (read > 0) {
                return (255 << 24) | (data[0] << 16) | (data[1] << 8) | data[2];
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return 0;
    }

    /**
     * Set the color of the device with Processing(?) color value
     *
     * @param value color as int
     */
    // TODO check against Android API
    @Override
    public void setColor(int value) {
        int r = (value >> 16) & 0xFF;
        int g = (value >> 8) & 0xFF;
        int b = value & 0xFF;

        setColor(r, g, b);
    }

    /**
     * Get value of InfoBlocks
     *
     * @param id InfoBlock id, should be 1 or 2 as only supported info blocks
     */
    private String getInfoBlock(int id) {
        byte[] data = new byte[32];
        //data[0] = (byte) (id + 1);

        StringBuilder result = new StringBuilder();
        try {
            int read = device.getFeatureReport(data, (byte) 1);
            if (read > 0) {
                for (byte datum : data) {
                    result.append((char) datum);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return result.toString();
    }

    /**
     * Get value of InfoBlock1
     *
     * @return The value of info block 1
     */
    @Override
    public String getInfoBlock1() {
        return getInfoBlock(1);
    }

    @Override
    public void setInfoBlock1(String value) {
        setInfoBlock(1, value);
    }

    /**
     * Get value of InfoBlock2
     *
     * @return The value of info block 2
     */
    @Override
    public String getInfoBlock2() {
        return getInfoBlock(2);
    }

    @Override
    public void setInfoBlock2(String value) {
        setInfoBlock(2, value);
    }

    private void setInfoBlock(int id, String value) {
        if (id < 1 || id > 2)
            throw new IllegalArgumentException("BlinkStick devices supports setting user strings for InfoBlock 1 or 2 as only");

        char[] charArray = value.toCharArray();
        byte[] data = new byte[32];
        //data[0] = (byte) (id + 1);

        for (int i = 0; i < charArray.length; i++) {
            if (i > 31) {
                break;
            }

            data[i] = (byte) charArray[i];
        }

        try {
            device.sendFeatureReport(data, (byte) (id + 1));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getManufacturer() {
        try {
            return device.getManufacturer();
        } catch (Exception e) {
            return "<unknown manufacturer>";
        }
    }

    @Override
    public String getProductDescription() {
        try {
            return device.getProduct();
        } catch (Exception e) {
            return "<unknown product>";
        }
    }

    @Override
    public String getSerial() {
        try {
            return device.getSerialNumber();
        } catch (Exception e) {
            return "<unknown serial#>";
        }
    }

    /**
     * Determine report id for the amount of data to be sent
     *
     * @return Returns the report id
     */
    private byte determineReportId(int length) {
        byte reportId = 9;
        //Automatically determine the correct report id to send the data to
        if (length <= 8 * 3) {
            reportId = 6;
        } else if (length <= 16 * 3) {
            reportId = 7;
        } else if (length <= 32 * 3) {
            reportId = 8;
        } else if (length <= 64 * 3) {
            reportId = 9;
        } else if (length <= 128 * 3) {
            reportId = 10;
        }

        return reportId;
    }

    /**
     * Determine the adjusted maximum amount of LED for the report
     *
     * @return Returns the adjusted amount of LED data
     */
    private byte determineMaxLeds(int length) {
        byte maxLeds = 64;
        //Automatically determine the correct report id to send the data to
        if (length <= 8 * 3) {
            maxLeds = 8;
        } else if (length <= 16 * 3) {
            maxLeds = 16;
        } else if (length <= 32 * 3) {
            maxLeds = 32;
        } else if (length <= 64 * 3) {
            maxLeds = 64;
        } else if (length <= 128 * 3) {
            maxLeds = 64;
        }

        return maxLeds;
    }

    /**
     * Send a packet of data to LEDs on channel 0 (R)
     *
     * @param colorData Report data must be a byte array in the following format: [g0, r0, b0, g1, r1, b1, g2, r2, b2 ...]
     */
    @Override
    public void setColors(byte[] colorData) {
        setColors(Channel.R, colorData);
    }

    /**
     * Send a packet of data to LEDs
     *
     * @param channel   Channel (0 - R, 1 - G, 2 - B)
     * @param colorData Report data must be a byte array in the following format: [g0, r0, b0, g1, r1, b1, g2, r2, b2 ...]
     */
    @Override
    public void setColors(Channel channel, byte[] colorData) {
        byte leds = determineMaxLeds(colorData.length);
        byte[] data = new byte[leds * 3 + 2];

        data[0] = channel.asByte();

        System.arraycopy(colorData, 0, data, 2, Math.min(colorData.length, data.length - 2));

        for (int i = colorData.length + 2; i < data.length; i++) {
            data[i] = 0;
        }

        try {
            device.sendFeatureReport(data, determineReportId(colorData.length));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setAllColors(byte leds, int rgb) {
        setAllColors(leds, (byte) ((rgb >> 16) & 0x000000FF), (byte) ((rgb >> 8) & 0x000000FF), (byte) (rgb & 0x000000FF));
    }

    @Override
    public void setAllColors(byte leds, byte r, byte g, byte b) {
        byte[] data = new byte[leds * 3 + 1];
        int i = 0;
        data[i++] = Channel.R.asByte();
        while (i < data.length) {
            data[i++] = g;
            data[i++] = r;
            data[i++] = b;
        }

        try {
            device.sendFeatureReport(data, determineReportId(data.length - 1));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Mode getMode() {
        byte[] data = new byte[1];

        try {
            int read = device.getFeatureReport(data, ReportId.ModeReportId.value());
            if (read > 0) {
                return Mode.fromByte(data[0]);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return Mode.Unknown;
    }

    @Override
    public void setMode(Mode mode) {
        Mode currentMode = getMode();
        if (currentMode != mode) {
            try {
                device.sendFeatureReport(new byte[]{ReportId.ModeReportId.value(), mode.asByte()}, (byte) 1);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void colorChanged(int r, int g, int b, float a) {
        //System.out.println(String.format("r:%d, g:%d, b:%d, a:%f", r, g, b, a));
        setAllColors((byte) 32, (byte) r, (byte) g, (byte) b);
    }

    @Override
    public void hidDeviceAttached(HidServicesEvent hidServicesEvent) {
        //TODO: compare to active device
        System.out.println("Attached:" + hidServicesEvent.getHidDevice().toString());
    }

    @Override
    public void hidDeviceDetached(HidServicesEvent hidServicesEvent) {
        System.out.println("Detached:" + hidServicesEvent.getHidDevice().toString());
    }

    @Override
    public void hidFailure(HidServicesEvent hidServicesEvent) {
        System.out.println("Failure:" + hidServicesEvent.getHidDevice().toString());
    }
}
