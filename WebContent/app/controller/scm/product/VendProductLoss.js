Ext.QuickTips.init();
Ext.define('erp.controller.scm.product.VendProductLoss', {
	extend : 'Ext.app.Controller',
	FormUtil : Ext.create('erp.util.FormUtil'),
	BaseUtil : Ext.create('erp.util.BaseUtil'),
	GridUtil : Ext.create('erp.util.GridUtil'),
	views : [ 'scm.product.VendProductLoss', 'core.form.Panel', 'core.form.FileField','core.form.MultiField', 'core.form.CheckBoxGroup',
			'core.trigger.MultiDbfindTrigger', 'core.button.Save','core.button.Add', 'core.button.Submit', 'core.button.Print',
			'core.button.Upload', 'core.button.ResAudit', 'core.button.Audit','core.button.Close', 'core.button.Delete', 'core.button.Update',
			'core.button.ResSubmit', 'core.grid.Panel2','core.button.TurnCustomer', 'core.button.Flow','core.button.DownLoad', 'core.button.Scan',
			'common.datalist.Toolbar', 'core.button.Confirm','core.trigger.DbfindTrigger', 'core.trigger.TextAreaTrigger',
			'core.form.YnField', 'core.trigger.AutoCodeTrigger','core.trigger.AddDbfindTrigger' ],
	init : function() {
		var me = this;
		this.control({
			/*'erpGridPanel2' : {
				itemclick : this.onGridItemClick
			},*/
			'erpSaveButton' : {
				click : function(btn) {
					var form = me.getForm(btn);
					if (Ext.getCmp(form.codeField).value == null
							|| Ext.getCmp(form.codeField).value == '') {
						me.BaseUtil.getRandomNumber();// 自动添加编号
					}
					// 保存之前的一些前台的逻辑判定
					this.FormUtil.beforeSave(this);
				},
				afterrender : function(btn) {
					var value = Ext.getCmp('vpl_id').getValue();
					if (value) {
						btn.hide();
					}
				}
			},
			'erpDeleteButton' : {
				click : function(btn) {
					me.FormUtil.onDelete(Ext.getCmp('vpl_id').value);
				},
				afterrender : function(btn) {
					var value = Ext.getCmp('vpl_id').getValue();
					if (!value) {
					   btn.hide();
					}
				}
			},
			'erpUpdateButton' : {
				click : function(btn) {
					this.FormUtil.onUpdate(this);
				},
				afterrender : function(btn) {
					var value = Ext.getCmp('vpl_id').getValue();
					if (!value) {
						btn.setDisabled(true);
					}
				}
			},
			/*'hidden[id=pl_id]':{
    			change:function(field){
    			Ext.ComponentQuery.query('erpSaveButton')[0].hide();
    			Ext.ComponentQuery.query('erpDeleteButton')[0].setDisabled(false);
    			Ext.ComponentQuery.query('erpUpdateButton')[0].setDisabled(false);
    			
    			}
    		 },*/
			'erpAddButton' : {
				click : function(btn) {
					//var condition = me.BaseUtil.getUrlParam('formCondition');
					//if(condition){						
    				 me.FormUtil.onAdd('addProductLoss', '新增委外供应商物料损耗规则', 'jsps/scm/product/VendProductLoss.jsp');
						//window.location.href=basePath+"jsps/scm/sale/productLoss.jsp";
					/*}else{
						me.getForm(btn).getForm().reset();
						Ext.ComponentQuery.query('erpSaveButton')[0].show();
						Ext.ComponentQuery.query('erpDeleteButton')[0]
								.setDisabled(true);
						Ext.ComponentQuery.query('erpUpdateButton')[0]
								.setDisabled(true);
					}*/
				}
			},
			'erpCloseButton' : {
				click : function(btn) {
					me.FormUtil.beforeClose(me);
				}
			}
		});
	},
	onGridItemClick : function(selModel, record) {// grid行选择
		Ext.getCmp('form').getForm().setValues(record.data);
	},
	getForm : function(btn) {
		return btn.ownerCt.ownerCt;
	}
});