Ext.define('erp.view.hr.emplmana.RelationGrid',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.relationgrid',
	layout : 'fit',
	id: 'relationgrid', 
	caller: 'RelationGrid', 
 	emptyText : $I18N.common.grid.emptyText,
    columnLines : true,
    autoScroll : true,
    detno: 're_detno',
    keyField: 're_id',
    mainField: 're_emid',
    necessaryField: 're_code',
    columns: [],
    bodyStyle:'background-color:#f1f1f1;',
    plugins: Ext.create('Ext.grid.plugin.CellEditing', {
        clicksToEdit: 1
    }),
    bbar: {xtype: 'erpToolbar'},
    GridUtil: Ext.create('erp.util.GridUtil'),
	initComponent : function(){
		this.callParent(arguments); 
	},
	getMyData: function(id){
		if(!id){
			id = 0;
		}
		var me = this;
		var params = {
    		caller: "RelationGrid", 
    		condition: "re_emid=" + id
    	};
		if(me.columns && me.columns.length > 2){
			me.GridUtil.loadNewStore(me, params);
		} else {
			me.GridUtil.getGridColumnsAndStore(me, 'common/singleGridPanel.action', params);
		}
	}
});