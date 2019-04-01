/*
 * Copyright 2019 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.codeu.servlets;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.codeu.data.Datastore;
import com.google.codeu.data.Message;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.Translate.TranslateOption;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.Document.Type;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.Sentiment;

/** Handles fetching and saving {@link Message} instances. */
@WebServlet("/messages")
public class MessageServlet extends HttpServlet {

  private Datastore datastore;

  @Override
  public void init() {
    datastore = new Datastore();
  }

  /**
   * Responds with a JSON representation of {@link Message} data for a specific
   * user. Responds with an empty array if the user is not provided.
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

    response.setContentType("application/json");

    String user = request.getParameter("user");

    if (user == null || user.equals("")) {
      // Request is invalid, return empty array
      response.getWriter().println("[]");
      return;
    }

    List<Message> messages = datastore.getMessages(user);
    String targetLanguageCode = request.getParameter("language");

    if(targetLanguageCode != null) {
      translateMessages(messages, targetLanguageCode);
    }
    
    Gson gson = new Gson();
    String json = gson.toJson(messages);

    response.getWriter().println(json);
  }

  /** Stores a new {@link Message}. */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

    UserService userService = UserServiceFactory.getUserService();
    if (!userService.isUserLoggedIn()) {
      response.sendRedirect("/index.html");
      return;
    }

    String user = userService.getCurrentUser().getEmail();
    //sanitize user data with JSoup
    String userText = Jsoup.clean(request.getParameter("text"), Whitelist.none());

    if (request.getParameter("recipient") != "") {
      String recipient = request.getParameter("recipient");

      //use regex to replace image URLs with <img> elements
      String regex = "(https?://\\S+\\.(png|jpg|gif))";
      String replacement = "<img src=\"$1\" />";
      String textWithImagesReplaced = userText.replaceAll(regex, replacement);

      //use regex to replace video URLS with <video elements>
      if(userText.contains("https://www.youtube.com/watch?v=")){
        String video_id = userText.split("v=")[1];
        int ampersandPosition = video_id.indexOf('&');
        if(ampersandPosition != -1) {
          video_id = video_id.substring(0, ampersandPosition);
        }
        regex = "^(https?\\:\\/\\/)?(www\\.youtube\\.com|youtu\\.?be)\\/.+$";
        replacement = "<iframe src= http://www.youtube.com/embed/" + video_id + "/>";
      }

      //use regex to replace audio files with <audio> elements
      if(userText.contains("wav") || userText.contains("mp3") || userText.contains("mp4")){
        regex = "(https?://\\S+\\.(wav|mp3|mp4))";
        replacement = "<audio controls> <source src=\"" +userText+ "\"/> </audio>" ;
      }
      
      textWithImagesReplaced = userText.replaceAll(regex, replacement);

      Message message = new Message(user, textWithImagesReplaced, recipient);
      response.sendRedirect("/user-page.html?user=" + recipient);

      datastore.storeMessage(message);

      // Store message's sentiment score in Datastore admin page
    /* String text = Jsoup.clean(request.getParameter("text"), Whitelist.none()); 
    float sentimentScore = getSentimentScore(text);

    Message message2 = new Message(user, text, sentimentScore);
    datastore.storeMessage(message2); */
    }
  }

  private void translateMessages(List<Message> messages, String targetLanguageCode) {
    Translate translate = TranslateOptions.getDefaultInstance().getService();
  
    for(Message message : messages) {
      String originalText = message.getText();
  
      Translation translation =
          translate.translate(originalText, TranslateOption.targetLanguage(targetLanguageCode));
      String translatedText = translation.getTranslatedText();
        
      message.setText(translatedText);
    }    
  }

  private float getSentimentScore(String text) throws IOException {
    Document doc = Document.newBuilder()
        .setContent(text).setType(Type.PLAIN_TEXT).build();

    LanguageServiceClient languageService = LanguageServiceClient.create();
    Sentiment sentiment = languageService.analyzeSentiment(doc).getDocumentSentiment();
    languageService.close();

    return sentiment.getScore();
  }
}
