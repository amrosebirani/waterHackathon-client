/**
 * 
 */
package org.wsp.hackathon;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

/**
 * @author neel
 *
 */
public class LocationDetailsScreen extends Activity {
	
	private double mSelfLatitude;
	private double mSelfLongitude;
	private double mLatitude;
	private double mLongitude;
	private String mName;
	private String mDescription;
	private String[] mComments = new String[]{};
	private String mRating;
	private String mRestroomId;
	private String mUsername;
	
	private Gallery mPictureGalleryG;
	private RatingBar mRatingRB;
	private ListView mCommentListLV;
	private CommentListAdapter mCommentListAdapter;
	private PictureAdapter mPictureAdapter;
	private Button mGetDirectionBtn;
	private Button mWriteReviewBtn;

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.location_details_screen);
		
		mSelfLatitude = getIntent().getDoubleExtra("selfLatitude", 0.0);
		mSelfLongitude = getIntent().getDoubleExtra("selfLongitude", 0.0);
		mLatitude = getIntent().getDoubleExtra("latitude", 0.0);
		mLongitude = getIntent().getDoubleExtra("longitude", 0.0);
		mName = getIntent().getStringExtra("name");
		mDescription = getIntent().getStringExtra("description");
		if(getIntent().getStringArrayExtra("comments") != null) {
			mComments = getIntent().getStringArrayExtra("comments");
		}
		mRating = getIntent().getStringExtra("rating");
		mRestroomId = getIntent().getStringExtra("restroomId");
		mUsername = getIntent().getStringExtra("username");
		
		mPictureGalleryG = (Gallery) findViewById(R.id.pictureG);
		mPictureAdapter = new PictureAdapter(LocationDetailsScreen.this);
		mPictureGalleryG.setAdapter(mPictureAdapter);
		mRatingRB = (RatingBar) findViewById(R.id.ratingRB);
		mRatingRB.setRating(Float.parseFloat(mRating));
		mRatingRB.setEnabled(false);
		mCommentListLV = (ListView) findViewById(R.id.commentListLV);
		mCommentListAdapter = new CommentListAdapter(LocationDetailsScreen.this);
		mCommentListLV.setAdapter(mCommentListAdapter);
		mCommentListAdapter.notifyDataSetChanged();
		mGetDirectionBtn = (Button) findViewById(R.id.getDirectionBtn);
		mWriteReviewBtn = (Button) findViewById(R.id.writeReviewBtn);
		
		setupListeners();
		
	}
	
	private void setupListeners() {
		mGetDirectionBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LocationDetailsScreen.this, DirectionScreen.class); 
				intent.putExtra("srcLatitude", mSelfLatitude);
				intent.putExtra("srcLongitude", mSelfLongitude);
				intent.putExtra("dstLatitude", mLatitude);
				intent.putExtra("dstLongitude", mLongitude);
				startActivity(intent);
			}
		});
		
		mWriteReviewBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LocationDetailsScreen.this, ReviewScreen.class);
				intent.putExtra("username", mUsername);
				intent.putExtra("restroomId", mRestroomId);
				startActivity(intent);
			}
		});
	}

	private class CommentListAdapter extends BaseAdapter {
		
		private Context mContext;
		private LayoutInflater mInflater;

		public CommentListAdapter(Context cont) {
			mContext = cont;
			mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		@Override
		public int getCount() {
			return mComments.length;
		}

		@Override
		public String getItem(int position) {
			return mComments[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			convertView = mInflater.inflate(android.R.layout.simple_list_item_1, null);
			((TextView)convertView).setText(getItem(position));
			return convertView;
		}
		
	}
	
	private class PictureAdapter extends BaseAdapter {
		
		private Context mContext;
		private LayoutInflater mInflater;
		
		public PictureAdapter(Context cont) {
			mContext = cont;
			mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		@Override
		public int getCount() {
			return 2;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			convertView = mInflater.inflate(R.layout.image_gallery_item, null);
			return convertView;
		}
		
	}

}
