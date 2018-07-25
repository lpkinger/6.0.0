Ext.QuickTips.init();
Ext.define('erp.controller.common.RulesApply', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'common.JProcess.RulesApply',   		
   			'core.form.Panel','core.button.ResAudit',   			 
   			'core.button.Add','core.button.Submit','core.button.Audit','core.button.ResSubmit',
    		'core.button.Save','core.button.Close','core.button.Print','core.button.Update','core.button.Delete',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.form.MultiField','core.form.FileField',
    		'core.button.CheckRuleSql','core.button.Modify'
    	],
    init:function(){
    	var me = this;
    	this.control({
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
    				me.FormUtil.onAdd('JprocessRulesApply', '新增流程规则申请', 'jsps/common/jprocessDeal/jprocessRulesApply.jsp');
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
	    			this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpDeleteButton': {
    			click: function(btn){
    				me.FormUtil.onDelete((Ext.getCmp('ra_id').value));
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
					var statu = Ext.getCmp('ra_statuscode');
					if(statu && statu.value != 'COMMITED'){
						btn.hide();
					}
				},
    			click: function(btn){
    				this.FormUtil.onAudit(Ext.getCmp('ra_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
					var statu = Ext.getCmp('ra_statuscode');
					if(statu && statu.value != 'AUDITED'){
						btn.hide();
					}
				},
    			click: function(btn){
    				this.FormUtil.onResAudit(Ext.getCmp('ra_id').value);
    			}
    		},
    		'erpSubmitButton': {afterrender: function(btn){
				var statu = Ext.getCmp('ra_statuscode');
				if(statu && statu.value != 'ENTERING'){
					btn.hide();
				}
			},
    			click: function(btn){
    				var me=this;
    				var form = me.getForm(btn);
    				var id=Ext.getCmp('ra_id').value;
    				me.FormUtil.onSubmit(Ext.getCmp('ra_id').value);
    				
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
					var statu = Ext.getCmp('ra_statuscode');
					if(statu && statu.value != 'COMMITED'){
						btn.hide();
					}
				},
    			click: function(btn){
    				this.FormUtil.onResSubmit(Ext.getCmp('ra_id').value);
    			}
    		}
    	});
    },
   getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});