Ext.QuickTips.init();
Ext.define('erp.controller.hr.attendance.SpeAttendance', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'hr.attendance.Ask4Leave','core.form.Panel','core.button.Add','core.button.Submit','core.button.Confirm',
    		'core.button.Audit','core.button.Save','core.button.Close','core.button.End','core.button.ResEnd',
    		'core.button.Update','core.button.Delete','core.button.ResAudit','core.form.ConDateHourMinuteField',
    		'core.button.ResSubmit','core.form.YnField','core.trigger.DbfindTrigger','core.form.MultiField','core.button.Modify'
    	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'field[name=sa_status]':{
    			beforerender:function(field){
    				var status=Ext.getCmp('sa_statuscode').value;
    				if(status=='COMMITED'){
    					field.readOnly=false;
    				}else{
    					field.readOnly=true;
    				}
    			}
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				/*var start=Ext.getCmp('va_startime').value,
				    end=Ext.getCmp('va_endtime').value,
				    organigerdate=Ext.getCmp('va_date').value;
				    if(end.getTime()<start.getTime()){
				      showError('开始时间不能大于结束时间!');
				      return;
				      } else if(start.getTime()<organigerdate.getTime()){
				         showError('开始时间不能小于录入时间');
				        return;
				      }	*/			   
    				var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber();//自动添加编号
    				}
    				this.FormUtil.beforeSave(this);
    				
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				this.FormUtil.beforeClose(this);
    			}
    		},
    		'erpAddButton': {
    			click: function(btn){
    				me.FormUtil.onAdd('SpeAttendance', '新增特殊考勤', 'jsps/hr/attendance/speattendance.jsp');
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				/*var start=Ext.getCmp('va_startime').value,
				    end=Ext.getCmp('va_endtime').value,
				    organigerdate=Ext.getCmp('va_date').value;
				    if(end.getTime()<start.getTime()){
				      showError('开始时间不能大于结束时间!');
				      return;
				      } else if(start.getTime()<organigerdate.getTime()){
				         showError('开始时间不能小于录入时间');
				        return;
				      }	*/
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpDeleteButton': {
    			click: function(btn){
    				me.FormUtil.onDelete((Ext.getCmp('sa_id').value));
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
					var statu = Ext.getCmp('sa_statuscode');
					if(statu && statu.value != 'COMMITED'){
						btn.hide();
					}
				},
    			click: function(btn){
    				/*var auditstatus=Ext.getCmp('sa_status').value;
    				if(auditstatus=='待批准'){
    					showError('请选择审核结果！');
    					return;
    				}*/
    				this.FormUtil.onAudit(Ext.getCmp('sa_id').value);
    				//this.FormUtil.onAudit(Ext.getCmp('va_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
					var statu = Ext.getCmp('sa_statuscode');
					if(statu && statu.value != 'AUDITED'){
						btn.hide();
					}
				},
    			click: function(btn){
    				this.FormUtil.onResAudit(Ext.getCmp('sa_id').value);
    			}
    		},
    		'erpSubmitButton': {afterrender: function(btn){
				var statu = Ext.getCmp('sa_statuscode');
				if(statu && statu.value != 'ENTERING'){
					btn.hide();
				}
			},
    			click: function(btn){
    				/*var start=Ext.getCmp('va_startime').value,
				    end=Ext.getCmp('va_endtime').value,
				    organigerdate=Ext.getCmp('va_date').value;
				    if(end.getTime()<start.getTime()){
				      showError('开始时间不能大于结束时间!');
				      return;
				      } else if(start.getTime()<organigerdate.getTime()){
				         showError('开始时间不能小于录入时间');
				        return;
				      }	*/	
    				this.FormUtil.onSubmit(Ext.getCmp('sa_id').value);
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
					var statu = Ext.getCmp('sa_statuscode');
					if(statu && statu.value != 'COMMITED'){
						btn.hide();
					}
				},
    			click: function(btn){
    				this.FormUtil.onResSubmit(Ext.getCmp('sa_id').value);
    			}
    		} ,
    		'erpConfirmButton': {
    			afterrender: function(btn){
					var statu = Ext.getCmp('sa_statuscode');
					if(statu && statu.value != 'AUDITED'){
						btn.hide();
					}
				},
    			click: function(btn){    				
    				me.onConfirm(Ext.getCmp('sa_id').value);
    			}
    		},   
    		'erpEndButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('sa_statuscode');console.log(status);
    				if(status && status.value!= 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				this.FormUtil.onEnd(Ext.getCmp('sa_id').value);
    			}
    		},
    		'erpResEndButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('sa_statuscode');
    				if(status && status.value != 'FINISH'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				this.FormUtil.onResEnd(Ext.getCmp('sa_id').value);
    			}
    		},
    		'erpModifyCommonButton':{
				afterrender: function(btn){
					var status = Ext.getCmp('va_statuscode');
					if(status && status.value == 'AUDITED'){
						btn.setText('更新备注');
						btn.show();//触发字段可编辑
					}
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
	   			//me.setLoading(false);
	   			var localJson = new Ext.decode(response.responseText);
    			if(localJson.success){
    				//audit成功后刷新页面进入可编辑的页面 
    				//auditSuccess(function(){
	   					window.location.reload();
	   				//});    				
	   			} else {
    				if(localJson.exceptionInfo){
    	   				var str = localJson.exceptionInfo;
    	   				if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
    	   					str = str.replace('AFTERSUCCESS', '');
    	   					showMessage("提示", '确认成功');
    	   					//auditSuccess(function(){
    	   						window.location.reload();
    	   					//});
    	   				} else {
    	   					showError(str);return;
    	   				}
    	   			}
    			}
	   		}
		});
	}
});