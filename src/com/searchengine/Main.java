package com.searchengine;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Main {

	public static void main(String[] args) throws IOException {
	        System.out.println("welcome!, what do you want to search for");
	        Scanner query = new Scanner(System.in);
	        String answer = query.nextLine();
	        String searchURL2 = "https://www.google.com/search?q=" + answer + "&num=5&sxsrf=ALiCzsYS3M8ebrkFl7yG5UK1a-hxp7jdlg%3A1667883038161&source=hp&ei=HuBpY_yRB8n8sAfvhY_4CQ&iflsig=AJiK0e8AAAAAY2nuLhApbQHNWJwJ-e_RNiNJDEKclL_O&ved=0ahUKEwj8mI7A5J37AhVJPuwKHe_CA58Q4dUDCAg&uact=5&oq=dancing&gs_lcp=Cgdnd3Mtd2l6EAMyCAgAEIAEELEDMggIABCxAxCDATIICAAQsQMQgwEyDgguEIAEELEDEMcBEK8BMgsILhCABBCxAxDUAjIICAAQgAQQsQMyCAguELEDEIMBMggIABCABBCxAzIICAAQgAQQsQMyCwgAEIAEELEDEIMBOgQIIxAnOggILhCxAxCABDoICC4QgAQQsQM6CwguEIAEELEDEIMBOgUIABCABDoOCC4QsQMQgwEQxwEQ0QM6CwgAEIAEELEDEMkDOgUILhCABFAAWLgHYPYxaAFwAHgAgAGVAogBqgiSAQUwLjIuM5gBAKABAQ&sclient=gws-wiz";

	        try {
	        	
//	        	      Parse an html document from the  searchURL2 	
		        Document doc = Jsoup.connect(searchURL2).userAgent("Brave").get();
		        
		     // Select all span elements that contain links
		        Elements spanElements = doc.select("span a[href]");
		        
		     // Create a list to store extracted links
		        List<String> links = new ArrayList<>();
		        
		     // Extract the href attribute from each selected span element
		        for (Element spanElement : spanElements) {
	
		            links.add(spanElement.attr("href"));
		            
		        }
		        // Number of threads to use
	            int numThreads = 5;
	            
	         // Create a thread pool with a fixed number of threads
	            ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
	
	         // Create a list to store LinkData objects
	            List<LinkData> linkDataList = new ArrayList<>();
	            
	            // Process each link concurrently
	            for (String link : links) {
	                // Submit a task to the thread pool to process each link
	                executorService.submit(() -> processLink(link, answer, linkDataList));
	            }
	            
	            // Shut down the thread pool
	            executorService.shutdown();
	            
	         // Wait for all submitted tasks to complete

	            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
	            
	            // Now you have processed all links concurrently, and you can sort data based on relevance.
	            // Sort the linkDataList based on the relevance score in descending order

	            linkDataList.sort(Comparator.comparingInt(LinkData::getRelevanceScore).reversed());
	            
	         // Print the sorted results
	            for (LinkData linkData : linkDataList) {
	                System.out.println("Title: " + linkData.title);
	                System.out.println("Relevant Text: " + linkData.text);
	                System.out.println("Link: " + linkData.link);
	                System.out.println("Relevance Score: " + linkData.relevanceScore);
	                System.out.println("------");
	            }
	
	        }catch(IOException | InterruptedException e) {
	            // Handle exceptions, e.g., print the stack trace
	            e.printStackTrace();
	        }
	    }


// Method to process each link
private static void processLink(String link, String keyword, List<LinkData> linkDataList) {
    try {
        // Connect to the specified link and retrieve the HTML document
        Document linkDocument = Jsoup.connect(link).get();

        // Extract title
        String title = linkDocument.title();

        // Extract relevant text
        Elements paragraphs = linkDocument.select("span");
        StringBuilder relevantText = new StringBuilder();
        for (Element paragraph : paragraphs) {
            relevantText.append(paragraph.text()).append(" ");
        }

        // Calculate keyword occurrences in title and text
        int titleOccurrences = countKeywordOccurrences(title, keyword);
        int textOccurrences = countKeywordOccurrences(relevantText.toString(), keyword);

        // Calculate overall relevance score (you can customize the scoring logic)
        int relevanceScore = titleOccurrences + textOccurrences;

        // Add data to the shared list in a thread-safe manner
        synchronized (linkDataList) {
            linkDataList.add(new LinkData(title, relevantText.toString(), link, relevanceScore));
        }

    } catch (IOException e) {
        // Handle exceptions, e.g., print the stack trace
        e.printStackTrace();
    }
}

// Method to count occurrences of the keyword in a given text
private static int countKeywordOccurrences(String text, String keyword) {
    int count = 0;
    int index = 0;
    // Loop to find occurrences of the keyword in the text
    while ((index = text.toLowerCase().indexOf(keyword.toLowerCase(), index)) != -1) {
        count++;
        index += keyword.length();
    }
    return count;
}
}
