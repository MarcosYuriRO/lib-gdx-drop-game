package com.marcos.drop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ScreenUtils;

public class MainMenuScreen implements Screen {

    final Gota jogo;

    public MainMenuScreen(final Gota jogo){
        this.jogo = jogo;
    }


    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.BLACK);

        jogo.viewport.apply();
        jogo.batch.setProjectionMatrix(jogo.viewport.getCamera().combined);

        jogo.batch.begin();
        jogo.font.draw(jogo.batch, "Bem-Vindo a Gotas!", 1, 1.5f);
        jogo.font.draw(jogo.batch, "Clique qualque botão para começar", 1, 1);
        jogo.batch.end();

        if (Gdx.input.isTouched()){
            jogo.setScreen(new Jogo(jogo));
            dispose();
        }
    }

    @Override
    public void resize(int i, int i1) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
