Ext.QuickTips.init();
Ext.define('erp.controller.oa.device.DevicePurchase', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    views:[
    		'core.form.Panel','oa.device.DevicePurchase','core.grid.Panel2','core.form.MultiField','core.form.FileField',
    		'core.button.Add','core.button.Submit','core.button.ResSubmit','core.button.Audit','core.button.Save','core.button.Close','core.button.Print','core.button.TurnCustomer',
			'core.button.Upload','core.button.Update','core.button.FeatureDefinition','core.button.FeatureView','core.button.Delete','core.button.ResAudit','core.button.ForBidden',
			'core.button.ResForBidden','core.button.Banned','core.button.ResBanned','core.button.CopyAll','core.button.CreateFeatrue',
			'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.form.YnField'
	],
	init:function(){
		var me = this;
		this.control({ 
			'erpGridPanel2': { 
    			afterrender: function(grid){
    				var status = Ext.getCmp('dp_statuscode');
    				if(status && status.value != 'ENTERING' ){
    					grid.setReadOnly(true);
    				}
    			},
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
    				me.FormUtil.onDelete(Ext.getCmp('dp_id').value);
    			}
    		},
			'erpUpdateButton': { 
				click: function(btn){
					this.FormUtil.onUpdate(this);
				}
			},
			'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addDevicePurchase', '新增设备申购单', 'jsps/oa/device/devicePurchase.jsp?whoami='+ caller);
    			}
    		},
			'erpCloseButton': {
    			click: function(btn){
    				this.FormUtil.beforeClose(this);
    			}
    		},
			'erpSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('dp_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onSubmit(Ext.getCmp('dp_id').value);
				}
			},
			'erpResSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('dp_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResSubmit(Ext.getCmp('dp_id').value);
				}
			},
			'erpAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('dp_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onAudit(Ext.getCmp('dp_id').value);
				}
			},
			'erpResAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('dp_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResAudit(Ext.getCmp('dp_id').value);
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
	resize: function(form, grid){
    	if(!this.resized && form && grid && form.items.items.length > 0){
    		var height = window.innerHeight, 
    			fh = form.getEl().down('.x-panel-body>.x-column-inner').getHeight();
		
			form.setHeight(70 + fh);
			grid.setHeight(height - fh - 70);
			this.resized = true;
		}
    },
	setLoading : function(b) {// 原this.getActiveTab().setLoading()换成此方法,解决Window模式下无loading问题
		var mask = this.mask;
		if (!mask) {
			this.mask = mask = new Ext.LoadMask(Ext.getBody(), {
				msg : "处理中,请稍后...",
				msgCls : 'z-index:10000;'
			});
		}
		if (b)
			mask.show();
		else
			mask.hide();
	}
});