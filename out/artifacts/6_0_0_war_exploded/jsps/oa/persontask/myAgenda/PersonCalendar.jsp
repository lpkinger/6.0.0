<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html>
<html>
<head>
<title>日程安排</title>

<link href="<%=basePath %>resource/gnt/resources/css/ext-all-gray.css"
	rel="stylesheet" type="text/css" />
	 <link href="<%=basePath %>resource/css/main.css" rel="stylesheet" type="text/css" />
<link rel="stylesheet" type="text/css"
	href="<%=basePath %>resource/gnt/resources/resources/css/calendar.css" />
<link rel="stylesheet" type="text/css"
	href="<%=basePath %>resource/gnt/resources/resources/css/examples.css" />
<script src="<%=basePath %>resource/gnt/ext-all.js"
	type="text/javascript"></script>
<script src="<%=basePath %>app/view/core/form/DateHourMinuteField.js"
	type="text/javascript"></script>
<script src="<%=basePath %>app/view/core/form/ConDateHourMinuteField.js"
	type="text/javascript"></script>
	
<script type="text/javascript" src="<%=basePath %>resource/i18n/i18n.js"></script>
<script type="text/javascript">
        Ext.Loader.setConfig({
            enabled: true,
            paths: {
                'Ext.calendar': 'src'
            }
        });
        Ext.require([
            'Ext.calendar.App'
        ]);
        var todo={
    			add:[],
    			update:[],
    			destroy:[]
    	};
    	var EventsData='', type='factory',emcode=getUrlParam('emcode'),mid=getUrlParam('mid'),PersonType=true,caller=getUrlParam('caller');
    	emcode=emcode==null?em_code:emcode;
        Ext.onReady(function(){
        	Ext.Ajax.request({//拿到grid的columns
            	url : basePath + 'plm/task/getAgendaData.action?_noc=1',
            	params:{
            	  emcode:emcode,
            	  /* condition:"pd_prjid="+mid */
            	},            	
            	method : 'post',
            	callback : function(options,success,response){
            		var res = new Ext.decode(response.responseText);
            		if(res.exceptionInfo){
            			showError(res.exceptionInfo);return;
            		}if(res.success){
            		     EventsData=res.evts;
            			 Ext.create('Ext.calendar.App');
            		}
            	}
        	});
            document.getElementById('logo-body').innerHTML = new Date().getDate();
        });
     /*    function mouseUp(a){
        	console.log(a);
        } */
    </script>
<script type="text/javascript">
    var basePath="<%=basePath%>";
    var menu=null;
    </script>
</head>
<body>
	<div style="display: none;">
		<div id="app-header-content">
			<div id="app-logo">
				<div class="logo-top">&nbsp;</div>
				<div id="logo-body">&nbsp;</div>
				<div class="logo-bottom">&nbsp;</div>
			</div>
			<h1>工作日历</h1>
			<span id="app-msg" class="x-hidden"></span>
		</div>
	</div>
</body>
</html>