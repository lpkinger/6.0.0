Ext.QuickTips.init();
Ext.define('erp.controller.oa.persontask.workPlan.AddType', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'oa.persontask.workPlan.AddType','core.form.Panel',
    		'core.button.Add','core.button.Save','core.button.Close','core.button.Update','core.button.Delete',
    			'core.button.Scan','core.form.ColorField'
    			
    	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'erpSaveButton': {
    			click: function(btn){
    				this.FormUtil.beforeSave(this);
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpDeleteButton': {
    			click: function(btn){
    				me.onDelete((Ext.getCmp('wpt_id').value));
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				parent.window.location.href = parent.window.location.href;
    				this.FormUtil.onClose(this);
    			}
    		}
    	});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	onDelete: function(id){
		var me = this;
		warnMsg($I18N.common.msg.ask_del_main, function(btn){
			if(btn == 'yes'){
				var form = Ext.getCmp('form');
				if(!contains(form.deleteUrl, '?caller=', true)){
					form.deleteUrl = form.deleteUrl + "?caller=" + caller;
				}
//				me.getActiveTab().setLoading(true);//loading...
				Ext.Ajax.request({
			   		url : basePath + form.deleteUrl,
			   		params: {
			   			id: id
			   		},
			   		method : 'post',
			   		callback : function(options,success,response){
//			   			me.getActiveTab().setLoading(false);
			   			var localJson = new Ext.decode(response.responseText);
			   			if(localJson.exceptionInfo){
		        			showError(localJson.exceptionInfo);return;
		        		}
		    			if(localJson.success){
			   				delSuccess(function(){
			   					parent.window.location.href = parent.window.location.href;
			   					me.FormUtil.onClose();							
							});//@i18n/i18n.js
			   			} else {
			   				delFailure();
			   			}
			   		}
				});
			}
		});
	}
});