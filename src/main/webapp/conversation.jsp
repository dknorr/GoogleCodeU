<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <title>Conversation with group: <%= request.getParameter("user")%> </title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css" integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T" crossorigin="anonymous">
    <link rel="stylesheet" href="/css/user-page.css" />
    <script src="/js/navigation-loader.js"></script>
</head>
<script>
    // Fetch messages and add them to the page.
    function fetchMessages(){
        const url = '/conversation';
        fetch(url).then((response) => {
            return response.json();
        }).then((messages) => {
            const messageContainer = document.getElementById('message-container');
            if(messages.length == 0){
                messageContainer.innerHTML = '<p>There are no posts yet.</p>';
            }
            else{
                messageContainer.innerHTML = '';
            }
            messages.forEach((message) => {
                const messageDiv = buildMessageDiv(message);
                messageContainer.appendChild(messageDiv);
            });
        });
    }

    function buildMessageDiv(message){
        const usernameDiv = document.createElement('div');
        usernameDiv.classList.add("left-align");
        usernameDiv.appendChild(document.createTextNode(message.user));

        const timeDiv = document.createElement('div');
        timeDiv.classList.add('right-align');
        timeDiv.appendChild(document.createTextNode(new Date(message.timestamp)));

        const headerDiv = document.createElement('div');
        headerDiv.classList.add('message-header');
        headerDiv.appendChild(usernameDiv);
        headerDiv.appendChild(timeDiv);

        const bodyDiv = document.createElement('div');
        bodyDiv.classList.add('message-body');
        bodyDiv.appendChild(document.createTextNode(message.text));

        const messageDiv = document.createElement('div');
        messageDiv.classList.add("message-div");
        messageDiv.appendChild(headerDiv);
        messageDiv.appendChild(bodyDiv);

        return messageDiv;
    }

    // Fetch data and populate the UI of the page.
    function buildUI(){
        fetchMessages();
    }
</script>
<body onload="buildUI()">
<nav class="navbar navbar-expand-lg navbar-dark bg-dark">
    <a class="navbar-brand" href="#">Rumor VonFlip</a>
    <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
        <span class="navbar-toggler-icon"></span>
    </button>

    <div class="collapse navbar-collapse" id="navbarSupportedContent">
        <ul class="navbar-nav mr-auto">
            <li class="nav-item active">
                <a class="nav-link" href="/">Home<span class="sr-only">(current)</span></a>
            </li>
            <li class="nav-item active">
                <a class="nav-link" href="/feed.html">Feed<span class="sr-only">(current)</span></a>
            </li>
            <li class="nav-item active">
                <a class="nav-link" href="/map.html">Map<span class="sr-only">(current)</span></a>
            </li>
            <li class="nav-item active">
                <a class="nav-link" href="/stats.html">Stats<span class="sr-only">(current)</span></a>
            </li>
            <li class="nav-item active">
                <a class="nav-link" href="/messages.jsp">Groups<span class="sr-only">(current)</span></a>
            </li>
        </ul>
    </div>
</nav>
<div class="container" style = "text-align: center">
    <div class="row justify-content-center">
        <h1>Messages with group: <%= request.getParameter("user")%></h1>
    </div>
    <hr/>
    <form id="message-form" action="/conversation" method="POST">
        <div class="form-group">
            <label for="comment">Enter a new message:</label>
            <textarea name="text" class="form-control" rows="5" id="comment"></textarea>
        </div>
        <input class="btn btn-primary btn-lg btn-block" type="submit" value="Submit"/>
    </form>
    <div id="message-container">Loading...</div>
</div>
</body>
</html>
