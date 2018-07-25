Ext.define('erp.view.oa.flow.flowEditor.FlowNodeEditor',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	requires:['erp.view.oa.flow.flowEditor.flowGrid.FlowNodeGrid'],
	hideBorders: true, 
	initComponent : function(){
		var me = this; 
		//获取当前节点的来源派生信息
		var flowData = me.getFlowData(shortName,name);
		//取得当前页面和所有页面
		var o = me.getNowData(shortName,name);
		var nowData = o.usingGroups;
		var remark = o.remark;
		var allData = me.getAllData(shortName);
		if(!nowData){
			nowData = new Array();
		}
		if(!allData){
			allData = new Array();
		}
		//剔除重复页面
		if(allData.length>0&&nowData.length>0){
			Ext.Array.each(nowData, function(b_item){
				Ext.Array.each(allData, function(a_item,index){
					if(a_item&&a_item.name==b_item.name){
						allData.splice(index,1)
						return;
					}
				});
			});
		}
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
					id:'save',
					margin:'0 0 0 2',
					handler:function(btn){
						var error;
						var togrid = Ext.getCmp('togrid');
						var remark = Ext.getCmp('remark').value;
						var saveJson = new Array();
						Ext.Array.each(togrid.store.data.items, function(item){
							if(item.data.title==null||item.data.title==''){
								error = true;
							}
							saveJson.push(item.data);
						});
						if(error){
							showInformation('请为所有选中分组填写显示名称', function(btn){});
							return;
						}
						Ext.Ajax.request({
							url : basePath + 'oa/flow/saveUsingGroups.action',
							params: {
			   					shortName:shortName,
			   					nodeName:name,
			   					groups:JSON.stringify(saveJson),
			   					remark:remark
			   				},
							method : 'post',
							callback : function(options,success,response){
								var localJson = new Ext.decode(response.responseText);
								if(localJson.exceptionInfo){
									showError(localJson.exceptionInfo);return;
								}
								if(localJson.success){
									showInformation('节点保存成功！', function(btn){
										parent.Ext.getCmp('nodeEdit').close()
									});
								}
							}
						});
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
								if(parent.Ext.getCmp('nodeEdit')){
									parent.Ext.getCmp('nodeEdit').close()
								}
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
						fieldLabel: '节点名称',
						name: '节点名称',
						columnWidth:0.5,
						xtype:'textfield',
						labelAlign: 'left',
						readOnly:true,
						editable:false,
						value:name,
						cls: "form-field-allowBlank"
					},{
						fieldLabel: '描述',
						id: 'remark',
						readOnly:false,
						value:remark!='null'?remark:'',
						xtype: "textareatrigger",
					    maxLength: 300,
				        maxLengthText: "字段长度不能超过300字符!",
				        hideTrigger: false,
				        editable: true,
				        columnWidth: 0.5,
				        allowBlank: true,
				        cls: "form-field-allowBlank",
				        labelAlign: "left",
				        allowDecimals: true
					},{
						flowData:flowData,
						nowData:nowData,
						allData:allData,
						height:window.innerHeight - 55,
						columnWidth: 1,
						xtype:'FlowNodeGrid'
					}]
				}]
			}] 
		});
		me.callParent(arguments);
	},
	//读取已加载分组和备注
	getNowData:function(shortName,name){
		var o = {};
		var usingGroups;
		var remark;
		Ext.Ajax.request({
			url : basePath + 'oa/flow/getUsingGroups.action',
			async:false,
			params:{
				shortName:shortName,
				nodeName:name
			},
			callback : function(options,success,response){
				var rs = new Ext.decode(response.responseText);
				if(rs.exceptionInfo){
					showError(rs.exceptionInfo);return;
				}
				usingGroups = Ext.JSON.decode(rs.groups);
				remark = rs.remark;
			}
		});
		o.usingGroups = usingGroups;
		o.remark = remark;
		return o;
		
	},
	//读取全部分组
	getAllData:function(shortName){
		var allGroups = new Array();
		Ext.Ajax.request({
			url : basePath + 'oa/flow/getAllGroups.action',
			async:false,
			params:{
				shortName:shortName
			},
			callback : function(options,success,response){
				var rs = new Ext.decode(response.responseText);
				if(rs.exceptionInfo){
					showError(rs.exceptionInfo);return;
				}
				if(rs.groups.length>0){
					Ext.Array.each(rs.groups, function(item){
						allGroups.push({
							name : item.FGC_GROUPNAME
						});
					});
				}
			}
		});
		return allGroups;
	},
	//读取该节点的派生信息
	getFlowData:function(shortName,name){
		var flowData = new Array();
		Ext.Ajax.request({
			url : basePath + 'common/getFieldsDatas.action',
			async: false,
			params:{
				fields : 'fo_nodename,fo_fdshortname',
				caller : 'flow_operation',
				condition : 'FO_FLOWNAME = \''+ shortName +'\' and FO_FLOWNODENAME = \''+ name +'\' order by fo_id'
			},
			callback : function(options,success,response){
				var rs = new Ext.decode(response.responseText);
				if(rs.exceptionInfo){
					showError(rs.exceptionInfo);return;
				}
				Ext.Array.each(Ext.decode(rs.data), function(item){
					flowData.push({
						nodename:item.FO_NODENAME,
						define:item.FO_FDSHORTNAME.split('-')[0],
						shortname:item.FO_FDSHORTNAME
					});
				});
			}
		});
		return flowData;
	}
});