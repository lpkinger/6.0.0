Ext.QuickTips.init();
Ext.define('erp.controller.ma.upgrade.AddUpgradeScheme', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    views:['ma.upgrade.AddUpgradeScheme','ma.upgrade.SysnavigationCheckTree'],
    init:function(){
    	var me = this;
    	this.control({
    		'#upgradescheme':{
	    		afterrender:function(v){//加载导航树
	    			var tree=Ext.getCmp();
	    			v.setLoading(true);
					Ext.Ajax.request({//拿到tree数据
			        	url : basePath + 'common/getAllCheckTree.action',
			        	callback : function(options,success,response){
			        		v.setLoading(false);
			        		var res = new Ext.decode(response.responseText);
			        		if(res.tree){
			        			var tree = res.tree;
			                	Ext.getCmp('tree-panel').store.setRootNode({
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
	    		}
    		},
    		'#saveScheme':{
    			click:function(btn){//生成方案
    				var form=btn.ownerCt.ownerCt;
    				var o=new Object();
    				var version_=Ext.getCmp('version').value;
    				if(version_==null || version_==''||version_=='0'||version_==0){
    					showError("版本号不能为空");
    					return false;
    				}
    				var desc_=Ext.getCmp('desc').value;
    				if(desc_==null || desc_==''||desc_=='0'||desc_==0){
    					showError("描述不能为空");
    					return false;
    				}
    				o['version_']=version_;
    				o['desc_']=desc_;
    				var fieldSets=form.query('fieldset');
    				Ext.each(fieldSets,function(fieldSet){
    					var arr=new Array();
    					var items=fieldSet.query('checkbox[checked=true]');
    					Ext.each(items,function(i){
    						arr.push(i.name);
    					});
    					arr=Ext.Array.unique(arr);
    					o[fieldSet.type]=arr.join(',');
    				});
    				var sqls=form.query('textarea');
    				o['sql_']=sqls[0].value;
    				var param =unescape(escape(Ext.JSON.encode(o)));
    				form.setLoading(true);
    				Ext.Ajax.request({ //拿到tree数据
	                   url: basePath + 'ma/upgrade/saveUpgradeScheme.action',
			           params: {
			               	param: param
			           },
		               callback: function(options, success, response) {
		                  form.setLoading(false);
		                  var res = new Ext.decode(response.responseText);
		                  if(res.success){
		                  	showMessage('提示', '添加成功!', 1000);
		                  }
		                  if(res.exceptionInfo){
		                  	showMessage('提示',res.exceptionInfo, 1000);
		                  }
		                }
		            });
    			}
    		},
    		'sysnavigationCheckTree': {
		    	checkchange: function(node, checked) {
		    		var tree=Ext.getCmp('tree-panel');
		    		node.set('checked', checked);
		    		Ext.each(node.childNodes,function(child){
			    		 child.set('checked', checked);
			    		 tree.fireEvent('checkchange', child, checked); 
			    	});
		    		if(!node.data.leaf){
		    			if(checked){
		    				node.expand(false, true); //展开
		    			}
		    			node.changeFlag=true;
		    		}   		
		    	}
		    },
		    '#check':{
		    	click:function(){//检测
		    		var tree=Ext.getCmp('tree-panel');
		    		var checkedNodes = tree.getChecked();
		    		var ids = [];
					for(var i=0;i<checkedNodes.length;i++){
							ids.push(checkedNodes[i].data.id);
					}
					if(ids.length==0){
						showError("请先选择导航");
    					return false;
					}
					Ext.Ajax.request({ //拿到tree数据
	                    url: basePath + 'ma/upgrade/check.action',
			            params: {
			               ids: ids.join(',')
			            },
		                callback: function(options, success, response) {
		                    tree.setLoading(false);
		                    var res = new Ext.decode(response.responseText);
		                    var formitems=new Array();
		                    var result=res.result;
		                    var Width=1/3;
		                    if(result.length>0){Ext.getCmp('resultForm').removeAll();}
		                    for(var i=0;i<result.length;i++){
		                    	var currentItem=result[i];
		                    	var items = new Array();
		                    	var emptyText='输入caller';
			                    Ext.each(currentItem.data,function(d){
			                    		items.push({
											xtype: 'checkbox',
											name: d,
											boxLabel:d,
											checked:true
										});
			                    });
		                    	if(currentItem.type=='sql_'){
		                    		emptyText='输入SQL';
			                    	items.push({xtype: 'panel',columnWidth: 1,border:0,layout:'column',margin: '10 0 10 0',
							 					items:[{xtype:'textarea',columnWidth:1,grow:true}]});
			                    }else{
			                    	if(currentItem.type=='table_') emptyText='输入表名';
			                    	if(currentItem.type=='object_') emptyText='输入对象名';
			                    	items.push({
											xtype: 'panel',
											columnWidth: 1,
											border:0,
											layout:'table',
											margin: '10 0 10 0',
											items:[{xtype:'textfield',emptyText:emptyText,margin: '1 0 0 0'},
												   {xtype:'button',text:'确定',
												    handler:function(btn){
												    	var callerField=btn.ownerCt.down('textfield');
												    	var caller=callerField.value;
												    	var fieldset_c=btn.ownerCt.ownerCt;
												    	fieldset_c.insert(fieldset_c.items.length-1,{
															xtype: 'checkbox',
															name: caller,
															boxLabel:caller,
															checked:true
														});
														callerField.setValue('');
												    }
												   }]
										});
			                   }
			                   formitems.push({
				                    	xtype:'fieldset',
								        title: currentItem.title,
								        collapsible: true,
								        collapsed:(items.length-1)>0?false:true,
								        defaultType: 'textfield',
								        defaults: { columnWidth: Width},
								        layout:'column',
								        type:currentItem.type,
								        items :items
			                    	});
		                    }
		                    Ext.getCmp('resultForm').add(formitems);
		                }
		            });
				}		    	
		    }
    	});
    }
});