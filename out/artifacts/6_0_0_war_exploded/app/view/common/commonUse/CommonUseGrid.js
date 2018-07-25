/**
 * 常用项设置页面
 */
Ext.define('erp.view.common.commonUse.CommonUseGrid',{
	extend: 'Ext.grid.Panel', 
	alias: 'widget.commonusegrid',
	id: 'commonusegrid',
	requires: ['erp.view.core.toolbar.Toolbar'],
	region: 'center',
	layout : 'fit',
	emptyText : '无数据',
	padding: 5,
	tbar:{
		xtype:'toolbar',
		items:[{
			width:215,
	        xtype: 'searchfield',
	        cls: 'search-field',
	        emptyText:'搜索',
	        onTriggerClick: function(){
	        	var f = this;
	        	var store = Ext.getCmp('commonusegrid').store;
				if(f.value == '' || f.value == null){
					store.clearFilter();
				}else{
					store.filterBy(function(record) { 
	                      return contains(record.data['cu_description'],f.value,true);   
	                });
				}
	        }
		}]			
	},
	columnLines : true,
	autoScroll : true, 
	store: [],	
	columns:[{
		text:'名称',
		width: 200,
		dataIndex:'cu_description',
		renderer: function(val, m, record, x, y, store, view) {
			var grid = view.ownerCt, me = grid.RenderUtil,column = grid.columns[y], url = record.get('cu_url');
			if(url) {
				val = "<a href=\"javascript:openTable('"+val+"','"+url.replace(/\'/g, '\\\'')+"')\" style=\'float:left;\'>"+val+"<img alt=\"\" src=\"\" class=\"x-commuse-addicon \"></a>";
			}
			return val;
		 }
	}, { 
		xtype: 'actioncolumn', 
		align: 'center', 
		dataIndex:'cu_lock',
		header: '操作',
		width: 120,
		items: [{
		    name: 'lockCommonuse',
		    getClass: function (v, metadata, r, rowIndex, colIndex, store) {
		    	return r.data['cu_lock'] ? 'x-commonuse-locked' : 'x-commonuse-unlocked';
		    },
		    getTip: function (v, metadata, r, rowIndex, colIndex, store) {
		    	return r.data['cu_lock'] ? '取消关注' : '关注';
		    },
		    handler: function(grid, rowIdx, colIdx) {
		    	var record = grid.getStore().getAt(rowIdx);
				grid.el.mask('loading...');
				Ext.Ajax.request({
					url : basePath + 'common/lockCommonUse.action',
					params : {
						_noc : 1,
						id : record.get('cu_id'),
						type : Number(!record.get('cu_lock')),
						count:999
					},
					callback : function(o, s, r) {
						grid.el.unmask();
						var rs = Ext.decode(r.responseText);
						if (rs.commonuse) {
							grid.store.loadData(rs.commonuse);
						}
					}
				});
	        }
	    }, '-', {
	        iconCls: 'x-commonuse-delete',
	        tooltip: '删除',
	        handler: function(grid, rowIdx, colIdx) {
	        	var record = grid.getStore().getAt(rowIdx);
				grid.el.mask('loading...');
				Ext.Ajax.request({
					url : basePath + 'common/deleteCommonUse.action',
					params: {
						id: record.get('cu_id')
					},
					method : 'post',
					callback : function(options,success,response){
						grid.el.unmask();
						grid.getStore().removeAt(rowIdx);
					}
				});
	        }
    	}] 
    }, {
    	flex: 1
    }],
	bodyStyle: 'background-color:#f1f1f1;',
	GridUtil: Ext.create('erp.util.GridUtil'),
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	dbfinds: [],
	caller: null,
	condition: null,
	gridCondition:null,
	initComponent : function(){
		var me = this;				
		Ext.apply(this,{
			store:me.getStore()	
		});
		this.callParent(arguments);
	},
	getStore:function(){
		var me=this;
		return Ext.create('Ext.data.Store',{
			fields: ['cu_id','cu_description', 'cu_url', 'cu_lock'],
			proxy: {
				type: 'ajax',
				url : basePath + 'common/getCommonUse.action',
				method : 'GET',
				extraParams:{
					count:999
				},
				reader: {
					type: 'json',
					root: 'commonuse'
				}
			}, 
			autoLoad:true  
		});
	}
});