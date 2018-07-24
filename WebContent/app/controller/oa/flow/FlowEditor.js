Ext.QuickTips.init();
Ext.define('erp.controller.oa.flow.FlowEditor', {
	extend : 'Ext.app.Controller',
	BaseUtil : Ext.create('erp.util.BaseUtil'),
	FormUtil : Ext.create('erp.util.FormUtil'),
	views : [ 'oa.flow.flowEditor.FlowNodeEditor','core.trigger.TextAreaTrigger',
			  'oa.flow.flowEditor.FlowDeriveEditor','oa.flow.flowEditor.FlowIdeaEditor',
			  'oa.flow.flowEditor.FlowCommitEditor','oa.flow.flowEditor.FlowJudgeEditor'],
	init : function() {
		var me = this;
		this.control({
			'#like':{
				expand:function(c){//首次加载时 根据是否展开过判断 展开过则可以动态加载 否则需要使用store替换
					if(c.isFirstStore){
						c.isFirstExpand = true
					}
				},
				focus:function(c){
					//根据shortName改变参照store
					var value = Ext.getCmp('FD_SHORTNAME').value;
					var newSelectData = me.getSelectTab(value);
			    	if(c.isFirstStore&&c.isFirstExpand){
			    		c.store.loadData(newSelectData);
			    	}else{
			    		var store = Ext.create('Ext.data.Store',{
							fields: ['name','shortName'],
							data: newSelectData
						})
						c.store = store;
						c.isFirstStore = true
						c.isFirstExpand = false
			    	}
				},
				change:function(c,newValue,oldValue){
					//初始化字段
					if(c.lastSelection.length>0){
						var fd_name = Ext.getCmp('FD_NAME');
						var newCaller = fd_name.lastSelection[0].data.FD_CALLER;
						var copyAllField = me.getAllData(newCaller);
						var flowShortName = Ext.getCmp('FD_SHORTNAME').value;
						//加载参照页面字段
						var selectTab = new Array();
						Ext.Ajax.request({
							url : basePath + 'oa/flow/getSelectTab.action',
							async:false,
							params:{
								shortName:flowShortName==null||''==flowShortName?shortName:flowShortName,
								groupName:newValue,
								caller:newCaller
							},
							callback : function(options,success,response){
								var rs = new Ext.decode(response.responseText);
								if(rs.exceptionInfo){
									showError(rs.exceptionInfo);return;
								}
								if(rs.groups.length>0){
									Ext.Array.each(rs.groups, function(item){
										var isNew = item.FGC_NEW?item.FGC_NEW:false
										if(isNew=='true'){isNew = true}else if(isNew=='false'){isNew = false}
										var main = item.FGC_REQUIREDFIELD?item.FGC_REQUIREDFIELD:false
										if(main=='true'){main = true}else if(main=='false'){main = false}
										var read = item.FGC_READ?item.FGC_READ:false
										if(read=='true'){read = true}else if(read=='false'){read = false}
										selectTab.push({
											logic: item.FD_LOGICTYPE,
											fgc_role : item.FGC_ROLE,
											fgc_rolecode : item.FGC_ROLECODE,
											text : item.FD_CAPTION,
											field : item.FGC_FIELD,
											main : main,
											isNew : isNew,
											read : read,
											columnsWidth : item.FGC_WIDTH
										});
									});
								}
							}
						});
						//剔除重复Group
						var toStroe = Ext.getCmp('togrid').store;
						var fromStore = Ext.getCmp('fromgrid').store;
						var allData = copyAllField.concat();
						var nowData = selectTab;
						if(allData.length>0&&nowData.length>0){
							Ext.Array.each(nowData, function(b_item){
								Ext.Array.each(allData, function(a_item,index){
									if(a_item&&a_item.field==b_item.field){
										allData.splice(index,1)
										return;
									}
								});
							});
						}
						//刷新usingGroups allGroups
						toStroe.loadData(selectTab);
						fromStore.loadData(allData);
					}
				}
			},
			'#FD_NAME':{
				change:function(c,nowValue,oldValue){
					var fd_shortname = Ext.getCmp('FD_SHORTNAME');
					var fn_nodename = Ext.getCmp('FN_NODENAME');
					fd_shortname.setValue('');
					fn_nodename.setValue('');
					//根据caller改变所有字段
					if(c.lastSelection.length>0){
						var newCaller = c.lastSelection[0].data.FD_CALLER;
						var newAllData = me.getAllData(newCaller);
						Ext.getCmp('fromgrid').store.loadData(newAllData);
						//清除使用字段
						Ext.getCmp('togrid').store.removeAll();
					}
				}
			},
			'#FD_SHORTNAME':{
				expand:function(c){//首次加载时 根据是否展开过判断 展开过则可以动态加载 否则需要使用store替换
					if(c.isFirstStore){
						c.isFirstExpand = true
					}
				},
				focus:function(c){
					var value = Ext.getCmp('FD_NAME').value;
					var com = new Array();
					Ext.Ajax.request({
						url : basePath + 'common/getFieldsDatas.action',
						async: false,
						params: {
							caller: 'flow_define',
							fields: 'fd_shortname',
							condition: "fd_name='"+value+"' and fd_parentid<>0 and fd_status='enable' order by fd_id"
						},
						callback : function(opt, s, res){
							var r = new Ext.decode(res.responseText);
							if(r.exceptionInfo){
								showError(r.exceptionInfo);return;
							}
							if (r.success && r.data) {
								data = Ext.decode(r.data);
							}
						}
					});
					for(var i=0;i<data.length;i++){
			    		com.push(data[i]);
			    	}
			    	if(c.isFirstStore&&c.isFirstExpand){
			    		c.store.loadData(com);
			    	}else{
			    		var store = Ext.create('Ext.data.Store',{
							fields:['FD_SHORTNAME'],
							data: com
						})
						c.store = store;
						c.isFirstStore = true
						c.isFirstExpand = false
			    	}
			    	
				},
				change:function(c){
					var fn_nodename = Ext.getCmp('FN_NODENAME');
					fn_nodename.setValue('');
					//清除参照
					var like = Ext.getCmp('like');
					like.setValue('');
					//查询目标流程使用字段
					var fd_name = Ext.getCmp('FD_NAME');
					if(fd_name.lastSelection.length>0){
						var flowCaller = fd_name.lastSelection[0].raw.FD_CALLER;
						var nowData = me.getNowData(c.value,name,flowCaller);
						Ext.getCmp('togrid').store.loadData(nowData);
					}
				}
			},
			'#FN_NODENAME':{
				expand:function(c){//首次加载时 根据是否展开过判断 展开过则可以动态加载 否则需要使用store替换
					if(c.isFirstStore){
						c.isFirstExpand = true
					}
				},
				focus:function(c){
					var value = Ext.getCmp('FD_SHORTNAME').value;
					var com = new Array();
					Ext.Ajax.request({
						url : basePath + 'common/getFieldsDatas.action',
						async: false,
						params: {
							caller: 'flow_node',
							fields: 'FN_NODENAME,FN_ID',
							condition: "fn_fdshortname='"+value+"' order by fn_id"
						},
						callback : function(opt, s, res){
							var r = new Ext.decode(res.responseText);
							if(r.exceptionInfo){
								showError(r.exceptionInfo);return;
							}
							if (r.success && r.data) {
								data = Ext.decode(r.data);
							}
						}
					});
					for(var i=0;i<data.length;i++){
			    		com.push(data[i]);
			    	}
			    	if(c.isFirstStore&&c.isFirstExpand){
			    		c.store.loadData(com);
			    	}else{
			    		var store = Ext.create('Ext.data.Store',{
							fields:['FN_NODENAME','FN_ID'], 
							data: com
						})
						c.store = store;
						c.isFirstStore = true
						c.isFirstExpand = false
			    	}
			    	
				}
			}
		});
	},
	getAllData:function(flowCaller){
		var allField = new Array();
		//获取所有字段
		var s = '(select fo_id from form where fo_caller = \''+flowCaller + '\') order by fd_detno ';
		Ext.Ajax.request({
			url : basePath + 'common/getFieldsDatas.action',
			async: false,
			params:{
				fields : 'fd_caption,fd_field,fd_logictype',
				caller : 'formdetail',
				condition : 'fd_foid = ' + s
			},
			callback : function(options,success,response){
				var rs = new Ext.decode(response.responseText);
				if(rs.exceptionInfo){
					showError(rs.exceptionInfo);return;
				}
				if(rs.data.length>0){
					Ext.Array.each(Ext.decode(rs.data), function(item){
						allField.push({
							text:item.FD_CAPTION,
							field:item.FD_FIELD,
							logic:item.FD_LOGICTYPE
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
	//根据条件读取已加载字段
	getNowData:function(defineShortName,name,flowCaller){
		var nowFields = new Array();
		Ext.Ajax.request({
			url : basePath + 'common/getFieldsDatas.action',
			async: false,
			params:{
				fields : 'FGC_ROLE,FGC_ROLECODE,FGC_ID,FD_CAPTION,FGC_FIELD,FGC_NEW,FGC_REQUIREDFIELD,FGC_READ,FGC_WIDTH,FT_TO,FT_TONAME',
				caller : ' flow_groupconfig left join flow_operation on fo_groupname = fgc_groupname and fo_fdshortname = fgc_fdshortname '+
						 ' left join (select ft_foid,ft_from,ft_to,ft_caller,fd_caption as ft_toname from flow_transfer left join formdetail on fd_field = ft_to '+
						 ' where fd_foid = (select fo_id from form where fo_caller = \''+flowCaller+'\')) ft on ft.ft_foid = fo_id and ft.ft_from = fgc_field '+
						 ' left join formdetail fd on fd.fd_field = fgc_field ',
				condition : 'fgc_fdshortname = \''+ defineShortName +'\' and fgc_groupname = \''+ name +'\'' +
						    ' and fd.fd_foid = (select fo_id from form where fo_caller = \''+flowCaller+'\')'
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
							logic:item.FD_LOGICTYPE,
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
							transferName:item.FT_TONAME
						});
					});
				}
			}
		});
		return nowFields;
	}
});