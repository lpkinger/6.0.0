Ext.QuickTips.init();
Ext.define('erp.controller.crm.marketmgr.marketresearch.TaskReport', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil:Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'crm.marketmgr.marketresearch.TaskReport','core.form.Panel','core.form.FileField','core.form.MultiField','core.grid.Panel2','core.toolbar.Toolbar',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit',
  			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.ResSubmit',
  			'core.button.TurnCustomer','core.button.Flow','core.button.DownLoad','core.button.Scan','common.datalist.Toolbar',
  			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.trigger.AutoCodeTrigger'
  	],
	init:function(){
		var me = this;
		this.control({
			'erpGridPanel2': { 
    			itemclick: this.onGridItemClick
    		},
    		'dbfindtrigger[name=mrd_costname]': {
    			focus: function(t){
    				t.setHideTrigger(false);
    				t.setReadOnly(false);
    				var code = Ext.getCmp('mr_prjid').value;
    				if(code == null || code == ''){
    					showError("项目不存在，请核对后重试！");
    					t.setHideTrigger(true);
    					t.setReadOnly(true);
    				} else {
    					t.dbBaseCondition = "prjplan_id=" + code;
    				}
    			},
    			aftertrigger:function(t){
    				var record = Ext.getCmp('grid').selModel.lastSelected;
    				record.data['mrd_status']='未转费用报销';
    			}
    		},
			'erpSaveButton': {
				click: function(btn){
					Ext.getCmp('mr_reportcode').setValue(caller);
					Ext.getCmp('mr_taskcode').setValue(Ext.getCmp('taskcode').getValue());
					Ext.getCmp('mr_prjid').setValue(Ext.getCmp('prjplanid').getValue());
					Ext.getCmp('mr_prjname').setValue(Ext.getCmp('prjplanname').getValue());
					var needqty=Ext.getCmp('needqty');
					var finishqty=Ext.getCmp('finishqty');
						if(needqty.value<finishqty.value+1){
							warnMsg('继续提交，报告数将超出要求数,是否继续?', function(btn){
								if(btn == 'yes'){
									var form = Ext.getCmp('form');
									if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
										me.BaseUtil.getRandomNumber();//自动添加编号
									}
									//保存之前的一些前台的逻辑判定
									me.FormUtil.beforeSave(me);
								} else {
									return;
								}
							});
						}else{
							var form = Ext.getCmp('form');
							if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
								me.BaseUtil.getRandomNumber();//自动添加编号
							}
							me.FormUtil.beforeSave(me);
						}
						
				}
			},
			'erpDeleteButton' : {
				click: function(btn){
					me.FormUtil.onDelete(Ext.getCmp('mr_id').value);
				}
			},
			'erpUpdateButton': {
				click: function(btn){
					this.FormUtil.onUpdate(this);
				}
			},
			'erpAddButton': {
				click: function(){
					me.FormUtil.onAdd('addResearch', '新增调研计划', 'jsps/crm/marketmgr/marketresearch/taskReport.jsp?whoami='+caller+'&cond='+cond);
				}
			},'erpSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('mr_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onSubmit(Ext.getCmp('mr_id').value,true);
				}
			},
			'erpResSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('mr_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResSubmit(Ext.getCmp('mr_id').value);
				}
			},
			'erpAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('mr_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onAudit(Ext.getCmp('mr_id').value);
				}
			},
			'erpResAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('mr_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResAudit(Ext.getCmp('mr_id').value);
				}
			},
			'erpCloseButton': {
				afterrender:function(btn){
					if(Ext.getCmp('mr_prjname')&&Ext.getCmp('prjplanname')){
						Ext.getCmp('mr_prjname').setValue(Ext.getCmp('prjplanname').getValue());
					}
					if(Ext.getCmp('mr_prjid')&&Ext.getCmp('prjplanid')){
						Ext.getCmp('mr_prjid').setValue(Ext.getCmp('prjplanid').getValue());
					}
					if(Ext.getCmp('mr_taskcode')&&Ext.getCmp('taskcode')){
						Ext.getCmp('mr_taskcode').setValue(Ext.getCmp('taskcode').getValue());
					}
					if(Ext.getCmp('mr_reportcode')){
						Ext.getCmp('mr_reportcode').setValue(caller);
					}
				},
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
	}
});