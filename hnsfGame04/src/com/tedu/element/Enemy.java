package com.tedu.element;

import java.awt.Graphics;
import java.util.Random;

import javax.swing.ImageIcon;

import com.tedu.manager.ElementManager;
import com.tedu.manager.GameElement;
import com.tedu.manager.GameLoad;
import com.tedu.show.GameJFrame;

public class Enemy extends ElementObj {
    protected int hp;
    protected int level; // 关卡等级
    protected long lastShootTime = 0;
    protected int shootInterval = 3000; // milliseconds
    protected boolean isBoss;
    protected boolean inBattlefield = false; // 是否进入战场区域

    public Enemy(int x, int y, int w, int h, ImageIcon icon, boolean isBoss, int level) {
        super(x, y, w, h, icon);
        this.isBoss = isBoss;
        this.level = level;
        this.hp = calculateHP(level);
    }

    private int calculateHP(int level) {
        // 基础生命值
        int baseHP = isBoss ? 100 : 2;
        // 根据关卡等级计算生命值
        return baseHP * (int) Math.pow(2, level - 1);
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    @Override
    public void showElement(Graphics g) {
        g.drawImage(icon.getImage(), x, y, w, h, null);
    }

    @Override
    public void model(long gameTime) {
        // 敌人行为逻辑
        move();
        shoot(gameTime);
    }
    
    public void move() {
        if (isBoss) {
            // Boss移动逻辑由Boss类实现
        } else {
            // 普通敌人移动逻辑
            if (!inBattlefield) {
                y += 5; // 快速进入战场
                if (y > 50) {
                    inBattlefield = true;
                }
            } else {
                y += 1; // 缓慢向下移动
            }
            
            if (y > GameJFrame.GameY) {
                die();
            }
        }
    }

    public void shoot(long currentTime) {
        // 只有进入战场后才开始射击
        if (!inBattlefield) return;
        
        if (currentTime - lastShootTime > shootInterval) {
            lastShootTime = currentTime;
            ImageIcon bulletIcon = isBoss ? 
                GameLoad.getImg("fly/fire/3.png") : 
                GameLoad.getImg("fly/fire/2.png");
                
            // 子弹从敌人中心发射
            int bulletX = x + w/2 - 5;
            int bulletY = y + (isBoss ? 0 : h); // BOSS向上射击，普通敌人向下射击
            
            if (bulletIcon != null) {
                ElementObj bullet = new EnemyBullet(bulletX, bulletY, 10, 20, bulletIcon);
                ElementManager.getManager().addElement(bullet, GameElement.ENEMYBULLET);
            }
        }
    }

    public void takeDamage(int damage) {
        hp -= damage;
        if (hp <= 0) {
            die();
        }
    }
    
    @Override
    public void die() {
        super.die();
        // 道具掉落逻辑
        dropItem();
    }
    
    private void dropItem() {
        if (isBoss) {
            // BOSS掉落3个道具
            for (int i = 0; i < 3; i++) {
                dropSpecificItem(i % 3); // 循环掉落三种道具
            }
        } else {
            // 小兵掉落
            Random rand = new Random(); 
            int chance1 = rand.nextInt(100); 
            int chance2 = rand.nextInt(100);
            int chance3 = rand.nextInt(100);
            if (chance1 < 45) {
                dropSpecificItem(0); // 攻击道具
            } 
            if (chance2 < 45) {
                dropSpecificItem(1); // 攻速道具
            }  
            if (chance3 < 25) {
                dropSpecificItem(0); // 回复道具
            } 
        }
    }
    
    private void dropSpecificItem(int type) {
        String imagePath = "";
        switch (type) {
            case 0: // 攻击道具
                imagePath = "fly/prop/7.png";
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
            
            Prop prop = new Prop(x + offsetX, y + offsetY, 30, 30, propIcon);
            prop.setType(type);
            ElementManager.getManager().addElement(prop, GameElement.PROP);
        }
    }
    
    @Override
    public void createDeathAnimation() {
        DeathAnimation anim = new DeathAnimation(this);
        ElementManager.getManager().addElement(anim, GameElement.DIE);
    }
}