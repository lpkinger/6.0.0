Ext.QuickTips.init();
Ext.define('erp.controller.scm.reserve.Inventory', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'scm.reserve.InventoryForm','scm.reserve.Inventory','core.trigger.MultiDbfindTrigger',
    		'core.button.Confirm','core.button.Close','core.trigger.DbfindTrigger','core.trigger.AddDbfindTrigger'
    	] ,
    	init:function(){
        	var me = this;
        	this.control({         		
        		'erpCloseButton': {
        			click: function(btn){
        				me.FormUtil.onClose();
        			}
        		},
        		'erpConfirmButton': {
        			click: function(btn){
        				this.confirm();
        			}
        		}
        	});
        },
    	getForm: function(btn){
    		return btn.ownerCt.ownerCt;
    	},
    	confirm: function(){
    		var me = this;
    		this.BaseUtil.getActiveTab().setLoading(true);
    		Ext.Ajax.request({
    			url : basePath + "scm/reserve/inventory.action",
    			params:{
    				method: Ext.getCmp('method').value,
    				whcode:	Ext.getCmp('pr_whcode').value
    			},
    			method:'post',
    			timeout: 300000,
    			callback:function(options,success,response){
    				me.BaseUtil.getActiveTab().setLoading(false);
    				var localJson = new Ext.decode(response.responseText);
        			if(localJson.success){
        				if(localJson.log){
        					Ext.Msg.alert("提示", localJson.log);
        				}
        			} else {
        				console.log(localJson);
        				if(localJson.exceptionInfo){
        	   				var str = localJson.exceptionInfo;
        	   				if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
        	   					str = str.replace('AFTERSUCCESS', '');
        	   					showError(str);
        	   				} else {
        	   					showError(str);return;
        	   				}
        	   			}
        			}
    			}
    		});
    	}
    });