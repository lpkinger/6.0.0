Ext.QuickTips.init();
Ext.define('erp.controller.fa.ars.FARepSet', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','fa.ars.FARepSet','core.grid.Panel2','core.toolbar.Toolbar',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Upload','core.button.ResAudit',
  			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail','core.button.ResSubmit',
  			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField', 'core.button.CopyAll','core.button.Sync'
  	],
	init:function(){
		var me = this;
		me.gridLastSelected = null;
		this.control({
			'erpGridPanel2': { 
				itemclick: this.onGridItemClick
			},
			'erpSaveButton': {
				click: function(btn){
					var form = me.getForm(btn);
					if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
						me.BaseUtil.getRandomNumber();//自动添加编号
					}
					this.FormUtil.beforeSave(this);
				}
			},
			'erpDeleteButton' : {
				click: function(btn){
					me.FormUtil.onDelete(Ext.getCmp('fs_id').value);
				}
			},
			'erpUpdateButton': {
				click: function(btn){
					this.FormUtil.onUpdate(this);
				}
			},
			'erpAddButton': {
				click: function(){
					me.FormUtil.onAdd('addFARepSet', '新增报表结构资料维护', 'jsps/fa/ars/fARepSet.jsp');
				}
			},
			'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			},
			'erpSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('fs_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onSubmit(Ext.getCmp('fs_id').value);
				}
			},
			'erpResSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('fs_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResSubmit(Ext.getCmp('fs_id').value);
				}
			},
			'erpAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('fs_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onAudit(Ext.getCmp('fs_id').value);
				}
			},
			'erpResAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('fs_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResAudit(Ext.getCmp('fs_id').value);
				}
			},
			'erpCopyButton': {
    			click: function(btn) {
    				this.copy();
    			}
    		}
		});
	}, 
	onGridItemClick: function(selModel, record){//grid行选择
		this.GridUtil.onGridItemClick(selModel, record);
	},
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	/**
	 * 复制
	 */
	copy: function(){
		var form = Ext.getCmp('form');
		var v = Ext.getCmp('fs_id').value;
		if(v > 0) {
			form.setLoading(true);
			Ext.Ajax.request({
				url: basePath + 'fa/ars/copyFARepSet.action',
				params: {
					id: v,
					caller: caller
				},
				callback: function(opt, s, r){
					form.setLoading(false);
					var res = Ext.decode(r.responseText);
					if(res.farepset) {
						showMessage('提示', '复制成功！<a href="javascript:openUrl(\'jsps/fa/ars/fARepSet.jsp?formCondition=fs_idIS' + 
								 + res.farepset.fs_id + '&gridCondition=fsd_fsidIS' + res.farepset.fs_id + 
								'\')">\n报表编号:&lt;' + res.farepset.fs_code + '&gt;</a>');
					} else {
						showError(res.exceptionInfo);
					}
				}
			});
		}
	}
});