Ext.define('erp.view.hr.emplmana.EmpsjobsGrid',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.empsjobsgrid',
	layout : 'fit',
	id: 'empsjobsgrid',
	caller: 'Empsjobs', 
 	emptyText : $I18N.common.grid.emptyText,
    columnLines : true,
    autoScroll : true,
    detno: null,
    keyField: 'jo_id',
    mainField: 'emp_id',
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
    		caller: "Empsjobs", 
    		condition: "emp_id=" + id
    	};
		if(me.columns && me.columns.length > 2){
			me.GridUtil.loadNewStore(me, params);
		} else {
			me.GridUtil.getGridColumnsAndStore(me, 'common/singleGridPanel.action', params);
		}
	}
});