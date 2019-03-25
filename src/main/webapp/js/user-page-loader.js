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

// Get ?user=XYZ parameter value
const urlParams = new URLSearchParams(window.location.search);
const parameterUsername = urlParams.get("user");

// URL must include ?user=XYZ parameter. If not, redirect to homepage.
if (!parameterUsername) {
  window.location.replace("/");
}

/** Sets the page title based on the URL parameter username. */
function setPageTitle() {
  document.getElementById("page-title").innerText = parameterUsername;
  document.title = parameterUsername + " - User Page";
}

/**
 * Shows the message form if the user is logged in and viewing their own page.
 */

function showMessageForm() {
  //Also will show about me if on one's own page
  fetch("/login-status")
    .then(response => {
      return response.json();
    })
    .then(loginStatus => {
      if (loginStatus.isLoggedIn) {
        const messageForm = document.getElementById("message-form");
        messageForm.action = "/messages?recipient=" + parameterUsername;
        messageForm.classList.remove("hidden");
      }
      if (loginStatus.isLoggedIn && loginStatus.username == parameterUsername) {
        document.getElementById("about-me-form").classList.remove("hidden");
      }
    });
}

/** Fetches messages and add them to the page. */
function fetchMessages() {
  const url = "/messages?user=" + parameterUsername;
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

function fetchConversation(recipient) {
  const url = "/conversation?user=" + parameterUsername + "?recipient=" + recipient;
  fetch(url)
      .then(response => {
        return response.json();
      })
      .then(conversation => {
        const conversationContainer = document.getElementById("conversation-container");
        if(conversation.length == 0) {
          conversationContainer.innerHTML = "<p>No messages with thus user yet.</p>";
        } else {
          conversationContainer.innerHTML = "";
        }
        conversation.forEach(message => {
          const messageDiv = buildMessageDiv(message);
          conversationContainer.appendChild(messageDiv);
        })
      })
}

/**
 * Builds an element that displays the message.
 * @param {Message} message
 * @return {Element}
 */
function buildMessageDiv(message) {
  const headerDiv = document.createElement("div");
  headerDiv.classList.add("message-header");
  headerDiv.appendChild(
    document.createTextNode(message.user + " - " + new Date(message.timestamp))
  );

  const bodyDiv = document.createElement("div");
  bodyDiv.classList.add("message-body");
  bodyDiv.innerHTML = message.text;

  const messageDiv = document.createElement("div");
  messageDiv.classList.add("message-div");
  messageDiv.appendChild(headerDiv);
  messageDiv.appendChild(bodyDiv);

  return messageDiv;
}

/** Fetches user's About Me data and adds it to page. */
function fetchAboutMe() {
  const url = "/about?user=" + parameterUsername;
  fetch(url)
    .then(response => {
      return response.text();
    })
    .then(aboutMe => {
      const aboutMeContainer = document.getElementById("about-me-container");
      if (aboutMe == "") {
        aboutMe = "This user has not entered any information yet.";
      }

      aboutMeContainer.innerHTML = aboutMe;
    });
}

/** Fetches data and populates the UI of the page. */
function buildUI() {
  setPageTitle();
  showMessageForm();
  fetchMessages();
  fetchAboutMe();
}

function buildConvoUI(recipient) {
  showMessageForm();
  fetchConversation(recipient);
}
