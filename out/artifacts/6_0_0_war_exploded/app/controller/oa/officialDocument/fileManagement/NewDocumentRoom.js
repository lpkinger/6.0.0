Ext.QuickTips.init();
Ext.define('erp.controller.oa.officialDocument.fileManagement.NewDocumentRoom', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'oa.officialDocument.fileManagement.documentRoom.NewDocumentRoom','core.form.Panel',
    		'core.button.Save','core.button.Close','core.button.Update','core.button.Delete',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.trigger.MultiDbfindTrigger'
    	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'erpSaveButton': {
    			click: function(btn){
    				this.FormUtil.beforeSave(this);
//    				parent.window.location.href = parent.window.location.href;
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
//    				parent.window.location.href = parent.window.location.href;
    			}
    		},
    		'erpDeleteButton': {
    			click: function(btn){
    				me.onDelete((Ext.getCmp('dr_id').value));
//    				me.FormUtil.onDelete((Ext.getCmp('dr_id').value));
//    				parent.window.location.href = parent.window.location.href;
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				parent.window.location.href = parent.window.location.href;
    				this.FormUtil.onClose(this);
//    				window.close();
    			}
    		}
    	});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	
    openTab : function (panel,id){ 
    	var o = (typeof panel == "string" ? panel : id || panel.id); 
    	var main = parent.Ext.getCmp("content-panel"); 
    	var tab = main.getComponent(o); 
    	if (tab) { 
    		main.setActiveTab(tab); 
    	} else if(typeof panel!="string"){ 
    		panel.id = o; 
    		var p = main.add(panel); 
    		main.setActiveTab(p); 
    	}
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