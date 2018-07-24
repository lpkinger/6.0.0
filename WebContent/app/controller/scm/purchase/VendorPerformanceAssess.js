Ext.QuickTips.init();
Ext.define('erp.controller.scm.purchase.VendorPerformanceAssess', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','scm.purchase.VendorPerformanceAssess','core.form.FileField','core.form.MonthDateField',
      		'core.button.Save','core.button.Update','core.button.Add','core.button.Submit','core.button.Audit','core.button.Close','core.form.MultiField',
      		'core.button.Delete','core.button.ResSubmit','core.button.ResAudit','core.trigger.MultiDbfindTrigger','core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField',
      	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'erpSaveButton': {
  			   click: function(btn){
  				  console.log(me);
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
     				me.FormUtil.onDelete(Ext.getCmp('vpa_id').value);
     			},
     		},
     		'erpAddButton': {
     			afterrender: function(btn){
    				   var statu = Ext.getCmp('vpa_statuscode');
    				   if(statu && statu.value != 'ENTERING'){
    					   btn.hide();
    				   }
    			   },
     			click: function(){
     				me.FormUtil.onAdd('addVPA', '新增供应商绩效考核', 'jsps/scm/purchase/vendorPerformanceAssess.jsp');
     			}
     		},
     		 'erpSubmitButton': {
   			   afterrender: function(btn){
   				   var statu = Ext.getCmp('vpa_statuscode');
   				   if(statu && statu.value != 'ENTERING'){
   					   btn.hide();
   				   }
   			   },
   			   click: function(btn){
   				   me.FormUtil.onSubmit(Ext.getCmp('vpa_id').value);
   			   }
   		   },
   		 'erpResSubmitButton': {
 			   afterrender: function(btn){
 				   var statu = Ext.getCmp('vpa_statuscode');
 				   if(statu && statu.value != 'COMMITED'){
 					   btn.hide();
 				   }
 			   },
 			   click: function(btn){
 				   me.FormUtil.onResSubmit(Ext.getCmp('vpa_id').value);
 			   }
 		   },
 			'erpAuditButton': {
 				afterrender: function(btn){
 					var status = Ext.getCmp('vpa_statuscode');
 					if(status && status.value != 'COMMITED'){
 						btn.hide();
 					}
 				},
 				click: function(btn){
 					me.FormUtil.onAudit(Ext.getCmp('vpa_id').value);
 				}
 			},
 			'erpResAuditButton': {
  			   afterrender: function(btn){
  				   var statu = Ext.getCmp('vpa_statuscode');
  				   if(statu && statu.value != 'AUDITED'){
  					   btn.hide();
  				   }
  			   },
  			   click: function(btn){
  				   me.FormUtil.onResAudit(Ext.getCmp('vpa_id').value);
  			   }
  		   }
    	});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}

});