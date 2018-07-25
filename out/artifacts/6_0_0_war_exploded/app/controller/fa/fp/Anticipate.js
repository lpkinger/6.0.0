Ext.QuickTips.init();
Ext.define('erp.controller.fa.fp.Anticipate', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
   		'fa.fp.Anticipate','core.form.Panel','core.toolbar.Toolbar','core.form.MultiField','core.form.FileField',
   		'core.grid.Panel2','core.form.SeparNumber','core.form.YnField','core.form.MonthDateField','core.form.YnField',
		'core.button.Add','core.button.Submit','core.button.ResSubmit','core.button.Audit','core.button.ResAudit',
		'core.button.DeleteDetail','core.button.Save','core.button.Close','core.button.Update','core.button.Delete',
		'core.button.Print','core.button.PrintByCondition',
		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.trigger.MultiDbfindTrigger' 
		],
    init:function(){
    	var me = this;
    	this.control({
    		'erpGridPanel2': { 
				itemclick: this.onGridItemClick
			},
    		'erpSaveButton': {
    			click: function(btn){
    				var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber();
    				}
    				this.FormUtil.beforeSave(this);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addAnticipate', '新增逾期应收单', 'jsps/fa/fp/anticipate.jsp');
    			}
    		},
    		'erpDeleteButton' : {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('an_id').value);
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			},
			'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('an_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onSubmit(Ext.getCmp('an_id').value);
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('an_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('an_id').value);
    			}
    		}, 
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('an_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('an_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('an_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('an_id').value);
    			}
    		},
    		'erpPrintButton': {
    			click: function(btn){
    				var reportName="Anticipate";
        			var id=Ext.getCmp('an_id').value;
        			var condition = '{Anticipate.an_id}=' + Ext.getCmp('an_id').value;
        			me.FormUtil.onwindowsPrint(id, reportName, condition);
    			}
    		},
    	});
    },
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	onGridItemClick: function(selModel, record){// grid行选择
    	this.GridUtil.onGridItemClick(selModel, record);
	}
});
