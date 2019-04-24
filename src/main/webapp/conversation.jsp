<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<script>

    // Fetch messages and add them to the page.
    function fetchMessages() {
        const parameterLanguage = urlParams.get('language');
        let url = '/messages?user=' + parameterUsername;
        if(parameterLanguage) {
            url += '&language=' + parameterLanguage;
        }
        fetch(url)
            .then(response => {
                return response.json();
            })
            .then(messages => {
                const messagesContainer = document.getElementById("message-container");
                if (messages.length == 0) {
                    messagesContainer.innerHTML = "<p>This user has no posts yet.</p>";
                } else {
                    messagesContainer.innerHTML = "";
                }
                messages.forEach(message => {
                    const messageDiv = buildMessageDiv(message);
                    messagesContainer.appendChild(messageDiv);
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
<head>
    <title><%= request.getParameter("user")%> Chatroom</title>
</head>
<body onload="buildUI()">
<div id="message-container">Messages</div>
</body>
</html>
