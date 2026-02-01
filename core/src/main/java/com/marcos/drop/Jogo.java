package com.marcos.drop;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;


/** {@link ApplicationListener} implementation shared by all platforms. */
public class Jogo implements Screen {
    final Gota jogo;

    Texture texturaDeFundo;
    Texture texturaDoBalde;
    Texture texturaDaGota;
    Sound somDaGota;
    Music musica;
    Sprite spriteDoBalde;
    Vector2 posicaoDoToque;
    Array<Sprite> spritesDasGotas;
    float temporizadorGota;
    Rectangle retanguloDoBalde;
    Rectangle dropRectangle;
    int gotasColetadas;

    public Jogo(final Gota jogo) {
        this.jogo = jogo;

        texturaDeFundo = new Texture("background.png");
        texturaDoBalde = new Texture("bucket.png");
        texturaDaGota = new Texture("drop.png");

        somDaGota = Gdx.audio.newSound(Gdx.files.internal("drop.mp3"));
        musica = Gdx.audio.newMusic(Gdx.files.internal("music.mp3"));
        musica.setLooping(true);
        musica.setVolume(0.5F);

        spriteDoBalde = new Sprite(texturaDoBalde);
        spriteDoBalde.setSize(1, 1);

        posicaoDoToque = new Vector2();

        retanguloDoBalde = new Rectangle();
        dropRectangle = new Rectangle();

        spritesDasGotas = new Array<>();
    }

    @Override
    public void show() {
        musica.play();
    }

    @Override
    public void render(float delta) {
        input();
        logic();
        draw();
    }

    private void input() {
        float velocidade = 4f;
        float delta = Gdx.graphics.getDeltaTime();

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            spriteDoBalde.translateX(velocidade * delta);
        }
        else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            spriteDoBalde.translateX(-velocidade * delta);
        }

        if (Gdx.input.isTouched()) {
            posicaoDoToque.set(Gdx.input.getX(), Gdx.input.getY());
            jogo.viewport.unproject(posicaoDoToque);
            spriteDoBalde.setCenterX(posicaoDoToque.x);
        }
    }

    private void logic() {
        float larguraTotal = jogo.viewport.getWorldWidth();
        float alturaTotal = jogo.viewport.getWorldHeight();
        float larguraDoBalde = spriteDoBalde.getWidth();
        float alturaDoBalde = spriteDoBalde.getHeight();
        float delta = Gdx.graphics.getDeltaTime();

        spriteDoBalde.setX(MathUtils.clamp(spriteDoBalde.getX(), 0, larguraTotal - larguraDoBalde));
        retanguloDoBalde.set(spriteDoBalde.getX(), spriteDoBalde.getY(), larguraDoBalde, alturaDoBalde);

        for (int i = spritesDasGotas.size - 1; i >= 0; i--) {
            Sprite spriteDaGota = spritesDasGotas.get(i);
            float larguraDaGota = spriteDaGota.getWidth();
            float alturaDaGota = spriteDaGota.getHeight();

            spriteDaGota.translateY(-2f * delta);
            dropRectangle.set(spriteDaGota.getX(), spriteDaGota.getY(), larguraDaGota, alturaDaGota);

            if (spriteDaGota.getY() < -alturaDaGota) spritesDasGotas.removeIndex(i);
            else if (retanguloDoBalde.overlaps(dropRectangle)) {
                gotasColetadas++;
                spritesDasGotas.removeIndex(i);
                somDaGota.play();
            }
        }

        temporizadorGota += delta;
        if (temporizadorGota > 1f) {
            temporizadorGota = 0;
            criarChuva();
        }
    }

    private void draw() {
        ScreenUtils.clear(Color.BLACK);
        jogo.viewport.apply();
        jogo.batch.setProjectionMatrix(jogo.viewport.getCamera().combined);
        jogo.batch.begin();

        float larguraTotal = jogo.viewport.getWorldWidth();
        float alturaTotal = jogo.viewport.getWorldHeight();

        jogo.batch.draw(texturaDeFundo, 0, 0, larguraTotal, alturaTotal);
        spriteDoBalde.draw(jogo.batch);

        jogo.font.draw(jogo.batch, "Gotas Coletadas: " + gotasColetadas, 0, alturaTotal);

        for (Sprite spriteDaGota : spritesDasGotas) {
            spriteDaGota.draw(jogo.batch);
        }

        jogo.batch.end();
    }

    private void criarChuva() {
        float larguraDaGota = 1;
        float alturaDaGota = 1;
        float larguraTotal = jogo.viewport.getWorldWidth();
        float alturaTotal = jogo.viewport.getWorldHeight();

        Sprite spriteDaGota = new Sprite(texturaDaGota);
        spriteDaGota.setSize(larguraDaGota, alturaDaGota);
        spriteDaGota.setX(MathUtils.random(0F, larguraTotal - larguraDaGota));
        spriteDaGota.setY(alturaTotal);
        spritesDasGotas.add(spriteDaGota);
    }

    @Override
    public void resize(int width, int height) {
        jogo.viewport.update(width, height, true);
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        texturaDeFundo.dispose();
        somDaGota.dispose();
        musica.dispose();
        texturaDaGota.dispose();
        texturaDoBalde.dispose();
    }
}
