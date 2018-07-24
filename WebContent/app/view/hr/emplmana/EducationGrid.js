Ext.define('erp.view.hr.emplmana.EducationGrid',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.educationgrid',
	layout : 'fit',
	id: 'educationgrid',
	caller: 'Areducation', 
 	emptyText : $I18N.common.grid.emptyText,
    columnLines : true,
    autoScroll : true,
    detno: 'ad_detno',
    keyField: 'ad_id',
    mainField: 'ad_arid',
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
    		caller: "Areducation", 
    		condition: "ad_arid=" + id
    	};
		if(me.columns && me.columns.length > 2){
			me.GridUtil.loadNewStore(me, params);
		} else {
			me.GridUtil.getGridColumnsAndStore(me, 'common/singleGridPanel.action', params);
		}
	}
});