Ext.QuickTips.init();
Ext.define('erp.controller.hr.wage.wageitem', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'hr.wage.wageitem','core.form.Panel',
    		'core.button.Add','core.button.Save','core.button.Close',
    		'core.button.Update','core.button.Delete','core.form.YnField',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger',
    		'core.button.Submit','core.button.ResSubmit',
    		'core.button.Audit','core.button.ResAudit'
    	],
    init:function(){
    	var me = this;
    	this.control({
    		'field[name=wi_type]': {
   			    change: function(t){
    				var value = t.getValue();
    				if(value){
    					var fieldname = t.up('form').down('field[name=wi_fieldname]');
    					fieldname.autoDbfind = false;
    					fieldname.setHideTrigger(false);
    					fieldname.setReadOnly(false);
    					fieldname.setValue('');
    				}
    				
    			}
    		},    		
    		'field[name=wi_fieldname]': {
    			afterrender: function(t){
    				var value = t.up('form').down('field[name=wi_type]').getValue();
    				if(!value){
    					t.autoDbfind = false;
    					t.setHideTrigger(true);
    					t.setReadOnly(true);
    				}
    			},
    			beforetrigger: function(t){
    				var value = t.up('form').down('field[name=wi_type]').getValue();
    				if(value){
    					t.dbBaseCondition = "abbname='" + value + "'";
    				}
    			}
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
    		'erpCloseButton': {
    			click: function(btn){
    				this.FormUtil.beforeClose(this);
    			}
    		},
    		'erpUpdateButton': {
                afterrender: function(btn) {
                    var status = Ext.getCmp('wi_statuscode');
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
                    var status = Ext.getCmp('wi_statuscode');
                    if (status && status.value == 'AUDITED' || status.value == 'COMMITED') {
                        btn.hide();
                    }
                },    			
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('wi_id').value);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addWageItem', '新增工资项目', 'jsps/hr/wage/wageitem.jsp');
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('wi_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onSubmit(Ext.getCmp('wi_id').value);
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('wi_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('wi_id').value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('wi_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('wi_id').value);
    			}
    		}
/*    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('wi_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('wi_id').value);
    			}
    		}*/
    	});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});