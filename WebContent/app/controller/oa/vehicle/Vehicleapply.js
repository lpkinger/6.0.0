Ext.QuickTips.init();
Ext.define('erp.controller.oa.vehicle.Vehicleapply', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'oa.vehicle.Vehicleapply','core.form.Panel','core.button.Add','core.button.Submit','core.form.ConDateHourMinuteField',
    		'core.button.Audit','core.button.Save','core.button.Close','core.form.TimeMinuteField',
    		'core.button.Update','core.button.Delete','core.button.ResAudit','core.button.Print',
    		'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.trigger.AutoCodeTrigger',
    		'core.button.ResSubmit','core.form.YnField','core.button.TurnVehicle'
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
					var start=new Date(Ext.getCmp('va_time').items.items[5].value);
    				var end=new Date(Ext.getCmp('va_endtime').value);
    				if(start-end>0){
    					showError('预计发车时间输入有误，请检查后重新输入');
    				}else{
    					this.FormUtil.beforeSave(this);
    				}
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				this.FormUtil.beforeClose(this);
    			}
    		},
    		'erpAddButton': {
    			click: function(btn){
    				me.FormUtil.onAdd('addVehicleapply', '新增用车申请单', 'jsps/oa/vehicle/vehicleapply.jsp');
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				var start=new Date(Ext.getCmp('va_time').items.items[5].value);
    				var end=new Date(Ext.getCmp('va_endtime').value);
    				if(start-end>0){
    					showError('预计发车时间输入有误，请检查后重新输入');
    				}else{
    					this.FormUtil.onUpdate(this);
    				}
    			}
    		},
    		'erpDeleteButton': {
    			click: function(btn){
    				me.FormUtil.onDelete((Ext.getCmp('va_id').value));
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
					var status = Ext.getCmp('va_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
    			click: function(btn){
    				this.FormUtil.onAudit(Ext.getCmp('va_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
					var status = Ext.getCmp('va_statuscode');			
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}			
				},
    			click: function(btn){
    				this.FormUtil.onResAudit(Ext.getCmp('va_id').value);
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
					var status = Ext.getCmp('va_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
    			click: function(btn){
    				var start=new Date(Ext.getCmp('va_time').items.items[5].value);
    				var end=new Date(Ext.getCmp('va_endtime').value);
    				if(start-end>0){
    					showError('预计发车时间输入有误，请检查后重新输入');
    				}else{
    					this.FormUtil.onSubmit(Ext.getCmp('va_id').value);
    				}
    				
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
					var status = Ext.getCmp('va_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
    			click: function(btn){
    				this.FormUtil.onResSubmit(Ext.getCmp('va_id').value);
    			}
    		},
    		'erpPrintButton': {
    			afterrender: function(btn){
					var status = Ext.getCmp('va_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				},
    			click: function(btn){
    			 var reportName = '';
                 reportName = "Vehicleapply";
                 var condition = '{Vehicleapply.va_id}=' + Ext.getCmp('va_id').value + '';
                 var id = Ext.getCmp('va_id').value;
                 me.FormUtil.onwindowsPrint(id, reportName, condition);
    			}	
    		},
    		'erpTurnVehicleButton':{
    			afterrender:function(btn){
    				var status = Ext.getCmp('va_statuscode');
    				if(status && status.value!='AUDITED'){
    					btn.hidden();
    				}
    				var va_isback = Ext.getCmp('va_isback');
    				if(va_isback && va_isback.value!='0'){
    					btn.hidden();
    				}
    				btn.setText("回填");
    			},
    			click: function(btn){
    				me.FormUtil.onUpdate(Ext.getCmp('va_id').value);
    			}
    		}
    	});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});