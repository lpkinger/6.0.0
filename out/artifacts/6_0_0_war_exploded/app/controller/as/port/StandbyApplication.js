Ext.QuickTips.init();
Ext.define('erp.controller.as.port.StandbyApplication', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    views:[
    		'core.form.Panel','core.grid.Panel2','as.port.StandbyApplication','core.form.MultiField','core.form.FileField',
    		'core.button.Add','core.button.Submit','core.button.ResSubmit','core.button.Audit','core.button.Save','core.button.Close','core.button.Print','core.button.TurnCustomer',
			'core.button.Upload','core.button.Update','core.button.Delete','core.button.ResAudit',
			'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.form.YnField','core.trigger.AutoCodeTrigger',
	],
	init:function(){
		var me = this;
		this.control({
			'erpGridPanel2': { 
				itemclick: this.onGridItemClick
			},
			'erpDeleteButton' : {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('sa_id').value);
    			}
    		},
			'erpSaveButton': {
				click: function(btn){
					var form = me.getForm(btn), codeField = Ext.getCmp(form.codeField);
					if(codeField.value == null || codeField.value == ''){
						me.BaseUtil.getRandomNumber(caller);//自动添加编号
					}
					this.FormUtil.beforeSave(this);
				}
			},   		
			'erpUpdateButton': {
				click: function(btn){
					this.FormUtil.onUpdate(this);
				}
			},
			'erpAddButton': {
				click: function(){
					me.FormUtil.onAdd('StandbyApplication', '新增备用机申请单', 'jsps/as/port/StandbyApplication.jsp');
				}
			},
			'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			},
			'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('sa_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				var grid = Ext.getCmp('grid'), c = Ext.getCmp('sa_code').value,
    				    items = grid.store.data.items, recorddate = Ext.getCmp('sa_applicationdate').value;
    				var bool = true;
    				if(bool){
    					me.FormUtil.onSubmit(Ext.getCmp('sa_id').value);
    				}
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('sa_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('sa_id').value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('sa_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('sa_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('sa_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('sa_id').value);
    			}
    		},
    		'erpPrintButton': {
    			click:function(btn){
				var reportName="application";
				var condition='{Application.ap_id}='+Ext.getCmp('ap_id').value+'';
				var id=Ext.getCmp('ap_id').value;
				me.FormUtil.onwindowsPrint(id,reportName,condition);
			}
    		},	
		});
	},
	onGridItemClick: function(selModel, record){//grid行选择
		this.GridUtil.onGridItemClick(selModel, record);
	},
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	getStore : function(condition) {
		var me = this;
		var grid = Ext.getCmp('grid');
		grid.store.removeAll(false);
		grid.setLoading(true);// loading...
		Ext.Ajax.request({// 拿到grid的columns
			url : basePath + "common/singleGridPanel.action",
			params : {
				caller : "StandbyApplication",
				condition : condition
			},
			method : 'post',
			callback : function(options, success, response) {
				grid.setLoading(false);
				var res = new Ext.decode(response.responseText);
				if (res.exceptionInfo) {
					showError(res.exceptionInfo);
					return;
				}
				var data = [];
				if (!res.data || res.data.length == 2) {
					me.GridUtil.add10EmptyItems(grid);
				} else {
					data = Ext.decode(res.data.replace(/,}/g, '}').replace(
							/,]/g, ']'));
					if (data.length > 0) {
						grid.store.loadData(data);
					}
				}
			}
		});
	}
	
});