Ext.QuickTips.init();
Ext.define('erp.controller.fa.fp.Receivable', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
   		'fa.fp.Receivable','core.form.Panel','core.grid.Panel2','core.form.YnField','core.form.MonthDateField','core.form.MultiField','core.form.FileField',
		'core.button.Add','core.button.Submit','core.button.ResSubmit','core.button.Audit','core.button.ResAudit','core.button.Save',
		'core.button.Close','core.button.Upload','core.button.Update','core.button.Delete','core.button.TurnBankRegister',
		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.toolbar.Toolbar'
	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'erpGridPanel2': {
    			reconfigure: function(grid){
    				Ext.defer(function(){
    					var form = grid.ownerCt.down('form'), status = form.down('#' + form.statuscodeField);
        				if(status && (status.value == 'ENTERING' || status.value == 'AUDITED')) {
        					grid.readOnly = false;
        				}
    				}, 500);
    			},
    			itemclick: function(selModel, record){
    				this.GridUtil.onGridItemClick(selModel, record);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addReceivable', '新收款', 'jsps/fa/fp/Receivable.jsp');
    			}
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				var form = me.getForm(btn);
    				this.FormUtil.beforeSave(this);
    			}
    		},
    		'erpDeleteButton' : {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('dcr_id').value);
    			}
    		},
    		'erpUpdateButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('dcr_statuscode');
    				if(status && status.value == 'AUDITED'){
    					btn.show();
    				}
    			},
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('dcr_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				this.FormUtil.onAudit(Ext.getCmp('dcr_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('dcr_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				this.FormUtil.onResAudit(Ext.getCmp('dcr_id').value);
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('dcr_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				this.FormUtil.onSubmit(Ext.getCmp('dcr_id').value);
    				
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('dcr_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				this.FormUtil.onResSubmit(Ext.getCmp('dcr_id').value);
    			}
    		},   	
    		'erpTurnBankRegisterButton':{
    			afterrender: function(btn){
    				var status = Ext.getCmp('dcr_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click:function(btn){
 				    me.batchdeal('Receivable!AccountRegister!Bank', 'dcrd_dcrid=' + Ext.getCmp('dcr_id').value +' and nvl(dcrd_actualsum,0) > nvl(dcrd_turnedamount,0)+nvl(dcrd_thisturnamount,0)', 'fa/ReceivableController/turnBankRegister.action');
    			}   			
    		},
    		'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			},
			'dbfindtrigger[name=dcr_contractno]' : {
	  			  aftertrigger : function(f) {
	  				  if (f.value != null && f.value != '') {
	  					  me.GridUtil.loadNewStore(Ext.getCmp('grid'), {
	  						  caller: caller,
	  						  condition: 'dcrd_dcrid=' + Ext.getCmp('dcr_id').value
	  					  });
	  				  }
	  			  }
	  		},
			'numberfield[name=dcr_managefee]':{
    			change:function(f){
    				if(f.value == null || f.value ==''){
    					f.value = 0;
    				}
    				var dcr_loanamount = Ext.getCmp('dcr_loanamount').value;
    				var dcr_interest = Ext.getCmp('dcr_interest').value;
    				var dcr_managefee = Ext.getCmp('dcr_managefee').value;
    				var dcr_receivesum = dcr_loanamount + dcr_interest +  dcr_managefee;
    				Ext.getCmp('dcr_receivesum').setValue(dcr_receivesum);
    			}
    		},
			'field[name=dcr_accountcode]':{
				beforerender:function(field){
					var status = Ext.getCmp('dcr_statuscode');
					if(status && status.value == 'AUDITED'){
						field.readOnly = false;
					}
				}
			}
    	});
    },
	batchdeal: function(nCaller, condition, url){
	 	   var win = new Ext.window.Window({
	 		   id : 'win',
	 		   height: "100%",
	 		   width: "80%",
	 		   maximizable : true,
	 		   buttonAlign : 'center',
	 		   layout : 'anchor',
	 		   items: [{
	 			   tag : 'iframe',
	 			   frame : true,
	 			   anchor : '100% 100%',
	 			   layout : 'fit',
	 			   html : '<iframe id="iframe_' + caller + '" src="' + basePath + 'jsps/common/editorColumn.jsp?caller=' + nCaller 
	 			   + "&condition=" + condition +'" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>'
	 		   }],
	 		   buttons : [{
	 			   name: 'confirm',
	 			   text : $I18N.common.button.erpConfirmButton,
	 			   iconCls: 'x-button-icon-confirm',
	 			   cls: 'x-btn-gray',
	 			   listeners: {
	 				   buffer: 500,
	 				   click: function(btn) {
	 					   var grid = Ext.getCmp('win').items.items[0].body.dom.getElementsByTagName('iframe')[0].contentWindow.Ext.getCmp("editorColumnGridPanel");
	 					   btn.setDisabled(true);
	 					   grid.updateAction(url);
	 				   }
	 			   }
	 		   }, {
	 			   text : $I18N.common.button.erpCloseButton,
	 			   iconCls: 'x-button-icon-close',
	 			   cls: 'x-btn-gray',
	 			   handler : function(){
	 				   Ext.getCmp('win').close();
	 			   }
	 		   }]
	 	   });
	 	   win.show();
	},
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	onGridItemClick: function(selModel, record){//grid行选择
    	this.gridLastSelected = record;    	
    	this.GridUtil.onGridItemClick(selModel, record);
    }
});