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



	private Document getDocument(String url) throws IOException {
		return Jsoup.connect(url).data("query", "Java").userAgent("Mozilla")
				.cookie("auth", "token").timeout(3000).post();
	}

}
