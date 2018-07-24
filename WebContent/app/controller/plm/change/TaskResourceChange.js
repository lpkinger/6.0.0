Ext.QuickTips.init();
Ext.define('erp.controller.plm.change.TaskResourceChange', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'core.form.Panel','plm.change.TaskResourceChange','core.toolbar.Toolbar','core.form.MultiField',
    		'core.button.Add','core.button.Submit','core.button.Audit','core.button.Save','core.button.Close',
    			'core.button.Update','core.button.Delete','core.button.ResAudit','core.button.ResSubmit','core.button.DeleteDetail',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.form.YnField', 'core.trigger.MultiDbfindTrigger'
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
    		'dbfindtrigger[name=ptc_oldtaskid]':{
    		  afterrender:function(trigger){
    		   trigger.dbKey='ptc_prjid';
    		   trigger.mappingKey='prjplanid';
    		   trigger.dbMessage='请选择该变更任务的项目ID';
    		  }    		
    		},
    		'dbfindtrigger[name=ptc_proposer]':{
    		   afterrender:function(trigger){
    		   trigger.dbKey='ptc_prjid';
    		   trigger.mappingKey='tm_prjid';
    		   trigger.dbMessage='请选择该变更任务的项目ID';
    		  }    
    		},
    		 'dbfindtrigger[name=rc_oldresourcecode]': {
    			afterrender:function(trigger){
    			trigger.gridKey='ptc_oldtaskid';
    			trigger.mappinggirdKey='ra_taskid';
    			trigger.gridErrorMessage='请选择需变更的任务ID';
    			}
    		},
    		 'dbfindtrigger[name=rc_resourcecode]': {
    			afterrender:function(trigger){
    			trigger.gridKey='ptc_prjid';
    			trigger.mappinggirdKey='tm_prjid';
    			trigger.gridErrorMessage='请选择该变更任务的项目计划ID';
    			}
    		},
    		'erpDeleteButton': {
    		  afterrender: function(btn){
    				var status = Ext.getCmp('ptc_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				this.FormUtil.onDelete(Ext.getCmp('ptc_id').value);
    			}
    		},
    		  'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addProjectTaskChange', '新增任务资源变更', 'jsps/plm/change/TaskResourceChange.jsp');
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ptc_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onSubmit(Ext.getCmp('ptc_id').value);
    			}
    		},
    		'erpResSubmitButton':{
    		    afterrender:function(btn){
    		    	var status=Ext.getCmp('ptc_statuscode');
    		    	if(status && status.value!='COMMITED'){
    		    		btn.hide();
    		    	}    		    	
    		    },
    		    click:function (btn){
    		    	me.FormUtil.onResSubmit(Ext.getCmp('ptc_id').value);
    		    }
    		    
    		},
    	    'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ptc_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('ptc_id').value);
    			}
    		},
    		'erpResAuditButton':{
    			afterrender:function (btn){
    				var status=Ext.getCmp('ptc_statuscode');
    				if(status && status.value!='AUDITED'){
    					btn.hide();
    				}
    			},
    			click:function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('ptc_id').value);	
    			}
    		}
    	});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	save: function(btn){
		var me = this;
		var form = me.getForm(btn);
		if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){me.BaseUtil.getRandomNumber();}
	    me.FormUtil.beforeSave(me);
	}
});