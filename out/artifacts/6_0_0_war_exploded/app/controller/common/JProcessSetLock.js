Ext.QuickTips.init();
Ext.define('erp.controller.common.JProcessSetLock', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'core.form.Panel','common.JProcess.JProcessSetLock','common.JProcess.JProcessSetViewPort','core.grid.Panel','core.button.Add','core.button.Submit','core.button.Audit',
    		'core.button.Save','core.button.Close','core.button.Print','core.button.Upload','core.button.Update','core.button.Delete',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger', 'core.button.Sync'
    	],
    init:function(){
    	var me = this;
    	/*formCondition = this.BaseUtil.getUrlParam('formCondition');
    	console.log(formCondition);*/
    	var js_formurl=Ext.decode(Ext.getCmp('js_formurl'));
    	this.control({   	
 		'erpSaveButton': {    			
 			afterrender: function(btn){
				var forcon = getUrlParam('formCondition');

				if(forcon != null && forcon != ''){
						btn.hide();
					}
			},
    			click: function(btn){
    				me.FormUtil.beforeSave(me);
    			}
    		},
    		'erpDeleteButton' : {
    			
    			afterrender: function(btn){
    				var forcon = getUrlParam('formCondition');
    				if(forcon == null || forcon == ""){
    						btn.hide();
    					}
    			},
    			click: function(btn){
    				if(Ext.getCmp('js_caller').value==null||Ext.getCmp('js_caller').value==''){
    					btn.hide();
    				}
    				me.FormUtil.onDelete(Ext.getCmp('js_id').value);
    			}
    		},
    		'erpPostButton' : {
    			
    			afterrender: function(btn){
    				var forcon = getUrlParam('formCondition');
    				if(forcon == null || forcon == ""){
    						btn.hide();
    					}
    			},
    			click:function(btn){
    				   				
    				me.FormUtil.onPost(Ext.getCmp('js_id').value);
    			}
    		},
    		'erpUpdateButton': {
    			
    			afterrender: function(btn){
    				var forcon = getUrlParam('formCondition');
    				if(forcon == null || forcon == ""){
    						btn.hide();
    					}
    			},
    			click: function(btn){
    				
    				me.beforeUpdate();
    			}
    		},
    		'erpAddButton': {
    			
    			afterrender: function(btn){
    				var forcon = getUrlParam('formCondition');
    				if(forcon == null || forcon == ""){
    						btn.hide();
    					}
    			},
    			click: function(){
    				
    				me.FormUtil.onAdd('JProcessSetLock', '新增流程设置', 'jsps/common/JProcessSetLock.jsp?whoami='+caller);
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				me.FormUtil.beforeClose(me);
    			}
    		}
    		
    });},
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	beforeUpdate: function(){
		var bool = true;
		if(bool)
			this.FormUtil.onUpdate(this);
	}             
});