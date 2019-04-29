package com.google.codeu.servlets;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.codeu.data.Datastore;
import com.google.codeu.data.Message;
import com.google.gson.Gson;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

@WebServlet("/conversation")
public class ConversationServlet extends HttpServlet {

    private Datastore datastore;

    @Override
    public void init() {
        datastore = new Datastore();
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        String group = request.getParameter("user");

        response.setContentType("application/json");

        List<Message> messages = datastore.getMessages(group);
        Gson gson = new Gson();
        String json = gson.toJson(messages);

        response.getOutputStream().println(json);

        UserService userService = UserServiceFactory.getUserService();

        boolean isUserLoggedIn = userService.isUserLoggedIn();
        request.setAttribute("isUserLoggedIn", isUserLoggedIn);

        if (userService.isUserLoggedIn()) {
            String username = userService.getCurrentUser().getEmail();
            request.setAttribute("username", username);
        }

        request.getRequestDispatcher("/WEB-INF/messages.jsp").forward(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String group = request.getParameter("user");
        UserService userService = UserServiceFactory.getUserService();
        if (!userService.isUserLoggedIn()) {
            response.sendRedirect("/index.html");
            return;
        }

        String user = userService.getCurrentUser().getEmail();
        //sanitize user data with JSoup
        String userText = Jsoup.clean(request.getParameter("text"), Whitelist.none());

        if (request.getParameter("user") != "") {
            String recipient = request.getParameter("user");

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

            datastore.storeMessage(message);

            response.sendRedirect("/conversation.jsp?user="+ recipient +"&search=" + group);
        }
    }
}