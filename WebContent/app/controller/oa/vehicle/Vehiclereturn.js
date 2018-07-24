Ext.QuickTips.init();
Ext.define('erp.controller.oa.vehicle.Vehiclereturn', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'oa.vehicle.Vehiclereturn','core.form.Panel','core.form.FileField','core.form.MultiField','core.grid.Panel2','core.toolbar.Toolbar',
    		'core.button.Add','core.button.Submit','core.button.Audit','core.button.Save','core.button.Close','core.button.Print',
    			'core.button.Upload','core.button.Update','core.button.Delete','core.button.ResAudit','core.button.ResSubmit',
    			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.trigger.AutoCodeTrigger','core.form.ConDateHourMinuteField',
    			'core.form.YnField','core.trigger.DbfindTrigger','core.button.Scan','core.button.RefreshSendDate'
    	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'erpGridPanel2': {    			
    			
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
						me.BaseUtil.getRandomNumber();//自动添加编号
					}
    				this.FormUtil.beforeSave(this);
    			}
    		},
    		'erpDeleteButton' : {
				afterrender: function(btn){
					var status = Ext.getCmp('vr_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onDelete(Ext.getCmp('vr_id').value);
				}
			},
			'erpUpdateButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('vr_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					this.FormUtil.onUpdate(this);
				}
			},
			'erpAddButton': {
				click: function(){
					me.FormUtil.onAdd('addVehiclereturn', '新增维修保养项目', 'jsps/oa/vehicle/vehiclereturn.jsp');
				}
			},
			'erpCloseButton': {
				afterrender:function(btn){
					if(cond!=''){
						var con=cond.split('=');
						cond=con[0]+'=\''+con[1]+'\'';
						me.getVehicleapply(cond);
					}
				},
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			},'erpSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('vr_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onSubmit(Ext.getCmp('vr_id').value);
				}
			},
			'erpResSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('vr_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResSubmit(Ext.getCmp('vr_id').value);
				}
			},
			'erpAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('vr_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onAudit(Ext.getCmp('vr_id').value);
				}
			},'erpResAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('vr_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResAudit(Ext.getCmp('vr_id').value);
				}
			},'erpPrintButton': {
    			afterrender: function(btn){
					var status = Ext.getCmp('vr_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				},    					
				click: function(btn){
				var reportName = 'Vehiclereturn',
				id = Ext.getCmp('vr_id').getValue(),
				condition = '{Vehiclereturn.vr_id}=' + id;
				me.FormUtil.onwindowsPrint(id, reportName, condition);
			}},
			'erpRefreshSendDateButton': {		
				afterrender: function(btn){
					var status = Ext.getCmp('vr_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				},   
				click: function(btn){
					var form = Ext.getCmp('form'), r = form.getValues();
					var formStore = unescape(escape(Ext.JSON.encode(r)));
					if(formStore) {
						Ext.Ajax.request({
							url : basePath + 'oa/vehicle/refreshSendTime.action',
							params: {
								formStore:formStore
							},
							method : 'post',
							callback : function(options,success,response){
								var localJson = new Ext.decode(response.responseText);
								if(localJson.success){
									showMessage('提示', '发车时间更新成功!', 1000);
									window.location.reload();
								} else if(localJson.exceptionInfo){
									var str = localJson.exceptionInfo;
									showError(str);return;
								}
							}
						});
					}
				}
			},	
			'field[name=vr_realsendtime]':{
				beforerender: function(field){
					field.readOnly=false;
				}
			}
    	});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	getVehicleapply:function(condi){
		Ext.Ajax.request({
	   		url : basePath + 'common/autoDbfind.action',
	   		params: {
	   			which: 'form',
	   			caller: 'Vehiclereturn',
	   			field: 'vr_vacode',
	   			condition: condi//vr_vacode like '%2013100002%'
	   		},
	   		async: false,
	   		method : 'post',
	   		callback : function(options,success,response){
	   			var res = new Ext.decode(response.responseText);
	   			if(res.exceptionInfo){
	   				showError(res.exceptionInfo);return;
	   			}
	   			if(res.data){
	   				var data = Ext.decode(res.data.replace(/,}/g, '}').replace(/,]/g, ']'));
	   				Ext.Array.each(res.dbfinds,function(ds){
	   					Ext.getCmp(ds.field).setValue(data[0][ds.dbGridField]);
	   				});
	   			} 
	   		}
	   });
	}
});