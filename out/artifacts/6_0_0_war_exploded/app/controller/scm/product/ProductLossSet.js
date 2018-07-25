Ext.QuickTips.init();
Ext.define('erp.controller.scm.product.ProductLossSet', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','scm.product.ProductLossSet','core.grid.Panel2','core.toolbar.Toolbar','core.form.MultiField',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit',
  			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail',
  			'core.button.ResSubmit','core.button.Banned','core.button.ResBanned','core.button.Flow','core.button.CatchProdCode',
			'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.form.YnField','core.button.ProductDetail','core.button.CopyByConfigs'
      	],
    init:function(){
    	var me = this;
		this.control({
			'erpGridPanel2': { 
    			itemclick: this.onGridItemClick,
    		},
			'erpSaveButton': {
				click: function(btn){
					var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber();//自动添加编号
    				}
					me.FormUtil.beforeSave(me);
				}
			},
			'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addProductLossSet', '新增损耗规则单', 'jsps/scm/product/productLossSet.jsp');
    			}
    		},
			'erpDeleteButton' : {
				click: function(btn){
					me.FormUtil.onDelete(Ext.getCmp('ps_id').value);
				}
			},
			'erpUpdateButton': {
				click: function(btn){
					this.FormUtil.onUpdate(this);
				}
			},
			'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			},
			'erpSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('ps_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onSubmit(Ext.getCmp('ps_id').value);
				}
			},
			'erpResSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('ps_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResSubmit(Ext.getCmp('ps_id').value);
				}
			},
			'erpAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('ps_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					var me = this;
					var form = Ext.getCmp('form');
					var id = Ext.getCmp('ps_id').value;
					me.setLoading(true);
					Ext.Ajax.request({
						url : basePath + form.auditUrl,
						params: {
							caller : caller,
							id: id
						},
						method : 'post',
						callback : function(options,success,response){
							me.setLoading(false);
							var localJson = new Ext.decode(response.responseText);
							if(localJson.success){
								if(localJson.log){
			    					showMessage("提示", localJson.log);
			    				}
								window.location.reload();
							} else {
								if(localJson.exceptionInfo){
									var str = localJson.exceptionInfo;
									if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
										str = str.replace('AFTERSUCCESS', '');
										showMessage("提示", str);
										auditSuccess(function(){
											window.location.reload();
										});
									} else {
										showError(str);return;
									}
								}
							}
						}
					});
				}
			},
			'erpResAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('ps_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResAudit(Ext.getCmp('ps_id').value);
				}
			}
		});
	},
	onGridItemClick: function(selModel, record){//grid行选择
    	this.GridUtil.onGridItemClick(selModel, record);
    },
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	setLoading : function(b) {// 原this.getActiveTab().setLoading()换成此方法,解决Window模式下无loading问题
		var mask = this.mask;
		if (!mask) {
			this.mask = mask = new Ext.LoadMask(Ext.getBody(), {
				msg : "处理中,请稍后...",
				msgCls : 'z-index:10000;'
			});
		}
		if (b)
			mask.show();
		else
			mask.hide();
	},
});