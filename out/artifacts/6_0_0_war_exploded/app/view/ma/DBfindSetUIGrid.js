 Ext.define('erp.view.ma.DBfindSetUIGrid',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.DBfindSetUIGrid',
	id: 'DBfindSetUIGrid', 
	findData:[],
	region:'center',
	columnLines:true,
	store:Ext.create('Ext.data.Store',{
		fields:['ds_findtoui_f','ds_findtoui_i','ds_dbcaption',{name:'ds_dbwidth',type:'float'},{name:'ds_type',defaultValue:'S'}]
	}),
	necessaryFields:['ds_findtoui_f','ds_dbcaption'],//,'ds_findtoui_i'
	viewConfig: {
		plugins: {
			ptype: 'gridviewdragdrop',
			dragGroup: 'dbgrid',
			dropGroup: 'dbgrid'
		}	    					
	},
	emptyText : $I18N.common.grid.emptyText,
	bodyStyle: 'background-color:#f1f1f1;',
	plugins: Ext.create('Ext.grid.plugin.CellEditing', {
		clicksToEdit: 1
	}),
	listeners:{
		itemclick:function(selModel, record,e,index){			    
			var grid=selModel.ownerCt;
			if(index.toString() == 'NaN'){
				index = '';
			}
			if(index == grid.store.data.items.length-1){//如果选择了最后一行
				var items=grid.store.data.items;
				for(var i=0;i<10;i++){
					var o = new Object();
					grid.store.insert(items.length, o);
					items[items.length-1]['index'] = items.length-1;
				}
			}
		}	
	},
	columns: [{
		cls : "x-grid-header-1",
		text: '取值字段',
		xtype: 'combocolumn',
		dataIndex: 'ds_findtoui_f',
		renderer: function(val, meta, record){
			if(!val){
				val="";
			}
			return '<img src="' + basePath + 'resource/images/icon/need.png" title="必填字段">' + 
			'<span style="color:blue;padding-left:2px;" title="必填字段">' + val + '</span>';						  
		},
		flex: 1,
		format:"",
		editor: {
			format:'',
			xtype: 'combo',
			listConfig:{
				maxHeight:180
			},
			store: {
				fields: ['display', 'value'],
				data :[]
			},
			displayField: 'display',
			valueField: 'value',
			queryMode: 'local',
			onTriggerClick:function(trigger){
				var me=this;
				var DBfindSetUIGrid=Ext.getCmp("DBfindSetUIGrid");
				var findData=DBfindSetUIGrid.findData;
				this.getStore().loadData(findData);
				if (!me.readOnly && !me.disabled) {
					if (me.isExpanded) {
						me.collapse();
					} else {
						me.expand();
					}
					me.inputEl.focus();
				}    
			}
		}
	},{
		cls : "x-grid-header-1",
		text: '赋值字段',
		xtype: 'combocolumn',
		id:'combocolumn',
		dataIndex: 'ds_findtoui_i',
		flex: 1,
		renderer: function(val, meta, record){
			return val;
		},
		editor: {
			format:"",
			xtype: 'combo',
			id:'combo',
			listConfig:{
				maxHeight:180
			},
			store: {
				fields: ['display', 'value'],
				data:[{'display':'忽略','value':'ignore'}]
			},
			displayField: 'display',
			valueField: 'value',
			queryMode: 'local'
		}
	},{
		cls : "x-grid-header-1",
		text: '描述',
		dataIndex: 'ds_dbcaption',
		flex: 1,	
		renderer: function(val, meta, record){
			if(!val){
				val="";
			}
			return '<img src="' + basePath + 'resource/images/icon/need.png" title="必填字段">' + 
			'<span style="color:blue;padding-left:2px;" title="必填字段">' + val + '</span>';						  
		},
		editor:{
			xtype:'textfield',
			format:""						
		}
	},{
		cls : "x-grid-header-1",
		text: '列宽',
		dataIndex: 'ds_dbwidth',
		//format: '0',
		flex:1,
		editor: {
			xtype: 'textfield'
				///format: '0'
		}
	},{
		cls : "x-grid-header-1",
		text: '类型',
		dataIndex: 'ds_type',
		flex: 1,	
		editor:{
			xtype:'combo',
			editable : false,
			store: {
				fields:['display','value'],
				data:[{'display':'字符串','value':'S'},
					{'display':'数字','value':'N'},
					{'display':'日期','value':'D'},
					{'display':'时间','value':'DT'},
					{'display':'是否(-1,0)','value':'YN'},
					{'display':'下拉框','value':'C'}]},
			queryMode: 'local',
			displayField: 'display',
			valueField: 'value'
		}
	},{
		xtype:'actioncolumn',
		cls : "x-grid-header-1",
		header:'操作',
		//width:50,
		align:'center',
		items: [{
            icon : basePath+'resource/images/delete.png',
            handler: function(grid, rowIndex, colIndex) {
			    grid.getStore().removeAt(rowIndex);
            }
        }],
		flex:0.3
	}],
	initComponent : function(){ 
		me=this;
		this.callParent(arguments);		
	},
	
});