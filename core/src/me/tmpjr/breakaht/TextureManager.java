package me.tmpjr.breakaht;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public class TextureManager
{
    public static Texture PADDLE = new Texture(Gdx.files.internal("paddleRed.png"));
    public static Texture BALL = new Texture(Gdx.files.internal("ballGrey.png"));
    public static Texture BRICK_RED_GLOSSY = new Texture(Gdx.files.internal("redGlossyBrick.png"));
    public static Texture MENU_BTN_PLAY = new Texture(Gdx.files.internal("yellow_button00.png"));
}
