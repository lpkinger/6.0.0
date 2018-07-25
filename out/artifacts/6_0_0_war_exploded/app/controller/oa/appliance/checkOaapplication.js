Ext.QuickTips.init();
Ext.define('erp.controller.oa.appliance.checkOaapplication', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'oa.appliance.checkOaapplication','core.form.Panel','core.form.MultiField','core.form.FileField',
    		'core.button.Add','core.button.Save','core.button.Close',
    		'core.button.Update','core.button.Delete','core.form.YnField',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger'
    	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'erpSaveButton': {
    			click: function(btn){
    				var od_total = Ext.Number.from(Ext.getCmp('od_total').getValue(), 0), 
    					od_yqty = Ext.Number.from(Ext.getCmp('od_yqty').getValue(), 0),
    					od_turnlyqty = Ext.Number.from(Ext.getCmp('od_turnlyqty').getValue(), 0);
    				if(od_total < od_yqty){
    					showError('批准数量不能小于已转采购数量！');return;
    				}
    				if(od_total < od_turnlyqty){
    					showError('批准数量不能小于已转领用数量！');return;
    				}
    				this.FormUtil.beforeSave(this);
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				this.FormUtil.beforeClose(this);
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				var od_total = Ext.Number.from(Ext.getCmp('od_total').getValue(), 0), 
						od_yqty = Ext.Number.from(Ext.getCmp('od_yqty').getValue(), 0),
						od_turnlyqty = Ext.Number.from(Ext.getCmp('od_turnlyqty').getValue(), 0);
					if(od_total < od_yqty){
						showError('批准数量不能小于已转采购数量！');return;
					}
					if(od_total < od_turnlyqty){
						showError('批准数量不能小于已转领用数量！');return;
					}
    				this.FormUtil.onUpdate(this);
    			}
    		},
    	});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});