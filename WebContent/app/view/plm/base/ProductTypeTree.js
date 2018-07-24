Ext.define('erp.view.plm.base.ProductTypeTree',{
	extend: 'Ext.tree.Panel', 
	alias: 'widget.erpProductTypeTreePanel', 
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	id:'ProductTypeTree',
	margins : '0 0 -1 1', 
	border : false, 
	enableDD : false,
	split: true,  
	layout:'fit',
	expandedNodes: [],
	title: "<font color:black;weight= bold;>产品类型</font>",
	toggleCollapse: function() {
		if (this.collapsed) {
			if(typeof(title)!='undefined'){
				this.title=title;
			}		   
			this.expand(this.animCollapse);
		} else{
		    this.title='产品类型';
			this.collapse(this.collapseDirection, this.animCollapse);
		}
		return this;
	},
	rootVisible: false, 
	singleExpand: false,
	containerScroll : true, 
	collapsible : true, 
	autoScroll: true, 
	bodyStyle : 'background-color:white;',
	store: Ext.create('Ext.data.TreeStore', {
		fields:['id','text','data','leaf','detno','qtip'],
		root : {
	    	text: 'root',
	    	id: 0,
			expanded: true
		}
	}),
	initComponent : function(){
		this.getTreeRootNode(this);
		this.callParent(arguments); 
	},
	getTreeRootNode: function(treepanel){
	var me=treepanel;
		Ext.Ajax.request({
        	url : basePath + 'plm/base/getRootProductType.action',
        	async: false,
        	params:{parentid:0},
        	callback : function(options,success,response){
        		var res = new Ext.decode(response.responseText);
        		if(res.tree){
        			var tree = res.tree;
        			 Ext.Array.each(tree, function(tr) {
                         tr.cls="x-tree-cls-node";
      					 tr.pt_id = tr.id;
      					 if(tr.data){
      					}
                      });                      
                	me.store.setRootNode({
                		text: 'root',
                	    id: 0,
                		expanded: true,
                		children: tree
                	});
        		} else if(res.exceptionInfo){
        			showError(res.exceptionInfo);
        		}
        	}
        });
	},
	listeners: {
		scrollershow: function(scroller) {
			if (scroller && scroller.scrollEl) {
				scroller.clearManagedListeners();  
				scroller.mon(scroller.scrollEl, 'scroll', scroller.onElScroll, scroller);  
			}
		}
	},
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
	}
});