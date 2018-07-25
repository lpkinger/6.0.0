Ext.define('erp.view.oa.attention.PersonalAddressTreePanel',{ 
	extend: 'Ext.tree.Panel', 
	alias: 'widget.erpPersonalAddressTreePanel', 
	 BaseUtil: Ext.create('erp.util.BaseUtil'),
	id: 'PersonalAddressTree', 
	margins : '0 0 -1 1', 
	border : false, 
	enableDD : false, 
	split: true, 
	width : '20%', 
	region: 'east',
	title: "<font color=#a1a1a1; size=3;weight= bold;>个人通讯录</font>",
	toggleCollapse: function() {
		if (this.collapsed) {
		   this.title=title;
			this.expand(this.animCollapse);
		} else{
		    this.title='个人通讯录';
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
	bodyStyle:'background-color:#f0f0f0;',
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
        	url : basePath + 'oa/addressbook/getAddressBookGroup.action',
        	async: false,
        	callback : function(options,success,response){
        		var res = new Ext.decode(response.responseText);
        		if(res.tree){
        			var tree = res.tree;
        			if(tree.length<2){
        			  var item=new Object();
        			  item.text='没有添加分组';        			
        			  item.leaf=true;
        			  item.id=-1;
        			  tree.push(item);
        			}
        			 Ext.Array.each(tree, function(tr) {
                          tr.cls="x-tree-cls-node";
                      });                      
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