Ext.QuickTips.init();
Ext.define('erp.controller.oa.appliance.ProductYP', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'oa.appliance.ProductYP','core.form.Panel','core.form.MultiField','core.form.FileField',
    		'core.button.Add','core.button.Submit','core.button.ResSubmit','core.button.Audit','core.button.Save','core.button.Close','core.button.Sync',
			'core.button.Upload','core.button.Update','core.form.YnField','core.button.Banned','core.button.ResBanned','core.button.Delete','core.button.ResAudit',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.trigger.MultiDbfindTrigger','core.form.MultiField'
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
    		'erpCloseButton': {
    			click: function(btn){
    				this.FormUtil.beforeClose(this);
    			}
    		},
    		'erpUpdateButton': {
    			afterrender: function(btn){
					var status = Ext.getCmp('pr_statuscode');
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
					var status = Ext.getCmp('pr_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('pr_id').value);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addProductYP', '新增用品资料', 'jsps/oa/appliance/productYP.jsp');
    			}
    		},
    		'erpSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('pr_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onSubmit(Ext.getCmp('pr_id').value);
				}
			},
			'erpResSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('pr_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResSubmit(Ext.getCmp('pr_id').value);
				}
			},
			'erpAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('pr_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onAudit(Ext.getCmp('pr_id').value);
				}
			},
			'erpResAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('pr_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResAudit(Ext.getCmp('pr_id').value);
				}
			},
			'erpBannedButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('pr_statuscode');
					if(status && (status.value == 'DELETED' || status.value == 'DISABLE')){
						btn.hide();
					}
				},
				click: function(btn){
					// confirm box modify
					// zhuth 2018-2-1
					Ext.Msg.confirm('提示', '确定要禁用此物料?', function(btn) {
						if(btn == 'yes') {
							//me.FormUtil.onBanned(Ext.getCmp('pr_id').value);
							me.toDisable();//zhongyl 2014 03 13
						}
					});
				}
			},
			'erpResBannedButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('pr_statuscode');
					if(status && status.value != 'DISABLE'){
						btn.hide();
					}
				},
				click: function(btn){
					// confirm box modify
					// zhuth 2018-2-1
					Ext.Msg.confirm('提示', '确定要反禁用此物料?', function(btn) {
						if(btn == 'yes') {
							me.FormUtil.onResBanned(Ext.getCmp('pr_id').value);
						}
					});
				}
			},
			'erpSyncButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('pr_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
					btn.caller = 'Product!Post';
				}
			},
    	});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	toDisable:function(){
		Ext.create('Ext.window.Window',{
	       	 width:350,
	       	 height:185,
	       	 id:'win',
	       	 title:'<h1>禁用物料</h1>',
	       	 layout:'column',
	       	 items:[{
					margin: '10 0 0 0',
					xtype: 'textfield',
					fieldLabel: '禁用备注',
					name:'disremark',
					value: '', 
				}],
	       	 buttonAlign:'center',
	       	 buttons:[{
	 				xtype:'button',
	 				columnWidth:0.12,
	 				text:'确定',
	 				width:60,
	 				iconCls: 'x-button-icon-save',
	 				handler:function(btn){ 
	 					var remark=btn.ownerCt.ownerCt.down('textfield[name=disremark]').value;
	 					if (remark==null || remark==''){
	 						showError('禁用备注必须填写');
	 					}
 						Ext.Ajax.request({
 					   		url : basePath + 'scm/product/bannedProduct.action',
 					   		params: {
 					   			caller : caller,
 					   			id     : Ext.getCmp('pr_id').value, 
 					   			remark : remark 
 					   		},
 					   		method : 'post',
 					   	callback: function(opt, s, r) {
 							var rs = Ext.decode(r.responseText);
 							if(rs.exceptionInfo) {
 								showError(rs.exceptionInfo);
 							} else {
 								alert('更新成功!');
 								window.location.reload();
 							}
 					   	}
 						});
	 				}
	 			},{
	 				xtype:'button',
	 				columnWidth:0.1,
	 				text:'取消',
	 				width:60,
	 				iconCls: 'x-button-icon-close',
	 				margin:'0 0 0 10',
	 				handler:function(btn){
	 					Ext.getCmp('win').close();
	 				}
	 			}]
	        }).show(); 
	}
});