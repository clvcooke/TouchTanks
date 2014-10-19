
//Package Name
package neo.phronesis.touchtanks;


//Imports needed for application to function
import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;
import android.app.Activity;
import android.content.Intent;

//Definition of the main class of this activity
public class Splash extends Activity {

	
	//onCreate method, this will occur upon the creation of the activity (when it is called)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //sets the Content to the activity_splash xml file (which is just a videoview)
        setContentView(R.layout.activity_splash);
        
        //defines the videoview by declaring it and finding it by its id
        VideoView videoView = (VideoView) findViewById(R.id.videoView);
        
        //creates a new mediacontroller to enable us to use the videoview
        MediaController mc = new MediaController(this);
        
        //sets the videoviews mediacontroller to the previously created mediacontroller
        videoView.setMediaController(mc);
        
        //defines the path of the video
        String str = "android.resource://neo.phronesis.touchtanks/raw/video";
        
        //sets the path for the videoview
        videoView.setVideoURI(Uri.parse(str));
        
        //requests focus from the system
        videoView.requestFocus();
        
        //starts the video
        videoView.start();
        
        
        //defines the timer Thread
        Thread timer = new Thread(){
        	//the run method inside the thread (a mandatory method and the first one to activate)
        	public void run(){
        		
        		//attempts to "sleep" pause for 4 seconds
        		try{
        			sleep(4000);
        			
        			//catches if the process is interuptted
        		}catch (InterruptedException e){
        			
        			//prints the error so it can be traced and fixed
        			e.printStackTrace();
        			
        			//this happens no matter if there is an error or not
        		}finally{
        			
        			//creates an intent which is defined as the path to the menu activity
        			Intent open = new Intent("neo.phronesis.touchtanks.MENU");
        			
        			//opens the menu activity using the above created intent
        			startActivity(open);
        		}
        	}
        };
        
        //starts the timer thread
        timer.start();
        
        
        
        
    }

    
    //This occurs if the user pauses (navigates away from the app)
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
	
		super.onPause();
		
		//kills all processes and ends the application
		finish();
	}

    
}
