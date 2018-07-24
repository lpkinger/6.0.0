Ext.QuickTips.init();
Ext.define('erp.controller.hr.emplmana.Recuitinfo', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'hr.emplmana.Recuitinfo','core.form.Panel','core.form.FileField',
    		'core.button.Add','core.button.Save','core.button.Close','core.button.Update',
    		'core.button.Delete','core.form.YnField','core.button.Upload','core.button.DownLoad',
    		'core.button.Submit','core.button.ResSubmit','core.button.Audit','core.button.ResAudit',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.button.Print'
    	],
    init:function(){
    	var me = this;
    	this.control({ 
    	    'erpPrintButton': {
                click: function(btn) {
                    me.onPrint();
                }
            },
    		'erpSaveButton': {
    			click: function(btn){
    				this.FormUtil.beforeSave(this);
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				this.FormUtil.beforeClose(this);
    			}
    		},
    		'erpUpdateButton': {
                afterrender: function(btn) {
                    var status = Ext.getCmp('re_statuscode');
                    if (status && status.value != 'ENTERING') {
                        btn.hide();
                    }
                },
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpDeleteButton': {
                afterrender: function(btn) {
                    var status = Ext.getCmp('re_statuscode');
                    if (status && status.value == 'AUDITED' || status.value == 'COMMITED') {
                        btn.hide();
                    }
                },
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('re_id').value);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addRecuitinfo', '新增应聘人员信息', 'jsps/hr/emplmana/recruitment/recuitinfo.jsp');
    			}
    		},
            'erpSubmitButton': {
                afterrender: function(btn) {
                    var status = Ext.getCmp('re_statuscode');
                    if (status && status.value != 'ENTERING') {
                        btn.hide();
                    }
                },
                click: {	
                    lock: 2000,
	                fn:function(btn) {
                        me.FormUtil.onSubmit(Ext.getCmp('re_id').value);
	                }
                }
            },
            'erpResSubmitButton': {
                afterrender: function(btn) {
                    var status = Ext.getCmp('re_statuscode');
                    if (status && status.value != 'COMMITED') {
                        btn.hide();
                    }
                },
                click: {
                    lock: 2000,
	                fn:function(btn) {
                        me.FormUtil.onResSubmit(Ext.getCmp('re_id').value);
	                }
                }
            },
            'erpAuditButton': {
                afterrender: function(btn) {
                    var status = Ext.getCmp('re_statuscode');
                    if (status && status.value != 'COMMITED') {
                        btn.hide();
                    }
                },
                click:{ 
    				lock: 2000,
	                fn: function(btn) {
                    	me.FormUtil.onAudit(Ext.getCmp('re_id').value);
	                }
                }
            },
            'erpResAuditButton': {
                afterrender: function(btn) {
                    var status = Ext.getCmp('re_statuscode');
                    if (status && status.value != 'AUDITED') {
                        btn.hide();
                    }
                },
                click:{ 
    				lock: 2000,
	                fn: function(btn) {
                  	  	me.FormUtil.onResAudit(Ext.getCmp('re_id').value);
	                }
                }
            }
    	});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});