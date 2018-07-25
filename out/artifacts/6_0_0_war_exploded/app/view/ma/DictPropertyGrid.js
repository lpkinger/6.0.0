Ext.define('erp.view.ma.DictPropertyGrid',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.dictpropertygrid',
	layout : 'fit',
	id: 'propertygrid', 
	emptyText : $I18N.common.grid.emptyText,
	columnLines : true,
	autoScroll : true, 
	bodyStyle: 'background-color:#f1f1f1;',
	plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
		clicksToEdit: 1,
		listeners:{
			beforeedit:function(e){
				var g=e.grid,r=e.record,f=e.field;
				if(g.binds){
					var bool=true;
					Ext.Array.each(g.binds,function(item){
						if(Ext.Array.contains(item.fields,f)){
							Ext.each(item.refFields,function(field){
								if(r.get(field)!=null && r.get(field)!=0 && r.get(field)!='' && r.get(field)!='0'){
									bool=false;
								} 
							});							
						} 
					});
					return bool;
				}
			}
		}

	}), Ext.create('erp.view.core.plugin.CopyPasteMenu')],
	GridUtil: Ext.create('erp.util.GridUtil'),
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	dbfinds: [],
	caller: null,
	condition: null,
	gridCondition:null,
	columnLines:true,
	plugins: [Ext.create('erp.view.core.grid.HeaderFilter'),Ext.create('Ext.grid.plugin.CellEditing', {
		clicksToEdit: 1
	})],
	necessaryFields:['column_name','data_type'],
	initComponent : function(){
		var me=this;
		Ext.apply(me,{
			columns:[{
				dataIndex:'column_name',
				cls: "x-grid-header-1",
				text:'列名',
				sortable:false,
				width:150,
				filter: {xtype:"textfield", filterName:"column_name"}
			},{
				dataIndex:'data_type',
				cls: "x-grid-header-1",
				text:'数据类型',
				sortable:false,
				logic : 'ignore',
				width:100,
				filter: {xtype:"textfield", filterName:"data_type"}
			},{
				dataIndex:'comments',
				cls: "x-grid-header-1",
				text:'注释',
				logic : 'ignore',
				sortable:false,
				format:'',
				flex:1,
				filter: {xtype:"textfield", filterName:"comments"}
			},{
				dataIndex:'allowbatchupdate_',
				cls: "x-grid-header-1",
				text:'允许批量更新',
				xtype: 'checkcolumn',
				sortable:false,
				format:'',
				width:110,
				filter: {xtype:"textfield", filterName:"allowbatchupdate_"},
				editor: {
        			xtype: 'checkbox',
        			cls: "x-grid-checkheader-editor"
        		}
			}],
			store:Ext.create('Ext.data.Store',{
				fields:['column_name','data_type','allowbatchupdate_',{name:'comments',type:'string',format:''}],
				proxy: {
					type: 'ajax',
					url: basePath+'/common/getProperty.action',
					extraParams :{
						tablename:tablename
					},
					reader: {
						type: 'json',
						root: 'list',
						idProperty:'column_name'
					}
				}, 
				autoLoad: true       
			})     
		});
		this.callParent(arguments);
	}
});