<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link rel="stylesheet" href="<%=basePath %>resource/ext/resources/css/ext-all-gray.css" type="text/css"></link>
<!--[if IE]>
	<link rel="stylesheet" href="<%=basePath %>resource/ext/resources/css/ext-ie-scoped.css" type="text/css"></link>
<![endif]-->
<link rel="stylesheet" href="<%=basePath %>resource/css/main.css" type="text/css"></link>
<script type="text/javascript" src="<%=basePath %>resource/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/i18n/i18n.js"></script>
<script type="text/javascript">	
Ext.Loader.setConfig({
	enabled: true
});//开启动态加载
Ext.application({
    name: 'erp',//为应用程序起一个名字,相当于命名空间
    appFolder: basePath+'app',//app文件夹所在路径
    controllers: [//声明所用到的控制层
        'oa.persontask.workPlan.See'
    ],
    launch: function() {
    	Ext.create('erp.view.oa.persontask.workPlan.See');//创建视图
    }
});
//var caller = 'WorkPlanType';
//var formCondition = '';
//var gridCondition = '';
var value = '';
Ext.Ajax.request({//查询当月计划是否存在
	url : basePath + 'oa/persontask/workPlan/queryWorkPlan.action',
	method : 'post',
	params:{
		title: '吴伟的' + new Date().getFullYear() + '年' + ((new Date().getMonth()+1) > 9 ? (new Date().getMonth()+1):'0'+(new Date().getMonth()+1)) +'月个人计划'
	},
	async: false,
	callback : function(options,success,response){
		var rs = new Ext.decode(response.responseText);
		if(rs.exceptionInfo){
			showError(rs.exceptionInfo);return;
		}
		if(rs.success){
			if(rs.workplan){//当月计划存在且不为空，则列出当月计划条目
				Ext.Ajax.request({
			   		url : basePath + 'oa/persontask/workPlan/getWorkPlanDetail.action',
			   		params : {
			   			id: rs.workplan.wp_id
			   		},
			   		method : 'post',
			   		async: false,
			   		callback : function(options,success,response){
//			   			me.getActiveTab().setLoading(false);
			   			var res = new Ext.decode(response.responseText);
		    			if(res.success && res.workplandetaillist.length>0){//当月计划条目存在且不为空
		    				for(var i=0; i<res.workplandetaillist.length; i++){
		    					if(i==res.workplandetaillist.length-1){
		    						value += res.workplandetaillist[i].wpd_plan;	    						
		    					} else {
		    						value += res.workplandetaillist[i].wpd_plan + '==###==';	 
		    					}		    					
		    				}		    				
			   			} else{
			   				saveFailure();//@i18n/i18n.js
			   			}
			   		}		   		
				});
			}
		}
	}
});
</script>
</head>
<body >
</body>
</html>