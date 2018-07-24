Ext.QuickTips.init();
Ext.define('erp.controller.scm.purchase.MakeExp', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','scm.purchase.MakeExp','core.grid.Panel2','core.toolbar.Toolbar','core.form.MultiField',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit',
  			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail',
  			'core.button.ResSubmit','core.button.Banned','core.button.ResBanned','core.button.Flow',
			'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.form.YnField','core.button.POGetPrice'
      	],
    init:function(){
    	var me = this;
		this.control({
			'erpGridPanel2': { 
    			itemclick: this.onGridItemClick
    		},
    		'field[name=ma_currency]': {
    			beforetrigger: function(field) {
    				var t = field.up('form').down('field[name=ma_date]'),
    					value = t.getValue();
    				if(value) {
    					field.findConfig = 'cm_yearmonth=' + Ext.Date.format(value, 'Ym');
    				}
    			}
    		},
			'erpSaveButton': {
				click: function(btn){
					var form = me.getForm(btn);
					if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
						me.BaseUtil.getRandomNumber();//自动添加编号
					}
					//保存之前的一些前台的逻辑判定
					this.FormUtil.beforeSave(this);
				}
			},
			'erpDeleteButton' : {
				click: function(btn){
					me.FormUtil.onDelete(Ext.getCmp('me_id').value);
				}
			},
			'erpUpdateButton': {
				click: function(btn){
					this.FormUtil.onUpdate(this);
				}
			},
			'erpAddButton': {
				click: function(){
					me.FormUtil.onAdd('addMakeExp', '新增委外PO单', 'jsps/scm/purchase/makeexp.jsp');
				}
			},
			'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			},
			'erpSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('me_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onSubmit(Ext.getCmp('me_id').value);
				}
			},
			'erpResSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('me_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResSubmit(Ext.getCmp('me_id').value);
				}
			},
			'erpAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('me_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onAudit(Ext.getCmp('me_id').value);
				}
			},
			'erpResAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('me_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResAudit(Ext.getCmp('me_id').value);
				}
			},
			'erpPOGetPriceButton' : {
				/*afterrender: function(btn){
					var status = Ext.getCmp('me_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				},*/
				click: function(btn){
					var me_code = Ext.getCmp('me_code').value;
					var form = me.getForm(btn);
					form.setLoading(true);
					Ext.Ajax.request({
						url : basePath + 'scm/purchase/getPOPrice.action',
						params : {
							me_code : me_code
						},
						method : 'post',
						callback : function(o,s,r){
							form.setLoading(false);
							var res = Ext.decode(r.responseText);
							if(res.exceptionInfo){
								showError(res.exceptionInfo);
								return;
							}
							if(res.success){
								showError("获取成功！");
								window.location.reload();
							}
							
						}
					});
				}
			},
			/*'erpPrintButton': {
				click:function(btn){
					var reportName="MAKE";
					var condition='{Make.ma_id}='+Ext.getCmp('ma_id').value+'';
					var id=Ext.getCmp('ma_id').value;
					me.FormUtil.onwindowsPrint(id,reportName,condition);
				}
			},*/
			/*'erpBannedButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('ma_statuscode');
					if(status && status.value == 'BANNED'){
						btn.hide();
					}
				},
    			click: function(btn){
    				this.FormUtil.onBanned(Ext.getCmp('ma_id').value);
    			}
    		},*/
    		/*'erpResBannedButton': {
    			afterrender: function(btn){
					var status = Ext.getCmp('ma_statuscode');
					if(status && status.value != 'BANNED'){
						btn.hide();
					}
				},
    			click: function(btn){
    				this.FormUtil.onResBanned(Ext.getCmp('ma_id').value);
    			}
    		}*/
		});
	},
	onGridItemClick: function(selModel, record){//grid行选择
    	this.GridUtil.onGridItemClick(selModel, record);
    },
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});