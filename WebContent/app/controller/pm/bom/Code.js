Ext.QuickTips.init();
Ext.define('erp.controller.pm.bom.Code', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'pm.bom.CodeForm','pm.bom.Code',
    		'core.button.Confirm','core.button.Close','core.button.Print',
    		'core.form.ConDateField'
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