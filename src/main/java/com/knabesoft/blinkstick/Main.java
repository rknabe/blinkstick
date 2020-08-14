package com.knabesoft.blinkstick;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public final class Main {

    public static void main(String... args) {
        try {
            final BlinkStick blinkStick = Usb.findFirstBlinkStick().orElse(null);
            if (blinkStick != null) {
                String desc = blinkStick.getProductDescription();
                System.out.println(desc);
                System.out.println(blinkStick.getSerial());
                System.out.println(blinkStick.getManufacturer());
                Rectangle rectangle = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
                Robot robot = new Robot();
                byte[] colors = new byte[]{(byte) (0), (byte) (0), (byte) (0)};
                clearAll(blinkStick);
                Thread hook = new Thread(() -> clearAll(blinkStick));
                Runtime.getRuntime().addShutdownHook(hook);
                BufferedImage img;
                do {
                    img = robot.createScreenCapture(rectangle);
                    byte[] avgColor = getAverageColor2(img, colors);
                    blinkStick.setAllColors((byte) 32, avgColor[0], avgColor[1], avgColor[2]);
                    //blinkStick.setColor(255, 255, 255);
                    //Thread.sleep(2000);
                    //blinkStick.setColor(0, 0, 0);

                    //int avgColor = getAverageColor(img);
                    //blinkStick.setAllColors((byte) 32, avgColor);

                    Thread.sleep(200);
                }
                while (true);
            }
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
        System.exit(1);
    }

    private static void clearAll(BlinkStick blinkStick) {
        blinkStick.setAllColors((byte) 32, (byte) 0, (byte) 0, (byte) 0);
    }

    private static byte[] getAverageColor2(BufferedImage img, byte[] colors) {
        int[] screenData = ((DataBufferInt) img.getRaster().getDataBuffer()).getData();
        long sumr = 0, sumg = 0, sumb = 0;
        for (int color : screenData) {
            sumr += (color >> 16) & 0x000000FF;
            sumg += (color >> 8) & 0x000000FF;
            sumb += (color) & 0x000000FF;
        }
        int num = screenData.length;
        colors[0]= (byte) (sumr / num);
        colors[1]= (byte) (sumg / num);
        colors[2]= (byte) (sumb / num);
        return colors;
    }

    private byte[] getAverageColor(BufferedImage img) {
        Image scaledImg = img.getScaledInstance(1, 1, Image.SCALE_AREA_AVERAGING);
        BufferedImage img2 = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img2.createGraphics();
        g2d.drawImage(scaledImg, 0, 0, 1, 1, null);
        g2d.dispose();
        int color = img2.getRGB(0, 0);
        return new byte[]{(byte) ((color >> 16) & 0x000000FF),
                (byte) ((color >> 8) & 0x000000FF),
                (byte) ((color) & 0x000000FF)};
    }
}
