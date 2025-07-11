package com.tedu.element;

import java.awt.Graphics;
import javax.swing.ImageIcon;

public class Background extends ElementObj {
    public Background(int x, int y, int w, int h, ImageIcon icon) {
        super(x, y, w, h, icon);
    }

    @Override
    public void showElement(Graphics g) {
        g.drawImage(icon.getImage(), x, y, w, h, null);
    }

    @Override
    public void model(long gameTime) {
        // 背景行为逻辑（如果有）
        // 可以在这里添加背景滚动等逻辑
    }
}