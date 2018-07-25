Ext.QuickTips.init();
Ext.define('erp.controller.crm.marketmgr.marketresearch.PreView', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    views:[
    		'crm.marketmgr.marketresearch.PreView','core.form.Panel','core.form.FileField','core.form.MultiField','core.form.CheckBoxGroup','core.trigger.MultiDbfindTrigger',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit','core.button.TurnCLFBX',
  			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.ResSubmit','core.grid.Panel2',
  			'core.button.TurnCustomer','core.button.Flow','core.button.DownLoad','core.button.Scan','common.datalist.Toolbar','core.button.Confirm',
  			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.trigger.AutoCodeTrigger','core.trigger.AddDbfindTrigger'
  	],
	init:function(){
		var me = this;
		this.control({
			'erpGridPanel2': { 
				itemclick: this.onGridItemClick
			},
			'erpSaveButton': {
				afterrender:function(btn){
					btn.hide();
				}
			},
			'erpDeleteButton' : {
				afterrender:function(btn){
					btn.hide();
				}
			},
			'erpTurnCLFBXButton' : {
				afterrender:function(btn){
					btn.hide();
				}
			},
			'erpUpdateButton': {
				afterrender:function(btn){
					btn.hide();
				}
			},
			'erpAddButton': {
				afterrender:function(btn){
					btn.hide();
				}
			},'erpSubmitButton': {
				afterrender: function(btn){
						btn.hide();
				}
			},'erpResSubmitButton': {
				afterrender: function(btn){
						btn.hide();
				}
			},
			'erpAuditButton': {
				afterrender: function(btn){
						btn.hide();
				}
			},
			'erpResAuditButton': {
				afterrender: function(btn){
						btn.hide();
				}
			},
			'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			}
    	});
	},
	onGridItemClick: function(selModel, record){//grid行选择
    	this.GridUtil.onGridItemClick(selModel, record);
    },
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});