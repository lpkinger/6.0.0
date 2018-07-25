Ext.QuickTips.init();
Ext.define('erp.controller.fa.fix.AssetsCardChange', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'fa.fix.AssetsCardChange','core.form.Panel','core.form.MultiField','core.form.FileField',
    		'core.button.Add','core.button.Submit','core.button.Audit','core.button.Save','core.button.Close','core.button.Print',
    			'core.button.Upload','core.button.Update','core.button.Delete','core.button.ResAudit',
    			'core.button.ResSubmit','core.button.Scan',
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
			'field[name=acc_currency]': {
    			beforetrigger: function(field) {
    				var t = field.up('form').down('field[name=acc_indate]'),
    					value = t.getValue();
    				if(value) {
    					field.findConfig = 'cm_yearmonth=' + Ext.Date.format(value, 'Ym');
    				}
    			}
    		},
			'erpDeleteButton' : {
				click: function(btn){
					me.FormUtil.onDelete(Ext.getCmp('acc_id').value);
				}
			},
			'erpUpdateButton': {
				click: function(btn){
					this.FormUtil.onUpdate(this);
				}
			},
			'erpAddButton': {
				click: function(){
					me.FormUtil.onAdd('addAssetsCardChange', '新增卡片变更单', 'jsps/fa/fix/assetsCardChange.jsp');
				}
			},
			'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			},
			'erpSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('acc_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onSubmit(Ext.getCmp('acc_id').value);
				}
			},
			'erpResSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('acc_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResSubmit(Ext.getCmp('acc_id').value);
				}
			},
			'erpAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('acc_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onAudit(Ext.getCmp('acc_id').value);
				}
			},
			'textfield[name=acc_accode]': {
    			aftertrigger: function(field){
//    				if(field != null && field != ''){
//    					me.getOldStore("pd_code='" + field.value + "'");
//    				}
    				if(field != null && field != ''){
    					if(typeof (f = Ext.getCmp('ac_code')) != 'undefined'){
    						Ext.getCmp('ac_code').setValue(Ext.getCmp('acc_accode').value);
    					}
        				Ext.getCmp('acc_ascode').setValue(Ext.getCmp('ac_ascode').value);
        				Ext.getCmp('acc_asname').setValue(Ext.getCmp('ac_asname').value);
        				Ext.getCmp('acc_kind').setValue(Ext.getCmp('ac_kind').value);
        				Ext.getCmp('acc_accatecode').setValue(Ext.getCmp('ac_accatecode').value);
        				Ext.getCmp('acc_ascatecode').setValue(Ext.getCmp('ac_ascatecode').value);
        				Ext.getCmp('acc_totalcatecode').setValue(Ext.getCmp('ac_totalcatecode').value);
        				Ext.getCmp('acc_location').setValue(Ext.getCmp('ac_location').value);
        				Ext.getCmp('acc_usestatus').setValue(Ext.getCmp('ac_usestatus').value);
        				Ext.getCmp('acc_department').setValue(Ext.getCmp('ac_department').value);
        				Ext.getCmp('acc_departmentname').setValue(Ext.getCmp('ac_departmentname').value);
        				Ext.getCmp('acc_addmethod').setValue(Ext.getCmp('ac_addmethod').value);
        				Ext.getCmp('acc_remark').setValue(Ext.getCmp('ac_remark').value);
        				Ext.getCmp('acc_currency').setValue(Ext.getCmp('ac_currency').value);
        				Ext.getCmp('acc_qty').setValue(Ext.getCmp('ac_qty').value);
        				Ext.getCmp('acc_unit').setValue(Ext.getCmp('ac_unit').value);
        				Ext.getCmp('acc_spec').setValue(Ext.getCmp('ac_spec').value);
        				if(typeof (f = Ext.getCmp('acc_useman')) != 'undefined'){
    						Ext.getCmp('acc_useman').setValue(Ext.getCmp('ac_useman').value);
    					}
    				}
    			},
    			blur: function(f){
    				
    			}
    		},
		});
	},
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});