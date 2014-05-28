package me.tmpjr.breakaht.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import me.tmpjr.breakaht.MainGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

        config.title = MainGame.TITLE;
        config.width = MainGame.V_WIDTH * MainGame.SCALE;
        config.height = MainGame.V_HEIGHT * MainGame.SCALE;

		new LwjglApplication(new MainGame(), config);
	}
}
