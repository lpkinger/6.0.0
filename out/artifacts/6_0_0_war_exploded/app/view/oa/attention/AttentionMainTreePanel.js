Ext.define('erp.view.oa.attention.AttentionMainTreePanel',{ 
	extend: 'Ext.tree.Panel', 
	alias: 'widget.AttentionMainTreePanel', 
	 BaseUtil: Ext.create('erp.util.BaseUtil'),
	id: 'attentionTree', 
	margins : '0 0 -1 1', 
	border : false, 
	enableDD : false, 
	split: true, 
	width : '20%', 
	region: 'east',
	title: "<font color=#a1a1a1; size=3;weight= bold;>所有下属</font>",
	toggleCollapse: function() {
		if (this.collapsed) {
		   this.title=title;
			this.expand(this.animCollapse);
		} else{
		    this.title='所有下属';
			this.collapse(this.collapseDirection, this.animCollapse);
		}
		return this;
	},
	rootVisible: false, 
	singleExpand: false,
	containerScroll : true, 
	collapsible : true, 
	autoScroll: false, 
	useArrows: true,
	select:null,
	//bodyStyle:'background-color:#f0f0f0;',
	store: Ext.create('Ext.data.TreeStore', {
		root : {
	    	text: 'root',
	    	id: 'root',
			expanded: true,	
		}
	}),
	initComponent : function(){ 
		this.getTreeRootNode(this);
		this.callParent(arguments); 
	},
	getTreeRootNode: function(treepanel){
	var me=treepanel;
		Ext.Ajax.request({
        	url : basePath + 'oa/attention/getEmployees.action',
        	async: false,
        	params:{
        	 emid:emid
        	},
        	callback : function(options,success,response){
        		var res = new Ext.decode(response.responseText);
        		if(res.tree){
        			var tree = res.tree; 
        			            
                	me.store.setRootNode({
                		text: 'root',
                	    id: 'root',
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
	}
});