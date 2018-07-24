Ext.QuickTips.init();
Ext.define('erp.controller.oa.persontask.myAgenda.TypeSet', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    views:[
    		'oa.persontask.myAgenda.TypeSet','common.datalist.GridPanel','common.datalist.Toolbar',
//    		'core.grid.AgendaTypeListGrid','oa.mail.MailPaging'
    	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'erpDatalistGridPanel': {
    			select: function(selModel, record){
    			}
    		},
    		'button[id=add]': {
    			click: function(){
    				var win = new Ext.window.Window({
	    				id : 'win',
	    				title: "添加日程类型",
	    				height: "180px",
	    				width: "60%",
	    				maximizable : false,
	    				buttonAlign : 'center',
	    				layout : 'anchor',
	    				items: [{
	    					tag : 'iframe',
	    					frame : true,
	    					anchor : '100% 100%',
	    					layout : 'fit',
	    					html : '<iframe id="iframe_' + id + '" src="' + basePath + 'jsps/oa/persontask/myAgenda/addType.jsp" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>'
	    				}]
	    			});
	    			win.show();
    			}
    		},
    		'button[id=delete]': {
    			click: function(){
    				me.vastDelete();
    			}
    		},
    		'button[id=update]': {
    			click: function(){
    				var grid = Ext.getCmp('grid');
    				var records = grid.selModel.getSelection();
    				if(records.length == 1){
    					var win = new Ext.window.Window({
    						id : 'win',
    						title: "修改日程类型",
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
    							html : '<iframe id="iframe_' + new Date() + '" src="' + basePath + 'jsps/oa/persontask/myAgenda/addType.jsp?formCondition=at_idIS' + records[0].data[keyField] + '&gridCondition=" height="100%" width="100%" frameborder="0" scrolling="yes"></iframe>'
    						}]
    					});
    			    	win.show();	
    				} else {
    					showError('请选择要修改类型，且每次只能修改一条记录');
    				}
    			}
    		},
    		'button[id=search]': {
    			click: function(){
    				var grid = Ext.getCmp('grid');
    				grid.getCount('AgendaType', '');
    				grid.filterCondition = "at_name like '%" + Ext.getCmp('titlelike').value + "%'";
    			}
    		}
    	});
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
		   		url : basePath + 'oa/persontask/myAgenda/vastDeleteAgendaType.action',
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