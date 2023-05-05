
//定义全局服务器返回URL
var g_targetURL = "";
var g_ContrastURL = "";

/**
* @Description 获取比对的图片
* @param null
* @return void
*/	
 function selectTarget(){
	 //从控件获取文件名称
    var picFile = document.getElementById("filetarget").files[0];
    var arrayBuffer = "" 
     //读取图片数据
	if (picFile) {
	    var reader = new FileReader();    	   
	    reader.onload = function (e) {
		    arrayBuffer = reader.result;
		    //组装json数据
			 var strJSON ={"picFile":arrayBuffer};
			 //发送数据到服务器
				jQuery.ajax({
					url:"/hikvision/compare1V1/getPicUrl",
					type:"POST",
					data:strJSON,
					success:function(obj){
						if(obj.errorCode==1){
						   //获取返回的URL
							g_targetURL= obj.URL ;
							//将URL设置给控件
							$("#targetImage").css("background-image","url(" + obj.URL + ")"); 
						}else{		
							var errorMsg = "failed,errorMsg:" + obj.errorMsg;
							alert(errorMsg);
						}
					},
					error:function(){
						alert("Communication exception, please check!");
					},
					complete:function(obj){

					}
				});
	    }
	    reader.readAsBinaryString(picFile);
	}
		
 }
	/**
	* @Description 获取比对的图片
	* @param null
	* @return void
	*/	
 function selectContrast(){
	    var picFile = document.getElementById("filecontrast").files[0];
	    var arrayBuffer = "" 
	    //读取图片数据
	    if (picFile) {
			var reader = new FileReader();
			       
			reader.onload = function (e) {
				arrayBuffer = reader.result;
				//组装json数据
				 var strJSON ={"picFile":arrayBuffer,};
				 //发送数据到服务器
					jQuery.ajax({
						url:"/hikvision/compare1V1/getPicUrl",
						type:"POST",
						data:strJSON,
						success:function(obj){
							if(obj.errorCode==1){
								 //获取返回的URL
								g_ContrastURL=  obj.URL ;
								//将URL设置给控件
								$("#contrastImage").css("background-image","url(" + obj.URL + ")"); 
							}else{		
								var errorMsg = "failed,errorMsg:" + obj.errorMsg;
								alert(errorMsg);
							}
						},
						error:function(){
							alert("Communication exception, please check!");
						},
						complete:function(obj){

						}
					});	
			}
		  reader.readAsBinaryString(picFile);
	    }	

 }
	/**
	* @Description  1v1比对
	* @param null
	* @return void
	*/	
 function compare(){
	//组装json数据
	 var strJSON ={"targetURL":g_targetURL,"constrastURL":g_ContrastURL};
	 //发送数据到服务器
	 jQuery.ajax({
			url:"/hikvision/compare1V1/constrast",
			type:"POST",
			data:strJSON,
			success:function(obj){
				if(obj.errorCode==1){
					//获取相似度
					var similarity = "相似度：" + obj.similarity;				
					alert(similarity);
				}else{		
					var errorMsg = "failed,errorMsg:" + obj.errorMsg;
					alert(errorMsg);
				}
			},
			error:function(){
				alert("Communication exception, please check!");
			},
			complete:function(obj){

			}
		});

	}