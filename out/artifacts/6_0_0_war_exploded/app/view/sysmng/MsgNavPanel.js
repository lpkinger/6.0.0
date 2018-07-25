Ext.define('erp.view.sysmng.MsgNavPanel',{ 
	extend: 'Ext.panel.Panel', 
	alias: 'widget.erpMsgNavPanel', 
	id: 'msgNavPanel',
	region: 'west',
	width: '25%',
	layout: 'accordion',
	collapsible:true,
	title:'消息设置导航',	
	defaults:{
		autoScroll:true,
		collapsed:true,
		listeners: {
			expand: function(node){
				if(node.items.items.length == 0){
					var me = Ext.getCmp('msgNavPanel');
					me.getTreeNode(node);
				}
			}
		}
	},
	items: [{
        title: '客户关系管理',
        //collapsed:false
    },{
        title: '产品生命周期管理',
        collapsed:false
    },{
        title: '供应链管理'
    },{
        title: '生产制造管理'
    },{
        title: '行政办公管理'
    },{
        title: '人力资源管理'
    },{
        title: '风险控制管理'
    },{
        title: '财务会计管理'
    },{
        title: '成本会计管理'
    },{
        title: '售后服务管理'
    }],
    listeners:{
    	afterrender:function(grid){
    		var doms = Ext.select('.x-panel-header-text-default');
   			doms.elements[0].style = 'color:black;font-weight:bold';

   			var first = grid.items.items[0];
   			Ext.defer(function(){
   				first.expand();
   			},300);
   			
    	}
    },
	getTreeNode: function(node){
		var me = this;
		Ext.Ajax.request({// 拿到tree数据
        	url : basePath + 'custommessage/getTree.action',
        	params: {
        		module:node.title
        	},
        	callback : function(options,success,response){
        		var res = new Ext.decode(response.responseText);
        		if(res.modules){
        			var tree = me.parseTree(res.modules);
        			if(tree.length>0){
	         			node.add({
	        				xtype: 'treepanel',
	        				autoHeight:true,
	        				rootVisible: false, 
	        				containerScroll : true, 
	        				autoScroll: false, 
	        				border:false,
	        				store: Ext.create('Ext.data.TreeStore', {
	        					fields:['id','text','leaf','parentId','caller'],
	        					root: {
	        						text: 'root',
		                    	    id: 'root',
		                    		expanded: true,
		                    		children: tree
	        					}
	        				}),
	        				listeners: {
	        					itemmousedown:function(tree,record){
	        						caller = record.get('caller');
	 								var form = 	Ext.getCmp(caller + 'msgMdlSet');	
	 								var viewport = Ext.getCmp('viewport');
	 								var forms = viewport.query('form');
	 								if(forms.length>0){
	 									Ext.Array.each(forms,function(item,index){
	 										item.hide();	 									
	 									});
	 								}
	 								if(!form){ 	
	 									var newform = Ext.create('widget.erpMsgModelSetPanel',{
	 										id:caller + 'msgMdlSet'
	 									});
		 								viewport.add(newform);	 									
	 								}else{
	 									form.show();
	 								}   
	        					}
	            			}
	        			});       				
        			}

        			
        		} else if(res.exceptionInfo){
        			showError(res.exceptionInfo);
        		}
        	}
        });
	},
	parseTree: function(arr){
		var tree = new Array(),t;
		Ext.each(arr, function(r,index){
			t = new Object();
			t.id = index;
			t.text = r.mm_name;
			t.caller = r.mm_caller;
			t.parentId = 0;
			t.leaf = true;
			tree.push(t);
		});
		return tree;
	}
});