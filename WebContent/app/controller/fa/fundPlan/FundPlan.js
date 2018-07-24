Ext.QuickTips.init();
Ext.define('erp.controller.fa.fundPlan.FundPlan', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'fa.fundPlan.FundPlan','core.form.Panel','core.grid.Panel2','core.form.MultiField','core.form.FileField',
    		'core.button.Add','core.button.Submit','core.button.Audit','core.button.Save','core.button.Close',
    		'core.button.ResSubmit','core.button.Update','core.button.Delete','core.button.ResAudit',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.form.YnField','core.form.MonthDateField','core.button.CallProcedureByConfig'
    	],
    init:function(){
    	var me = this;
    	this.control({
    		'erpSaveButton': {
    			click: function(btn){
    				var form = me.getForm(btn);
    				this.FormUtil.beforeSave(me);
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				this.FormUtil.beforeClose(this);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('FundPlan', '新增资金计划数据', 'jsps/fa/fundPlan/fundPlan.jsp?whoami=FundPlan');
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpDeleteButton': {
    			click: function(btn){
    				this.FormUtil.onDelete(Ext.getCmp('fp_id').value);
    			}
    		}
    		
    	});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});