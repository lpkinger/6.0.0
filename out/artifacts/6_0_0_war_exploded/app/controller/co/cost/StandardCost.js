Ext.QuickTips.init();
Ext.define('erp.controller.co.cost.StandardCost', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'co.cost.StandardCostForm','co.cost.StandardCost',
    		'core.button.Confirm','core.button.Close',
    		'core.form.ConDateField','core.form.FtFindField', 'core.trigger.DbfindTrigger'
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