function dologin(){
    let formData = new FormData();
    formData.append("email","a.email");
    formData.append("password","a");
    fetch("/home",{
        method: "POST",
        body:formData,
        redirect:"follow"
     }).then(res=>{res.json()})
       .then(data=>
         console.log(data);
       );
    
    

}

dologin();