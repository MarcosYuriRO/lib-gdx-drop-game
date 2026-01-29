package com.marcos.drop;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Jogo implements ApplicationListener {
    Texture texturaDoFundo;
    Texture texturaDoBalde;
    Texture texturaDaGota;
    Sound somDaGota;
    Music musica;

    Sprite spriteDoBalde;

    SpriteBatch spriteBatch;
    FitViewport viewport;

    Vector2 posicaoToque;

    Array<Sprite> spritesDasGotas;

    float temporizadorGota;

    Rectangle retanguloDoBalde;
    Rectangle retanguloDaGota;

    @Override
    public void create() {
        // Prepare your application here.
        texturaDoFundo = new Texture("background.png");
        texturaDoBalde = new Texture("bucket.png");
        texturaDaGota = new Texture("drop.png");

        somDaGota = Gdx.audio.newSound(Gdx.files.internal("drop.mp3"));
        musica = Gdx.audio.newMusic(Gdx.files.internal("music.mp3"));

        spriteBatch = new SpriteBatch();
        viewport = new FitViewport(8, 5);
        spriteDoBalde = new Sprite(texturaDoBalde);
        spriteDoBalde.setSize(1,1);

        posicaoToque = new Vector2();

        spritesDasGotas = new Array<>();

        retanguloDoBalde = new Rectangle();
        retanguloDaGota = new Rectangle();

        musica.setLooping(true);
        musica.setVolume(.2f);
        musica.play();
    }

    @Override
    public void resize(int width, int height) {
        // If the window is minimized on a desktop (LWJGL3) platform, width and height are 0, which causes problems.
        // In that case, we don't resize anything, and wait for the window to be a normal size before updating.
        if(width <= 0 || height <= 0) return;

        // Resize your application here. The parameters represent the new window size.

        viewport.update(width, height, true);
    }



    @Override
    public void render() {
        // Draw your application here.
        input();
        logic();
        draw();
    }

    private void input(){
        float velocidade = 4f;
        float delta = Gdx.graphics.getDeltaTime();

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            spriteDoBalde.translateX(velocidade * delta);
        } else if(Gdx.input.isKeyPressed(Input.Keys.LEFT)){
            spriteDoBalde.translateX(-velocidade * delta);
        }

        if (Gdx.input.isTouched()){
            posicaoToque.set(Gdx.input.getX(), Gdx.input.getY());
            viewport.unproject(posicaoToque);
            spriteDoBalde.setCenterX(posicaoToque.x);
        }
    }

    private void logic(){
        float larguraTotal = viewport.getWorldWidth();
        float alturaTotal = viewport.getWorldHeight();

        float larguraDoBalde = spriteDoBalde.getWidth();
        float alturaDoBalde = spriteDoBalde.getHeight();

        spriteDoBalde.setX(MathUtils.clamp(spriteDoBalde.getX(), 0, larguraTotal - larguraDoBalde));

        float delta = Gdx.graphics.getDeltaTime();

        retanguloDoBalde.set(spriteDoBalde.getX(), spriteDoBalde.getY(), larguraDoBalde, alturaDoBalde);

        for (int i = spritesDasGotas.size - 1; i >= 0 ; i--) {
            Sprite spriteDaGota = spritesDasGotas.get(i);
            float larguraDaGota = spriteDaGota.getWidth();
            float alturaDaGota = spriteDaGota.getHeight();

            spriteDaGota.translateY(-2f * delta);

            retanguloDaGota.set(spriteDaGota.getX(), spriteDaGota.getY(), larguraDaGota, alturaDaGota);

            if (spriteDaGota.getY() < -alturaDaGota){
                spritesDasGotas.removeIndex(i);
            } else if (retanguloDoBalde.overlaps(retanguloDaGota)) {
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

    private void draw(){
        ScreenUtils.clear(Color.BLACK);
        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);

        spriteBatch.begin();

        float larguraTotal = viewport.getWorldWidth();
        float alturaTotal = viewport.getWorldHeight();


        spriteBatch.draw(texturaDoFundo, 0, 0, larguraTotal, alturaTotal);
        spriteDoBalde.draw(spriteBatch);

        for (Sprite gotaCaindo : spritesDasGotas){
            gotaCaindo.draw(spriteBatch);
        }

        spriteBatch.end();
    }

    private void criarChuva(){
        float larguraDaGota = 1;
        float alturaDaGota = 1;

        Sprite spriteDaGota = new Sprite(texturaDaGota);
        spriteDaGota.setSize(larguraDaGota, alturaDaGota);
        spriteDaGota.setX(MathUtils.random(0f, viewport.getWorldWidth() - larguraDaGota));
        spriteDaGota.setY(viewport.getWorldHeight());
        spritesDasGotas.add(spriteDaGota);
    }

    @Override
    public void pause() {
        // Invoked when your application is paused.
    }

    @Override
    public void resume() {
        // Invoked when your application is resumed after pause.
    }

    @Override
    public void dispose() {
        // Destroy application's resources here.
    }
}
