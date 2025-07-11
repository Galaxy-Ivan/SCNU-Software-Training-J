package com.tedu.show;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.*;

public class GameWinJPanel extends JPanel implements ActionListener {
    private JButton returnButton;
    private BufferedImage backgroundImage;

    public GameWinJPanel() {
        init();
        loadBackground();
    }

    private void loadBackground() {
        try {
            backgroundImage = ImageIO.read(new File("image/fly/background/6.jpg")); // 胜利背景图
        } catch (Exception e) {
            System.err.println("无法加载胜利背景图片: " + e.getMessage());
            backgroundImage = new BufferedImage(GameJFrame.GameX, GameJFrame.GameY, BufferedImage.TYPE_INT_RGB);
            Graphics g = backgroundImage.getGraphics();
            g.setColor(new Color(0, 100, 0)); // 深绿色背景
            g.fillRect(0, 0, GameJFrame.GameX, GameJFrame.GameY);
            g.dispose();
        }
    }

    private void init() {
        this.setLayout(null);
        returnButton = new JButton("胜利返回");
        
        int buttonWidth = 150;
        int buttonHeight = 50;
        int centerX = (GameJFrame.GameX - buttonWidth) / 2;
        
        returnButton.setBounds(centerX, 500, buttonWidth, buttonHeight);
        returnButton.setBackground(new Color(30, 144, 255));
        returnButton.setForeground(Color.WHITE);
        returnButton.setFont(new Font("黑体", Font.BOLD, 18));
        returnButton.addActionListener(this);
        
        this.add(returnButton);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, GameJFrame.GameX, GameJFrame.GameY, this);
        }
        
        // 绘制胜利文本
        g.setFont(new Font("微软雅黑", Font.BOLD, 48));
        g.setColor(Color.YELLOW);
        String winText = "游戏胜利!";
        int textWidth = g.getFontMetrics().stringWidth(winText);
        g.drawString(winText, (GameJFrame.GameX - textWidth) / 2, 300);
        
        g.setFont(new Font("宋体", Font.PLAIN, 24));
        g.setColor(Color.WHITE);
        g.drawString("恭喜你击败了最终BOSS!", (GameJFrame.GameX - 200) / 2, 380);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == returnButton) {
            GameJFrame frame = (GameJFrame) this.getTopLevelAncestor();
            frame.switchToStartPanel();
        }
    }
}