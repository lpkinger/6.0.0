function showMain(id,title,img){
	$.ajax({
		url:basePath + 'common/charts/getPreviewMain.action',
		type: 'POST',
		data: {
			id : id
		},
		success: function(result){
			var count = result.details.length;
			var src=result.mainImg!=null?'data:image/gif;base64,'+result.mainImg:basePath + 'resource/images/a1.png';
			if(count>1){
				$('#title').html('<img id="imageIcon" src='+src+' style=" width: 90px; height: 90px;"><font id="DYOnly">'+title+'</font>');
				$('#intro').html('<div><font id = "JJ">简介</font><font id = "introa">:此订阅号包含'+count+'个订阅项</font></div>');
				for(var i = 0; i<count; i++){
					var titlex = result.details[i][0]+';';
					$('#intro').append('<div><font id="introb">'+(i+1)+'、'+titlex+'</font></div>');
				}
				for(var i = 0; i<count; i++){
					var intro = result.details[i][1] || '该订阅项暂没有简介';
					$('#main').append('<div><font id = "introa">'+result.details[i][0]+'</font></div><div><font id="introc">介绍:'+intro+'</font></div></div>');
					if(result.details[i][2]){
						if(result.details[i][3]=='sum'){
							$('#main').append('<div id = "imagepos"><img id="imageMain2" class="img-responsive" src="data:image/gif;base64,'+result.details[i][2]+'" alt="订阅项说明简图"></div>');
						}else{
							$('#main').append('<div id = "imagepos"><img id="imageMain" class="img-responsive" src="data:image/gif;base64,'+result.details[i][2]+'" alt="订阅项说明简图"></div>');
						}
					}else{
						$('#main').append('<font id="NoPic" size="5">暂无预览图片</font>');
					}
				}
			}else if(count==1){
				$('#title').html('<img id="imageIcon" src='+src+' style=" width: 90px; height: 90px;"><font id="DYOnly">'+title+'</font>');
				$('#intro').html('<div><font id = "JJ">简介</font><font id = "introa">:'+result.details[0][0]+'</font></div>');
				$('#intro').append('<font id="introb">'+(result.details[0][1]||"该订阅项暂没有简介")+'</font>')
				if(result.details[0][2]){
					if(result.details[0][3]=='sum'){
						$('#image').append('<div><img class="img-responsive" id="imageMain2" src="data:image/gif;base64,'+result.details[0][2]+'" alt="订阅项说明简图"></div>');
					}else{
						$('#image').append('<div><img class="img-responsive" id="imageMain" src="data:image/gif;base64,'+result.details[0][2]+'" alt="订阅项说明简图"></div>');
					}
				}else{
					$('#image').append('<font id="NoPic" size="5">暂无预览图片</font>');
				}
			}else{
				$('#title').html('<h1>该订阅号暂没有订阅项</h1>');
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