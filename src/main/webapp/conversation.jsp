<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <title>Conversation with <%= request.getParameter("user")%> </title>
    <script src="/js/user-page-loader.js"></script>
</head>
<body onLoad="buildConvoUI(<%= request.getParameter("user")%>)">
<nav>
    <ul id="navigation">
        <li><a href="/">Home</a></li>
    </ul>
</nav>

<form id="message-form" action="/conversation" method="POST" class="hidden">
    Enter a new message:
    <br />
    <textarea name="text" id="message-input"></textarea>
    <br />
    <input type="submit" value="Submit" />
</form>
<hr />

<div id="conversation-container">Loading...</div>
</body>
</html>
