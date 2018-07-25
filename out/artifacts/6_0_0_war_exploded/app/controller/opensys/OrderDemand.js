Ext.QuickTips.init();
Ext.define('erp.controller.opensys.OrderDemand', {
	extend: 'Ext.app.Controller',
	requires: ['erp.util.BaseUtil', 'erp.util.FormUtil', 'erp.util.RenderUtil'],
	views:['opensys.demand.OrderDemand','crm.aftersalemgr.OrderDemand','core.form.Panel2','core.form.Panel','core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField',
		   'core.trigger.AddDbfindTrigger','core.form.FileField','core.form.CheckBoxGroup','core.form.SpecialContainField',
	       'core.button.Save','core.button.Close','core.button.Add','core.button.Update','core.button.Delete','core.button.Submit','core.button.ResSubmit',
	       'core.button.Audit','core.button.ResAudit'],
	init:function(){
		this.BaseUtil = Ext.create('erp.util.BaseUtil');
		this.FormUtil = Ext.create('erp.util.FormUtil');
		this.GridUtil = Ext.create('erp.util.GridUtil');
		var me=this;
		this.control({		
			'erpcheckboxgroup':{
				afterrender:function(f){
					if(caller=="OrderDemand"){
						var columnWidth=1/Math.min(f.items.length, 5);
						Ext.each(f.items.items,function(i){
						i.columnWidth=columnWidth;
						});
					}									
				}
			},
			'erpSaveButton': {
    			click: function(btn){
    				var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber();// 自动添加编号
    				}
    				this.FormUtil.beforeSave(this);
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				this.FormUtil.beforeClose(this);
    			}
    		},
    		'erpAddButton': {
    			click: function(btn){
    				if(caller=="OrderDemand"){
    					me.FormUtil.onAdd('addOrderDemand', '新增订单软件需求', 'jsps/opensys/demand/orderDemand.jsp');
    				}else if(caller=="SysOrderDemand"){
    					me.FormUtil.onAdd('addSysOrderDemand', '新增订单软件需求', 'jsps/crm/aftersalemgr/orderDemand.jsp');
    				}
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpDeleteButton': {
    			click: function(btn){
    				me.FormUtil.onDelete((Ext.getCmp('cd_id').value));
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
					var statu = Ext.getCmp('cd_statuscode');
					if(statu && statu.value != 'COMMITED'){
						btn.hide();
					}
				},
    			click: function(btn){
    				this.FormUtil.onAudit(Ext.getCmp('cd_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
					var statu = Ext.getCmp('cd_statuscode');
					if(statu && statu.value != 'AUDITED'){
						btn.hide();
					}
				},
    			click: function(btn){
    				this.FormUtil.onResAudit(Ext.getCmp('cd_id').value);
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
					var status = Ext.getCmp('cd_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
			},
    			click: function(btn){
    				this.FormUtil.onSubmit(Ext.getCmp('cd_id').value);
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
					var statu = Ext.getCmp('cd_statuscode');
					if(statu && statu.value != 'COMMITED'){
						btn.hide();
					}
				},
    			click: function(btn){
    				this.FormUtil.onResSubmit(Ext.getCmp('cd_id').value);
    			}
    		}
		});
	},
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});