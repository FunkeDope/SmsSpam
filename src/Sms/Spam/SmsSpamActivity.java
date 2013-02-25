package Sms.Spam;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class SmsSpamActivity extends Activity {
    /** Called when the activity is first created. */
	
	EditText inputNumber;
	EditText inputMessage;
	EditText inputAmount;
	EditText inputDelay;
	String phNumber;
	String txtMessage;
	int amount;
	int delay;
	Boolean hammer = false;
	
	final String DEFAULT_NUMBER = "123";
	final String DEFAULT_MESSAGE = "no sean kanes | (c) bill stickers inc";
	final String DEFAULT_AMOUNT = "30";
	final String DEFAULT_DELAY = "50";
	
	SmsManager sm = SmsManager.getDefault();
	
	SharedPreferences prefs;
	
	
	//BroadcastReceiver receiver;
	//PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent("SMS_SENT"), 0);
	//PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0, new Intent("SMS_DELIVERED"), 0);
	
	@Override
    public void onCreate (Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        inputNumber = (EditText) findViewById(R.id.inputNumber);
        inputMessage = (EditText) findViewById(R.id.inputMessage);
        inputAmount = (EditText) findViewById(R.id.inputAmount);
        inputDelay = (EditText) findViewById(R.id.inputDelay);
        
        // Restore preferences
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        inputNumber.setText(prefs.getString("inputNumber", DEFAULT_NUMBER));
        inputMessage.setText(prefs.getString("inputMessage", DEFAULT_MESSAGE));
        inputAmount.setText(prefs.getString("inputAmount", DEFAULT_AMOUNT));
        inputDelay.setText(prefs.getString("inputDelay", DEFAULT_DELAY));
        
        
        //listeners
        /*receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
            	switch (getResultCode())
        		{
    	    		case Activity.RESULT_OK:
    	    			alert("SMS Sent");
    	    			break;
    				default:
    	    			alert("SMS ERROR: " + getResultCode());
    	    			break;
        		}	
            }
          };
          IntentFilter filter = new IntentFilter();
          filter.addAction("SMS_SENT");
          filter.addAction("SMS_DELIVERED");
          registerReceiver(receiver, filter);*/
	}
    
    public void sendSMS (View v)
    {
    	phNumber = inputNumber.getText().toString();
    	txtMessage = inputMessage.getText().toString();
    	amount = new Integer(inputAmount.getText().toString());
    	delay = new Integer(inputDelay.getText().toString());
    	hammer = (delay < 30) ? true : false;
    	
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setMessage("Are you SURE you want to send " + amount + " messages!?\n\nTHIS CANNOT BE STOPPED!")
    		.setCancelable(false)
    		.setPositiveButton("Yes", new DialogInterface.OnClickListener() {				
				public void onClick(DialogInterface dialog, int which) {
					//if the delay is to short the timer doesnt work right, so just hammer away oldschool
					if(hammer)
					{
						for (int x = 0; x < amount; x++)
				    	{
				    		dispatchSMS();
				    		try {Thread.sleep(delay);}
				    		catch (InterruptedException e) {e.printStackTrace();}
				    	}
					}
					else
					{
						new CountDownTimer(delay*amount, delay) {
							public void onTick(long millisUntilFinished) {
								Log.d("alert", "inOnTick");
								dispatchSMS();
							}
							public void onFinish() {
								Log.d("alert", "Finished Sending All");
								completedSMS();
							}
						}.start();
					}
				}
			})
			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			});
    	
    	AlertDialog alert = builder.create();
    	alert.show();
    }
    public void dispatchSMS ()
    {
    	//sm.sendTextMessage(phNumber, null, txtMessage, sentPI, deliveredPI);
    	sm.sendTextMessage(phNumber, null, txtMessage, null, null);
    }
    public void completedSMS ()
    {
    	Toast.makeText(getApplicationContext(), "All messages sent!", Toast.LENGTH_SHORT).show();
    }
    
    
    public void alert (String txt)
	{
		Toast.makeText(getApplicationContext(), txt, Toast.LENGTH_SHORT).show();
	}
    
    public void restoreDefaults (View v)
    {
        inputNumber.setText(DEFAULT_NUMBER);
        inputMessage.setText(DEFAULT_MESSAGE);
        inputAmount.setText(DEFAULT_AMOUNT);
        inputDelay.setText(DEFAULT_DELAY);
        
        Toast.makeText(getApplicationContext(), "All inputs reverted to defaults.", Toast.LENGTH_LONG).show();
    }
    
    @Override
    protected void onPause(){
       super.onPause();

       //save the data
       prefs = PreferenceManager.getDefaultSharedPreferences(this);
       SharedPreferences.Editor editor = prefs.edit();
        
       //"savedData" is the key that we will use in onCreate to get the saved data 
       //mDataString is the string we want to save
       editor.putString("inputNumber", inputNumber.getText().toString());
       editor.putString("inputMessage", inputMessage.getText().toString());
       editor.putString("inputAmount", inputAmount.getText().toString());
       editor.putString("inputDelay", inputDelay.getText().toString());

       // commit the edits
       editor.commit();
    }
    
    @Override
    protected void onDestroy() {
    	onPause();
    	super.onDestroy();
    	//unregisterReceiver(receiver);
    }
}