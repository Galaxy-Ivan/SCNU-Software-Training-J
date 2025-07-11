package com.tedu.show;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.List;
import java.util.Map;
import javax.swing.JPanel;
import javax.swing.Timer;
import com.tedu.element.ElementObj;
import com.tedu.element.Play;
import com.tedu.manager.ElementManager;
import com.tedu.manager.GameElement;

public class GameMainJPanel extends JPanel {
	 private static GameMainJPanel instance;
	        
	 public static GameMainJPanel getInstance() {
	        return instance;
	 }
    private ElementManager em;
    

    public GameMainJPanel() {
        init();
        em = ElementManager.getManager();
        // 添加定时重绘
        new Timer(16, e -> repaint()).start();
    }

    public void init() {
        this.setSize(GameJFrame.GameX, GameJFrame.GameY);
    }
    
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Map<GameElement, List<ElementObj>> all = em.getGameElements();
        
        // 首先绘制背景
        List<ElementObj> maps = all.get(GameElement.MAPS);
        if(maps != null) {
            for(ElementObj obj : maps) {
                obj.showElement(g);
            }
        }
        
        // 然后绘制其他元素
        GameElement[] drawOrder = {
            GameElement.PROP,
            GameElement.PLAY,
            GameElement.ENEMY,
            GameElement.BOSS,
            GameElement.PLAYFILE,
            GameElement.ENEMYBULLET,
            GameElement.DIE
        };
        
        for (GameElement ge : drawOrder) {
            List<ElementObj> list = all.get(ge);
            if (list != null) {
                for (ElementObj obj : list) {
                    obj.showElement(g);
                }
            }
        }
        
        // 最后绘制玩家血量（确保在最上层）
        drawPlayerHealth(g);
    }
    
    private void drawPlayerHealth(Graphics g) {
        List<ElementObj> players = em.getElementsByKey(GameElement.PLAY);
        int playerIndex = 0;
        
        for (ElementObj obj : players) {
            if (obj instanceof Play) {
                Play player = (Play) obj;
                int x = playerIndex == 0 ? 20 : GameJFrame.GameX - 120;
                g.setColor(Color.RED);
                g.fillRect(x, 20, 100, 20);
                g.setColor(Color.GREEN);
                g.fillRect(x, 20, player.getHp() * 10, 20);
                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.BOLD, 14));
                g.drawString("P" + (playerIndex + 1) + " HP: " + player.getHp(), x + 5, 35);
                playerIndex++;
            }
        }
    }
}