'use strict';

var chatPage = document.querySelector('#chat-page');
var messageForm = document.querySelector('#messageForm');
var messageInput = document.querySelector('#message');
var messageArea = document.querySelector('#messageArea');
var connectingElement = document.querySelector('.connecting');

var stompClient = null;
var currentSubscription;
var username = null;
var title = Cookies.get("courseTitle");
var roomId = Cookies.get("chatId");
var topic = null;

function connect(event) {
  chatPage.classList.remove('hidden');
  var socket = new SockJS('/websocket');
  stompClient = Stomp.over(socket);

  stompClient.connect({}, onConnected, onError);
}

function enterRoom() {
  topic = `/ws/chat/${roomId}`;

  if (currentSubscription) {
    currentSubscription.unsubscribe();
  }

  stompClient.subscribe("/user/topic/private",
                            onSpecificMessageReceived);

  currentSubscription = stompClient.subscribe(`/topic/${roomId}`,
                            onMessageReceived);
}

function onConnected() {
  enterRoom();
  stompClient.send("/ws/private", {}, JSON.stringify({courseId: roomId}));
}

function onError(error) {
  connectingElement.textContent = 'Could not connect to WebSocket server. Please refresh this page to try again!';
  connectingElement.style.color = 'red';
}

function sendMessage(event) {
  var messageContent = messageInput.value.trim();
  if (messageContent && stompClient) {
    var chatMessage = {
      sender: Cookies.get("username"),
      content: messageInput.value,
    };
    stompClient.send(`${topic}/sendMessage`, {}, JSON.stringify(chatMessage));
  }
  messageInput.value = '';
  event.preventDefault();
}

function onSpecificMessageReceived(payload) {
  var messageList = JSON.parse(payload.body);
  for (let message of messageList) {
    var messageElement = document.createElement('li');
    messageElement.classList.add('chat-message');

    var usernameElement = document.createElement('span');
    var usernameText = document.createTextNode(message.sender);
    usernameElement.appendChild(usernameText);
    messageElement.appendChild(usernameElement);

    var textElement = document.createElement('p');
    var messageText = document.createTextNode(message.content);
    textElement.appendChild(messageText);

    messageElement.appendChild(textElement);

    messageArea.appendChild(messageElement);
  }
}

function onMessageReceived(payload) {
  let message = JSON.parse(payload.body);

  let messageElement = document.createElement('li');

  messageElement.classList.add('chat-message');

  let usernameElement = document.createElement('span');
  let usernameText = document.createTextNode(message.sender);
  let textElement = document.createElement('p');
  let messageText = document.createTextNode(message.content);


  usernameElement.appendChild(usernameText);
  messageElement.appendChild(usernameElement);

  textElement.appendChild(messageText);

  messageElement.appendChild(textElement);

  messageArea.appendChild(messageElement);
  messageArea.scrollTop = messageArea.scrollHeight;
}

$(document).ready(function() {
  if (!roomId || !Cookies.get("username") || !Cookies.get("token")) {
    window.location = 'https://app:8888/main';
  }
  document.getElementById("course_title").innerText = title;
  connect();
  messageForm.addEventListener('submit', sendMessage, true);
});
