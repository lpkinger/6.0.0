Ext.QuickTips.init();
Ext.define('erp.controller.pm.bom.SonSeqencing', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'pm.bom.SonSeqencingForm','pm.bom.SonSeqencing',
    		'core.button.Confirm','core.button.Close','core.button.Print',
    		'core.form.ConDateField','core.trigger.DbfindTrigger'
    	] ,
    	init:function(){
        	var me = this;
        	this.control({         		
        		'erpCloseButton': {
        			click: function(btn){
        				me.FormUtil.beforeClose(me);
        			}
        		}
        	});
        },
    	getForm: function(btn){
    		return btn.ownerCt.ownerCt;
    	}
    });