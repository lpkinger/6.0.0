Ext.QuickTips.init();
Ext.define('erp.controller.co.cost.ManuFactFee', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'core.form.Panel','co.cost.ManuFactFee','core.form.MultiField',
    		'core.button.Add','core.button.Submit','core.button.ResSubmit','core.button.Audit','core.button.Save','core.button.Close',
			'core.button.Upload','core.button.Update','core.button.Delete','core.button.ResAudit','core.form.MonthDateField',
		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.form.YnField'
	],
	init:function(){
		var me = this;
		this.control({ 
			'erpSaveButton': {
				click: function(btn){
					var form = me.getForm(btn);
					if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
						me.BaseUtil.getRandomNumber();//自动添加编号
					}
					//保存之前的一些前台的逻辑判定
					this.FormUtil.beforeSave(this);
				}
			},
			'erpDeleteButton' : {
				click: function(btn){
					me.FormUtil.onDelete(Ext.getCmp('mf_id').value);
				}
			},
			'erpUpdateButton': {
				click: function(btn){
					this.FormUtil.onUpdate(this);
				}
			},
			'erpAddButton': {
				click: function(){
					me.FormUtil.onAdd('addManuFactFee', '新增直接人工制造费用', 'jsps/co/cost/manufactFee.jsp');
				}
			},
			'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			},
			'erpSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('mf_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onSubmit(Ext.getCmp('mf_id').value);
				}
			},
			'erpResSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('mf_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResSubmit(Ext.getCmp('mf_id').value);
				}
			},
			'erpAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('mf_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onAudit(Ext.getCmp('mf_id').value);
				}
			},
			'erpResAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('mf_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResAudit(Ext.getCmp('mf_id').value);
				}
			},
			'field[name=mf_yearmonth]': {
    			afterrender: function(field) {
    				var formCondition = getUrlParam('formCondition');
    				if(!field.value||!formCondition){
    					me.getCurrentYearmonth(field);
    				}
    				
    			}
    		}
		});
	},
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	getCurrentYearmonth : function(f) {
		Ext.Ajax.request({
			url : basePath + 'co/cost/getCurrentYearmonthCo.action',
			method : 'GET',
			callback : function(opt, s, r) {
				var rs = Ext.decode(r.responseText);
				if (rs.exceptionInfo) {
					showError(rs.exceptionInfo);
				} else if (rs.data) {
					f.setValue(rs.data);
				}
			}
		});
	}
});