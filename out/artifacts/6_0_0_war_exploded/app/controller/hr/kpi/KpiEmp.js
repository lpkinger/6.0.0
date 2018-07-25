Ext.QuickTips.init();
Ext.define('erp.controller.hr.kpi.KpiEmp', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    views:[
    		'hr.kpi.KpiEmp','core.form.Panel','core.form.FileField','core.form.MultiField','core.form.CheckBoxGroup','core.trigger.MultiDbfindTrigger',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit','hr.kpi.EmpGrid',
  			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.ResSubmit','core.grid.Panel2','core.grid.YnColumn',
  			'core.button.TurnCustomer','core.button.Flow','core.button.DownLoad','core.button.Scan','common.datalist.Toolbar','core.button.Confirm',
  			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.trigger.AutoCodeTrigger','core.trigger.AddDbfindTrigger'
  	],
	init:function(){
		var me = this;
		this.control({
			'hidden[name=ke_emid]':{
				change:function(field){
					var grid = Ext.getCmp('empGrid');
					var gridParam = {caller: caller, condition: "ke_emid="+field.value};
					grid.loadNewStore(grid,'hr/kpi/singleGridPanel2.action',gridParam,"");
				}
			},
			'erpSaveButton': {
				click: function(btn){
					me.save();
				}
			},
			'erpDeleteButton' : {
				click: function(btn){
					me.FormUtil.onDelete(Ext.getCmp('kr_id').value);
				}
			},
			'erpUpdateButton': {
				click: function(btn){
					this.FormUtil.onUpdate(this);
				}
			},
			'erpAddButton': {
				click: function(){
					me.FormUtil.onAdd('addKpiRule', '新增考核规则定义', 'jsps/hr/kpi/kpiRule.jsp');
				}
			},'erpSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('kr_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onSubmit(Ext.getCmp('kr_id').value);
				}
			},'erpResSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('kr_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResSubmit(Ext.getCmp('kr_id').value);
				}
			},
			'erpAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('kr_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onAudit(Ext.getCmp('kr_id').value);
				}
			},
			'erpResAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('kr_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResAudit(Ext.getCmp('kr_id').value);
				}
			},
			'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			}
    	});
	},
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	save:function(){
		var form=Ext.getCmp('form');
		var r = form.getValues();
		var grid = Ext.getCmp('empGrid');
		var s = grid.getStore().data.items;
		var jsonGridData=new Array();
		for(var i=0;i<s.length;i++){
			var data=s[i].data;
			if(data.isUsed){
				jsonGridData.push(Ext.JSON.encode(data));
			}
		}
		var params=new Object();
		params.formStore = unescape(Ext.JSON.encode(r));
		params.param = unescape(jsonGridData.toString());
		Ext.Ajax.request({
	   		url : basePath + form.saveUrl,
	   		params : params,
	   		method : 'post',
	   		callback : function(options,success,response){
	   				var localJson = new Ext.decode(response.responseText);
	   				if(localJson.exceptionInfo){
	   					var str = localJson.exceptionInfo;
	   					showError(str);
	   				}else if(localJson.success){
	    				saveSuccess(function(){
	    					var gridParam = {caller: caller, condition: "ke_emid="+Ext.getCmp('ke_emid').value};
	    					grid.loadNewStore(grid,'hr/kpi/singleGridPanel2.action',gridParam,"");
	    				});
		   			} 
	   			}
	   		});
	}
});