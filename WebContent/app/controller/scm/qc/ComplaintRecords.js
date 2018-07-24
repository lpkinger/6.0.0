Ext.QuickTips.init();
Ext.define('erp.controller.scm.qc.ComplaintRecords', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    views:[
      		'core.form.Panel','scm.qc.ComplaintRecords','core.toolbar.Toolbar','core.form.MultiField',
      		'core.button.Print','core.button.ResAudit','core.button.Audit','core.button.Close',
      		'core.button.Delete','core.button.Update','core.button.Add','core.button.Submit',
      		'core.button.ResSubmit','core.button.Save','core.button.ComplaintUpdate','core.button.End','core.button.ResEnd',
			'core.form.FileField','core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.grid.YnColumn'
      	],
    init:function(){
    	var me = this;
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
    		'erpDeleteButton' : {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('cr_id').value);
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpEndButton': {
 			   afterrender: function(btn){
 				   var status = Ext.getCmp('cr_statuscode');
 				   if(status && status.value != 'AUDITED'){
 					   btn.hide();
 				   }
 			   },
 			   click: function(btn){
 				   me.FormUtil.onEnd(Ext.getCmp('cr_id').value);
 			   }
 		    },
 		    'erpResEndButton': {
 		 	   afterrender: function(btn){
 				   var status = Ext.getCmp('cr_statuscode');
 		 		   if(status && status.value != 'FINISH'){
 					   btn.hide();
 		 		   }
 		 	   },
 			   click: function(btn){
 				   me.FormUtil.onResEnd(Ext.getCmp('cr_id').value);
 			   }
 		    },
    		'erpCloseButton': {
    			click: function(btn){
    				me.FormUtil.beforeClose(me);
    			}
    		},
    		'erpAddButton': {
				click: function(){
					me.FormUtil.onAdd('addComplaintRecords', '新增客户投诉单', 'jsps/scm/qc/ComplaintRecords.jsp?whoami=ComplaintRecords');
				}
			},
			'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			},
			'erpSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('cr_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onSubmit(Ext.getCmp('cr_id').value);
				}
			},
			'erpResSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('cr_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResSubmit(Ext.getCmp('cr_id').value);
				}
			},
			'erpAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('cr_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onAudit(Ext.getCmp('cr_id').value);
				}
			},
			'erpResAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('cr_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResAudit(Ext.getCmp('cr_id').value);
				}
			},
			'erpPrintButton': {
				click: function(btn){
				var id = Ext.getCmp('cr_id').value;
				var condition = '{ComplaintRecords.cr_id}=' + Ext.getCmp('cr_id').value + '';
				var reportName="tousu";
				me.FormUtil.onwindowsPrint(id, reportName, condition);
				}
			}
    	});
    }, 
    onGridItemClick: function(selModel, record){//grid行选择
    	this.GridUtil.onGridItemClick(selModel, record);
    },
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});