package com.knabesoft.blinkstick;

import com.codeminders.hidapi.HIDDevice;
import com.codeminders.hidapi.HIDDeviceInfo;
import com.codeminders.hidapi.HIDManager;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.codeminders.hidapi.ClassPathLibraryLoader.loadNativeHIDLibrary;
import static java.util.stream.Collectors.toList;

public final class Usb {

    private static boolean loaded = false;

    static {
        loadLibrary();
    }

    private static boolean loadLibrary() {
        if (!loaded)
            loaded = loadNativeHIDLibrary();
        return loaded;
    }

    public static List<Device> devices() {
        return findAllDevices()
                .stream()
                .map(info -> new Device(info.getManufacturer_string(), info.getProduct_string(), info.getSerial_number(), info.getVendor_id(), info.getProduct_id()))
                .collect(toList());
    }

    static Optional<BlinkStick> findFirstBlinkStick() {
        return findAllBlinkStickDevices()
                .stream()
                .findFirst()
                .map(Usb::createBlinkStick);
    }

    public static Optional<BlinkStick> findBlinkStickBy(String serial) {
        return findAllBlinkStickDevices()
                .stream()
                .filter(info -> info.getSerial_number().equals(serial))
                .map(Usb::createBlinkStick)
                .findFirst();
    }

    public static List<BlinkStick> findAllBlinkSticks() {
        return findAllBlinkStickDevices()
                .stream()
                .map(Usb::createBlinkStick)
                .collect(toList());
    }

    private static List<HIDDeviceInfo> findAllDevices() {
        Callable<List<HIDDeviceInfo>, IOException> callable = () -> {
            HIDDeviceInfo[] devices = HIDManager.getInstance().listDevices();
            return Arrays.asList(devices);
        };
        return Callable.wrapExceptions(callable);
    }

    private static List<HIDDeviceInfo> findAllBlinkStickDevices() {
        return findAllDevices()
                .stream()
                .filter(info -> info.getVendor_id() == Device.BlinkStickVendorId && info.getProduct_id() == Device.BlinkStickProductName)
                .collect(toList());
    }

    private static BlinkStick createBlinkStick(HIDDeviceInfo info) {
        try {
            HIDDevice device = Optional.ofNullable(info.open()).orElseThrow(() ->
                    new NullPointerException("Failed to open USB device, the native open() method returned null. Could be a OS/driver issue, check your library path. Otherwise, I have no idea.")
            );
            return new BlinkStickImpl(device);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
