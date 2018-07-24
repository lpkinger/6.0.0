Ext.QuickTips.init();
Ext.define('erp.controller.pm.mould.DeliveryOrder', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','pm.mould.DeliveryOrder','core.grid.Panel2','core.toolbar.Toolbar','core.form.MultiField',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.ResAudit',
  			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail',
  			'core.button.ResSubmit','core.form.FileField','core.button.Post','core.button.ResPost',
			'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.form.YnField'      
  	],
  	refs: [{
  		selector: '#md_sourcecode',
  		ref: 'source'
  	}],
	init:function(){
		var me = this;
		this.control({
			'erpGridPanel2': {
				afterrender: function(grid){
					grid.plugins[0].on('beforeedit', function(args){
    					if(args.field == "mdd_qty") {
    						if(me.getSource() && !Ext.isEmpty(me.getSource().getValue())){
    							return false;
    						}
    					}
    				});
    			},
    			itemclick: this.onGridItemClick
			},
			'erpSaveButton': {
				click: function(btn){
    				var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber(caller);//自动添加编号
    				}
    				this.FormUtil.beforeSave(this);
    			}
			},
			'erpDeleteButton' : {
				click: function(btn){
					me.FormUtil.onDelete(Ext.getCmp('md_id').value);
				}
			},
			'erpUpdateButton': {
				afterrender: function(btn){
    				var status = Ext.getCmp('md_auditstatuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
			},
			'erpAddButton': {
				click: function(){
					me.FormUtil.onAdd('addDeliveryOrder', '新增模具出货单', 'jsps/pm/mould/deliveryOrder.jsp');
				}
			},
			'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('md_auditstatuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onSubmit(Ext.getCmp('md_id').value);
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('md_auditstatuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('md_id').value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('md_auditstatuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('md_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var auditstatus = Ext.getCmp('md_auditstatuscode'),
    					status = Ext.getCmp('md_statuscode');
    				if(auditstatus && auditstatus.value != 'AUDITED'){
    					btn.hide();
    				}
    				if(status && status.value != 'UNPOST'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('md_id').value);
    			}
    		},
			'erpPrintButton': {
				click: function(btn){
					me.FormUtil.onPrint(Ext.getCmp('md_id').value);
				}
			},
			'erpPostButton': {
    			afterrender: function(btn){
     				var status = Ext.getCmp('md_statuscode'),
    					auditstatus = Ext.getCmp('md_auditstatuscode');
    				if(status && status.value != 'UNPOST'){
    					btn.hide();
    				}
    				if(auditstatus && auditstatus.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			buffer : 1000,
    			click: function(btn){
    				me.FormUtil.onPost(Ext.getCmp('md_id').value);
    			}
    		},
    		'erpResPostButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('md_statuscode');
    				if(status && status.value != 'POSTED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResPost(Ext.getCmp('md_id').value);
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