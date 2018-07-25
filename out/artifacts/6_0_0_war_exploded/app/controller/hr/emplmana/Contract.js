Ext.QuickTips.init();
Ext.define('erp.controller.hr.emplmana.Contract', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'hr.emplmana.Contract','core.form.Panel','core.form.MultiField','core.form.FileField',
    		'core.button.Add','core.button.Save','core.button.Close','core.button.Submit','core.button.ResSubmit',
    		'core.button.ResAudit','core.button.Audit','core.button.Update','core.button.Delete',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.form.YnField'
    	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'erpSaveButton': {
    			click: function(btn){
    				var statdate = Ext.getCmp('co_begintime').value,
						enddate = Ext.getCmp('co_endtime').value;
    				if(!Ext.isEmpty(enddate)){
    					if(Ext.Date.format(statdate,'Y-m-d') > Ext.Date.format(enddate,'Y-m-d')){
    						showError('结束时间不能小于开始时间！');return;
    					}
    					if(Ext.Date.format(enddate,'Y-m-d') < Ext.Date.format(new Date(),'Y-m-d')){
    						showError('结束时间不能小于当前日期!');return;
    					}
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
    			click: function(btn){
    				var statdate = Ext.getCmp('co_begintime').value,
						enddate = Ext.getCmp('co_endtime').value;
					if(!Ext.isEmpty(enddate)){
						if(Ext.Date.format(statdate,'Y-m-d') > Ext.Date.format(enddate,'Y-m-d')){
							showError('结束时间不能小于开始时间！');return;
						}
						if(Ext.Date.format(enddate,'Y-m-d') < Ext.Date.format(new Date(),'Y-m-d')){
							showError('结束时间不能小于当前日期!');return;
						}
					}
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpDeleteButton': {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('co_id').value);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addContract', '新增合同', 'jsps/hr/emplmana/contract/contract.jsp');
    			}
    		},
    		'erpSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('co_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onSubmit(Ext.getCmp('co_id').value);
				}
			},
			'erpResSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('co_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResSubmit(Ext.getCmp('co_id').value);
				}
			},
			'erpAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('co_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onAudit(Ext.getCmp('co_id').value);
				}
			},
			'erpResAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('co_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResAudit(Ext.getCmp('co_id').value);
				}
			}
    	});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});