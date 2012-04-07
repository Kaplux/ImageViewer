package fr.mildlyusefulsoftware.imageviewer.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import android.content.Context;
import android.util.Log;

public class PictureProvider {

	private static String TAG = "cutekitty";

	public PictureProvider(Context context) {
		super();
	}

	public List<Picture> getPicturesFromPage(int pageNumber) throws IOException {
		Log.d(TAG, "loading page " + pageNumber);

		List<Picture> pictures = new ArrayList<Picture>();
		Document doc = getDocument("http://icanhascheezburger.com/page/"
				+ pageNumber);
		for (Element elt : doc.select("div.post")) {
			Element imageElt = elt.select("img.event-item-lol-image").first();
			if (imageElt != null) {
				int pictureId = Integer.valueOf(elt.attr("id").substring(
						elt.attr("id").lastIndexOf("-") + 1));
				String imageURL = imageElt.attr("src");
				if (!imageURL.endsWith(".gif")) {
					try{
					pictures.add(new Picture(pictureId, imageURL, Picture
							.getPictureThumbnail(imageURL)));
					}catch (Exception e){
						Log.e(TAG,e.getMessage(),e);
					}
				}
			}

		}
		return pictures;
	}

	private Document getDocument(String url) throws IOException {
		return Jsoup.connect(url).data("query", "Java").userAgent("Mozilla")
				.cookie("auth", "token").timeout(3000).post();
	}

}
