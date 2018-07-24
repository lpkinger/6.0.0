Ext.QuickTips.init();
Ext.define('erp.controller.oa.myProcess.synergy.Manage', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
     		'oa.myProcess.synergy.manage.Viewport','common.datalist.GridPanel','common.datalist.Toolbar',
     		'oa.myProcess.synergy.manage.Form',
     		'core.trigger.DbfindTrigger','core.form.ConDateField','core.form.WordSizeField','oa.mail.MailPaging'
     	],
    init:function(){
    	var me = this;
    	this.control({
    		'erpDatalistGridPanel': {
    			afterrender: function(grid){
    				console.log(grid);
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
//    	var me = this;
    	var id = record.data.sy_id;
    	var win = new Ext.window.Window({
			id : 'win',
			title: "协同查看",
			height: "80%",
			width: "80%",
			maximizable : false,
			buttonAlign : 'left',
			layout : 'anchor',
			items: [{
				tag : 'iframe',
				frame : true,
				anchor : '100% 100%',
				layout : 'fit',
				html : '<iframe id="iframe_' + id + '" src="' + basePath + 'jsps/oa/myProcess/synergy/seeSynergy.jsp?id=' + id + '" height="100%" width="100%" frameborder="0" scrolling="yes"></iframe>'
			}]
		});
		win.show();	
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