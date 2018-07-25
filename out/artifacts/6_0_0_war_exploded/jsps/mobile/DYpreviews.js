function show(id){
	$.ajax({
		url:basePath + 'common/charts/getPreviewsMain.action',
		type: 'POST',
		data: {
			id : id
		},
		success: function(result){
			var title = result[0];
			var introduction = result[1]||'该订阅项暂没有简介';
			var image = result[2];
			var src = basePath + 'resource/images/a1.png';
			$('#title').html('<img id="imageIcon" src='+src+'><font id="DYOnly">'+title+'</font>')
			$('#intro').html('<div><font id = "JJ">简介</font><font id = "introa">:'+introduction+'</font></div>');
			if(image){
				if(result[3]=='sum'){
					$('#image').html('<div><img id="imageMain2" class="img-responsive" src="data:image/gif;base64,'+image+'" alt="订阅项说明简图"></div>');
				}else{
					$('#image').html('<div><img id="imageMain" class="img-responsive" src="data:image/gif;base64,'+image+'" alt="订阅项说明简图"></div>');
				}
			}else{
				$('#image').append('<font id="NoPic" size="5">暂无预览图片</font>');
			}
		},
		error: function(xhr,a,b,c){
			if(xhr.responseText) {
				var response =$.evalJSON(xhr.responseText);
				if(response.exceptionInfo == 'ERR_NETWORK_SESSIONOUT') {
					$('#title').html('<h1>获取信息失败,请先登录!</h1>');
				} else {
					$('#title').html('<h1>'+response.exceptionInfo+'</h1>');
				}
			}
		}
	});
}