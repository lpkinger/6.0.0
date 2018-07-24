Ext.QuickTips.init();
Ext.define('erp.controller.scm.product.Sampleapply', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    views:[
	'scm.product.Sampleapply','core.form.Panel','core.grid.Panel2','core.toolbar.Toolbar','core.form.FileField',
	'core.button.TurnProductSamp','core.button.TurnProductApproval',
	'core.button.Add','core.button.Save','core.button.Close','core.button.DeleteDetail',
	'core.button.Update','core.button.Delete','core.form.YnField','core.form.FileField',
	'core.button.ResAudit','core.button.Audit','core.button.Submit','core.button.ResSubmit',
	'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger',
	'core.button.TurnProductSample'
    	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'erpGridPanel2': { 
    			itemclick: this.onGridItemClick
    		},
    		'erpSaveButton': {
    			click: function(btn){
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
    				me.FormUtil.onDelete(Ext.getCmp('sa_id').value);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addSampleapply', '新增研发样品申请单', 'jsps/scm/product/Sampleapply.jsp');
    			}
    		},'erpSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('sa_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onSubmit(Ext.getCmp('sa_id').value);
				}
			},
			'erpResSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('sa_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResSubmit(Ext.getCmp('sa_id').value);
				}
			},
			'erpAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('sa_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onAudit(Ext.getCmp('sa_id').value);
				}
			},
			'erpResAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('sa_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResAudit(Ext.getCmp('sa_id').value);
				}
			},
			'erpTurnProductSampButton':{
				afterrender:function(btn){
					var status = Ext.getCmp('sa_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
					var requirement = Ext.getCmp('sa_requirement');
					if(requirement && requirement.value != '是'){
						btn.hide();
					}
				}
			},
    		'erpTurnProductSampleButton':{
    			afterrender: function(btn){
					var status = Ext.getCmp('sa_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
					var requirement = Ext.getCmp('sa_requirement');
					if(requirement && requirement.value != '是'){
						btn.hide();
					}
				},
				click: function(btn){
					var id = Ext.getCmp('sa_id').value;
					var code = Ext.getCmp('sa_code').value; 
					var param = this.GridUtil.getAllGridStore();
					param = "[" + param.toString() + "]";
					warnMsg('确定要转打样申请单吗?', function(btn){
						if (btn == 'yes') {
							Ext.Ajax.request({
								url:basePath + "scm/product/turnProductSample.action",
								params:{
									code:code,
									param:param,
									id:id
								},
								method:'post',
								callback:function(options,success,response){
									var res = new Ext.decode(response.responseText);
									if(res.success){
										Ext.Msg.alert("提示", "转打样申请单成功！");
									}else{
										Ext.Msg.alert("提示", "转打样申请单失败！");
									}
								}
							});
						} else {
							return;
						}
					});
				}
    		},
    		'erpTurnProductApprovalButton':{
				afterrender: function(btn){
					var status = Ext.getCmp('sa_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
					var requirement = Ext.getCmp('sa_requirement');;
					if(requirement && requirement.value == '是'){
						btn.hide();
					}
				},
				click: function(btn){
					this.turn('Sampleapply!Deal', 'sd_said=' + Ext.getCmp('sa_id').value + ' AND nvl(sd_turnprostatus,\' \')=\' \'', 'scm/product/sampleapply/turnProductApproval.action');
				}
			}
    	});
    },
    turn: function(nCaller, condition, url){
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
    onGridItemClick: function(selModel, record){//grid行选择
    	this.GridUtil.onGridItemClick(selModel, record);
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});