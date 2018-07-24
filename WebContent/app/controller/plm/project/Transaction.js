Ext.QuickTips.init();
Ext.define('erp.controller.plm.project.Transaction', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'plm.project.Transaction','core.form.Panel','core.button.Scan',
    		'core.button.Add','core.button.Submit','core.button.Audit','core.button.Save','core.button.Close','core.button.Print',
    			'core.button.Upload','core.button.Update','core.button.Delete','core.button.ResAudit','core.button.ResSubmit',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger'
    	],
    init:function(){
    	this.control({ 
    		'erpSaveButton': {
    			click: function(btn){
    				this.save(btn);
    			}
    		},
    		'erpCloseButton': {
    		afterrender:function(btn){
    		  Ext.getCmp('tt_remark').setHeight(350);
    		},
    			click: function(btn){
    				this.FormUtil.beforeClose(this);
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpDeleteButton': {
    			click: function(btn){
    				this.FormUtil.onDelete({pre_id: Number(Ext.getCmp('tt_id').value)});
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('tt_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onSubmit(Ext.getCmp('tt_id').value);
    			}
    		},
    	   'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('tt_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('tt_id').value);
    			}
    		},
    		'datefield[name=tt_startdate]':{
    		   change:function(field){
    		       if(field.value){
    		        Ext.getCmp('tt_enddate').setMinValue(field.value);
    		       }
    		   }    		
    		},
    		'dbfindtrigger[name=tt_employeecode]':{
    		  afterrender:function(trigger){  	        
                trigger.dbKey='tt_prjid';
    			trigger.mappingKey='tm_prjid';
    			trigger.dbMessage='请选择该成员所属项目ID';    	        
    	     },    		
    		}
    	});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	save: function(btn){
		var me = this;
		if(Ext.getCmp('tt_code').value == null || Ext.getCmp('tt_code').value == ''){
			me.BaseUtil.getRandomNumber();
		}
		me.FormUtil.beforeSave(me);
	}
});