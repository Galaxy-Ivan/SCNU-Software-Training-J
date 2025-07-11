package com.tedu.element;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import com.tedu.manager.DeathAnimationLoader;
import com.tedu.manager.GameLoad;
import com.tedu.manager.GameElement;
import com.tedu.manager.ElementManager;

public class DeathAnimation extends ElementObj {
    private List<ImageIcon> animationFrames;
    private int currentFrame;
    private long lastUpdateTime;
    private boolean isFinished;
    private int frameDelay = 50; // 每帧显示时间(ms)
    private int totalFrames;    // 总帧数

    public DeathAnimation(ElementObj obj) {
        // 从爆炸对象中心点开始绘制，宽度和高度根据第一帧确定
        super(obj.getX() + obj.getW()/2, obj.getY() + obj.getH()/2, 0, 0, null);
        
        animationFrames = DeathAnimationLoader.loadDeathAnimation();
        totalFrames = animationFrames.size();
        
        if (totalFrames > 0) {
            // 设置初始宽高为第一帧的尺寸
            w = animationFrames.get(0).getIconWidth();
            h = animationFrames.get(0).getIconHeight();
            
            // 调整位置居中于爆炸点
            x -= w / 2;
            y -= h / 2;
        } else {
            // 创建简单的备用动画（红-橙闪烁）
            loadFallbackAnimation();
            w = 80;
            h = 80;
            x -= w / 2;
            y -= h / 2;
        }
        
        currentFrame = 0;
        lastUpdateTime = System.currentTimeMillis();
        isFinished = false;
    }
    
    // 备用动画加载
    private void loadFallbackAnimation() {
        animationFrames = new ArrayList<>();
        try {
            // 创建5帧简单动画
            for (int i = 0; i < 5; i++) {
                animationFrames.add(GameLoad.createColoredIcon(80, 80, 
                    (i % 2 == 0) ? java.awt.Color.RED : java.awt.Color.ORANGE));
            }
            totalFrames = 5;
        } catch (Exception e) {
            System.err.println("创建备用动画失败: " + e.getMessage());
            // 最终保障 - 添加至少一个空帧
            animationFrames.add(new ImageIcon());
            totalFrames = 1;
        }
    }

    @Override
    public void showElement(Graphics g) {
        if (currentFrame < totalFrames) {
            g.drawImage(animationFrames.get(currentFrame).getImage(), x, y, w, h, null);
        }
    }

    @Override
    public void model(long gameTime) {
        if (isFinished) return;
        
        // 检查是否需要切换到下一帧
        if (gameTime - lastUpdateTime > frameDelay) {
            currentFrame++;
            lastUpdateTime = gameTime;
            
            // 播放完所有帧后标记结束
            if (currentFrame >= totalFrames) {
                isFinished = true;
                die(); // 动画结束，移除此对象
            }
        }
    }
    
    @Override
    public boolean isLive() {
        return !isFinished;
    }
}