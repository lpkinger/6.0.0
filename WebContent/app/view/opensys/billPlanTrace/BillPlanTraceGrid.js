Ext.define('erp.view.opensys.billPlanTrace.BillPlanTraceGrid',{ 
	extend: 'Ext.tree.Panel', 
	alias: 'widget.BillPlanTraceGrid',
	region: 'south',
	layout : 'fit',
	id: 'querygrid',
	cls: 'custom',
 	emptyText : $I18N.common.grid.emptyText,
    useArrows: true,
    rootVisible: false,
	bodyStyle:'background-color:#f1f1f1;',
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	constructor: function(cfg) {
		if(cfg) {
			cfg.plugins = cfg.plugins || [ Ext.create('erp.view.core.plugin.CopyPasteMenu')];
	    	Ext.apply(this, cfg);
		}
		this.callParent(arguments);
	},
	initComponent : function(){ 
		Ext.override(Ext.data.AbstractStore,{
			indexOf: Ext.emptyFn
		});
//		var gridParam = {caller: caller, condition: this.emptyCondition+' and '+condition};
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
	defaultCondition: "isleaf=0", 
	emptyCondition: '1=2',
	columns: new Array(),
	getGridColumnsAndStore: function(grid, param){
		var me = Ext.getCmp('querygrid') || this;
		/*parent.Ext.getCmp("content-panel").getActiveTab().setLoading(true);*/
		Ext.Ajax.request({
        	url : basePath + 'common/singleGridPanel.action',
        	params: param,
        	method : 'post',
        	async: false,
        	callback : function(options, success, response){
        		/*parent.Ext.getCmp("content-panel").getActiveTab().setLoading(false);*/
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
        			if(res.data&&Ext.getCmp('sa_pocode')&&Ext.getCmp('sa_code').value){
            			var tree = Ext.decode(res.data.replace(/,}/g, '}').replace(/,]/g, ']'));
            			me.store.setRootNode({
                    		text: 'root',
                    	    collapsed:true,
                    		children: me.parseGrid2Tree(tree)
                    	});
            			me.collapseAll();
//            			var root = me.store.getRootNode();
//            	        root.eachChild(function(node) {
//            	        	if(node.data['bs_level']==0){
//            	        		node.expand();
//            	        		Ext.getCmp('querygrid').loadChildNodes(node);
//            	        	}
//            	        });
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
		Ext.Ajax.request({
        	url : basePath + 'common/singleGridPanel.action',
        	params: {
        		caller: 'BillPlanTraceExpand',
        		condition: 'sa_code=\'' + record.data['sa_code'] + '\' AND sd_detno=' + record.data['sd_detno'] + ' and ' +
        				' p_ma_code = \'' + record.data['ma_code'] + '\' and ma_id is not null' 
        	},
        	method : 'post',
        	async: false,
        	callback : function(options,success,response){
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
	parseGrid2Tree: function(data){
		var tree = new Array();
		Ext.each(data, function(d, index){
			if(d.isleaf == '0') {
				d.cls = 'x-tree-cls-root';
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