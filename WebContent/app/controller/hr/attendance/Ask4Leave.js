Ext.QuickTips.init();
Ext.define('erp.controller.hr.attendance.Ask4Leave', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'hr.attendance.Ask4Leave','core.form.Panel','core.button.Add','core.button.Submit','core.button.Confirm','core.button.Print',
    		'core.button.Audit','core.button.Save','core.button.Close','core.form.FileField','core.form.DateHourMinuteComboField',
    		'core.button.Update','core.button.Delete','core.button.ResAudit','core.form.ConDateHourMinuteField',
    		'core.button.ResSubmit','core.form.YnField','core.trigger.DbfindTrigger','core.form.MultiField','erp.view.core.button.Modify','core.form.MonthDateField',
    		'core.button.End','core.button.ResEnd'
    	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'field[name=va_auditstatus]':{
    			beforerender:function(field){
    				var status=Ext.getCmp('va_statuscode').value;
    				if(status=='COMMITED'){
    					field.readOnly=false;
    				}else{
    					field.readOnly=true;
    				}
    			}
    		},
    		'monthdatefield':{//富为期间字段不要默认值
    			beforerender:function(field){
    				field.autoValue=false;
    			}    			
    		},
    		'field[name=va_holidaytype]':{
    			change:function(field,newValue,oldValue,eOpts ){
    				if(newValue=='bytime'){
    					Ext.getCmp('va_alldays').setValue(0);
    					Ext.getCmp('va_alldays').setReadOnly(true);
    					Ext.getCmp('va_alltimes').setReadOnly(false);
    					Ext.getCmp('va_alltimes').setValue();
    				}
    				if(newValue=='byday'){
    					Ext.getCmp('va_alltimes').setValue(0);
    					Ext.getCmp('va_alltimes').setReadOnly(true);
    					Ext.getCmp('va_alldays').setReadOnly(false);
    					Ext.getCmp('va_alldays').setValue();
    				}
    			}
    			
    		},
    		'field[name=va_alldays]':{
    			afterrender:function(filed){
    				var holidaytype=Ext.getCmp('va_holidaytype').value;
    				if(holidaytype=='bytime'){
    					Ext.getCmp('va_alldays').setValue(0);
    					Ext.getCmp('va_alldays').setReadOnly(true);
    					Ext.getCmp('va_alltimes').setReadOnly(false);
    					/*Ext.getCmp('va_alltimes').setValue();*/
    				}
    				if(holidaytype=='byday'){
    					Ext.getCmp('va_alltimes').setValue(0);
    					Ext.getCmp('va_alltimes').setReadOnly(true);
    					Ext.getCmp('va_alldays').setReadOnly(false);
    					/*Ext.getCmp('va_alldays').setValue();*/
    				}
    			}
    		},
    		'field[name=va_vacationtype]':{
    			afterrender:function(field){
    				var va_vacationtype = Ext.getCmp('va_vacationtype').value;
    				if(va_vacationtype=='年假'){
    					if(Ext.getCmp('em_number1')) Ext.getCmp('em_number1').show();
    					if(Ext.getCmp('em_resttime')) Ext.getCmp('em_resttime').hide();
    				}else if(va_vacationtype=='调休'){
    					if(Ext.getCmp('em_number1')) Ext.getCmp('em_number1').hide();
    					if(Ext.getCmp('em_resttime')) Ext.getCmp('em_resttime').show();
    				}else{
    					if(Ext.getCmp('em_resttime')) Ext.getCmp('em_resttime').hide();
    					if(Ext.getCmp('em_number1')) Ext.getCmp('em_number1').hide();
    				}
    			},
    			change:function(field,newValue,oldValue,eOpts ){
    				if(newValue=='调休'){
    					if(Ext.getCmp('em_number1')) Ext.getCmp('em_number1').hide();
    					if(Ext.getCmp('em_resttime')) Ext.getCmp('em_resttime').show();
    					
    				}else if(newValue=='年假'){
    					if(Ext.getCmp('em_number1')) Ext.getCmp('em_number1').show();
    					if(Ext.getCmp('em_resttime')) Ext.getCmp('em_resttime').hide();
    				}else{
    					if(Ext.getCmp('em_resttime')) Ext.getCmp('em_resttime').hide();
    					if(Ext.getCmp('em_number1')) Ext.getCmp('em_number1').hide();
    				}
    			}
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber();// 自动添加编号
    				}
    				this.FormUtil.beforeSave(this);
    			}
    		},
    		'erpPrintButton': {
                click: function(btn) {
                	 var reportName='';
                	 reportName="AskLeave";
                	 var id = Ext.getCmp('va_id').value;
                     me.FormUtil.onwindowsPrint2(id, reportName, "");
                }
            },
    		'erpCloseButton': {
    			click: function(btn){
    				this.FormUtil.beforeClose(this);
    			}
    		},
    		
    		'erpAddButton': {
    			click: function(btn){
    				me.FormUtil.onAdd('addAsk4Leave', '新增请假申请单', 'jsps/hr/attendance/ask4leave.jsp');
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
	    			this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpDeleteButton': {
    			click: function(btn){
    				me.FormUtil.onDelete((Ext.getCmp('va_id').value));
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
					var statu = Ext.getCmp('va_statuscode');
					if(statu && statu.value != 'COMMITED'){
						btn.hide();
					}
				},
    			click: function(btn){
    				/*
					 * var auditstatus=Ext.getCmp('va_auditstatus');
					 * if(auditstatus && auditstatus.value=='待批准'){
					 * showError('请选择审核结果！'); return; }
					 * console.log(auditstatus); var value=auditstatus == null ?
					 * '':auditstatus.value;
					 * me.onAudit(Ext.getCmp('va_id').value,value);
					 */
    				this.FormUtil.onAudit(Ext.getCmp('va_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
					var statu = Ext.getCmp('va_statuscode');
					if(statu && statu.value != 'AUDITED'){
						btn.hide();
					}
				},
    			click: function(btn){
    				this.FormUtil.onResAudit(Ext.getCmp('va_id').value);
    			}
    		},
    		'erpSubmitButton': {afterrender: function(btn){
				var statu = Ext.getCmp('va_statuscode');
				if(statu && statu.value != 'ENTERING'){
					btn.hide();
				}
			},
    			click: function(btn){
    				var me=this;
    				var form = me.getForm(btn);
    				var id=Ext.getCmp('va_id').value;
    				var type=Ext.getCmp('va_vacationtype');//假期类型
    				if(type&&type.value=='病假'){
    					me.sickCheck(form,id);
    				}else{
    					me.FormUtil.onSubmit(Ext.getCmp('va_id').value);
    				}	
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
					var statu = Ext.getCmp('va_statuscode');
					if(statu && statu.value != 'COMMITED'){
						btn.hide();
					}
				},
    			click: function(btn){
    				this.FormUtil.onResSubmit(Ext.getCmp('va_id').value);
    			}
    		},
    		'erpConfirmButton': {
    		afterrender: function(btn){
				var statu = Ext.getCmp('va_statuscode');
				if(statu && statu.value != 'AUDITED'){
					btn.hide();
				}
			},
    			click: function(btn){    				
    				me.onConfirm(Ext.getCmp('va_id').value);
    				
    			}
    		},
    		'erpEndButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('va_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onEnd(Ext.getCmp('va_id').value);
    			}
    		},
    		'erpResEndButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('va_statuscode');
    				if(status && status.value != 'FINISH'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResEnd(Ext.getCmp('va_id').value);
    			}
    		}
    	});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	onConfirm: function(id){
		var form = Ext.getCmp('form');	
		Ext.Ajax.request({
	   		url : basePath + form.confirmUrl,
	   		params: {
	   			id: id,
	   			caller:caller
	   		},
	   		method : 'post',
	   		callback : function(options,success,response){
	   			// me.setLoading(false);
	   			var localJson = new Ext.decode(response.responseText);
    			if(localJson.success){
    				// audit成功后刷新页面进入可编辑的页面
    				// auditSuccess(function(){
    				showMessage("提示", '确认成功');
	   					window.location.reload();
	   				// });
	   			} else {
    				if(localJson.exceptionInfo){
    	   				var str = localJson.exceptionInfo;
    	   				if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){// 特殊情况:操作成功，但是出现警告,允许刷新页面
    	   					str = str.replace('AFTERSUCCESS', '');
    	   					showMessage("提示", '确认成功');
    	   					// auditSuccess(function(){
    	   						window.location.reload();
    	   					// });
    	   				} else {
    	   					showError(str);return;
    	   				}
    	   			}
    			}
	   		}
		});
	},
	sickCheck:function(form,id){
    	var me=this;
    	me.BaseUtil.getSetting('Ask4Leave', 'vacation_sick_check', function(v) {
			if(v){
				form.setLoading(true);
				Ext.Ajax.request({
					url : basePath + 'oa/check/sickCheck.action',
					params: {
						id: id,
						caller:caller,
						_noc:1
					},
					method : 'post',
					callback : function(options,success,response){
						form.setLoading(false);
						var localJson = new Ext.decode(response.responseText);
						if(localJson.exceptionInfo){
							showError(localJson.exceptionInfo);return;
						}
						if(localJson.success){
							if(localJson.result=='6'){
								warnMsg("累计病假天数为："+localJson.sickdays+"天，继续提交将清除剩余年假天数，是否继续？",
								function(btn){
									if(btn == 'yes'){
										Ext.Ajax.request({//清除年假em_number1
					    					url : basePath + 'oa/check/cleanEmpdays.action',
					    					params : {
					    						id: id,
												caller:caller,
												_noc:1
					    					},
					    					callback : function(opt, s, res) {
					    						var r = Ext.decode(res.responseText);
					    						if(r.exceptionInfo){
													showError(r.exceptionInfo);return;
												}
					    						if (r.success) {
					    							 me.FormUtil.onSubmit(Ext.getCmp('va_id').value);
					    						}
					    					}
					    				});
									}
								});
							}else{
							   	me.FormUtil.onSubmit(Ext.getCmp('va_id').value);
							}
						}
					}
				});
			}else{
				me.FormUtil.onSubmit(Ext.getCmp('va_id').value);
			}
		});  				
	},
	onAudit: function(id,auditstatus){
		var form = Ext.getCmp('form');	
		Ext.Ajax.request({
	   		url : basePath + form.auditUrl,
	   		params: {
	   			id: id,
	   			auditstatus:auditstatus
	   		},
	   		method : 'post',
	   		callback : function(options,success,response){
	   			// me.setLoading(false);
	   			var localJson = new Ext.decode(response.responseText);
    			if(localJson.success){
    				// audit成功后刷新页面进入可编辑的页面
    				auditSuccess(function(){
	   					window.location.reload();
	   				});
    				
	   			} else {
    				if(localJson.exceptionInfo){
    	   				var str = localJson.exceptionInfo;
    	   				if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){// 特殊情况:操作成功，但是出现警告,允许刷新页面
    	   					str = str.replace('AFTERSUCCESS', '');
    	   					showMessage("提示", str);
    	   					auditSuccess(function(){
    	   						window.location.reload();
    	   					});
    	   				} else {
    	   					showError(str);return;
    	   				}
    	   			}
    			}
	   		}
		});
	}
});