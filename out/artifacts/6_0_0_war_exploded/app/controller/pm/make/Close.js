Ext.QuickTips.init();
Ext.define('erp.controller.pm.make.Close', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'pm.make.CloseForm','pm.make.Close',
    		'core.button.Confirm','core.button.Close','core.button.Print',
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