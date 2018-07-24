Ext.define('erp.view.pm.bom.BOMTreeGrid',{ 
	extend: 'Ext.tree.Panel', 
	alias: 'widget.bomTreeGrid',
	region: 'south',
	layout : 'fit',
	id: 'querygrid', 
 	emptyText : $I18N.common.grid.emptyText,
    useArrows: true,
    rootVisible: false,
	bodyStyle:'background-color:#f1f1f1;',
	initComponent : function(){ 
		Ext.override(Ext.data.AbstractStore,{
			indexOf: Ext.emptyFn
		});
		var gridParam = {caller: caller, condition: ''};
		this.getGridColumnsAndStore(this, 'common/singleGridPanel.action', gridParam, "");
		this.callParent(arguments);
		this.GridUtil = new Object();
		this.GridUtil.getGridColumnsAndStore = this.getGridColumnsAndStore;
	},
	listeners: {//滚动条有时候没反应，添加此监听器
		scrollershow: function(scroller) {
			if (scroller && scroller.scrollEl) {
				scroller.clearManagedListeners();  
				scroller.mon(scroller.scrollEl, 'scroll', scroller.onElScroll, scroller);  
			}
		}
	},
	columns: new Array(),
	getGridColumnsAndStore: function(grid, url, param, no){
		var me = Ext.getCmp('querygrid') || this;
		parent.Ext.getCmp("content-panel").getActiveTab().setLoading(true);
		Ext.Ajax.request({
        	url : basePath + url,
        	params: param,
        	method : 'post',
        	async: false,
        	callback : function(options,success,response){
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
        		condition: 'bd_motherid=' + record.data['bd_sonbomid'] 
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
            			var nodes = me.parseGrid2Tree(tree)[0].children;
            			Ext.each(nodes, function(n){
            				n.bs_node = Number(record.data['bs_node']) + 1;
            			});
            			record.appendChild(nodes);
            			record.expand(false,true);
        			}
        		}
        	}
		});
	},
	/**
	 * 把筛选出来的BOMDetail的数据转化成Tree格式数据
	 * @param details {Array} BOMDetails
	 * @return TreeNodes
	 */
	parseGrid2Tree: function(details){
		var tree = new Array();
		var me = this;
		Ext.each(details, function(node, index){
			if(me.isParentCode(details, node.bd_mothercode)){
				var n = me.getParentNode(tree, node);
				n.children = n.children || new Array();
				n.children.push(me.getTreeNode(details, node, node.bd_mothercode, index, 2));
			}
		});
		return tree;
	},
	/**
	 * 将BOMDetail及其下级BOM解析成Tree格式
	 * @param details {Array} BOMDetails
	 * @param node BOM节点
	 * @param pId parentId
	 * @param index 节点的id由prodcode而来，加pId和index主要为了防止id重复
	 * @param tier 层级
	 */
	getTreeNode: function(details, node, pId, index, tier){
		var me = this;
		node.id = node.bd_soncode + index;
		node.parentId = pId;
		node.qtip = node.bd_soncode;
		node.bs_node = tier;
		var array = me.getNodeChilds(details, node);
		if(array.length > 0){
			node.cls = "x-tree-cls-parent";
			node.leaf = false;
			node.children = node.children || new Array();
			Ext.each(array, function(a, idx){
				node.children.push(me.getTreeNode(details, a, node.bd_soncode + index, idx, tier + 1));
			});
		} else {
			if(node.bd_sonbomid > 0 ){
				node.cls = "x-tree-cls-parent";
				node.leaf = false;
			} else {
				node.leaf = true;
				node.cls = "x-tree-cls-node";
			}
		}
		return node;
	},
	/**
	 * @param details {Array} BOMDetails
	 * @param node BOM节点
	 * @return node的下级BOM
	 */
	getNodeChilds: function(details, node){
		var array = new Array();
		Ext.each(details, function(d){
			if(d.bd_bomid == node.bd_sonbomid){//根据ID
				array.push(d);
			}
		});
		return array;
	},
	/**
	 * 判断code是否是最上层的BOM
	 * @param details {Array} BOMDetails
	 * @param code bd_mothercode
	 * @return true-是/false-否
	 */
	isParentCode: function(details, code){
		var bool = true;
		Ext.each(details, function(d){
			if(d.bd_soncode == code){
				bool = false;
			}
		});
		return bool;
	},
	getParentNode: function(tree, node){
		var o = null;
		Ext.each(tree, function(t){
			if(t.id == node.bd_mothercode){
				o = t;
			}
		});
		if(o == null){
			o = new Object();
			o.id = node.bd_mothercode;
			o.cls = "x-tree-cls-root";
			o.parentId = "0";
			o.bd_soncode = node.bd_mothercode;
			o.bd_sonname = node.bd_mothername;
			o.bd_sonspec = node.bd_motherspec;
			o.bs_node = 1;
			o.qtip = node.bd_mothercode;
			tree.push(o);
		}
		return o;
	}
});