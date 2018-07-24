Ext.QuickTips.init();
Ext.define('erp.controller.scm.sale.PreForecastClash', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','scm.sale.PreForecastClash','core.grid.Panel2','core.toolbar.Toolbar','core.form.MultiField','core.form.MonthDateField',
      		'core.button.Save','core.button.Update','core.button.Add','core.button.Submit','core.button.ResSubmit','core.button.Upload',
      		'core.button.ResAudit','core.button.Audit','core.button.Close','core.button.Delete','core.button.DeleteDetail',
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
					me.FormUtil.onDelete(Ext.getCmp('pfc_id').value);
				}
			},
			'erpUpdateButton': {
				click: function(btn){
					var grid = Ext.getCmp('grid'), items = grid.store.data.items;
					for(var i=0;i<items.length;i++){
						var pfd_sdid = items[i].data['pfd_sdid'];
						var pfd_id = items[i].data['pfd_id'];
						if(!pfd_sdid && pfd_id>0){
							showError("来源ID pfd_sdid不存在,不允许更新");
							return;
						}
					}
					this.FormUtil.onUpdate(this);
				}
			},
			'erpAddButton': {
				click: function(){
					me.FormUtil.onAdd('addPreForecastClash', '新增业务员预测调整单', 'jsps/scm/sale/preForecastClash.jsp');
				}
			},
			'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			},
			'erpSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('pfc_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onSubmit(Ext.getCmp('pfc_id').value);
				}
			},
			'erpResSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('pfc_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResSubmit(Ext.getCmp('pfc_id').value);
				}
			},
			'erpAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('pfc_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onAudit(Ext.getCmp('pfc_id').value);
				}
			},
			'erpResAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('pfc_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResAudit(Ext.getCmp('pfc_id').value);
				}
			},
			'field[name=pfc_month2]': {
				afterrender: function(field) {
					if(Ext.isEmpty(field.value)){
						field.setValue(me.getMonth(new Date(), 1));
					}
					
    			}
    		},
    		'field[name=pfc_month3]': {
				afterrender: function(field) {
					if(Ext.isEmpty(field.value)){
						field.setValue(me.getMonth(new Date(), 2));
					}
    			}
    		}
		});
	}, 
	onGridItemClick: function(selModel, record){//grid行选择
		this.GridUtil.onGridItemClick(selModel, record);
	},
	getMonth: function(curr, i){
		return Number(Ext.Date.format(new Date(curr.getFullYear(),curr.getMonth()+i), 'Ym'));
	},
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});