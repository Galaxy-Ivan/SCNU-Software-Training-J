package com.tedu.element;

import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.ImageIcon;

public abstract class ElementObj {
    protected int x;
	protected int y;
	protected int w;
	protected int h;
    protected ImageIcon icon;
    private boolean live = true;

    public ElementObj(int x, int y, int w, int h, ImageIcon icon) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.icon = icon;
    }

    public abstract void showElement(Graphics g);

    public void move() {}

    public void die() {
        live = false;
    }

    public Rectangle getRectangle() {
        return new Rectangle(x, y, w, h);
    }

    public boolean isLive() {
        return live;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getW() {
        return w;
    }

    public void setW(int w) {
        this.w = w;
    }

    public int getH() {
        return h;
    }

    public void setH(int h) {
        this.h = h;
    }

    public ImageIcon getIcon() {
        return icon;
    }

    public void setIcon(ImageIcon icon) {
        this.icon = icon;
    }
    public boolean pk(ElementObj obj) {
        return this.getRectangle().intersects(obj.getRectangle());
    }
    public void createDeathAnimation() {
        // 由子类实现
    }
    public void takeDamage(int damage) {
        // 默认空实现，具体子类覆盖
    }

	public void keyClick(boolean pressed, int keyCode) {
		// TODO Auto-generated method stub
		
	}

	public abstract void model(long gameTime);
}