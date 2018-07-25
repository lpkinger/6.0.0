Ext.define('erp.view.common.main.NavigationTreePanel',{ 
	extend: 'Ext.tree.Panel', 
	alias: 'widget.erpNavigationTreePanel', 
	id: 'navigation-panel', 
	border:'0 1 0 0',
	margins : '0 1 0 0', 
	enableDD : false, 
	split: true, 
	cls:'allNavigationTree',
	//title: $I18N.common.main.allNavigation,
	toggleCollapse: function() {
		if (this.collapsed) {
			this.expand(this.animCollapse);
		} else {
			this.title = $I18N.common.main.allNavigation,
			this.collapse(this.collapseDirection, this.animCollapse);
		}
		return this; 
	},
	singleExpand: true,
	rootVisible: false, 
	containerScroll : true, 
	//collapsible : true, 
	autoScroll: false, 
	//useArrows: true,
	bodyStyle:'background-color:#FFFFFF;',
	expandClick:function(record){
		var me = this;
		if (record.childNodes.length == 0 &&record.data['id']!='root') {
            //从后台加载
            var tree = this;
            var condition = me.baseCondition;
            me.setLoading(true, tree.body);
            Ext.Ajax.request({ //拿到tree数据
            	 url: basePath + 'common/getAllNavigation.action',
                 params: {
                     parentId: record.data['id'],
                     condition: condition
                 },
                 callback: function(options, success, response) {
                     tree.setLoading(false);
                     var res = new Ext.decode(response.responseText);
                     if (res.tree) {
                         if (!record.get('level')) {
                             record.set('level', 0);
                         }
                     	 Ext.each(res.tree, function(n) {
                             if (!n.leaf) {
                                  n.level = record.get('level') + 1;
                                  n.iconCls = 'x-tree-icon-level-' + n.level;
                             }
                         });
                         record.appendChild(res.tree);
                         Ext.each(record.childNodes,function(r){ 
				            if(r.raw.updateflag==1){//下级子菜单有升级的隐藏当前升级图标
				        		record.expanded=true;
				        		var node=Ext.query("tr[data-recordid="+record.raw.id+"]"); 
				         		var icons = Ext.DomQuery.select('.x-action-col-icon', node); 
				         		Ext.get(icons[0]).addCls('x-hidden');
	           					return false;
				        		
				            }
				        });
                         record.expand(false, true); //展开
                      } else if (res.exceptionInfo) {
                         showError(res.exceptionInfo);
                      }
                   }
                });
        }else{
        	 Ext.each(record.childNodes,function(r){
	            if(r.raw.updateflag==1){
	           		record.expanded=true;
	           		return false;
	         		
	            }
	        });
        }       
	},
	hideHeaders: true,
	columnLines:false,
	initComponent : function(){
		var me=this;
		 me.columns =[{
		        xtype: 'treecolumn',
		        dataIndex: 'text',
		        flex: 1
		    },{
		        width: 24,
		        renderer :function(val, meta, record){
		        	 if (!record.expanded&&(!(record.get('updateflag')==0||(record.raw &&record.raw.updateflag==0)))) {
		        		return '<img src="' + basePath + 'resource/images/tree/uasupdate.png" class="x-action-col-icon"' +
		        				'data-qtip="升级">';		             
		        	}
		        }
		    },{
				width: 24,
		        renderer :function(val, meta, record){
		        	return '<img src="' + basePath + 'resource/images/tree/uasquestion.png" class="x-action-col-icon"' +
		        				'data-qtip="查看说明">';
		        }
		    }];
		me.store= Ext.create('Ext.data.TreeStore', {
			fields: ['id','text'],
	    	root : {
	        	text: 'root',
	        	id: 'root',
	    		expanded: true
	    	},
	    	listeners:{
	           beforeexpand:function(record){
	           		me.expandClick(record); 
	           },
	           beforeCollapse:function(record){
	           	if(record.raw.updateflag==1){
	           		record.expanded=false;
	           	}
	           }
	        } 
	    }),
		this.getTreeRootNode(0);//页面加载时，只将parentId = 0的节点加载进来
		this.callParent(arguments);
		me.addEvents('addclick');
	},
	getTreeRootNode: function(parentId){
		var condition = this.baseCondition;
		Ext.Ajax.request({//拿到tree数据
        	url : basePath + 'common/getAllNavigation.action',
        	params: {
        		parentId: parentId,
        		condition: condition
        	},
        	callback : function(options,success,response){
        		var res = new Ext.decode(response.responseText);
        		if(res.tree){
        			var tree = res.tree;
                	Ext.getCmp('navigation-panel').store.setRootNode({
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
	listeners: {//滚动条有时候没反应，添加此监听器
		scrollershow: function(scroller) {
			if (scroller && scroller.scrollEl) {
				scroller.clearManagedListeners();  
				scroller.mon(scroller.scrollEl, 'scroll', scroller.onElScroll, scroller);  
			}
		}
	}
});