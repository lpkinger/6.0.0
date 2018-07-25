Ext.QuickTips.init();
Ext.define('erp.controller.oa.officialDocument.fileManagement.DocumentRoom', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'oa.officialDocument.fileManagement.documentRoom.DocumentRoom','oa.officialDocument.fileManagement.documentRoom.DocumentRoomTreePanel','common.datalist.GridPanel','common.datalist.Toolbar',
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
    					Ext.getCmp('deldr').setDisabled(false);
    					Ext.getCmp('updatedr').setDisabled(false);
    					var id=record.get('id');
        				condition="drd_drid="+id;
        				Ext.getCmp('pagingtoolbar').child('#inputItem').setValue(1);
        				page=1;
        				Ext.getCmp('grid').getCount(caller,condition);
					} else {
						Ext.getCmp('deldr').setDisabled(true);
    					Ext.getCmp('updatedr').setDisabled(true);
						Ext.getCmp('grid').getCount(caller,'drd_drid=0');
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
    				if(items.length > 0 && items[0].data.id !=0){
    					var grid = Ext.getCmp('grid');
    					var flag = new Array();
    					var urlcondition = 'urlcondition=';
    					Ext.each(grid.store.data.items, function(){
    						flag.push(this.data.drd_dept_id);
    					});
    					if(flag.length > 0){
    						urlcondition += 'or_id not in (' + flag.join(',') + ')';    						
    					}
    					var dr_id = items[0].data.id;
    					var win = new Ext.window.Window({
    						id : 'win',
    						title: "添加使用部门",
    						height: "80%",
    						width: "50%",
    						maximizable : false,
    						buttonAlign : 'left',
    						layout : 'anchor',
    						items: [{
    							tag : 'iframe',
    							frame : true,
    							anchor : '100% 100%',
    							layout : 'fit',
    							html : '<iframe id="iframe_' + dr_id + '" src="' + basePath + 'jsps/oa/officialDocument/fileManagement/addHrorg.jsp?whoami=HrOrg!Add&' + urlcondition + '&id=' + dr_id + '" height="100%" width="100%" frameborder="0" scrolling="yes"></iframe>'
    						}]
    					});
    					win.show();	    					
    				}
    			}
    		},
    		'button[id=deldr]': {
    			click: function(){
    				var treegrid = Ext.getCmp('tree');
    				var items = treegrid.selModel.selected.items;
    				if(items.length > 0 && items[0].data.id !=0){
    					warnMsg('确定要删除'+items[0].data.text, function(btn){
    						if(btn == 'yes'){
//    							me.getActiveTab().setLoading(true);//loading...
    							Ext.Ajax.request({
    						   		url : basePath + 'oa/officialDocument/fileManagement/deleteDocumentRoom.action',
    						   		params: {
    						   			id: items[0].data.id
    						   		},
    						   		method : 'post',
    						   		callback : function(options,success,response){
//    						   			me.getActiveTab().setLoading(false);
    						   			var localJson = new Ext.decode(response.responseText);
    						   			if(localJson.exceptionInfo){
    					        			showError(localJson.exceptionInfo);return;
    					        		}
    					    			if(localJson.success){
    						   				delSuccess(function(){
    						   				window.location.href = window.location.href;						
    										});//@i18n/i18n.js
    						   			} else {
    						   				delFailure();
    						   			}
    						   		}
    							});
    						}
    					});
    				}
    			}
    		},
    		'button[id=adddr]': {
    			click: function(){
    				var win = new Ext.window.Window({
    					id : 'win',
    					title: "添加档案室",
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
    						html : '<iframe id="iframe_' + new Date() + '" src="' + basePath + 'jsps/oa/officialDocument/fileManagement/newDocumentRoom.jsp" height="100%" width="100%" frameborder="0" scrolling="yes"></iframe>'
    					}]
    				});
    		    	win.show();	
    			}
    		},
    		'button[id=updatedr]': {
    			click: function(){
    				var treegrid = Ext.getCmp('tree');
    				var items = treegrid.selModel.selected.items;
    				console.log(items);
    				if(items.length > 0){
    					var win = new Ext.window.Window({
    						id : 'win',
    						title: "修改档案室",
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
    							html : '<iframe id="iframe_' + new Date() + '" src="' + basePath + 'jsps/oa/officialDocument/fileManagement/newDocumentRoom.jsp?formCondition=dr_idIS' + items[0].data.id + '&gridCondition=" height="100%" width="100%" frameborder="0" scrolling="yes"></iframe>'
    						}]
    					});
    			    	win.show();	
    				}
    			}
    		},
    		
    	
    	});
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
		   					Ext.getCmp('grid').getCount('DocumentRoomDept',"drd_drid=" + dr_id);
		   				});
		   			}
		   		}
			});
		}
    }
});