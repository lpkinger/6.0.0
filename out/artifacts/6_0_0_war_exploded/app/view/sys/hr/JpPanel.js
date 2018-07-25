Ext.define('erp.view.sys.hr.JpPanel',{ 
	extend: 'Ext.panel.Panel', 
	alias: 'widget.jppanel', 
	id:'jppanel',
	animCollapse: false,
	bodyBorder: true,
	layout: 'accordion',
	border: false,
	autoShow: true,
	loadFlag:true,
	collapsible :false,
	bodyStyle:'background-color:#f1f1f1; ',
	initComponent : function(){
		this.getNodes(0,this);
		this.callParent(arguments);
	},
	getNodes: function(pid,p){
		var me = this;
		Ext.Ajax.request({//拿到tree数据
        	url : basePath +'common/getFieldsDatas.action',
        	params: {
	   			caller: 'jproclassify',
	   			fields: 'jc_id,jc_name',
	   			condition: '1=1 order by jc_detno'
	   		},
        	callback : function(options,success,response){
        		var res = new Ext.decode(response.responseText);
        		if(res.success){
	  				var data = Ext.decode(res.data);
	  				data.push({'JC_ID':'0','JC_NAME':'未分类流程'});
	  				for(var i in data) {
	  					p.add({
        					title:data[i].JC_NAME,
        					autoScroll: true,
        					id: data[i].JC_ID,
        					listeners: {
        						expand: function(n){
        							if(n.items.items.length == 0){
        								me.getTreeNode(n);
        							}
        						}
        					}
        				});
	  				}
        			var n = p.items.items[0];
					if(n && n.items.items.length == 0) {
						me.getTreeNode(n);
					}
        		}else if(res.exceptionInfo){
        			showError(res.exceptionInfo);
        		}
        	}
        });
	},
	getTreeNode: function(node){
		var me = this;
		Ext.Ajax.request({// 拿到tree数据
        	url : basePath + 'common/getJpTree.action',
        	params: {
        		condition: 'jd_selfid='+node.id
        	},
        	callback : function(options,success,response){
        		var res = new Ext.decode(response.responseText);
        		if(res.tree){
        			var tree = me.parseTree(res.tree);
        			node.add({
        				xtype: "treepanel",
        	            itemId: "tree",
        	           /* width: 600,*/
        	            border:false,
        	            height: '100%',
        	            rootVisible: false,
        	            cls:'jptreecolumn',
        	            listeners: {
        					afterrender:function(me){
        						if(Ext.getCmp('jppanel').loadFlag){
	        						if(this.view.store.data.items.length>0){
	        							this.fireEvent('itemmousedown',this.selModel,this.view.store.data.items[0]);
		        						this.selModel.select(this.view.store.data.items[0]);
		        						Ext.getCmp('jppanel').loadFlag=false;
	        						}
	        					}
	     					},
            				itemmousedown: function(selModel, record){
            					Ext.Ajax.request({//拿到tree数据
            			        	url : basePath +'common/checkSimpleJp.action',
            			        	params: {
            				   			jd_id:record.data.id
            				   		},
            			        	callback : function(options,success,response){
            			        		var res = new Ext.decode(response.responseText);
            			        		if(res.success){
            				  				var data = Ext.decode(res.data);
            				  				var jprocesstab=Ext.getCmp("jprocesstab");
            				  				if(data){
            				  					/*jprocesstab.setActiveTab(1);*/
            				  					Ext.Ajax.request({//拿到tree数据
            				  			        	url : basePath +'common/getSimpleJpData.action',
            				  			        	params: {
            				  				   			jd_id:record.data.id
            				  				   		},
            				  			        	callback : function(options,success,response){
            				  			        		var res = new Ext.decode(response.responseText);
            				  			        		if(res.success){
            				  				  				var data = Ext.decode(res.data);
            				  			        			var simpleJpform=Ext.getCmp('simplejpform');
            				  			        			Ext.getCmp('jpname').getEl().update('<b>流程名称:&nbsp;</b>'+res.jpInfo[0].JD_PROCESSDEFINITIONNAME);
            				  			        			Ext.getCmp('jpdescription').getEl().update('<b>流程说明:&nbsp;</b>'+res.jpInfo[0].JD_PROCESSDESCRIPTION);
            				  			        			Ext.getCmp('jpcaller').setValue(res.jpInfo[0].JD_CALLER);
            				  			        			Ext.getCmp('jpenabled').setValue(res.jpInfo[0].JD_ENABLED);
            				  			        			Ext.getCmp('jpressubmit').setValue(res.jpInfo[0].JD_RESSUBMIT);
            				  			        			Ext.getCmp('jpparentid').setValue(res.jpInfo[0].JD_PARENTID);
            				  			        			Ext.getCmp('simplejpid').setValue(res.jpInfo[0].JD_ID);
            				  			        			jprocesstab.setActiveTab(1);
            				  			        			Ext.getCmp('initnavigationpanel').collapse();
            				  			        		}else if(res.exceptionInfo){
            				  			        			showError(res.exceptionInfo);
            				  			        		}
            				  			        	}
            				  			        });
            				  				}else{
            				  					/*jprocesstab.setActiveTab(0);*/
            				  					var simplejprocesspanel=Ext.getCmp("simplejprocesspanel");
            				  					var myMask = new Ext.LoadMask(Ext.getCmp('simplejprocess').getEl(), {//也可以是Ext.getCmp('').getEl()窗口名称
            				  						msg    : "正在加载数据...",//你要写成Loading...也可以
            				  						msgCls : 'z-index:10000;'
            				  					});
            				  		    	 	myMask.show();
            				  					//simplejprocesspanel.store.
            				  					Ext.Ajax.request({//拿到tree数据
            				  			        	url : basePath +'common/getSimpleJpData.action',
            				  			        	params: {
            				  				   			jd_id:record.data.id
            				  				   		},
            				  			        	callback : function(options,success,response){
            				  			        		var res = new Ext.decode(response.responseText);
            				  			        		if(res.success){
            				  				  				/*var data = Ext.decode(res.data);*/
            				  			        			var simpleJpform=Ext.getCmp('simplejpform');
            				  			        			Ext.getCmp('jpname').getEl().update('<b>流程名称:&nbsp;</b>'+res.jpInfo[0].JD_PROCESSDEFINITIONNAME);
            				  			        			Ext.getCmp('jpdescription').getEl().update('<b>流程说明:&nbsp;</b>'+res.jpInfo[0].JD_PROCESSDESCRIPTION);
            				  			        			Ext.getCmp('jpcaller').setValue(res.jpInfo[0].JD_CALLER);
            				  			        			Ext.getCmp('jpenabled').setValue(res.jpInfo[0].JD_ENABLED);
            				  			        			Ext.getCmp('jpressubmit').setValue(res.jpInfo[0].JD_RESSUBMIT);
            				  			        			Ext.getCmp('jpparentid').setValue(res.jpInfo[0].JD_PARENTID);
            				  			        			Ext.getCmp('simplejpid').setValue(res.jpInfo[0].JD_ID);
            				  			        			myMask.hide();
            				  			        			var data = Ext.decode(res.data.replace(/,}/g, '}').replace(/,]/g, ']'));
            				  				  				var jprocesstab=Ext.getCmp("jprocesstab");
            				  				  				if(data){
            				  				  					simplejprocesspanel.store.loadData(data);
            				  				  				}else{
            				  				  					
            				  				  				}
            				  				  			jprocesstab.setActiveTab(0);
            				  			        		}else if(res.exceptionInfo){
            				  			        			showError(res.exceptionInfo);
            				  			        		}
            				  			        	}
            				  			        });
            				  				}
            			        		}else if(res.exceptionInfo){
            			        			showError(res.exceptionInfo);
            			        		}
            			        	}
            			        });
            						var jprocesspanel=Ext.getCmp('jprocesspanel');
                					jprocesspanel.removeAll();
                					jprocesspanel.add({
    										tag : 'iframe',
    										style:{
    											background:'#f0f0f0',
    											border:'none'
    										},						  
    										frame : true,
    										border : false,
    										layout : 'fit',
    										height:window.innerHeight*0.9,
    										html :'<iframe id="iframe_maindetail_" src="'+basePath+'workfloweditor/workfloweditor2.jsp?caller='
    										+record.data.qtip
    										+'" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>'	
    									});
                					jprocesspanel.doLayout();
            				}
            			},
        	            store: Ext.create('Ext.data.TreeStore', {
        	            	fields:[{name:'text',type:'string'},{name:'id',type:'int'},{name:'enabled',type:'string'},{name:'caller',type:'string'}],	
        	            	root: {
        						text: 'root',
	                    	    id: 'root',
	                    		expanded: true,
	                    		children: tree
        					}
        				}), 
        	            columns: [ 
								{dataIndex:'enabled',width: 43,text:'启用',fixed:true,xtype: "",
									filter: {dataIndex: "enabled", xtype: "textfield"},
									  renderer: function(value, cellmeta, record) {
										  var id=record.data.id;
										  var value=record.data.enabled;
									  	  cellmeta.style="padding:0px!important";
										  if(value=='是'){
										  	return '<input class="mui-switch" onchange="changeJpEnable(this)"  type="checkbox" ' +
										  			'data-value="'+value+'" data-id="'+id+'" checked>';
										  }else{
										  	return '<input class="mui-switch" onchange="changeJpEnable(this)"  type="checkbox" ' +
										  			'data-value="'+value+'" data-id="'+id+'">';
										  }
									  }
								},
							  {
        	                    xtype: 'treecolumn',
        	                    text: '流程名称',
        	                    dataIndex: "text",
        	                    flex: 1,
        	                    sortable: false,
        	                 },{
        	                	 xtype:'',
        	                	 text:'id',
        	                	 dataIndex:'id',
        	                	 hidden:true
        	                 },{
        	                	 xtype:'',
        	                	 text:'caller',
        	                	 dataIndex:'caller',
        	                	 hidden:true
        	                 }
        	            ]
        			});
        			
        		} else if(res.exceptionInfo){
        			showError(res.exceptionInfo);
        		}
        	}
        });
	},
	listeners: {
		/*scrollershow: function(scroller) {
			if (scroller && scroller.scrollEl) {
				scroller.clearManagedListeners();  
				scroller.mon(scroller.scrollEl, 'scroll', scroller.onElScroll, scroller);  
			}
		}*/
	},
	parseTree: function(arr){
		var tree = new Array(),t;
		Ext.each(arr, function(r){
			t = new Object();
			t.id = r.jd_id;
			t.text = r.jd_processDefinitionName;
			t.qtip = r.jd_caller;
			t.caller = r.jd_caller;
			t.parentId = r.jd_selfId;
			t.leaf =true;
			t.enabled=r.jd_enabled;
			tree.push(t);
		});
		return tree;
	}
});