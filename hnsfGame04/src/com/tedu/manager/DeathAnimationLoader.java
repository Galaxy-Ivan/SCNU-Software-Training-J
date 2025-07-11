package com.tedu.manager;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;

public class DeathAnimationLoader {
    /**
     * 从单张雪碧图中加载爆炸动画序列
     */
    public static List<ImageIcon> loadDeathAnimation() {
        List<ImageIcon> frames = new ArrayList<>();
        
        // 加载雪碧图
        ImageIcon spriteSheet = GameLoad.getImg("fly/bang1.png");
        
        if (spriteSheet == null || spriteSheet.getIconWidth() <= 0) {
            System.err.println("死亡动画雪碧图加载失败");
            return frames;
        }
        
        // 将ImageIcon转换为BufferedImage以便操作
        BufferedImage spriteImage = new BufferedImage(
            spriteSheet.getIconWidth(),
            spriteSheet.getIconHeight(),
            BufferedImage.TYPE_INT_ARGB);
        
        spriteImage.getGraphics().drawImage(spriteSheet.getImage(), 0, 0, null);
        
        // 根据图片内容分析帧信息
        int totalFrames = 8;      // 图片中一共有8个爆炸帧
        int frameWidth = spriteImage.getWidth() / totalFrames; // 每帧宽度
        int frameHeight = spriteImage.getHeight();  // 每帧高度（整个图片高度）
        
        // 切割雪碧图
        for (int i = 0; i < totalFrames; i++) {
            BufferedImage frameImage = spriteImage.getSubimage(
                i * frameWidth, 
                0, 
                frameWidth, 
                frameHeight
            );
            frames.add(new ImageIcon(frameImage));
        }
        
        return frames;
    }
}