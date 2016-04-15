package net.wyvernia.gdxtest;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

public class MyGdxTest extends ApplicationAdapter {
	SpriteBatch batch;
	//Texture img;
	private Texture dropImage;
	private Texture bucketImage;
	private Rectangle bucket;
	private Vector3 touchPos;
	private Array<Rectangle> raindrops;
	private long lastDropTime;

	private OrthographicCamera camera;

	private BitmapFont font;
	private int score;
	private int timeForNextDrop = 1000;

	//
    private static final int        FRAME_COLS = 12;         // #1
    private static final int        FRAME_ROWS = 8;          // #2

    Animation walkAnimation;
    Texture catapultShoot;
    TextureRegion[] nFrames;
    TextureRegion[] neFrames;
    TextureRegion[] eFrames;
    TextureRegion[] seFrames;
    TextureRegion[] sFrames;
    TextureRegion[] swFrames;
    TextureRegion[] wFrames;
    TextureRegion[] nwFrames;
    TextureRegion currentFrame;
    float stateTime;


	
	@Override
	public void create () {
		batch = new SpriteBatch();
		dropImage = new Texture(Gdx.files.internal("droplet.png"));
		bucketImage = new Texture(Gdx.files.internal("bucket.png"));
		font = new BitmapFont();

		//img = new Texture("badlogic.jpg");
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);

		bucket = new Rectangle();
		bucket.x = 800 / 2 - 64 / 2;
		bucket.y = 20;
		bucket.width = 64;
		bucket.height = 64;
		raindrops = new Array<Rectangle>();
		spawnRaindrop();

		//
        catapultShoot = new Texture(Gdx.files.internal("catapult_shoot.png"));
        TextureRegion[][] tmp = TextureRegion.split(catapultShoot, catapultShoot.getWidth()/FRAME_COLS, catapultShoot.getHeight()/FRAME_ROWS);
        swFrames = new TextureRegion[FRAME_COLS];
        sFrames = new TextureRegion[FRAME_COLS];
        seFrames = new TextureRegion[FRAME_COLS];
        eFrames = new TextureRegion[FRAME_COLS];
        neFrames = new TextureRegion[FRAME_COLS];
        nFrames = new TextureRegion[FRAME_COLS];
        nwFrames = new TextureRegion[FRAME_COLS];
        wFrames = new TextureRegion[FRAME_COLS];
        for (int i = 0; i < FRAME_ROWS; i++) {
            for (int j = 0; j < FRAME_COLS; j++) {
                switch(i){
                    case 0: swFrames[j] = tmp[i][j]; break; //sw
                    case 1: sFrames[j] = tmp[i][j]; break; //s
                    case 2: seFrames[j] = tmp[i][j]; break; //se
                    case 3: eFrames[j] = tmp[i][j]; break; //e
                    case 4: neFrames[j] = tmp[i][j]; break; //ne
                    case 5: nFrames[j] = tmp[i][j]; break; //n
                    case 6: nwFrames[j] = tmp[i][j]; break; //nw
                    case 7: wFrames[j] = tmp[i][j]; break; //w
                }
            }
        }
        walkAnimation = new Animation(0.025f, eFrames);      // #11
        stateTime = 0f;

    }

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		camera.update();

        //
        stateTime += Gdx.graphics.getDeltaTime();           // #15
        currentFrame = walkAnimation.getKeyFrame(stateTime, true);  // #16

		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.draw(bucketImage, bucket.x, bucket.y);
		for(Rectangle raindrop: raindrops) {
			batch.draw(dropImage, raindrop.x, raindrop.y);
		}
		font.draw(batch,"Score: "+score, 10,10);
        batch.draw(currentFrame, bucket.x, bucket.y+128);

		batch.end();

		if(Gdx.input.isTouched()) {
			touchPos = new Vector3();
			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(touchPos);
			bucket.x = touchPos.x - 64 / 2;
		}

		Iterator<Rectangle> iter = raindrops.iterator();
		while(iter.hasNext()) {
			Rectangle raindrop = iter.next();
			raindrop.y -= 200 * Gdx.graphics.getDeltaTime();
			if(raindrop.y + 64 < 0) iter.remove();
			if(raindrop.overlaps(bucket)) {
				//dropSound.play();
				//timeForNextDrop -= timeForNextDrop/100;
				score++;
				iter.remove();
			}
		}

		if(TimeUtils.nanoTime() - lastDropTime > timeForNextDrop * 1000000) spawnRaindrop();

		//update score

	}

	private void spawnRaindrop() {
		Rectangle raindrop = new Rectangle();
		raindrop.x = MathUtils.random(0, 800-64);
		raindrop.y = 480;
		raindrop.width = 64;
		raindrop.height = 64;
		raindrops.add(raindrop);
		lastDropTime = TimeUtils.nanoTime();
	}

	@Override
	public void dispose() {
		dropImage.dispose();
		bucketImage.dispose();
		batch.dispose();
	}
}
