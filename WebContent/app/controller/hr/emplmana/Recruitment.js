Ext.QuickTips.init();
Ext.define('erp.controller.hr.emplmana.Recruitment', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    views:[
   		'hr.emplmana.Recruitment','core.form.Panel','core.grid.Panel2','core.toolbar.Toolbar',
   		'core.button.Add','core.button.Save','core.button.Close','core.button.DeleteDetail',
   		'core.button.Update','core.button.Delete','core.form.YnField',
   		'core.button.ResAudit','core.button.Audit','core.button.Submit','core.button.ResSubmit',
   		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.button.TurnRecruitplan'
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
    				me.FormUtil.onDelete(Ext.getCmp('re_id').value);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addRecruitment', '新增用人申请', 'jsps/hr/emplmana/recruitment/recruitment.jsp');
    			}
    		},
    		'erpSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('re_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onSubmit(Ext.getCmp('re_id').value);
				}
			},
			'erpResSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('re_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResSubmit(Ext.getCmp('re_id').value);
				}
			},
			'erpAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('re_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onAudit(Ext.getCmp('re_id').value);
				}
			},
			'erpResAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('re_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResAudit(Ext.getCmp('re_id').value);
				}
			},
			'erpTurnRecruitplanButton':{
				afterrender: function(btn){
					var status = Ext.getCmp('re_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
					var isturn = Ext.getCmp('re_isturn');
					if(isturn && isturn.value != '0'){
						btn.hide();
					}
				},
				click: function(btn){
					var form = Ext.getCmp('form');
					var formdata = form.getValues();
					var param = this.GridUtil.getAllGridStore();
					param = "[" + param.toString() + "]";
					warnMsg('确定要转招聘计划单?', function(btn){
						if (btn == 'yes') {
							Ext.Ajax.request({
								url:basePath + "hr/emplmana/turnRecruitplan.action",
								params:{
									formdata: Ext.JSON.encode(formdata),
									param:param,
									caller : caller
								},
								method:'post',
								callback:function(options,success,response){
									var res = new Ext.decode(response.responseText);
									if(res.success){
										Ext.Msg.alert("提示", "转招聘计划单成功！",function(){
											window.location.reload();
											showMessage('提示',res.log);
										});
										
									}else{
										Ext.Msg.alert("提示", "转招聘计划单失败！");
									}
								}
							});
						} else {
							return;
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