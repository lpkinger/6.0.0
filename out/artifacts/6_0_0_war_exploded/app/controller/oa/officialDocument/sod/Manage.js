Ext.QuickTips.init();
Ext.define('erp.controller.oa.officialDocument.sod.Manage', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
     		'oa.officialDocument.sod.manage.Viewport','common.datalist.GridPanel','common.datalist.Toolbar',
     		'oa.officialDocument.sod.manage.Form',
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
    		}
    	});
    },
    onGridItemClick: function(record){//grid行选择
    	console.log(record);
    	var me = this;
    	if(record.data.sod_statuscode == 'OVERED'){
    		var id = record.data.sod_id;
    		var panel = Ext.getCmp("msod" + id); 
    		var main = parent.Ext.getCmp("content-panel");
    		if(!panel){ 
    			var title = "发文分发";
    			panel = { 
    					title : title,
    					tag : 'iframe',
    					tabConfig:{tooltip: record.data['sod_title']},
    					frame : true,
    					border : false,
    					layout : 'fit',
    					iconCls : 'x-tree-icon-tab-tab1',
    					html : '<iframe id="iframe_' + id + '" src="' + basePath + "jsps/oa/officialDocument/sendODManagement/sodDetail.jsp?flag=manage&id=" + id + '" height="100%" width="100%" frameborder="0" scrolling="yes"></iframe>',
    					closable : true,
    					listeners : {
    						close : function(){
    							main.setActiveTab(main.getActiveTab().id); 
    						}
    					} 
    			};
    			me.FormUtil.openTab(panel, "msod" + id); 
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