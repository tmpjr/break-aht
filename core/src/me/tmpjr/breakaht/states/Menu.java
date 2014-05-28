package me.tmpjr.breakaht.states;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import me.tmpjr.breakaht.MainGame;
import me.tmpjr.breakaht.handlers.B2DVars;
import me.tmpjr.breakaht.handlers.GameStateManager;

public class Menu extends GameState
{
    private Skin skin;
    private Stage stage;
    private Table tblMenu;

    private final GameStateManager gameStateManager;
    private final Sound effectMenuClick;

    public Menu(GameStateManager gsm)
    {
        super(gsm);

        gameStateManager = gsm;
        stage = new Stage(new ScreenViewport());

        effectMenuClick = Gdx.audio.newSound(Gdx.files.internal("click3.ogg"));

        FreeTypeFontGenerator ftgen = new FreeTypeFontGenerator(Gdx.files.internal("kenvector_future_thin.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter ftparam = new FreeTypeFontGenerator.FreeTypeFontParameter();
        ftparam.size = 24;
        BitmapFont font = ftgen.generateFont(ftparam);
        ftgen.dispose();

        skin = new Skin(Gdx.files.internal("uiskin.json"));

        Label.LabelStyle lblStyle = new Label.LabelStyle();
        lblStyle.font = font;
        Label lblTitle = new Label("Break Aht!", lblStyle);
        float lblX = (MainGame.V_WIDTH*MainGame.SCALE - lblTitle.getWidth())/2;
        float lblY = MainGame.V_HEIGHT * MainGame.SCALE - 120;
        lblTitle.setPosition(lblX, lblY);


        tblMenu = new Table(skin);
        tblMenu.setFillParent(true);
        tblMenu.center();

        TextButton.TextButtonStyle txtBtnStyle = new TextButton.TextButtonStyle();
        txtBtnStyle.font = font;
        txtBtnStyle.up = skin.getDrawable("default-round");
        txtBtnStyle.down = skin.getDrawable("default-round-down");

        TextButton btnPlay  = new TextButton("Play", txtBtnStyle);
        TextButton btnExit = new TextButton("Exit", txtBtnStyle);
        TextButton btnSettings = new TextButton("Settings", txtBtnStyle);
        tblMenu.add(btnPlay).width(240).height(60).padBottom(5);
        tblMenu.row();
        tblMenu.add(btnSettings).width(240).height(60).padBottom(5);
        tblMenu.row();
        tblMenu.add(btnExit).width(240).height(60);

        stage.addActor(lblTitle);
        stage.addActor(tblMenu);

        inputMultiplexer.addProcessor(stage);

        btnPlay.addListener(new ClickListener() {
            public void touchUp(InputEvent event, float x, float y, int pointer, int button)
            {
                Gdx.app.log("INFO", "You touched the Play button!");
                effectMenuClick.play();
                gameStateManager.setState(gameStateManager.PLAY);
            }
        });

        btnExit.addListener(new ClickListener() {
            public void touchUp(InputEvent event, float x, float y, int pointer, int button)
            {
                Gdx.app.log("INFO", "You touched the Exit button!");
                effectMenuClick.play();
                Gdx.app.exit();
            }
        });

        camera.setToOrtho(false, MainGame.V_WIDTH, MainGame.V_HEIGHT);
    }

    @Override
    public void resize(int width, int height)
    {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void handleInput()
    {

    }

    @Override
    public void update(float dt)
    {
        handleInput();
    }

    @Override
    public void render()
    {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act();
        stage.draw();
    }

    @Override
    public void dispose()
    {
        stage.dispose();
        skin.dispose();
    }
}
