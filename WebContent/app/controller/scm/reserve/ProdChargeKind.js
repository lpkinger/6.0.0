Ext.QuickTips.init();
Ext.define('erp.controller.scm.reserve.ProdChargeKind', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','scm.reserve.ProdChargeKind',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.ResAudit',
      		'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update',
      		'core.button.ResSubmit','core.button.Banned','core.button.ResBanned',
			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField'
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
    				this.FormUtil.beforeSave(this);
    			}
    		},
    		'erpDeleteButton' : {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('pck_id').value);
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addProdChargeKind', '新增出入库单费用类型', 'jsps/scm/reserve/prodChargeKind.jsp');
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				me.FormUtil.beforeClose(me);
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('pck_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onSubmit(Ext.getCmp('pck_id').value);
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('pck_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('pck_id').value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('pck_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('pck_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('pck_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('pck_id').value);
    			}
    		},
    		'erpBannedButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('pck_statuscode');
					if(status && status.value == 'DISABLE'){
						btn.hide();
					}
				},
    			click: function(btn){
    				this.FormUtil.onBanned(Ext.getCmp('pck_id').value);
    			}
    		},
    		'erpResBannedButton': {
    			afterrender: function(btn){
					var status = Ext.getCmp('pck_statuscode');
					if(status && status.value != 'DISABLE'){
						btn.hide();
					}
				},
    			click: function(btn){
    				this.FormUtil.onResBanned(Ext.getCmp('pck_id').value);
    			}
    		}
    	});
    }, 
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});