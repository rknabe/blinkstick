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

                // byte[] data = new byte[32 * 3];
                BufferedImage img;
                for (int i = 0; i < 1000000; i++) {
                    img = robot.createScreenCapture(rectangle);
                    //Color avgColor = getDominantColor(img);
                    java.awt.Color avgColor = getAverageColor(img);
                    //for (int index = 0; index < 32 * 3; ) {
                    //    data[index++] = (byte) avgColor.getRed();
                    //    data[index++] = (byte) avgColor.getGreen();
                    //    data[index++] = (byte) avgColor.getBlue();
                    //}
                    //blinkStick.setColors(data);
                    blinkStick.setAllColors((byte) 32, (byte) avgColor.getRed(), (byte) avgColor.getGreen(), (byte) avgColor.getBlue());
                    //blinkStick.setColor(avgColor.getRed(), avgColor.getGreen(), avgColor.getBlue());
                    //System.out.println(avgColor.toString());
                    Thread.sleep(200);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                blinkStick.turnOff();
            }
        }
    }

    private static BlinkStick getBlinkStick() {
        return Usb.findFirstBlinkStick().orElse(null);
    }

    private static java.awt.Color getAverageColor(BufferedImage img) {
        Image scaledImg = img.getScaledInstance(1, 1, Image.SCALE_AREA_AVERAGING);
        BufferedImage img2 = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img2.createGraphics();
        g2d.drawImage(scaledImg, 0, 0, 1, 1, null);
        g2d.dispose();
        return new java.awt.Color(img2.getRGB(0, 0));

    }

    private static Color getAverageColor2(BufferedImage img, int x0, int y0, int w, int h) {
        int x1 = x0 + w;
        int y1 = y0 + h;
        long sumr = 0, sumg = 0, sumb = 0;
        for (int x = x0; x < x1; x++) {
            for (int y = y0; y < y1; y++) {
                int color = img.getRGB(x, y);
                int rgb = 0xff000000 | color;
                ///Color pixel = new Color(color);
                //sumr += pixel.getRed();
                //sumg += pixel.getGreen();
                //sumb += pixel.getBlue();
                sumr = ((rgb >> 16) & 0xFF);
                sumg += ((rgb >> 8) & 0xFF);
                sumb += ((rgb >> 0) & 0xFF);
            }
        }
        int num = w * h;
        return new Color((int) sumr / num, (int) sumg / num, (int) sumb / num);
    }

}
