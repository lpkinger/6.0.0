Ext.QuickTips.init();
Ext.define('erp.controller.scm.sale.EvaluationRemark', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'scm.sale.EvaluationRemark','core.form.Panel','core.grid.Panel2','core.toolbar.Toolbar',
      		'core.button.Save','core.button.Upload','core.button.Close','core.button.Update','core.button.DeleteDetail',
      		'core.button.Add',
      		'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger'
      	],
    init:function(){
    	var me = this;
    	this.control({
    		'erpGridPanel2': { 
				itemclick: this.onGridItemClick
			},
    		'erpSaveButton': {
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpUpdateButton': {
				click: function(btn){
					this.FormUtil.onUpdate(this);
				}
			},
    		'erpAddButton': {
				click: function(){
					me.FormUtil.onAdd('addEvaluationRemark', '新增估价单备注', 'jsps/scm/sale/evaluationRemark.jsp');
				}
			},
    		'erpCloseButton': {
    			click: function(btn){
    				me.FormUtil.beforeClose(me);
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