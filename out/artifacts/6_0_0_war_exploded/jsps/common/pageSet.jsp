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
<script type="text/javascript" src="<%=basePath %>app/view/core/trigger/DbfindTrigger.js"></script>
<script type="text/javascript">
//demo
Ext.onReady(function(){
	var drawComponent = Ext.create('Ext.draw.Component', {    
		viewBox: false,    
		items: [{
			id: 'dataDictionary',
			type: 'circle',
			fill: 'red',        
			radius: 10,        
			x: 200,        
			y: 100,
			listeners: {
		        click: function(){
		        	var table = Ext.getCmp('fo_table').value;
		        	table = table == null ? '' : table.toUpperCase();
		            openWindow('数据字典维护', "jsps/ma/dataDictionary.jsp?formCondition=dd_tablenameIS'"
		            	 + table + "'&gridCondition=ddd_tablenameIS'" + table + "'");
		        }
		    }
		},{       
			id: 'dataDictionary_text',
			type: 'text',
			text: '数据字典维护',
			fill: 'red',
			x: 180,        
			y: 120,
			listeners: {
		        click: function(){
		        	var table = Ext.getCmp('fo_table').value;
		        	table = table == null ? '' : table.toUpperCase();
		        	openWindow('数据字典维护', "jsps/ma/dataDictionary.jsp?formCondition=dd_tablenameIS'"
		            	 + table + "'&gridCondition=ddd_tablenameIS'" + table + "'");
		        }
		    }
		},{        
			type: "path",        
			path: "M210 100 L520 100 Z",    //路径        
			"stroke-width": "1",        
			stroke: "#000",        
			fill: "blue"    
		},{     
			id: 'form',
			type: 'circle',                        
			fill: '#79BB3F',        
			radius: 10,        
			x: 280,        
			y: 100,
			listeners: {
		        click: function(){
		        	var id = Ext.getCmp('fo_id').value;
		        	if(id == null){
		        		openWindow('Form维护', "jsps/ma/form.jsp");
		        	} else {
		        		openWindow('Form维护', "jsps/ma/form.jsp?formCondition=fo_idIS"
		            	 	+ id + "&gridCondition=fd_foidIS" + id);
		        	}
		        }
		    }
		},{        
			type: "path",        
			path: "M290 100 L320 160 Z",    //路径        
			"stroke-width": "1",        
			stroke: "#000",        
			fill: "blue"    
		},{        
			type: "path",        
			path: "M290 100 L320 40 Z",    //路径        
			"stroke-width": "1",        
			stroke: "#000",        
			fill: "blue"    
		},{        
			type: "path",        
			path: "M320 160 L360 200 Z",    //路径        
			"stroke-width": "1",        
			stroke: "#000",        
			fill: "blue"    
		},{   
			id: 'form_text',
			type: 'text',
			text: 'Form维护',
			fill: '#79BB3F',
			x: 260,        
			y: 120,
			listeners: {
		        click: function(){
		        	var id = Ext.getCmp('fo_id').value;
		        	if(id == null){
		        		openWindow('Form维护', "jsps/ma/form.jsp");
		        	} else {
		        		openWindow('Form维护', "jsps/ma/form.jsp?formCondition=fo_idIS"
		            	 	+ id + "&gridCondition=fd_foidIS" + id);
		        	}
		        }
		    }
		},{      
			id: 'detailgrid_text',
			type: 'text',
			text: 'Form维护--从表detailgrid',
			fill: '#79BB3F',
			x: 330,        
			y: 160,
			listeners: {
		        click: function(){
		        	openWindow('Form维护--从表detailgrid', 'jsps/ma/detailGrid.jsp');
		        }
		    }
		},{       
			id: 'detailgrid',
			type: 'circle',                        
			fill: '#79BB3F',        
			radius: 6,        
			x: 320,        
			y: 160,
			listeners: {
		        click: function(){
		        	openWindow('Form维护--从表detailgrid', 'jsps/ma/detailGrid.jsp');
		        }
		    }
		},{        
			id: 'dbfindSet_text',
			type: 'text',
			text: 'Form维护--从表detailgrid--dbfind',
			fill: '#79BB3F',
			x: 370,        
			y: 200,
			listeners: {
		        click: function(){
		        	openWindow('Form维护--从表detailgrid--dbfind', 'jsps/ma/dbFindSet.jsp');
		        }
		    }
		},{       
			id: 'dbfindSet',
			type: 'circle',                        
			fill: '#79BB3F',        
			radius: 6,        
			x: 360,        
			y: 200,
			listeners: {
		        click: function(){
		        	openWindow('Form维护--从表detailgrid--dbfind', 'jsps/ma/dbFindSet.jsp');
		        }
		    }
		},{        
			id: 'dbfindSetUI_text',
			type: 'text',
			text: 'Form维护--dbfind',
			fill: '#79BB3F',
			x: 330,        
			y: 40,
			listeners: {
		        click: function(){
		        	openWindow('Form维护--dbfind', 'jsps/ma/dbFindSetUI.jsp');
		        }
		    }
		},{        
			id: 'formCombo_text',
			type: 'text',
			text: 'Form维护--combo',
			fill: '#79BB3F',
			x: 190,        
			y: 40,
			listeners: {
		        click: function(){
		        	openWindow('Form维护--combo', 'jsps/ma/dataListCombo.jsp');
		        }
		    }
		},{        
			type: "path",        
			path: "M270 100 L180 40 Z",    //路径        
			"stroke-width": "1",        
			stroke: "#000",        
			fill: "blue"    
		},{      
			id: 'formCombo',
			type: 'circle',                        
			fill: '#79BB3F',        
			radius: 6,        
			x: 180,        
			y: 40,
			listeners: {
		        click: function(){
		        	openWindow('Form维护--combo', 'jsps/ma/dataListCombo.jsp');
		        }
		    }
		},{      
			id: 'dbfindSetUI',
			type: 'circle',                        
			fill: '#79BB3F',        
			radius: 6,        
			x: 320,        
			y: 40,
			listeners: {
		        click: function(){
		        	openWindow('Form维护--从表detailgrid--dbfind', 'jsps/ma/dbFindSetUI.jsp');
		        }
		    }
		},{        
			id: 'detailgridCombo',
			type: 'circle',                        
			fill: '#79BB3F',        
			radius: 6,        
			x: 240,        
			y: 230,
			listeners: {
		        click: function(){
		        	openWindow('Form维护--从表detailgrid--combo', 'jsps/ma/dataListCombo.jsp');
		        }
		    }
		},{        
			type: "path",        
			path: "M320 165 L240 225 Z",    //路径        
			"stroke-width": "1",        
			stroke: "#000",        
			fill: "blue"    
		},{   
			id: 'detailgridCombo_text',
			type: 'text',
			text: 'Form维护--从表detailgrid--combo',
			fill: '#79BB3F',
			x: 250,        
			y: 230,
			listeners: {
		        click: function(){
		        	openWindow('datalist维护', 'jsps/ma/dataListCombo.jsp');
		        }
		    }
		},{    
			id: 'datalist',
			type: 'circle',                        
			fill: '#79BB3F',        
			radius: 10,        
			x: 360,        
			y: 100,
			listeners: {
		        click: function(){
		        	openWindow('datalist维护', 'jsps/ma/dataList.jsp');
		        }
		    }
		},{      
			id: 'datalist_text',
			type: 'text',
			text: 'datalist维护',
			fill: '#79BB3F',
			x: 340,        
			y: 120,
			listeners: {
		        click: function(){
		        	openWindow('datalist维护', 'jsps/ma/dataList.jsp');
		        }
		    }
		},{        
			id: 'documentSetup',
			type: 'circle',                        
			fill: '#79BB3F',        
			radius: 10,        
			x: 440,        
			y: 100,
			listeners: {
		        click: function(){
		        	openWindow('逻辑设计', 'jsps/ma/documentSetup.jsp');
		        }
		    }
		},{    
			id: 'documentSetup_text',
			type: 'text',
			text: '逻辑设计',
			fill: '#79BB3F',
			x: 420,        
			y: 120,
			listeners: {
		        click: function(){
		        	openWindow('逻辑设计', 'jsps/ma/documentSetup.jsp');
		        }
		    }
		},{        
			id: 'sysNavigation',
			type: 'circle',                        
			fill: '#79BB3F',        
			radius: 10,        
			x: 520,        
			y: 100,
			listeners: {
		        click: function(){
		        	openWindow('tree链接维护', 'jsps/ma/sysNavigation.jsp');
		        }
		    }
		},{    
			id: 'sysNavigation_text',    
			type: 'text',
			text: 'tree链接维护',
			fill: '#79BB3F',
			x: 500,        
			y: 120,
			listeners: {
		        click: function(){
		        	openWindow('tree链接维护', 'jsps/ma/sysNavigation.jsp');
		        }
		    }
		},{   
			id: 'table', 
			type: 'text',
			fill: 'blue',
			x: 520,        
			y: 20
		},{   
			id: 'caller', 
			type: 'text',
			fill: 'blue',
			x: 520,        
			y: 50
		}]
	}); 
	var form = Ext.create('Ext.form.Panel', {
	    title: '<font color=gray>筛选</font>',
	    bodyPadding: 5,
	    style: 'background-color: #f1f1f1;cursor: pointer;',
	    width: 350,
	    url: basePath + 'common/getPageSet.action',
	    defaultType: 'textfield',
	    items: [{
	        fieldLabel: 'Table',
	        name: 'fo_table',
	        xtype: 'dbfindtrigger',
	        id: 'fo_table'
	    },{
	        fieldLabel: 'Caller',
	        name: 'fo_caller',
	        xtype: 'dbfindtrigger',
	        id: 'fo_caller'
	    },{
	        name: 'fo_id',
	        xtype: 'hidden',
	        id: 'fo_id'
	    }],
	    buttonAlign: 'center',
	    buttons: [{
	        text: '重  置',
	        handler: function() {
	            this.up('form').getForm().reset();
	        }
	    }, {
	        text: '查  询',
	        handler: function() {
	            var form = this.up('form').getForm();
	            if (form.isValid()) {
	            	var main = parent.Ext.getCmp("content-panel");
	            	main.getActiveTab().setLoading(true);
	                form.submit({
	                    success: function(form, action) {
	                    	main.getActiveTab().setLoading(false);
	                       var res = action.result;
	                       Ext.each(drawComponent.surface.items.items, function(){
	                       		if(this.id == 'dataDictionary' || this.id == 'dataDictionary_text'){
	                       			if(res.dataDictionary){
	                       				this.setAttributes({fill: 'red'}, true);
	                       			} else {
	                       				this.setAttributes({fill: '#79BB3F'}, true);
	                       			}
	                       		}
	                       		if(this.id == 'form' || this.id == 'form_text'){
	                       			if(res.form){
	                       				this.setAttributes({fill: 'red'}, true);
	                       			} else {
	                       				this.setAttributes({fill: '#79BB3F'}, true);
	                       			}
	                       		}
	                       		if(this.id == 'formCombo' || this.id == 'formCombo_text'){
	                       			if(res.formCombo){
	                       				this.setAttributes({fill: 'red'}, true);
	                       			} else {
	                       				this.setAttributes({fill: '#79BB3F'}, true);
	                       			}
	                       		}
	                       		if(this.id == 'detailgrid' || this.id == 'detailgrid_text'){
	                       			if(res.detailgrid){
	                       				this.setAttributes({fill: 'red'}, true);
	                       			} else {
	                       				this.setAttributes({fill: '#79BB3F'}, true);
	                       			}
	                       		}
	                       		if(this.id == 'detailgridCombo' || this.id == 'detailgridCombo_text'){
	                       			if(res.detailgridCombo){
	                       				this.setAttributes({fill: 'red'}, true);
	                       			} else {
	                       				this.setAttributes({fill: '#79BB3F'}, true);
	                       			}
	                       		}
	                       		if(this.id == 'dbfindSet' || this.id == 'dbfindSet_text'){
	                       			if(res.dbfindSet){
	                       				this.setAttributes({fill: 'red'}, true);
	                       			} else {
	                       				this.setAttributes({fill: '#79BB3F'}, true);
	                       			}
	                       		}
	                       		if(this.id == 'dbfindSetUI' || this.id == 'dbfindSetUI_text'){
	                       			if(res.dbfindSetUI){
	                       				this.setAttributes({fill: 'red'}, true);
	                       			} else {
	                       				this.setAttributes({fill: '#79BB3F'}, true);
	                       			}
	                       		}
	                       		if(this.id == 'datalist' || this.id == 'datalist_text'){
	                       			if(res.datalist){
	                       				this.setAttributes({fill: 'red'}, true);
	                       			} else {
	                       				this.setAttributes({fill: '#79BB3F'}, true);
	                       			}
	                       		}
	                       		if(this.id == 'documentSetup' || this.id == 'documentSetup_text'){
	                       			if(res.documentSetup){
	                       				this.setAttributes({fill: 'red'}, true);
	                       			} else {
	                       				this.setAttributes({fill: '#79BB3F'}, true);
	                       			}
	                       		}
	                       		if(this.id == 'sysNavigation' || this.id == 'sysNavigation_text'){
	                       			if(res.sysNavigation){
	                       				this.setAttributes({fill: 'red'}, true);
	                       			} else {
	                       				this.setAttributes({fill: '#79BB3F'}, true);
	                       			}
	                       		}
	                       		if(this.id == 'table'){
	                       			this.setAttributes({text: 'Table:  ' + Ext.getCmp('fo_table').value}, true);
	                       		}
	                       		if(this.id == 'caller'){
	                       			this.setAttributes({text: 'Caller:  ' + Ext.getCmp('fo_caller').value}, true);
	                       		}
	                       });
	                    },
	                    failure: function(form, action) {
	                    	main.getActiveTab().setLoading(false);
	                        showError(action.result.exceptionInfo);
	                    }
	                });
	            }
	        }
	    }]
	});
	Ext.create('Ext.panel.Panel', {    
		width: '100%',    
		height: '100%',    
		layout: 'fit',    
		title: '<font color=gray>设计您的页面</font>',
		style: 'background-color: #f1f1f1;cursor: pointer;',
		renderTo: Ext.getBody(),
		items: [{
			layout: 'border', 
			items: [{
				region: 'center', 
				layout: 'fit',
				width: '60%',
				items: [drawComponent]
			},{
				region: 'east',
				width: '40%',
				items: [form]
			}]
		}]
	});
	function openTable(title, url){
		var panel = Ext.getCmp(url.substring(0, url.lastIndexOf('.jsp'))); 
		var main = parent.Ext.getCmp("content-panel");
		if(!panel){ 
			var t = title;
	    	if (title.toString().length>4) {
	    		 t = title.toString().substring(title.toString().length-4);	
	    	}
	    	panel = { 
	    			title : $I18N.common.msg.title_info + '(' + t + ')',
	    			tag : 'iframe',
	    			tabConfig:{tooltip: title},
	    			frame : true,
	    			border : false,
	    			layout : 'fit',
	    			iconCls : 'x-tree-icon-tab-tab',
	    			html : '<iframe id="iframe_maindetail_pageSet" src="' + basePath + url + '" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>',
	    			closable : true,
	    			listeners : {
	    				close : function(){
	    			    	main.setActiveTab(main.getActiveTab().id); 
	    				}
	    			} 
	    	};
			var p = main.add(panel); 
			main.setActiveTab(p);
		}else{ 
	    	main.setActiveTab(panel); 
		} 
	}
	function openWindow(title, url){
		var win = new Ext.window.Window({
	    	id : 'win',
			title: title,
			height: "100%",
			width: "80%",
			maximizable : true,
			buttonAlign : 'center',
			layout : 'anchor',
			items: [{
			    	  tag : 'iframe',
			    	  frame : true,
			    	  anchor : '100% 100%',
			    	  layout : 'fit',
			    	  html : '<iframe id="iframe" src="' + basePath+url + '" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>'
			}]
		});
		win.show();
	}
});
</script>
</head>
<body >
</body>
</html>