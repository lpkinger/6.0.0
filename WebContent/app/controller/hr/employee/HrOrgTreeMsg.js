Ext.QuickTips.init();
Ext.define('erp.controller.hr.employee.HrOrgTreeMsg', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'hr.employee.HrOrgTreeMsg','hr.employee.HrOrgStrTree',
    		'core.form.YnField',
    		'core.form.Panel',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger',
    		'core.toolbar.Toolbar'
    	],
    init:function(){
    	var me = this;
    	me.datamanager = [];
    	this.control({ 
    		'hrOrgStrTree': {
    			itemmousedown:function(selModel,record){
    				var emid,emcode='';
    				if(record.data.leaf){
    					emid = record.data.id;
    					if(record.data.id.indexOf('-')>0)emid=emid.split('-')[0]; 
    				} else {
	    				var parentId=record.data.id;
			            if (record.isExpanded() && record.childNodes.length > 0) { //是根节点，且已展开
            			    record.collapse(true, true); //收拢
			            } else { //未展开
            		    //看是否加载了其children
	                		if (record.childNodes.length == 0) {
	                    		//从后台加载
	                    		var tree = Ext.getCmp('tree-panel');
	                   			tree.setLoading(true, tree.body);
	                   			Ext.Ajax.request({ //拿到tree数据
	                    		    url: basePath + 'hr/employee/getAllHrOrgsTree.action',
			                        params: {
			                            	parentId: parentId
			                        },
	                       		 callback: function(options, success, response) {
	                          		  tree.setLoading(false);
	                          		  var res = new Ext.decode(response.responseText);
	                         		  if (res.tree) {
		                                record.appendChild(me.setTreeFirstLeafCls(res.tree));
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
    			beforerender:function(){
    				Ext.getCmp('tree-panel').collapsible=false;
    				Ext.getCmp('tree-panel').tools=null;
    			}
    		},
    	});
    },
    setTreeFirstLeafCls:function(tree){
    	var arry = tree;
		if (arry[0].leaf==true) {
			arry[0].cls = "x-hrorgTreeFirstLeaf";	
		}
		return arry;
    }
});