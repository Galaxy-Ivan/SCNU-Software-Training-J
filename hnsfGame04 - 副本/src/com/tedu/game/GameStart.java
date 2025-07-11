package com.tedu.game;

import com.tedu.controller.GameListener;
import com.tedu.controller.GameThread;
import com.tedu.show.GameJFrame;
import com.tedu.show.GameMainJPanel;
import com.tedu.show.GameWinJPanel;

public class GameStart {
    public static void main(String[] args) {
        // 预加载胜利界面资源
        new GameWinJPanel();
        
        GameJFrame gj = new GameJFrame();
        GameListener listener = new GameListener();
        GameThread th = new GameThread();

        gj.addKeyListener(listener);
        gj.setKeyListener(listener);
        gj.setThread(th);
        gj.setVisible(true);
    }
}