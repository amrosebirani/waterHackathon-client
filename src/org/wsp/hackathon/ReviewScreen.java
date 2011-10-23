/**
 * 
 */
package org.wsp.hackathon;

import org.wsp.hackathon.request.HackathonRequest.HackathonRequestListener;
import org.wsp.hackathon.service.ServiceHelper;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

/**
 * @author neel
 *
 */
public class ReviewScreen extends Activity {
	
	private String mRestroomId;
	private String mUsername;
	
	private Button mSubmitReviewBtn;
	private RatingBar mReviewRatingRB;
	private EditText mReviewET;

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.review_screen);
		
		mRestroomId = getIntent().getStringExtra("restroomId");
		mUsername = getIntent().getStringExtra("username");
		
		mSubmitReviewBtn = (Button) findViewById(R.id.submitReviewBtn);
		mReviewRatingRB = (RatingBar) findViewById(R.id.reviewRatingRB);
		mReviewET = (EditText) findViewById(R.id.reviewET);
		
		setupLiteners();
	}

	private void setupLiteners() {
		mSubmitReviewBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if(mReviewET.getText() == null || (mReviewET.getText() != null && "".equals(mReviewET.getText().toString()))) {
					Toast.makeText(ReviewScreen.this, "review comment is empty", Toast.LENGTH_SHORT).show();
					return;
				}
				
				ServiceHelper.getInstance(getApplicationContext()).submitRating(Float.toString(mReviewRatingRB.getRating()), mRestroomId, new HackathonRequestListener() {
					
					@Override
					public void onRequestComplete(Object obj) {
						Toast.makeText(ReviewScreen.this, "rating submitted", Toast.LENGTH_SHORT).show();
					}
					
					@Override
					public void onError(int errorCode, String errorMessage) {
						Toast.makeText(ReviewScreen.this, "rating submission failed", Toast.LENGTH_SHORT).show();
					}
				});
				
				ServiceHelper.getInstance(getApplicationContext()).uploadComment(mRestroomId, mUsername, mReviewET.getText().toString(), new HackathonRequestListener() {
					
					@Override
					public void onRequestComplete(Object obj) {
						Toast.makeText(ReviewScreen.this, "comment submitted", Toast.LENGTH_SHORT).show();
						
					}
					
					@Override
					public void onError(int errorCode, String errorMessage) {
						Toast.makeText(ReviewScreen.this, "comment submission  failed", Toast.LENGTH_SHORT).show();
						
					}
				});
			}
		});
		
	}

}
