package me.tmpjr.breakaht.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import me.tmpjr.breakaht.MainGame;
import me.tmpjr.breakaht.handlers.GameStateManager;

public abstract class GameState extends InputAdapter
{
    protected GameStateManager gsm;
    protected MainGame game;

    protected SpriteBatch spriteBatch;
    protected OrthographicCamera camera;
    protected OrthographicCamera hudCamera;

    protected InputMultiplexer inputMultiplexer;

    protected GameState(GameStateManager gsm)
    {
        this.gsm = gsm;
        game = gsm.game();
        spriteBatch = game.getSpriteBatch();
        camera = game.getCamera();
        hudCamera = game.getHudCamera();
        inputMultiplexer = new InputMultiplexer();
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    public abstract void handleInput();
    public abstract void update(float dt);
    public abstract void render();
    public abstract void dispose();
    public abstract void resize(int width, int height);
}
