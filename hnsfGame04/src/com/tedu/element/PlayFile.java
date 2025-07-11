package com.tedu.element;

import java.awt.Graphics;
import javax.swing.ImageIcon;

public class PlayFile extends ElementObj {
    private int attackPower = 1; // 子弹攻击力
    private int distanceTraveled = 0; // 记录飞行距离
    private static final int MAX_DISTANCE = 800; // 最大射程
    public PlayFile(int x, int y, int w, int h, ImageIcon icon) {
        super(x, y, w, h, icon);
    }

    @Override
    public void showElement(Graphics g) {
        g.drawImage(icon.getImage(), x, y, w, h, null);
    }

    @Override
    public void move() {
        y -= 30;
        distanceTraveled += 30; // 更新飞行距离
        
        // 超出屏幕或超过最大射程时消失
        if(y < 0 || distanceTraveled > MAX_DISTANCE) {
            die();
        }
    }

    @Override
    public void model(long gameTime) {
        move();
    }

    // === 新增的setter方法 ===
    public void setAttackPower(int attackPower) {
        this.attackPower = attackPower;
    }
    // =======================

    public int getAttackPower() {
        return attackPower;
    }
    
    @Override
    public void keyClick(boolean pressed, int keyCode) {
        // 不需要实现
    }
    
}