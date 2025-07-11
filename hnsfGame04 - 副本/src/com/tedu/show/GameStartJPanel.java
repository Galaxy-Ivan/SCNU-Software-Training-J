package com.tedu.show;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JPanel;
import com.tedu.controller.GameThread;

public class GameStartJPanel extends JPanel implements ActionListener {
    private JButton startButton;
    private JButton exitButton;
    private BufferedImage backgroundImage; // 背景图片

    public GameStartJPanel() {
        init();
        loadBackground();
    }

    private void loadBackground() {
        try {
            // 加载背景图片
            backgroundImage = ImageIO.read(new File("image/fly/background/8.png"));
        } catch (Exception e) {
            System.err.println("无法加载开始界面背景图片: " + e.getMessage());
            // 创建纯色背景作为备用
            backgroundImage = new BufferedImage(GameJFrame.GameX, GameJFrame.GameY, BufferedImage.TYPE_INT_RGB);
            Graphics g = backgroundImage.getGraphics();
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, GameJFrame.GameX, GameJFrame.GameY);
            g.dispose();
        }
    }

    public void init() {
        this.setLayout(null);
        startButton = new JButton("开始游戏");
        exitButton = new JButton("退出游戏");

        // 设置按钮位置（居中）
        int buttonWidth = 100;
        int buttonHeight = 50;
        int centerX = (GameJFrame.GameX - buttonWidth) / 2;
        
        startButton.setBounds(centerX, 300, buttonWidth, buttonHeight);
        exitButton.setBounds(centerX, 400, buttonWidth, buttonHeight);

        // 设置按钮样式
        startButton.setBackground(new Color(70, 130, 180));
        startButton.setForeground(Color.WHITE);
        startButton.setFont(new Font("黑体", Font.BOLD, 14));
        
        exitButton.setBackground(new Color(220, 20, 60));
        exitButton.setForeground(Color.WHITE);
        exitButton.setFont(new Font("黑体", Font.BOLD, 14));

        startButton.addActionListener(this);
        exitButton.addActionListener(this);

        this.add(startButton);
        this.add(exitButton);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // 绘制背景图片
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, GameJFrame.GameX, GameJFrame.GameY, this);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == startButton) {
            GameJFrame frame = (GameJFrame) this.getTopLevelAncestor();
            frame.switchToGamePanel();
            
            GameThread gameThread = new GameThread();
            frame.setThread(gameThread);
            gameThread.start();
        } else if (e.getSource() == exitButton) {
            System.exit(0);
        }
    }
}