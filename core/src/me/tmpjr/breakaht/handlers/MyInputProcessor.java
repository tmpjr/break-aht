package me.tmpjr.breakaht.handlers;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef;
import me.tmpjr.breakaht.MainGame;
import me.tmpjr.breakaht.states.GameState;
import me.tmpjr.breakaht.states.Play;

public class MyInputProcessor extends InputAdapter
{
    private Play playState;
    private MouseJoint mouseJoint;

    public MyInputProcessor(Play play)
    {
        playState = play;
    }

    @Override
    public boolean keyDown(int k)
    {
        switch (k) {
            case Keys.A:
                playState.setPaddleMoveLeft(true);
                break;
            case Keys.D:
                playState.setPaddleMoveRight(true);
                break;

        }

        return true;
    }

    @Override
    public boolean keyUp(int k)
    {
        switch (k) {
            case Keys.A:
                playState.setPaddleMoveLeft(false);
                break;
            case Keys.D:
                playState.setPaddleMoveRight(false);
                break;

        }

        return true;
    }

    private Vector3 tmp = new Vector3();
    private Vector2 tmp2 = new Vector2();

    private QueryCallback queryCallback = new QueryCallback()
    {
        @Override
        public boolean reportFixture(Fixture fixture)
        {
            if (!fixture.testPoint(tmp.x, tmp.y)) {
                return true;
            }

            World world = playState.getWorld();
            MouseJointDef mouseJointDef = playState.getMouseJointDef();

            mouseJointDef.bodyB = fixture.getBody();
            mouseJointDef.target.set(tmp.x, tmp.y);
            mouseJoint = (MouseJoint) world.createJoint(mouseJointDef);

            return false;
        }
    };

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button)
    {
        playState.getBox2DCamera().unproject(tmp.set(screenX, screenY, 0));
        World world = playState.getWorld();
        world.QueryAABB(queryCallback, tmp.x, tmp.y, tmp.x, tmp.y);

        return true;
    }

    @Override
    public boolean touchDragged(int x, int y, int pointer)
    {
        if (mouseJoint == null) {
            return false;
        }

        playState.getBox2DCamera().unproject(tmp.set(x, y, 0));
        mouseJoint.setTarget(tmp2.set(tmp.x, tmp.y));

        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button)
    {
        if (mouseJoint == null) {
            return false;
        }

        playState.getWorld().destroyJoint(mouseJoint);
        mouseJoint = null;

        return true;
    }
}
