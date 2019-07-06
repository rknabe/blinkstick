package com.knabesoft.blinkstick;

import java.awt.*;
import java.awt.image.BufferedImage;

public final class Main {

    public static void main(String... args) {
        try {
            BlinkStick blinkStick = Usb.findFirstBlinkStick().orElse(null);
            if (blinkStick != null) {
                Rectangle rectangle = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
                Robot robot = new Robot();
                blinkStick.setAllColors((byte) 32, (byte) 0, (byte) 0, (byte) 0);
                BufferedImage img;
                do {
                    img = robot.createScreenCapture(rectangle);
                    int avgColor = getAverageColor(img);
                    blinkStick.setAllColors((byte) 32, avgColor);
                    Thread.sleep(200);
                }
                while (true);
            }
        } catch (Throwable t) {
            System.exit(1);
        }
        System.exit(1);
    }

    private static int getAverageColor(BufferedImage img) {
        Image scaledImg = img.getScaledInstance(1, 1, Image.SCALE_AREA_AVERAGING);
        BufferedImage img2 = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img2.createGraphics();
        g2d.drawImage(scaledImg, 0, 0, 1, 1, null);
        g2d.dispose();
        return img2.getRGB(0, 0);
    }
}
