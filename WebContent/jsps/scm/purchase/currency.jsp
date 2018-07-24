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
	Ext.onReady(function(){
		Ext.create('Ext.form.Panel', {
		    title: 'FieldContainer实现FormPanel分组Example',
		    id: 'form',
		    width: '100%',
		    bodyPadding: 10,
		    layout: 'column',
		    defaultType: 'textfield',
		    items: [{
		    	html: '<div onclick="javascript:collapse(1)" class="x-form-group-label"><h6>分组1</h6></div>',
		    	columnWidth: 1
		    },{
	            fieldLabel: 'First Name',
	            group: 1,
	            name: 'firstName',
	            columnWidth: 0.5
	        }, {
	            fieldLabel: 'Last Name',
	            group: 1,
	            name: 'lastName',
	            columnWidth: 0.5
	        }, {
	            fieldLabel: 'First Name',
	            group: 1,
	            name: 'firstName',
	            columnWidth: 0.5
	        }, {
	            fieldLabel: 'Last Name',
	            group: 1,
	            name: 'lastName',
	            columnWidth: 0.5
	        },{
		    	html: '<div onclick="javascript:collapse(2);" class="x-form-group-label"><h6>分组2</h6></div>',
		    	columnWidth: 1
		    },{
	            fieldLabel: 'First Name',
	            name: 'firstName',
	            group: 2,
	            columnWidth: 0.5
	        }, {
	            fieldLabel: 'Last Name',
	            name: 'lastName',
	            group: 2,
	            columnWidth: 0.5
	        }, {
	            fieldLabel: 'First Name',
	            name: 'firstName',
	            group: 2,
	            columnWidth: 0.5
	        }, {
	            fieldLabel: 'Last Name',
	            name: 'lastName',
	            group: 2,
	            columnWidth: 0.5
	        },{
		    	html: '<div onclick="javascript:collapse(3);" class="x-form-group-label"><h6>分组3</h6></div>',
		    	columnWidth: 1
		    },{
	            fieldLabel: 'First Name',
	            name: 'firstName',
	            group: 3,
	            columnWidth: 0.5
	        }, {
	            fieldLabel: 'Last Name',
	            name: 'lastName',
	            group: 3,
	            columnWidth: 0.5
	        }, {
	            fieldLabel: 'First Name',
	            name: 'firstName',
	            group: 3,
	            columnWidth: 0.5
	        }, {
	            fieldLabel: 'Last Name',
	            name: 'lastName',
	            group: 3,
	            columnWidth: 0.5
	        },{
		    	html: '<div onclick="javascript:collapse(4);" class="x-form-group-label"><h6>分组4</h6></div>',
		    	columnWidth: 1
		    },{
	            fieldLabel: 'First Name',
	            name: 'firstName',
	            group: 4,
	            columnWidth: 0.5
	        }, {
	            fieldLabel: 'Last Name',
	            name: 'lastName',
	            group: 4,
	            columnWidth: 0.5
	        }, {
	            fieldLabel: 'First Name',
	            name: 'firstName',
	            group: 4,
	            columnWidth: 0.5
	        }, {
	            fieldLabel: 'Last Name',
	            name: 'lastName',
	            group: 4,
	            columnWidth: 0.5
	        },{
		    	html: '<div onclick="javascript:collapse(5);" class="x-form-group-label"><h6>分组5</h6></div>',
		    	columnWidth: 1
		    },{
	            fieldLabel: 'First Name',
	            name: 'firstName',
	            group: 5,
	            columnWidth: 0.5
	        }, {
	            fieldLabel: 'Last Name',
	            name: 'lastName',
	            group: 5,
	            columnWidth: 0.5
	        }, {
	            fieldLabel: 'First Name',
	            name: 'firstName',
	            group: 5,
	            columnWidth: 0.5
	        }, {
	            fieldLabel: 'Last Name',
	            name: 'lastName',
	            group: 5,
	            columnWidth: 0.5
	        }],
		    renderTo: Ext.getBody()
		});
	});
	function collapse(id){
		Ext.each(Ext.getCmp('form').items.items, function(item){
			if(item.group && item.group == id){
				if(item.hidden == false){
					item.hide();
				} else {
					item.show();
				}
			}
		});
	}
</script>
</head>
<body >
</body>
</html>