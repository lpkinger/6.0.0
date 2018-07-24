Ext.QuickTips.init();
Ext.define('erp.controller.as.port.MaterielOut', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    views:[
    		'as.port.MaterielOut','core.form.Panel','core.toolbar.Toolbar','core.grid.Panel2',
    		'core.button.Add','core.button.Save','core.button.Close',
    		'core.button.Update','core.button.Delete','core.form.YnField',
    		'core.button.ResAudit','core.button.Audit','core.button.Submit','core.button.ResSubmit',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.button.UpdateQty'
    	],
    init:function(){
      var me = this;
    	this.control({
    		'#amo_text4' :{
    			beforerender : function(f){
					var statuscode = Ext.getCmp('amo_statuscode');
					if(statuscode&&statuscode.value!='COMMITED'){
						f.readOnly=false;
    					f.fieldStyle="background:#FFFAFA;color:#515151;;background:##fbfbfb;";
					}
				}
    		},
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
 		   'erpUpdateQtyButton':{
 			  afterrender: function(btn){
  				var status = Ext.getCmp('amo_statuscode');
  				if(status && status.value != 'COMMITED'){
  					btn.hide();
  				}
  			},
  			click:function(btn){
				 var grid=Ext.getCmp('grid');
 				 grid.GridUtil.onUpdate(grid,'as/port/updateMaterialQtyChangeInProcss.action?caller=' +caller);
 			  }
 		   },
    		'erpCloseButton': {
    			click: function(btn){
    				this.FormUtil.beforeClose(this);
    			}
    		},
    		'erpUpdateButton': {
    		    afterrender: function(btn){
    				var status = Ext.getCmp('amo_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.show();
    				}
    			},
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpDeleteButton': {
    		  afterrender: function(btn){
    				var status = Ext.getCmp('amo_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				this.FormUtil.onDelete(Ext.getCmp('amo_id').value);
    			}
    		},
    		  'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addMaterielOut', '新增物料申请单', 'jsps/as/port/materielout.jsp');
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('amo_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onSubmit(Ext.getCmp('amo_id').value);
    			}
    		},
    		'erpResSubmitButton': {
 			   afterrender: function(btn){
 				   var statu = Ext.getCmp('amo_statuscode');
 				   if(statu && statu.value != 'COMMITED'){
 					   btn.hide();
 				   }
 			   },
 			   click: function(btn){
 				   me.FormUtil.onResSubmit(Ext.getCmp('amo_id').value);
 			   }
 		   },
    	   'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('amo_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('amo_id').value);
    			}
    		},
    		'erpResAuditButton': {
  			   afterrender: function(btn){
  				   var statu = Ext.getCmp('amo_statuscode');
  				   if(statu && statu.value != 'AUDITED'){
  					   btn.hide();
  				   }
  			   },
  			   click: function(btn){
  				   me.FormUtil.onResAudit(Ext.getCmp('amo_id').value);
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