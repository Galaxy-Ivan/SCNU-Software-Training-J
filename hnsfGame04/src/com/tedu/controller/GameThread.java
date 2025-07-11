package com.tedu.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

import com.tedu.element.Background;
import com.tedu.element.Boss;
import com.tedu.element.DeathAnimation;
import com.tedu.element.ElementObj;
import com.tedu.element.Enemy;
import com.tedu.element.EnemyBullet;
import com.tedu.element.Play;
import com.tedu.element.PlayFile;
import com.tedu.element.Prop;
import com.tedu.manager.ElementManager;
import com.tedu.manager.GameElement;
import com.tedu.manager.GameLoad;
import com.tedu.show.GameJFrame;
import com.tedu.show.GameMainJPanel;

public class GameThread extends Thread {
    private ElementManager em;
    private int currentLevel = 1;
    private long gameStartTime;
    private boolean isPlaying = false;
    private long nextWaveTime = 0;
    private long bossSpawnTime = 0;
    private int waveCount = 0;
    private boolean playersEntered = false;
    private Boss currentBoss = null; // 当前关卡BOSS对象
    private long levelEndTime = 0; // 关卡结束时间
    private boolean levelCompleted = false; // 关卡是否完成
    private boolean bossDefeated = false; // BOSS是否被击败

    public GameThread() {
        em = ElementManager.getManager();
    }

    @Override
    public void run() {
        gameStartTime = System.currentTimeMillis();
        isPlaying = true;
        gameLoad(currentLevel);
        
        while (isPlaying) {
            long currentTime = System.currentTimeMillis();
            
            // 玩家入场动画
            if (!playersEntered) {
                playersEntered = movePlayersIn();
                if (playersEntered) {
                    // 玩家入场完成后设置可控制
                    List<ElementObj> plays = em.getElementsByKey(GameElement.PLAY);
                    for (ElementObj play : plays) {
                        if (play instanceof Play) {
                            ((Play) play).setCanControl(true);
                        }
                    }
                    // 设置第一波敌人生成时间
                    nextWaveTime = currentTime + 5000;
                }
            }
            
            // 生成敌人波次
            if (playersEntered && nextWaveTime > 0 && currentTime > nextWaveTime && waveCount < 3) {
                spawnEnemyWave();
                waveCount++;
                nextWaveTime = (waveCount < 3) ? currentTime + 10000 : 0;
            }
            
            // 生成BOSS条件
            if (playersEntered && waveCount == 3 && 
                em.getElementsByKey(GameElement.ENEMY).isEmpty() && 
                em.getElementsByKey(GameElement.BOSS).isEmpty() &&
                bossSpawnTime == 0 && !bossDefeated) {
                bossSpawnTime = currentTime + 7000;
            }
            
            // 生成BOSS
            if (bossSpawnTime > 0 && currentTime > bossSpawnTime) {
                spawnBoss();
                bossSpawnTime = 0;
            }
            
            // 检查BOSS是否死亡
            if (currentBoss != null && !currentBoss.isLive() && !bossDefeated) {
                bossDefeated = true;
                levelCompleted = true;
                levelEndTime = currentTime + 7000; // 15秒后结束关卡（给玩家捡道具时间）
                System.out.println("BOSS defeated! Collecting items...");
            }
            
            // 关卡完成处理
            if (levelCompleted && currentTime > levelEndTime) {
                // 玩家飞出动画
                boolean allPlayersExited = movePlayersOut();
                
             // 在GameThread类的run方法中修改关卡完成后的处理逻辑
             // 替换原有的TODO部分：
             if (allPlayersExited) {
                 if (currentLevel < 3) {
                     currentLevel++;
                     resetForNextLevel();
                 } else {
                     // 游戏胜利，切换到胜利界面
                     SwingUtilities.invokeLater(() -> {
                    	 GameJFrame frame = (GameJFrame) SwingUtilities.getWindowAncestor(
                    			    GameMainJPanel.getInstance()
                    			);
                         if (frame != null) {
                             frame.switchToWinPanel();
                         }
                     });
                     isPlaying = false;
                 }
             }
            }
            
            // 更新游戏状态
            updateGame(currentTime);
            
            // 检查游戏结束
            if (checkGameOver()) {
                isPlaying = false;
                // TODO: 显示游戏结束
            }
            
            try {
                Thread.sleep(16);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean movePlayersIn() {
        List<ElementObj> plays = em.getElementsByKey(GameElement.PLAY);
        boolean allArrived = true;
        for (ElementObj play : plays) {
            if (play instanceof Play) {
                int targetY = GameJFrame.GameY - 150;
                int currentY = play.getY();
                if (currentY > targetY) {
                    play.setY(Math.max(targetY, currentY - 5));
                    allArrived = false;
                }
            }
        }
        return allArrived;
    }
    
    private boolean movePlayersOut() {
        List<ElementObj> plays = em.getElementsByKey(GameElement.PLAY);
        boolean allExited = true;
        
        for (ElementObj play : plays) {
            if (play instanceof Play) {
                ((Play) play).setCanControl(false); // 在这里禁用控制（飞出前才禁用）
                play.setY(play.getY() + 5); // 向下飞出屏幕
                
                if (play.getY() < GameJFrame.GameY) {
                    allExited = false;
                }
            }
        }
        return allExited;
    }
    
    private void spawnEnemyWave() {
        Random rand = new Random();
        for (int i = 0; i < 3; i++) {
            int x = rand.nextInt(500) + 50;
            ImageIcon enemyIcon = GameLoad.getImg("fly/enemy/" + currentLevel + ".png");
            if (enemyIcon == null) {
                System.err.println("无法加载敌人图片: fly/enemy/" + currentLevel + ".png");
                continue;
            }
            Enemy enemy = new Enemy(x, -50, enemyIcon.getIconWidth(), enemyIcon.getIconHeight(), 
                                   enemyIcon, false, currentLevel);
            enemy.setHp(calculateEnemyHP(false));
            em.addElement(enemy, GameElement.ENEMY);
        }
    }
    
    private void spawnBoss() {
        ImageIcon bossIcon = GameLoad.getImg("fly/boss/" + currentLevel + ".png");
        if (bossIcon == null) {
            System.err.println("无法加载BOSS图片: fly/boss/" + currentLevel + ".png");
            return;
        }
        
        Boss boss = new Boss(GameJFrame.GameX / 2 - 50, -100, bossIcon.getIconWidth(), 
                           bossIcon.getIconHeight(), bossIcon, true, currentLevel);
        boss.setHp(calculateEnemyHP(true));
        em.addElement(boss, GameElement.BOSS);
        currentBoss = boss; // 记录当前BOSS
        bossDefeated = false; // 重置BOSS击败状态
        System.out.println("BOSS spawned at level " + currentLevel);
    }
    
    private int calculateEnemyHP(boolean isBoss) {
        int baseHP = isBoss ? 100 : 2;
        return baseHP * (int) Math.pow(2, currentLevel - 1);
    }
    
    private void updateGame(long currentTime) {
        Map<GameElement, List<ElementObj>> all = em.getGameElements();
        
        for (GameElement ge : GameElement.values()) {
            List<ElementObj> list = all.get(ge);
            if (list != null) {
                for (int i = list.size() - 1; i >= 0; i--) {
                    ElementObj obj = list.get(i);
                    
                    if (!obj.isLive()) {
                        // 只对非动画元素创建新动画
                        if (!(obj instanceof DeathAnimation || obj instanceof Prop)) {
                            createDeathAnimation(obj);
                        }
                        list.remove(i);
                    } else {
                        obj.model(currentTime);
                    }
                }
            }
        }
        
        // 碰撞检测
        checkCollisions();
    }
    
    private void checkCollisions() {
        ElementPK(em.getElementsByKey(GameElement.PLAY), em.getElementsByKey(GameElement.ENEMY));
        ElementPK(em.getElementsByKey(GameElement.PLAY), em.getElementsByKey(GameElement.ENEMYBULLET));
        ElementPK(em.getElementsByKey(GameElement.PLAYFILE), em.getElementsByKey(GameElement.ENEMY));
        ElementPK(em.getElementsByKey(GameElement.PLAYFILE), em.getElementsByKey(GameElement.BOSS));
        ElementPK(em.getElementsByKey(GameElement.PLAY), em.getElementsByKey(GameElement.PROP));
    }
    
    private void createDeathAnimation(ElementObj obj) {
        if (obj.isLive()) return;
        DeathAnimation anim = new DeathAnimation(obj);
        em.addElement(anim, GameElement.DIE);
    }
    
    private void resetForNextLevel() {
        // 保留玩家状态
        List<ElementObj> plays = em.getElementsByKey(GameElement.PLAY);
        List<Play> players = new ArrayList<>();
        for (ElementObj obj : plays) {
            if (obj instanceof Play) {
                players.add((Play) obj);
            }
        }
        
        // 重置状态
        playersEntered = false;
        waveCount = 0;
        bossSpawnTime = 0;
        levelEndTime = 0;
        levelCompleted = false;
        bossDefeated = false;
        currentBoss = null;
        
        // 清除敌人和子弹
        em.getElementsByKey(GameElement.ENEMY).clear();
        em.getElementsByKey(GameElement.ENEMYBULLET).clear();
        em.getElementsByKey(GameElement.PLAYFILE).clear();
        em.getElementsByKey(GameElement.PROP).clear();
        em.getElementsByKey(GameElement.DIE).clear();
        em.getElementsByKey(GameElement.BOSS).clear();
        
        // 重新加载关卡
        gameLoad(currentLevel);
        
        // 恢复玩家状态
        List<ElementObj> newPlays = em.getElementsByKey(GameElement.PLAY);
        for (int i = 0; i < newPlays.size() && i < players.size(); i++) {
            Play oldPlayer = players.get(i);
            Play newPlayer = (Play) newPlays.get(i);
            newPlayer.setHp(oldPlayer.getHp());
            newPlayer.setAttackLevel(oldPlayer.getAttackLevel());
            newPlayer.setFireRateLevel(oldPlayer.getFireRateLevel());
        }
        
        gameStartTime = System.currentTimeMillis();
        System.out.println("Starting level " + currentLevel);
    }
    
    private boolean checkGameOver() {
        List<ElementObj> plays = em.getElementsByKey(GameElement.PLAY);
        for (ElementObj play : plays) {
            if (play.isLive()) {
                return false;
            }
        }
        return true;
    }
    
    private void gameLoad(int level) {
        // 清除所有元素
        em.clearAllElements();

        // 加载背景
        ImageIcon backgroundIcon = GameLoad.getImg("fly/background/" + level + ".png");
        if (backgroundIcon == null) {
            System.err.println("无法加载背景图片: fly/background/" + level + ".png");
        } else {
            Background background = new Background(0, 0, GameJFrame.GameX, GameJFrame.GameY, backgroundIcon);
            em.addElement(background, GameElement.MAPS);
        }

        // 加载玩家飞机（初始位置在屏幕下方之外）
        ImageIcon player1Icon = GameLoad.getImg("fly/play/11.png");
        ImageIcon player2Icon = GameLoad.getImg("fly/play/12.png");
        
        int player1X = GameJFrame.GameX / 2 - 60;
        int player2X = GameJFrame.GameX / 2 + 20;
        int startY = GameJFrame.GameY + 100;
        int width = 30, height = 30;
        
        if (player1Icon != null) {
            width = player1Icon.getIconWidth()-20;
            height = player1Icon.getIconHeight()-20;
        }
        
        Play player1 = new Play(player1X, startY, width, height, player1Icon);
        Play player2 = new Play(player2X, startY, width, height, player2Icon);
        
        player1.setPlayerType(1);
        player2.setPlayerType(2);
        player1.setHp(10);
        player2.setHp(10);
        player1.setCanControl(false);
        player2.setCanControl(false);
        
        em.addElement(player1, GameElement.PLAY);
        em.addElement(player2, GameElement.PLAY);
    }

    public void ElementPK(List<ElementObj> listA, List<ElementObj> listB) {
        if (listA == null || listB == null) return;
        
        for (int i = 0; i < listA.size(); i++) {
            ElementObj objA = listA.get(i);
            if (objA == null || !objA.isLive()) continue;
            
            for (int j = 0; j < listB.size(); j++) {
                ElementObj objB = listB.get(j);
                if (objB == null || !objB.isLive()) continue;
                
                if (objA.pk(objB)) {
                    handleCollision(objA, objB);
                }
            }
        }
    }
    
    private void handleCollision(ElementObj objA, ElementObj objB) {
        // 玩家与敌人碰撞
        if (objA instanceof Play && objB instanceof Enemy) {
            ((Play) objA).takeDamage(1);
            objB.die();
        } 
        // 玩家与敌人子弹碰撞
        else if (objA instanceof Play && objB instanceof EnemyBullet) {
            ((Play) objA).takeDamage(1);
            objB.die();
        } 
        // 玩家子弹与敌人碰撞
        else if (objA instanceof PlayFile && objB instanceof Enemy) {
            Enemy enemy = (Enemy) objB;
            enemy.takeDamage(((PlayFile) objA).getAttackPower());
            objA.die();
        } 
        // 玩家子弹与BOSS碰撞
        else if (objA instanceof PlayFile && objB instanceof Boss) {
            Boss boss = (Boss) objB;
            boss.takeDamage(((PlayFile) objA).getAttackPower());
            objA.die();
        } 
        // 玩家与道具碰撞
        else if (objA instanceof Play && objB instanceof Prop) {
            Prop prop = (Prop) objB;
            Play player = (Play) objA;
            applyPropEffect(player, prop.getType());
            objB.die();
        }
    }
    
    private void applyPropEffect(Play player, int propType) {
        switch (propType) {
            case 0: // 攻击道具
                player.increaseAttackLevel();
                break;
            case 1: // 攻速道具
                player.increaseFireRateLevel();
                break;
            case 2: // 回复道具
                player.setHp(Math.min(10, player.getHp() + 5));
                break;
        }
    }
}