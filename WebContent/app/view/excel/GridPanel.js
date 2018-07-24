Ext.define('erp.view.excel.GridPanel',{ 
	extend: 'Ext.grid.Panel', 
	id:'excelGrid',
	alias: 'widget.excelGridPanel',
 	emptyText : '无数据',
    columnLines : true,
    autoScroll : true,
    store:Ext.create('Ext.data.Store', {
    	storeId : 'excelStore',
    	pageSize : 10,
	    fields:['FILEID','FILENAME','FILEDESC','FILECOLOR','FILECREATETIME',
	    		'FILEUPDATETIME','FILEMAN','FILESTATUS',
	    		'FILESTATUSCODE','FILEVERSION','FILEUSE','FILETPLSOURCE','FILEVERSIONSOURCE'],
	    proxy:{
			type:'ajax',
//			async:false, 
			url:basePath + 'Excel/file/getExcelsByTplsource.action',
			reader:{
				type : 'json',
				root : 'data',
				totalProperty : 'totalCount'
			},
			writer:{
				type:'json'
			}
		},
		autoLoad:false
	}),
	columns:[
		{header: "名称",
		 width:200,
		 sortable: false,
		 dataIndex: 'FILENAME',
		 items:[{
		 	id:'FILENAME',
		 	width:200,
		 	enableKeyEvents: true,
		 	xtype:"textfield"
		 }],
		 field:{
		 	type:'textfield'
		 }		
		 },
		{header: "创建时间",
		width:150,
		sortable: false,
		dataIndex: 'FILECREATETIME',
		items:[{
			id:'FILECREATETIME',
			width:150,
		 	xtype:"datefield"
		 }],
		field:{
		 	xtype:'textfield'
		 }	
		},
		{header: "创建人", 
		width:100,
		sortable: false,
		dataIndex: 'FILEMAN',
		items:[{
			id:'FILEMAN',
			enableKeyEvents: true,
		 	xtype:"textfield",
		 	value:em_name
		 }],
		field:{
		 	xtype:'textfield'
		 }
		},
		{header: "状态",
		width:100,
		sortable: false,
		dataIndex: 'FILESTATUS',
		items:[{
			id:'FILESTATUS',
			enableKeyEvents: true,
		 	xtype:"textfield"
		 }],
		field:{
		 	xtype:'textfield'
		 }
		},
		{header: "版本号",
		width:100, 
		sortable: false,
		dataIndex: 'FILEVERSION',
		items:[{
			id:'FILEVERSION',
			enableKeyEvents: true,
		 	xtype:"textfield"
		 }],
		field:{
		 	xtype:'textfield'
		 }
		},
		{header: "是否启用",
		width:70, 
		sortable: false,
		dataIndex: 'FILEUSE',
		items:[{
			id:'FILEUSE',
			width:70, 
			enableKeyEvents: true,
			xtype:'combobox',
            store:Ext.create('Ext.data.Store',{
            	fields:['name','value'],
            	data:[{
            		"name":'是',"value":1
            	},{
            		"name":'否',"value":0
            	}]
            	
            }),
            displayField: 'name',
            valueField: 'value'
		 }],
		field:{
		 	xtype:'textfield'
		 }
		}
	],
	tbar:[
		{xtype:'button',text:'删除',id:'delete'}
	],
	dockedItems:[{
		xtype:'pagingtoolbar',
		id:'pagetool',
		store:Ext.data.StoreManager.lookup('excelStore'),
		dock:'bottom',
		emptyMsg:'没有数据',
		displayInfo:true
	}],
	initComponent : function(){
		this.callParent();
	},
	listeners:{
		afterrender:function(){
			this.reconfigure(null, this.columns);
		}
	}
});
