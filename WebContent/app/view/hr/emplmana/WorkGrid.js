Ext.define('erp.view.hr.emplmana.WorkGrid',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.workgrid',
	layout : 'fit',
	id: 'workgrid',
	caller: 'Arwork', 
 	emptyText : $I18N.common.grid.emptyText,
    columnLines : true,
    autoScroll : true,
    detno: 'aw_detno',
    keyField: 'aw_id',
    mainField: 'aw_arid',
    columns: [],
    bodyStyle:'background-color:#f1f1f1;',
    plugins: Ext.create('Ext.grid.plugin.CellEditing', {
        clicksToEdit: 1
    }),
    bbar: {xtype: 'erpToolbar'},
    GridUtil: Ext.create('erp.util.GridUtil'),
	initComponent : function(){
		this.callParent(arguments); 
		//this.getMyData();
	},
	getMyData: function(id){
		if(!id){
			id = 0;
		}
		var me = this;
		var params = {
    		caller: "Arwork", 
    		condition: "aw_arid=" + id
    	};
		if(me.columns && me.columns.length > 2){
			me.GridUtil.loadNewStore(me, params);
		} else {
			me.GridUtil.getGridColumnsAndStore(me, 'common/singleGridPanel.action', params);
		}
	}
});