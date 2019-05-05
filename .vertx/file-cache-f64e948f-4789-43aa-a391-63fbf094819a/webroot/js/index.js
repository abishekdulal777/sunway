function dologin(){
    let formData = new FormData();
    formData.append("email","a.email");
    formData.append("passord","a");
    fetch("/login",{
        method: "POST",
        body:formData,
        redirect:"follow"
    });

}

dologin();