package me.tmpjr.breakaht.handlers;

import me.tmpjr.breakaht.MainGame;
import me.tmpjr.breakaht.states.GameState;
import me.tmpjr.breakaht.states.Menu;
import me.tmpjr.breakaht.states.Play;

import java.util.Stack;

public class GameStateManager
{
    private MainGame game;

    private Stack<GameState> gameStates;

    public static final int MENU = 123398;
    public static final int PLAY = 234345;

    public GameStateManager(MainGame game)
    {
        this.game = game;
        gameStates = new Stack<GameState>();
        pushState(MENU);
    }

    public MainGame game()
    {
        return game;
    }

    public void update(float dt)
    {
        gameStates.peek().update(dt);
    }

    public void render()
    {
        gameStates.peek().render();
    }

    public GameState getState(int state)
    {
        if (state == MENU) {
            return new Menu(this);
        }

        if (state == PLAY) {
            System.out.println("Loading PLAY state...");
            return new Play(this);
        }

        return null;
    }

    public void setState(int state)
    {
        popState();
        pushState(state);
    }

    public void pushState(int state)
    {
        System.out.println("Pushing state..." + state);
        gameStates.push(getState(state));
    }

    public void popState()
    {
        GameState g = gameStates.pop();
        g.dispose();
    }
}
