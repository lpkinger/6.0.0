	Ext.define('erp.view.common.main.Toolbar',{ 
		extend: 'Ext.Toolbar', 
		alias: 'widget.erpTreeToolbar',
		items: [{
			/*width:215,*/
	        xtype: 'searchfield',
	        cls: 'search-field'
	        /*id: 'searchField'*/
		}, '->',{
	        iconCls: 'tree-back',
	        cls: 'x-btn-tb',
	        width: 16,
	        tooltip: $I18N.common.main.treeBack,
	        hidden: false,
	    	handler: function(){
	    		var treepanel = this.up('treepanel');
	    		var searchField = treepanel.down('erpTreeToolbar').down('searchfield');
	    		if(treepanel.xtype=='workspaceTreePanel'){
	    			treepanel.getTreeRootNode(-999);
	    			searchField.originalValue = null;
	    			var win = Ext.getCmp('benchWin');
					win && win.destroy();
	    		}else{
	    			treepanel.getTreeRootNode(0);
	    		}
	    		searchField.setValue(null);
	    	}
	    },'->'/*, '-',{//将用户每次搜索的字条存入cookie，下次搜索时，作为关键字，可以被用户直接选择；也可以自定义关键字
			xtype: 'button',
			iconCls: 'x-button-icon-help',
			cls: 'x-btn-tb',
			width: 16,
			tooltip: $I18N.common.main.keywords
		}*//*,{
	        type: 'plus',
	        id:'open',
	        cls: 'tree-open',
	        tooltip:'全部展开',
	        hidden:false,
	    	handler: function(){
	    		Ext.getCmp('tree-panel').openCloseFun();
	    	}
	    },{
	    	type:'minus',
	    	id:'close',
	    	tooltip:'全部关闭',
	    	cls: 'tree-close',
	    	hidden:true,
	    	handler: function(){
	    		Ext.getCmp('tree-panel').openCloseFun();
	    	}
	    },{
	    	type:'refresh',
	    	tooltip:'刷新',
	    	cls: 'tree-refresh',
	    	handler: function(c, t) {
	    		var tree = Ext.getCmp('tree-panel');
	            tree.setLoading(true, tree.body);
	            var root = tree.getRootNode();
	            root.collapseChildren(true, false);
	            Ext.Function.defer(function() {
	                tree.setLoading(false);
	                root.expand(true, true);
	                Ext.getCmp("open").hide();
	                Ext.getCmp("close").show();
	            }, 1000);
	        }
	    }*/],
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});