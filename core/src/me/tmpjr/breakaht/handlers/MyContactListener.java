package me.tmpjr.breakaht.handlers;

import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;

public class MyContactListener implements ContactListener
{
    private Array<Body> bricksToRemove = new Array<Body>();

    private boolean leftPaddleHit = false;
    private boolean rightPaddleHit = false;
    private boolean paddleHit = false;
    private boolean brickHit = false;
    private int bricksRemoved = 0;

    @Override
    public void beginContact(Contact contact)
    {
        Fixture fa = contact.getFixtureA();
        Fixture fb = contact.getFixtureB();

        if (fa.getUserData() != null && fa.getUserData().toString().contains("brick")) {
            removeBrick(fa.getBody());
            brickHit = true;
        }

        if (fb.getUserData() != null && fb.getUserData().toString().contains("brick")) {
            removeBrick(fb.getBody());
            brickHit = true;
        }

        if (fa.getUserData() != null && fa.getUserData().equals("paddle")) {
            paddleHit = true;
        }
    }

    @Override
    public void endContact(Contact contact){}
    @Override
    public void preSolve(Contact contact, Manifold oldManifold){}
    @Override
    public void postSolve(Contact contact, ContactImpulse impulse){}

    public void removeBrick(Body brick)
    {
        bricksRemoved++;
        bricksToRemove.add(brick);
    }

    public int getBricksRemoved()
    {
        return bricksRemoved;
    }

    public Array<Body> getBricksToRemove()
    {
        return bricksToRemove;
    }

    public void clearBricksToRemove()
    {
        bricksToRemove.clear();
    }

    public boolean isLeftPaddleHit()
    {
        return leftPaddleHit;
    }

    public boolean isRightPaddleHit()
    {
        return rightPaddleHit;
    }

    public boolean isPaddleHit()
    {
        boolean myPaddleHit = paddleHit;
        paddleHit = false;
        return myPaddleHit;
    }

    public boolean isBrickHit()
    {
        boolean myBrickHit = brickHit;
        brickHit = false;
        return myBrickHit;
    }
}
