package neo.phronesis.touchtanks;

/************************************************************************************************************************************************************************************************************************************************************************************
 * Author: Colin Cooke
 * File: Main_game.java
 * 
 * Created on May 20th, 2013
 * 
 * Title: Touch Tanks - Main game
 ************************************************************************************************************************************************************************************************************************************************************************************/

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import neo.phronesis.touchtanks.Main_game;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.os.Bundle;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.ViewSwitcher;

public class Main_game extends Activity implements OnTouchListener {

	Button next_player, next_button, back_button;

	ViewSwitcher viewSwitcher;

	View adjust1, adjust2;

	SeekBar seekbar;

	Canvas canvas;

	Paint myPaint, statusPaint, exploPaint;

	Random generator = new Random();

	GameView TankView;
	SurfaceView surface;

	TextView player_text, power_text, angle_text, fuel_text, health_text,
			adjust_text;

	Bitmap next, next_pressed, back, back_pressed, check, check_pressed,
			power_slide, bullet_1, bullet_2, bullet_3, bullet_4, bullet_5,
			bullet;

	Bitmap background;

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
	boolean isRunning = false;

	float tank_height, tank_width, touch_y, touch_x, next_tank_x,
			movement_amount, gas_max, origin_x, origin_y, dest_x, dest_y,
			angle_float, gun_length, max_height, air_x;

	final Context context = this;

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

		// adds strings to instructions
		instructions.add("Touch Location You Want To Move To");
		instructions.add("Aim Your Tank!");
		instructions.add("Adjust Your Power!");
		//instructions.add("Choose Your Weapon!");

		setContentView(R.layout.game_layout);

		player_text = (TextView) findViewById(R.id.Player);
		power_text = (TextView) findViewById(R.id.Power);
		angle_text = (TextView) findViewById(R.id.Angle);
		health_text = (TextView) findViewById(R.id.Health);
		adjust_text = (TextView) findViewById(R.id.adjustText);
		fuel_text = (TextView) findViewById(R.id.Fuel);
		next_player = (Button) findViewById(R.id.next_player);
		surface = (SurfaceView) findViewById(R.id.surface);
		next_button = (Button) findViewById(R.id.next_button);
		back_button = (Button) findViewById(R.id.back_button);
		adjust1 = findViewById(R.id.adjust1);
		adjust2 = findViewById(R.id.adjust2);
		seekbar = (SeekBar) findViewById(R.id.settings_progress);

		seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {

				if (progress == 0)
					progress = 1;
				power.remove(player_num);
				power.add(player_num, progress);
				power_text.setText(Integer.toString(power.get(player_num))
						+ "%");
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

		});

		viewSwitcher = (ViewSwitcher) findViewById(R.id.viewSwitcher1);

		next_player.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				fire = true;
				if (ammo_num == 5) {
					air_strike = true;
					bullet_counter = 0;
				}
			}
		});

		next_button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				if (instruct_num != instructions.size() - 1) {
					instruct_num = instruct_num + 1;
				} else {
					instruct_num = 0;
				}
				if (instruct_num == 2) {

					viewSwitcher.showNext();
				} else {
					if (viewSwitcher.getNextView() == adjust2)
						adjust_text.setText(instructions.get(instruct_num));
					else {
						viewSwitcher.showNext();
						adjust_text.setText(instructions.get(instruct_num));
					}
				}
			}
		});

	
		
		back_button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				if (instruct_num != 0) {
					instruct_num = instruct_num - 1;
				} else {
					instruct_num = instructions.size() - 1;
				}

				if (instruct_num == 2) {

					viewSwitcher.showNext();
				} else {
					if (viewSwitcher.getNextView() == adjust2)
						adjust_text.setText(instructions.get(instruct_num));
					else {
						viewSwitcher.showNext();
						adjust_text.setText(instructions.get(instruct_num));
					}
				}

			}
		});

		TankView = new GameView(this);
		surface.setOnTouchListener(this);

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
		background = BitmapFactory.decodeResource(getResources(),
				R.drawable.tanks_background);

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
		int gas_percent = (int) ((gas.get(player_num) / gas_max) * 100);
		String temp_str = Integer.toString(gas_percent);
		fuel_text.setText(temp_str + "%");

		// draws the angle
		temp_str = Integer.toString(angle_int);
		angle_text.setText(temp_str);

		// draws the power
		temp_str = Integer.toString(power.get(player_num));
		power_text.setText(temp_str + "%");

		// draws the health
		temp_str = Integer.toString(health.get(player_num));
		health_text.setText(temp_str + "%");

		return true;
	}

	public class GameView extends SurfaceView implements Runnable {

		SurfaceHolder ourHolder;
		Thread gameThread = null;

		Thread tetThread = null;

		public GameView(Context context) {
			super(context);

			gameThread = new Thread(this);
			tetThread = new Thread(this);
			

			ourHolder = surface.getHolder();
		}

		public void pause() {
			isRunning = false;
			while (true) {
				try {
					Main_game.this.finish();
					gameThread.join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			}
			gameThread = null;
			tetThread = null;
		}

		public void resume() {

			gameThread.start();
			tetThread.start();
			isRunning = true;

		}

		public void run() {

			// TODO Auto-generated method stub

			while (isRunning) {
				if (!ourHolder.getSurface().isValid())
					continue;

				canvas = ourHolder.lockCanvas();

				if (once) {

					int x1, x2, y1, y2;

					// /////////////////////
					String PREFS_NAME = "settings_player";
					SharedPreferences player_settings = getSharedPreferences(
							PREFS_NAME, 0);

					number_of_players = player_settings.getInt("play_settings",
							2);
					player_num = 0;
					bullet_num = 1;
					ammo_num = 1;

					// ///////////////////

					instruct_num = 0;
					myPaint = new Paint();
					statusPaint = new Paint();
					air_strike = false;
					once = false;
					screen_height = canvas.getHeight();
					screen_width = canvas.getWidth();

					max_height = screen_height - screen_height / 5;

					for (int i = 0; i < number_of_players; i++) {
						tank_y.add((float) 0);
						if (number_of_players == 2) {
							if (i == 0)
								tank_x.add((float) (screen_width / 10));
							else
								tank_x.add((float) (screen_width / 10 * 9));
						} else if (number_of_players == 3) {
							if (i == 0)
								tank_x.add((float) (screen_width / 10));
							else if (i == 1)
								tank_x.add((float) (screen_width / 2));
							else
								tank_x.add((float) (screen_width / 10 * 9));
						} else if (number_of_players == 1) {
							tank_x.add((float) (screen_width / 2));
						} else {
							if (i == 0)
								tank_x.add((float) (screen_width / 10));
							else if (i == 1)
								tank_x.add((float) ((screen_width - screen_width / 5) / 3 + screen_width / 10));
							else if (i == 2)
								tank_x.add((float) ((screen_width - screen_width / 5) / 3 * 2 + screen_width / 10));
							else
								tank_x.add((float) (screen_width / 10 * 9));

						}
						gas.add((float) ((screen_width / 7) * 2));
						// //////////////////////////////////////////
						angle.add(0.785398163);
						power.add(50);
						angle_right.add(1);
						tank_gun_x.add(null);
						tank_gun_y.add(null);
						health.add(100);
						dead.add(false);
					}

					v_max = v_max * (screen_width / delta_d);

					gas_max = gas.get(0);

					exploPaint = new Paint();

					myPaint.setColor(Color.GREEN);
					myPaint.setStyle(Style.FILL);

					exploPaint.setColor(Color.RED);
					exploPaint.setStyle(Style.FILL);

					statusPaint.setTextSize(screen_height / 10 - screen_height
							/ 25);
					statusPaint.setStyle(Style.FILL);

					tank_height = screen_height / 30;
					tank_width = screen_width / 30;

					movement_amount = screen_width / 100;

					y1 = generator.nextInt((screen_height / 10) * 7)
							+ screen_height / 10;
					y2 = generator.nextInt((screen_height / 10) * 7)
							+ screen_height / 10;

					x1 = 0;

					x2 = generator.nextInt(screen_width);

					bullet_1 = Bitmap.createScaledBitmap(bullet_1,
							screen_height / 10, screen_height / 10, true);
					bullet_2 = Bitmap.createScaledBitmap(bullet_2,
							screen_height / 10, screen_height / 10, true);
					bullet_3 = Bitmap.createScaledBitmap(bullet_3,
							screen_height / 10, screen_height / 10, true);
					bullet_4 = Bitmap.createScaledBitmap(bullet_4,
							screen_height / 10, screen_height / 10, true);
					bullet_5 = Bitmap.createScaledBitmap(bullet_5,
							screen_height / 10, screen_height / 10, true);
					bullet = Bitmap.createScaledBitmap(bullet,
							screen_height / 100, screen_height / 100, true);
					background = Bitmap.createScaledBitmap(background,screen_width,screen_height,true);

					gun_length = tank_width / 5 * 4;

					counter = 0;

					int temp_count = 0;

					while (true) {

						for (int i = 0; i < x2 - x1; i++) {
							float tempy = ((i * i) * (y2 - y1)) / (x2 * x2)
									+ y1;

							terrain_y.add(tempy);
							temp_count = temp_count + 1;
						}

						if (temp_count <= screen_width) {

							y1 = y2;
							x2 = generator.nextInt(screen_width);
							y2 = generator.nextInt((screen_height / 10) * 8);
						} else {
							break;
						}

					}

					// shifts the terrain y values
					for (int i = 0; i < terrain_y.size(); i++) {
						float temp = screen_height - terrain_y.get(i);
						terrain_y.remove(i);
						terrain_y.add(i, temp);
					}

					for (int i = 0; i < number_of_players; i++) {

						// finds position the tank should be at
						float temp_tankx1 = tank_x.get(i);
						float temp_tankx2 = temp_tankx1 + tank_width;
						float temp_tanky1 = terrain_y.get((int) temp_tankx1);
						float temp_tanky2 = terrain_y.get((int) temp_tankx2);

						tank_y.remove(i);
						if (temp_tanky1 < temp_tanky2) {
							tank_y.add(i, temp_tanky1);
						} else {
							tank_y.add(i, temp_tanky2);
						}

					}

				}

				
				canvas.drawBitmap(background,0,0,null);
				// draws terrain
				for (int i = 0; i < terrain_y.size(); i++) {
					// 1st pass (light green)
					myPaint.setColor(Color.GREEN);
					canvas.drawRect(i, terrain_y.get(i) - 4, i + 1,
							screen_height, myPaint);

					myPaint.setColor(Color.rgb(0, 100, 0));
					// 2nd pass (dark green)
					canvas.drawRect(i, terrain_y.get(i), i + 1,
							terrain_y.get(i) - 4, myPaint);
				}

				if (touching && !fire && !air_strike) {
					if (!moving && instruct_num == 0
							&& touch_y <= (screen_height / 10) * 9) {
						next_tank_x = touch_x;
						moving = true;
					} else if (instruct_num == 1) {
						origin_x = tank_x.get(player_num) + tank_width / 2;
						origin_y = tank_y.get(player_num) + tank_height / 4 * 3;

						if (touch_x <= origin_x) {
							angle_right.remove(player_num);
							angle_right.add(player_num, -1);
						} else {
							angle_right.remove(player_num);
							angle_right.add(player_num, 1);
						}

						if (touch_y <= origin_y) {
							dest_x = touch_x;
							dest_y = touch_y;

							double delta_x = dest_x - origin_x;
							double delta_y = dest_y - origin_y;

							double rad = (Math.atan2(delta_y, delta_x))
									+ Math.PI / 2;

							angle.remove(player_num);
							angle.add(player_num, rad);

						}
					} else if (instruct_num == 2) {
						if (touch_y >= screen_height - screen_height / 10) {
							if (touch_x >= screen_width / 4 - screen_height
									/ 10
									&& touch_x <= screen_width / 4) {
								if (power.get(player_num) > 0) {
									int temp = power.get(player_num);
									power.remove(player_num);
									power.add(player_num, temp - 1);
								}
								canvas.drawBitmap(back_pressed, screen_width
										/ 4 - screen_height / 10, screen_height
										- screen_height / 10, null);

							} else if (touch_x >= screen_width / 4 * 3
									&& touch_x <= screen_width / 4 * 3
											+ screen_height / 10) {
								if (power.get(player_num) < 100) {
									int temp = power.get(player_num);
									power.remove(player_num);
									power.add(player_num, temp + 1);
								}
								canvas.drawBitmap(next_pressed,
										screen_width / 4 * 3, screen_height
												- screen_height / 10, null);
							}

						}
					} else if (instruct_num == 3) {
						if (touch_y >= screen_height / 10 * 9) {
							if (touch_x >= 0 && touch_x < screen_height / 10) {
								canvas.drawBitmap(back_pressed, 0,
										screen_height - screen_height / 10,
										null);
								ammo_num = ammo_num - 1;
							} else if (touch_x > screen_height / 10 * 2
									&& touch_x <= screen_height / 10 * 3) {
								canvas.drawBitmap(next_pressed,
										screen_height / 5, screen_height
												- screen_height / 10, null);
								ammo_num = ammo_num + 1;

							}
						}
					}
				} else if (air_strike && touching && not_pressed) {
					air_x = touch_x;
					bullet_counter = 0;
				}

				angle_int = (int) (Math.toDegrees(angle.get(player_num)));

				if (air_strike && not_pressed) {
					// ///////////////////////////////////////////////

					int temp_counter = 0;
					float air_strike_x = 0;
					float air_y = 0;
					List<Float> explo_y = new ArrayList<Float>();
					for (int b = 0; b < 5; b++)
						explo_y.add((float) 0);

					for (int i = 0; i < 5; i++) {

						air_strike_x = air_x - screen_width / 25 * (2 - i);

						air_y = bullet_counter * (screen_height / 50)
								+ screen_height / 10;

						if (air_strike_x >= 0) {
							if (terrain_y.get((int) (air_strike_x)) < air_y) {

								if (explo_y.get(i) == 0) {
									explo_y.remove(i);
									explo_y.add(i, air_y);
									explosion(air_strike_x, explo_y.get(i), screen_width/100);

								} else {

									air_y = explo_y.get(i);
								}

								temp_counter = temp_counter + 1;
							} else {

								canvas.drawBitmap(bullet, air_strike_x
										+ bullet_counter / 4, air_y, null);
							}

						}

						if (temp_counter == 5) {
							air_strike = false;
						}
					}

					bullet_counter = bullet_counter + 1;
				}

				if (fire && !firing && ammo_num != 5) {
					bullet_counter = 0;
					bullet_y.clear();
					bullet_x.clear();

					firing = true;
					// More physics, again this can be skipped over with minimal
					// impact on program understanding,
					// there will be comments regardless although

					// This segment will generate the bullets flight values

					double temp2 = power.get(player_num);

					// Bullet velocity
					double v = v_max * (temp2 / 100);

					// Gun angle
					double a = Math.abs(angle_int);

					if (a > 89)
						a = 89;

					if (a < 1)
						a = 1;

					a = Math.toRadians(a);

					// Horizontal speed
					double h = Math.sin(a) * v;

					// Vertical speed
					double vert = Math.sqrt(v * v - h * h);

					// This will get the bullets apex in its flight using
					// equations of motion

					double apex_y = (vert / 2) * (vert / gravity);
					double apex_x = (vert / gravity) * h;

					// using the apex values to generate a parabolic trajectory
					// for the bullet and add those values to arrays to be used
					// in the firing sequence
					for (int i = 0; i < screen_width; i++) {
						double temp_y = tank_gun_y.get(player_num)

						- (-apex_y / (Math.pow(apex_x, 2)))
								* (Math.pow(i - apex_x, 2)) - apex_y;
						float temp_x;

						temp_x = tank_gun_x.get(player_num)
								+ angle_right.get(player_num) * i;

						bullet_y.add((float) Math.round(temp_y));
						bullet_x.add(temp_x);

					}

					bullet_max = (int) screen_width;
				}
				//
				
				
				if (firing && bullet_counter <= bullet_max - 1

				) {

					if (bullet_y.get(bullet_counter) < terrain_y
							.get((int) (bullet_counter
									* angle_right.get(player_num) + tank_gun_x
									.get(player_num)))) {
						canvas.drawBitmap(bullet, bullet_x.get(bullet_counter),
								bullet_y.get(bullet_counter), null);
						bullet_counter = bullet_counter + screen_width / 200;
					} else {

						explosion(bullet_counter * angle_right.get(player_num)
								+ tank_gun_x.get(player_num),
								bullet_y.get(bullet_counter), screen_width / 25);
						firing = false;
						fire = false;

						for (int i = 0; i < number_of_players; i++) {

							// finds position the tank should be at
							float temp_tankx1 = tank_x.get(i);
							float temp_tankx2 = temp_tankx1 + tank_width;
							float temp_tanky1 = terrain_y
									.get((int) temp_tankx1);
							float temp_tanky2 = terrain_y
									.get((int) temp_tankx2);

							tank_y.remove(i);
							if (temp_tanky1 < temp_tanky2) {
								tank_y.add(i, temp_tanky1);
							} else {
								tank_y.add(i, temp_tanky2);
							}

						}

						String winner = "1";
						// Checks if the player is still alive (health is over
						// 0)
						int temp_counter4 = 0;
						for (int i = 0; i < number_of_players; i++) {
							if (health.get(i) < 0 && !dead.get(i)) {
								dead.remove(i);
								dead.add(i, true);

							}
							
							if(tank_y.get(i)>screen_height){
								dead.remove(i);
								dead.add(i,true);
							}
						}

						for (int i = 0; i < number_of_players; i++) {
							if (dead.get(i)) {
								temp_counter4 = temp_counter4 + 1;
							}
						}

						if (number_of_players - temp_counter4 <= 1) {

							Looper.prepare();

							AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
									context);

							// set title
							alertDialogBuilder.setTitle("We Have A Winner!");

							// set dialog message
							alertDialogBuilder
									.setMessage(
											"Well it looks like player "
													+ winner + " has won")
									.setCancelable(false)
									.setPositiveButton(
											"Exit to Menu",
											new DialogInterface.OnClickListener() {
												public void onClick(
														DialogInterface dialog,
														int id) {
													// if this button is
													// clicked,
													// close
													// current activity
													Looper.myLooper().quit();
													Main_game.this.finish();

												}
											})
									.setNegativeButton(
											"Play Again",
											new DialogInterface.OnClickListener() {
												public void onClick(
														DialogInterface dialog,
														int id) {
													// if this button is
													// clicked, it
													// will bring the player to
													// the
													// settings activity

													Looper.myLooper().quit();
													Main_game.this.finish();
													try {

														Class setClass = Class
																.forName("neo.phronesis.touchtanks.Main_game");
														Intent theIntent = new Intent(
																Main_game.this,
																setClass);
														startActivity(theIntent);
													} catch (ClassNotFoundException e) {
														e.printStackTrace();
													}

												}
											});

							// create alert dialog
							AlertDialog alertDialog = alertDialogBuilder
									.create();

							// show it
							alertDialog.show();
							Looper.loop();

						}

						do {
							if (player_num == number_of_players - 1) {
								player_num = 0;
							} else {
								player_num = player_num + 1;
							}
						} while (dead.get(player_num));

						Main_game.this.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								final String temp23 = Integer
										.toString(player_num + 1);

								// TODO Auto-generated method stub
								player_text.setText(temp23);

								final String temp32 = Integer.toString(health
										.get(player_num));
								health_text.setText(temp32);
							}
						});

						for (int j = 0; j < number_of_players; j++) {
							tank_y.remove(j);
							tank_y.add(j,
									terrain_y.get((int) (tank_x.get(j) - 0)));

							for (int i = 0; i <= tank_width; i++) {
								if (tank_y.get(j) > terrain_y.get((int) (tank_x
										.get(j) + i))) {
									tank_y.remove(j);
									tank_y.add(j, terrain_y.get((int) (tank_x
											.get(j) + i)));
								}
							}
						}

					}
				}

				if (moving) {

					int gas_percent = (int) ((gas.get(player_num) / gas_max) * 100);

					if (gas_percent < 0)
						gas_percent = 0;

					final String temp_str5 = Integer.toString(gas_percent);

					Main_game.this.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							fuel_text.setText(temp_str5 + "%");
						}

					});

					if (gas.get(player_num) == 0 || gas.get(player_num) < 0) {
						moving = false;
						gas.remove(player_num);
						gas.add(player_num, (float) 0);
					} else if (tank_x.get(player_num) > next_tank_x) {
						float temp = tank_x.get(player_num);
						tank_x.remove(player_num);

						if (temp - next_tank_x < movement_amount) {
							tank_x.add(player_num, next_tank_x);
						} else {
							tank_x.add(player_num, temp - movement_amount);
						}

						float temp2 = gas.get(player_num);
						gas.remove(player_num);
						gas.add(player_num, temp2 - movement_amount);

					} else if (tank_x.get(player_num) < next_tank_x) {
						float temp = tank_x.get(player_num);
						tank_x.remove(player_num);
						if (next_tank_x - temp < movement_amount) {
							tank_x.add(player_num, next_tank_x);
						} else {
							tank_x.add(player_num, temp + movement_amount);
						}

						float temp2 = gas.get(player_num);
						gas.remove(player_num);
						gas.add(player_num, temp2 - movement_amount);
					} else {
						moving = false;
					}

					// finds position the tank should be at

					tank_y.remove(player_num);
					tank_y.add(player_num,
							terrain_y.get((int) (tank_x.get(player_num) - 0)));

					for (int i = 0; i <= tank_width; i++) {
						if (tank_y.get(player_num) > terrain_y
								.get((int) (tank_x.get(player_num) + i))) {
							tank_y.remove(player_num);
							tank_y.add(player_num, terrain_y.get((int) (tank_x
									.get(player_num) + i)));
						}
					}

				}

				// draws tanks

				for (int i = 0; i < number_of_players; i++) {
					if (!dead.get(i)) {
						// draws tank body

						if (i == 0)
							myPaint.setColor(Color.WHITE);
						else if (i == 1)
							myPaint.setColor(Color.BLUE);
						else if (i == 2)
							myPaint.setColor(Color.CYAN);
						else if (i == 3)
							myPaint.setColor(Color.MAGENTA);

						canvas.drawRect(tank_x.get(i), tank_y.get(i),
								tank_x.get(i) + tank_width, tank_y.get(i)
										- tank_height, myPaint);

						// finds x and y values for the end of the tank gun
						// based on
						// angle

						tank_gun_x.remove(i);
						tank_gun_y.remove(i);

						if (angle.get(i) == 0) {
							tank_gun_x.add(i, tank_x.get(i) + tank_width / 2
									+ gun_length);
							tank_gun_y.add(i, tank_y.get(i) - tank_height / 4
									* 3);
						} else if (angle.get(i) == 180) {
							tank_gun_x.add(i, tank_x.get(i) + tank_width / 2
									- gun_length);
							tank_gun_y.add(i, tank_y.get(i) - tank_height / 4
									* 3);
						} else if (angle.get(i) > 90) {
							tank_gun_x
									.add(i,
											(float) -(Math.sin(180 - angle
													.get(i)) * gun_length)
													+ tank_x.get(i)
													+ tank_width / 2);
							tank_gun_y
									.add(i, (float) (tank_y.get(i)
											- tank_height / 4 * 3 - (Math
											.cos(angle.get(i)) * gun_length)));
						} else {

							tank_gun_x
									.add(i,
											(float) (Math.sin(angle.get(i)) * gun_length)
													+ tank_x.get(i)
													+ tank_width / 2);
							tank_gun_y
									.add(i, (float) (tank_y.get(i)
											- tank_height / 4 * 3 - (Math
											.cos(angle.get(i)) * gun_length)));
						}

						// draws tank gun
						canvas.drawLine(tank_x.get(i) + tank_width / 2,
								tank_y.get(i) - tank_height / 4 * 3,
								tank_gun_x.get(i), tank_gun_y.get(i), myPaint);
					}
				}

			 Thread	timer = new Thread() {
					public void run() {
						try {
							sleep(10);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						
					}
				};
				timer.start();
				
				
				ourHolder.unlockCanvasAndPost(canvas);
			}
		}
	}

	void explosion(float x, float y, float r) {

		canvas.drawCircle(x, y, r, exploPaint);
		float tempy;
		air_strike = false;

		y = terrain_y.get((int) (x));

		// destroys the terrain and checks if any of the players have been hit
		// by the explosion
		for (int i = 0; i < r * 2; i++) {
			try {

				if (i <= r) {

					tempy = y + (float) Math.sqrt(r * r - i * i);
					if (tempy > terrain_y.get((int) (x - i))) {
						terrain_y.remove((int) (x - i));
						terrain_y.add((int) (x - i), tempy);

					}

				} else {
					tempy = y + (float) Math.sqrt(r * r - Math.pow(i - r, 2));
					if (tempy > terrain_y.get((int) (x + i - r))) {
						terrain_y.remove((int) (x + i - r));
						terrain_y.add((int) (x + i - r), tempy);
					}

				}

			} catch (ArrayIndexOutOfBoundsException e) {
				e.printStackTrace();
			}
		}
		for (int j = 0; j < number_of_players; j++) {
			if (x - r < tank_x.get(j) + tank_width && x + r > tank_x.get(j)) {
				int temph = health.get(j);
				health.remove(j);
				health.add(j, temph - 34);

			}
		}

		for (int j = 0; j < number_of_players; j++) {

			for (int i = 0; i <= tank_width; i++) {
				if (tank_y.get(j) > terrain_y.get((int) (tank_x.get(j) + i))) {
					tank_y.remove(j);
					tank_y.add(j, terrain_y.get((int) (tank_x.get(j) + i)));
				}
			}

		}

	}

}
