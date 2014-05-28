package me.tmpjr.breakaht.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import me.tmpjr.breakaht.handlers.B2DVars;

public class B2DSprite
{
    protected Body body;
    protected float width;
    protected float height;
    protected Texture texture;

    public B2DSprite(Body body)
    {
        this.body = body;
    }

    public void render(SpriteBatch sb)
    {
        sb.begin();
        sb.draw(
            texture,
            body.getPosition().x * B2DVars.PPM - width / 2,
            body.getPosition().y * B2DVars.PPM - height / 2
        );
        sb.end();
    }

    public Body getBody()
    {
        return body;
    }

    public Vector2 getPostion()
    {
        return body.getPosition();
    }
}
