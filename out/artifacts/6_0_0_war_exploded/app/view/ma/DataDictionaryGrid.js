Ext.define('erp.view.ma.DataDictionaryGrid',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.dictionarygrid',
	layout : 'fit',
	id: 'grid', 
	emptyText : $I18N.common.grid.emptyText,
	columnLines : true,
	autoScroll : true, 
	dockedItems: [{
		xtype: 'toolbar',
		dock: 'top',
		items: [{
			xtype:'button',
			iconCls:'x-button-icon-add',
			itemId:'column_add',
			text:'添加'
		},{
			xtype:'button',
			iconCls:'x-button-icon-delete2',
			itemId:'column_delete',
			text:'删除'
		}]
	}],
	bodyStyle: 'background-color:#fafafa;',
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
		clicksToEdit: 1,
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
				renderer:function(val){
					if(val != null && val.toString().trim() != ''){
						return val;
					} else {
						return '<img src="' + basePath + 'resource/images/icon/need.png" title="必填字段">' + 
						'<span style="color:blue;padding-left:2px;" title="必填字段">' + val + '</span>';
					}
				},
				width:200,
				filter: {xtype:"textfield", filterName:"column_name"},
				editor:{
					xtype:'textfield',
					field:'column_name'
				}
			},{
				dataIndex:'data_type',
				cls: "x-grid-header-1",
				text:'数据类型',
				sortable:false,
				width:120,
				filter: {xtype:"textfield", filterName:"data_type"},
				renderer:function(val,meta,record){
					if(val){
						if(val=='VARCHAR2'){
							if(!record.get('data_length')){
								record.set('data_length',20);
							}
						}/*else if(val=='CLOB' ||  val=='DATE' || val=='TIMESTAMP'){
							if(record.get('data_length') && record.get('data_length')!=7) record.set('data_length',null);
						} */ 

					}	
					return val;
				},
				editor:{
					xtype:'combo',
					editable:false,
					queryMode: 'local',
					displayField: 'type',
					valueField: 'type',
					store:Ext.create('Ext.data.Store',{
						fields:['type'],
						data:[{type:'VARCHAR2'},{
							type:'NUMBER'
						},{
							type:'DATE'
						},{
							type:'CLOB'
						},{
							type:'TIMESTAMP'
						},{
							type:'FLOAT'
						}]
					})
				}
			},{
				dataIndex:'comments',
				cls: "x-grid-header-1",
				text:'注释',
				sortable:false,
				format:'',
				flex:1,
				filter: {xtype:"textfield", filterName:"comments"},
				editor:{
					xtype:'textfield',
					format:''
				},
				renderer:function(val){
					if(val != null && val.toString().trim() != ''){
						return val;
					} else {
						return '<img src="' + basePath + 'resource/images/icon/need.png" title="必填字段">' + 
						'<span style="color:blue;padding-left:2px;" title="必填字段">' + val + '</span>';
					}
				}
			},{
				text:'其他属性',
				columns:[{
					dataIndex:'data_length',
					cls: "x-grid-header-1",
					text:'字段长度',
					sortable:false,
					width:100,
					xtype:'numbercolumn',
					//format:0,
					editor:{
						xtype:'numberfield',
						hideTrigger:true,
						name:'data_length'
					},
					renderer:function(val,meta,record){
						if(record.get('data_type')=='CLOB' || record.get('data_type')=='BLOB') return null;
						if(val){
							if(record.get('data_type')=='DATE' && val==7 ) return null;  
						}	
						return val;
					}
				},{
					dataIndex:'data_precision',
					cls: "x-grid-header-1",
					text:'字段精度',
					sortable:false,
					width:100,
					editor:{
						xtype:'numberfield',
						hideTrigger:true
					}
				},{
					dataIndex:'nullable',
					cls: "x-grid-header-1",
					text:'允许为空',
					sortable:false,
					width:80,
					editor:{
						xtype:'combo',
						queryMode: 'local',
						displayField: 'type',
						valueField: 'type',
						editable:false,
						store:Ext.create('Ext.data.Store',{
							fields:['type'],
							data:[{type:'Y'},
							      {type:'N'}]
						})
					}
				},{
					dataIndex:'data_default',
					cls: "x-grid-header-1",
					text:'默认值',
					sortable:false,
					flex:100,
					editor:{
						xtype:'textfield'
					}
				}]
			}],
			store:Ext.create('Ext.data.Store',{
				fields:['column_name','data_type',{name:'data_length',type:'int',format:0},'data_precision','nullable','data_default',{name:'comments',type:'string',format:''}],
				proxy: {
					type: 'ajax',
					url: basePath+'/common/getDetail.action',
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
		})
		this.callParent(arguments);
	}
});