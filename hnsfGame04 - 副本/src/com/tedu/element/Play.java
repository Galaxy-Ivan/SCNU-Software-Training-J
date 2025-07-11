package com.tedu.element;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import com.tedu.manager.ElementManager;
import com.tedu.manager.GameElement;
import com.tedu.manager.GameLoad;
import com.tedu.show.GameJFrame;

public class Play extends ElementObj {
    // 玩家状态属性
    private int hp = 10;
    private int attackLevel = 1;    // 攻击力等级 (0-2)
    private int fireRateLevel = 1;  // 射速等级 (0-2)
    private boolean canControl = false; // 玩家是否可以控制
    private long lastShootTime = 0;  // 上次射击时间
    
    // 控制键映射（针对不同玩家）
    private int upKey;
    private int downKey;
    private int leftKey;
    private int rightKey;
    private int shootKey;
    
    // 移动方向状态
    private boolean movingUp = false;
    private boolean movingDown = false;
    private boolean movingLeft = false;
    private boolean movingRight = false;
    
    // 玩家类型 (1 或 2)
    private int playerType;
    
    public Play(int x, int y, int w, int h, ImageIcon icon) {
        super(x, y, w, h, icon);
        // 默认设置玩家1，但通常在游戏加载时会调用setPlayerType
        setPlayerType(1);
    }

    /**
     * 设置玩家控制按键
     * 
     * @param type 玩家类型 (1 或 2)
     */
    public void setPlayerType(int type) {
        this.playerType = type;
        
        // 设置不同玩家的控制按键
        if (type == 1) { // 玩家1（上下左右）
            upKey = KeyEvent.VK_UP;
            downKey = KeyEvent.VK_DOWN;
            leftKey = KeyEvent.VK_LEFT;
            rightKey = KeyEvent.VK_RIGHT;
            shootKey = KeyEvent.VK_SPACE;
        } else { // 玩家2（WASD）
            upKey = KeyEvent.VK_W;
            downKey = KeyEvent.VK_S;
            leftKey = KeyEvent.VK_A;
            rightKey = KeyEvent.VK_D;
            shootKey = KeyEvent.VK_CONTROL;
        }
    }
    
    /**
     * 设置玩家是否可以控制
     */
    public void setCanControl(boolean canControl) {
        this.canControl = canControl;
    }
    
    /**
     * 获取当前子弹攻击力
     */
    public int getAttackPower() {
        return 1 + attackLevel; // 初始1点，每级+1
    }
    
    /**
     * 获取子弹发射间隔（毫秒）
     */
    public int getBulletInterval() {
        switch (fireRateLevel) {
            case 0: return 1000; // 1秒
            case 1: return 500;   // 0.5秒
            case 2: return 250;   // 0.25秒
            default: return 250;
        }
    }
    
    /**
     * 玩家受到伤害
     * 
     * @param damage 伤害值
     */
    public void takeDamage(int damage) {
        hp -= damage;
        
        // 受伤后攻击和射速等级下降
        if (attackLevel > 0) attackLevel--;
        if (fireRateLevel > 0) fireRateLevel--;
        
        if (hp <= 0) {
            die(); // 生命值为0时死亡
        }
    }
    
    /**
     * 处理道具拾取
     * 
     * @param type 道具类型 (0=攻击道具, 1=射速道具, 2=回复道具)
     */
    public void pickupProp(int type) {
        switch (type) {
            case 0: // 攻击道具
                if (attackLevel < 2) attackLevel++;
                break;
                
            case 1: // 射速道具
                if (fireRateLevel < 2) fireRateLevel++;
                break;
                
            case 2: // 回复道具
                hp = 10; // 回满血
                break;
        }
    }
    
    /**
     * 玩家射击
     */
    private void shoot(long currentTime) {
        if (currentTime - lastShootTime < getBulletInterval()) {
            return;
        }
        
        // 创建子弹对象
        int bulletX = x + w / 2 - 5; // 居中发射
        int bulletY = y - 20; // 从飞机顶部发射
        
        ImageIcon bulletIcon = GameLoad.getImg("fly/fire/1.png");
        if (bulletIcon != null) {
            PlayFile bullet = new PlayFile(bulletX, bulletY, 
                                          bulletIcon.getIconWidth(), 
                                          bulletIcon.getIconHeight(), 
                                          bulletIcon);
            bullet.setAttackPower(getAttackPower());
            ElementManager.getManager().addElement(bullet, GameElement.PLAYFILE);
        }
        
        lastShootTime = currentTime;
    }
    
    @Override
    public void keyClick(boolean pressed, int keyCode) {
        if (!canControl) return;
        System.out.println("玩家" + playerType + " 按键: " + keyCode + " 状态: " + pressed);
        
        if (keyCode == upKey) {
            movingUp = pressed;
        } else if (keyCode == downKey) {
            movingDown = pressed;
        } else if (keyCode == leftKey) {
            movingLeft = pressed;
        } else if (keyCode == rightKey) {
            movingRight = pressed;
        } else if (keyCode == shootKey && pressed) {
            // 持续射击在model方法中处理，这里不需要额外处理
        }
    }
    
    @Override
    public void model(long gameTime) {
        if (canControl) {
            // 处理射击
            shoot(gameTime);
            
            // 处理移动
            int moveSpeed = 5;
            int newX = x;
            int newY = y;
            
            if (movingUp) newY -= moveSpeed;
            if (movingDown) newY += moveSpeed;
            if (movingLeft) newX -= moveSpeed;
            if (movingRight) newX += moveSpeed;
            
            // 边界检查 (窗口大小为800x600)
            if (newX < 0) newX = 0;
            if (newX > GameJFrame.GameX - w) newX = GameJFrame.GameX - w;
            if (newY < 0) newY = 0;
            if (newY > GameJFrame.GameY - h) newY = GameJFrame.GameY - h;
            
            setX(newX);
            setY(newY);
        }
    }

    @Override
    public void showElement(Graphics g) {
        if (isLive()) {
            g.drawImage(icon.getImage(), x, y, w, h, null);
        }
    }
    public void increaseAttackLevel() {
        if (attackLevel < 2) {
            attackLevel++;
        }
    }
    
    /**
     * 提升射速等级
     */
    public void increaseFireRateLevel() {
        if (fireRateLevel < 2) {
            fireRateLevel++;
        }
    }

	public void setHp(int i) {
		hp=i;
		
	}

	public int getHp() {
		return hp;
	}
	public int getAttackLevel() {
        return attackLevel;
    }
    
    public void setAttackLevel(int attackLevel) {
        this.attackLevel = Math.min(2, Math.max(0, attackLevel));
    }
    
    public int getFireRateLevel() {
        return fireRateLevel;
    }
    
    public void setFireRateLevel(int fireRateLevel) {
        this.fireRateLevel = Math.min(2, Math.max(0, fireRateLevel));
    }
    public void setY(int y) {
        this.y = y;
    }

    
}