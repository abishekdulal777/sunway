"use strict";

window.onload = function() {
  showSuggesFriend();
  showfriendlist();
  createChatGroup();
  init();
};

function showSuggesFriend() {
  let suggestfriendDiv = document.getElementById("suggestfriend");
  suggestfriendDiv.style.border = "1px solid black";

  let rebutton = document.createElement("button");
  suggestfriendDiv.append(rebutton);
  rebutton.textContent = "refresh";

  rebutton.addEventListener("click", () => {
    let suggestcontentdiv = document.getElementById("suggestlistcontent");
    suggestcontentdiv.remove();
    fetchSuggestfriend(suggestfriendDiv);
  });

  fetchSuggestfriend(suggestfriendDiv);
}

async function addfriend(friend_id) {
  let formdata = new FormData();
  formdata.append("friend_id", friend_id);

  let response = await fetch("/friendadd", {
    method: "POST",
    body: formdata
  });
  response = await response.json();
  return response;
}

function showfriendlist() {
  //this is for friend list div it is outer div not the list itself
  let friendlsitDiv = document.getElementById("friendlist");
  friendlsitDiv.style.border = "1px solid black";

  //created the button to refresh the page
  let rebutton = document.createElement("button");
  friendlsitDiv.append(rebutton);
  rebutton.textContent = "refresh";

  //event to refresh the friendlist
  rebutton.addEventListener("click", () => {
    let friendscontentdiv = document.getElementById("friendlistcontent");
    friendscontentdiv.remove();
    fetchfriendlist(friendlsitDiv);
  });

  //method to fetch friendlist content
  fetchfriendlist(friendlsitDiv);
}

function fetchfriendlist(friendlsitDiv) {
  // inner container div for friend list itself
  let friendscontentdiv = document.createElement("div");
  friendscontentdiv.id = "friendlistcontent";

  //fetching  friend list for div
  fetch("/friendlist")
    .then(res => res.json())
    .then(res => {
      if (res.response) {
        res.friends.map(item => {
          //friend list element example  [abishek@gmail.com   *]
          let div = document.createElement("div");
          div.id = `friendlist:${item.id}`;
          div.innerHTML = `<span style ="width:500px">${item.email}</span>`;

          //div color for  active
          let activediv = document.createElement("div");
          activediv.style.display = "inline-block";
          activediv.style.width = "10px";
          activediv.style.height = "10px";
          activediv.style.backgroundColor = "grey";
          activediv.style.marginLeft = "150px";
          activediv.style.border = "1px solid color";
          activediv.style.borderRadius = "10px";

          //clicking this make chat box appear
          div.addEventListener("click", event => {
            createSingleChat(item.id, item.email);
          });

          div.append(activediv);
          friendscontentdiv.append(div);
        });
      } else {
        div.innerHTML = "<span>network error</span>";
      }
    });
  friendlsitDiv.append(friendscontentdiv);
}

function fetchSuggestfriend(suggestfriendDiv) {
  let suggestcontentdiv = document.createElement("div");
  suggestcontentdiv.id = "suggestlistcontent";
  fetch("/suggestfriend")
    .then(res => res.json())
    .then(res => {
      if (res.response) {
        res.friends.map(item => {
          let div = document.createElement("div");
          div.dataset.suggestid = item.id;
          div.innerHTML = `<span>${item.email}</span>`;
          let button = document.createElement("button");
          button.textContent = "Add friend";

          div.addEventListener("click", event => {
            if (event.target.nodeName == "BUTTON") {
              (async () => {
                let res = await addfriend(
                  event.currentTarget.dataset.suggestid
                );
                if (res.response) {
                  div.remove();
                } else {
                  alert("network error something is wrong");
                }
              })();
            }
          });

          div.append(button);
          suggestcontentdiv.append(div);
        });
      } else {
        div.innerHTML = "<span>network error</span>";
      }
    });
  suggestfriendDiv.append(suggestcontentdiv);
}

//fetches the conversation id for single chat
async function createSingleChat(friend_id, title) {
  let formdata = new FormData();
  formdata.append("friend_id", friend_id);
  formdata.append("title", title);
  let res = await fetch("/createConversation", {
    method: "POST",
    body: formdata
  });
  res = await res.json();
  if (res.response) {
    createSingleChatBox(res.id, title);
    let id = document.getElementById("useriddiv").dataset.id;
    let formdata = new FormData();
    formdata.append("convid", res.id);
    await fetch("/api/chatserver/singlechatbox/" + id, {
      method: "POST",
      body: formdata
    });
  }
}
//creates the ui
function createSingleChatBox(id, title) {
  let div = document.getElementById("conv:" + id);
  if (!div) {
    div = document.createElement("div");
    div.style.display = "inline-block";
    div.style.width = "250px";
    div.style.border = "1px solid black";
    div.style.margin = "10px";

    div.id = "conv:" + id;
    div.dataset.id = id;

    let cancelbtn = document.createElement("button");
    cancelbtn.textContent = "X";
    cancelbtn.addEventListener("click", event => {
      div.remove();
    });
    cancelbtn.style.cssFloat = "right";

    //conv header for chatbox
    let convheader = document.createElement("div");
    convheader.style.textAlign = "center";
    convheader.textContent = title;
    convheader.style.color = "white";
    convheader.style.backgroundColor = "purple";
    convheader.append(cancelbtn);

    //conv content for chatbox
    let chatcontentbox = document.createElement("div");
    chatcontentbox.style.height = "330px";
    chatcontentbox.style.backgroundColor = "grey";

    //conv  input  for chatbox
    let chatinputbox = document.createElement("div");
    chatinputbox.style.height = "70px";
    chatinputbox.style.backgroundColor = "orange";

    //conv inputbox and send button
    let textinput = document.createElement("textarea");
    textinput.cols = 17;
    textinput.rows = 2;
    textinput.textContent = "";

    textinput.style.margin = "10px 0px 0px 10px";
    textinput.style.display = "inline-block";
    chatinputbox.append(textinput);

    //conv send button
    let sendbutton = document.createElement("button");
    sendbutton.style.width = "60px";
    sendbutton.style.margin = "10px";
    sendbutton.style.height = "50px";
    sendbutton.style.backgroundColor = "green";
    sendbutton.style.display = "inline-block";
    sendbutton.style.position = "relative";
    sendbutton.style.bottom = "16px";
    sendbutton.textContent = "Send";
    chatinputbox.append(sendbutton);

    div.append(convheader);
    div.append(chatcontentbox);
    div.append(chatinputbox);

    document.body.append(div);
  }
}

async function createChatGroup() {
  let chatgroupdiv = document.getElementById("grouplist");
  chatgroupdiv.textContent = "Grouplist";
  let addbutton = document.createElement("button");
  addbutton.textContent = "+";
  addbutton.addEventListener("click", event => {
    createfriendAddModel();
  });
  chatgroupdiv.append(addbutton);
  let res = await fetch("/conversationlist");
  res = await res.json();
  if (res.response) {
    res.convlist.map(item => {
      let ldiv = document.createElement("div");
      ldiv.dataset.convid = item.conv_id;
      ldiv.innerHTML = `<span style ="width:500px">${item.title}</span>`;
      ldiv.addEventListener("click", event => {
        createSingleChatBox(item.conv_id, item.title);
      });
      chatgroupdiv.append(ldiv);
    });
  }
}

function createfriendAddModel() {
  let overlay = document.createElement("div");
  let modal = document.createElement("div");

  modal.textContent = "Add Group ";

  overlay.style.cssText = `
  display: block; /* Hidden by default */
  position: fixed; /* Stay in place */
  z-index: 1; /* Sit on top */
  left: 0;
  top: 0;
  width: 100%; /* Full width */
  height: 100%; /* Full height */
  overflow: auto; /* Enable scroll if needed */
  background-color: rgb(0,0,0); /* Fallback color */
  background-color: rgba(0,0,0,0.4); /* Black w/ opacity */
`;

  modal.style.cssText = `background-color: #fefefe;
  margin: 15% auto; /* 15% from the top and centered */
  padding: 20px;
  border: 1px solid #888;
  width: 80%;`;

  let modalcolapse = document.createElement("button");
  modalcolapse.textContent = "X";
  modalcolapse.addEventListener("click", event => {
    overlay.remove();
  });

  modal.append(modalcolapse);

  let groupcreate = document.createElement("div");

  let input = document.createElement("input");
  let createButton = document.createElement("button");
  createButton.textContent = "add group";

  createButton.addEventListener("click", e => {
    if (input.value) {
      let formdata = new FormData();
      formdata.append("title", input.value);
      fetch("/createGroupConversation", {
        method: "POST",
        body: formdata
      })
        .then(res => res.json())
        .then(res => {
          if (res.response) {
            let frienddiv = document.createElement("div");
            frienddiv.innerHTML = "<p>friends</p> ";
            let input = document.createElement("input");
            input.id = "friendsearchtxt";
            let button = document.createElement("button");
            button.id = "friendsearchbtn";
            button.textContent = "search";
            let friendlist = document.createElement("div");
            friendlist.id = "friendlistdiv";
            frienddiv.append(input);
            frienddiv.append(button);
            frienddiv.append(friendlist);
            createButton.disabled = true;
            modal.append(frienddiv);

            doSearch(
              "friendsearchbtn",
              "friendsearchtxt",
              "friendlistdiv",
              "/friendlistsearch",
              "Add to Group",
              res.id,
              handlegrouplist
            );
          }
        });
    }
  });

  groupcreate.append(input);
  groupcreate.append(createButton);
  modal.append(groupcreate);
  overlay.append(modal);
  document.body.append(overlay);
}

function doSearch(
  buttonid,
  inputid,
  listid,
  route,
  buttontxt,
  convid,
  handler
) {
  let button = document.getElementById(buttonid);
  button.addEventListener("click", event => {
    let input = document.getElementById(inputid);

    if (!(input.value === "")) {
      let listdiv = document.getElementById(listid);
      listdiv.innerHTML = " ";
      handlelist(listdiv, route, buttontxt, handler, input.value, convid);
    }
  });
}

async function fetchpost(route, targetname, targetid) {
  let formdata = new FormData();
  formdata.append(targetname, targetid);
  let res = await fetch(route, {
    method: "POST",
    body: formdata
  });
  res = await res.json();
  return res;
}

async function handlelist(
  listdiv,
  route,
  buttontxt,
  handler,
  inputtxt,
  convid
) {
  let res = await fetchpost(route, "friend_txt", inputtxt);
  if (res.response) {
    res.friends.map(item => {
      let div = document.createElement("DIV");
      div.dataset.id = item.id;
      div.innerHTML = `<span>${item.email}</span>`;
      let button = document.createElement("BUTTON");
      button.textContent = buttontxt;
      div.append(button);
      div.addEventListener("click", event => {
        if (event.target.nodeName == "BUTTON") {
          (async () => {
            let res = await handler(event.currentTarget.dataset.id, convid);
            if (res.response) {
              div.remove();
            }
          })();
        }
      });
      listdiv.append(div);
    });
    if (res.friends === undefined || res.friends.length == 0) {
      let div = document.createElement("div");
      div.innerHTML = "there are no friends";
      listdiv.append(div);
    }
  }
}

async function handlegrouplist(targetid, convid) {
  let formdata = new FormData();
  formdata.append("conv_id", convid);
  formdata.append("friend_id", targetid);
  let res = await fetch("/groupinsert", {
    method: "POST",
    body: formdata
  });
  return await res.json();
}

function init() {
  registerHandler();
}

let eventBus;

function registerHandler() {
  let id = document.getElementById("useriddiv").dataset.id;
  eventBus = new EventBus("http://localhost:8080/eventbus");

  eventBus.onopen = function() {
    eventBus.registerHandler(`chatserver/${id}`, function(error, message) {
      handleMessages(JSON.parse(message.body));
    });
    fetch("/api/chatserver/" + id);
  };
}

function handleMessages(message) {
  switch (message.title) {
    case "activefriends":
      activefriendshandler(message);
      break;
    case "friendloginfo":
      friendloghandler(message);
      break;
    case "getchatbox":
      handlegetChatbox(message);
      break;
    default:
      break;
  }
}

//handling chat login code
function handlegetChatbox(message) {
  createSingleChatBox(message.userid, message.convtitle);
}

//set active friendlist
function activefriendshandler(message) {
  message.activefriends.forEach(element => {
    document.getElementById(
      `friendlist:${element}`
    ).lastChild.style.backgroundColor = "Green";
  });
}

//handler active login and logut
function friendloghandler(message) {
  if (message.status) {
    document.getElementById(
      `friendlist:${message.friendid}`
    ).lastChild.style.backgroundColor = "Green";
  } else {
    document.getElementById(
      `friendlist:${message.friendid}`
    ).lastChild.style.backgroundColor = "grey";
  }
}
