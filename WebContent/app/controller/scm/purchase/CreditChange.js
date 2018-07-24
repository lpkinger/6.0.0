Ext.QuickTips.init();
Ext.define('erp.controller.scm.purchase.CreditChange', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'scm.purchase.CreditChange','core.form.Panel','core.form.MultiField','core.form.FileField',
    		'core.button.Add','core.button.Save','core.button.Close','core.button.Upload','core.button.Update',
    			'core.button.Delete','core.button.Sync','core.button.Submit','core.button.ResSubmit','core.button.Print',
    			'core.button.ResAudit','core.button.Audit',
    		'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField'
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
      				me.FormUtil.onAdd('addCreditChange', '新增供应商信用变更', 'jsps/scm/purchase/creditChange.jsp');
      			}
      		},
      		'erpCloseButton': {
      			click: function(btn){
      				me.FormUtil.beforeClose(me);
      			}
      		},
      		'erpDeleteButton' : {
				click: function(btn){
					me.FormUtil.onDelete(Ext.getCmp('vc_id').value);
				}
			},
			'erpSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('vc_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onSubmit(Ext.getCmp('vc_id').value);
				}
			},
			'erpResSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('vc_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResSubmit(Ext.getCmp('vc_id').value);
				}
			},
			'erpAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('vc_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onAudit(Ext.getCmp('vc_id').value);
				}
			},
			'erpResAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('vc_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResAudit(Ext.getCmp('vc_id').value);
				}
			},
			'erpPrintButton': {
				click: function(btn){
					me.FormUtil.onPrint(Ext.getCmp('vc_id').value);
				}
			},
			'textfield[name=vc_vendcode]': {
    			aftertrigger: function(field){
    				if(field != null && field != ''){
        				Ext.getCmp('vc_newcredit').setValue(Ext.getCmp('vc_credit').value);
    				}
    			}
    		}
      	});
      },
      getForm: function(btn){
      	return btn.ownerCt.ownerCt;
      }
});