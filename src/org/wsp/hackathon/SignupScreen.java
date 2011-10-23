/**
 * 
 */
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
import android.widget.Toast;
import android.widget.TextView.OnEditorActionListener;

/**
 * @author neel
 *
 */
public class SignupScreen extends Activity {
	
	private static final String TAG = SignupScreen.class.getSimpleName();
	
	private EditText mUsernameET;
	private EditText mPasswordET;
	private EditText mRePasswordET;
	private Button mSignupBtn;
	private Handler mHandler = new Handler();

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.signup_screen);
		
		mUsernameET = (EditText)findViewById(R.id.userNameET);
        mPasswordET = (EditText)findViewById(R.id.passwordET);
        mRePasswordET = (EditText)findViewById(R.id.rePasswordET);
        mSignupBtn = (Button)findViewById(R.id.signupBtn);
        
        setupListeners();
	}
	
	private void setupListeners() {
		
		mRePasswordET.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE || event != null) {
					signUp();
					return true;
				}
				return false;
			}

		});
		
		mSignupBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				signUp();
			}

		});
	}
	
	private void signUp() {
		if(mUsernameET.getText() == null || (mUsernameET.getText() != null && "".equals(mUsernameET.getText().toString()))) {
			Toast.makeText(SignupScreen.this, "Username is empty", Toast.LENGTH_SHORT).show();
			return;
		}
		
		if(mPasswordET.getText() == null || (mPasswordET.getText() != null && "".equals(mPasswordET.getText().toString()))) {
			Toast.makeText(SignupScreen.this, "Password is empty", Toast.LENGTH_SHORT).show();
			return;
		}
		
		if(mRePasswordET.getText() == null || (mRePasswordET.getText() != null && "".equals(mRePasswordET.getText().toString()))) {
			Toast.makeText(SignupScreen.this, "Reenter the password", Toast.LENGTH_SHORT).show();
			return;
		}
		
		if(mPasswordET.getText() != null && !mPasswordET.getText().toString().equals(mRePasswordET.getText().toString())) {
			Toast.makeText(SignupScreen.this, "password mismatch", Toast.LENGTH_SHORT).show();
			return;
		}
		
		ServiceHelper.getInstance(getApplicationContext()).signUp(mUsernameET.getText().toString(), mPasswordET.getText().toString(), new HackathonRequestListener() {
			
			@Override
			public void onRequestComplete(Object obj) {
				startActivity(new Intent(SignupScreen.this, HomeScreen.class));
			}
			
			@Override
			public void onError(int errorCode, String errorMessage) {
				Toast.makeText(SignupScreen.this, "Signup failed", Toast.LENGTH_SHORT).show();
			}
		});
	}

}
