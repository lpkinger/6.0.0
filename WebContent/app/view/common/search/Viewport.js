Ext.define('erp.view.common.search.Viewport', {
	extend : 'Ext.Viewport',
	layout : 'anchor',
	hideBorders : true,
	initComponent : function() {
		var me = this;
		me.callParent(arguments);
		me.add(me.createGrid([], []));
	},
	getColumns: function(fn) {
		var me = this;
		Ext.Ajax.request({
        	url : basePath + 'common/singleGridPanel.action',
        	params: {
        		caller: caller,
        		condition: ''
        	},
        	method : 'post',
        	callback : function(opt, s, r){
        		var res = new Ext.decode(r.responseText);
        		if(res.exceptionInfo){
        			showError(res.exceptionInfo);return;
        		}
        		if(res.columns){
        			fn.call(me, res.columns, res.fields);
        		}
        	}
		});
	},
	createGrid: function(columns, fields) {
		var me=this;
		Ext.define('Temp', {
		    extend: 'Ext.data.Model',
		    fields: fields
		});
		var store = Ext.create('Ext.data.Store', {
			buffered: true,
			model: 'Temp'
	    });
		return Ext.create('Ext.grid.Panel', {
			anchor: '100% 100%',
			id : 'querygrid',
			cls : 'default-grid',
			maxDataSize: 30000,
			loadMask: true,
	        plugins: 'bufferedrenderer',
	        plugins : [Ext.create('erp.view.core.plugin.CopyPasteMenu')],
			BaseUtil: Ext.create('erp.util.BaseUtil'),
			RenderUtil: Ext.create('erp.util.RenderUtil'),
		  	features : [{
		  		id: 'group',
		  		ftype: 'groupingsummary',
	            showSummaryRow: true
		    },{
		    	ftype: 'summary',
		    	dock: 'bottom'
		    }],
		    enableLocking : true,
		    lockable : true,
			columns: columns,
			store: store,
			columnLines: true,
			tbar: {padding:'0 0 5 0',defaults:{cls:'x-btn-gray',margin:'0 10 0 0'},items:[{
				text: '筛选', iconCls: 'icon-find', name: 'find'
			},{
		        text: '排序', iconCls: 'icon-sort', name: 'sort'
		    },{
		        text: '导出',
		        iconCls: 'icon-xls',
		        menu: [{text: '导出Excel (.xls)', iconCls: 'icon-xls', name: 'exportexcel'}, {text: '导出PDF (.pdf)', iconCls: 'icon-pdf', name: 'exportpdf'}]
		    },{
		        text: '锁定列',
		        iconCls: 'icon-fixed',
		        name: 'lock'
		    },{
		        text: '分组',
		        iconCls: 'icon-group',
		        name: 'group'
		    },{
		        iconCls: 'icon-maximize',
		        text: '切换',
		        menu: [{text: '最大化', name: 'max', iconCls: 'icon-maximize'},{text: 'Web Excel', name: 'webexcel', iconCls: 'icon-amp'}]
		    },{
		    	text: '清除格式', iconCls: 'icon-remove', name: 'removeformat'
		    },{
		    	text: '清除数据', iconCls: 'icon-clear', name: 'clearall'
		    },{
		    	cls:null,
		    	xtype: 'tbtext',
		    	id: 'dataCount',
		    	hidden: true,
		    	tpl: Ext.create('Ext.XTemplate','为您找到 <strong>{count}</strong> 条数据')
		    },{ xtype: 'tbfill',cls:null},{
		    	text: '关闭', name: 'close', iconCls: 'icon-del',margin:0
		    }]}
		});
	}
});