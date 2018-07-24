<%@ page pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head> 
<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <link rel="stylesheet" href="<%=basePath %>resource/ext/resources/css/ext-all-gray.css" type="text/css"></link>
    <link rel="stylesheet" href="<%=basePath %>resource/css/main.css" type="text/css"></link>
    <style>  
     .btn{ background-color:#FFF; border:1px solid #CDCDCD;height:24px; width:70px;}  
     </style> 
    <script type="text/javascript" src="<%=basePath %>resource/ext/ext-all.js"></script>
    <script type="text/javascript">
     var i=0;
    //用来动态生成span,upfile的id 
    function addAttachmentToList(){
    	var count=null;
    	var o=G('attachmentList').childNodes;
    	if(G('btnAdd').value=='添加文件'&&o.length>0){
    		G('attachmentList').removeChild(o[0]);
    	}
    	
    	if(findAttachment(event.srcElement.value))
    		return;
    	//如果此文档已在附件列表中则不再添加 //动态创建附件信息栏并添加到附件列表中 
    	var span=document.createElement('span');
    	span.id='_attachment'+i; 
    	//span.innerHTML='"<input type="file" name="upload'+i+'"id="fileupload'+i+'" value="'++'">"'+extractFileName(event.srcElement.value)+'&nbsp;<a href="javascript:delAttachment('+(i++)+')"><font color="blue">删除</font></a><font id="sendMsg'+i+'"></font><br/>'; 
    	span.innerHTML=extractFileName(event.srcElement.value)+'&nbsp;<a href="javascript:delAttachment('+(i++)+')"><font color="blue">删除</font></a><font id="sendMsg'+i+'"></font><br/>'; 
    	span.title=event.srcElement.value; 
    	G('attachmentList').appendChild(span);
    	send(event.srcElement.value,i);
    	//显示附件列表并变换添加附件按钮文本 
    	if(G('attachmentList').style.display=='none'){ 
    		G('btnAdd').value='继续添加'; 
    		G('attachmentList').style.display=''; 
    		G('btnClear').style.display=''; 
    		} 
       G('total').innerText='当前选择上传'+G('attachmentList').childNodes.length+'个附件'; 
    	} 
    function selectAttachment() {
    	//先清除无效动态生成的多余upfile 
    	cleanInvalidUpfile(); 
    	//动态创建上传控件并与span对应 
    	var upfile='<input type="file" style="display:none" name="upload'+i+'"  onchange="addAttachmentToList();"id="_upfile'+i+'">'; 
    	document.body.insertAdjacentHTML('beforeEnd',upfile); 
    	G('_upfile'+i).click(); 
    } 
    function extractFileName(fn){
    	return fn.substr(fn.lastIndexOf('//') + 1); 
    } 
    function findAttachment(fn){ 
    	var o=G('attachmentList').getElementsByTagName('span'); 
    	for(var i=0;i<o.length;i++) 
    		if(o[i].title==fn)
    			return true; 
    	return false; 
    } 
    function delAttachment(id) {
    	G('attachmentList').removeChild(G('_attachment'+id)); 
    	document.body.removeChild(G('_upfile'+id)); 
    	//当附件列表为空则不显示并且变化添加附件按钮文本 
    	if(G('attachmentList').childNodes.length==0){
    		G('btnAdd').value='添加附件'; 
    		G('attachmentList').style.display='none'; 
    		G('btnClear').style.display='none'; } 
    	G('total').innerText='当前选择上传'+G('attachmentList').childNodes.length+'个附件';
		
    } 
    function cleanInvalidUpfile(){
    	var o=document.body.getElementsByTagName('input'); 
    	for(var i=o.length-1;i>=0;i--)
    		if(o[i].type=='file'&&o[i].id.indexOf('_upfile')==0){ 
    			if(!G('_attachment'+o[i].id.substr(7))) 
    				document.body.removeChild(o[i]);
			} 
    	} 
    function clearAttachment(){
		var o=G('attachmentList').childNodes; 
		for(var i=o.length-1;i>=0;i--) 
			G('attachmentList').removeChild(o[i]); 
		o=document.body.getElementsByTagName('input'); 
		for(var i=o.length-1;i>=0;i--) 
			if(o[i].type=='file'&&o[i].id.indexOf('_upfile')==0){
				document.body.removeChild(o[i]); 
				} 
		G('btnAdd').value='添加文件'; 
		G('attachmentList').style.display='none'; 
		G('btnClear').style.display='none'; 
		G('total').innerText='当前选择上传0个附件'; 
		} 
    function getAttachmentInfo(){ 
    	//已知的js获取本地文件大小的三种方式 
    	//1.通过FSO2.通过ActiveX3.通过Flash(设置可能更麻烦)与js交互
    	//注：QQ邮箱中获取本地文件大小就是采用第二种方式 
    	} 
    function G(id){ 
    	return document.getElementById(id); 
    } 
    function send(file,index){
    	Ext.Ajax.request({
			url :'fileUpload.action',
			params :{
				file : file
			},
			method : 'post',
			callback : function(options,success,response){
				document.getElementById('sendMsg'+index).innerHTML = "文件上传成功";
				var f = parent.document.getElementById('sendFiles');
				f.innerHTML = f.innerHTML + '&nbsp;&nbsp;' + file.substring(file.lastIndexOf('\\')+1);
			}
    	});
    }
    </script>
<body>
<form id="form">
	<fieldset style="border:1pxsolid#84A24A;text-align:left;COLOR:#84A24A;FONT-SIZE: 12px;font-family:Verdana;padding:5px;">
	 <legend></legend> 
	 <input type="button"  class='btn' value="添加文件"id="btnAdd"onclick="selectAttachment();">&nbsp;
	 <input type="button"  class='btn' value="清空文件"id="btnClear"style="display:none"onclick="clearAttachment();"> 
	 <div id="attachmentList"style="margin:3px0px0px0px;padding:4px3px4px3px;background-color:#DEEBC6;display:none;border:1pxsolid#84A24A;"> </div> 
	 <div id="total"style="margin:3px0px0px0px;">当前选择上传0个文件</div> </fieldset> 
	 <!-- //<input type="submit" name="submit" > -->
</form>
</body>
</html>