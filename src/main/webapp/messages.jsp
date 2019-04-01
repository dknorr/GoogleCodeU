<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Conversations</title>
    <link rel="stylesheet" href="/css/main.css">
</head>
<body>
<%
    UserService userService = UserServiceFactory.getUserService();
    if (userService.isUserLoggedIn()) {
        String username = userService.getCurrentUser().getEmail();
%>
<form action="conversation.jsp" method="GET">
<p><%= username %> is logged in</p>
<p>Search for your conversation with a user: </p> <input type="text" name="user">
<input type="submit" name="search" value="Search">
</form>
<% } else {   %>
<p>Must be logged in to view conversations</p>
<% } %>
</body>
</html>
