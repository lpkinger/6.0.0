Ext.QuickTips.init();
Ext.define('erp.controller.oa.officialDocument.fileManagement.AddHrorg', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
     		'oa.officialDocument.fileManagement.documentRoom.AddHrorg','common.datalist.GridPanel',
     		'common.datalist.Toolbar','oa.mail.MailPaging'
     	],
    init:function(){
    	var me = this;
    	this.control({
    		'erpDatalistGridPanel': {
    			reconfigure: function(){
    			}
    		},
    		'button[id=cancel]': {
    			click: function(){
    				var win = parent.Ext.ComponentQuery.query('window');
					if(win){
						Ext.each(win, function(){
							this.close();
						});
					} else {
						window.close();
					}
    			}
    		},
    		'button[id=add]': {
    			click: function(){
    				me.vastAdd();
    			}
    		}
    	});
    },
    vastAdd: function(){
    	var grid = Ext.getCmp('grid');
		var drid =  getUrlParam('id');
		var items = grid.selModel.selected.items;
		console.log(items);
		if(items.length > 0){
			var dept = new Array();
			var deptid = new Array();
			Ext.each(items, function(){
				deptid.push(this.data.or_id);
				dept.push(this.data.or_name);
			});
//			var main = parent.Ext.getCmp("content-panel");
//			main.getActiveTab().setLoading(true);//loading...
			Ext.Ajax.request({
		   		url : basePath + 'oa/officialDocument/fileManagement/addDept.action',
		   		params: {
		   			drid: drid,
		   			dept: dept,
		   			deptid: deptid
		   		},
		   		method : 'post',
		   		callback : function(options,success,response){
//		   			main.getActiveTab().setLoading(false);
		   			var localJson = new Ext.decode(response.responseText);
		   			if(localJson.exceptionInfo){
		   				showError(localJson.exceptionInfo);
		   				return "";
		   			}
	    			if(localJson.success){
		   				Ext.Msg.alert("提示", "添加成功!", function(){
		   					parent.Ext.getCmp('grid').getCount('DocumentRoomDept',"drd_drid=" + drid);
		   					var win = parent.Ext.ComponentQuery.query('window');
							if(win){
								Ext.each(win, function(){
									this.close();
								});
							} else {
								window.close();
							}
		   				});
		   			}
		   		}
			});
		} else {
			return;
		}
    }

});