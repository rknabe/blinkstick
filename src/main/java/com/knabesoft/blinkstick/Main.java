package com.knabesoft.blinkstick;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Main {

    public static void main(String... args) {
        BlinkStick blinkStick = getBlinkStick();
        if (blinkStick != null) {
            try {
                Rectangle rectangle = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
                Robot robot = new Robot();

                System.out.println("starting");
                blinkStick.setAllColors((byte) 32, (byte) 0, (byte) 0, (byte) 0);
                BufferedImage img;
                for (int i = 0; i < 1000000; i++) {
                    img = robot.createScreenCapture(rectangle);
                    int avgColor = getAverageColor(img);
                    blinkStick.setAllColors((byte) 32, avgColor);
                    Thread.sleep(200);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                blinkStick.turnOff();
            }
        }
    }

    private static int getAverageColor(BufferedImage img) {
        Image scaledImg = img.getScaledInstance(1, 1, Image.SCALE_AREA_AVERAGING);
        BufferedImage img2 = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img2.createGraphics();
        g2d.drawImage(scaledImg, 0, 0, 1, 1, null);
        g2d.dispose();
        return img2.getRGB(0, 0);
    }

    private static BlinkStick getBlinkStick() {
        return Usb.findFirstBlinkStick().orElse(null);
    }
}
