Ext.QuickTips.init();
Ext.define('erp.controller.hr.emplmana.Trainassess', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'hr.emplmana.Trainassess','core.form.Panel','core.button.Update','core.button.Delete','core.form.YnField','core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger',
    		'core.button.Add','core.button.Save','core.button.Close','core.button.Submit','core.button.ResSubmit','core.button.Audit','core.button.ResAudit'
    		
    	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'field[name=ta_grade]':{
    			afterrender:function(f){
    				f.setMaxValue(100);
    			}
    		},
    		'field[name=ta_score]':{
    			afterrender:function(f){
    				f.setMaxValue(100);
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
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpDeleteButton': {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('ta_id').value);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addTrainassess', '新增培训评估', 'jsps/hr/emplmana/train/trainassess.jsp');
    			}
    		},
    		'erpSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('ta_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onSubmit(Ext.getCmp('ta_id').value);
				}
			},
			'erpResSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('ta_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResSubmit(Ext.getCmp('ta_id').value);
				}
			},
			'erpAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('ta_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onAudit(Ext.getCmp('ta_id').value);
				}
			}
    		
    	});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});