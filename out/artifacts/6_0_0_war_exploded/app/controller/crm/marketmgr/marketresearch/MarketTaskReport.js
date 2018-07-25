Ext.QuickTips.init();
Ext.define('erp.controller.crm.marketmgr.marketresearch.MarketTaskReport', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','crm.marketmgr.marketresearch.MarketTaskReport','core.grid.Panel2','core.toolbar.Toolbar',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit',
  			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail',
  			'core.button.ResSubmit','core.form.FileField',
  			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField'
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
					var mr_taskcode=Ext.getCmp('mr_taskcode').value;
					me.FormUtil.onAdd('addMarketTaskReport', '新增任务报告单', 'jsps/crm/marketmgr/marketresearch/marketTaskReport.jsp?whoami='+caller+'&mr_taskcode='+mr_taskcode);
				}
			},
			'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				},
				afterrender:function(btn){
					var code=me.BaseUtil.getUrlParam('mr_taskcode');
					if(code){
						me.setTask(code);
					}
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
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResAudit(Ext.getCmp('mr_id').value);
				}
			},
    		'dbfindtrigger[name=mrd_costname]': {
    			focus: function(t){
    				t.setHideTrigger(false);
    				t.setReadOnly(false);
    				var code = Ext.getCmp('mr_prjid').value;
    				if(code == null || code == ''){
    					showError("请先填写任务编号!");
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
			'textfield[name=sc_newdelivery]': {
				change: function(field){
					if(field.value != null && field.value != ''){
						var grid = Ext.getCmp('grid');
						var date = field.value;
						Ext.Array.each(grid.getStore().data.items,function(item){
							item.set('scd_newdelivery',date);
						});
					}
				}
    		}
		});
	}, 
	setTask:function(code){
		code=code.replace(/\'/g,'');
		var condition='mr_taskcode like \'%'+code+'%\'';
		Ext.Ajax.request({
			url:basePath + 'common/autoDbfind.action',
			params: {
	   			which: 'form',
	   			caller: 'MarketTaskReport',
	   			field: 'mr_taskcode',
	   			condition: 'mr_taskcode like \'%'+code+'%\''
	   		},
	   		async: false,
	   		method : 'post',
	   		callback : function(options,success,response){
	   			var res = new Ext.decode(response.responseText);
	   			if(res.exceptionInfo){
	   				showError(res.exceptionInfo);return;
	   			}
	   			if(res.data){
	   				var datas = Ext.decode(res.data.replace(/,}/g, '}').replace(/,]/g, ']'));
	   				var data=datas[0];
	   				Ext.Array.each(res.dbfinds,function(dbfind){
	   					var f = Ext.getCmp(dbfind.field);
	   					if(f){
	   						f.setValue(data[dbfind.dbGridField]);
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