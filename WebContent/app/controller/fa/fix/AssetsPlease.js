Ext.QuickTips.init();
Ext.define('erp.controller.fa.fix.AssetsPlease', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','fa.fix.AssetsPlease','core.grid.Panel2','core.toolbar.Toolbar',
      		'core.button.Save','core.button.Scan','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit',
  			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail','core.button.ResSubmit',
  			'core.button.Banned','core.button.ResBanned','core.button.Post','core.button.ResPost',
  			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger'
      	],
    init:function(){
    	var me = this;
    	me.gridLastSelected = null;
    	this.control({ 
    		'erpGridPanel2': { 
    			itemclick: function(selModel, record){
    				this.GridUtil.onGridItemClick(selModel, record);
    			}
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					Ext.getCmp(form.codeField).setValue(me.BaseUtil.getRandomNumber());//自动添加编号
    				}
    				me.save(btn);
    			}
    		},
    		'erpDeleteButton' : {
    			click: function(btn){
    				me.FormUtil.onDelete({ap_id: Number(Ext.getCmp('ap_id').value)});
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				me.FormUtil.onUpdate(me);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('add' + caller, '新增', "jsps/fa/fix/assetsPlease.jsp?whoami=" + caller);
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				me.FormUtil.beforeClose(me);
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp(me.getForm(btn).statuscodeField);
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onSubmit(Ext.getCmp('ap_id').value);
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp(me.getForm(btn).statuscodeField);
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('ap_id').value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp(me.getForm(btn).statuscodeField);
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('ap_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp(me.getForm(btn).statuscodeField);
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('ap_id').value);
    			}
    		},
    		'erpBannedButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp(me.getForm(btn).statuscodeField);
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onBanned(Ext.getCmp('ap_id').value);
    			}
    		},
    		'erpResBannedButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp(me.getForm(btn).statuscodeField);
    				if(status && status.value != 'BANNED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResBanned(Ext.getCmp('ap_id').value);
    			}
    		},
    		'erpPrintButton': {
    			click: function(btn){
    				me.FormUtil.onPrint(Ext.getCmp('ap_id').value);
    			}
    		},
    		'erpPostButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp(me.getForm(btn).statuscodeField);
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			}
    		},
    		'erpResPostButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp(me.getForm(btn).statuscodeField);
    				if(status && status.value != 'POSTED'){
    					btn.hide();
    				}
    			}
    		},
    		'erpScanButton': {
    			afterrender: function(btn){
    				btn.urlcondition = "ap_statuscode<>'DELETED' and ap_class = '"+Ext.getCmp("ap_class").getValue()+"'";
    			}
    		}
    	});
    },
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	save: function(btn){
		var me = this;
//		Ext.getCmp('Fin_Code').setValue(me.BaseUtil.getRandomNumber());//流水号
		var form = me.getForm(btn);
		if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
			Ext.getCmp(form.codeField).setValue(me.BaseUtil.getRandomNumber());//自动添加编号
		}
//		var grid = Ext.getCmp('grid');
//	    Ext.Array.each(grid.store.data.items, function(item){
//	    	item.set('pd_inoutno', Ext.getCmp('pi_inoutno').value);
//	    	item.set('pd_piclass', Ext.getCmp('pi_class').value);
//		});
		me.FormUtil.beforeSave(me);
	}
});