Ext.define('erp.view.ma.sql.Grid', {
    extend: 'Ext.grid.Panel',
    xtype: 'result-grid',
    id:'result-grid',
    resizable: false,
    loadMask: true,
    columnLines:true,
    frame:true,
    syncRowHeight: false,
    viewConfig:{
    	 stripeRows:false
    },
    selModel: {
        type: 'spreadsheet'
    },
    columns: [],
    requires:['Ext.grid.plugin.Exporter'],
    plugins:['gridexporter','clipboard'],
    bbar: {
        xtype: 'pagingtoolbar',
        displayInfo: true,
        pageSize:20,
        displayMsg: '显示 {0} - {1} 共 {2}条',
        emptyMsg: "无记录"  
    }
});