Ext.QuickTips.init();
Ext.define('erp.controller.oa.officialDocument.fileManagement.NoFile', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
     		'oa.officialDocument.fileManagement.noFile.Viewport','common.datalist.GridPanel','common.datalist.Toolbar',
     		'oa.officialDocument.fileManagement.noFile.Form',
     		'core.trigger.DbfindTrigger','core.form.ConDateField','core.form.WordSizeField','oa.mail.MailPaging'
     	],
    init:function(){
    	var me = this;
    	this.control({
    		'erpDatalistGridPanel': {
    			afterrender: function(grid){
    				grid.onGridItemClick = function(){//改为点击button进入详细界面
    					me.onGridItemClick(grid.selModel.lastSelected);
    				};
    			}
    		},
    		'button[id=delete]': {
    			click: function(){
    				me.vastDelete();
    			}
    		},
    		'button[id=file]': {
    			click: function(){
    				var who = getUrlParam('whoami');
    				var tablename = '';
    				var field = new Array();
    				if(contains(who, 'Send', true)){
    		    		tablename = 'SendOfficialDocument';
    		    		field[0] = 'sod_isfile';
    		    		field[1] = 'sod_id';
    				} else if(contains(who, 'Receive', true)){
    		    		tablename = 'ReceiveOfficialDocument';
    		    		field[0] = 'rod_isfile';
    		    		field[1] = 'rod_id';
    				} else if(contains(who, 'Instruction', true)){
    		    		tablename = 'Instruction';
    		    		field[0] = 'in_isfile';
    		    		field[1] = 'in_id';
    				}
    				me.newFile(tablename);
//    				me.vastFile(tablename, field);
    			}
    		},
    		'button[id=rod]': {
    			click: function(){
    				var condition = "urlcondition=rod_statuscode='OVERED' AND rod_isfile=0";
    				var path = window.location.href.toString().split('?');
    				window.location.href = path[0] + '?whoami=File!ReceiveOfficialDocument&' + condition;
    			}
    		},
    		'button[id=sod]': {
    			click: function(){
    				var condition = "urlcondition=sod_statuscode='OVERED' AND sod_isfile=0";
    				var path = window.location.href.toString().split('?');
    				window.location.href = path[0] + '?whoami=File!SendOfficialDocument&' + condition;
    			}
    		},
    		'button[id=in]': {
    			click: function(){
    				var condition = "urlcondition=in_statuscode='OVERED' AND in_isfile=0";
    				var path = window.location.href.toString().split('?');
    				window.location.href = path[0] + '?whoami=File!Instruction&' + condition;
    			}
    		},
    	});
    },
    onGridItemClick: function(record){//grid行选择
//    	console.log(record);
    	var me = this;
    	var who = getUrlParam('whoami');
    	var path = '';
    	var id = 0;
    	var title = '';
    	if(contains(who, 'Send', true)){
    		id = record.data.sod_id;
    		title = record.data.sod_title;
			path = 'jsps/oa/officialDocument/sendODManagement/sodDetail.jsp';
		} else if(contains(who, 'Receive', true)){
    		id = record.data.rod_id;
    		title = record.data.rod_title;
			path = 'jsps/oa/officialDocument/receiveODManagement/rodDetail.jsp';
		} else if(contains(who, 'Instruction', true)){
    		id = record.data.in_id;
    		title = record.data.in_title;
			path = 'jsps/oa/officialDocument/instruction/instructionDetail.jsp';
		}
    	var panel = Ext.getCmp(who + id); 
    	var main = parent.Ext.getCmp("content-panel");
    	if(!panel){ 
	    	panel = { 
	    			title : "文件查看",
	    			tag : 'iframe',
	    			tabConfig:{tooltip: title},
	    			frame : true,
	    			border : false,
	    			layout : 'fit',
	    			iconCls : 'x-tree-icon-tab-tab1',
	    			html : '<iframe id="iframe_' + who + id + '" src="' + basePath + path + "?flag=query&id=" + id + '" height="100%" width="100%" frameborder="0" scrolling="yes"></iframe>',
	    			closable : true,
	    			listeners : {
	    				close : function(){
	    			    	main.setActiveTab(main.getActiveTab().id); 
	    				}
	    			} 
	    	};
	    	me.FormUtil.openTab(panel, who + id); 
    	}else{ 
	    	main.setActiveTab(panel); 
    	} 
    },
    vastDelete: function(){
    	var grid = Ext.getCmp('grid');
		var records = grid.selModel.getSelection();
		if(records.length > 0){
			var id = new Array();
			Ext.each(records, function(record, index){
				id[index] = record.data[keyField];
			});
			var main = parent.Ext.getCmp("content-panel");
			main.getActiveTab().setLoading(true);//loading...
			Ext.Ajax.request({
		   		url : basePath + 'common/vastDelete.action',
		   		params: {
		   			caller: caller,
		   			id: id
		   		},
		   		method : 'post',
		   		callback : function(options,success,response){
		   			main.getActiveTab().setLoading(false);
		   			var localJson = new Ext.decode(response.responseText);
		   			if(localJson.exceptionInfo){
		   				showError(localJson.exceptionInfo);
		   				return "";
		   			}
	    			if(localJson.success){
		   				Ext.Msg.alert("提示", "删除成功!", function(){
		   					window.location.href = window.location.href;
		   				});
		   			}
		   		}
			});
		}
    },
    newFile: function(tablename){
    	var grid = Ext.getCmp('grid');
		var records = grid.selModel.getSelection();
		if(records.length == 1){
			var id = records[0].data[keyField];
			var win = new Ext.window.Window({
				id : 'win',
				title: "添加档案",
				height: "80%",
				width: "90%",
				maximizable : false,
				buttonAlign : 'left',
				layout : 'anchor',
				items: [{
					tag : 'iframe',
					frame : true,
					anchor : '100% 100%',
					layout : 'fit',
					html : '<iframe id="iframe_' + new Date() + '" src="' + basePath + 'jsps/oa/officialDocument/fileManagement/newFile.jsp?odtype=' + tablename + '&odid=' + id + '" height="100%" width="100%" frameborder="0" scrolling="yes"></iframe>'
				}]
			});
			win.show();
		}
    },
    vastFile: function(tablename, field){
    	var grid = Ext.getCmp('grid');
		var records = grid.selModel.getSelection();
		if(records.length > 0){
			var id = new Array();
			Ext.each(records, function(record, index){
				id[index] = record.data[keyField];
			});
			var main = parent.Ext.getCmp("content-panel");
			main.getActiveTab().setLoading(true);//loading...
			Ext.Ajax.request({
		   		url : basePath + 'oa/officialDocument/vastFile.action',
		   		params: {
		   			tablename: tablename,
		   			field: field,
		   			id: id
		   		},
		   		method : 'post',
		   		callback : function(options,success,response){
		   			main.getActiveTab().setLoading(false);
		   			var localJson = new Ext.decode(response.responseText);
		   			if(localJson.exceptionInfo){
		   				showError(localJson.exceptionInfo);
		   				return "";
		   			}
	    			if(localJson.success){
		   				Ext.Msg.alert("提示", "归档成功!", function(){
		   					window.location.href = window.location.href;
		   				});
		   			}
		   		}
			});
		}
    }

});