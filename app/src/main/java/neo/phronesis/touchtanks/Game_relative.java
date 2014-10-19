package neo.phronesis.touchtanks;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import neo.phronesis.touchtanks.Main_game.GameView;


import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;

public class Game_relative extends Activity implements OnTouchListener{

	Button next_player = (Button) findViewById(R.id.next_player);

	Canvas canvas;

	Paint myPaint, statusPaint, exploPaint;

	Random generator = new Random();

	GameView TankView;

	Bitmap next, next_pressed, back, back_pressed, check, check_pressed,
			power_slide, bullet_1, bullet_2, bullet_3, bullet_4, bullet_5,
			bullet;

	int screen_height, screen_width, number_of_players, player_num,
			instruct_num, angle_int, bullet_num;
	int bullet_max;
	int ammo_num;
	int bullet_counter;
	boolean not_pressed = true;
	int counter;
	boolean once = true;
	boolean touching = false;
	boolean moving = false;
	boolean fire = false;
	boolean firing = false;
	boolean air_strike = false;

	float tank_height, tank_width, touch_y, touch_x, next_tank_x,
			movement_amount, gas_max, origin_x, origin_y, dest_x, dest_y,
			angle_float, gun_length, max_height, air_x;

	/*
	 * This is some physics information that is used in missile flight, feel
	 * free to disregard this section as it not needed for complete
	 * understanding of the program.
	 * 
	 * m-missile = 10 kg g = 9.8 m/s g = 9.8 n/kg d = 1 000 m f-max = 45 000 N
	 * theoretical gun_length (not to scale in game) = 2.0 m KE of missile = 45
	 * 000 J V-max = 70 m/s
	 */

	double m_missile = 10, gravity = 9.8, delta_d = 1000, f_max = 45000,
			t_gun_length = 2.0, KE_max = 45000, v_max = 70;

	List<Float> terrain_y = new ArrayList<Float>();
	List<Float> tank_x = new ArrayList<Float>();
	List<Float> tank_y = new ArrayList<Float>();
	List<Float> tank_gun_x = new ArrayList<Float>();
	List<Float> tank_gun_y = new ArrayList<Float>();
	List<Float> gas = new ArrayList<Float>();
	List<String> instructions = new ArrayList<String>();
	List<Double> angle = new ArrayList<Double>();
	List<Integer> power = new ArrayList<Integer>();
	List<Float> bullet_y = new ArrayList<Float>();
	List<Float> bullet_x = new ArrayList<Float>();
	List<Integer> angle_right = new ArrayList<Integer>();
	List<Integer> health = new ArrayList<Integer>();
	List<Boolean> dead = new ArrayList<Boolean>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

	//	TankView = new GameView(this);
		TankView.setOnTouchListener(this);

		next = BitmapFactory.decodeResource(getResources(),
				R.drawable.right_unpressed);
		next_pressed = BitmapFactory.decodeResource(getResources(),
				R.drawable.right_pressed);
		back = BitmapFactory.decodeResource(getResources(),
				R.drawable.left_unpressed);
		back_pressed = BitmapFactory.decodeResource(getResources(),
				R.drawable.left_pressed);
		check = BitmapFactory.decodeResource(getResources(), R.drawable.check);
		check_pressed = BitmapFactory.decodeResource(getResources(),
				R.drawable.check_pressed);
		power_slide = BitmapFactory.decodeResource(getResources(),
				R.drawable.power);
		bullet_1 = BitmapFactory.decodeResource(getResources(),
				R.drawable.bullet);
		bullet_2 = BitmapFactory.decodeResource(getResources(),
				R.drawable.volcano);
		bullet_3 = BitmapFactory.decodeResource(getResources(),
				R.drawable.shower);
		bullet_4 = BitmapFactory
				.decodeResource(getResources(), R.drawable.atom);
		bullet_5 = BitmapFactory.decodeResource(getResources(),
				R.drawable.air_strike);
		bullet = BitmapFactory.decodeResource(getResources(),
				R.drawable.bullet_circle);

		setContentView(TankView);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub

		super.onResume();

		TankView.resume();

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		TankView.pause();

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub

		touch_x = event.getX();
		touch_y = event.getY();
		// determines if the event was a lift up or a push down (the source of
		// the touch hit or left the screen)
		if (event.getAction() == MotionEvent.ACTION_UP) {
			touching = false;
		} else if (event.getAction() == MotionEvent.ACTION_DOWN) {
			touching = true;
		}
		return true;
	}

	
	
}
