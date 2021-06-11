package com.knabesoft.blinkstick;

import org.hid4java.HidDevice;
import org.hid4java.HidManager;
import org.hid4java.HidServices;
import org.hid4java.HidServicesSpecification;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

public final class Usb {
    private HidServices hidServices = null;

    public Usb() {
        // Configure to use custom specification
        HidServicesSpecification hidServicesSpecification = new HidServicesSpecification();

        // Use the v0.7.0 manual start feature to get immediate attach events
        //hidServicesSpecification..setAutoStart(false);

        // Get HID services using custom specification
        hidServices = HidManager.getHidServices(hidServicesSpecification);
        //hidServices.addHidServicesListener(listener);

        // Manually start the services to get attachment event
        hidServices.start();
    }

    public BlinkStick findFirstBlinkStick() throws Exception {
        // Provide a list of attached devices
        for (HidDevice hidDevice : hidServices.getAttachedHidDevices()) {
            //System.out.println(hidDevice);
            Device device = new Device(hidDevice.getManufacturer(), hidDevice.getProduct(), hidDevice.getSerialNumber(), hidDevice.getVendorId(), hidDevice.getProductId());
            if (device.isBlinkStick()) {
                return new BlinkStickImpl(hidDevice);
            }
        }
        return null;
    }


}
