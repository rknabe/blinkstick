package com.knabesoft.blinkstick;

public class Device {
    //HidServicesEvent{hidDevice=HidDevice [path=\\?\hid#vid_20a0&pid_41e5#6&17dee3c3&0&0000#{4d1e55b2-f16f-11cf-88cb-001111000030}, vendorId=0x20a0, productId=0x41e5, serialNumber=BS025149-3.1, releaseNumber=0x203, manufacturer=Agile Innovative Ltd, product=BlinkStick Flex, usagePage=0xffffff00, usage=0x1, interfaceNumber=-1]}
    final static int BlinkStickVendorId = 0x20a0;
    final static int BlinkStickProductName = 0x41e5;
    private final String manufacturer;
    private final String productName;
    private final String serialNumber;
    private final int vendorId;
    private final int productId;

    Device(String manufacturer, String productName, String serialNumber, int vendorId, int productId) {
        this.manufacturer = manufacturer;
        this.productName = productName;
        this.serialNumber = serialNumber;
        this.vendorId = vendorId;
        this.productId = productId;
    }

    public boolean isBlinkStick() {
        return vendorId == BlinkStickVendorId && productId == BlinkStickProductName;
    }

    @Override
    public String toString() {
        return String.format("%s %s (#%s)", manufacturer, productName, serialNumber);
    }
}
