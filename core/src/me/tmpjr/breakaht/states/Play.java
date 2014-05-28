package me.tmpjr.breakaht.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.*;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import me.tmpjr.breakaht.entities.BallSprite;
import me.tmpjr.breakaht.entities.BrickSprite;
import me.tmpjr.breakaht.entities.PaddleSprite;
import me.tmpjr.breakaht.handlers.B2DVars;
import me.tmpjr.breakaht.handlers.GameStateManager;
import me.tmpjr.breakaht.MainGame;
import me.tmpjr.breakaht.handlers.MyContactListener;
import me.tmpjr.breakaht.handlers.MyInputProcessor;

import java.util.Iterator;

public class Play extends GameState
{
    private World world;
    private Box2DDebugRenderer b2dr;

    private OrthographicCamera b2dCam;

    private Body ballBody;
    private Body paddleBody;
    private Body groundBody;
    private Body anchorBody;

    private MyContactListener myContactListener;

    private Sound effectHitPaddle;
    private Sound effectHitBrick;

    private Array<Body> worldBodies = new Array<Body>();

    private float maxBallSpeed = 4.0f;
    private float maxPaddleSpeed = 3.5f;

    private SpriteBatch sb;

    private Texture paddleTexture;
    private PaddleSprite paddleSprite;
    private Texture ballTexture;
    private BallSprite ballSprite;
    private Texture brickTexture;
    private BrickSprite brickSprite;

    private Vector2 paddleDragPosition;
    private MouseJointDef mouseJointDef;
    private MouseJoint mouseJoint;
    private WeldJointDef weldJointDef;

    private int totalBricks = 0;

    public static final float PPM = 100;

    private boolean paddleMoveLeft = false;
    private boolean paddleMoveRight = false;

    private MyInputProcessor inputProcessor;

    private Stage stage;
    private Skin skin;

    private final GameStateManager gameStateManager;

    public Play(GameStateManager gsm)
    {
        super(gsm);

        gameStateManager = gsm;

        System.out.println("Play game...");

        world = new World(new Vector2(0, -9.81f/B2DVars.PPM), true);
        myContactListener = new MyContactListener();
        world.setContactListener(myContactListener);
        b2dr = new Box2DDebugRenderer();
        world.setVelocityThreshold(0.0f);
        sb = new SpriteBatch();

        stage = new Stage();

        // create platform
        BodyDef bdef = new BodyDef();

        // create fixture
        FixtureDef fdef = new FixtureDef();

        createPaddle();
        createAnchor();

        createBall();


        // Set ground just above bottom of screen
        bdef.position.set(10/PPM, 10/PPM);
        bdef.type = BodyDef.BodyType.StaticBody;
        Body body = world.createBody(bdef);

        // Shape for borders
        EdgeShape groundShape = new EdgeShape();

        // Ground
        BodyDef groundBodyDef = new BodyDef();
        groundBodyDef.position.set(0, 0);
        groundBody = world.createBody(groundBodyDef);

        FixtureDef groundFixtureDef = new FixtureDef();
        groundFixtureDef.density = 10.0f;
        groundFixtureDef.friction = 100.0f;
        groundFixtureDef.restitution = 1.0f;

        groundShape.set(0, 0, (MainGame.V_WIDTH-20)/PPM, 0);
        groundFixtureDef.shape = groundShape;
        groundFixtureDef.filter.categoryBits = B2DVars.BIT_GROUND;
        groundFixtureDef.filter.maskBits = B2DVars.BIT_PADDLE | B2DVars.BIT_BALL;
        body.createFixture(groundFixtureDef).setUserData("ground");

        // Left wall
        groundShape.set(0, 0, 0, (MainGame.V_HEIGHT-20)/PPM);
        groundFixtureDef.shape = groundShape;
        groundFixtureDef.filter.categoryBits = B2DVars.BIT_WALL;
        groundFixtureDef.filter.maskBits = B2DVars.BIT_BALL | B2DVars.BIT_PADDLE;
        body.createFixture(groundFixtureDef).setUserData("ground");

        // Top wall
        groundShape.set(0, (MainGame.V_HEIGHT-20)/PPM, (MainGame.V_WIDTH-20)/PPM, (MainGame.V_HEIGHT-20)/PPM);
        groundFixtureDef.shape = groundShape;
        groundFixtureDef.filter.categoryBits = B2DVars.BIT_WALL;
        groundFixtureDef.filter.maskBits = B2DVars.BIT_BALL | B2DVars.BIT_PADDLE;
        body.createFixture(groundFixtureDef).setUserData("ground");

        // Right wall
        groundShape.set((MainGame.V_WIDTH-20)/PPM, 0, (MainGame.V_WIDTH-20)/PPM, (MainGame.V_HEIGHT-20)/PPM);
        groundFixtureDef.shape = groundShape;
        groundFixtureDef.filter.categoryBits = B2DVars.BIT_WALL;
        groundFixtureDef.filter.maskBits = B2DVars.BIT_BALL | B2DVars.BIT_PADDLE;
        body.createFixture(groundFixtureDef).setUserData("ground");

        groundShape.dispose();

        // Create prismatic joint to keep paddle in bounds
        PrismaticJointDef worldJointDef = new PrismaticJointDef();
        // world axis to anchor to joint
        Vector2 worldAxis = new Vector2(1.0f, 0.0f); // allow horizontal movement
        worldJointDef.initialize(paddleBody, body, paddleBody.getWorldCenter(), worldAxis);
        worldJointDef.collideConnected = true;
        world.createJoint(worldJointDef);

        // Mouse joint def
        mouseJointDef = new MouseJointDef();
        mouseJointDef.bodyA = groundBody;
        mouseJointDef.collideConnected = true;
        mouseJointDef.maxForce = 500;

        // create bricks
        createBricks();

        // Load sounds
        effectHitPaddle = Gdx.audio.newSound(Gdx.files.internal("hitPaddle.ogg"));
        effectHitBrick = Gdx.audio.newSound(Gdx.files.internal("hitBrick.ogg"));

        b2dCam = new OrthographicCamera();
        b2dCam.setToOrtho(false, MainGame.V_WIDTH/B2DVars.PPM, MainGame.V_HEIGHT/B2DVars.PPM);

        inputProcessor = new MyInputProcessor(this);
        inputMultiplexer.addProcessor(inputProcessor);

        FreeTypeFontGenerator ftgen = new FreeTypeFontGenerator(Gdx.files.internal("kenvector_future_thin.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter ftparam = new FreeTypeFontGenerator.FreeTypeFontParameter();
        ftparam.size = 24;
        BitmapFont font = ftgen.generateFont(ftparam);
        ftgen.dispose();

        final Sound effectMenuClick = Gdx.audio.newSound(Gdx.files.internal("click3.ogg"));
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        TextButton.TextButtonStyle txtBtnStyle = new TextButton.TextButtonStyle();
        txtBtnStyle.font = font;
        txtBtnStyle.up = skin.getDrawable("default-round");
        txtBtnStyle.down = skin.getDrawable("default-round-down");
        TextButton exitButton = new TextButton("Exit", skin);
        Table tblExitBtn = new Table(skin);
        tblExitBtn.setFillParent(true);
        tblExitBtn.top().right();
        tblExitBtn.add(exitButton);
        stage.addActor(tblExitBtn);

        inputMultiplexer.addProcessor(stage);

        exitButton.addListener(new ClickListener()
        {
            public void touchUp(InputEvent event, float x, float y, int pointer, int button)
            {
                Gdx.app.log("INFO", "You touched the Play button!");
                effectMenuClick.play();
                gameStateManager.setState(gameStateManager.MENU);
            }
        });
        font.dispose();
    }

    public World getWorld()
    {
        return world;
    }

    public OrthographicCamera getBox2DCamera()
    {
        return b2dCam;
    }

    public OrthographicCamera getCamera()
    {
        return camera;
    }

    public MouseJointDef getMouseJointDef()
    {
        return mouseJointDef;
    }

    public MouseJoint getMouseJoint()
    {
        return mouseJoint;
    }

    public void setPaddleMoveLeft(boolean t)
    {
        if (paddleMoveRight && t) {
            paddleMoveRight = false;
        }

        paddleMoveLeft = t;
    }

    public void setPaddleMoveRight(boolean t)
    {
        if (paddleMoveLeft && t) {
            paddleMoveLeft = false;
        }

        paddleMoveRight = t;
    }

    @Override
    public void handleInput()
    {
        Vector2 velocity = paddleBody.getLinearVelocity();

        float accelX = Gdx.input.getAccelerometerX();
        //Gdx.app.log("INFO", "accelX: " + accelX);

        if (accelX > 0) {
            setPaddleMoveLeft(true);
        }

        if (accelX  < 0) {
            setPaddleMoveRight(true);
        }

        if (paddleMoveLeft) {
            velocity.x = -1;
        } else if (paddleMoveRight) {
            velocity.x = 1;
        } else {
            // Stop paddle if key not pressed
            velocity.x = 0;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            gsm.setState(gsm.MENU);
        }

        paddleBody.setLinearVelocity(velocity);
    }

    @Override
    public void update(float dt)
    {
        // If no more bricks, game over
        if (myContactListener.getBricksRemoved() >= totalBricks) {
           gsm.setState(gsm.MENU);
        }

        // Use fixed time
        world.step(MainGame.STEP, 6, 2);

        handleInput();

        //paddleBody.setLinearVelocity(new Vector2(0, 0));

        world.getBodies(worldBodies);
        Iterator<Body> i = worldBodies.iterator();
        while (i.hasNext()) {
            Body b = i.next();
            if (b.getUserData() != null) {
                // If body is a ball, limit its velocity
                if (b.getUserData() instanceof BallSprite) {
                    Vector2 ballVelocity = b.getLinearVelocity();
                    float ballSpeed = ballVelocity.len();
                    if (ballSpeed > maxBallSpeed) {
                        b.setLinearDamping(0.5f);
                    } else {
                        b.setLinearDamping(0.0f);
                    }
                }

                if (b.getUserData() instanceof PaddleSprite) {
                    Sprite sprite = (Sprite) b.getUserData();

                    Vector2 paddleVelocity = b.getLinearVelocity();
                    float paddleSpeed = paddleVelocity.len();
                    //System.out.println(paddleSpeed);
                    if (paddleSpeed > maxPaddleSpeed) {
                        b.setLinearDamping(0.5f);
                    } else {
                        b.setLinearDamping(0.0f);
                    }
                }

            }
        }

        if (myContactListener.isPaddleHit()) {
            // play sound effect when paddle hits ball
            effectHitPaddle.play();
        }

        if (myContactListener.isBrickHit()) {
            effectHitBrick.play();
        }

        //System.out.println(ballSpeed);

        // do we have any bricks ot remove?
        Array<Body> bricksToRemove = myContactListener.getBricksToRemove();
        Iterator<Body> it = bricksToRemove.iterator();
        while (it.hasNext()) {
            Body brick = it.next();
            world.destroyBody(brick);
            it.remove();
        }
        myContactListener.clearBricksToRemove();
    }

    @Override
    public void render()
    {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        sb.setProjectionMatrix(camera.combined);
        camera.update();

        sb.begin();
        world.getBodies(worldBodies);
        for(Body body : worldBodies) {
            if (body.getUserData() != null &&  body.getUserData() instanceof Sprite) {
                Sprite sprite = (Sprite) body.getUserData();
                float ypos = (body.getPosition().y * B2DVars.PPM - sprite.getHeight() * B2DVars.PPM / 2);
                sb.draw(
                    sprite.getTexture(),
                    body.getPosition().x * B2DVars.PPM - sprite.getWidth() * B2DVars.PPM / 2,
                    ypos,
                    //body.getPosition().y * B2DVars.PPM - sprite.getHeight() * B2DVars.PPM / 2,
                    sprite.getWidth() * B2DVars.PPM,
                    sprite.getHeight() * B2DVars.PPM
                );
            }
        }
        sb.end();

        stage.act();
        stage.draw();

        b2dr.render(world, b2dCam.combined);
    }

    @Override
    public void resize(int width, int height)
    {

    }

    @Override
    public void dispose()
    {
        effectHitPaddle.dispose();
        effectHitBrick.dispose();
        sb.dispose();
        spriteBatch.dispose();
        stage.dispose();
        skin.dispose();

        world.destroyBody(ballBody);
        world.destroyBody(groundBody);
        world.destroyBody(paddleBody);
    }

    private void createAnchor()
    {
        BodyDef bdef = new BodyDef();
        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();

        //bdef.position.set(160/PPM, 100/PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;
        anchorBody = world.createBody(bdef);
        shape.setRadius(4 / B2DVars.PPM);
        fdef.shape = shape;
        fdef.restitution = 1.0f;
        fdef.density = 7.0f;
        fdef.friction = 0.f;

        Fixture fixture = anchorBody.createFixture(fdef);

        shape.dispose();
    }

    private void createBall()
    {
        BodyDef bdef = new BodyDef();
        FixtureDef fdef = new FixtureDef();
        CircleShape ballShape = new CircleShape();

        bdef.position.set(160/PPM, 100/PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;
        ballBody = world.createBody(bdef);
        ballShape.setRadius(5/PPM);
        fdef.shape = ballShape;
        fdef.restitution = 1.0f;
        fdef.density = 7.0f;
        fdef.friction = 0.f;
        fdef.filter.categoryBits = B2DVars.BIT_BALL;
        fdef.filter.maskBits = B2DVars.BIT_PADDLE | B2DVars.BIT_WALL
                | B2DVars.BIT_GROUND | B2DVars.BIT_BRICK;
        Fixture ballFixture = ballBody.createFixture(fdef);
        ballFixture.setUserData("ball");

        ballTexture = new Texture(Gdx.files.internal("ballGrey.png"));
        ballSprite = new BallSprite(ballTexture);
        ballSprite.setSize(10/B2DVars.PPM, 10/B2DVars.PPM);
        ballBody.setUserData(ballSprite);

        ballShape.dispose();

        // create initial movement for ball
        Vector2 ballForce = new Vector2(10/PPM, 10/PPM);
        ballBody.applyLinearImpulse(ballForce, bdef.position, true);
    }

    private void createPaddle()
    {
        BodyDef bdef = new BodyDef();
        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();

        // create paddle
        Vector2 paddlePosition = new Vector2(160/B2DVars.PPM, 25/B2DVars.PPM);
        bdef.position.set(paddlePosition.x, paddlePosition.y);
        bdef.type = BodyDef.BodyType.DynamicBody;
        paddleBody = world.createBody(bdef);

        shape.setAsBox(25/B2DVars.PPM, 5/B2DVars.PPM);
        fdef.shape = shape;
        fdef.density = 10.0f;
        fdef.friction = 0.4f;
        fdef.restitution = 0.5f;
        fdef.filter.categoryBits = B2DVars.BIT_PADDLE;
        fdef.filter.maskBits = B2DVars.BIT_BALL | B2DVars.BIT_WALL;
        Fixture paddleFixture =  paddleBody.createFixture(fdef);
        paddleFixture.setUserData("paddle");

        paddleTexture = new Texture(Gdx.files.internal("paddleRed.png"));
        paddleSprite = new PaddleSprite(paddleTexture);
        paddleSprite.setSize(50/B2DVars.PPM, 10/B2DVars.PPM);
        paddleBody.setUserData(paddleSprite);

        shape.dispose();
    }

    private void createBricks()
    {
        int maxBricks = 5;
        int maxRows = 3;
        float brickPadding = 1/PPM;
        BodyDef brickDef = new BodyDef();
        brickDef.type = BodyDef.BodyType.StaticBody;
        //Body brickBody = world.createBody(brickDef);
        Body brickBody;
        PolygonShape brickShape = new PolygonShape();
        brickShape.setAsBox(20/PPM, 5/PPM);
        FixtureDef brickFixtureDef = new FixtureDef();
        brickFixtureDef.shape = brickShape;
        brickFixtureDef.density = 10.0f;
        brickFixtureDef.friction = 0.4f;
        brickFixtureDef.restitution = 0.1f;
        brickFixtureDef.filter.categoryBits = B2DVars.BIT_BRICK;
        brickFixtureDef.filter.maskBits = B2DVars.BIT_BALL;

        brickTexture = new Texture(Gdx.files.internal("redGlossyBrick.png"));
        brickSprite = new BrickSprite(brickTexture);
        brickSprite.setSize(40/B2DVars.PPM, 10/B2DVars.PPM);

        Vector2 brickPos = new Vector2();
        brickPos.y = MainGame.V_HEIGHT/PPM - 30/PPM;
        float xOffset = 30.f / B2DVars.PPM;
        float startX = 5 / B2DVars.PPM;

        float brickWidth = brickSprite.getWidth();
        float brickHeight = brickSprite.getHeight();
        float startY = (MainGame.V_HEIGHT - 150) / B2DVars.PPM;
        boolean isFirstRow = true;
        float Y;

        for (int y = 0; y < maxRows; y++) {
            for (int x = 0; x < maxBricks; x++) {
                totalBricks++;
                float X = brickWidth + (brickWidth * x) % (maxBricks * brickWidth);
                Y = startY + brickHeight + y * brickHeight;

                brickDef.position.set(X, Y);
                brickBody = world.createBody(brickDef);
                Fixture brickFixture = brickBody.createFixture(brickFixtureDef);
                brickFixture.setUserData("brick" + x);
                brickBody.setUserData(brickSprite);
            }
        }


//        for (int i =0; i < maxBricks; i++) {
//            //xOffset = brickPadding + 80/PPM/2 + ((80/PPM+brickPadding)*i);
//            brickPos.x = xOffset;
//            xOffset += brickSprite.getWidth()/B2DVars.PPM + 40/B2DVars.PPM;
//
//            brickDef.position.set(brickPos.x, brickPos.y);
//            brickBody = world.createBody(brickDef);
//            Fixture brickFixture = brickBody.createFixture(brickFixtureDef);
//            brickFixture.setUserData("brick" + i);
//            brickBody.setUserData(brickSprite);
//        }

        brickShape.dispose();
    }

    public Body getPaddleBody()
    {
        return paddleBody;
    }
}
