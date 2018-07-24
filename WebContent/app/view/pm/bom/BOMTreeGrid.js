Ext.define('erp.view.pm.bom.BOMTreeGrid',{ 
	extend: 'Ext.tree.Panel', 
	alias: 'widget.bomTreeGrid',
	region: 'south',
	layout : 'fit',
	id: 'querygrid',
	cls: 'custom',
 	emptyText : $I18N.common.grid.emptyText,
    useArrows: true,
    rootVisible: false,
	bodyStyle:'background-color:#f1f1f1;',
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	initComponent : function(){ 
		Ext.override(Ext.data.AbstractStore,{
			indexOf: Ext.emptyFn
		});
		//var gridParam = {caller: caller, condition: this.emptyCondition};
		var gridParam = {caller: caller, condition:null};
		//this.beforeQuery(caller, this.emptyCondition);
		this.getGridColumnsAndStore(this, gridParam);
		this.callParent(arguments);
		this.GridUtil = new Object();
		this.GridUtil.getGridColumnsAndStore = this.getGridColumnsAndStore;
		this.GridUtil.loadNewStore = this.getGridColumnsAndStore;
	},
	listeners: {//滚动条有时候没反应，添加此监听器
		scrollershow: function(scroller) {
			if (scroller && scroller.scrollEl) {
				scroller.clearManagedListeners();  
				scroller.mon(scroller.scrollEl, 'scroll', scroller.onElScroll, scroller);  
			}
		}
	},
	defaultCondition: "bs_sonbomid > 0 and bs_level = '0'", 
	emptyCondition: '1=2',
	columns: new Array(),
	getGridColumnsAndStore: function(grid, param){
		var me = Ext.getCmp('querygrid') || this;
		parent.Ext.getCmp("content-panel").getActiveTab().setLoading(true);
		Ext.Ajax.request({
        	url : basePath + 'common/singleGridPanel.action',
        	params: param,
        	method : 'post',
        	async: false,
        	callback : function(options, success, response){
        		parent.Ext.getCmp("content-panel").getActiveTab().setLoading(false);
        		var res = new Ext.decode(response.responseText);
        		if(res.exceptionInfo){
        			showError(res.exceptionInfo);
        		} else {
        			if(me.columns.length == 0){
        				Ext.each(res.columns, function(c){
            				c = me.removeKeys(c, ['locked', 'summaryType', 'logic', 'renderer']);
            				me.columns.push(c);
            			});
        			}
        			if(!me.store){
        				me.store = Ext.create('Ext.data.TreeStore', {
            				fields: res.fields,
            		    	root : {
            		        	text: 'root',
            		        	id: 'root',
            		    		expanded: true
            		    	}
            			});
        			}
        			if(res.data){
            			var tree = Ext.decode(res.data.replace(/,}/g, '}').replace(/,]/g, ']'));
            			me.store.setRootNode({
                    		text: 'root',
                    	    id: 'root',
                    		expanded: true,
                    		children: me.parseGrid2Tree(tree)
                    	});
            			var root = me.store.getRootNode();
            			root.expand();
            	        root.eachChild(function(node) {
            	        	if(node.data['bs_level']==0){
            	        		node.expand();
            	        		Ext.getCmp('querygrid').loadChildNodes(node);
            	        	}
            	        });
        			}
        		}
        	}
		});
	},
	removeKeys: function(obj, keys){
		var o = new Object();
		var key = Ext.Object.getKeys(obj);
		Ext.each(key, function(k){
			if(!Ext.Array.contains(keys, k)){
				o[k] = obj[k];
			}
		});
		return o;
	},
	/**
	 * 加载子节点
	 */
	loadChildNodes: function(record){
		var me = this;
		parent.Ext.getCmp("content-panel").getActiveTab().setLoading(true);
		Ext.Ajax.request({
        	url : basePath + 'common/singleGridPanel.action',
        	params: {
        		caller: caller,
        		condition: 'bs_bomid=' + record.data['bs_sonbomid'] + ' AND bs_topbomid=' + record.data['bs_topbomid'] +
        			' AND bs_level=\'' + me.getChildLevel(record) + '\'' 
        	},
        	method : 'post',
        	async: false,
        	callback : function(options,success,response){
        		parent.Ext.getCmp("content-panel").getActiveTab().setLoading(false);
        		var res = new Ext.decode(response.responseText);
        		if(res.exceptionInfo){
        			showError(res.exceptionInfo);
        		} else {
        			if(res.data){
            			var tree = Ext.decode(res.data.replace(/,}/g, '}').replace(/,]/g, ']'));
            			var nodes = me.parseGrid2Tree(tree);
            			record.appendChild(nodes);
            			record.expand(false,true);
        			}
        		}
        	}
		});
	},
	getChildLevel: function(record) {
		var lev = Number(record.data['bs_level'].substr(record.data['bs_level'].lastIndexOf('.') + 1)),
			s = '';
		for(var i = 0;i <= lev;i ++) {
			s += '.';
		}
		s += (lev + 1);
		return s;
	},
	/**
	 * 把筛选出来的BOMDetail的数据转化成Tree格式数据
	 * @param details {Array} BOMDetails
	 * @return TreeNodes
	 */
	parseGrid2Tree: function(data){
		var tree = new Array();
		Ext.each(data, function(d, index){
			if(d.bs_sonbomid > 0) {
				d.cls = 'x-tree-cls-parent';
				d.leaf = false;
			} else {
				d.leaf = true;
				d.cls = 'x-tree-cls-node';
			}
			tree.push(d);
		});
		return tree;
	},
	beforeQuery: function(call, cond) {
		Ext.Ajax.request({
			url: basePath + 'common/form/beforeQuery.action',
			params: {
				caller: call,
				condition: cond
			},
			async: false,
			callback: function(opt, s, r) {
				var rs = Ext.decode(r.responseText);
				if(rs.exceptionInfo) {
					showError(rs.exceptionInfo);
				}
			}
		});
	}
});