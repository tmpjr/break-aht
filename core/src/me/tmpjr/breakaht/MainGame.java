package me.tmpjr.breakaht;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import me.tmpjr.breakaht.handlers.GameStateManager;
import me.tmpjr.breakaht.handlers.MyInput;
import me.tmpjr.breakaht.handlers.MyInputProcessor;

public class MainGame extends ApplicationAdapter
{
    public static final String TITLE = "BreakAht!";
    //public static final int V_WIDTH = 320;
    public static final int V_WIDTH = 240;
    //public static final int V_HEIGHT = 240;
    public static final int V_HEIGHT = 400;
    public static final int SCALE = 2;

    public static final float STEP = 1 / 60f;
    public float accum;

    private SpriteBatch spriteBatch;
    private OrthographicCamera camera;
    private OrthographicCamera hudCamera;

    private GameStateManager gsm;

    @Override
	public void create ()
    {

        spriteBatch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, V_WIDTH, V_HEIGHT);
        hudCamera = new OrthographicCamera();
        hudCamera.setToOrtho(false, V_WIDTH, V_HEIGHT);

        gsm = new GameStateManager(this);
	}

	@Override
	public void render ()
    {
        gsm.update(Gdx.graphics.getDeltaTime());
        gsm.render();
        MyInput.update();
	}

    @Override
    public void resize(int width, int height)
    {

    }

    public SpriteBatch getSpriteBatch()
    {
        return spriteBatch;
    }

    public void setSpriteBatch(SpriteBatch spriteBatch)
    {
        this.spriteBatch = spriteBatch;
    }

    public OrthographicCamera getCamera()
    {
        return camera;
    }

    public void setCamera(OrthographicCamera camera)
    {
        this.camera = camera;
    }

    public OrthographicCamera getHudCamera()
    {
        return hudCamera;
    }

    public void setHudCamera(OrthographicCamera hudCamera)
    {
        this.hudCamera = hudCamera;
    }




}
