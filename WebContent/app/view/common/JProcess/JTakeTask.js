Ext.define('erp.view.common.JProcess.JTakeTask',{ 
	extend: 'Ext.Viewport', 
	/*layout: 'fit', */
	hideBorders: true, 
	layout:'anchor',
	id:'jTakeTask', 
	initComponent : function(){ 
		var me = this; 
		me.callParent(arguments); 
	},
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	listeners: {
		afterrender: function(){
			var me=this;
			var nodewin=Ext.getCmp('win-nodeflow'+id);
			if(nodewin){
				nodewin.show();
			}else {
				var s=getUrlParam("formCondition").replace(/IS/g,'=');
				var nodeId=s.split("=")[1];
				Ext.Ajax.request({ //获取当前节点对应的JProcess对象
					url: basePath + 'common/getCurrentNode.action',
					params: {
						jp_nodeId: nodeId,
						master: master,
						_noc: 1
					},
					success: function(response) {
						var res = new Ext.decode(response.responseText);
						ProcessData = res.info.currentnode;
						var formCondition = ProcessData.jp_keyName + "IS" + ProcessData.jp_keyValue;
						var gridCondition = '';
						if (ProcessData.jp_keyName) {
							gridCondition = ProcessData.jp_formDetailKey + 'IS' + ProcessData.jp_keyValue;
						}
						var url = basePath + ProcessData.jp_url;
						var myurl;
						if (me.BaseUtil.contains(url, '?', true)) {
							myurl = url + '&formCondition=' + formCondition + '&gridCondition=' + gridCondition;
						} else {
							myurl = url + '?formCondition=' + formCondition + '&gridCondition=' + gridCondition;
						}
						myurl += '&_noc=1&_nobutton=1'; // 不限制权限
						if (master) {
							myurl += '&newMaster=' + master;
						}
						Ext.create('Ext.window.Window', {
							id : 'win-nodeflow' + id,
							title: '<span style="color:#CD6839;">设置节点处理人</span>',
							iconCls: 'x-button-icon-set',
							closeAction: 'destroy',
							height : '90%',
							width : '95%',
							maximizable : true,
							buttonAlign : 'center',
							layout : 'anchor',
							items : [ {
								tag : 'iframe',
								frame : true,
								anchor : '100% 100%',
								layout : 'fit',
								html : '<iframe id="iframe_maindetail" src="'+myurl+'" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>'
							}],
							buttons:['->',{
								text:'接&nbsp;&nbsp;管',
								iconCls: 'x-button-icon-check',
								cls: 'x-btn-gray',
								width : 65,
								style: {
									marginLeft: '10px'
								},
								handler:function(btn){
									    var contentpanel =parent.Ext.getCmp("content-panel");
							    	    var s=getUrlParam("formCondition").replace(/IS/g,'=');
							    	    var index = s.indexOf('=');
							    	    if(index!=-1){
							    	    	var nodeId=s.split("=")[1];
							    	    	Ext.Ajax.request({
							    			    url: basePath +'common/takeOverTask.action',
							    			   params: {
							    				   	em_code:emcode,
							    			        nodeId: nodeId,
							    			        needreturn:true
							    			    },
							    			    callback : function(options,success,response){
							    			        var text =  Ext.decode(response.responseText);
							    			        if(text.success){
							    			        	Ext.Msg.alert('提示',"你已接受该审批任务！",function(){
							    			        		window.location.href=basePath+"/jsps/common/flow.jsp?formCondition=jp_nodeIdIS"+nodeId;
							    			        	   /* if(contentpanel){
							    			        	    	contentpanel.getActiveTab().close();
							    			        	    }*/
							    			        	});				    			        	
							    				    }else if(text.exceptionInfo){
							    				    	showError("该任务已经被接管!");
							    				    	 if(contentpanel){
							    			        	    contentpanel.getActiveTab().close();
							    			        	 }							    				    	
							    				    }

							    			   }

							    			});
							    	    }
								}
							},{
								text:'关 &nbsp;&nbsp;闭',
								iconCls: 'x-button-icon-close',
								cls: 'x-btn-gray',
								width: 65,
								style: {
									marginLeft: '10px'
								},
								handler:function(btn){			        	
									btn.ownerCt.ownerCt.close();
									me.BaseUtil.getActiveTab().close();
								}
							},'->']
						}).show();
					}
				});

				/*Ext.Msg.show({
				     title:'确定接管该任务吗?',
				     msg: '如果接管该任务,你将成为该任务节点的办理人,须在指定时间内审批该任务',
				     buttons: Ext.Msg.YESNOCANCEL,
				     fn: function(btn){
				    	 if (btn == 'yes'){
				    	    var o =  parent.Ext.getCmp("content-panel").getActiveTab();
				    	    var s=getUrlParam("formCondition").replace(/IS/g,'=');
				    	    var index = s.indexOf('=');
				    	    if(index!=-1){
				    	    	var nodeId=s.split("=")[1];
				    	    	Ext.Ajax.request({
				    			    url: basePath +'common/takeOverTask.action',
				    			   params: {
				    				   	em_code:em_code,
				    			        nodeId: nodeId,
				    			        needreturn:true
				    			    },
				    			    callback : function(options,success,response){

				    			        var text =  Ext.decode(response.responseText);
				    			        if(text.success){
				    			        	Ext.Msg.alert('提示',"你已接受该审批任务！",function(){
				    			        		o.close();
				    			        		window.location.href=basePath+"/jsps/common/flow.jsp?formCondition=jp_nodeIdIS"+nodeId;
				    			        	});				    			        	


				    				    }else if(text.exceptionInfo){
				    				    	showError("该任务已经被接管!");
				    				    }

				    			   }

				    			});
				    	    }




				    	    }
				    	 else if(btn='no'){
				    		 console.log(this.xtype);
				    		 console.log(parent);
				    		 parent.Ext.getCmp("content-panel").getActiveTab().close();
				    	 } else if(btn='cancel'){
				    		 console.log(parent);
				    		 parent.Ext.getCmp("content-panel").getActiveTab().close();
				    	 }

				     },
				     animateTarget: 'elId',
				     icon: Ext.window.MessageBox.QUESTION

				});*/

			}


		}
	},
	processResult:function(){

	}


});