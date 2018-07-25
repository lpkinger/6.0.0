Ext.QuickTips.init();
Ext.define('erp.controller.oa.powerApply.PowerApply', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    views:[
    		'oa.powerApply.PowerApply','core.form.Panel','core.form.FileField','core.form.MultiField','core.form.CheckBoxGroup',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit',
  			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.ResSubmit','core.grid.Panel2',
  			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.trigger.AutoCodeTrigger'
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
					if(parent.Ext.getCmp('powerApplyTab')){//全功能导航打开的权限申请界面
						me.onDelete(Ext.getCmp('pa_id').value);
					}else{
						me.FormUtil.onDelete(Ext.getCmp('pa_id').value);
					}
				}
			},
			'erpUpdateButton': {
				click: function(btn){
					this.FormUtil.onUpdate(this);
				}
			},
			'erpAddButton': {
				click: function(){
					me.FormUtil.onAdd('powerApply', '新增权限申请', 'jsps/oa/powerApply/powerApply.jsp');
				},
				afterrender:function(btn){
					if(parent.Ext.getCmp('powerApplyTab')){//全功能导航打开的权限申请界面
						btn.hide();
					}
				}
			},'erpSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('pa_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onSubmit(Ext.getCmp('pa_id').value);
				}
			},'erpResSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('pa_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResSubmit(Ext.getCmp('pa_id').value);
				}
			},
			'erpAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('pa_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onAudit(Ext.getCmp('pa_id').value);
				}
			},
			'erpResAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('pa_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResAudit(Ext.getCmp('pa_id').value);
				}
			},
			'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				},
				afterrender:function(btn){
					if(parent.Ext.getCmp('powerApplyTab')){//全功能导航打开的权限申请界面
						btn.hide();
					}
				}
			}
    	});
	},
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	onDelete:function(id){
		var me = this;
		warnMsg($I18N.common.msg.ask_del_main, function(btn){
			if(btn == 'yes'){
				var form = Ext.getCmp('form');
				if(!me.FormUtil.contains(form.deleteUrl, '?caller=', true)){
						form.deleteUrl = form.deleteUrl + "?caller=" + caller;
				}
				form.setLoading(true);
				Ext.Ajax.request({
					url : basePath + form.deleteUrl,
					params: {
						id: id
					},
					method : 'post',
					callback : function(options,success,response){
						form.setLoading(false);
						var localJson = new Ext.decode(response.responseText);
						if(localJson.exceptionInfo){
							showError(localJson.exceptionInfo);return;
						}
						if(localJson.success){
							delSuccess(function(){
								window.location.href =basePath +'jsps/oa/powerApply/powerApply.jsp';
								window.location.reload();
							});//@i18n/i18n.js
						} else {
							delFailure();
						}
					}
				});
			}
		});		
	}
});