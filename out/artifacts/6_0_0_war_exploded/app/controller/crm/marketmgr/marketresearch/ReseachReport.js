Ext.QuickTips.init();
Ext.define('erp.controller.crm.marketmgr.marketresearch.ReseachReport', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil:Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'crm.marketmgr.marketresearch.ReseachReport','core.form.Panel','core.form.FileField','core.form.MultiField','core.grid.Panel2','core.toolbar.Toolbar',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit',
  			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.ResSubmit','core.button.TurnCLFBX',
  			'core.button.TurnCustomer','core.button.Flow','core.button.DownLoad','core.button.Scan','common.datalist.Toolbar',
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
					Ext.getCmp('mr_reportcode').setValue(caller);
					Ext.getCmp('mr_taskcode').setValue(Ext.getCmp('taskcode').getValue());
					Ext.getCmp('mr_prjid').setValue(Ext.getCmp('prjplanid').getValue());
					Ext.getCmp('mr_prjname').setValue(Ext.getCmp('ep_prname').getValue());
					var form = Ext.getCmp('form');
					if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
						me.BaseUtil.getRandomNumber();//自动添加编号
					}
					me.FormUtil.beforeSave(me);	
				}
			},
			'erpDeleteButton' : {
				afterrender: function(btn){
					var status = Ext.getCmp('mr_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onDelete(Ext.getCmp('mr_id').value);
				}
			},
			'erpUpdateButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('mr_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					this.FormUtil.onUpdate(this);
				}
			},
			'erpSubmitButton': {
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
					var isTurn = Ext.getCmp('mr_isturnfeeplease');
					if((status && status.value != 'AUDITED')||(isTurn&&isTurn.value!='否')){
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
						Ext.getCmp('mr_prjname').setValue(Ext.getCmp('ep_prname').getValue());
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
					var win=parent.Ext.getCmp('singlewin');	
					if(win)
					win.close();
					else
					me.FormUtil.beforeClose(me);							
				}
			},
			'erpTurnCLFBXButton':{
				afterrender:function(btn){
					var status=Ext.getCmp('mr_statuscode');
					var isTurn=Ext.getCmp('mr_isturnfeeplease');
					if((status && status.value != 'AUDITED')||(isTurn&&isTurn.value!='否')){
						btn.hide();
					}
				},
				click:function(btn){
					me.turnCLFBX();
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
	turnCLFBX:function(){
		var mr_id=Ext.getCmp('mr_id').value;
		Ext.Ajax.request({
			url : basePath + 'crm/marketmgr/turnFeepleaseCLFBX.action?caller='+caller,
			params: {id:mr_id},
			method : 'post',
			callback : function(options, success, response){
				if (!response) return;
				var res = new Ext.decode(response.responseText);
				console.log(res);
				if(res.exceptionInfo != null){
					showError(res.exceptionInfo);return;
				}
				showMessage('转入成功',res.log);
				window.location.reload();
			}
		});
	}
});