package com.solutiosoft.android.inithub.ui;

import static com.solutiosoft.android.inithub.Constants.MESSAGE_ACTIVITY;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.solutiosoft.android.inithub.ApiHelper;
import com.solutiosoft.android.inithub.Constants;
import com.solutiosoft.android.inithub.R;
import com.solutiosoft.android.inithub.dao.InitHubDatabaseManager;
import com.solutiosoft.android.inithub.entities.Message;
import com.solutiosoft.android.inithub.provider.MessageProvider;

public class MessageAddActivity extends Activity {
	
	private EditText mComments;
	private int mSubjectId;
	private static final String TAG = "MessageAddActivity";
	private MessageAddTask mMsgAddTask = null;
	private View mMsgAddStatusView;
	private TextView mMsgAddStatusMessageView;
	private View mMsgAddFormView;
	private TextView mMsgAddAlertView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		IntentFilter logoutIntentFilter = new IntentFilter();
		logoutIntentFilter.addAction(Constants.LOGOUT_INTENT_FILTER);
	    registerReceiver(mLogoutReceiver, logoutIntentFilter);	
	    
		setContentView(R.layout.activity_message_add);
		mComments = (EditText) findViewById(R.id.message_comment_edit);
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		mSubjectId = (int)bundle.getLong(Constants.SUBJECT_ID);
		
		findViewById(R.id.add_message_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						addMessage();
					}
				});
		

		mMsgAddStatusView = findViewById(R.id.msg_add_status);
		mMsgAddStatusMessageView = (TextView) findViewById(R.id.msg_add_status_message);
		mMsgAddFormView = findViewById(R.id.msg_add_form);
		mMsgAddAlertView = (TextView) findViewById(R.id.msg_add_alert);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_message_add, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
		case R.id.menu_settings:
			Intent menu_intent = new Intent(Constants.SETTING_ACTIVITY);
			//finish();
			startActivity(menu_intent);
			break;
			
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mMsgAddStatusView.setVisibility(View.VISIBLE);
			mMsgAddStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mMsgAddStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mMsgAddFormView.setVisibility(View.VISIBLE);
			mMsgAddFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mMsgAddFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mMsgAddStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mMsgAddFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}
	
	private void addMessage(){
		if (mMsgAddTask != null) {
			return;
		}
		
		boolean cancel = false;
		View focusView = null;
		
		// clear any previous alerts
		mMsgAddAlertView.setText("");		
		String comments = mComments.getText().toString();
		
		if (TextUtils.isEmpty(comments)) {
			mComments.setError(getString(R.string.error_field_required));
			focusView = mComments;
			cancel = true;
		}
		
		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		}
		else {
			mMsgAddStatusMessageView.setText(R.string.msg_add_progress_sending);
			showProgress(true);
			mMsgAddTask = new MessageAddTask();
			mMsgAddTask.execute((Void) null);

		}
		
		
	}
	
	public class MessageAddTask extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			
			boolean success = false;
			Log.d(TAG, "MessageAddTask.doInBackground()");			
			
			String createDate = new SimpleDateFormat("EEE, MMM d yyyy HH:mm").format(new Date());
			Message message = new Message(mSubjectId, mComments.getText().toString(), createDate);
			
			// add to remote db
		    ApiHelper api = ApiHelper.getInstance(getApplicationContext());
		    Message new_message = api.addMessage(message);
		    
		    if(new_message != null){
		    	ContentValues values = new ContentValues();
		    	// from local message
				values.put(InitHubDatabaseManager.MESSAGE_COMMENT, message.getComment());
			    values.put(InitHubDatabaseManager.MESSAGE_CREATE_DATE, message.getCreateDate());
			    // from api return message (new_message)
			    values.put(InitHubDatabaseManager.MESSAGE_SUBJECT_ID, new_message.getRemoteSubjectId());
			    values.put(InitHubDatabaseManager.MESSAGE_REMOTE_ID, new_message.getRemoteId());
			    values.put(InitHubDatabaseManager.MESSAGE_FIRST_NAME, new_message.getFirstName());
			    values.put(InitHubDatabaseManager.MESSAGE_LAST_NAME, new_message.getLastName());
			    
			    getContentResolver().insert(MessageProvider.CONTENT_URI, values);
			    
			    success = true;		    	
		    }
		    
			return success;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mMsgAddTask = null;
			showProgress(false);
			if(success){
				Intent i = new Intent(MESSAGE_ACTIVITY);
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				Bundle b = new Bundle();
				b.putLong(Constants.SUBJECT_ID, mSubjectId);
				i.putExtras(b);
				finish();
				startActivity(i);				
			}
			else {
				
				Toast.makeText(MessageAddActivity.this, "An error occurred sending message", Toast.LENGTH_LONG).show();
				mMsgAddAlertView.setText("An error occurred sending message");
				mComments.requestFocus();
			}
		}

		@Override
		protected void onCancelled() {
			mMsgAddTask = null;
			showProgress(false);
		}
		
	}
	
	private BroadcastReceiver mLogoutReceiver = new BroadcastReceiver() {
		  @Override
		  public void onReceive(Context context, Intent intent) {
			  Log.d("onReceive","Logout in progress");
			    //At this point you should start the login activity and finish this one
			  finish();
		  }
		};

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(mLogoutReceiver);
	}
	
}
