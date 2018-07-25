Ext.QuickTips.init();
Ext.define('erp.controller.oa.info.NotifyJprocess', {
	extend : 'Ext.app.Controller',
	FormUtil : Ext.create('erp.util.FormUtil'),
	BaseUtil : Ext.create('erp.util.BaseUtil'),
	views : [ 'oa.info.Note', 'core.form.Panel', 'core.form.FileField', 'core.form.YnField',
			'core.trigger.MultiDbfindTrigger', 'core.button.Add', 'core.button.Submit', 'core.button.Audit',
			'core.button.Save', 'core.button.Close', 'core.button.Print', 'core.button.Upload', 'core.button.Update',
			'core.button.Delete', 'core.button.ResAudit', 'core.button.ResSubmit', 'core.trigger.DbfindTrigger',
			'core.button.Upload', 'core.button.DownLoad', 'core.button.Scan', 'core.form.YnField',
			'core.trigger.HrOrgTreeDbfindTrigger', 'core.button.Confirm' ],
	init : function() {
		var me = this;
		this.control({
			'erpConfirmButton' : {
				click : function(btn) {// prd_id prd_status = 0
					var id = me.getId();
					if(id)
						me.onConfirm(id);
				}
			},
			'erpFormPanel' : {
				afterload : function() {
					var id = me.getId();
					if(id)
						me.onConfirm(id, true);
				}
			},
			'htmleditor[name=PR_CONTEXT]' : {
				afterrender : function(editor) {
					editor.getToolbar().hide();
					editor.readOnly = true;
					var value=editor.value;
					if(value &&  value.indexOf('close();')>0){
						var otherinfo=value.substring(value.indexOf('javascript:')+11,value.lastIndexOf('close')+8);
						var splits=value.split(otherinfo);
						value=splits[0]+''+splits[1];
					}
					editor.setValue(value.replace(/openFormUrl/g, 'parent.openFormUrl').replace(/openUrl/g,
							'parent.openUrl'));
				}
			},
			'htmleditor[name=IH_CONTEXT]' : {
				afterrender : function(editor) {
					editor.getToolbar().hide();
					editor.readOnly = true;
					var value=editor.value;
					if(value &&  value.indexOf('close();')>0){
						var otherinfo=value.substring(value.indexOf('javascript:')+11,value.lastIndexOf('close')+8);
						var splits=value.split(otherinfo);
						value=splits[0]+''+splits[1];
					}
					editor.setValue(value.replace(/openFormUrl/g, 'parent.openFormUrl').replace(/openUrl/g,
							'parent.openUrl'));
				}
			},
			'erpCloseButton' : {
				click : function(btn) {
					this.FormUtil.beforeClose(this);
				}
			}
		});
	},
	getForm : function(btn) {
		return btn.ownerCt.ownerCt;
	},
	getId : function() {
		var f = Ext.getCmp('ihd_id') || Ext.getCmp('IHD_ID') || Ext.getCmp('prd_id') || Ext.getCmp('PRD_ID');
		return f ? f.getValue() : null;
	},
	onConfirm : function(id, isAuto) {
		var me = this;
		me.FormUtil.setLoading(true);
		Ext.Ajax.request({
			url : basePath + "oa/info/confirmNotifyJprocess.action",
			params : {
				id : id,
				source : source
			},
			method : 'post',
			callback : function(options, success, response) {
				me.FormUtil.setLoading(false);
				var res = new Ext.decode(response.responseText);
				if (res.exceptionInfo != null) {
					showError(res.exceptionInfo);
					return;
				}
				if (!isAuto && res.result) {
					Ext.Msg.alert('提示', res.result, function() {
						parent.Ext.getCmp('content-panel').getActiveTab().close();
					});
				}
			}
		});
	}
});