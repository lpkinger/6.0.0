Ext.define('erp.view.common.JProcess.LaunchContainer',{ 
	extend: 'Ext.panel.Panel', 
	hideBorders: true, 
	layout:'fit',
	alias: 'widget.erpLaunchContainer',
	BaseUtil:Ext.create('erp.util.BaseUtil'),
	FormUtil:Ext.create('erp.util.FormUtil'),
	initComponent : function(){
		this.items=this.getItems();
		this.callParent(arguments); 
	},
	getItems:function(){
		var items=new Array();
		var me=this;
		this.BaseUtil.getActiveTab().setLoading(true);
		Ext.Ajax.request({
			url : basePath + "common/getAllProcessInfo.action",
			params:{},
			method : 'post',
			async:false,
			callback : function(options,success,response){
				Ext.create('erp.util.BaseUtil').getActiveTab().setLoading(false);
				var localJson = new Ext.decode(response.responseText);
				if(localJson.exceptionInfo){
					showError(localJson.exceptionInfo);return;
				}
				if(localJson.success){
					var classify=localJson.classify;
					var deploy=localJson.deploy;
	                 if(deploy['0']){
	                	 items.push({
	 						xtype:'grid',
	 						title:'未分类流程',
	 						iconCls: 'main-msg',
	 						id:'grid0',
	 						tools: me.getTools(),
	 						height:300,
	 						layout:'fit',
	 						columns:me.getColumns(),
	 						requires: ['erp.view.core.grid.HeaderFilter'],
	 						plugins : [Ext.create('erp.view.core.grid.HeaderFilter')],
	 						store:Ext.create('Ext.data.Store', {
	 							fields :[{name:'jd_id',type:'int'},{name:'jd_processdefinitionname',type:'string'},{name:'js_formurl',type:'string'},{name:'jd_caller',type:'string'},{name:'jd_selfid',type:'int'}],
	 							data:deploy['0']
	 						})			
	 					});
	                 }					
					Ext.Array.each(classify,function(item){
						var data=deploy[item.JC_ID]?deploy[item.JC_ID]:[];
						if(data.length>0){
							items.push({
								xtype:'grid',
								iconCls: 'main-msg',
								title:'<div style="color:green;">'+item.JC_NAME+'</div>',
								id:'grid'+item.JC_ID,
								tools: me.getTools(),
								height:300,
								layout:'fit',
								columns:me.getColumns(),
								requires: ['erp.view.core.grid.HeaderFilter'],
		 						plugins : [Ext.create('erp.view.core.grid.HeaderFilter')],
								store:Ext.create('Ext.data.Store', {
									fields :[{name:'jd_id',type:'int'},{name:'jd_processdefinitionname',type:'string'},{name:'js_formurl',type:'string'},{name:'jd_caller',type:'string'}],
									data:data
								})			
							});
						}
					});    	
				} else {
					delFailure();
				}
			}
		});
		return items;
	},
	getTools: function(){
		var me=this;
		return [{xtype:'button',
			tooltip:'添加流程',
			height:15.3,
			width:15.3,
			hidden:canAdd==1,
			menu: [{
				iconCls: 'main-msg',
				text: '单表',
				handler: function(){
					if(Ext.getCmp('mainwin')){
						Ext.getCmp('mainwin').close();
					}
					var gridCondition="fd_foid=0";
					var win = parent.Ext.create('Ext.window.Window',
							{  
						id : 'singlewin',
						height : '90%',
						width : '90%',
						maximizable : true,
						buttonAlign : 'center',
						layout : 'anchor',
						title:'添加流程',
						items : [{
							frame : true,
							anchor : '100% 100%',
							layout : 'fit',
							html : '<iframe id="iframe_form" src="'+basePath+'jsps/oa/custom/customform.jsp?_noc=1'+'" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>'					
						 }],
						 listeners:{
						 	 	close:function(){
									window.location.reload();
								}
						 }
					    });
					win.show();
				}
			},{
				iconCls: 'main-msg',
				text: '主从表',
				handler:function(){
					if(Ext.getCmp('singlewin')){
						Ext.getCmp('singlewin').close();
					}
					var gridCondition="fd_foid=0";
					var win = parent.Ext.create('Ext.window.Window',
							{  
						id : 'mainwin',
						height : '90%',
						width : '90%',
						maximizable : true,
						buttonAlign : 'center',
						layout : 'anchor',
						title:'添加流程',
						items : [{
							//tag : 'iframe',
							frame : true,
							anchor : '100% 100%',
							layout : 'fit',
							// html : '<iframe id="iframe_form" src="'+basePath+'jsps/ma/form.jsp?_noc=1&gridCondition='+gridCondition+'&formCondition='+formCondition+'" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>'
							html:'<iframe id="iframe_form" src="'+basePath+'jsps/oa/custom/multiform.jsp?_noc=1'+'" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>'
						}],
						listeners:{
						 	 	close:function(){
									window.location.reload();
								}
						 }
					});
					win.show();

				}
			}],
			/*handler:function(e,target,panelHeader,tool){
    				var win = parent.Ext.create('Ext.window.Window',
    							{  
    								id : 'win',
    								height : 600,
    								width : 800,
    								maximizable : true,
    								buttonAlign : 'center',
    								layout : 'anchor',
    								items : [ {
    									//tag : 'iframe',
    									frame : true,
    									anchor : '100% 100%',
    									layout : 'fit',
    									// html : '<iframe id="iframe_form" src="'+basePath+'jsps/ma/form.jsp?_noc=1&gridCondition='+gridCondition+'&formCondition='+formCondition+'" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>'
    								    html:'<iframe id="iframe_form" src="'+basePath+'jsps/ma/form.jsp?_noc=1'+'" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>'
    								} ]

					});
					win.show();
        	       //Ext.create('erp.util.FormUtil').onAdd('添加流程','Form',basePath+'jsps/ma/form.jsp');
        	    }**/
		},/*{
			xtype:'tool',
			type:'up',
			handler:function(e,target,panelHeader,tool){
				if(tool.type=='up'){
					tool.setType('down');
					panelHeader.ownerCt.collapse(Ext.Component.DIRECTION_TOP,true);
				}else {
					tool.setType('up');
					panelHeader.ownerCt.expand();
				}

			},
		},*/{
			xtype: 'tool',
			type: 'collapse',
			tooltip:'已发起流程',
			hidden:canAdd==1,
			handler: function(e, target, panelHeader, tool){
				var portlet = panelHeader.ownerCt;
				var select=portlet.getSelectionModel().getLastSelected();
				if(!select){
					showError('请先选择需要查看的流程!');
				}else {
					openTable(select.data.jd_processdefinitionname,"jsps/common/datalist.jsp?whoami=JProcess&urlcondition=jp_flag=1 and jp_caller='"+select.data.jd_caller+"'",select.data.jd_caller);            
				}
			}
		},{
			xtype: 'tool',
			type: 'gear',
			tooltip:'流程排序',
			hidden:canAdd==1,
			handler: function(e, target, panelHeader, tool){
				var portlet = panelHeader.ownerCt;
				var jcid=portlet.id.substring(portlet.id.indexOf('grid')+4,portlet.id.length);
				var condition="jd_selfid="+jcid;
				var win = Ext.create('Ext.window.Window',
						{  
					title:'流程排序',
					id : 'orderwin',
					height : '60%',
					width : '45%',
					maximizable : true,
					buttonAlign : 'center',
					layout : 'anchor',
					items : [{
						//tag : 'iframe',
						frame : true,
						anchor : '100% 100%',
						layout : 'fit',
						// html : '<iframe id="iframe_form" src="'+basePath+'jsps/ma/form.jsp?_noc=1&gridCondition='+gridCondition+'&formCondition='+formCondition+'" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>'
						html:'<iframe id="iframe_form" src="'+basePath+'jsps/common/editorColumn.jsp?caller=JprocessDeploy&condition='+condition+'&_noc=1'+'" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>'
					}],
					buttonAlign:'center',
					buttons:[{
						xtype:'erpSaveButton',
						handler:function(){
							var grid = Ext.getCmp('orderwin').items.items[0].body.dom.getElementsByTagName('iframe')[0].contentWindow.Ext.getCmp("editorColumnGridPanel");
							var data = grid.getEffectData();                      
							if(data != null){
								grid.setLoading(true);
								Ext.Ajax.request({
									url : basePath + 'custom/orderByJprocess.action',
									params: {
										data: Ext.encode(data)
									},
									method : 'post',
									callback : function(options,success,response){
										grid.setLoading(false);
										var localJson = new Ext.decode(response.responseText);
										if(localJson.exceptionInfo){
											showError(localJson.exceptionInfo);
											return "";
										}
										if(localJson.success){
											if(localJson.log){
												showMessage("提示", localJson.log);
											}
											Ext.Msg.alert("提示", "处理成功!", function(){
												win.close();
												me.loadNewStore(portlet,condition);
											});
										}
									}
								});
							}
						}
					},{
						xtype:'erpCloseButton',
						handler:function(){
							win.close();
						}
					}]

						});
				win.show();
			}	
		},{
			xtype:'tool',
			type:'close',
			tooltip:'删除流程',
			hidden:canAdd==1,
			handler:function(e, target, panelHeader, tool){
				//me.onPortletClose(panelHeader.ownerCt);
				//panelHeader.ownerCt.close();
				var portlet = panelHeader.ownerCt;
				var select=portlet.getSelectionModel().getLastSelected();
				if(!select){
					showError('请先选择需要查看的流程!');
				}else {
					warnMsg('确认要删除该流程吗', function(btn){
						if(btn == 'yes'){
							me.BaseUtil.getActiveTab().setLoading(true);//loading...
							Ext.Ajax.request({
								url:basePath+'common/deleteProcessDeploy.action',
								params:{
									id:select.data.jd_id
								},
								method:'post',
								callback : function(options,success,response){
									me.BaseUtil.getActiveTab().setLoading(false);
									var localJson = new Ext.decode(response.responseText);
									if(localJson.exceptionInfo){
										showError(localJson.exceptionInfo);return;
									}
									if(localJson.success){
										delSuccess(function(){
											me.loadNewStore(portlet,"jd_selfid="+select.data.jd_selfid);						
										});//@i18n/i18n.js
									} else {
										delFailure();
									}
								}
							});
						}
					});
				}

			}
		}];
	},
	onPortletClose: function(portlet) {
		this.showMsg('"' + portlet.title + '" 已关闭!');
	},
	showMsg: function(msg) {
		var el = Ext.get('app-msg'),
		msgId = Ext.id();

		this.msgId = msgId;
		el.update(msg).show();

		Ext.defer(this.clearMsg, 3000, this, [msgId]);
	},

	clearMsg: function(msgId) {
		if (msgId === this.msgId) {
			Ext.get('app-msg').hide();
		}
	},
	loadNewStore:function(grid,condition){
		var me = this;
		grid.setLoading(true);//loading...
		Ext.Ajax.request({//拿到grid的columns
			url : basePath + "common/getProcessInfoByCondition.action",
			params: {
				condition:condition
			},
			method : 'post',
			callback : function(options,success,response){
				grid.setLoading(false);
				var res = new Ext.decode(response.responseText);
				if(res.exceptionInfo){
					showError(res.exceptionInfo);return;
				}
				var data = res.data;
				if(!data || data.length == 0){
					grid.store.removeAll();
					me.add10EmptyItems(grid);
				} else {        				
					grid.store.loadData(data);
				}
				//自定义event
				grid.addEvents({
					storeloaded: true
				});
				grid.fireEvent('storeloaded', grid, data);
			}
		});
	},
	getColumns:function(){
		var me=this;
		if(canAdd!=1){
			return [
			    {dataIndex:'jd_id',width:0,fixed:true},
				{dataIndex:'jd_processdefinitionname',renderer:open,header:'流程名称',width:150,flex:1,filter: {xtype: 'textfield', filterName: 'jd_processdefinitionname'},fixed:true},
				{xtype:'actioncolumn',width:50,fixed:true,items:[{
				icon: basePath+'/resource/images/icon/detail.png',  // Use a URL in the icon config
				tooltip: '查看列表',
				style: {
					marginLeft: '2px'
				},
				handler: function(grid, rowIndex, colIndex) {
					var rec = grid.getStore().getAt(rowIndex);
					//根据caller查找列表的配置
					Ext.Ajax.request({
						url:basePath+'/custom/IfDatalist.action',
						params:{
							caller:rec.data.jd_caller
						},
						method:'post',
						callback : function(options,success,response){
							var rs = new Ext.decode(response.responseText);
							if(rs.exceptionInfo){
								warnMsg('该单据未配置相关列表,是否配置列表?', function(btn){
									if(btn == 'yes'){
										me.BaseUtil.getActiveTab().setLoading(true);//loading...
										var type=me.FormUtil.contains(rec.data.js_formurl,'single',true)?'single':'multi';
										Ext.Ajax.request({
											url:basePath+'/custom/ToDatalistByForm.action',
											params:{
												caller:rec.data.jd_caller,
												type:type
											},
											method:'post',
											callback : function(options,success,response){
												me.BaseUtil.getActiveTab().setLoading(false);
												var localJson = new Ext.decode(response.responseText);
												if(localJson.exceptionInfo){
													showError(localJson.exceptionInfo);return;
												}
												if(localJson.success){
													Ext.Msg.alert('提示','配置成功!',function(){
														openTable(rec.data.jd_processdefinitionname,"jsps/common/datalist.jsp?whoami="+rec.data.jd_caller,rec.data.jd_caller);	   				
													});
												} else {
													delFailure();
												}
											}
										});
									}
								});
							}
							if(rs.success){
								openTable(rec.data.jd_processdefinitionname,"jsps/common/datalist.jsp?whoami="+rec.data.jd_caller,rec.data.jd_caller);	   				
							}
						}

					});
				}
			},{
				icon: basePath+'/resource/images/icon/execute.png', 
				tooltip: '查看流程',		
				style: {
					marginLeft: '2px',
					width:0,
				},
				handler: function(grid, rowIndex, colIndex) {
					var select= grid.getStore().getAt(rowIndex);;
					var formCondition="jd_idIS"+select.data.jd_id;
					openTable(select.data.jd_processdefinitionname,"jsps/common/jprocessDeploy.jsp?formCondition="+formCondition,select.data.jd_caller);
					// window.open();
				}
			},{
				icon: basePath+'/resource/images/icon/trash.png', 
				tooltip: '删除流程',
				style: {
					marginLeft: '2px'
				},
				handler: function(grid, rowIndex, colIndex) {
					var select= grid.getStore().getAt(rowIndex);;
					warnMsg('确认要删除该流程吗', function(btn){
						if(btn == 'yes'){
							me.BaseUtil.getActiveTab().setLoading(true);//loading...
							Ext.Ajax.request({
								url:basePath+'common/deleteProcessDeploy.action',
								params:{
									id:select.data.jd_id
								},
								method:'post',
								callback : function(options,success,response){
									me.BaseUtil.getActiveTab().setLoading(false);
									var localJson = new Ext.decode(response.responseText);
									if(localJson.exceptionInfo){
										showError(localJson.exceptionInfo);return;
									}
									if(localJson.success){
										delSuccess(function(){
											me.loadNewStore(grid,"jd_selfid="+select.data.jd_selfid);						
										});//@i18n/i18n.js
									} else {
										delFailure();
									}
								}
							});
						}
					});
				}   		            	
			}]},{dataIndex:'js_formurl',width:0,fixed:true},{dataIndex:'jd_caller',width:0,fixed:true},{dataIndex:'jd_selfid',width:0,fixed:true}];
		}else {
			return  [{dataIndex:'jd_id',width:0,fixed:true},{dataIndex:'jd_processdefinitionname',renderer:open,header:'流程名称',width:150,flex:1,filter: {xtype: 'textfield', filterName: 'jd_processdefinitionname'},fixed:true},{xtype:'actioncolumn',width:50,fixed:true,items:[{
				icon: basePath+'/resource/images/icon/detail.png',  // Use a URL in the icon config
				tooltip: '查看列表',
				style: {
					marginLeft: '2px'
				},
				handler: function(grid, rowIndex, colIndex) {
					var rec = grid.getStore().getAt(rowIndex);
					//根据caller查找列表的配置
					Ext.Ajax.request({
						url:basePath+'/custom/IfDatalist.action',
						params:{
							caller:rec.data.jd_caller
						},
						method:'post',
						callback : function(options,success,response){
							var rs = new Ext.decode(response.responseText);
							if(rs.exceptionInfo){
								warnMsg('该单据未配置相关列表,是否配置列表?', function(btn){
									if(btn == 'yes'){
										me.BaseUtil.getActiveTab().setLoading(true);//loading...
										var type=me.FormUtil.contains(rec.data.js_formurl,'single',true)?'single':'multi';
										Ext.Ajax.request({
											url:basePath+'/custom/ToDatalistByForm.action',
											params:{
												caller:rec.data.jd_caller,
												type:type
											},
											method:'post',
											callback : function(options,success,response){
												me.BaseUtil.getActiveTab().setLoading(false);
												var localJson = new Ext.decode(response.responseText);
												if(localJson.exceptionInfo){
													showError(localJson.exceptionInfo);return;
												}
												if(localJson.success){
													Ext.Msg.alert('提示','配置成功!',function(){
														openTable(rec.data.jd_processdefinitionname,"jsps/common/datalist.jsp?whoami="+rec.data.jd_caller,rec.data.jd_caller);	   				
													});
												} else {
													delFailure();
												}
											}
										});
									}
								});
							}
							if(rs.success){
								openTable(rec.data.jd_processdefinitionname,"jsps/common/datalist.jsp?whoami="+rec.data.jd_caller,rec.data.jd_caller);	   				
							}
						}

					});
				}
			}]},{dataIndex:'js_formurl',width:0,fixed:true},{dataIndex:'jd_caller',width:0,fixed:true},{dataIndex:'jd_selfid',width:0,fixed:true}];
			
		}
		
		
		
		
	}
});