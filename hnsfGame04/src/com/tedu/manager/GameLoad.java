package com.tedu.manager;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class GameLoad {
    private static final String BASE_PATH = "image/"; // 相对于项目根目录

    public static ImageIcon getImg(String relativePath) {
        String path = BASE_PATH + relativePath;
        File file = new File(path);
        if (file.exists()) {
            return new ImageIcon(path);
        } else {
            System.err.println("找不到资源: " + path);
            return null;
        }
    }
    public static ImageIcon createColoredIcon(int width, int height, Color color) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g2d = image.createGraphics();
        g2d.setColor(color);
        g2d.fillRect(0, 0, width, height);
        g2d.dispose();
        return new ImageIcon(image);
    }
    
}