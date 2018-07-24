Ext.define('erp.view.common.JProcess.SetNodeGridPanel',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.SetNodeGridPanel',
	id: 'NodeGrid', 
	emptyText : '无数据',
	title: '<h1 style="color:black ! important;">所有节点</h1>',
	columnLines : true,
	autoScroll : true,
	columns: [],
	keyValue:null,
	FlowCaller:null,
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	GridUtil: Ext.create('erp.util.GridUtil'),
	selModel: Ext.create('Ext.selection.CheckboxModel',{
		headerWidth: 0
	}),
	plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
		clicksToEdit: 1
	})],
	requires: ['erp.view.core.grid.YnColumn'],
	menu:null,
	initComponent : function(){
		this.getGridStore(this,this.keyValue,this.FlowCaller);
		this.callParent(arguments); 
	},
	dbfinds:[{
		dbGridField: "em_code",
		field: "JP_EXTRAMAN",
	},{
		dbGridField: "em_name",
		field: "JP_EXTRAMANNAME",
	}],
	getGridStore:function(grid,keyValue,caller){
		Ext.Ajax.request({
			url : basePath + 'common/getCurrentJnodes.action',
			params: {
				caller: caller,
				keyValue:keyValue,
				_noc:1
			},
			method : 'post',
			callback : function(options,success,response){
				var localJson = new Ext.decode(response.responseText);
				if(localJson.success){
					var griddata=localJson.data;
					var current=localJson.currentnode;
					var historynodes=localJson.nodes;
					var dojprocess=localJson.processs;
					var dojprocand=localJson.jprocands;
					var sel = Ext.Array.pluck(historynodes, 'jn_name');	
					var doj = Ext.Array.pluck(dojprocess,"jp_nodeName");
					Ext.each(griddata, function(d, index){
						if(Ext.Array.contains(sel, d.JP_NODENAME)){
							for(i in historynodes){
								if(historynodes[i].jn_name==d.JP_NODENAME){
									if (historynodes[i].jn_dealResult=='结束流程')d.STATUS='已结束';
									else if (historynodes[i].jn_dealResult=='不同意')d.STATUS='未通过';
									else d.STATUS='已审批';
									d.REALDEALMAN=historynodes[i].jn_dealManId+'('+historynodes[i].jn_dealManName+')';
								}
							}											      					
						}else d.STATUS='未触发';						
						if(Ext.Array.contains(dojprocand, d.JP_NODENAME)){
							    d.STATUS='在进行';
						}else if(Ext.Array.contains(doj, d.JP_NODENAME)){							
							for(i in dojprocess){
								if(dojprocess[i].jp_nodeName==d.JP_NODENAME && dojprocess[i].jp_flag==1 && dojprocess[i].jp_status=='待审批'){
									d.REALDEALMAN=dojprocess[i].jp_nodeDealMan+'('+dojprocess[i].jp_nodeDealManName+')';
									d.STATUS='在进行';									
								}
							}
						}	   						
					});
					var store=Ext.create('Ext.data.Store',{
						fields:['JP_ID','JP_PROCESSDEFID','JP_NODENAME','JP_NODEDEALMAN','JP_NODEDEALMANNAME','JP_NEWNODEDEALMAN','JP_NEWNODEDEALMANNAME','JP_CANEXTRA','JP_EXTRAMAN','JP_EXTRAMANNAME','REALDEALMAN','STATUS'],
						data:griddata
					});
					var columns=grid.getColumns();
					grid.reconfigure(store,columns);
				}
			}
		});
	},
	loadNewStore:function(grid,caller,keyValue){
		Ext.Ajax.request({
			url : basePath + 'common/getCurrentJnodes.action',
			params: {
				caller: caller,
				keyValue:keyValue,
				_noc:1
			},
			method : 'post',
			callback : function(options,success,response){
				var localJson = new Ext.decode(response.responseText);
				if(localJson.success){
					var griddata=localJson.data;
					var current=localJson.currentnode;
					var historynodes=localJson.nodes;
					var dojprocess=localJson.processs;
					var dojprocand=localJson.jprocands;
					var sel = Ext.Array.pluck(historynodes, 'jn_name');					
					var doj = Ext.Array.pluck(dojprocess,"jp_nodeName");
					Ext.each(griddata, function(d, index){
						if(Ext.Array.contains(sel, d.JP_NODENAME)){
							for(i in historynodes){
								if(historynodes[i].jn_name==d.JP_NODENAME){
									if (historynodes[i].jn_dealResult=='结束流程')d.STATUS='已结束';
									else if (historynodes[i].jn_dealResult=='不同意')d.STATUS='未通过';
									else d.STATUS='已审批';
									d.REALDEALMAN=historynodes[i].jn_dealManId+'('+historynodes[i].jn_dealManName+')';
								}
							}											      					
						}else d.STATUS='未触发';						
						if(Ext.Array.contains(dojprocand, d.JP_NODENAME)){
							    d.STATUS='在进行';
						}else if(Ext.Array.contains(doj, d.JP_NODENAME)){							
							for(i in dojprocess){
								if(dojprocess[i].jp_nodeName==d.JP_NODENAME && dojprocess[i].jp_flag==1 && dojprocess[i].jp_status=='待审批'){
									d.REALDEALMAN=dojprocess[i].jp_nodeDealMan+'('+dojprocess[i].jp_nodeDealManName+')';
									d.STATUS='在进行';									
								}
							}
						}	   						
					});
					grid.store.loadData(griddata);
				}
			}
		});
	},
	getColumns:function(){
		return [{
			text:'ID',
			dataIndex:'JP_ID',
			width:0,
		},{
			cls : "x-grid-header-1",
			text: '流程版本',
			dataIndex: 'JP_PROCESSDEFID',
			width:0,
			readOnly:true
		},{
			cls : "x-grid-header-1",
			text: '节点名称',
			dataIndex: 'JP_NODENAME',
			flex: 1,
		},{
			cls : "x-grid-header-1",
			text:'节点处理人',
			dataIndex: 'JP_NODEDEALMAN',
			flex: 1,
			readOnly:true
		},{
			cls : "x-grid-header-1",
			text:'处理人名称',
			dataIndex:'JP_NODEDEALMANNAME',
			flex:1,
			readOnly:true
		},{
			cls : "x-grid-header-1",
			text:'设置处理人',
			dataIndex: 'JP_NEWNODEDEALMAN',
			flex:1,
			editor: {
				format:'',
				xtype: 'combo',
				editable:false,
				onTriggerClick:function(trigger){
					var me=this;
					var parentgrid=Ext.getCmp('NodeGrid'),storedata=[],selecteddata=[];
					var selected=parentgrid.getSelectionModel().getLastSelected();
					var codevalue=selected.data['JP_NODEDEALMAN'].split(",");
					var namevalue=selected.data['JP_NODEDEALMANNAME'].split(",");
					Ext.Array.each(codevalue,function(item,index){
						storedata.push({
							emcode:item,
							emname:namevalue[index]
						});
					});
					if(selected.data['JP_NEWNODEDEALMAN']){
						var selectcodevalue=selected.data['JP_NEWNODEDEALMAN'].split(",");
						var selectnamevalue=selected.data['JP_NEWNODEDEALMANNAME'].split(",");
						Ext.Array.each(selectcodevalue,function(item,index){
							selecteddata.push({
								emcode:item,
								emname:selectnamevalue[index]
							});
						});
					}
					if(parentgrid.menu==null){
						parentgrid.menu=Ext.create('Ext.menu.Menu', {
							id : 'win-flow' + this.id,		
							buttonAlign : 'center',
							ownerCt: me,
							width:200,
							renderTo: Ext.getBody(),
							style: {
								overflow: 'visible', 
							},
							items:[{
								xtype:'gridpanel',
								width:200,
								frame:true,
								columnLines:true,
								height:250,
								id:'smallgrid',
								multiselected:new Array(),
								plugins:[Ext.create('erp.view.core.grid.HeaderFilter')],
								//hideHeaders:true,
								selModel: Ext.create('Ext.selection.CheckboxModel',{
									ignoreRightMouseSelection : false,									
									//mode:'single',
									onRowMouseDown: function(view, record, item, index, e) {//改写的onRowMouseDown方法
										var me = Ext.getCmp('smallgrid');
										var bool = true;
										var items = me.selModel.getSelection();
									/*	Ext.each(items, function(item, index){//禁用点击取消当前勾选的方法，必须勾选一条
											if(this.index && record.index && this.index == record.index){
												bool = false;
												me.selModel.deselect(record);
												me.multiselected=new Array();
												Ext.Array.remove(items, item);
												Ext.Array.remove(me.multiselected, record);
											}
										});*/
										Ext.each(me.multiselected, function(item, index){
											items.push(item);
										});
										if(bool){
											view.el.focus();
											var checkbox = item.childNodes[0].childNodes[0].childNodes[0];
											if(checkbox.getAttribute && checkbox.getAttribute('class') == 'x-grid-row-checker'){
												this.deselectAll(true);
												me.multiselected=new Array();
												me.multiselected.push(record);
												items.push(record);
												me.selModel.select(me.multiselected);
											} else {
												me.selModel.deselect(record);
												me.multiselected=new Array();
												Ext.Array.remove(me.multiselected, record);
											}
										}							        	
									},
									onHeaderClick: function(headerCt, header, e) {/*
										if (header.isCheckerHd) {
											e.stopEvent();
											var isChecked = header.el.hasCls(Ext.baseCSSPrefix + 'grid-hd-checker-on');
											if (isChecked) {
												this.deselectAll(true);
												var grid = Ext.getCmp('smallgrid');
												this.deselect(grid.multiselected);
												grid.multiselected = new Array();
												var els = Ext.select('div[@class=x-grid-row-checker-checked]').elements;
												Ext.each(els, function(el, index){
													el.setAttribute('class','x-grid-row-checker');
												});
												header.el.removeCls(Ext.baseCSSPrefix + 'grid-hd-checker-on');//添加这个
											} else {
												var grid = Ext.getCmp('smallgrid');
												this.deselect(grid.multiselected);
												grid.multiselected = new Array();
												var els = Ext.select('div[@class=x-grid-row-checker-checked]').elements;
												Ext.each(els, function(el, index){
													el.setAttribute('class','x-grid-row-checker');
												});
												this.selectAll(true);
												header.el.addCls(Ext.baseCSSPrefix + 'grid-hd-checker-on');//添加这个
											}
										}								          
									*/}

								}),
								buttonAlign:'center',
								columns:[{
									cls : "x-grid-header-1",
									text: '人员名称',
									dataIndex: 'emname',
									flex: 1,
									filter:{
										xtype : "textfield"	
									}
								},{
									cls : "x-grid-header-1",
									text: '人员编号',
									dataIndex: 'emcode',
									flex: 1,
									filter:{
										xtype : "textfield"	
									}
								}],
								store:Ext.create('Ext.data.Store',{
									fields:[{name:'emname'},{name:'emcode'}],
									data:storedata
								}),
								dockedItems: [{ 
									buttonAlign:'center',
									xtype: 'toolbar',
									dock: 'bottom',
									items: ['->',{
										xtype:'button',
										text:'确认',
										handler:function(btn){
											var grid=btn.ownerCt.ownerCt;
											if(grid.multiselected.length<1){
												Ext.Msg.alert('提示','请选择需要设置的审批人!');
											}else {
												var newnodedealman="",newnodedealmanname="";
												Ext.Array.each(grid.multiselected,function(item,index){				            		    	  
													newnodedealman+=item.data['emcode']+",";
													newnodedealmanname+=item.data['emname']+",";
												});	
												var selected=parentgrid.getSelectionModel().getLastSelected();
												selected.set('JP_NEWNODEDEALMAN',newnodedealman.substring(0,newnodedealman.lastIndexOf(",")));
												selected.set('JP_NEWNODEDEALMANNAME',newnodedealmanname.substring(0,newnodedealmanname.lastIndexOf(",")));
												parentgrid.menu.hide();
											}
										}
									},{  
										xtype: 'button', 
										text: '取消'	,
										style :'margin-left:10px',
										handler:function(){
											parentgrid.menu.hide();
										}
									},'->']
								}], 

							}]
						});
					}else Ext.getCmp('smallgrid').getStore().loadData(storedata);						
					parentgrid.menu.alignTo(me.inputEl, 'tl-bl?');
					parentgrid.menu.show();
					var smallgrid=Ext.getCmp('smallgrid');
					var selectitems=new Array();
					Ext.Array.each(smallgrid.store.data.items,function(item){
						Ext.Array.each(selecteddata,function(d0){
							if(item.data['emcode']==d0['emcode']){
								selectitems.push(item);	
								return false;
							} 
						});
					});
					smallgrid.multiselected=selectitems;
					smallgrid.selModel.select(selectitems);
				}
			},
			renderer: function(val, meta, record){
				if(!val){
					val="";
				}
				return '<img src="' + basePath + 'resource/images/icon/need.png" title="必填字段">' + 
				'<span style="color:blue;padding-left:2px;" title="必填字段">' + val + '</span>';						  
			}
		},{
			cls : "x-grid-header-1",
			text:'人员名称',
			dataIndex:'JP_NEWNODEDEALMANNAME',
			flex:1,
			readOnly:true,
			renderer: function(val, meta, record){
				if(!val){
					val="";
				}
				return '<img src="' + basePath + 'resource/images/icon/need.png" title="必填字段">' + 
				'<span style="color:blue;padding-left:2px;" title="必填字段">' + val + '</span>';						  
			}	
		},{
			cls : "x-grid-header-1",
			text:'额外人',
			dataIndex:'JP_EXTRAMAN',
			flex:1,
			dbfind:'Employee|em_code',
			editor:{
				xtype:'dbfindtrigger',
				hideTrigger: false,
				name:'JP_EXTRAMAN',
				which:'grid',
				dbfind:'Employee|em_code',
				listeners:{
					focus: function(t){
						t.setHideTrigger(false);
						t.setReadOnly(false);
						var record = Ext.getCmp('NodeGrid').selModel.getLastSelected();
						var canextra = record.data['JP_CANEXTRA'];
						if(canextra == null || canextra == '' || canextra=='0'){
							showError("当前节点不允许指定额外处理人!");   
							t.setHideTrigger(true);
							t.setReadOnly(true);
						} 
					}
				}
			}
		},{
			cls:'x-grid-header-1',
			text:'额外人名',
			flex:1,
			dataIndex:'JP_EXTRAMANNAME',
			readOnly:true
		},{
			cls:'x-grid-header-1',
			text:'额外指定',
			flex:1,
			dataIndex:'JP_CANEXTRA',
			readOnly:true,
			xtype:'yncolumn'
		},{
			cls:'x-grid-header-1',
			text:'实际处理人',
			logic:'ignore',
			flex:1,
			dataIndex:'REALDEALMAN',
			readOnly:true,	
		},{
			cls:'x-grid-header-1',
			text:'当前状态',
			logic:'ignore',
			flex:1,
			dataIndex:'STATUS',
			readOnly:true,
			renderer:function(val,mata,record){
				if(val=='已审批'){
					return '<img src="' + basePath + 'resource/images/renderer/finishrecord.png" >' + 
					'<span style="color:green;padding-left:2px;">' + val + '</span>';
				}else if(val=='在进行'){
					return '<img src="' + basePath + 'resource/images/renderer/doing.png" >' + 
					'<span style="color:blue;padding-left:2px;">' + val + '</span>';
				}else if(val=='未通过' || val=='已结束'){
					return '<img src="' + basePath + 'resource/images/renderer/remind2.png" >' + 
					'<span style="color:red;padding-left:2px;">' + val + '</span>';
				}else {
					return '<img src="' + basePath + 'resource/images/renderer/key1.png">'+'<span style="color:#8B8B83;padding-left:2px ">' + val + '<a/></span>';
				}
			}
		}];

	}
});