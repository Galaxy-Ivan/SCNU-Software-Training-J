package com.tedu.show;

import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class GameJFrame extends JFrame {
    public static int GameX = 600;
    public static int GameY = 800;
    private JPanel jPanel = null; // 当前显示的面板
    private KeyListener keyListener = null; // 键盘监听
    private MouseMotionListener mouseMotionListener = null; // 鼠标移动监听
    private MouseListener mouseListener = null; // 鼠标监听
    private Thread thread = null;  // 游戏主线程

    public GameJFrame() {
        init();
    }

    public void init() {
        this.setSize(GameX, GameY); // 设置窗体大小
        this.setTitle("飞机大战");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 设置关闭操作
        this.setLocationRelativeTo(null); // 窗体居中显示

        // 设置主界面
        GameStartJPanel startPanel = new GameStartJPanel();
        this.add(startPanel);
    }

    public void startGame() {
        if (thread != null) {
            thread.start(); // 启动游戏线程
        }
    }

    public void setjPanel(JPanel jPanel) {
        this.jPanel = jPanel;
    }

    public void setKeyListener(KeyListener keyListener) {
        this.keyListener = keyListener;
    }

    public void setMouseMotionListener(MouseMotionListener mouseMotionListener) {
        this.mouseMotionListener = mouseMotionListener;
    }

    public void setMouseListener(MouseListener mouseListener) {
        this.mouseListener = mouseListener;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }

    public void switchToGamePanel() {
        getContentPane().removeAll();
        GameMainJPanel gamePanel = new GameMainJPanel();
        add(gamePanel);
        revalidate();
        repaint();
    }
    public void switchToStartPanel() {
        getContentPane().removeAll();
        GameStartJPanel startPanel = new GameStartJPanel();
        add(startPanel);
        revalidate();
        repaint();
    }

    public void switchToWinPanel() {
        getContentPane().removeAll();
        GameWinJPanel winPanel = new GameWinJPanel();
        add(winPanel);
        revalidate();
        repaint();
    }
}