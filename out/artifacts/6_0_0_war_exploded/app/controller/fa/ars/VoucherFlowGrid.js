/**
 * 
 */
Ext.define('erp.view.fa.ars.VoucherFlowGrid',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.voucherflowgrid',
	layout : 'fit',
	id: 'flowgrid', 
 	emptyText : $I18N.common.grid.emptyText,
    columnLines : true,
    autoScroll : true,
    detno: 'vf_detno',
    keyField: 'vf_id',
    mainField: 'vf_voucherid',
    necessaryField: 'vf_flowcode',
    columns: new Array(),
    bodyStyle:'background-color:#f1f1f1;',
    plugins: Ext.create('Ext.grid.plugin.CellEditing', {
        clicksToEdit: 1
    }),
    GridUtil: Ext.create('erp.util.GridUtil'),
	initComponent : function(){
		this.callParent(arguments); 
	},
	getMyData: function(id){
		var me = this;
		var params = {
    		caller: "Voucher!Flow", 
    		condition: "vf_voucherid=" + id
    	};
		if(me.columns && me.columns.length > 0){
			me.GridUtil.loadNewStore(me, params);
		} else {
			me.GridUtil.getGridColumnsAndStore(me, 'common/singleGridPanel.action', params);
		}
	}
});