Ext.QuickTips.init();
Ext.define('erp.controller.crm.marketmgr.marketresearch.MarketResearch', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    views:[
    		'crm.marketmgr.marketresearch.MarketResearch','core.form.Panel','core.form.FileField','core.form.MultiField','core.form.CheckBoxGroup','core.trigger.MultiDbfindTrigger',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit',
  			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.ResSubmit','core.grid.Panel2',
  			'core.button.TurnCustomer','core.button.Flow','core.button.DownLoad','core.button.Scan','common.datalist.Toolbar','core.button.Confirm',
  			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.trigger.AutoCodeTrigger','core.trigger.AddDbfindTrigger'
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
					if(!me.validate()){
						showError('结束时间不能小于开始时间!');
						return;
					}
					//保存之前的一些前台的逻辑判定
					this.FormUtil.beforeSave(this);
				}
			},
			'erpDeleteButton' : {
				click: function(btn){
					me.FormUtil.onDelete(Ext.getCmp('mr_id').value);
				}
			},
			'erpUpdateButton': {
				click: function(btn){
					if(!me.validate()){
						showError('结束时间不能小于开始时间!');
						return;
					}
					this.FormUtil.onUpdate(this);
				}
			},
			'erpAddButton': {
				click: function(){
					me.FormUtil.onAdd('addMarketResearch', '新增市场调研审批 ', 'jsps/crm/marketmgr/marketresearch/marketResearch.jsp');
				}
			},'erpSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('mr_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onSubmit(Ext.getCmp('mr_id').value);
				}
			},'erpResSubmitButton': {
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
			'field[name=mr_startime]': {
	 			   blur:function(f){
	 				   var endtime=Ext.getCmp('mr_endtime').value;
	 				   if(endtime&&endtime!=''&&f.value&&f.value!=''){
	 					   Ext.getCmp('mr_days').setValue((endtime-f.value)/(24*3600*1000)+1);
	 				   }
	 			   }
	   		},
	   		'field[name=mr_endtime]': {
	   			blur:function(f){
	 				   var startime=Ext.getCmp('mr_startime').value;
	 				   if(startime&&startime!=''&&f.value&&f.value!=''){
	 					   Ext.getCmp('mr_days').setValue((f.value-startime)/(24*3600*1000)+1);
	 				   }
	 			   }
	   		},
			'field[name=mr_tpname]': {
	 			   afterrender:function(f){
	 				   f.setFieldStyle({
		   					 'color': 'blue'
		   				  });
		   				  f.focusCls = 'mail-attach';
		   				   var c = Ext.Function.bind(me.openRelative, me);
		   				   Ext.EventManager.on(f.inputEl, {
		   					   mousedown : c,
		   					   scope: f,
		   					   buffer : 100
		   				   });
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
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			}
    	});
	},
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	validate:function(){
		if(Ext.getCmp('mr_startime').value>Ext.getCmp('mr_endtime').value){
			return false;
		}
		return true;
	},
	openRelative:function(e, el, obj){
		if(Ext.getCmp('mr_tpname').value=='')return;
		var tpcode=Ext.getCmp('mr_tpcode').value;
		var url='jsps/crm/marketmgr/marketresearch/preView.jsp?_noc=1&formCondition=rt_codeIS'+tpcode;
		this.FormUtil.onAdd('ReportTemplates!PreView', '模板预览', 
				url);
		
	}
});