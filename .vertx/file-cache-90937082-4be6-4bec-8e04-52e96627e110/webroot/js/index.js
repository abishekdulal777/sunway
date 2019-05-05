function dologin(){
    let formData = new FormData();
    formData.append("email","a.email");
    formData.append("password","a");
    fetch("/home",{
        method: "POST",
        body:formData,
        redirect:"follow"
     }).then(res->{
         alert(res.redirect);
    });

}

dologin();