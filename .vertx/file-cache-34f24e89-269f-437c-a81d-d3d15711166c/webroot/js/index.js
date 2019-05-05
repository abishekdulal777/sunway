function dologin(){
    let formData = new FormData();
    formData.append("email","catman");
    formData.append("password","hari");
    fetch("/login",{
        method: "POST",
        body:formData,
        redirect:"follow"
     }).then(data=>data.json())
       .then(data=>{
        if(data.response){
         redirect: window.location.replace("home");
        } else{
        alert("Invalid Email or Password");
         }
       });

}

dologin();