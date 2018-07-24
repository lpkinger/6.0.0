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
<link rel="stylesheet" href="<%=basePath %>resource/css/main.css" type="text/css"></link>
<script type="text/javascript" src="<%=basePath %>resource/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/i18n/i18n.js"></script>
<script type="text/javascript">	
Ext.Loader.setConfig({
	enabled: true
});//开启动态加载
var store=null;
var columns=null;
var fields=null;
var data=null;
var caller='';
    Ext.Ajax.request({//拿到grid的columns
          url : basePath + 'plm/resource/Analysegrid.action',
          async:false, 
          params:{
            condition:""
          },
          method : 'post',
          callback : function(options,success,response){
          var res = new Ext.decode(response.responseText);
        		if(res.success){
        		 fields=res.fields;
               data=res.data
               columns=res.columns;
              store= Ext.create('Ext.data.Store', {
                 fields: res.fields,            
                  data:data
               });
        		}else if(res.exceptionInfo){
        			showError(res.exceptionInfo);return;
        		}
          }
          })
Ext.application({
    name: 'erp',//为应用程序起一个名字,相当于命名空间
    appFolder: basePath+'app',//app文件夹所在路径
    controllers: [//声明所用到的控制层
        'plm.resource.Analyse'
    ],
    launch: function() {
    	Ext.create('erp.view.plm.resource.Analyse');//创建视图
    }
});
</script>
</head>
<body >
</body>
</html>