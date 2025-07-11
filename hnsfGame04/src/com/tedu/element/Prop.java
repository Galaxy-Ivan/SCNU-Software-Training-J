package com.tedu.element;

import java.awt.Graphics;

import javax.swing.ImageIcon;

import com.tedu.show.GameJFrame;

public class Prop extends ElementObj {
    private int type; // 0=攻击, 1=攻速, 2=回复
    
    public Prop(int x, int y, int w, int h, ImageIcon icon) {
        super(x, y, w, h, icon);
    }
    
    public void setType(int type) {
        this.type = type;
    }
    
    public int getType() {
        return type;
    }
    
    @Override
    public void model(long gameTime) {
        // 道具行为逻辑
        move();
    }
    @Override
    public void move() {
        y += 2; // 缓慢下降
        if(y > GameJFrame.GameY) {
            die();
        }
    }

	@Override
	public void showElement(Graphics g) {
		if (icon != null) {
            g.drawImage(icon.getImage(), x, y, w, h, null);
        }
	}
}