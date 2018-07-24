/**
 * ERP项目gridpanel样式5:documentCatalog专用treegrid
 */
Ext.define('erp.view.core.grid.DocCaTreeGrid2',{ 
	extend: 'Ext.tree.Panel', 
	alias: 'widget.erpDocCaTreeGrid2',
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	region: 'south',
	layout : 'fit',
	id: 'treegrid', 
 	emptyText : $I18N.common.grid.emptyText,
    useArrows: true,
    rootVisible: false,
    singleExpand: true,
    expandedNodes: [],
    store: Ext.create('Ext.data.TreeStore', {
    	fields: fields,
    	root : {
        	text: 'root',
        	id: 'root',
    		expanded: true
    	}
    }),
    columns: columns,
    plugins: Ext.create('Ext.grid.plugin.CellEditing', {
		clicksToEdit: 1
	}),
	tbar: [
//	       {
//		iconCls: 'tree-add',
//		text: '确定',
//		handler: function(){
//			var url = '';
//			Ext.each(Ext.getCmp('treegrid').expandedNodes, function(){
//	    		console.log(this);
//				if(this.data['dc_isfile'] == 'F'){
//					url += '/' + this.data['qtip'];	    			
//	    		}
//	    	});
//			trigger.setValue(url);
//	    	trigger.fireEvent('aftertrigger', trigger);
//			parent.Ext.getCmp('dbwin').close();		
//		}
//	},
	{
		xtype: 'tbtext',
		height: "15",
		id: 'path',
		style: 'background: #C6d1c5'
	}],
	
	bodyStyle:'background-color:#f1f1f1;',
	initComponent : function(){ 
		Ext.override(Ext.data.AbstractStore,{
			indexOf: Ext.emptyFn
		});
//		console.log(columns);
		this.callParent(arguments);
		this.getTreeGridNode({parentId: 0});
	},
	listeners: {//滚动条有时候没反应，添加此监听器
		scrollershow: function(scroller) {
			if (scroller && scroller.scrollEl) {
				scroller.clearManagedListeners();  
				scroller.mon(scroller.scrollEl, 'scroll', scroller.onElScroll, scroller);  
			}
		}
	},
	getTreeGridNode: function(param){
		var me = this;
		var activeTab = me.getActiveTab();
		activeTab.setLoading(true);
		Ext.Ajax.request({//拿到tree数据
        	url : basePath + 'common/lazyDocumentTree.action',
        	params: param,
        	callback : function(options,success,response){
        		var res = new Ext.decode(response.responseText);
        		activeTab.setLoading(false);
        		if(res.tree){
        			var tree = res.tree;
        			Ext.each(tree, function(t){
        				t.dc_id = t.id;
        				t.dc_parentid = t.parentId;
        				t.dc_displayname = t.text;
        				t.dc_isfile = t.leaf ? 'T' : 'F';
//        				t.dc_tabtitle = t.text;
//        				t.dc_url = t.url;
//        				t.dc_deleteable = t.deleteable ? 'T' : 'F';
        				t.dc_updatetime = t.updatetime;
//        				t.dc_filesize = t.filesize;
        				t.dc_version = t.version;
        				t.dc_creator = t.creator;
        				t.dc_creator_id = t.creator_id;
        			});
        			me.store.setRootNode({
                		text: 'root',
                	    id: 'root',
                		expanded: true,
                		children: tree
                	});
        			Ext.each(me.store.tree.root.childNodes, function(){
        				this.dirty = false;
        			});
        		} else if(res.exceptionInfo){
        			showError(res.exceptionInfo);
        		}
        	}
        });
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
//	getExpandNode: function(root){
//		var treegrid = Ext.getCmp('treegrid');
//		var items = treegrid.selModel.selected.items;
//		var road = '';
//		Ext.each(items,function(){
////			console.log(items.length);
//			road += this.data['dc_id']; 
//		});
//		return road;
//	},
	getActiveTab: function(){
		var tab = null;
		if(Ext.getCmp("content-panel")){
			tab = Ext.getCmp("content-panel").getActiveTab();
		}
		if(!tab){
			var win = parent.Ext.ComponentQuery.query('window');
			if(win.length > 0){
				tab = win[win.length-1];
			}
		}
    	if(!tab && parent.Ext.getCmp("content-panel"))
    		tab = parent.Ext.getCmp("content-panel").getActiveTab();
    	if(!tab  && parent.parent.Ext.getCmp("content-panel"))
    		tab = parent.parent.Ext.getCmp("content-panel").getActiveTab();
    	return tab;
	},
	getExpandedItems: function(record){
		var me = this;
		me.getRecordParents();
		if(record.isExpanded() || record.data['leaf']){
			
		} else {
			me.expandedNodes.push(record);						
		}
	},
	getRecordParents: function(parent){
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
						me.getRecordParents(this);
					}
				}
			});
		}
	}
});