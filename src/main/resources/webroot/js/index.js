window.onload = function() {
  let loginform = document.getElementById("loginform");
  loginform.addEventListener("submit", event => {
    event.preventDefault(event);
    let email = document.getElementsByName("email")[0].value;
    let password = document.getElementsByName("password")[0].value;
    let formdata = new FormData();
    formdata.append("email", email);
    formdata.append("password", password);
    dologin(formdata);
  });
};

function dologin(formData) {
  fetch("/login", {
    method: "POST",
    body: formData,
    redirect: "follow"
  })
    .then(data => data.json())
    .then(data => {
      if (data.response) {
        redirect: window.location.replace("home");
      } else {
        alert("Invalid Email or Password");
      }
    });
}
