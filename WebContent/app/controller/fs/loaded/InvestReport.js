Ext.QuickTips.init();
Ext.define('erp.controller.fs.loaded.InvestReport', {
	extend : 'Ext.app.Controller',
	FormUtil : Ext.create('erp.util.FormUtil'),
	GridUtil : Ext.create('erp.util.GridUtil'),
	BaseUtil : Ext.create('erp.util.BaseUtil'),
	views : ['core.form.Panel', 'fs.loaded.InvestReport', 'core.grid.Panel2','core.toolbar.Toolbar',
			'core.button.Save', 'core.button.Add','core.button.Submit', 'core.button.Upload','core.button.ResAudit',
			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.ResSubmit',
			'core.button.Export','core.button.TurnBankRegister','core.button.PrintByCondition',
			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField', 'core.grid.YnColumn',
			'core.form.StatusField','core.form.FileField','core.form.MultiField','core.trigger.AddDbfindTrigger',
			'core.trigger.MultiDbfindTrigger', 'core.form.SeparNumber','core.button.FormsDoc','core.button.InfoPerfect'],
	init : function() {
		var me = this;
		this.control({
			'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('InvestReport', '授信后调查报告', 'jsps/fs/loaded/investReport.jsp');
    			}
        	},
        	'erpInfoPerfectButton': {
				click : function(btn) {
					var li_id = Ext.getCmp('li_id'),liid=null;
					if(li_id&&li_id.value){
						liid = li_id.value;
					}
					var status = Ext.getCmp('li_statuscode');
					if (status && status.value == 'ENTERING') {
						me.FormUtil.setLoading(true);
						Ext.Ajax.request({
					   		url : basePath + 'fs/loaded/getDefaultDatas.action',
					   		params : {id:liid},
					   		method : 'post',
					   		callback : function(options,success,response){	
					   			me.FormUtil.setLoading(false);
					   			var localJson = new Ext.decode(response.responseText);
				    			if(localJson.exceptionInfo){
					   				str = localJson.exceptionInfo;
				   					showError(str);
					   			}
					   			me.FormUtil.onAdd('ReportContent'+liid,'调查报告内容', 'jsps/fs/loaded/reportContent.jsp?formCondition='+formCondition);
					   		}
						});
					}else{
						me.FormUtil.onAdd('ReportContent'+liid,'调查报告内容', 'jsps/fs/loaded/reportContent.jsp?formCondition='+formCondition);
					}
				}
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
				click : function(btn) {
					me.FormUtil.onDelete(Ext.getCmp('li_id').value);
				}
			},
			'erpUpdateButton' : {
				click : function(btn) {
					me.FormUtil.onUpdate(this);
				}
			},
			'erpCloseButton' : {
				click : function(btn) {
					me.FormUtil.beforeClose(me);
				}
			},
			'erpSubmitButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('li_statuscode');
					if (status && status.value != 'ENTERING') {
						btn.hide();
					}
				},
				click : function(btn) {
					me.FormUtil.onSubmit(Ext.getCmp('li_id').value);
				}
			},
			'erpResSubmitButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('li_statuscode');
					if (status && status.value != 'COMMITED') {
						btn.hide();
					}
				},
				click : function(btn) {
					me.FormUtil.onResSubmit(Ext.getCmp('li_id').value);
				}
			},
			'erpAuditButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('li_statuscode');
					if (status && status.value != 'COMMITED') {
						btn.hide();
					}
				},
				click : function(btn) {
					me.FormUtil.onAudit(Ext.getCmp('li_id').value);
				}
			},
			'erpResAuditButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('li_statuscode');
					if (status && status.value != 'AUDITED') {
						btn.hide();
					}
				},
				click : function(btn) {
					me.FormUtil.onResAudit(Ext.getCmp('li_id').value);
				}
			}
		})
	},
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});