function dologin(){
    let formData = new FormData();
    formData.append("email","a.email");
    formData.append("password","a");
    fetch("/login",{
        method: "POST",
        body:formData,
        redirect:"follow"
     }).then(data=>data.json())
       .then(data=>{
        if(data.response){
         redirect: window.location.replace(data.url) 
        } else{
        alert("Invalid Email or Password");
         }
       });
    
    

}

dologin();