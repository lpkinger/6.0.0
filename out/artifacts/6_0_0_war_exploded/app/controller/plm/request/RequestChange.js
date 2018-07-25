Ext.QuickTips.init();
Ext.define('erp.controller.plm.request.RequestChange', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    views:[
	'plm.request.RequestChange','core.form.Panel','core.toolbar.Toolbar',
	'core.button.Add','core.button.Save','core.button.Close','core.button.Banned','core.button.ResBanned',
	'core.button.Update','core.button.Delete','core.form.YnField','core.button.Sync',
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
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpDeleteButton': {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('prc_id').value);
    			},
    		},
    		'erpAddButton': {
    			afterrender: function(btn){
   				   var statu = Ext.getCmp('prc_statuscode');
   				   if(statu && statu.value != 'ENTERING'){
   					   btn.hide();
   				   }
   			   },
    			click: function(){
    				me.FormUtil.onAdd('addRequestChange', '新增需求单变更', 'jsps/plm/request/requestChange.jsp');
    			}
    		},
    		 'erpSubmitButton': {
  			   afterrender: function(btn){
  				   var statu = Ext.getCmp('prc_statuscode');
  				   if(statu && statu.value != 'ENTERING'){
  					   btn.hide();
  				   }
  			   },
  			   click: function(btn){
  				   me.FormUtil.onSubmit(Ext.getCmp('prc_id').value);
  			   }
  		   },
  		 'erpResSubmitButton': {
			   afterrender: function(btn){
				   var statu = Ext.getCmp('prc_statuscode');
				   if(statu && statu.value != 'COMMITED'){
					   btn.hide();
				   }
			   },
			   click: function(btn){
				   me.FormUtil.onResSubmit(Ext.getCmp('prc_id').value);
			   }
		   },
			'erpAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('prc_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onAudit(Ext.getCmp('prc_id').value);
				}
			},
			'erpResAuditButton': {
 			   afterrender: function(btn){
 				   var statu = Ext.getCmp('prc_statuscode');
 				   if(statu && statu.value != 'AUDITED'){
 					   btn.hide();
 				   }
 			   },
 			   click: function(btn){
 				   me.FormUtil.onResAudit(Ext.getCmp('prc_id').value);
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