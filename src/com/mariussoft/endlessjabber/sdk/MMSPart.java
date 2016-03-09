package com.mariussoft.endlessjabber.sdk;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * 
 * An MMS attachment
 * 
 */
public class MMSPart implements Parcelable {
	/**
	 * The name of the MMS attachment
	 */
	public String Name = "";
	/**
	 * The MimeType of the MMS attachment
	 */
	public String MimeType = "";
	/**
	 * The byte[] of the MMS attachment
	 */
	public byte[] Data = new byte[0];
	
	/**
	 * The content URI of the attachment
	 */
	public String URI;

	public MMSPart() {   
		
	}
	
	public MMSPart(Parcel parcel) {
		Name = parcel.readString();
		MimeType = parcel.readString();		
		Data = new byte[parcel.readInt()];
		parcel.readByteArray(Data);  
		URI = parcel.readString();			
	}
	
	public void ParseURI(Context ctx)
	{

		if(URI != null && URI.length() > 0)
		{
			try {
				InputStream iStream =  ctx.getContentResolver().openInputStream(Uri.parse(URI));
				Data = getBytes(iStream);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int arg1) {
		dest.writeString(Name);
		dest.writeString(MimeType);
		dest.writeInt(Data.length); 
		dest.writeByteArray(Data);
		dest.writeString(URI);
	}

	public static final Parcelable.Creator<MMSPart> CREATOR = new Parcelable.Creator<MMSPart>() {
		public MMSPart createFromParcel(Parcel in) {
			return new MMSPart(in);
		}

		public MMSPart[] newArray(int size) {
			return new MMSPart[size];
		}
	};


	 /**
	 * get bytes from input stream.
	 *
	 * @param inputStream inputStream.
	 * @return byte array read from the inputStream.
	 * @throws IOException
	 */
	private static byte[] getBytes(InputStream inputStream) throws IOException {

	    byte[] bytesResult = null;
	    ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
	    int bufferSize = 1024;
	    byte[] buffer = new byte[bufferSize];
	    try {
	        int len;
	        while ((len = inputStream.read(buffer)) != -1) {
	            byteBuffer.write(buffer, 0, len);
	        }
	        bytesResult = byteBuffer.toByteArray();
	    } finally {
	        // close the stream
	        try{ byteBuffer.close(); } catch (IOException ignored){ /* do nothing */ }
	    }
	    return bytesResult;
	}
}