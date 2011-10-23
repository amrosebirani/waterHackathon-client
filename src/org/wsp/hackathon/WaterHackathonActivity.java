package org.wsp.hackathon;

import org.wsp.hackathon.request.HackathonRequest.HackathonRequestListener;
import org.wsp.hackathon.service.ServiceHelper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

/**
 * @author neel
 *
 */
public class WaterHackathonActivity extends Activity {
    
	private static final String TAG = WaterHackathonActivity.class.getSimpleName();
	
	private EditText mUsernameET;
	private EditText mPasswordET;
	private Button mSignUpBtn;
	private Button mResetPasswordBtn;
	private Button mLoginBtn;
	private Handler mHandler = new Handler();

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);
        
        mUsernameET = (EditText)findViewById(R.id.userNameET);
        mPasswordET = (EditText)findViewById(R.id.passwordET);
        mSignUpBtn = (Button)findViewById(R.id.signUpBtn);
        mResetPasswordBtn = (Button)findViewById(R.id.resetPasswordBtn);
        mLoginBtn = (Button)findViewById(R.id.loginBtn);
        
        setupListeners();
    }

	private void setupListeners() {
		
		mPasswordET.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE || event != null) {
					login();
					return true;
				}
				return false;
			}
		});
		
		mSignUpBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				startActivity(new Intent(WaterHackathonActivity.this, SignupScreen.class));
			}
		});
		
		mResetPasswordBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			}
		});
		
		mLoginBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				login();
			}

		});
	}
	
	private void login() {
		
		if(mUsernameET.getText() == null || (mUsernameET.getText() != null && "".equals(mUsernameET.getText().toString()))) {
			showToast();
			Toast.makeText(WaterHackathonActivity.this, "Username is empty", Toast.LENGTH_SHORT).show();
			return;
		}
		
		if(mPasswordET.getText() == null || (mPasswordET.getText() != null && "".equals(mPasswordET.getText().toString()))) {
			Toast.makeText(WaterHackathonActivity.this, "Password is empty", Toast.LENGTH_SHORT).show();
			return;
		}
		
		/*if(mRePasswordET.getText() == null || (mRePasswordET.getText() != null && "".equals(mRePasswordET.getText().toString()))) {
			Toast.makeText(WaterHackathonActivity.this, "Reenter the password", Toast.LENGTH_SHORT);
			return;
		}
		
		if(mPasswordET.getText() != null && !mPasswordET.getText().toString().equals(mRePasswordET.getText().toString())) {
			Toast.makeText(WaterHackathonActivity.this, "password mismatch", Toast.LENGTH_SHORT);
			return;
		}*/
		
		ServiceHelper.getInstance(getApplicationContext()).login(mUsernameET.getText().toString(), mPasswordET.getText().toString(), new HackathonRequestListener() {
			
			@Override
			public void onRequestComplete(Object obj) {
				Intent intent = new Intent(WaterHackathonActivity.this, HomeScreen.class);
				intent.putExtra("username", mUsernameET.getText().toString());
				startActivity(intent);
			}
			
			@Override
			public void onError(int errorCode, String errorMessage) {
				Toast.makeText(WaterHackathonActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
			}
		});
		
	}

	private void showToast() {
		
	}
}