Ext.define('erp.view.pm.bom.Maketypegrid',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.maketypegrid',
	layout : 'fit',
	id: 'maketypegrid', 
 	emptyText : $I18N.common.grid.emptyText,
    columnLines : true,
    autoScroll : true,
    detno: 'md_detno',
    keyField: 'md_id',
    mainField: 'md_blid',
    columns: [],
    bodyStyle:'background-color:#f1f1f1;',
    plugins: Ext.create('Ext.grid.plugin.CellEditing', {
        clicksToEdit: 1
    }),
    bbar: {xtype: 'erpToolbar'},
    caller:'Maketypedetail',
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
    		caller: "Maketypedetail", 
    		condition: "md_blid=" + id
    	};
		if(me.columns && me.columns.length > 2){
			me.GridUtil.loadNewStore(me, params);
		} else {
			me.GridUtil.getGridColumnsAndStore(me, 'common/singleGridPanel.action', params);
		}
	}
});