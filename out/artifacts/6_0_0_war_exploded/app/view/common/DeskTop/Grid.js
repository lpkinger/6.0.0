/**
 * ERP项目gridpanel通用样式2
 */
Ext.define('erp.view.common.DeskTop.Grid',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.erpDesktopGrid',
	layout : 'fit',
	autoScroll : false, 
	store: [],
	columns: [],
	pageCount:10,
	GridUtil: Ext.create('erp.util.GridUtil'),
	data:null,
	initComponent : function(){
		var grid = this;
		var data = this.data;
		this.title = data.title,
		
		this.callParent(arguments);
		
		if(!this.boxready) {
			var reg =new RegExp("^yncolumn-{1}\\d{0,1}$");
			Ext.Array.each(data.columns, function(column, y){
				if(column.xtype=='textareatrigger'){
					column.xtype='';
					column.renderer='texttrigger';
				}
				//yncoloumn支持配置默认是/否
				if(column.xtype &&reg.test(column.xtype)&&(column.xtype.substring(8)==-1||column.xtype.substring(8)==-0)){
					Ext.each(data.fields, function(field, y){
	    				if(field.type=='yn' && column.dataIndex==field.name){
	    					field.defaultValue=0-column.xtype.substring(9);
	    				}
					});
					column.xtype='yncolumn';
				}
				// column有取别名
				if(column.dataIndex.indexOf(' ') > -1) {
					column.dataIndex = column.dataIndex.split(' ')[1];
				}
				//renderer
				grid.GridUtil.setRenderer(grid, column);
			});
			var store = grid.getQueryStore(data.fields,data.condition,data.caller);
			grid.reconfigure(store, data.columns);
		}
	},
	viewConfig :{
		stripeRows:false,
		trackOver: false
	},
	listeners:{
		activate:function(grid){
			grid.getStore().load();
		}
	},
	getQueryStore:function(fields,condition,caller,autoLoad){
		var me=this;
		condition = parseUrl(condition);
		return Ext.create('Ext.data.Store',{
			fields:fields,
			proxy: {
				type: 'ajax',
				url : basePath + 'common/desktop/data.action',
				method : 'POST',
				extraParams:{
					caller: caller,
					condition:condition,
					pageSize:me.pageCount,
					_noc:1
				},
				actionMethods:{
					    read: 'POST'
					   },
				reader: {
					type: 'json',
					root: 'data'
				}
			}, 
			autoLoad:false  
		});	
	}
});