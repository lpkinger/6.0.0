Ext.QuickTips.init();
Ext.define('erp.controller.plm.cost.ProjectCost', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'plm.cost.ProjectCost','core.form.Panel','plm.cost.ProjectCost',
    		'core.button.Add','core.button.Submit','core.button.Audit','core.button.Save','core.button.Close','core.button.Print',
    			'core.button.Update','core.button.Delete','core.button.ResAudit','core.button.ResSubmit',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger'
    	],
    init:function(){
      var me = this;
    	me.attachcount = 0;
    	this.control({ 
    		'erpSaveButton': {
    			click: function(btn){
    				this.save(btn);
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				this.FormUtil.beforeClose(this);
    			}
    		},
    		'erpUpdateButton': {
    		    afterrender: function(btn){
    				var status = Ext.getCmp('prj_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpDeleteButton': {
    		  afterrender: function(btn){
    				var status = Ext.getCmp('pc_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				this.FormUtil.onDelete(Ext.getCmp('pc_id').value);
    			}
    		},
    		  'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addProjectCost', '新增成本记录', 'jsps/plm/cost/ProjectCost.jsp');
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('pc_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onSubmit(Ext.getCmp('pc_id').value);
    			}
    		},
    	   'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('pc_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('pc_id').value);
    			}
    		},		
    	});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	save: function(btn){
		var me = this;
		var form = me.getForm(btn);
		if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber();
    				}
		me.FormUtil.beforeSave(me);
	}
});