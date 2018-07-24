Ext.QuickTips.init();
Ext.define('erp.controller.scm.reserve.QUABatch', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','scm.reserve.QUABatch','core.grid.Panel2','core.toolbar.Toolbar','core.form.MultiField',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit',
  			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail','core.button.ResSubmit',
  			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.button.PrintDelivery'
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
    				var grid = Ext.getCmp('grid'), items = grid.store.data.items;
    				/*var bool = true;
    				Ext.each(items, function(item){
						if(!Ext.isEmpty(item.data['qbd_prodcode'])){
							if(!Ext.isEmpty(item.data['qbd_newvalidtime']) && Ext.Date.format(item.data['qbd_newvalidtime'],'Y-m-d') < Ext.Date.format(new Date(),'Y-m-d')){
								bool = false;
								showError('明细表第' + item.data['qbd_detno'] + '行的新有效日期小于当前日期');return;
							}
							if(!Ext.isEmpty(item.data['qbd_validtime']) && Ext.Date.format(item.data['qbd_newvalidtime'],'Y-m-d') < Ext.Date.format(item.data['qbd_validtime'],'Y-m-d')){
								bool = false;
								showError('明细表第' + item.data['qbd_detno'] + '行的新有效日期小于原有效日期');return;
							}
						}
					});
					if(bool)*/
						this.FormUtil.beforeSave(this);
				}
			},
			'erpDeleteButton' : {
				afterrender: function(btn){
					var status = Ext.getCmp('qba_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onDelete(Ext.getCmp('qba_id').value);
				}
			},
			'erpUpdateButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('qba_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					var grid = Ext.getCmp('grid'), items = grid.store.data.items;
    				/*var bool = true;
    				Ext.each(items, function(item){
						if(!Ext.isEmpty(item.data['qbd_prodcode'])){
							if(!Ext.isEmpty(item.data['qbd_newvalidtime']) && Ext.Date.format(item.data['qbd_newvalidtime'],'Y-m-d') < Ext.Date.format(new Date(),'Y-m-d')){
								bool = false;
								showError('明细表第' + item.data['qbd_detno'] + '行的新有效日期小于当前日期');return;
							}
							if(!Ext.isEmpty(item.data['qbd_validtime']) && Ext.Date.format(item.data['qbd_newvalidtime'],'Y-m-d') < Ext.Date.format(item.data['qbd_validtime'],'Y-m-d')){
								bool = false;
								showError('明细表第' + item.data['qbd_detno'] + '行的新有效日期小于原有效日期');return;
							}
						}
					});
					if(bool)*/
						this.FormUtil.onUpdate(this);
				}
			},
			'erpAddButton': {
				click: function(){
					me.FormUtil.onAdd('addQUABatch', '新增库存检验单', 'jsps/scm/reserve/quaBatch.jsp');
				}
			},
			'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			},
			'erpSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('qba_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					var grid = Ext.getCmp('grid'), items = grid.store.data.items;
    				/*var bool = true;
    				Ext.each(items, function(item){
						if(!Ext.isEmpty(item.data['qbd_prodcode'])){
							if(!Ext.isEmpty(item.data['qbd_newvalidtime']) && Ext.Date.format(item.data['qbd_newvalidtime'],'Y-m-d') < Ext.Date.format(new Date(),'Y-m-d')){
								bool = false;
								showError('明细表第' + item.data['qbd_detno'] + '行的新有效日期小于当前日期');return;
							}
							if(!Ext.isEmpty(item.data['qbd_validtime']) && Ext.Date.format(item.data['qbd_newvalidtime'],'Y-m-d') < Ext.Date.format(item.data['qbd_validtime'],'Y-m-d')){
								bool = false;
								showError('明细表第' + item.data['qbd_detno'] + '行的新有效日期小于原有效日期');return;
							}
						}
					});
					if(bool)*/
						me.FormUtil.onSubmit(Ext.getCmp('qba_id').value);
				}
			},
			'erpResSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('qba_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResSubmit(Ext.getCmp('qba_id').value);
				}
			},
			'erpAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('qba_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					var grid = Ext.getCmp('grid'), items = grid.store.data.items;
    				/*var bool = true;
    				Ext.each(items, function(item){
						if(!Ext.isEmpty(item.data['qbd_prodcode'])){
							if(!Ext.isEmpty(item.data['qbd_newvalidtime']) && Ext.Date.format(item.data['qbd_newvalidtime'],'Y-m-d') < Ext.Date.format(new Date(),'Y-m-d')){
								bool = false;
								showError('明细表第' + item.data['qbd_detno'] + '行的新有效日期小于当前日期');return;
							}
							if(!Ext.isEmpty(item.data['qbd_validtime']) && Ext.Date.format(item.data['qbd_newvalidtime'],'Y-m-d') < Ext.Date.format(item.data['qbd_validtime'],'Y-m-d')){
								bool = false;
								showError('明细表第' + item.data['qbd_detno'] + '行的新有效日期小于原有效日期');return;
							}
						}
					});
					if(bool)*/
						me.FormUtil.onAudit(Ext.getCmp('qba_id').value);
				}
			},
			'erpResAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('qba_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResAudit(Ext.getCmp('qba_id').value);
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