Ext.QuickTips.init();
Ext.define('erp.controller.hr.attendance.BookAirTicket', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    views:[
    		'hr.attendance.BookAirTicket','core.form.Panel','core.form.FileField','core.form.MultiField','core.form.CheckBoxGroup',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit',
  			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.ResSubmit','core.grid.Panel2',
  			'core.button.TurnCustomer','core.button.Flow','core.button.DownLoad','core.button.Scan','common.datalist.Toolbar','core.button.Confirm',
  			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.trigger.AutoCodeTrigger'
  	],
	init:function(){
		var me = this;
		this.control({
			'erpGridPanel2': { 
				itemclick: this.onGridItemClick
			},
			'erpSaveButton': {
				click: function(btn){
					var form = me.getForm(btn);
					if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
						me.BaseUtil.getRandomNumber();//自动添加编号
					}
					//保存之前的一些前台的逻辑判定
					this.FormUtil.beforeSave(this);
				}
			},
			'erpDeleteButton' : {
				click: function(btn){
					me.FormUtil.onDelete(Ext.getCmp('bt_id').value);
				}
			},
			'erpUpdateButton': {
				click: function(btn){
					this.FormUtil.onUpdate(this);
				}
			},
			'erpAddButton': {
				click: function(){
					me.FormUtil.onAdd('addBookAirTicket', '新增机票预订', 'jsps/hr/attendance/bookAirTicket.jsp');
				}
			},'erpSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('bt_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onSubmit(Ext.getCmp('bt_id').value);
				}
			},'erpResSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('bt_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResSubmit(Ext.getCmp('bt_id').value);
				}
			},
			'erpAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('bt_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onAudit(Ext.getCmp('bt_id').value);
				}
			},
			'erpResAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('bt_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResAuditWithManAndTime(Ext.getCmp('bt_id').value,'bt_auditer','bt_auditdate');
				}
			},
			'erpConfirmButton': {afterrender: function(btn){
				var statu = Ext.getCmp('bt_statuscode');
				if(statu && statu.value != 'AUDITED'){
					btn.hide();
				}
			},
    			click: function(btn){    				
    				me.onConfirm(Ext.getCmp('bt_id').value);
    				
    			}
    		} ,
			'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			}
    	});
	},
	onGridItemClick: function(selModel, record){//grid行选择
    	this.GridUtil.onGridItemClick(selModel, record);
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
	   			auditerFieldName:'bt_auditstatus'
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