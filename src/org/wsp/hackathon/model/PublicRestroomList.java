/**
 * 
 */
package org.wsp.hackathon.model;

import java.util.ArrayList;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementArray;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

/**
 * @author neel
 *
 */
@Root(name="publicRestroomList", strict=false)
public class PublicRestroomList {
	
	@ElementList(name="publicRestroom", required=false, inline=true)
	private ArrayList<PublicRestroom> publicRestroom;
	
	public ArrayList<PublicRestroom> getPublicRestroomList() {
		return publicRestroom;
	}

	@Root(name="publicRestroom", strict=false)
	public static class PublicRestroom {
		
		@Element(name="latitude")
		private double latitude;
		
		@Element(name="longitude")
		private double longitude;
		
		@Element(name="title", required=false)
		private String title;
		
		@Element(name="description")
		private String description;
		
		@Element(name="restroomId")
		private String restroomId;
		
		@Element(name="rating", required=false)
		private String rating;
		
		@Element(name="imageUrls", required=false)
		private ImageUrls imageUrls;
		
		@Element(name="distance")
		private String distance;
		
		@Element(name="duration")
		private String duration;
		
		/*@Element(name="commentlist", required=false)
		private CommentList commentlist;*/
		
		@ElementArray(entry="comment", required=false)
		private String[] commentlist;
		
		public double getLatitude() {
			return latitude;
		}
		public double getLongitude() {
			return longitude;
		}
		public String getTitle() {
			return title;
		}
		public String getDescription() {
			return description;
		}
		public String getRestroomId() {
			return restroomId;
		}
		public String getRating() {
			return rating;
		}
		public ImageUrls getImageUrls() {
			return imageUrls;
		}
		public String getDistance() {
			return distance;
		}
		public String getDuration() {
			return duration;
		}
		/*public CommentList getCommentlist() {
			return commentlist;
		}*/
		public String[] getCommentlist() {
			return commentlist;
		}
		
	}
	
	@Root(name="imageUrls", strict=false)
	public static class ImageUrls {
		
		@ElementList(name="image", required=false, inline=true)
		private ArrayList<Image> imageList;

		public ArrayList<Image> getImageList() {
			return imageList;
		}
	}
	
	@Root(name="image", strict=false)
	public static class Image {
		
		@Element(name="imageurl", required=false)
		private String imageurl;
		
		@Element(name="timestamp", required=false)
		private String timestamp;
		
		public String getImageurl() {
			return imageurl;
		}
		public String getTimestamp() {
			return timestamp;
		}
	}
	
	@Root(name="commentlist", strict=false)
	public static class CommentList {
		
		@ElementArray(name="comment", required=false)
		private String[] comments;

		public String[] getComments() {
			return comments;
		}
	}
	
	
}
