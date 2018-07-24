Ext.define('erp.view.ma.RelativeSearchGrid',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.relativesearchgrid',
	layout : 'fit',
	id: 'relativesearchgrid', 
 	emptyText : $I18N.common.grid.emptyText,
    columnLines : true,
    autoScroll : true,
    detno: 'rsg_detno',
    keyField: 'rsg_id',
    mainField: 'rsg_rsid',
    columns: [],
    bodyStyle:'background-color:#f1f1f1;',
    plugins: Ext.create('Ext.grid.plugin.CellEditing', {
        clicksToEdit: 1
    }),
    bbar: {xtype: 'erpToolbar'},
    caller:'RelativeSearchGrid',
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
    		caller: "RelativeSearchGrid", 
    		condition: "rsg_rsid=" + id
    	}; 
		this.gridCondition="rsg_rsid=" + id;
		if(me.columns && me.columns.length > 2){
			me.GridUtil.loadNewStore(me, params);
		} else {
			me.GridUtil.getGridColumnsAndStore(me, 'common/singleGridPanel.action', params);
		}
	}
});