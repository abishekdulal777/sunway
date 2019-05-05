function doSearch(buttonid, inputid, listid, route, buttontxt, handler) {
  let button = document.getElementById(buttonid);
  button.addEventListener("click", event => {
    let input = document.getElementById(inputid);

    if (!(input.value === "")) {
      let listdiv = document.getElementById(listid);
      listdiv.innerHTML = " ";
      handlelist(listdiv, route, buttontxt, handler, input.value);
    }
  });
}

window.onload = function() {
  doSearch(
    "unfriendlistbtn",
    "unfriendlisttxt",
    "unfriendlist",
    "/friendlistsearch",
    "Unfriend",
    handleunfriendlist
  );
  doSearch(
    "unblocklistbtn",
    "blocklisttxt",
    "blocklist",
    "/friendlistsearch",
    "Block",
    handleblocklist
  );
  handleunblocklist();
};

async function fetchlist(route) {
  let res = await fetch(route);
  res = await res.json();
  return res;
}

async function handlelist(listdiv, route, buttontxt, handler, inputtxt) {
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
        if ((event.target.nodeName = "BUTTON")) {
          (async () => {
            let res = await handler(event.currentTarget.dataset.id);
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

async function handleblocklist(targetid) {
  return await fetchpost("/friendblock", "friend_id", targetid);
}

async function handleunfriendlist(targetid) {
  return await fetchpost("/unfriend", "friend_id", targetid);
}

async function handleunblocklist() {
  let listdiv = document.getElementById("unblocklist");
  let res = await fetchlist("/blocklist");
  if (res.response) {
    res.friends.map(item => {
      let div = document.createElement("DIV");
      div.dataset.id = item.id;
      div.innerHTML = `<span>${item.email}</span>`;
      let button = document.createElement("BUTTON");
      button.textContent = "Unblock";
      div.addEventListener("click", event => {
        if ((event.target.nodeName = "BUTTON")) {
          (async () => {
            let res = await fetchpost(
              "/unblock",
              "friend_id",
              event.currentTarget.dataset.id
            );
            if (res.response) {
              div.remove();
            }
          })();
        }
      });
      div.append(button);
      listdiv.append(div);
    });
    if (res.friends === undefined || res.friends.length == 0) {
      let div = document.createElement("div");
      div.innerHTML = "there are no friends";
      listdiv.append(div);
    }
  }
}
