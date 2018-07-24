Ext.QuickTips.init();
Ext.define('erp.controller.hr.attendance.SignCard', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    views:[ 'hr.attendance.SignCard','core.form.Panel','core.grid.Panel2','core.button.Add','core.button.Save','core.button.Update','core.button.Delete',
    		'core.button.Submit','core.button.ResSubmit','core.button.Audit','core.button.ResAudit','core.button.Close','core.button.Print','core.button.ReLoad',
    		'core.form.FileField','core.form.MultiField','core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.trigger.MultiDbfindTrigger',
    		'core.form.YnField','core.trigger.DbfindTrigger','core.form.ConDateHourMinuteField','core.form.EmpSelectField','core.form.DateHourMinuteField',
    		'core.button.End','core.button.ResEnd'
    		],
    init:function(){
    	var me = this;
    	this.control({
    		'erpGridPanel2': { 
    			
    		},
    		'erpAddButton': {
				click: function(){
					me.FormUtil.onAdd('addSignCard', '新增批次签卡', 'jsps/hr/attendance/signCard.jsp');
				}
			},
			'erpSaveButton': {
    			click: function(btn){
    				var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
						me.BaseUtil.getRandomNumber();//自动添加编号
					}
					if(!Ext.getCmp('sc_emids').getValue()){
						showError("请选择员工");
					}else{
						me.FormUtil.beforeSave(this);  
					}
    			}
    		},
    		'erpUpdateButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('sc_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					if(!Ext.getCmp('sc_emids').getValue()){
						showError("请选择员工");
					}else{
						me.FormUtil.onUpdate(this);
					}	
				}
			},    		
			'erpDeleteButton' : {
				afterrender: function(btn){
					var status = Ext.getCmp('sc_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onDelete(Ext.getCmp('sc_id').value);
				}
			},
    		'erpSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('sc_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
    				me.FormUtil.onSubmit(Ext.getCmp('sc_id').value);
				}
			},
			'erpResSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('sc_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResSubmit(Ext.getCmp('sc_id').value);
				}
			},
			'erpAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('sc_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('sc_id').value);
				}
			},
			'erpResAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('sc_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResAudit(Ext.getCmp('sc_id').value);
				}
			},
			'erpCloseButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('sc_statuscode');
					if(status && status.value == 'ENTERING'){
						Ext.getCmp('grid').hide();
					}
				},
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			},
			'erpEndButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('sc_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				this.FormUtil.onEnd(Ext.getCmp('sc_id').value);
    			}
    		},
    		'erpResEndButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('sc_statuscode');
    				if(status && status.value != 'FINISH'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				this.FormUtil.onResEnd(Ext.getCmp('sc_id').value);
    			}
    		}
    	});
    },
    onGridItemClick: function(selModel, record){//grid行选择
		this.GridUtil.onGridItemClick(selModel, record);
	},
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});