Ext.define('erp.view.hr.emplmana.TrainingGrid',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.traininggrid',
	layout : 'fit',
	id: 'traininggrid', 
 	emptyText : $I18N.common.grid.emptyText,
    columnLines : true,
    autoScroll : true,
    detno: 'at_detno',
    keyField: 'at_id',
    mainField: 'at_emid',
    caller: "Artraining",
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
    		caller: "Artraining", 
    		condition: "at_emid=" + id
    	};
		if(me.columns && me.columns.length > 2){
			me.GridUtil.loadNewStore(me, params);
		} else {
			me.GridUtil.getGridColumnsAndStore(me, 'common/singleGridPanel.action', params);
		}
	}
});