/**
 * 生成唯一uuid
 * @returns uuid
 */
function generateUUID() {
	var d = new Date().getTime();
	var uuid = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g,
			function(c) {
				var r = (d + Math.random() * 16) % 16 | 0;
				d = Math.floor(d / 16);
				return (c == 'x' ? r : (r & 0x3 | 0x8)).toString(16);
			});
	return uuid;
};

/**
 * 超时取消轮询
 * @param polling
 * @param clientId
 */
function cancelPollingByTimeout(polling,clientId){
	clearInterval(polling);
	//轮询结束，使二维码图片失效，通知后台去掉对应的map
	$.ajax({
		url: basePath + "common/cancelQrcodeLogin.action",
		type:"get",
		data:{
			"clientId":clientId
		},
		dataType:"json",
		success:function(result){
			if (result.success) {
				$(".qrcode-img").attr("src",basePath+"resource/sources/image/errorQrcode.png");
				$("#qrcode_refresh").show();
			}
			
		}
	});
	
}

/**
 * 手动点击按钮取消轮询
 * @param polling
 * @param clientId
 */
function cancelPollingByClick(polling,clientId){
	clearInterval(polling);
	//轮询结束，使二维码图片失效，通知后台去掉对应的map
	$.ajax({
		url: basePath + "common/cancelQrcodeLogin.action",
		type:"get",
		data:{
			"clientId":clientId
		},
		dataType:"json",
		success:function(result){
			if (result.success) {
				
			}
			
		}
	});
	
}

/**
 * 检查登录
 * @param clientId
 */
function checkQrcodeLogin(clientId){
	
	polling = setInterval(function(){
		
		$.ajax({
			url: basePath + "common/checkQrcodeLogin.action",
			type:"get",
			data:{
				"clientId":clientId
			},
			dataType:"json",
			success:function(result){
				//返回登录结果成功，停止论询
				if (result.success) {
					
					clearInterval(polling);
					
					//重新载入页面
					document.location.href = basePath;
				}else{
					//如果有错误原因
					if (result.reason) {
						//取消轮询
						clearInterval(polling);
						//切换二维码刷新按钮
						$(".qrcode-img").attr("src",basePath+"resource/sources/image/errorQrcode.png");
						$("#qrcode_refresh").show();
						//展示错误原因
						$.showtip(result.reason, 6000);
					}
				}
			}
		})
	},2000);
	//50s后取消轮询
	setTimeout(function(){
		if(clientId != "" && polling != ""){
			cancelPollingByTimeout(polling,clientId)
		}
	},50000);
	return polling;
};
	

/**
 * 页面加载完成后，执行的函数
 */
$(function(){
	var qrcodeFlag = true;	//状态，用于二维码与普通登陆之间的切换
	//全局唯一的clientId
	var clientId="";
	//全局唯一的轮询对象
	var polling="";
	
	//二维码切换按钮点击事件
	$("#qrcode-btn").click(function(){
		if(qrcodeFlag){
			//切换右上角图标
			$("#qrcode-btn").css("background","url(resource/sources/image/closeQrcode.png) no-repeat");
			//切换界面
			$(".prmiary-login").hide();
			$(".qrcode-login").show();
			//如果有轮询，清除
			if (!(""==clientId&&""==polling)) {
				clearInterval(polling);
				clientId="";
				polling="";
			}
			clientId = generateUUID();
			//请求生成带uuid的二维码
			$(".qrcode-img").attr("src",basePath + "common/qrcode.action"+"?clientId="+clientId)
			polling = checkQrcodeLogin(clientId);
			qrcodeFlag = !qrcodeFlag;
		}else{
			//切换右上角图标
			$("#qrcode-btn").css("background","url(resource/sources/image/changeQrcode.png) no-repeat");
			//切换界面
			$(".qrcode-login").hide();
			$(".prmiary-login").show();
			//隐藏刷新按钮
			$("#qrcode_refresh").hide();
			//如果有轮询，清除
			if (!(""==clientId&&""==polling)) {
				cancelPollingByClick(polling,clientId);
				clientId="";
				polling="";
			}
			qrcodeFlag = !qrcodeFlag;
		}
	});	//切换按钮点击事件  函数结尾
	
	//二维码刷新按钮点击事件
	$("#qrcode_refresh").click(function() {
		//隐藏刷新按钮
		$("#qrcode_refresh").hide();
		// 如果有轮询，清除
//		if (!("" == clientId && "" == polling)) {
//			clientId = "";
//			polling = "";
//		}
		clientId = generateUUID();
		// 请求生成带uuid的二维码
		$(".qrcode-img").attr("src",basePath + "common/qrcode.action" + "?clientId="+ clientId);
		polling = checkQrcodeLogin(clientId);
	});
	
})