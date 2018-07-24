Ext.define('erp.view.common.CateTreepanelDbfind.CateStrTree',{ 
	extend: 'Ext.tab.Panel', 
	alias: 'widget.cateStrDbfindTree', 
	id: 'tree-panel', 
	defaults: {
	    bodyStyle:'background-color:#f1f1f1;',
	    xtype: 'container'
	},
	border : false,
	enableDD : false,
	split: true,
	width : '100%',
	height: '100%',
	expandedNodes: [],
	bodyStyle: 'background-color:#f1f1f1;',
	mode: 'SINGLE',
	initComponent : function(){ 
		this.getTreeRootNode(0, key, caller);
		this.callParent(arguments);
		if (trigger.mode) {
			this.mode = trigger.mode;
		}
	}, 
	getTreeRootNode: function(parentid, key, caller){
		var me = this;
		Ext.Ajax.request({//拿到tree数据
        	url : basePath + 'common/getAllCateTree.action',
        	params: {
        		key: key,
        		caller: caller1
        	},
        	callback : function(options, success, response){
        		var res = new Ext.decode(response.responseText);
        		if(res.success){
        			var data = res.data, keys = Ext.Object.getKeys(data), title = '', d = {};
        			for(var i in keys) {
        				title = keys[i];
        				d = data[title];
        				if(d) {
        					me.add({
        						title: title,
        						items: [{
        							xtype: 'treepanel',
            				    	height: '100%',
            						singleExpand: true,
            						rootVisible: false, 
            						containerScroll : true, 
            						autoScroll: false, 
            						useArrows: true,
            						cls: 'custom',
            						store: Ext.create('Ext.data.TreeStore', {
            							fields: me.treefields,
            					    	root : {
            					        	text: 'root',
            					    		expanded: true,
            					    		children: d
            					    	}
            						}),
            						columns: me.treecolumns,
            						listeners: {
            							checkchange: function(record, b) {
            								var ts = me.query('treepanel');
            								if(me.mode == 'SINGLE' && b) {
            									Ext.each(ts, function(t){
            										var ch = t.getChecked();
                    								Ext.each(ch, function(c){
                    									if(c.id != record.id) {
                    										c.set('checked', false);
                    									}
                    								});
            									});
            								}
            							}
            						}
        						}]
        					});
        				}
        			}
                	if(res.findToUi){
                		me.dbfinds = res.findToUi;
                	}
        		} else if(res.exceptionInfo){
        			showError(res.exceptionInfo);
        		}
        	}
        });
	},
	listenerNode: function(node){
		var me = this;
		var Node = node || Ext.getCmp('tree-panel').store.tree.root;
		Ext.each(Node,function(e){
			e.on('beforecollapse',function(p,o){
			});
			if(e.data['leaf'] == false){
				me.listenerNode(e);
			}
		});
		
	},
    openCloseFun: function(){
	 	  var o = Ext.getCmp("open");
	 	  var c = Ext.getCmp("close");
	 	  var tree = Ext.getCmp('tree-panel');
	 		  if(o.hidden==false&&c.hidden==true){
	 			  tree.expandAll();
	 			  o.hide();
	 			  c.show();
	 		  }else{
	 			  tree.collapseAll();
	 			  o.show();
	 			  c.hide();
	 		  }
	},
	listeners: {//滚动条有时候没反应，添加此监听器
		scrollershow: function(scroller) {
			if (scroller && scroller.scrollEl) {
				scroller.clearManagedListeners();  
				scroller.mon(scroller.scrollEl, 'scroll', scroller.onElScroll, scroller);  
			}
		},
		//单选
		checkchange: function(node, checked){
		    var tree = Ext.getCmp('tree-panel');
		    var checkedList = tree.getChecked();
			Ext.each(checkedList,function(tr,index){
				tr.set('checked', false);
			});
			node.set('checked', true);
		}
	},
	/**
	 * 找到所有已展开的节点，包括当前被选中的节点
	 * @param record 当前被选中的节点
	 */
	getExpandedItems: function(record){
		var me = this;
		me.getRecordParents(record);
		if(record.isLeaf()){
			me.expandedNodes.push(record);
		}
	},
	getRecordParents: function(record, parent){
		var me = this;
		if(!parent){
			parent = me.store.tree.root;
			me.expandedNodes = [];
		}
		if(parent.childNodes.length > 0){
			Ext.each(parent.childNodes, function(){
				if(this.isExpanded()){
					me.expandedNodes.push(this);
					if(this.childNodes.length > 0){
						me.getRecordParents(record, this);
					}
				}
			});
		}
	},
	getExpandItem: function(root){
		var me = this;
		if(!root){
			root = this.store.tree.root;
		}
		var node = null;
		if(root.childNodes.length > 0){
			Ext.each(root.childNodes, function(){
				if(this.isExpanded()){
					node = this;
					if(this.childNodes.length > 0){
						var n = me.getExpandItem(this);
						node = n == null ? node : n;
					}
				}
			});
		}
		return node;
	},
	getChecked: function() {
		var trees = this.query('treepanel'), records = new Array();
		Ext.each(trees, function(t){
			var c = t.getChecked();
			if(c.length > 0) {
				records = c;
			}
		});
		return records;
	},
	treefields: [{
		name: 'ca_id',
		type: 'int'
	},'ca_code','ca_pcode','ca_name','ca_description','ca_class','currency','ca_asstype','ca_assname'],
	treecolumns: [{
		dataIndex : 'ca_code',
		xtype: 'treecolumn',
		header: '科目',
		flex: 1,
		renderer: function(val, meta, record) {
			return record.raw.data.ca_code;
		}
	},{
		dataIndex : 'ca_description',
		header: '描述',
		flex: 2,
		renderer: function(val, meta, record) {
			return record.raw.data.ca_description;
		}
	},{
		dataIndex : 'ca_currency',
		header: '外币',
		flex: 0.5,
		renderer: function(val, meta, record) {
			return record.raw.data.ca_currency;
		}
	},{
		dataIndex : 'ca_assname',
		header: '辅助核算',
		flex: 2,
		renderer: function(val, meta, record) {
			return record.raw.data.ca_assname;
		}
	}]
});