Ext.QuickTips.init();
Ext.define('erp.controller.scm.purchase.ReplaceRateChange', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','scm.purchase.ReplaceRateChange','core.grid.Panel2','core.toolbar.Toolbar','core.button.Appstatus','core.button.ResAppstatus',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit',
				'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail','core.button.ImportExcel',
				'core.button.ResSubmit','core.button.Banned','core.button.ResBanned','core.button.Abate','core.button.ResAbate',
			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.form.FileField','core.button.Sync',
			'core.button.CopyAll','core.grid.detailAttach'],
    init:function(){
    	var me = this;
    	me.alloweditor = true;
    	this.control({
    		'erpGridPanel2': {
    			itemclick: function(view,record){
    				me.itemclick(view,record);
    			}
    		},
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
    				me.FormUtil.onDelete(Ext.getCmp('rc_id').value);
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpAddButton': {
    			afterrender: function(btn){
    				btn.hide();
    			},
    			click: function(){
    				me.FormUtil.onAdd('addPurchasePrice', '新增物料核价', 'jsps/scm/purchase/purchasePrice.jsp');
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				me.FormUtil.beforeClose(me);
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('rc_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				var bool = true;
    				if(bool){
    					me.FormUtil.onSubmit(Ext.getCmp('rc_id').value);
    				}
    			}
    		},
    		 'erpImportExcelButton':{
    			   afterrender:function(btn){
    				   var statuscode=Ext.getCmp('rc_statuscode').getValue();
    				   if(statuscode&&statuscode!='ENTERING'){
    					   btn.hide();
    				   }
    			   }  
    		   },
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('rc_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('rc_id').value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('rc_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('rc_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('rc_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('rc_id').value);
    			}
    		},
    		'erpPrintButton': {
    			click: function(btn){
    				me.FormUtil.onPrint(Ext.getCmp('rc_id').value);
    			}
    		},
            'erpSyncButton': {
            	afterrender: function(btn) {
                    var form = btn.ownerCt.ownerCt, s = form.down('#rc_statuscode');
                    if (s.getValue() != 'AUDITED')
                        btn.hide();
                }
            }
    	});
    }, 
    onGridItemClick: function(selModel, record){//grid行选择
    	if(this.alloweditor){
    		this.GridUtil.onGridItemClick(selModel, record);
    	}
    },
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	itemclick:function(view, record){
    	if (Ext.getCmp('fileform')) {
			Ext.getCmp('fileform').setDisabled(false);
		}
		this.GridUtil.onGridItemClick(view, record);
    },
	getSetting : function(cal, code) {
		var me = this;
		var t = false;
		Ext.Ajax.request({
	   		url : basePath + 'common/getFieldData.action',
	   		async: false,
	   		params: {
	   			caller: 'configs',
	   			field: 'data',
	   			condition: 'code=\''+code+'\' and caller=\''+cal+'\''
	   		},
	   		method : 'post',
	   		callback : function(opt, s, res){
	   			var r = new Ext.decode(res.responseText);
	   			if(r.exceptionInfo){
	   				showError(r.exceptionInfo);return;
	   			}
    			if(r.success && r.data){
    				if(r.data == '1'){
    					t = true;
    				}
    			}
	   		}
		});
		return t;
	}
});