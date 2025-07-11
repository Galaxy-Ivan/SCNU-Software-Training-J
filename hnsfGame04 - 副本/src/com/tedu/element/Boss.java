package com.tedu.element;

import java.awt.Graphics;
import java.util.Random;

import javax.swing.ImageIcon;

import com.tedu.manager.ElementManager;
import com.tedu.manager.GameElement;
import com.tedu.manager.GameLoad;
import com.tedu.show.GameJFrame;

public class Boss extends Enemy {
    private int moveDirection = 1; // 1表示向右，-1表示向左
    private int moveSpeed = 1; // 横向移动速度
    private int boundaryOffset = 50; // 边界偏移量，防止BOSS完全离开屏幕
    

    public Boss(int x, int y, int w, int h, ImageIcon icon, boolean isBoss, int level) {
        super(x, y, w, h, icon, isBoss, level);
    }

    @Override
    public void move() {
        if (!inBattlefield) {
            // 进场阶段：向下移动直到进入战场
            y += 3;
            if (y > 100) {
                inBattlefield = true;
            }
        } else {
            // 规律移动阶段：左右往返移动
            x += moveSpeed * moveDirection;
            
            // 检查边界并改变方向
            if (x <= boundaryOffset) {
                x = boundaryOffset; // 防止移出左边界
                moveDirection = 1; // 向右移动
            } else if (x >= GameJFrame.GameX - w - boundaryOffset) {
                x = GameJFrame.GameX - w - boundaryOffset; // 防止移出右边界
                moveDirection = -1; // 向左移动
            }
        }
    }
    
    @Override
    public void die() {
        super.die();
        
        // BOSS死亡后掉落多个道具
        for (int i = 0; i < 5; i++) {
            dropProp(i % 3, x + w / 2, y + h / 2);
        }
    }
    
    private void dropProp(int type, int centerX, int centerY) {
        String imagePath = "";
        switch (type) {
            case 0: // 攻击道具
                imagePath = "fly/prop/6.png";
                break;
            case 1: // 攻速道具
                imagePath = "fly/prop/6.png";
                break;
            case 2: // 回复道具
                imagePath = "fly/prop/1.png";
                break;
        }
        
        ImageIcon propIcon = GameLoad.getImg(imagePath);
        if (propIcon != null) {
            // 随机散布道具位置
            Random rand = new Random();
            int offsetX = rand.nextInt(200) - 100; // -100到100
            int offsetY = rand.nextInt(200) - 100; // -100到100
            
            Prop prop = new Prop(centerX + offsetX, centerY + offsetY, 30, 30, propIcon);
            prop.setType(type);
            ElementManager.getManager().addElement(prop, GameElement.PROP);
        }
    }
}