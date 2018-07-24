Ext.QuickTips.init();
Ext.define('erp.controller.hr.emplmana.Turnposition', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    views:[
   		'hr.emplmana.Turnposition','core.form.Panel','core.grid.Panel2','core.toolbar.Toolbar',
   		'core.button.Add','core.button.Save','core.button.Close','core.button.DeleteDetail',
   		'core.button.Update','core.button.Delete','core.form.YnField','core.form.FileField',
   		'core.button.ResAudit','core.button.Audit','core.button.Submit','core.button.ResSubmit',
   		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.button.TurnPosition','core.button.TurnEmpTransferCheck'
   	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'dbfindtrigger[name=tp_emcode]':{
				'aftertrigger':function(){
					Ext.getCmp('tp_oldposition').setValue(Ext.getCmp('tp_position').value);
				}
			},
    		'erpGridPanel2': { 
    			itemclick: this.onGridItemClick
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber();//自动添加编号
    				}
    				var type=Ext.getCmp("tp_type").value;
    				if(type=='turnposition'){
    					var new_po=Ext.getCmp('tp_newpositionid').value;
    					if(new_po==""||new_po==null){
    						showError('新岗位未选择');
    					}else{
    						this.FormUtil.beforeSave(this);
    					}
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
    		'erpUpdateButton': {
    			click: function(btn){
    				var type=Ext.getCmp("tp_type").value;
    				if(type=='turnposition'){
    					var new_po=Ext.getCmp('tp_newpositionid').value;
    					if(new_po==""||new_po==null){
    						showError('新岗位未选择');
    					}else{
    						this.FormUtil.onUpdate(this);
    					}
    				}else{
    					this.FormUtil.onUpdate(this);
    				}
    			}
    		},
    		'erpDeleteButton': {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('tp_id').value);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addTurnposition', '新增职位调用单', 'jsps/hr/emplmana/employee/turnposition.jsp');
    			}
    		},'erpSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('tp_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onSubmit(Ext.getCmp('tp_id').value);
				}
			},
			'erpResSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('tp_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResSubmit(Ext.getCmp('tp_id').value);
				}
			},
			'erpAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('tp_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onAudit(Ext.getCmp('tp_id').value);
				}
			},
			'erpResAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('tp_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResAudit(Ext.getCmp('tp_id').value);
				}
			},
			'erpTurnPositionButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('tp_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				},
				click: function(btn){
					var param = this.GridUtil.getAllGridStore();
					param = "[" + param.toString() + "]";
					warnMsg('确定要更新用户资料吗?', function(btn){
						if (btn == 'yes') {
							Ext.Ajax.request({
								url:basePath + "hr/emplmana/updatePosition.action",
								params:{
									param:param
								},
								method:'post',
								callback:function(options,success,response){
									var res = new Ext.decode(response.responseText);
									if(res.success){
										Ext.Msg.alert("提示", "更新员工资料成功！");
									}else{
										Ext.Msg.alert("提示", "更新员工资料失败！");
									}
								}
							});
						} else {
							return;
						}
					});
				}
			},
			'erpTurnEmpTransferCheckButton':{
				afterrender: function(btn){
					/*var status = Ext.getCmp('tp_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}*/
				},
				click:function(){
					var id=Ext.getCmp('tp_id').value;
					Ext.Ajax.request({
								url:basePath + "hr/emplmana/turnEmpTransferCheck.action",
								params:{
									caller:caller,
									id:id									
								},
								method:'post',
								callback:function(options,success,response){
									var res = new Ext.decode(response.responseText);
									if(res.success){
										Ext.Msg.alert("提示", "转任务交接单成功！");
									}
									if(res.exceptionInfo){
										showError(res.exceptionInfo);
									}
								}
							});
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