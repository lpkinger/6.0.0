Ext.QuickTips.init();
Ext.define('erp.controller.hr.attendance.AttendConfirm', {
	extend: 'Ext.app.Controller',
	FormUtil: Ext.create('erp.util.FormUtil'),
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	views:['core.form.Panel','core.grid.Panel2','core.button.Close','core.button.Confirm','core.button.ResConfirm','core.form.FileField',
	       'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.form.YnField'
	       ],    
    init: function(){
    	var me = this;
    	this.control({
            'erpCloseButton': {
                click: function(btn) {
                    me.FormUtil.beforeClose(me);
                }
            },
            'erpConfirmButton': {
	    		afterrender: function(btn){
					var status = Ext.getCmp("ac_confirmstatuscode");
					if(status && status.value != 'UNCONFIRMED'){
						btn.hide();
					}
				},
    			click: function(btn){    				
    				var id = Ext.getCmp(me.getForm(btn).keyField).value;
    				me.onConfirm(id);
    			}
	    	},'erpResConfirmButton': {
	    		afterrender: function(btn){
	    			var status = Ext.getCmp("ac_confirmstatuscode");
					if(status && status.value != 'CONFIRMED'){
						btn.hide();
					}
				},
    			click: function(btn){  
    				var id = Ext.getCmp(me.getForm(btn).keyField).value;
    				me.onResConfirm(id);
    			}
    		}
    	});
	},
	getForm: function(btn) {
        return btn.ownerCt.ownerCt;
    },
    onConfirm: function(id){
		var form = Ext.getCmp('form');	
		Ext.Ajax.request({
	   		url : basePath + form.confirmUrl,
	   		params: {
	   			id: id,
	   			caller:caller
	   		},
	   		method : 'post',
	   		callback : function(options,success,response){
	   			var localJson = new Ext.decode(response.responseText);
    			if(localJson.success){
    				showMessage("提示", '确认成功');
	   				window.location.reload();
	   			} else {
    				if(localJson.exceptionInfo){
    	   				var str = localJson.exceptionInfo;
    	   				if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){// 特殊情况:操作成功，但是出现警告,允许刷新页面
    	   					str = str.replace('AFTERSUCCESS', '');
							showMessage("提示", str);
							window.location.reload();
    	   				} else {
    	   					showError(str);return;
    	   				}
    	   			}
    			}
	   		}
		});
	},
	onResConfirm: function(id){
			var form = Ext.getCmp('form');	
			Ext.Ajax.request({
		   		url : basePath + form.resconfirmUrl,
		   		params: {
		   			id: id,
		   			caller:caller
		   		},
		   		method : 'post',
		   		callback : function(options,success,response){
		   			var localJson = new Ext.decode(response.responseText);
	    			if(localJson.success){
	    				showMessage("提示", '取消成功');
		   				window.location.reload();    				
		   			}else {
	    				if(localJson.exceptionInfo){
	    	   				var str = localJson.exceptionInfo;
	    	   				if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){// 特殊情况:操作成功，但是出现警告,允许刷新页面
	    	   					str = str.replace('AFTERSUCCESS', '');
								showMessage("提示", str);
								window.location.reload();
	    	   				} else {
	    	   					showError(str);return;
	    	   				}
	    	   			}
    				}
		   		}
			});
	}
});