package com.knabesoft.blinkstick;

import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.awt.*;
import java.util.Random;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        String javaVersion = System.getProperty("java.version");
        String javafxVersion = System.getProperty("javafx.version");
        Label l = new Label("Hello, JavaFX " + javafxVersion + ", running on Java " + javaVersion + ".");
        Scene scene = new Scene(new StackPane(l), 640, 480);
        stage.setScene(scene);
        stage.show();

        startDrawing();
    }

    public static void main(String[] args) {
        launch(args);
    }


    private void startDrawing() {
        try {
            final BlinkStick blinkStick = Usb.findFirstBlinkStick().orElse(null);
            if (blinkStick != null) {
                Random r = new Random();
                blinkStick.setAllColors((byte) 32, (byte) 0, (byte) 0, (byte) 0);
                Rectangle rectangle = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
                Rectangle2D rect2d = new Rectangle2D(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
                //final Robot robot = new Robot();
                javafx.scene.robot.Robot robot = new javafx.scene.robot.Robot();
                WritableImage image = new WritableImage(rectangle.height, rectangle.width);
                //blinkStick.setAllColors((byte) 32, (byte) 0, (byte) 0, (byte) 0);
                //BufferedImage img;
                byte[] avgColor = new byte[3];
                do {
                    image = robot.getScreenCapture(image, rect2d);
                    getAverageColor(image, avgColor);
                    blinkStick.setAllColors((byte) 32, avgColor[0], avgColor[1], avgColor[2]);

                    //int randomInt = r.nextInt(128);
                    //blinkStick.setAllColors((byte) 32, (byte) r.nextInt(128), (byte) r.nextInt(128), (byte) r.nextInt(128));

                    //int avgColor = getAverageColor(img);
                    //blinkStick.setAllColors((byte) 32, avgColor);

                    //int avgColor = getAverageColor3(jfxRobot, rect2d);
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

    private void getAverageColor(WritableImage img, byte[] avgColor) {
        int width = (int) img.getWidth();
        int height = (int) img.getHeight();
        int pixelCount = width * height;
        long sumr = 0, sumg = 0, sumb = 0;
        PixelReader reader = img.getPixelReader();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int color = reader.getArgb(x, y);
                sumr += (color >> 16) & 0x000000FF;
                sumg += (color >> 8) & 0x000000FF;
                sumb += (color) & 0x000000FF;
            }
        }
        avgColor[0] = (byte) (sumr / pixelCount);
        avgColor[1] = (byte) (sumg / pixelCount);
        avgColor[2] = (byte) (sumb / pixelCount);
    }
/*
    private static void getAverageColor2(BufferedImage img, byte[] avgColor) {
        int[] screenData = ((DataBufferInt) img.getRaster().getDataBuffer()).getData();
        long sumr = 0, sumg = 0, sumb = 0;
        for (int color : screenData) {
            sumr += (color >> 16) & 0x000000FF;
            sumg += (color >> 8) & 0x000000FF;
            sumb += (color) & 0x000000FF;
        }
        avgColor[0] = (byte) (sumr / screenData.length);
        avgColor[1] = (byte) (sumg / screenData.length);
        avgColor[2] = (byte) (sumb / screenData.length);
    }

    private static int getAverageColor(BufferedImage img) {
        Image scaledImg = img.getScaledInstance(1, 1, Image.SCALE_AREA_AVERAGING);
        BufferedImage img2 = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img2.createGraphics();
        g2d.drawImage(scaledImg, 0, 0, 1, 1, null);
        g2d.dispose();
        return img2.getRGB(0, 0);
    } */
}
