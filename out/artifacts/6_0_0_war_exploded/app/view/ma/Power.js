Ext.define('erp.view.ma.Power', {
	extend : 'Ext.Viewport',
	layout : 'border',
	hideBorders : true,
	initComponent : function() {
		var me = this;
		Ext.apply(me, {
			items : [{
				region : 'west',
				width : '24%',
				height : '100%',
				xtype : 'treepanel',				
				singleExpand: true,
				id : 'powertree',
				rootVisible : false,
				useArrows : true,
				enableDD : false, 
				split: true,
				containerScroll : true, 
				collapsible : true,
				tbar:Ext.create('Ext.Toolbar',{
					items: [{
						width:215,
				        xtype: 'searchfield',
				        id: 'searchField'
					}, '->',{
				        iconCls: 'tree-back',
				        cls: 'x-btn-tb',
				        width: 16,
				        tooltip: $I18N.common.main.treeBack,
				        hidden: false,
				    	handler: function(btn){
				    		btn.ownerCt.ownerCt.getTreeRootNode(0);
				    		btn.ownerCt.down('searchfield').setValue(null);
				    	}
				    },'->']
			    }),
	    		getTreeRootNode: function(parentId){
	    			var tree = this;
	    			var nodes = new Array();
	    			Ext.Ajax.request({//拿到tree数据
			        	url : basePath + 'bench/ma/getBenchTree.action',
			        	params: {
			        		isRoot: true
			        	},
			        	async:false,
			        	callback : function(options,success,response){
			        		var res = new Ext.decode(response.responseText);
			        		if(res.tree){
			        			nodes = res.tree;
			        		} else if(res.exceptionInfo){
			        			showError(res.exceptionInfo);
			        		}
			        	}
			        });
					Ext.Ajax.request({//拿到tree数据
			        	url : basePath + 'ma/lazyTree.action',
			        	params: {
			        		parentId: parentId,
			        		condition:'sn_limit=1'
			        	},
			        	callback : function(options,success,response){
			        		var res = new Ext.decode(response.responseText);
			        		if(res.tree){
			        			res.tree = Ext.Array.merge(nodes,res.tree,res.tree);
			                	tree.store.setRootNode({
			                		text: 'root',
			                	    id: 'root',
			                		expanded: true,
			                		children: res.tree
			                	});
			        		} else if(res.exceptionInfo){
			        			showError(res.exceptionInfo);
			        		}
			        	}
			        });
				},
				store : Ext.create('Ext.data.TreeStore', {
					root : {
						text : 'root',
						id : 'root',
						expanded : true
					}
				})
			}, {
				region : 'center',
				height : '100%',
				xtype : 'grouppower'
			} ]
		});
		me.callParent(arguments);
	}
});