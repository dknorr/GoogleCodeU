
const urlParams = new URLSearchParams(window.location.search)

// Fetch messages and add them to the page.
function fetchMessages(){
    const parameterLanguage = urlParams.get('language');
    let url = '/feed?';

    if(parameterLanguage) {
        url += 'language=' + parameterLanguage;
    }

    fetch(url)
      .then((response) => {
        return response.json();
      })
      .then((messages) => {
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

  function buildLanguageLinks(){
    let url = '/feed.html?'
    const languagesListElement  = document.getElementById("languages");
    languagesListElement.appendChild(createLink(
        url + 'language=en', 'English'));
    languagesListElement.appendChild(createLink(
        url + 'language=zh', 'Chinese'));
    languagesListElement.appendChild(createLink(
        url + 'language=hi', 'Hindi'));
    languagesListElement.appendChild(createLink(
        url + 'language=es', 'Spanish'));
    languagesListElement.appendChild(createLink(
        url + 'language=ar', 'Arabic'));
        
    var elements = languagesListElement.childNodes;
    for (var i = 1;i < elements.length;i++){
      var current = elements[i];
      current.classList.add("dropdown-item");
    }
  }
  
  // Fetch data and populate the UI of the page.
  function buildUI(){
    buildLanguageLinks();
    fetchMessages();
  }