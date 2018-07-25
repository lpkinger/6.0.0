Ext.QuickTips.init();
Ext.define('erp.controller.oa.officialDocument.fileManagement.Dossier', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'oa.officialDocument.fileManagement.dossier.Dossier','oa.officialDocument.fileManagement.dossier.DocumentRoomTreePanel','common.datalist.GridPanel','common.datalist.Toolbar',
    		'core.button.Add','core.button.Submit','core.button.Audit','core.button.Save','core.button.Close','core.button.Print',
    		'core.button.Upload','core.button.Update','core.button.Delete','core.button.ResAudit','core.button.DeleteDetail',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger'
    	],
    init:function(){
    	var me = this;
    	this.control({ 
    	    'erpDocumentRoomTreePanel': {
    			itemmousedown: function(selModel, record){
    				if(record.get('leaf')){	
    					var id=record.get('id');
        				condition = "do_documentroom_id=" + id;
        				Ext.getCmp('pagingtoolbar').child('#inputItem').setValue(1);
        				page=1;
        				Ext.getCmp('grid').getCount(caller,condition);
					} else {
						Ext.getCmp('grid').getCount(caller,'do_documentroom_id=0');
					}					
    			}
    		}, 
    		'button[id=delete]': {
    			click: function(){
    				me.vastDelete();
    			}
    		},
    		'button[id=add]': {
    			click: function(){
    				var treegrid = Ext.getCmp('tree');
    				var items = treegrid.selModel.selected.items;
    				console.log(items);
    				if(items.length > 0 && items[0].data.id !=0){//id=0为档案室节点
    					var dr_id = items[0].data.id;
    					var dr_name = items[0].data.text;
    					var win = new Ext.window.Window({
    						id : 'win',
    						title: "添加案卷",
    						height: "75%",
    						width: "60%",
    						maximizable : false,
    						buttonAlign : 'left',
    						layout : 'anchor',
    						items: [{
    							tag : 'iframe',
    							frame : true,
    							anchor : '100% 100%',
    							layout : 'fit',
    							html : '<iframe id="iframe_' + dr_id + '" src="' + basePath + 
    							'jsps/oa/officialDocument/fileManagement/addDossier.jsp?id=' 
    							+ dr_id + '&name=' + dr_name + '" height="100%" width="100%" frameborder="0" scrolling="yes"></iframe>'
    						}]
    					});
    					win.show();	    					
    				}
    			}
    		},
    		'button[id=update]': {
    			click: function(){
    				me.vastUpdate();
    			}
    		},
    		'button[id=cj]': {
    			click: function(){
//    				me.vastDelete();
    			}
    		},
    		'button[id=fj]': {
    			click: function(){
//    				me.vastDelete();
    			}
    		},
    		'gridcolumn[dataIndex=do_period]':{
    		}
    	});
    },	
    vastUpdate: function(){
    	var grid = Ext.getCmp('grid');
		var records = grid.selModel.getSelection();
		if(records.length == 0){
			showError('请先选择需要修改的案卷');
		} else if(records.length > 1){
			showError('每次只能修改一个案卷');
		} else {
			var id = records[0].data.do_id;
			var win = new Ext.window.Window({
				id : 'win',
				title: "修改案卷",
				height: "75%",
				width: "60%",
				maximizable : false,
				buttonAlign : 'left',
				layout : 'anchor',
				items: [{
					tag : 'iframe',
					frame : true,
					anchor : '100% 100%',
					layout : 'fit',
					html : '<iframe id="iframe_' + new Date() + '" src="' + basePath + 'jsps/oa/officialDocument/fileManagement/addDossier.jsp?formCondition=do_idIS' + id + '&gridCondition=" height="100%" width="100%" frameborder="0" scrolling="yes"></iframe>'
				}]
			});
			win.show();
		}
    },
    vastDelete: function(){
    	var treegrid = Ext.getCmp('tree');
		var items = treegrid.selModel.selected.items;
    	var dr_id = items[0].data.id;
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
		   					Ext.getCmp('grid').getCount('Dossier',"do_documentroom_id=" + dr_id);
		   				});
		   			}
		   		}
			});
		}
    }
});