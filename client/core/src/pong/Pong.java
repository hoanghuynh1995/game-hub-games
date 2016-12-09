package pong;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;


public class Pong extends ApplicationAdapter {
	private final float UPDATE_TIME = 1/60f;
	private final float player1XPos = 20;
	private final float player2XPos = 800 - 30;
	private final String STATE_WAITING = "waiting";
	private final String STATE_PLAYING = "playing";
	private final String STATE_ENDED = "ended";

	private float timer = 0;
	private BitmapFont font;

	private JSONObject transferredData = new JSONObject();

	private OrthographicCamera camera;
	private SpriteBatch batch;

	private Rectangle bucket;
	private Vector3 touchPos;
	private Vector3 playerPos;

	private Player player;
	private Player opponent;
	private Player ball;
	private boolean isPlayer1 = false;

	private Texture pongTexture;
	private Texture backgroundTexture;
	private Texture ballTexture;

	private String id;
	private String state = STATE_WAITING;
	private Socket socket;

	private String result = "";
	MyFileHandleResolver fileHandleResolver;

	public Pong(Socket socket){
		this.socket = socket;
	}
	@Override
	public void create () {
		batch = new SpriteBatch();
		font = new BitmapFont();

		//set camera
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);

		//bucket rect
		bucket = new Rectangle();
		bucket.x = 800 / 2 - 64 / 2;
		bucket.y = 20;
		bucket.width = 64;
		bucket.height = 64;

		//
		touchPos = new Vector3();
		playerPos = new Vector3();

		//load assets
		fileHandleResolver = new MyFileHandleResolver();
		pongTexture = new Texture(fileHandleResolver.resolve("PongPlayer.png"));
		backgroundTexture = new Texture(fileHandleResolver.resolve("pongbackground.png"));
		ballTexture = new Texture(fileHandleResolver.resolve("ball.png"));

		//create ball
		ball = new Player(ballTexture,393,235,14,14);
		//connect
		configSocketEvents();

	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.draw(backgroundTexture, 0, 0);
		ball.draw(batch);
		if (player != null) {
			player.draw(batch);
		}
		if (opponent != null) {
			opponent.draw(batch);
		}
		if(state == STATE_WAITING){
			font.getData().setScale(3f);
			font.draw(batch,"Tap to ready",800/2 - 100,480/2);
			if(Gdx.input.isTouched()) {
				socket.emit("ready");
			}
		}else if(state == STATE_PLAYING){
			handleInput(Gdx.graphics.getDeltaTime());
			updateServer(Gdx.graphics.getDeltaTime());
			if(ball.rectangle.overlaps(player.rectangle)){
				socket.emit("onCollision");
			}
		}else if(state== STATE_ENDED){
			font.draw(batch,result,800/2 - 40,480/2);
		}
		batch.end();
	}
	private void updateServer(float dt){
		timer += dt;
		if(timer >= UPDATE_TIME && player != null && player.hasMoved()){
			try {
				transferredData.put("x",player.getX());
				transferredData.put("y",player.getY());
				socket.emit("playerMoved", transferredData);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	private void handleInput(float dt) {
		if(Gdx.input.isTouched()) {
			if(isPlayer1) {
				touchPos.set(player1XPos, Gdx.input.getY() + player.getHeight(), 0);
				camera.unproject(touchPos);
				playerPos = playerPos.slerp(touchPos, 0.3f);
				player.updatePosition(player1XPos, playerPos.y);
			}else{
				touchPos.set(player2XPos, Gdx.input.getY() + player.getHeight(), 0);
				camera.unproject(touchPos);
				playerPos = playerPos.slerp(touchPos, 0.3f);
				player.updatePosition(player2XPos, playerPos.y);
			}
		}
	}

	@Override
	public void dispose () {
		batch.dispose();
		socket.disconnect();
		socket.close();
	}

	public void connectSocket(){
		try {
			socket = IO.socket("http://192.168.0.101:8080");
			socket.connect();
		} catch(Exception e){
			System.out.println(e);
		}
	}
	public void configSocketEvents(){
		socket.on("connected", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				Gdx.app.log("SocketIO", "Connected");
				//playerPos.set(player.previousPosition.x,player.previousPosition.y,0);
			}
		}).on("socketID", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				JSONObject data = (JSONObject) args[0];
				try {

					id = data.getString("id");
					Gdx.app.log("SocketIO", "My ID: " + id);
				} catch (JSONException e) {
					Gdx.app.log("SocketIO", "Error getting ID");
				}
			}
		}).on("playerDisconnected", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				JSONObject data = (JSONObject) args[0];
				try {
					id = data.getString("id");
				}catch(JSONException e){
					Gdx.app.log("SocketIO", "Error getting disconnected PlayerID");
				}
			}
		}).on("playerMoved", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				JSONObject data = (JSONObject) args[0];
				try {
					String playerId = data.getString("id");
					opponent.updatePosition((float)data.getDouble("x"),(float)data.getDouble("y"));
				}catch(JSONException e){
					Gdx.app.log("SocketIO", "Error getting disconnected PlayerID");
				}
			}
		}).on("startGame", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				JSONObject objects = (JSONObject) args[0];
				try {
					isPlayer1 = objects.getInt("player") == 1;
					if(isPlayer1){
						player = new Player(pongTexture,player1XPos,480/2,10,100);
						opponent = new Player(pongTexture,player2XPos,480/2,10,100);
					}else{
						player = new Player(pongTexture,player2XPos,480/2,10,100);
						opponent = new Player(pongTexture,player1XPos,480/2,10,100);
					}
					state = STATE_PLAYING;
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}).on("ballInfo", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				JSONObject objects = (JSONObject) args[0];
				try {
					ball.updatePosition((float)(objects.getDouble("x")),(float)(objects.getDouble("y")));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}).on("endGame", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				JSONObject objects = (JSONObject) args[0];
				try {
					if(objects.getBoolean("win")){
						result = "You win";
					}else{
						result = "You lose";
					}
					state = STATE_ENDED;
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}
}
