<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html>
<html>
<head>
    <title>工作日历</title>
    <link href="<%=basePath %>resource/gnt/resources/css/ext-all-gray.css" rel="stylesheet" type="text/css" />
    <link rel="stylesheet" type="text/css" href="<%=basePath %>resource/gnt/resources/resources/css/calendar.css" />
     <link rel="stylesheet" type="text/css" href="<%=basePath %>resource/gnt/resources/resources/css/examples.css" />
   <script src="<%=basePath %>resource/gnt/ext-all.js" type="text/javascript"></script>
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
    	var EventsData='';
    	var type='factory';
        Ext.onReady(function(){
        	Ext.Ajax.request({//拿到grid的columns
            	url : basePath + 'plm/calendar/getMyData.action',
            	params:{
            	  emid:emid
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
    </script>
     <script type="text/javascript">
    var recorder = '<%=session.getAttribute("em_name")%>';
    var emid='<%=session.getAttribute("em_uu")%>';
    var basePath="<%=basePath%>";
    </script>
</head>
<body>
    <div style="display:none;">
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