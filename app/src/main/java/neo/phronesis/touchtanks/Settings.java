package neo.phronesis.touchtanks;

import neo.phronesis.touchtanks.R;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class Settings extends Activity{

	
	TextView playerText;
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		String PREFS_NAME = "settings_player";

		SharedPreferences settings_players = getSharedPreferences(
				PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings_players.edit();
		String key = "play_settings";

		int temp = Integer.parseInt( (String) playerText.getText());
		
		editor.putInt(key, temp);
		editor.commit();
		
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.settings);
		
		SeekBar seekbar = (SeekBar) findViewById(R.id.settings_progress);
		
		playerText = (TextView) findViewById(R.id.settings_progress_text);
		
		seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {

				String text = Integer.toString(Math.round(progress/33)+1);
				playerText.setText(text);
				
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
		});
		
	}
	
	
	
	

}
