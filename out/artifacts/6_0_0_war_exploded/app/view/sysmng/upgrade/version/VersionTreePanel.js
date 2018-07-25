/**
 * ERP项目gridpanel样式5:sysNavigation专用treegrid
 */
Ext.define('erp.view.sysmng.upgrade.version.VersionTreePanel',{ 
    extend: 'Ext.tree.Panel', 
    alias: 'widget.VersionTreePanel',   
    layout : 'fit',
    id: 'VersionTreePanel', 
    emptyText : $I18N.common.grid.emptyText,
    useArrows: true,
    rootVisible: false,
    singleExpand: true,
    /*autoScroll:true,*/
    lockable: true,    
   // cls: 'x-tree-icon-parent',
    columns : [ {
            "header" : "ID",
            "dbfind" : "",
            "cls" : "x-grid-header-1",
            "summaryType" : "",
            "dataIndex" : "sn_id",
            "align" : "left",
            "xtype" : "treecolumn",
            "readOnly" : false,
            "hidden" : true,
            "text" : "ID"
            }, {
            header : "描述",            
            dbfind : "",
            cls : "x-grid-header-1",
            summaryType : "",
            dataIndex : "sn_displayname",
            align : "left",
            sortable : true,
            xtype : "treecolumn",
            readOnly : true, 
            hidden : false,          
          	flex: 2,
           //width:270,
            text : "描述"
      
           },{
                "dbfind" : "",
                "cls" : "x-grid-header-1",
                "summaryType" : "",
                "dataIndex" : "sn_parentid",
                "align" : "left",
                "xtype" : "treecolumn",
                "readOnly" : false,
                "hidden" : true,
                "width" : 0.0,
                "text" : "父节点ID"
           },{
        	   "dbfind" : "",
        	   "cls" : "x-grid-header-1",
        	   "summaryType" : "",
        	   "dataIndex" : "sn_num",
        	   "align" : "center",
        	   "readOnly" : false,
        	   "hidden" : false,
        	 // width:130,
        	   "text" : "标识号"
        	  
          },{
            "dbfind" : "",
            "cls" : "x-grid-header-1",
            "summaryType" : "",
            "dataIndex" : "sn_svnversion",
            "align" : "center",
            "readOnly" : false,
            "hidden" : false,
           //width:130,
            "text" : "版本号"
            
        },
        {
		   xtype: 'actioncolumn',
			
		   width: 24,
		   icon: (window.basePath || '') + 'jsps/sysmng/images/add.png',

		        handler:function(v,r,c,i,e){
		        	
		        	var me=this.ownerCt.ownerCt;
		        	me.handleAddClick(v,r,c,i,e);
		        	
		        	
		        }
		        
		        
		    }

            ],

    bodyStyle:'background-color:#f1f1f1;',
    initComponent : function(){ 
        var me=this;
        Ext.override(Ext.data.AbstractStore,{
            indexOf: Ext.emptyFn
        });
        me.store=Ext.create('Ext.data.TreeStore', {
            storeId: 'systreestore',
            fields: [{"name":"sn_id","type":"string"},
                     {"name":"sn_displayname","type":"string"},
                     {"name":"sn_parentid","type":"string"},
                     {"name":"sn_num","type":"string"},
                     {"name":"sn_svnversion","type":"number"}
                     ],
            root : {
                text: 'root',
                id: 'root',
                expanded: true
            },
            listeners:{
                     beforeexpand:Ext.bind(me.handleSpExpandClick, me)                  
                 } 
        });
        me.plugins = [me.cellEditingPlugin = Ext.create('Ext.grid.plugin.CellEditing',{
            clicksToEdit:1,
            listeners:{
             beforeedit:function(t,e){
             
             }
            }
            }
        )];
        
        this.callParent(arguments);
        this.view.onItemClick = function(record, item, index, e) {
        	
            if (e.getTarget(this.expanderSelector, item)) {
                this.toggle(record);
                return false;
            }
           // return this.callParent(arguments);
        };
        this.getTreeGridNode({parentId: 0});
        
    },
    listeners: {//滚动条有时候没反应，添加此监听器
        scrollershow: function(scroller) {
            if (scroller && scroller.scrollEl) {
                scroller.clearManagedListeners();  
                scroller.mon(scroller.scrollEl, 'scroll', scroller.onElScroll, scroller);  
            }
        },
        itemclick:function(view,record,item,index,e,o){
        	
        	var id=record.data.sn_num;
			var vppanel=Ext.getCmp('versionpanelpanel');
			vppanel.getSysUpgradeLog(id);

         		
        }
       
    },
  
    
   
    getTreeGridNode: function(param){
        var me = this;
        var activeTab = me.getActiveTab();
        activeTab.setLoading(true);
        Ext.Ajax.request({//拿到tree数据
            url : basePath + 'upgrade/lazyTree.action',
            params: param,
            callback : function(options,success,response){
                var res = new Ext.decode(response.responseText);
                activeTab.setLoading(false);
                if(res.tree){
                    var tree = res.tree;
                   
                    Ext.each(tree, function(t){                       
 						t.sn_id = t.id;
				        t.sn_parentid = t.parentId;
				        t.sn_displayname = t.text;
				        t.sn_detno = t.detno;
				        t.sn_isleaf = t.leaf;
				        t.sn_using = t.using;
				        t.sn_tabtitle = t.text;
				        t.sn_url = t.url;
				        t.dirty = false;
				        t.sn_deleteable = t.deleteable;
				        t.sn_showmode = t.showMode;
				        t.sn_logic = t.data.sn_logic;
				        t.sn_limit = t.data.sn_limit;
				        t.sn_caller = t.data.sn_caller;	
				        t.sn_addurl = t.data.sn_addurl;	
				        t.sn_show=t.data.sn_show,
        				t.sn_standardDesc=t.data.sn_standardDesc;                     	
                        t.sn_num=t.data.sn_num;
        				t.sn_svnversion=t.data.sn_svnversion;
                        t.data = null;
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
                   // me.setParentNodes(me.store.tree.root, true);
                } else if(res.exceptionInfo){
                    showError(res.exceptionInfo);
                }
            }
        });
    },
    setParentNodes: function(record, isExpand){
        var tree = this, 
            grid = tree.ownerCt.down('grid'), data = new Array(), nodes = record.childNodes;
           
        Ext.each(nodes, function(node){
            data.push({
                sn_id: node.get('sn_id'),
                sn_displayname: node.get('sn_displayname'),
                sn_detno: node.get('sn_detno'),
                sn_addurl:node.get('sn_addurl')
            });
        });
        grid.store.loadData(data);
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
   
   
    checkRecord: function(record, dataIndex, checked){
        var me = this;
        if(record.childNodes.length > 0){
            Ext.each(record.childNodes, function(){
                this.set(dataIndex, checked);
                me.checkRecord(this, dataIndex, checked);
            });
        }
    },
    handleSpExpandClick: function(record) {//自己新加的
    	
    
    	
        if(record.get('id')!='root'){
         this.fireEvent('spcexpandclick', record);
     }
    },
    handleAddClick: function(gridView, rowIndex, colIndex, column, e) {
    	
        this.fireEvent('addclick', gridView, rowIndex, colIndex, column, e);
	}
    
    
    
    
    
});