Ext.QuickTips.init();
Ext.define('erp.controller.plm.cost.ProjectFinishReport', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'core.form.Panel','plm.cost.ProjectFinishReport','core.grid.Panel2','core.toolbar.Toolbar','core.button.Scan',
    		'core.button.Add','core.button.Submit','core.button.Audit','core.button.Save','core.button.Close','core.button.ResSubmit',
    			'core.button.Update','core.button.Delete','core.button.ResAudit','core.button.DeleteDetail','core.button.TurnCapitalization',
    			'core.button.VoCreate','core.button.ConfirmIncome',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.form.ColorField','core.form.YnField'
    	],
    init:function(){
       var me=this;
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
    		'erpCloseButton': {
    			click: function(btn){
    				this.FormUtil.beforeClose(this);
    			}
    		},
    		'erpUpdateButton': {
    		    afterrender: function(btn){
    				var status = Ext.getCmp('pfr_statuscode');
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
    				var status = Ext.getCmp('pfr_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				this.FormUtil.onDelete(Ext.getCmp('pfr_id').value);
    			}
    		},
    		  'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addProjectFinishReport', '新增项目结案报告', 'jsps/plm/cost/projectFinishReport.jsp');
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('pfr_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onSubmit(Ext.getCmp('pfr_id').value);
    			}
    		},
    		'erpResSubmitButton':{
    		afterrender: function(btn){
    				var status = Ext.getCmp('pfr_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('pfr_id').value);
    			}
    		
    		},
    	   'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('pfr_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('pfr_id').value);
    			}
    		},   
    	   'erpResAuditButton':{
    	      afterrender: function(btn){
    				var status = Ext.getCmp('pfr_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('pfr_id').value);
    			}
    	   }	 		    
    	});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});