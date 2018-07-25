Ext.define('erp.view.oa.flow.flowEditor.FlowDeriveEditor',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	requires:['erp.view.oa.flow.flowEditor.flowGrid.FlowDeriveGrid'],
	initComponent : function(){
		var me = this; 
		//初始化数据
		var remark;
		var define;
		var defineShortName;
		var flowNodeName;
		var flowCaller;//派生目标caller
		var GroupName;//分组名
		var flowNodeId;//目标节点ID
		var nodeId;
		Ext.Ajax.request({
			url : basePath + 'common/getFieldsDatas.action',
			async: false,
			params:{
				fields : 'fo_flownodename,fo_remark,fo_flowname,fn_nodename,fn_id,fo_flowcaller,fo_groupname,fo_flownodeid',
				caller : 'flow_operation a left join flow_node b on a.fo_flownodeid = b.fn_id',
				condition : 'a.fo_fdshortname = \''+ shortName +'\' and a.fo_name = \''+ name +'\''
			},
			callback : function(options,success,response){
				var rs = new Ext.decode(response.responseText);
				if(rs.exceptionInfo){
					showError(rs.exceptionInfo);return;
				}
				var rs = Ext.decode(rs.data);
				if(rs.length>0){
					remark = rs[0].FO_REMARK
					define = rs[0].FO_FLOWNAME?rs[0].FO_FLOWNAME.split('-')[0]:null
					defineShortName = rs[0].FO_FLOWNAME
					flowNodeName = rs[0].FO_FLOWNODENAME
					flowCaller = rs[0].FO_FLOWCALLER
					GroupName = rs[0].FO_GROUPNAME
					flowNodeId = rs[0].FO_FLOWNODEID
					nodeId = rs[0].FN_ID;
				}
			}
		});
		//初始化目标流程所有字段
		var allData = me.getAllData(flowCaller);
		//初始化使用中字段
		var nowData = me.getNowData(defineShortName,GroupName,flowCaller);
		//用作删除字段模板
		var deleteField = nowData.concat();
		//剔除重复field
		if(nowData.length>0&&allData.length>0){
			Ext.Array.each(nowData, function(b_item){
				Ext.Array.each(allData, function(a_item,index){
					if(a_item&&a_item.field==b_item.field){
						allData.splice(index,1)
						return;
					}
				});
			});
		}
		
		//初始化参照页面
		var allSelect = me.getSelectTab(defineShortName);
		//初始化本地流程所有字段 用于映射
		var localData = me.getLocalData(caller);
		Ext.apply(me, { 
			items: [{ 
				id:'floweditor',
				xtype:'panel',
				layout: 'fit', 
				cls:'floweditor',
				bbar: ['->',{
					xtype:'button',
					cls:'x-btn-gray',
					text:'保存',
					margin:'0 0 0 2',
					handler:function(){
						var DeriveName = Ext.getCmp('FD_NAME');
						if(type=='Task'){
							var grid = Ext.getCmp('messageTransfer');
							var s = grid.getStore().data.items;//获取store里面的数据
							var transferData = new Array();
							for(var i=0;i<s.length;i++){//将grid里面各行的数据获取并拼成jsonGridData
								if(s[i].dirty||(s[i].data.traName!=null&&s[i].data.traName!='')){
									var data = s[i].data;
									dd = new Object();
									dd['ft_from'] = data.traName;
									dd['ft_to'] = data.candName;
									dd['ft_id'] = data.traId;
									dd['ft_foname'] = name;
									transferData.push(dd);
								}
							}
							bb = new Object();
							bb['operationName'] = name;
							bb['operationType'] = type;
							bb['nodeId'] = fromId;
							bb['nodeName'] = fromNodeName;
							bb['remark'] = name;
							
							Ext.Ajax.request({
								url:basePath + 'oa/flow/saveDerive.action',
								params: {
									deriveData:JSON.stringify(transferData),
									baseMessage:JSON.stringify(bb),
									caller:caller,
									shortName:shortName
								},
								callback : function(opt, s, res){
									var r = new Ext.decode(res.responseText);
									if(r.exceptionInfo){
										showError(r.exceptionInfo);return;
									}
									if (r.success) {
										showInformation("保存派生任务操作成功！", function(btn){
											window.location.reload();
										});
									}
								}
							})
						}else if(type=='Flow'){
							var flowDeriveGrid = Ext.getCmp('flowDeriveGrid');
							//剔除重复Group
							var toStroe = Ext.getCmp('togrid').store;
							var deleteField = flowDeriveGrid.deleteField;
							var deleteArray = new Array();
							var toItems = toStroe.data.items;
							if(toItems.length<1){
								showInformation('至少选中一个字段', function(btn){});
								return;
							}
							//校验责任人 标题字段必须要填写
							var haveDuty;
							var haveTitle;
							Ext.Array.each(toItems, function(item){
								if(item.data.fgc_rolecode=='duty'){
									haveDuty = true
								}
							});
							if(!haveDuty){
								showInformation('责任人字段必须要填写', function(btn){});
								return;
							}
							if(toItems.length>0&&deleteField.length>0){
								Ext.Array.each(toItems, function(b_item){
									Ext.Array.each(deleteField, function(a_item,index){
										if(a_item&&a_item.field==b_item.data.field){
											deleteField.splice(index,1);
											return false;
										}
									});
								});
							}
							var FD_NAME = Ext.getCmp('FD_NAME');
							var FD_SHORTNAME = Ext.getCmp('FD_SHORTNAME');
							var FN_NODENAME = Ext.getCmp('FN_NODENAME');
							if(!FD_NAME.value||!FD_SHORTNAME.value||!FN_NODENAME.value){
								showInformation('请填写目标流程必填字段', function(btn){});
								return;
							}
							var groupName = Ext.getCmp('groupName');
							if(!groupName.value||groupName.value.trim().length<1){
								showInformation('请填写正确的分组名', function(btn){});
								return;
							}
							var FN_ID;
							if(FN_NODENAME.valueModels.length>0){
								FN_ID = FN_NODENAME.valueModels[0].data.FN_ID;
							}else{
								FN_ID = FN_NODENAME.flowNodeId;
							}
							dd = new Object();
							dd['operationName'] = name;
							dd['operationType'] = type;
							dd['nodeId'] = fromId;
							dd['nodeName'] = fromNodeName;
							dd['remark'] = name;
							dd['flowName'] = FD_SHORTNAME.value;
							dd['flowNodeName'] = FN_NODENAME.value;
							dd['flowNodeId'] = FN_ID ;
							dd['groupName'] = groupName.value;
							
							deriveData = new Array();
							
							//将映射分离出来
							nowIems = new Array();
							Ext.Array.each(toItems,function(i,index){
								ddItem = new Object();
								var idata = i.data;
								if(idata.transferName!=null && idata.transferName!=''){
									ddItem['ft_id'] = idata.transferId;
									ddItem['ft_from'] = idata.field;
									ddItem['ft_to'] = idata.transfer;
									ddItem['ft_foname'] = name;
									deriveData.push(ddItem);
								}
								delete toItems[index].data.transferId;
								delete toItems[index].data.transfer;
								delete toItems[index].data.transferName;
								nowIems.push(i.data);
							});
							groupData = new Object();
							groupData['nowItems'] = nowIems;
							groupData['deleteItems'] = deleteField;
							Ext.Ajax.request({
								url:basePath + 'oa/flow/saveDerive.action',
								params: {
									baseMessage:JSON.stringify(dd),
									caller:caller,
									deriveData:JSON.stringify(deriveData),
									groupData:JSON.stringify(groupData),
									shortName:shortName
								},
								callback : function(opt, s, res){
									var r = new Ext.decode(res.responseText);
									if(r.exceptionInfo){
										showError(r.exceptionInfo);return;
									}
									if (r.success) {
										showInformation("保存派生流程操作成功！", function(btn){
											window.location.reload();
										});
									}
								}
							});
						}
					}
				},{xtype:'splitter',width:10},{
					xtype:'button',
					cls:'x-btn-gray',
					text:'关闭',
					margin:'0 5 0 0',
					handler:function(){
						var errInfo = '是否继续关闭？';
						warnMsg(errInfo, function(btn){
							if(btn == 'yes'){
								parent.Ext.getCmp('operationEdit').close()
							} else {
								return;
							}
						});
					}
				},'->'],
				items: [{
					xtype: 'panel',
					id:'flow_groupconfig',
					layout: 'column', 
					items:[{
						fieldLabel: '名称',
						name: '名称',
						columnWidth:0.3333,
						xtype:'textfield',
						labelAlign: 'left',
						readOnly:true,
						editable:false,
						value:name,
						cls: "form-field-allowBlank"
					},{
						fieldLabel: '分组名称',
						id: 'groupName',
						hidden:type=='Task'?true:false,
						columnWidth:0.3333,
						xtype:'textfield',
						labelAlign: 'left',
						editable:true,
						readOnly:false,
						value:GroupName,
						cls: "form-field-allowBlank",
						baseValue:GroupName,
						listeners:{
							blur:function(t){
								var store = Ext.getCmp('like').store;
								Ext.Array.each(store.data.items,function(item){
									if(t.baseValue!=item.data.name){
										if(t.value==item.data.name){
											showInformation("该分组已存在", function(btn){});
											t.setValue(t.baseValue);
										}
									}
								});
							}
						}
					},{
						fieldLabel: '描述',
						id: 'remark',
						readOnly:false,
						value:remark,
						xtype: "textareatrigger",
					    maxLength: 300,
				        maxLengthText: "字段长度不能超过300字符!",
				        hideTrigger: false,
				        editable: true,
				        columnWidth:type=='Task'?0.66664:0.3334,
				        allowBlank: true,
				        cls: "form-field-allowBlank",
				        labelAlign: "left",
				        allowDecimals: true
					},{
						hidden:type=='Task'?true:false,
						fieldLabel: '参照',
						id:'like',
						allowDecimals:true,
						columnWidth: 1,
						hideTrigger:false,
						cls: "form-field-allowBlank",
						labelAlign:"left",
						maxLength:50,
						maxLengthText:"字段长度不能超过50字符!",
						readOnly:false,
						xtype:"combobox",
						editable: false,
						store: Ext.create('Ext.data.Store',{
							fields: ['name','shortName'],
							data: allSelect
						}),
						displayField: 'name',
						valueField: 'name'
					},{   	
						columnWidth: 1,
		            	xtype:'form',
		            	layout:'column',
		            	id:'flowDriverGrid',
		            	style:'border:none',
		            	height:window.innerHeight - 65,
		            	items: [{
		            		cls: "form-field-allowBlank",
		            		hideTrigger:type=='Task'?true:false,
		            		fieldLabel: '派生类型<span style="color:red;">*</span>',
		            		name: 'FD_NAME',
		            		allowBlank: false,
		            		editable:false,
		            		readOnly:type=='Task'?true:false,
		            		xtype:'combo',
		            		columnWidth:type=='Task'?1:0.3333,
		            		displayField: 'FD_NAME',
		            		valueField: 'FD_NAME',
		            		id:'FD_NAME',
							store:type=='Task'?[]:me.getStore('type'),
							value:type=='Task'?'任务':define,
							listeners:{
								beforerender:function(c){
									if(!c.value){
										c.setValue('请选择')
									}
								}
							}
		            	},{
		            		cls: "form-field-allowBlank",
		            		editable:false,
		            		hidden:type=='Task'?true:false,
		            		columnWidth:0.3333,
		            		fieldLabel: '流程实例<span style="color:red;">*</span>',
		            		xtype:'combo',
		            		displayField: 'FD_SHORTNAME',
		            		valueField: 'FD_SHORTNAME',
		            		id:'FD_SHORTNAME',
							store:me.getStore('define'),
							value:defineShortName,
							listeners:{
								beforerender:function(c){
									if(c.value){
										c.show();
									}
								}
							}
		            	},{
		            		cls: "form-field-allowBlank",
		            		editable:false,
		            		hidden:type=='Task'?true:false,
		            		columnWidth:0.3333,
		            		fieldLabel:'流程转到<span style="color:red;">*</span>',
		            		xtype:'combo',
		            		displayField: 'FN_NODENAME',
		            		valueField: 'FN_NODENAME',
		            		id:'FN_NODENAME',
		            		store:me.getStore('turn'),
		            		value:flowNodeName,
		            		flowNodeId:flowNodeId,
							listeners:{
								beforerender:function(c){
									if(c.value){
										c.show();
									}
								}
							}
		                },{
		                	hidden:type=='Task'?false:true,
			            	title:'信息映射',
			            	height:window.innerHeight - 85,
			            	columnWidth: 1,
							xtype:'grid',
				    		autoScroll:true,
				    		height:190,
				    		id:'messageTransfer',
				    		plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
				            	 clicksToEdit: 1
				             })],
							columns:[{
								align:'left',
								style:'text-align:center;',
								text:'候选字段',
								dataIndex:'candField',
								readOnly:true,
								flex:0.3
							},{
								text:'候选字段缩写',
								dataIndex:'candName',
								width:0
							},{
								text:'信息映射规则',
								dataIndex: 'traField',
								flex:0.7,
								id:'traField',
								editable:true,
								editor:{
									 editable:false,
			            			 xtype:'combo',
			            			 queryMode: 'local',
			            			 displayField: 'FD_CAPTION',
			            			 valueField: 'FD_CAPTION',
			            			 store:me.getStore('traField')
			            		 },
			            		 renderer:function(sel,mata,record){
		            				 if(sel!='空' && sel!=null && ''!=sel){
		            					 var traField = Ext.getCmp('traField');
		            					 //第一次触发时
		            					 var items;
		            					 if(traField.editor){
		            						 items = traField.editor.store.data.items; 
		            					 }
		            					 //第二次触发
		            					 if(traField.field){
		            						 items = traField.field.store.data.items; 
		            					 }
		            					 Ext.Array.each(items,function(item){
		            						 if(sel==item.data.FD_CAPTION){
		            							 record.data.traName = item.data.FD_FIELD;
		            						 }
		            					 });
		            				 }else if(sel=='空'||sel==null||''==sel){
		            					record.data.traName = null;
		            					record.data.traId = null;
		            				 }
		            				 return sel;
		            			 }
							},{
								text:'信息映射规则字段缩写',
								dataIndex: 'traName',
								id:'traName',
								width:0
							},{
								text:'信息映射规则字段ID',
								dataIndex: 'traId',
								id:'traId',
								width:0
							}],
							store: Ext.create('Ext.data.Store',{
								fields: ['candField','candName','traField','traName','traId'],
								data: me.getStore('transferData')
							})
					 	},{
					 		localData:localData,
					 		hidden:type=='Task'?true:false,
					 		nowFields:nowData,
							allFields:allData,
							height:window.innerHeight - 110,
							columnWidth: 1,
							id:'flowDeriveGrid',
							deleteField:deleteField,
							xtype:'FlowDeriveGrid'
					 	}]
		             }]
		          }]
			  }]
		});
		me.callParent(arguments);
	},
	getStore:function(type){//根据类型加载store
		if(type=='type'){
			var com = new Array();
			var data;
			Ext.Ajax.request({
				url : basePath + 'oa/flow/getDefine.action',
				async: false,
				params: {
					caller: ''
				},
				method : 'post',
				callback : function(opt, s, res){
					var r = new Ext.decode(res.responseText);
					if(r.exceptionInfo){
						showError(r.exceptionInfo);return;
					}
					if (r.success && r.data) {
						data = r.data;
					}
				}
			});
			for(var i=0;i<data.length;i++){
	    		com.push(data[i]);
	    	}
			var store = Ext.create('Ext.data.Store', {
				 fields:['FD_NAME','FD_CALLER'],   
				 data:com
			});
			return store;
		}else if(type=='define'){
			var store = Ext.create('Ext.data.Store', {
				 fields:['FD_SHORTNAME'],   
				 data:[]
			});
			return store;
		}else if (type=='turn'){
			var store = Ext.create('Ext.data.Store', {
				 fields:['FN_NODENAME','FN_ID'],   
				 data:[]
			});
			return store;
		}else if(type=='traField'){
			var com = new Array();
			var data;
			Ext.Ajax.request({
				url : basePath + 'oa/flow/getTransferField.action',
				async: false,
				params: {
					fromId:fromId,
					fdid:fd_id,
					shortName:shortName
				},
				callback : function(opt, s, res){
					var r = new Ext.decode(res.responseText);
					if(r.exceptionInfo){
						showError(r.exceptionInfo);return;
					}
					if (r.success && r.data) {
						data = r.data;
					}
				}
			});
			if(data!=null){
				for(var i=0;i<data.length;i++){
		    		com.push(data[i]);
		    	}
			}
	    	com.push({
	    		FD_CAPTION:'空',
	    		FD_FIELD:null
	    	});
			var store = Ext.create('Ext.data.Store', {
				 fields:['FD_CAPTION','FD_FIELD'],   
				 data:com
			});
			return store;
		}else if(type=='transferData'){
			var data = new Array();
			Ext.Ajax.request({
				url:basePath + 'common/getFieldsDatas.action',
				async: false,
				params:{
					fields : 'ft_id,ft_from,ft_to,fd_caption',
					caller : 'flow_transfer left join flow_operation on fo_id=ft_foid left join formdetail on ft_from = fd_field',
					condition : "fo_name='"+name+"' and fo_fdshortname='"+shortName+"' and ft_caller='DriverTask' and fd_foid=(select fo_id from form where fo_caller='"+caller+"')"
				},
				callback : function(options,success,response){
					var rs = new Ext.decode(response.responseText);
					if(rs.exceptionInfo){
						showError(rs.exceptionInfo);return;
					}
					var rs = Ext.decode(rs.data);
					data = rs;
				}
			});
			var canData = [
				{candField:'任务名称',candName:'name',traField:'',traName:'',traId:''},
				{candField:'开始时间',candName:'startdate',traField:'',traName:'',traId:''},
				{candField:'结束时间',candName:'enddate',traField:'',traName:'',traId:''},
				{candField:'执行人',candName:'recorder',traField:'',traName:'',traId:''},
				{candField:'任务描述',candName:'description',traField:'',traName:'',traId:''}
			];
			Ext.Array.each(canData,function(c){
				Ext.Array.each(data,function(d){
					if(c.candName==d.FT_TO){
						c.traName = d.FT_FROM;
						c.traField = d.FD_CAPTION;
						c.traId = d.FT_ID;
					}
				});
			});
			return canData;
		}
	},
	getLocalData:function(caller){
		var allField = new Array();
		//获取本地所有字段
		var s = '(select fo_id from form where fo_caller = \''+caller + '\') order by fd_detno ';
		Ext.Ajax.request({
			url : basePath + 'common/getFieldsDatas.action',
			async: false,
			params:{
				fields : 'fd_caption,fd_field',
				caller : 'formdetail',
				condition : 'fd_foid = ' + s
			},
			callback : function(options,success,response){
				var rs = new Ext.decode(response.responseText);
				if(rs.exceptionInfo){
					showError(rs.exceptionInfo);return;
				}
				if(rs.data.length>2){
					Ext.Array.each(Ext.decode(rs.data), function(item){
						allField.push({
							display:item.FD_CAPTION,
							value:item.FD_FIELD
						});
					});
				}
			}
		});
		return allField;
	},
	getAllData:function(flowCaller){
		var allField = new Array();
		//获取所有字段
		var s = '(select fo_id from form where fo_caller = \''+flowCaller + '\') order by fd_detno ';
		Ext.Ajax.request({
			url : basePath + 'common/getFieldsDatas.action',
			async: false,
			params:{
				fields : 'fd_caption,fd_field,FD_LOGICTYPE',
				caller : 'formdetail',
				condition : 'fd_foid = ' + s
			},
			callback : function(options,success,response){
				var rs = new Ext.decode(response.responseText);
				if(rs.exceptionInfo){
					showError(rs.exceptionInfo);return;
				}
				if(rs.data.length>2){
					Ext.Array.each(Ext.decode(rs.data), function(item){
						allField.push({
							text:item.FD_CAPTION,
							field:item.FD_FIELD,
							logic: item.FD_LOGICTYPE
						});
					});
				}
			}
		});
		return allField;
	},
	getSelectTab:function(shortName){
		//获取参照页面
		var GroupStore = new Array();
		Ext.Ajax.request({
			url : basePath + 'common/getFieldsDatas.action',
			async: false,
			params:{
				fields : 'fgc_groupname,min(fgc_id)',
				caller : 'flow_groupconfig',
				condition : 'fgc_fdshortname = \''+ shortName +'\' group by fgc_groupname order by min(fgc_id)'
			},
			callback : function(options,success,response){
				var rs = new Ext.decode(response.responseText);
				if(rs.exceptionInfo){
					showError(rs.exceptionInfo);return;
				}
				Ext.Array.each(Ext.decode(rs.data), function(item){
					GroupStore.push({
						name:item.FGC_GROUPNAME,
						shortName:shortName
					});
				});
			}
		});
		return GroupStore;
	},
	//读取已加载字段
	getNowData:function(defineShortName,name,flowCaller){
		var nowFields = new Array();
		Ext.Ajax.request({
			url : basePath + 'common/getFieldsDatas.action',
			async: false,
			params:{
				fields : 'FD_LOGICTYPE,FGC_ID,FGC_ROLE,FGC_ROLECODE,FD_CAPTION,FGC_FIELD,FGC_NEW,FGC_REQUIREDFIELD,FGC_READ,FGC_WIDTH,FT_TO,FT_TONAME,FT_ID',
				caller : ' flow_groupconfig left join flow_operation on fo_groupname = fgc_groupname and fo_fdshortname = fgc_fdshortname '+
						 ' left join (select ft_foid,ft_from,ft_to,ft_id,ft_caller,fd_caption as ft_toname from flow_transfer left join formdetail on fd_field = ft_to '+
						 ' where fd_foid = (select fo_id from form where fo_caller = \''+flowCaller+'\')) ft on ft.ft_foid = fo_id and ft.ft_from = fgc_field '+
						 ' left join formdetail fd on fd.fd_field = fgc_field ',
				condition : 'fgc_fdshortname = \''+ defineShortName +'\' and fgc_groupname = \''+ name +'\'' +
						    ' and fd.fd_foid = (select fo_id from form where fo_caller = \''+flowCaller+'\') order by fgc_id'
			},
			callback : function(options,success,response){
				var rs = new Ext.decode(response.responseText);
				if(rs.exceptionInfo){
					showError(rs.exceptionInfo);return;
				}
				if(rs.data.length>2){
					Ext.Array.each(Ext.decode(rs.data), function(item){
						var isNew = item.FGC_NEW?item.FGC_NEW:false
						if(isNew=='true'){isNew = true}else if(isNew=='false'){isNew = false}
						var main = item.FGC_REQUIREDFIELD?item.FGC_REQUIREDFIELD:false
						if(main=='true'){main = true}else if(main=='false'){main = false}
						var read = item.FGC_READ?item.FGC_READ:false
						if(read=='true'){read = true}else if(read=='false'){read = false}
						nowFields.push({
							logic: item.FD_LOGICTYPE,
							fgc_role : item.FGC_ROLE,
							fgc_rolecode : item.FGC_ROLECODE,
							fgc_id : item.FGC_ID,
							text : item.FD_CAPTION,
							field : item.FGC_FIELD,
							main : main,
							isNew : isNew,
							read : read,
							columnsWidth : item.FGC_WIDTH,
							transfer:item.FT_TO,
							transferName:item.FT_TONAME,
							transferId:item.FT_ID
						});
					});
				}
			}
		});
		return nowFields;
	}
});