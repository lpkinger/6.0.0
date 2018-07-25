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
<script type="text/javascript" src="<%=basePath %>resource/ext/ext-all.js"></script>
<style>
	#nc_content p {
	    font-size: 14px;
	    color: #626262;
	    text-indent: 2em;
	    margin: 32px 0;
    }
</style>
</head>
<body>
<script type="text/javascript">
	var basePath = (function() {
		var fullPath = window.document.location.href;
		var path = window.document.location.pathname;
		var subpos = fullPath.indexOf('//');
		var subpath = subpos > -1 ? fullPath.substring(0, subpos + 2) : '';
		if (subpos > -1)
			fullPath = fullPath.substring(subpos + 2);
		var pos = fullPath.indexOf(path), sname = path.substring(0, path.substr(1).indexOf('/') + 1);
		sname = (['/jsps','/workfloweditor','/resource','/system','/process','/demo','/exam','/oa','/opensys','/mobile'].indexOf(sname) > -1 ? '/' : sname);
		return subpath + fullPath.substring(0, pos) + sname + (sname == '/' ? '' : '/');
	})();
	
	var id = '${param.id}';
	var opener = window.opener;
	if(opener){
		var div = document.createElement("div");
		var val = opener.Ext.getCmp(id).getValue();
		var html = '<div style="padding:10px 0px;height:100%;position: absolute;left: 50%;top:-8px;transform: translate(-50%,0);"><div id = "' + id + '" style="width:900px;height:100%;border:1px solid #000;overflow:auto;">'+val+'</div></div>'
		div.innerHTML = html;
		document.body.appendChild(div);
	}else if (parseInt(id)!='NaN'){
		Ext.Ajax.request({
    		url: basePath + 'public/getNewsHtml.action',
    	   	params: {
	    		id: id
    	   	},
	    	callback: function(opt, s, r) {
	    		var res = Ext.decode(r.responseText);
		   		if(res.exceptionInfo) {
		   			showError(res.exceptionInfo);
		   		} else if (res.success){
		   			var div = document.createElement("div");
		   			div.innerHTML = '<div id = "' + id + '">'+res.newshtml+'</div>';
		   			document.body.appendChild(div);
		   		}
   			}
    	});
	}
</script>
</body>
</html>