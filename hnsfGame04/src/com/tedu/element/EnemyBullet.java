package com.tedu.element;

import java.awt.Graphics;

import javax.swing.ImageIcon;

public class EnemyBullet extends ElementObj {
    public EnemyBullet(int x, int y, int w, int h, ImageIcon icon) {
        super(x, y, w, h, icon);
    }

    @Override
    public void showElement(Graphics g) {
        g.drawImage(icon.getImage(), x, y, w, h, null);
    }

    @Override
    public void move() {
        y += 5;
        if (y > 800) {
            die();
        }
    }

    @Override
    public void model(long gameTime) {
        // 敌人子弹行为逻辑
        move();
    }
}