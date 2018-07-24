Ext.QuickTips.init();
Ext.define('erp.controller.crm.customermgr.customervisit.SellerSaleReport', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'crm.customermgr.customervisit.SellerSaleReport','core.form.Panel','core.grid.Panel2','core.toolbar.Toolbar',
    		'core.grid.YnColumn','core.button.Scan',
    		'core.button.Add','core.button.Submit','core.button.Audit','core.button.Save','core.button.Close','core.button.Print',
    			'core.button.Upload','core.button.Update','core.button.Delete','core.button.ResAudit','core.button.ResSubmit',
    			'core.trigger.DbfindTrigger','core.form.YnField','core.button.DeleteDetail','core.button.Upload','core.form.FileField',
    			'core.trigger.MultiDbfindTrigger','core.form.MultiField'
    	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'erpGridPanel2': { 
    			itemclick: this.onGridItemClick
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				var form = me.getForm(btn);
    				this.FormUtil.beforeSave(this);
    			}
    		},
    		'erpAddButton': {
    			click: function(btn){
    				me.FormUtil.onAdd('addSellerSaleReport', '新增推广进度项目表 ', 'jsps/crm/customermgr/customervisit/sellerSaleReport.jsp');
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
    				me.FormUtil.onDelete((Ext.getCmp('ps_id').value));
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