Ext.define('erp.view.ma.upgrade.SysnavigationCheckTree',{ 
	extend: 'Ext.tree.Panel', 
	alias: 'widget.sysnavigationCheckTree', 
	id: 'tree-panel', 
	border : false, 
	enableDD : false, 
	split: true, 
	width : '100%',
	height: '100%',
	expandedNodes: [],
	toggleCollapse: function() {
		if (this.collapsed) {
			this.expand(this.animCollapse);
		} else {
			this.collapse(this.collapseDirection, this.animCollapse);
		}
		return this;
	},
	rootVisible: false, 
	singleExpand: true,
	containerScroll : true, 
	collapsible : true, 
	autoScroll: true,
	bodyStyle:'background-color:#FFFFFF;',
	initComponent : function(){
		var me=this;
		me.store= Ext.create('Ext.data.TreeStore', {
	    	root : {
	        	text: 'root',
	        	id: 'root',
	    		expanded: true
	    	}
	    });
		this.callParent(arguments);
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
		beforeitemcollapse:function(eOpts){
			if(eOpts.changeFlag){
				eOpts.changeFlag=false;
				return false;
			}
		},
		beforeitemexpand:function(eOpts){
			if(!eOpts.data.checked&&eOpts.changeFlag){
				eOpts.changeFlag=false;
				return false;
			}
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
	}
});