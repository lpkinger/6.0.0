Ext.define('erp.view.pm.bom.Billtypegrid',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.billtypegrid',
	layout : 'fit',
	id: 'billtypegrid', 
 	emptyText : $I18N.common.grid.emptyText,
    columnLines : true,
    autoScroll : true,
    detno: 'bd_detno',
    keyField: 'bd_id',
    mainField: 'bd_blid',
    columns: [],
    bodyStyle:'background-color:#f1f1f1;',
    plugins: Ext.create('Ext.grid.plugin.CellEditing', {
        clicksToEdit: 1
    }),
    bbar: {xtype: 'erpToolbar'},
    caller:'Billtypedetail',
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
	initComponent : function(){
		this.callParent(arguments); 
		this.getMyData();
	},
	getMyData: function(id){
		if(!id){
			id = 0;
		}
		var me = this,
		condition=getUrlParam('formCondition');
		if(condition!='' && condition!=null) id=condition.split('IS')[1];
		var params = {
    		caller: "Billtypedetail", 
    		condition: "bd_blid=" + id
    	}; 
		if(me.columns && me.columns.length > 2){
			me.GridUtil.loadNewStore(me, params);
		} else {
			me.GridUtil.getGridColumnsAndStore(me, 'common/singleGridPanel.action', params);
		}
	}
});