Ext.QuickTips.init();
Ext.define('erp.controller.hr.attendance.outAddressSet', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'hr.attendance.outAddressSet','core.form.Panel','core.form.FileField','core.form.MultiField',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit',
  			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.ResSubmit',
  			'core.button.Flow','core.button.DownLoad','core.button.Scan','common.datalist.Toolbar','core.form.ColorField',
  			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.trigger.LongitudeAndLatitude',
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
					me.beforeSave('save');
				}
			},
			'erpDeleteButton' : {
				click: function(btn){
					me.FormUtil.onDelete(Ext.getCmp('os_id').value);
				}
			},
			'erpUpdateButton': {
				click: function(btn){
					this.FormUtil.onUpdate(this);
				}
			},
			'erpSubmitButton': {
				afterrender: function(btn){
					var statu = Ext.getCmp('os_statuscode');
					if(statu && statu.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onSubmit(Ext.getCmp('os_id').value);
				}
			},
			'erpResSubmitButton': {
				afterrender: function(btn){
					var statu = Ext.getCmp('os_statuscode');
					if(statu && statu.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResSubmit(Ext.getCmp('os_id').value);
				}
			},
			'erpAuditButton': {
				afterrender: function(btn){
					var statu = Ext.getCmp('os_statuscode');
					if(statu && statu.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onAudit(Ext.getCmp('os_id').value);
				}
			},
			'erpResAuditButton': {
				afterrender: function(btn){
					var statu = Ext.getCmp('os_statuscode');
					if(statu && statu.value != 'AUDITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResAudit(Ext.getCmp('os_id').value);
				}
			},
			'erpAddButton': {
				click: function(){
					me.FormUtil.onAdd('addoutAddressSet', '新增外勤地址设置', 'jsps/hr/attendance/outaddressset.jsp');
				}
			},
			'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			}
    	});
	},
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},

	beforeSave: function(type){
		var longitude = Ext.getCmp('os_longitude');
		var latitude = Ext.getCmp('os_latitude');
		if(longitude.value==""||latitude.value==""){
			showError("请点击办公地址输入框后的按钮获取经纬度");
			return;
		}
		if(type='save');
			this.FormUtil.beforeSave(this);		
	}
});
