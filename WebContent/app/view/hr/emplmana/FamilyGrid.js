Ext.define('erp.view.hr.emplmana.FamilyGrid',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.familygrid',
	layout : 'fit',
	id: 'familygrid', 
 	emptyText : $I18N.common.grid.emptyText,
    columnLines : true,
    autoScroll : true,
    detno: 'af_detno',
    keyField: 'af_id',
    mainField: 'af_arid',
  //  necessaryField: 'vf_flowcode',
    columns: [],
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
    		caller: "Arfamily", 
    		condition: "af_arid=" + id
    	};
		if(me.columns && me.columns.length > 2){
			me.GridUtil.loadNewStore(me, params);
		} else {
			me.GridUtil.getGridColumnsAndStore(me, 'common/singleGridPanel.action', params);
		}
	}
});