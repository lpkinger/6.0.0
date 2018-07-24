Ext.QuickTips.init();
Ext.define('erp.controller.scm.sale.CustomerCredit', {
	extend : 'Ext.app.Controller',
	FormUtil : Ext.create('erp.util.FormUtil'),
	BaseUtil : Ext.create('erp.util.BaseUtil'),
	views : [ 'scm.sale.CustomerCredit', 'core.form.Panel', 'core.form.MultiField', 'core.form.FileField','core.form.YnField' , 
			'core.button.Add', 'core.button.Save', 'core.button.Close', 'core.button.Upload',
			'core.button.Update', 'core.button.Delete', 'core.button.Sync', 'core.button.Scan',
			'core.button.Submit', 'core.button.ResSubmit', 'core.button.Print',
			'core.button.ResAudit', 'core.button.Audit','core.form.SeparNumber',
			'core.trigger.DbfindTrigger', 'core.trigger.TextAreaTrigger','core.button.FormsDoc'
	],
	init : function() {
		var me = this;
		this.control({
			'erpFormPanel': {
    			afterload: function(form){
    				var main = getUrlParam('main');
					formCondition = getUrlParam('formCondition');
					if(main&&!formCondition){
						me.FormUtil.autoDbfind(caller, 'cuc_custcode', main);
					}
    			}
    		},
			'erpSaveButton' : {
				click : function(btn) {
					var form = me.getForm(btn);
					this.FormUtil.beforeSave(this);
				}
			},
			'erpFormsDocButton':{
        		click: function(btn){
        			btn.defaultOpen = false;
        			var cuc_id=Ext.getCmp('cuc_id').value;
        			var cuc_code=Ext.getCmp('cuc_custcode').value;
        			me.FormUtil.onAdd("CustomerCredit", "附件管理", "jsps/common/formsdoc.jsp?whoami="+caller+"&formsid="+cuc_id+"&formscode="+cuc_code);
        		}
        	},
			'erpUpdateButton' : {
				click : function(btn) {
					this.FormUtil.onUpdate(this);
				}
			},
			'erpAddButton' : {
				click : function() {
					me.FormUtil.onAdd('addCustomerCredit', '新增客户信用额度',
							'jsps/scm/sale/customerCredit.jsp');
				}
			},
			'erpCloseButton' : {
				click : function(btn) {
					me.FormUtil.beforeClose(me);
				}
			},
			'erpDeleteButton' : {
				click : function(btn) {
					me.FormUtil.onDelete(Ext.getCmp('cuc_id').value);
				}
			},
			'erpSubmitButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('cuc_statuscode');
					if (status && status.value != 'ENTERING') {
						btn.hide();
					}
				},
				click : function(btn) {
					me.FormUtil.onSubmit(Ext.getCmp('cuc_id').value);
				}
			},
			'erpResSubmitButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('cuc_statuscode');
					if (status && status.value != 'COMMITED') {
						btn.hide();
					}
				},
				click : function(btn) {
					me.FormUtil.onResSubmit(Ext.getCmp('cuc_id').value);
				}
			},
			'erpAuditButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('cuc_statuscode');
					if (status && status.value != 'COMMITED') {
						btn.hide();
					}
				},
				click : function(btn) {
					me.FormUtil.onAudit(Ext.getCmp('cuc_id').value);
				}
			},
			'erpResAuditButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('cuc_statuscode');
					if (status && status.value != 'AUDITED') {
						btn.hide();
					}
				},
				click : function(btn) {
					me.FormUtil.onResAudit(Ext.getCmp('cuc_id').value);
				}
			},
			'erpPrintButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('cuc_statuscode');
					if (status && status.value != 'AUDITED') {
						btn.hide();
					}
				},
				click : function(btn) {
					var reportName = '';
					reportName = "CustomerCredit";
					var condition = '{CustomerCredit.cuc_id}='
							+ Ext.getCmp('cuc_id').value + '';
					var id = Ext.getCmp('cuc_id').value;
					me.FormUtil.onwindowsPrint(id, reportName, condition);
				}
			}
		});
	},
	getForm : function(btn) {
		return btn.ownerCt.ownerCt;
	}
});