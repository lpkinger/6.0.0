Ext.QuickTips.init();
Ext.define('erp.controller.fs.cust.RecBalanceAssign', {
	extend : 'Ext.app.Controller',
	FormUtil : Ext.create('erp.util.FormUtil'),
	GridUtil : Ext.create('erp.util.GridUtil'),
	BaseUtil : Ext.create('erp.util.BaseUtil'),
	views : ['core.form.Panel', 'fs.cust.RecBalanceAssign', 'core.grid.Panel2','core.toolbar.Toolbar','core.button.TurnBillARChange', 
			'core.button.Submit', 'core.button.Upload','core.button.Audit','core.button.Close','core.trigger.MultiDbfindTrigger',
			'core.button.Delete','core.button.Update','core.button.DeleteDetail','core.button.ResSubmit', 
			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField', 'core.grid.YnColumn'],
	init : function() {
		var me = this;
		this.control({
			'field[name=rad_sacode]':{
    			afterrender:function(t){
    				t.gridKey="ra_custname|ra_currency";
    				t.mappinggirdKey="sa_apcustname|sa_currency";
    				t.gridErrorMessage="请先选择客户名称|请先选择币别";
    			}
    		},
			'erpGridPanel2': { 
    			itemclick: this.onGridItemClick
    		},
			'erpDeleteButton' : {
				click : function(btn) {
					me.FormUtil.onDelete(Ext.getCmp('ra_id').value);
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
					var status = Ext.getCmp('ra_statuscode');
					if (status && status.value != 'ENTERING') {
						btn.hide();
					}
				},
				click : function(btn) {
					me.FormUtil.onSubmit(Ext.getCmp('ra_id').value);
				}
			},
			'erpResSubmitButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('ra_statuscode');
					if (status && status.value != 'COMMITED') {
						btn.hide();
					}
				},
				click : function(btn) {
					me.FormUtil.onResSubmit(Ext.getCmp('ra_id').value);
				}
			},
			'erpAuditButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('ra_statuscode');
					if (status && status.value != 'COMMITED') {
						btn.hide();
					}
				},
				click : function(btn) {
					me.FormUtil.onAudit(Ext.getCmp('ra_id').value);
				}
			},
			'erpTurnBillARChangeButton':{
				beforerender : function(btn){
					btn.text = '转让';
				},
				afterrender : function(btn) {
					btn.setWidth(65);
					var status = Ext.getCmp('ra_statuscode');
					if (status && status.value != 'AUDITED') {
						btn.hide();
					}
				},
				click : function(btn) {
					me.FormUtil.setLoading(true);
					Ext.Ajax.request({
				   		url : basePath + 'fs/cust/assignRecBalance.action',
				   		params : {
				   			caller: caller,
				   			id: Ext.getCmp('ra_id').value
				   		},
				   		method : 'post',
				   		callback : function(options,success,response){	
				   			me.FormUtil.setLoading(false);
				   			var localJson = new Ext.decode(response.responseText);
			    			if(localJson.success){
			    				showMessage('提示', '应收账款转让成功!', 1000);
			    				window.location.reload();
				   			} else if(localJson.exceptionInfo){
			   					showError(localJson.exceptionInfo);
				   				return;
				   			} 
				   		}
					});
				}
			}
		})
	},
	onGridItemClick: function(selModel, record){//grid行选择
		this.GridUtil.onGridItemClick(selModel, record);
	}
});