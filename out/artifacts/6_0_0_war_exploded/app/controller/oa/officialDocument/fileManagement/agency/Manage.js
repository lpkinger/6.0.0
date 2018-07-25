Ext.QuickTips.init();
Ext.define('erp.controller.oa.officialDocument.fileManagement.agency.Manage', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
     		'oa.officialDocument.fileManagement.agency.Viewport','common.datalist.GridPanel','common.datalist.Toolbar',
     		'oa.officialDocument.fileManagement.agency.Form',
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
    		'button[id=add]': {
    			click: function(){
    				me.newAgency();
    			}
    		}
    	});
    },
    newAgency: function(){
    	var win = new Ext.window.Window({
			id : 'win',
			title: "机构问题",
			height: "50%",
			width: "50%",
			maximizable : false,
			buttonAlign : 'left',
			layout : 'anchor',
			items: [{
				tag : 'iframe',
				frame : true,
				anchor : '100% 100%',
				layout : 'fit',
				html : '<iframe id="iframe_' + new Date() + '" src="' + basePath + 'jsps/oa/officialDocument/fileManagement/agency/new.jsp" height="100%" width="100%" frameborder="0" scrolling="yes"></iframe>'
			}]
		});
    	win.show();	
    },
    onGridItemClick: function(record){//grid行选择
    	console.log(record);
    	var me = this;
    	if(record.data.rod_statuscode == 'OVERED'){
    		var id = record.data.rod_id;
    		var panel = Ext.getCmp("mrod" + id); 
    		var main = parent.Ext.getCmp("content-panel");
    		if(!panel){ 
    			var title = "收文分发";
    			panel = { 
    					title : title,
    					tag : 'iframe',
    					tabConfig:{tooltip: record.data['rod_title']},
    					frame : true,
    					border : false,
    					layout : 'fit',
    					iconCls : 'x-tree-icon-tab-tab1',
    					html : '<iframe id="iframe_' + id + '" src="' + basePath + "jsps/oa/officialDocument/receiveODManagement/rodDetail.jsp?flag=manage&id=" + id + '" height="100%" width="100%" frameborder="0" scrolling="yes"></iframe>',
    					closable : true,
    					listeners : {
    						close : function(){
    							main.setActiveTab(main.getActiveTab().id); 
    						}
    					} 
    			};
    			me.FormUtil.openTab(panel, "mrod" + id); 
    		}else{ 
    			main.setActiveTab(panel); 
    		}   		
    	} else {
    		return;
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
    }

});