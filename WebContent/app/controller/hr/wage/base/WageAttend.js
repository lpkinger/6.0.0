Ext.QuickTips.init();
Ext.define('erp.controller.hr.wage.base.WageAttend',{
	extend:'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
	views:[
		'hr.wage.base.WageAttend',
		'core.form.Panel',
		'core.grid.Panel2',
		'core.toolbar.Toolbar',
		'core.trigger.DbfindTrigger',
		'core.form.MonthDateField',
		'core.button.Add','core.button.Save','core.button.Close','core.button.Update','core.button.Delete',
		'core.button.Submit','core.button.ResSubmit','core.button.Audit','core.button.ResAudit'
	],
	init:function(){
		var me = this;
		this.control({
            'erpGridPanel2': {
                afterrender: function(grid) {
                    var status = Ext.getCmp('wa_statuscode');
                    if (status && status.value != 'ENTERING' && status.value != 'COMMITED') {
                        Ext.each(grid.columns,
                        function(c) {
                            c.setEditor(null);
                        });	
                    }  
                },
                itemclick: this.onGridItemClick
            },
    		'erpSaveButton': {
    			click: function(btn){
                	var form = me.getForm(btn), codeField = Ext.getCmp(form.codeField);
					if(codeField.value == null || codeField.value == ''){
						me.BaseUtil.getRandomNumber(caller);//自动添加编号
					}    				
    				this.FormUtil.beforeSave(this);
    			}
    		},
            'erpUpdateButton': {
                afterrender: function(btn) {
                    var status = Ext.getCmp('wa_statuscode');
                    if (status && status.value != 'ENTERING') {
                        btn.hide();
                    }
                },
                click: function(btn) {
                    me.beforeUpdate();
                }
            },
    		'erpDeleteButton': {
                afterrender: function(btn) {
                    var status = Ext.getCmp('wa_statuscode');
                    if (status && status.value == 'AUDITED' || status.value == 'COMMITED') {
                        btn.hide();
                    }
                },
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('wa_id').value);
    			}
    		},
            'erpSubmitButton': {
                afterrender: function(btn) {
                    var status = Ext.getCmp('wa_statuscode');
                    if (status && status.value != 'ENTERING') {
                        btn.hide();
                    }
                },
                click: {	
                    lock: 2000,
	                fn:function(btn) {
                        me.FormUtil.onSubmit(Ext.getCmp('wa_id').value);
	                }
                }
            },
            'erpResSubmitButton': {
                afterrender: function(btn) {
                    var status = Ext.getCmp('wa_statuscode');
                    if (status && status.value != 'COMMITED') {
                        btn.hide();
                    }
                },
                click: {
                    lock: 2000,
	                fn:function(btn) {
                        me.FormUtil.onResSubmit(Ext.getCmp('wa_id').value);
	                }
                }
            },
            'erpAuditButton': {
                afterrender: function(btn) {
                    var status = Ext.getCmp('wa_statuscode');
                    if (status && status.value != 'COMMITED') {
                        btn.hide();
                    }
                },
                click:{ 
    				lock: 2000,
	                fn: function(btn) {
                    	me.FormUtil.onAudit(Ext.getCmp('wa_id').value);
	                }
                }
            },
            'erpResAuditButton': {
                afterrender: function(btn) {
                    var status = Ext.getCmp('wa_statuscode');
                    if (status && status.value != 'AUDITED') {
                        btn.hide();
                    }
                },
                click:{ 
    				lock: 2000,
	                fn: function(btn) {
                  	  	me.FormUtil.onResAudit(Ext.getCmp('wa_id').value);
	                }
                }
            },
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addWageAttend', '新增考勤数据', 'jsps/hr/wage/base/wageattend.jsp');
    			}
    		}
		});
	},
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	beforeUpdate:function(){
		this.FormUtil.onUpdate(this);
	},
    onGridItemClick: function(selModel, record) { //grid行选择	
        this.GridUtil.onGridItemClick(selModel, record);
    }
});