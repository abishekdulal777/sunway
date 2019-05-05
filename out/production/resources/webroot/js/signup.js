window.onload = function() {
  let signupform = document.getElementById("signupform");
  signupform.addEventListener("submit", event => {
    event.preventDefault(event);
    let email = document.getElementsByName("email")[0].value;
    let firstname = document.getElementsByName("firstname")[0].value;
    let lastname = document.getElementsByName("lastname")[0].value;
    let password = document.getElementsByName("password")[0].value;
    let re_password = document.getElementsByName("re-password")[0].value;
    let phone_no = document.getElementsByName("phone_no")[0].value;

    if (password === re_password) {
      let formdata = new FormData();
      formdata.append("email", email);
      formdata.append("firstname", firstname);
      formdata.append("lastname", lastname);
      formdata.append("password", password);
      formdata.append("phone_no", phone_no);
      dosignup(formdata);
    }
  });
};

function dosignup(formData) {
  fetch("/signup", {
    method: "POST",
    body: formData,
    redirect: "follow"
  })
    .then(data => data.json())
    .then(data => {
      if (data.response) {
        redirect: window.location.replace("/");
      } else {
        alert("error in signup form");
      }
    });
}
