package com.searchengine;
import java.util.ArrayList;
import java.util.List;


// Class to store information about each link
public class LinkData {
	String title;
    String text;
    String link;
    int relevanceScore;
	public LinkData(String title, String text, String link, int relevanceScore) {
		super();
		this.title = title;
		this.text = text;
		this.link = link;
		this.relevanceScore = relevanceScore;
	}
	//Getter for relevance score
	public int getRelevanceScore() {
		return relevanceScore;
	}

    
    
    
}

