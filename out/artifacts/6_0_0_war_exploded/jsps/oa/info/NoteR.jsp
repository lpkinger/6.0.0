<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html>
<html style="min-height:100%">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link rel="stylesheet" href="<%=basePath %>resource/ext/resources/css/ext-all-gray.css" type="text/css"></link>
<link rel="stylesheet" href="<%=basePath %>resource/css/main.css" type="text/css"></link>
<style type="text/css">
* {
  margin: 0;
  padding: 0;
}
h1{
	padding-top: 85px;
}
body{
	min-height:100%;
	background: #f1f2f5;
	font-family: 宋体;
}
.bgimage{
	 position: fixed;
  	 left: 0;
  	 top: 0;
     width: 100%;
     height: 100%;
	 background-image:url("<%=basePath%>resource/images/screens/news_bg.jpg");
     background-size: cover;
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
#top{
	position: relative;
 	z-index: 101; 
	margin:0; 
	height:100%;
	width:100%;	
}
#content{
 	position: relative;
 	z-index: 100; 
	font-size: 16px;
	margin:0; 
	height:100%;
	width:100%;	
} 
.note-body {
    padding-left: 25%;
    z-index: 0;
}
.note-title {
	font-weight: 800;
	font: bold 16px Arial, Helvetica;
    color: #8f5a0a;
    text-transform: uppercase;
    text-align: center;
    margin: 0 0 20px 0;
    letter-spacing: 4px;
    position: relative;
    max-width: 400px;
}
.note-title:after, .note-title:before{
    background-color: #777;
    content: "";
    height: 1px;
    position: absolute;
    top: 15px;
    width: 120px;   
}
.note-title:after{ 
    background-image: -webkit-gradient(linear, left top, right top, from(#777), to(#fff));
    background-image: -webkit-linear-gradient(left, #777, #fff);
    background-image: -moz-linear-gradient(left, #777, #fff);
    background-image: -ms-linear-gradient(left, #777, #fff);
    background-image: -o-linear-gradient(left, #777, #fff);
    background-image: linear-gradient(left, #777, #fff);      
    right: 0;
}
.note-title:before{
    background-image: -webkit-gradient(linear, right top, left top, from(#777), to(#fff));
    background-image: -webkit-linear-gradient(right, #777, #fff);
    background-image: -moz-linear-gradient(right, #777, #fff);
    background-image: -ms-linear-gradient(right, #777, #fff);
    background-image: -o-linear-gradient(right, #777, #fff);
    background-image: linear-gradient(right, #777, #fff);
    left: 0;
}
.note-text {
	margin-bottom: 15px;
	margin-left: 20px;
}
.note-item {
	color: #666;
	margin: 2px 0px 2px 10px;
}
.note-default {
	overflow:hidden;margin:0 auto;text-align:left;width:700px;
}
</style>
<script type="text/javascript" src="<%=basePath %>resource/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/i18n/i18n.js"></script>
<script type="text/javascript">	
/*通知浏览界面*/
Ext.onReady(function(){
	var id = getUrlParam('formCondition').replace(/IS/g, '=').split('=')[1].replace(/'/g, "");
	Ext.Ajax.request({
   		url : basePath + 'oa/note/getNote.action',
   		params: {
   			id:id
   		},
   		method : 'post',
   		callback : function(options,success,response){
   			var localJson = new Ext.decode(response.responseText);
			if(localJson.success){			
					Ext.get('title').insertHtml('afterBegin', localJson.no_title);
					Ext.get('date').insertHtml('afterBegin', localJson.date);
					Ext.get('approver').insertHtml('beforeBegin', '审批人:');
					Ext.get('approver').insertHtml('afterBegin', localJson.approver);
					Ext.get('content').insertHtml('afterBegin', "&nbsp;&nbsp;&nbsp;" + localJson.content);
					var prev = Ext.get('prevNOID');
					if(localJson.prevNOID != null){
						prev.insertHtml('beforeBegin','上一条:');
						prev.insertHtml('afterBegin',localJson.prevTitle);
						prev.dom.href = 'NoteR.jsp?formCondition=no_idIS'+localJson.prevNOID;
					}
					var next = Ext.get('nextNOID');
					if(localJson.nextNOID != null){
						next.insertHtml('beforeBegin','&nbsp;&nbsp;&nbsp;下一条:');
						next.insertHtml('afterBegin',localJson.nextTitle);
						next.dom.href = 'NoteR.jsp?formCondition=no_idIS'+localJson.nextNOID;
					}
					var attachs=localJson.no_attachs;
					  if(attachs.length>0&&attachs!=""&&attachs!=null){	
						  Ext.get('content').insertHtml('beforeEnd','</br></br></br>');					 
					        Ext.Array.each(attachs,function(attach){
					        	Ext.get('content').insertHtml('beforeEnd','<li style="float:right;padding-right:80px; font-size:14px;"><a href='+basePath+'common/downloadbyId.action?id='+attach.split("#")[0]+'>'+attach.split('#')[1]+'</a></li>');
						        });
						 } 
					if(localJson.no_emergency == '3' || localJson.no_emergency == '特急'){
						document.getElementById('emergency').className = 'emergency_worry';
					}
					//审核人 可编辑和修改新闻
					if(localJson.approver == em_name){
						Ext.get('buttons').insertHtml('afterBegin','<input type="button" value="编&nbsp;辑" class="input" onclick="editNote();" id="edit"><input type="button" value="删&nbsp;除" class="input" onclick="deleteNote();" id="delete">');
						/* Ext.get('edit').destroy();
						Ext.get('delete').destroy(); */
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
function deleteNote() {
	var id = getUrlParam('formCondition').replace(/IS/g, '=').split('=')[1].replace(/'/g, "");
	Ext.Ajax.request({
   		url : basePath + '/oa/note/deleteNote.action',
   		params: {
   			id: id
   		},
   		method : 'post',
   		callback : function(options,success,response){   		
   			window.location.href = basePath + 'jsps/oa/info/Note.jsp';
   			}
   		});
}
function editNote() {
	var id = getUrlParam('formCondition').replace(/IS/g, '=').split('=')[1].replace(/'/g, "");	
   	window.location.href = basePath + 'jsps/oa/info/Note.jsp?formCondition=no_id='+id;
}
</script>
</head>
<body>
		<div class="bgimage"></div>
		<div id='top'><center><h1><span id="title" ></span></h1><br/>
		<font color=#999;><span id="date"></span></font>&nbsp;&nbsp;&nbsp;
		<font color=#999;><span id="approver"></span></font>&nbsp;&nbsp;&nbsp;	
		<div id = 'buttons'></div></center>
		<div align="right" style="margin-right:100px;"><a id = "prevNOID" href="#"></a><a id = "nextNOID" href="#"></a>
		</div>
		</div>
		<div id="content"></div>
		<!-- <div id="attachs"> </div> -->
		<!-- <div id="emergency" style="height:174px"></div> -->
</body>
</html>