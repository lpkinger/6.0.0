Ext.define('erp.view.oa.doc.OrgTreePanel', {
	extend: 'Ext.tree.Panel',
	xtype: 'erpOrgTreePanel',
	id:'orgtree',
	isSpecial:false,//贝腾代理商使用
	lines:true,
	rootVisible: false, 
	containerScroll : true, 
	autoScroll: false, 
	useArrows: true,
	split:true,
	closeAction:'destroy',
	border : false, 
	enableDD : false,
	FormUtil:Ext.create('erp.util.FormUtil'),
	store: Ext.create('Ext.data.TreeStore', {
		root : {
			text: 'root',
			id: 'root',
			expanded: true
		}
	}),
	initComponent : function(){ 
		this.callParent(arguments);
		this.getTreeRootNode(0);
	},
	listeners:{
		itemmousedown: function(selModel, record){
			var me=this;
			var tree=selModel.ownerCt;
			if(! tree.itemselector) tree.itemselector=Ext.getCmp('itemselector-field');
			var data=new Array();
			data.push({
				text:'<font color="#D52B2B">[组织]</font>'+record.data.text,
			    value:'org#'+record.data.id
			});
			Ext.Array.each(record.data.otherInfo,function(item){
				data.push({
					text:'<font color="#C4C43C">[岗位]</font>'+item.jo_name,
				    value:'job#'+item.jo_id
				});
			});
			Ext.Array.each(record.data.data,function(item){
				data.push({
					text:/*'<font color="#4DB34D">[个人]</font>'+*/item.em_name,
				    value:'employee#'+item.em_id
				});
			});
			tree.itemselector.fromField.store.loadData(data);
	    	var parentId=record.data.id;
			if (record.isExpanded() && record.childNodes.length > 0) { //是根节点，且已展开
            	record.collapse(true, true); //收拢
			} else { //未展开
             //看是否加载了其children
	           if (record.childNodes.length == 0) {
	             //从后台加载
	                tree.setLoading(true, tree.body);
	                Ext.Ajax.request({ //拿到tree数据
	                    url: basePath + (me.isSpecial?'hr/employee/getAgentHrOrgsTreeAndEmployees.action':'hr/employee/getHrOrgsTreeAndEmployees.action'),
			            params: {
			                 parentId: parentId
			            },
	                    callback: function(options, success, response) {
	                        tree.setLoading(false);
	                        var res = new Ext.decode(response.responseText);
	                        if (res.tree) {
		                        record.appendChild(res.tree);
		                        record.expand(false, true); //展开
	                        } else if (res.exceptionInfo) {
	                           showError(res.exceptionInfo);
	                        }
	                    }
	                 });
               	} else {
		            record.expand(false, true); //展开
		        }
		   }   
		}
	},
	getTreeRootNode: function(parentId){
		treepanel=this;
		treepanel.setLoading(true);
		treepanel.store.removeAll(true);
		Ext.Ajax.request({//拿到tree数据
			url : basePath + (treepanel.isSpecial?'hr/employee/getAgentHrOrgsTreeAndEmployees.action':'hr/employee/getHrOrgsTreeAndEmployees.action'),
			params: {
        		parentId: parentId
        	},
			callback : function(options,success,response){
				var res = new Ext.decode(response.responseText);
				treepanel.setLoading(false);
				if(res.tree){	
					  var root=({					
						text: 'root',
						id: 'root',
						expanded: true,
						children:[]
					});
					 treepanel.store.setRootNode(root);		
					 treepanel.store.getRootNode().appendChild(res.tree);								 
				} else if(res.exceptionInfo){
					showError(res.exceptionInfo);
				}
			}
		});
	}
});
