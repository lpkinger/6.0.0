Ext.QuickTips.init();
Ext.define('erp.controller.as.port.MaterielReturn', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    views:[
    		'as.port.MaterielReturn','core.form.Panel','core.toolbar.Toolbar','core.grid.Panel2',
    		'core.button.Add','core.button.Save','core.button.Close',
    		'core.button.Update','core.button.Delete','core.form.YnField',
    		'core.button.ResAudit','core.button.Audit','core.button.Submit','core.button.ResSubmit',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger'
    	],
    init:function(){
      var me = this;
    	this.control({ 
    		'erpSaveButton': {
  			   click: function(btn){
  				  var form = me.getForm(btn);
  				  if(!Ext.isEmpty(form.codeField) && Ext.getCmp(form.codeField) && ( 
   						Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == '')){
   					me.BaseUtil.getRandomNumber(caller);//自动添加编号
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
    				var status = Ext.getCmp('amr_statuscode');
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
    				var status = Ext.getCmp('amr_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				this.FormUtil.onDelete(Ext.getCmp('amr_id').value);
    			}
    		},
    		  'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addMaterielReturn', '新增物料申请单', 'jsps/as/port/materielreturn.jsp');
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('amr_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onSubmit(Ext.getCmp('amr_id').value);
    			}
    		},
    		'erpResSubmitButton': {
 			   afterrender: function(btn){
 				   var statu = Ext.getCmp('amr_statuscode');
 				   if(statu && statu.value != 'COMMITED'){
 					   btn.hide();
 				   }
 			   },
 			   click: function(btn){
 				   me.FormUtil.onResSubmit(Ext.getCmp('amr_id').value);
 			   }
 		   },
    	   'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('amr_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('amr_id').value);
    			}
    		},
    		'erpResAuditButton': {
  			   afterrender: function(btn){
  				   var statu = Ext.getCmp('amr_statuscode');
  				   if(statu && statu.value != 'AUDITED'){
  					   btn.hide();
  				   }
  			   },
  			   click: function(btn){
  				   me.FormUtil.onResAudit(Ext.getCmp('amr_id').value);
  			   }
  		   },
    	});
    },
    onGridItemClick: function(selModel, record){//grid行选择
    	this.GridUtil.onGridItemClick(selModel, record);
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});