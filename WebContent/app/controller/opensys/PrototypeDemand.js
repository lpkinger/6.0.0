Ext.QuickTips.init();
Ext.define('erp.controller.opensys.PrototypeDemand', {
	extend: 'Ext.app.Controller',
	requires: ['erp.util.BaseUtil', 'erp.util.FormUtil', 'erp.util.RenderUtil'],
	views:['opensys.demand.PrototypeDemand','core.form.Panel2','crm.aftersalemgr.PrototypeDemand','core.form.Panel','core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField', 'core.grid.YnColumn',
		   'core.trigger.AddDbfindTrigger','core.form.FileField','core.button.Save','core.button.Close','core.form.CheckBoxGroup','core.form.SpecialContainField'
	       ,'core.button.Add','core.button.Update','core.button.Delete','core.button.Submit','core.button.ResSubmit','core.button.Audit','core.button.ResAudit'],
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
    				if(caller=="PrototypeDemand"){
    					me.FormUtil.onAdd('addPrototypeDemand', '新增样机软件需求', 'jsps/opensys/demand/prototypeDemand.jsp');
    				}else if(caller=="SysPrototypeDemand"){
    					me.FormUtil.onAdd('addPrototypeDemand', '新增样机软件需求', 'jsps/crm/aftersalemgr/prototypeDemand.jsp');
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