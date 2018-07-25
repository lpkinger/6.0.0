/**
 * 
 */
Ext.define('erp.view.fa.gs.AccountRegisterBillGrid',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.accountregisterbill',
	layout : 'fit',
	id: 'billgrid', 
 	emptyText : $I18N.common.grid.emptyText,
 	readOnly: true,
    columnLines : true,
    autoScroll : true,
    keyField: 'arb_id',
    mainField: 'arb_arid',
    columns: new Array(),
    bodyStyle:'background-color:#f1f1f1;',
    plugins : [ Ext.create('erp.view.core.plugin.CopyPasteMenu') ],
    GridUtil: Ext.create('erp.util.GridUtil'),
	initComponent : function(){
		this.callParent(arguments); 
	},
	getMyData: function(id){
		var me = this;
		var params = {
    		caller: "AccountRegisterBill", 
    		condition: "arb_arid=" + id
    	};
		if(me.columns && me.columns.length > 0){
			me.GridUtil.loadNewStore(me, params);
		} else {
			me.GridUtil.getGridColumnsAndStore(me, 'common/singleGridPanel.action', params);
		}
	}
});