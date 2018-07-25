Ext.QuickTips.init();
Ext.define('erp.controller.plm.project.ConfirmProject', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'plm.project.ConfirmProject','core.form.Panel',
    		'core.button.Add','core.button.Submit','core.button.Audit','core.button.Save','core.button.Close','core.button.Confirm',
    			'core.button.Upload','core.button.Update','core.button.Delete','core.button.ResAudit',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger'
    	],
    init:function(){
    var me=this;
    	this.control({ 
    		'erpCloseButton': {
    		  
    			click: function(btn){
    				this.FormUtil.beforeClose(this);
    			}
    		},
    		'erpConfirmButton': {
    		  afterrender: function(btn){
    		  btn.hide();
    		  },
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('cp_id').value);
    			}
             
    		},
    		'erpDeleteButton': {
    		  afterrender: function(btn){
    		     Ext.getCmp('cp_description').setHeight(350);
    		      btn.hide();
    		  },
    			click: function(btn){
    				this.FormUtil.onDelete(Ext.getCmp('cp_id').value);
    			}
    		},
    		'erpUpdateButton': { 
    		   afterrender: function(btn){
    		      btn.hide();
    		  },  		 
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'dbfindtrigger[name=cp_id]':{
    		  afterrender:function(dbfindtrigger){  	        
               dbfindtrigger.dbBaseCondition=" PRJPLAN_STATUSCODE IS 'DOING'";    	        
    	     },	
    	    change: function(dbfindtrigger){
    	     if(dbfindtrigger&&dbfindtrigger.value!=''){
    	       Ext.getCmp('deletebutton').show();
    	       Ext.getCmp('updatebutton').show();
    	       Ext.getCmp('confirmbutton').show();
    	     }    	    
    	    }
    		} 		
    	});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
});