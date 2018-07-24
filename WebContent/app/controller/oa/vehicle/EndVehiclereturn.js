Ext.QuickTips.init();
Ext.define('erp.controller.oa.vehicle.EndVehiclereturn', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'oa.vehicle.EndVehiclereturn','core.form.Panel','core.form.FileField','core.form.MultiField','core.grid.Panel2','core.toolbar.Toolbar',
    		'core.button.Add','core.button.Submit','core.button.Audit','core.button.Save','core.button.Close','core.button.Print',
    			'core.button.Upload','core.button.Update','core.button.Delete','core.button.ResAudit','core.button.ResSubmit','core.button.Confirm',
    			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.trigger.AutoCodeTrigger','core.form.ConDateHourMinuteField',
    			'core.form.YnField','core.trigger.DbfindTrigger','core.button.Scan','core.button.ResConfirm'
    	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'erpGridPanel2': {    			
    			
    		},
			'erpUpdateButton': {				
				click: function(btn){
					this.FormUtil.onUpdate(this);
				}
			},			
			'erpCloseButton': {				
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			},
			'erpConfirmButton': {
				afterrender: function(btn){
				var statu = Ext.getCmp('vr_isback');
				if(statu && statu.value == '1'){
					btn.hide();
				  }
			    },
    			click: function(btn){    				
    				me.onConfirm(Ext.getCmp('vr_id').value);    				
    			}
    		},
    		'erpResConfirmButton': {
				afterrender: function(btn){
				var statu = Ext.getCmp('vr_isback');
				if(statu && statu.value != '1'){
					btn.hide();
				  }
			    },
    			click: function(btn){    				
    				me.onResConfirm(Ext.getCmp('vr_id').value); 
    			}
    		}
    	});
    },
    getForm: function(btn){
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
		   					window.location.reload();    				
		   			} 
		   			if (localJson.exceptionInfo) {
							showError(localJson.exceptionInfo);
					}
		   		}
			});
		},
		 onResConfirm: function(id){
			var form = Ext.getCmp('form');	
			Ext.Ajax.request({
		   		url : basePath + form.resConfirmUrl,
		   		params: {
		   			id: id,
		   			caller:caller
		   		},
		   		method : 'post',
		   		callback : function(options,success,response){
		   			var localJson = new Ext.decode(response.responseText);
	    			if(localJson.success){    			
		   					window.location.reload();    				
		   			} 
		   			if (localJson.exceptionInfo) {
							showError(localJson.exceptionInfo);
					}
		   		}
			});
		}
});