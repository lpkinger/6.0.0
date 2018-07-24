Ext.define('erp.view.pm.make.StepioMakeScrapGrid',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.stepiomakescrapgrid',
	layout : 'fit',
	id: 'stepiomakescrapgrid',
	caller: 'MakeScrap', 
 	emptyText : $I18N.common.grid.emptyText,
    columnLines : true,
    autoScroll : true,
    detno: 'md_detno ',
	keyField: 'md_id',
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
    		caller: "MakeScrap", 
    		condition: "md_msid=" + id
    	};
		if(me.columns && me.columns.length > 2){
			me.GridUtil.loadNewStore(me, params);
		} else {
			me.GridUtil.getGridColumnsAndStore(me, 'common/singleGridPanel.action', params);
		}
	}
});