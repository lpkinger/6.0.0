Ext.define('erp.view.sys.sale.CurrencyGrid',{    
	extend: 'Ext.grid.Panel', 
	alias: 'widget.currencygrid',
	id:'currencygrid',
	autoHeight:true,
	background: '#ffffff', 
	columnLines: true,
	bodyStyle : 'background:#FFFFFF;',
	plugins: Ext.create('Ext.grid.plugin.CellEditing', {
        clicksToEdit: 1
    }),
	viewConfig: {
        stripeRows: true,
        enableTextSelection: true//允许选中文字
    },
	//frame: true,
	margin:'0 0 0 40',
	columns: [
	           { text: '外币', dataIndex: 'CR_NAME',menuDisabled:true,width:140},
	           { text: '汇率(本位币)',  dataIndex: 'CR_RATE',menuDisabled:true,width:141,
	        	   editor:{
						xtype:'numberfield',
						field:'CR_RATE',
						decimalPrecision: 4,
						allowBlank:false,
						hideTrigger:true,
	        	   }
	           }
	       ],
	    store:Ext.create('Ext.data.Store',{
				fields:['CR_NAME','CR_RATE'],
	       }),
	initComponent : function(){
		this.callParent(arguments);
	},
    listeners:{
 	   afterrender:function(panel){
			panel.store.loadData([{},{},{},{},{},{}]);  
    }}
})