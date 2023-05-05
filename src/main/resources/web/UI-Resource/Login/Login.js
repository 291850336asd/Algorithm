
function LoginSubmit(){
	var IP=$("input#IP").val();
	var port=$("input#port").val();
	var username=$("input#username").val();
	var password=$("input#password").val();
	var httpsEnalbe = $("#httpsEnable").is(":checked"); 
	var strJSON ={"IP":IP,"port":port,"username":username,"password":password,"httpsEnalbe":httpsEnalbe};
	jQuery.ajax({
		url:"hikvision/userCheck",
		type:"POST",
		data:strJSON,
		success:function(obj){
			if(obj.loginResult==1){
				alert("Login success!");
				window.location.replace("hikvision/hello");
			}else{
				alert("Login failed ! Please check!");
			}
		},
		error:function(){
			alert("Login failed ! Please check!");
		},
		complete:function(obj){

		}
	});
}

//设置port
function setPort(){
    var isHttps= $("#httpsEnable").is(":checked");
    if(isHttps){
    	//如果是https
        $('#port').val("443");
    }
    else{
    	//如果是http
        $('#port').val("80");
    }
}