<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link rel="stylesheet" href="<%=basePath %>resource/ext/resources/css/ext-all-gray.css" type="text/css"></link>
<!--[if IE]>
	<link rel="stylesheet" href="<%=basePath %>resource/ext/resources/css/ext-ie-scoped.css" type="text/css"></link>
<![endif]-->
<link rel="stylesheet" href="<%=basePath %>resource/css/main.css" type="text/css"></link>
<style type="text/css">
h1{
	padding-top: 10px;
	font-size: 15px;
}
.comment{
	padding-left: 15px;
}
.comment .title{
	color: #999;
	font-size: 14px;
	font-weight: bold;
	font-family: fantasy;
}
.input{
	margin-right: 5px;
	background-color: #9ABACD;
	font-weight: bold;
	height: 30;
	width: 70;
}
.input:hover{
	background-color: #3CD60D;
}
form{
	padding-left: 15px;
}
form textarea{
	height: 100;
	width: 100%;
	background: #fefefe;
	border: 1px solid #EEDFCC;
}
form textarea:focus{
	background: #ffffff;
	border: 2px solid #FFA07A;
}
form input{
	font-weight: bold;
	 height: 30;
	 width: 70;
	 color: #fdfdfd;
	 float: right;
	 cursor: pointer;
	 background-color: #6A5ACD;
	 padding-right: 5px;
}
form input:hover{
	background-color: #6CA6CD;
}
form font {
	color: #999;
	font-size: 14px;
	font-weight: bold;
	font-family: fantasy;
}
form #user{
	font-size: 13px;
	font-weight: normal;
}
/* .feel{
	padding-left: 15px;
}
.feel font{
	color: #7D9EC0;
	font-size: 14px;
	font-weight: bold;
	font-family: fantasy;
} 
#myfeel li{
	margin-left: 36px;
	margin-top: 8px;
	padding: 0px;
	white-space: nowrap;
	display: inline;
	float: left;
}
#myfeel li div,img{
	margin-top: 8px;
	color: #999;
}
#myfeel li img:hover{
	background: #FFEC8B;
	background-position: 0 -30px;
	cursor: pointer;
}*/
.pill{
	height: 60px;
	width: 13px;
	border: 1px solid #E5E5E5;
	background: #fefefe url(<%=basePath%>resource/images/pill.gif) no-repeat scroll center center;
}
.main{
	
}

#content{
	padding-left: 15px;
}
#submitBtn{
font-size:12px;
height:30px;
width:100px;
border-width:2;
}
</style>
<script type="text/javascript" src="<%=basePath %>resource/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/i18n/i18n.js"></script>
<script type="text/javascript">	
/*新闻浏览界面*/
Ext.onReady(function(){
	var id = getUrlParam('formCondition').replace(/IS/g, '=').split('=')[1];
	Ext.Ajax.request({
   		url : basePath + 'oa/news/getNews.action',
   		params: {
   			id: id
   		},
   		method : 'post',
   		callback : function(options,success,response){
   			var localJson = new Ext.decode(response.responseText);
			if(localJson.success){
				var news = localJson.news;
				Ext.get('theme').insertHtml('afterBegin', news.ne_theme);
				Ext.get('date').insertHtml('afterBegin', Ext.Date.format(new Date(news.ne_releasedate), 'Y-m-d H:i:s'));
				Ext.get('releaser').insertHtml('afterBegin', news.ne_releaser);
				Ext.get('editText').insertHtml('beforeBegin','编辑:');
				Ext.get('count').insertHtml('afterBegin', news.ne_browsenumber);
				Ext.get('viewText').insertHtml('beforeBegin','浏览:');
				Ext.get('content').insertHtml('afterBegin', news.ne_content);
				Ext.get('user').insertHtml('afterBegin', '&nbsp;&nbsp;欢迎您:&nbsp;&nbsp;' + em_code + "(" + em_name + ")");
				Ext.get('center').insertHtml('afterEnd', '<hr>');
				Ext.get('yourComment').insertHtml('afterBegin', '填写您的评论:');
				//审核人 可编辑和修改新闻
				if(news.ne_releaser == em_name){
					//Ext.get('edit').destroy();
					//Ext.get('delete').destroy();
					Ext.get('viewText').insertHtml('afterEnd','<input type="button" value="编&nbsp;辑" class="input" onclick="editNews();" id="edit"><input type="button" value="删&nbsp;除" class="input" onclick="deleteNews();" id="delete">');
				}
				Ext.get('viewText').insertHtml('afterEnd','次&nbsp;&nbsp;&nbsp;<a href="#">评论:(<font color=red;>0</font>人参与)</a>');
				Ext.get('commit').insertHtml('afterBegin','<input type="button" id="submitBtn" onclick="sendComment();"  value="&nbsp;提&nbsp;&nbsp;交" />');
				Ext.create('Ext.form.HtmlEditor', {
			         width: 1039,
			         height: 150,
			         id:'comment',
			        renderTo:'textcomment'
			      }); 
				//设置心情
/* 				var count = 0;
				news.ne_feel = news.ne_feel || '0#0#0#0#0#0#0#0';
				Ext.each(news.ne_feel.split('#'), function(){
					count += Number(this);					
				});
				Ext.each(news.ne_feel.split('#'), function(f, index){
					if(count > 0){
						document.getElementById("feel_p_" + (index + 1)).style.backgroundPosition = "center " + (60 - 60*Number(f)/count) + "px";
						Ext.get("feel_n_" + (index + 1)).insertHtml('afterBegin', f);
					} else {
						document.getElementById("feel_p_" + (index + 1)).style.backgroundPosition = "center 60px";
						Ext.get("feel_n_" + (index + 1)).insertHtml('afterBegin', 0);
					}
				}); */
				var comments=news.comments;
		        Ext.Array.each(comments,function(comment){
		        	Ext.get('newscomment').insertHtml('afterBegin','<li><div style="height:50px; background-color:#FFF0F5;">'+comment.nc_comment+'</div></br><div style="float: right;">'+comment.nc_caster+"&nbsp&nbsp&nbsp"+comment.nc_date+'</div></li></br>');
		        	
		        });
		        //console.log(news);
		        var prevstr=news.prevNews!=null?news.prevNews.ne_theme:"没有上一条";
		        var nextstr=news.nextNews!=null?news.nextNews.ne_theme:"没有下一条";
		        if(!news.prevNews&&!news.nextNews){
		        	Ext.get('change').insertHtml('afterBegin',' <div align="right" style="margin-right:20px">上一条:<a href="#"><font >'+prevstr+'</font></a>&nbsp;&nbsp;下一条 <a href="#">'+nextstr+'<font ></font></a></div>');
		        }
		        else if(!news.prevNews){
		        	Ext.get('change').insertHtml('afterBegin',' <div align="right" style="margin-right:20px">上一条:<a href="#"><font >'+prevstr+'</font></a>&nbsp;&nbsp;下一条 <a href="javascript:openFormUrl(\'' + news.nextNews.ne_id + '\',\'ne_id\',\'jsps/oa/news/NewsR.jsp\',\'新闻\');">'+nextstr+'<font ></font></a></div>');
		        }else if(!news.nextNews){
		        	Ext.get('change').insertHtml('afterBegin',' <div align="right" style="margin-right:20px">上一条:<a href="javascript:openFormUrl(\'' + news.prevNews.ne_id + '\',\'ne_id\',\'jsps/oa/news/NewsR.jsp\',\'新闻\');"><font >'+prevstr+'</font></a>&nbsp;&nbsp;下一条 <a href="#">'+nextstr+'<font ></font></a></div>');
		        }else {
		        	Ext.get('change').insertHtml('afterBegin',' <div align="right" style="margin-right:20px">上一条:<a href="javascript:openFormUrl(\'' + news.prevNews.ne_id + '\',\'ne_id\',\'jsps/oa/news/NewsR.jsp\',\'新闻\');"><font >'+prevstr+'</font></a>&nbsp;&nbsp;下一条 <a href="javascript:openFormUrl(\'' + news.nextNews.ne_id + '\',\'ne_id\',\'jsps/oa/news/NewsR.jsp\',\'新闻\');">'+nextstr+'<font ></font></a></div>');
		        }
			} else {
				if(localJson.exceptionInfo){
					showError(localJson.exceptionInfo);
					window.location.href = basePath + 'jsps/error/e-500.jsp';
	   			}
			}
   		}
	});
}); 
function sendComment() {
	var id = getUrlParam('formCondition').replace(/IS/g, '=').split('=')[1];
	var comment=Ext.getCmp('comment').getValue( );
	Ext.Ajax.request({
   		url : basePath + 'oa/news/sendComment.action',
   		params: {
   			id: id,
   			comment:comment,
   		},
   		method : 'post',
   		callback : function(options,success,response){
   			Ext.getCmp('comment').setValue(null);
   			window.location.href = basePath + 'jsps/oa/news/NewsR.jsp?formCondition=id='+id;
   			}
   		});
}
function deleteNews() {
	var id = getUrlParam('formCondition').replace(/IS/g, '=').split('=')[1];
	var comment=Ext.getCmp('comment').getValue( );
	console.log(basePath + 'oa/news/deleteNews.action');
	Ext.Ajax.request({
   		url : basePath + 'oa/news/deleteNews.action',
   		params: {
   			id: id
   		},
   		method : 'post',
   		callback : function(options,success,response){
   			Ext.getCmp('comment').setValue(null);
   			window.location.href = basePath + 'jsps/oa/news/NewsR.jsp?formCondition=id='+id;
   			}
   		});
}
function editNews() {
	var id = getUrlParam('formCondition').replace(/IS/g, '=').split('=')[1];	
   	window.location.href = basePath + 'jsps/oa/news/News.jsp?formCondition=ne_id='+id;
   			
}
</script>
</head>
<body >
 	<center id='center'>
		<h1><span id="theme"></span></h1><br/>
		<font color=#999;><span id="date"></span></font>&nbsp;&nbsp;&nbsp;
		<font id="editText" color=#999;><span id="releaser"></span></font>&nbsp;&nbsp;&nbsp;
		<font id="viewText" color=red><span id="count"></span></font>
		<div id="change"></div>
	</center>
	&nbsp;&nbsp;&nbsp;<div class="main"><span id="content"></span></div>
	
	<!--  <div class="feel">
		<font>读完这篇新闻后，您心情如何？</font>
		<ul id="myfeel">
			<li>
				<div id="feel_n_1"></div>
				<div id="feel_p_1" class="pill"></div>
				<img src="<%=basePath %>resource/images/face/1.gif">
				<div id="feel_i_1">高兴</div>
			</li>
			<li>
				<div id="feel_n_2"></div>
				<div id="feel_p_2" class="pill"></div>
				<img src="<%=basePath %>resource/images/face/2.gif">
				<div id="feel_i_2">感动</div>
			</li>
			<li>
				<div id="feel_n_3"></div>
				<div id="feel_p_3" class="pill"></div>
				<img src="<%=basePath %>resource/images/face/3.gif">
				<div id="feel_i_3">同情</div>
			</li>
			<li>
				<div id="feel_n_4"></div>
				<div id="feel_p_4" class="pill"></div>
				<img src="<%=basePath %>resource/images/face/4.gif">
				<div id="feel_i_4">愤怒</div>
			</li>
			<li>
				<div id="feel_n_5"></div>
				<div id="feel_p_5" class="pill"></div>
				<img src="<%=basePath %>resource/images/face/5.gif">
				<div id="feel_i_5">搞笑</div>
			</li>
			<li>
				<div id="feel_n_6"></div>
				<div id="feel_p_6" class="pill"></div>
				<img src="<%=basePath %>resource/images/face/6.gif">
				<div id="feel_i_6">难过</div>
			</li>
			<li>
				<div id="feel_n_7"></div>
				<div id="feel_p_7" class="pill"></div>
				<img src="<%=basePath %>resource/images/face/7.gif">
				<div id="feel_i_7">新奇</div>
			</li>
			<li>
				<div id="feel_n_8"></div>
				<div id="feel_p_8" class="pill"></div>
				<img src="<%=basePath %>resource/images/face/8.gif">
				<div id="feel_i_8">流汗</div>
			</li>
		</ul>
	</div> 
-->
	<div>
	<br/><br/><br/><br/><br/>
	<ul id="newscomment">
	 	<li></li>
	</ul>
	</div>
	<div>
		<form >
			<br/><br/><br/><br/><br/>
			<font id='yourComment'></font><font id='user'></font><br/>
			<div id="textcomment"></div>
			<!-- <textarea rows="10" name="comment" id="comment"></textarea> -->
			<div id='commit'></div>
		</form>
	</div>
	<br/>	
<!-- 	<div action="">			
			<font id='comment'></font><br/>	
			<font id='div'></font><br/>			
			<input type='button' value="&nbsp;回&nbsp;&nbsp;复"/>
	</div>	
	<div class="comment"><span class="title">查看最新评论(</span><font color=red;>0</font><span class="title">人参与)</span></div>	 -->
</body>
</html>