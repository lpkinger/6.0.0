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
<link rel="stylesheet" type="text/css" href="<%=basePath %>resource/ext/resources/css/ext-all-gray.css"/>
<link rel="stylesheet" href="<%=basePath %>resource/css/main.css" type="text/css"></link>
<script type="text/javascript" src="<%=basePath %>resource/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/i18n/i18n.js"></script>
<style type="text/css">
.x-button-icon-upload {
	background-image: url('../../resource/images/icon/upload.png')
}
.x-button-icon-download {
	background-image: url('../../resource/images/icon/download.png')
}
.x-button-icon-match {
	background-image: url('../../resource/images/16/find.png')
}
.x-form-display-field {
    font-size: 14px;
    color: red;
}
.x-form-item-label{
   padding: 3px 0 0 0;
} 
.x-column-header {
    background-color: #c8c8c8;
    background-image: -webkit-gradient(linear, 50% 0%, 50% 100%, color-stop(0%, #f0f0f0),
 	color-stop(100%, #c8c8c8));
    background-image: -webkit-linear-gradient(top, #f0f0f0, #c8c8c8);
    background-image: -moz-linear-gradient(top, #f0f0f0, #c8c8c8);
    background-image: -o-linear-gradient(top, #f0f0f0, #c8c8c8);
    background-image: -ms-linear-gradient(top, #f0f0f0, #c8c8c8);
    background-image: linear-gradient(top, #f0f0f0, #c8c8c8);
    /* text-align: center; */
}
.x-btn {
    background: #d5d5d5;
    background: -moz-linear-gradient(top, #fff 0, #efefef 38%, #d5d5d5 88%);
    background: -webkit-gradient(linear, left top, left bottom, color-stop(0%, #fff),
 	color-stop(38%, #efefef), color-stop(88%, #d5d5d5));
    filter: progid:DXImageTransform.Microsoft.gradient(startColorstr='#fff',
 	endColorstr='#d5d5d5', GradientType=0);
    border-color: #bfbfbf;
    border-radius: 2px;
    vertical-align: bottom;
    text-align: center;
}
#fileUploadform-body {
    background-color: rgb(216, 216, 216);
    background-image: -webkit-linear-gradient(top, rgb(230, 230, 230), rgb(239, 239, 239));
    border-color: rgb(208, 208, 208);
}



</style>
<script type="text/javascript">
Ext.Loader.setConfig({
	enabled: true
});//开启动态加载
Ext.application({
    name: 'erp',//为应用程序起一个名字,相当于命名空间
    appFolder: basePath+'app',//app文件夹所在路径
    controllers: [//声明所用到的控制层
        'common.MultiFileUpload'
    ],
    launch: function() {
        Ext.create('erp.view.common.multiFileUpload.Viewport');
    }
});

var caller = "multiFileUpload";

</script>
</head>
<body>

</body>
</html>