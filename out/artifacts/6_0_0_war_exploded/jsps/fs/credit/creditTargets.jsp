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
<link rel="stylesheet" href="<%=basePath%>resource/ext/4.2/resources/ext-theme-gray/ext-theme-gray-all.css" type="text/css"/>
<script type="text/javascript" src="<%=basePath %>resource/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/i18n/i18n.js"></script>
<script type="text/javascript">
	
	String.prototype._split = function(regexp) {
		if (regexp instanceof RegExp) {
			var arr = this.split(""), _arr = [], temp = '', isStr = false;
			for ( var i in arr) {
				// 字符串不拆
				if (arr[i] == "'") {
					if (temp.length > 0) {
						_arr.push("'" + temp + "'");
						isStr = false;
					} else
						isStr = true;
					temp = '';
				} else if (isStr){
					temp += arr[i];
				} else {
					if (regexp.test(arr[i])) {
						if (temp.length > 0)
							_arr.push(temp);
						_arr.push(arr[i]);
						temp = '';
					} else
						temp += arr[i];
				}
			}
			if (temp.length > 0)
				_arr.push(temp);
			return _arr;
		}
		return this.split(regexp);
	};
	
	//is number(Ext.isNumber doesn't work)
	var isNumber = function(n) {
		return !isNaN(parseFloat(n)) && isFinite(n);
	};
	
	Ext.Loader.setConfig({
		enabled: true
	});//开启动态加载
	
	Ext.application({
	    name: 'erp',//为应用程序起一个名字,相当于命名空间
	    appFolder: basePath+'app',//app文件夹所在路径
	    controllers: [//声明所用到的控制层
	        'fs.credit.CreditTargets'
	    ],
	    launch: function() {
	        Ext.create('erp.view.fs.credit.CreditTargets');
	    }
	});
	
	var caller='CreditTargets';
	var height = window.innerHeight;
	if(Ext.isIE){//ie不支持window.innerHeight;document.documentElement.clientHeight == 0
		height = screen.height*0.73;
	}
</script>
</head>
<body >
</body>
</html>