Ext.QuickTips.init();
Ext.define('erp.controller.oa.appliance.OaacceptanceYT', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'oa.appliance.OaacceptanceYT','core.form.Panel','core.grid.Panel2','core.toolbar.Toolbar','core.form.MultiField','core.form.FileField',
    		'core.button.Add','core.button.Save','core.button.Close','core.button.DeleteDetail',
    		'core.button.Update','core.button.Delete','core.form.YnField','core.button.Submit','core.button.Post','core.button.ResPost',
    		'core.button.ResAudit','core.button.ResSubmit','core.button.Audit','core.button.TurnOainstorage',
    		'core.trigger.TextAreaTrigger','core.button.Print','core.trigger.DbfindTrigger'
    	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'erpGridPanel2': { 
    			itemclick: this.onGridItemClick
    		},
    		'dbfindtrigger[name=od_opcode]': {
    			afterrender:function(trigger){
	    			trigger.dbKey='op_vecode';
	    			trigger.mappingKey='op_vecode';
	    			trigger.dbMessage='请先选择供应商';
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
    				console.log(Ext.getCmp('op_id').value);
    				me.FormUtil.onDelete(Ext.getCmp('op_id').value);
    			}
    		},
    		'erpPrintButton':{// 打印
    			click: function(btn){
    				var reportName = "OfficeAccOutlist";
    				var condition = '{Oaacceptance.op_id}=' + Ext.getCmp('op_id').value + '';
    				var id = Ext.getCmp('op_id').value;
    				me.FormUtil.onwindowsPrint(id, reportName, condition);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addOaacceptance', '新增采购验退单', 'jsps/oa/appliance/oaacceptanceYT.jsp');
    			}
    		},'erpSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('op_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onSubmit(Ext.getCmp('op_id').value);
				}
			},
			'erpResSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('op_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResSubmit(Ext.getCmp('op_id').value);
				}
			},
			'erpAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('op_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onAudit(Ext.getCmp('op_id').value);
				}
			},
			'erpResAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('op_statuscode');
					if(status && status.value != 'AUDITED' || Ext.getCmp('op_inoutstatus').value=='已过账'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResAudit(Ext.getCmp('op_id').value);
				}
			},
			'erpPostButton': {
				afterrender: function(btn){
    				var status = Ext.getCmp('op_inoutstatuscode');
    				if(status && status.value != 'UNPOST'){
    					btn.hide();
    				}
    			},
    			buffer : 1000,
    			click: function(btn){
    				me.FormUtil.onPost(Ext.getCmp('op_id').value);
    			}
			},
			'erpResPostButton': {
				afterrender: function(btn){
    				var status = Ext.getCmp('op_inoutstatuscode');
    				if(status && status.value != 'POSTED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResPost(Ext.getCmp('op_id').value);
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