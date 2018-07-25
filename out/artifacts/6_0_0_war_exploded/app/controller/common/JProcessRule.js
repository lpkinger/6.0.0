Ext.QuickTips.init();
Ext.define('erp.controller.common.JProcessRule', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    views:[
    		'core.form.Panel','common.JProcess.JProcessRule','core.grid.Panel','core.button.Add','core.trigger.TextAreaTrigger',
    		'core.button.Save','core.button.Close','core.button.Update','core.button.Delete','core.form.TimeMinuteField','core.trigger.DbfindTrigger',
			'core.button.CheckRuleSql'
    	],
    	 init:function(){
    	    	var me = this;
    	    	var forcon = getUrlParam('formCondition');
    	    	var noFormCondition = false;
    	    	if(forcon == null || forcon == ""){
    	    		noFormCondition = true;
    	    	}
    	    	this.control({   	
    	 		'erpSaveButton': {    			
    	 			afterrender: function(btn){
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
	    				if(noFormCondition){
	    					btn.hide();
	    				}
	    			},
	    			click: function(btn){
	    				if(noFormCondition){
	    					btn.hide();
	    				}
	    				me.FormUtil.onDelete(Ext.getCmp('ru_id').value);
	    			}
	    		},
	    		'erpUpdateButton': {
	    			afterrender: function(btn){
	    				if(noFormCondition){
	    					btn.hide();
	    				}
	    			},
	    			click: function(btn){	
	    				me.beforeUpdate();
	    			}
	    		},
	    		'erpAddButton': {
	    			afterrender: function(btn){
	    				if(noFormCondition){
	    					btn.hide();
	    				}
	    			},
	    			click: function(){
	    				me.FormUtil.onAdd('jprocesstRule', '新增流程自动审核规则', 'jsps/common/jprocessRule.jsp');
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