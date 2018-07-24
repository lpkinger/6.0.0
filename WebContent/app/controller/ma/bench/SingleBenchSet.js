Ext.QuickTips.init();
Ext.define('erp.controller.ma.bench.SingleBenchSet', {
	extend: 'Ext.app.Controller',
	views:['ma.bench.BenchPanel','ma.bench.TabPanel','core.form.YnField','core.button.Save','core.button.Close',
			'core.button.Delete','core.button.Sync','core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.trigger.MultiDbfindTrigger'],
  	init:function(){
  		var me = this;
   		this.control({
			'#benchbtn' : {
				click : function(btn){
					me.showBenchButton(btn);
				}
			}
		});
   	},
	showBenchButton : function(btn){
		var me = this;
		var btns = [{
			xtype : 'erpSaveButton',
			handler : function(btn) {
				var grid = btn.ownerCt.ownerCt.down('gridpanel');
				var gridStore = me.getGridStore(grid);
				Ext.Ajax.request({
					url:basePath + 'bench/ma/saveBenchButtons.action',
					params:{
						benchButtons: '['+gridStore.toString()+']',
						bccode : benchcode
					},
					method:'post',
					callback:function(options,success,resp){
						var res = new Ext.decode(resp.responseText);
						if(res.success){
							me.getBenchButtons(grid,benchcode);
						}
						if(res.exceptionInfo){
							showError(res.exceptionInfo);
							return;
						}
					}
				});
			}
		}, {
			xtype : 'erpDeleteButton',
			disabled: true,
			handler : function(btn) {
				var grid = btn.ownerCt.ownerCt.down('gridpanel');
				var records = grid.selModel.getSelection();
				Ext.Msg.confirm('提示','确定删除工作台按钮',function(option){
					if(option=='yes'){
						var ids = '';
						Ext.Array.each(records,function(record){
							if(record.data['bb_id']){
								ids += ','+record.data['bb_id'];
							}
						})
						if(ids.length>0){
							ids = ids.substring(1);
							Ext.Ajax.request({
								url:basePath + 'bench/ma/deleteBenchButtons.action',
								params:{
									ids: ids,
									bccode : benchcode
								},
								method:'post',
								callback:function(options,success,resp){
									var res = new Ext.decode(resp.responseText);
									if(res.success){
										me.getBenchButtons(grid,benchcode);			
									}
									if(res.exceptionInfo){
										showError(res.exceptionInfo);
										return;
									}
								}
							});
						}else{
							grid.store.remove(records);
						}
					}
				});
			}
		}];
		if(!isSaas){
			btns.push({
				xtype : 'erpSyncButton',
				disabled: true,
				autoClearCache:true,
				checkMaster: function(g,curMaster,s,ms,t,sc){
					var me = this;
					var bool =true;
					if("true" === g && "admin" !== t && !Ext.Array.contains(ms, s.ma_name))
						bool=false;
					if(s.ma_name == 'DataCenter' && "admin" !== t) {
						bool=false;
					}
					return bool;
				},
				sync: function() {
					var masters = this.getCheckData(), form = Ext.getCmp('form'), w = this.win, me = this;
					var grid = this.ownerCt.ownerCt.down('gridpanel');
					var records = grid.selModel.getSelection();
					var codes = '';
					Ext.Array.each(records,function(record){
						if(record.data['bb_code']){
							codes += ",'"+record.data['bb_code']+"'";
						}
					})
					if(codes.length>0)
						codes = codes.substring(1);
					if (!Ext.isEmpty(masters)) {
						w.setLoading(true);
						Ext.Ajax.request({
							url: basePath + 'common/form/vastPost.action',
							params: {
								caller: 'BenchButton!Post',
								data: codes,
								to: masters
							},
							timeout: 600000,
							callback: function(opt, s, r) {
								w.setLoading(false);
								if(s) {
									var rs = Ext.decode(r.responseText);
									if(rs.data) {
										showMessage('提示', rs.data);
									} else {
										alert('同步成功!');
									}
				   					w.hide();
				   					if(me.autoClearCache) {
				   						me.clearCache();
				   					}
								}
							}
						});
					}
				}
			});
		}
		btns.push({
			xtype : 'erpCloseButton',
			handler : function(btn) {
				btn.ownerCt.ownerCt.close();
			}
		});
		var win = Ext.create('Ext.window.Window', {
			title : '工作台按钮',
			height : '85%',
			width : '80%',
			id : 'win_benchbtn',
			layout : 'fit',
			modal : true,
			border:false,
			items: [{
				xtype : 'gridpanel',
				border:false,
				columnLines : true,
				bodyStyle: 'background-color:#f1f1f1;',
				detno: 'bb_detno',
				necessaryFields:['bb_text'],
				plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
					clicksToEdit: 1
				}), Ext.create('erp.view.core.plugin.CopyPasteMenu')],
				selModel : Ext.create('Ext.selection.CheckboxModel', {
					checkOnly : true,
					headerWidth : 30
				}),
				dbfinds:[{
			　　　　　　dbGridField:'sn_caller',
			　　　　　　field:'bb_caller',
			　　　　　　trigger:null
			　　　},{
			　　　　　　dbGridField:'sn_displayname',
			　　　　　　field:'bb_text',
			　　　　　　trigger:null
			　　　},{
			　　　　　　dbGridField:'sn_addurl',
			　　　　　　field:'bb_url',
			　　　　　　trigger:null
			　　　},{
			　　　　　　dbGridField:'sn_url',
			　　　　　　field:'bb_listurl',
			　　　　　　trigger:null
			　　　}],
				columns : [{
					header: 'ID',
					cls: "x-grid-header-1",
					flex:2,
					dataIndex: 'bb_id',
					hidden: true,
					xtype: 'numbercolumn'
				},{
					header: '按钮编号',
					cls: "x-grid-header-1",
					flex:2,
					dataIndex: 'bb_code',
					hidden: true
				},{
					header: '工作台编号',
					cls: "x-grid-header-1",
					flex:2,
					dataIndex: 'bb_bccode',
					hidden: true
				},{
					header: '序号',
					cls: "x-grid-header-1",
					width:35,
					align : 'center',
					dataIndex: 'bb_detno',
					xtype: 'numbercolumn',
					editor: {
						xtype: 'numberfield',
						hideTrigger:true
					}
				},{
					header: '列表caller',
					cls: "x-grid-header-1",
					flex:2,
					dataIndex: 'bb_caller',
					dbfind:"SysNavigation|sn_caller",
					editor: {
						//xtype :'dbfindtrigger'
						xtype :'multidbfindtrigger'
					}
				},{
					header: '功能分组',
					cls: "x-grid-header-1",
					width: 100,
					dataIndex: 'bb_group',
					style : 'color:red',
					editor: {
						xtype:'combo',
						store: Ext.create('Ext.data.Store', {
						   fields: ['display', 'value'],
						   data : [{"display": '基础资料', "value": '基础资料'},
						           {"display": '业务制单', "value": '业务制单'},
						           {"display": '更多操作', "value": '更多操作'}]
					   }),
					   displayField: 'display',
					   valueField: 'value',
					   queryMode: 'local',
					   value:'业务制单'
					}
				},{
					header: '业务分类',
					cls: "x-grid-header-1",
					flex:2,
					dataIndex: 'bb_busingroup',
					editor: {
						xtype :'textfield'
					}
				},{
					header: '按钮名称',
					cls: "x-grid-header-1",
					flex:2,
					dataIndex: 'bb_text',
					style : 'color:red',
					editor: {
						xtype :'textfield'
					}
				},{
					header: '链接',
					cls: "x-grid-header-1",
					flex:6,
					style : 'color:red',
					dataIndex: 'bb_listurl',
					editor: {
						xtype :'textareatrigger'
					}
				},{
					header: '扩展链接',
					cls: "x-grid-header-1",
					flex:6,
					dataIndex: 'bb_url',
					editor: {
						xtype :'textareatrigger'
					}
				}],
				store: Ext.create('Ext.data.Store', {
			    	fields: [{
			    		name: 'bb_id',
			    		type: 'number'
			    	},{
			        	name: 'bb_code',
			        	type: 'string'
			        },{
			        	name:'bb_bccode',
			        	type:'string'
			        },{
			        	name:'bb_detno',
			        	type:'number'
			        },{
			        	name:'bb_caller',
			        	type:'string'
			        },{
			        	name:'bb_text',
			        	type:'string'
			        },{
			        	name:'bb_listurl',
			        	type:'string'
			        },{
			        	name:'bb_url',
			        	type:'string'
			        },{
			        	name:'bb_group',
			        	defaultValue: '业务制单',
			        	type:'string'
			        },{
			        	name:'bb_busingroup',
			        	type:'string'
			        }]
			    }),
			    listeners: {
					itemclick: function(selModel, record,item,index){
						var grid = selModel.ownerCt;
						if(index == grid.store.indexOf(grid.store.last())){
							me.add10Empty(grid,benchcode,record.data['bb_detno']);//就再加10行
				    	}
					},
					selectionchange : function(selModel, selected) {
						var panel = selModel.view.ownerCt.ownerCt;
						if(selected.length<1){
							panel.down('toolbar[dock=bottom] erpDeleteButton').setDisabled(true);
							panel.down('toolbar[dock=bottom] erpSyncButton').setDisabled(true);
						}else{
							panel.down('toolbar[dock=bottom] erpDeleteButton').setDisabled(false);
							panel.down('toolbar[dock=bottom] erpSyncButton').setDisabled(false);
						}
					}
				}
			}],
			listeners: {
				show: function(win){
					var grid = win.down('gridpanel');
					me.getBenchButtons(grid,benchcode);
				}
			},
			buttonAlign:'center',
			buttons: btns
		});
		win.show();
	},
	getBenchButtons : function(grid,benchcode){
		var me = this;
		Ext.Ajax.request({
			url:basePath + 'bench/ma/getBenchButtons.action',
			params:{
				benchcode: benchcode
			},
			method:'post',
			callback:function(options,success,resp){
				var res = new Ext.decode(resp.responseText);
				if(res.success){
					if(res.buttons.length>0){
						grid.store.loadData(res.buttons);
					}else{
						me.add10Empty(grid,benchcode);
					}
				}
				if(res.exceptionInfo){
					showError(res.exceptionInfo);
					win.close();
				}
			}
		});
	},
	add10Empty : function(grid,benchcode,detno){
		if(!detno){
			detno = 0;
			grid.store.removeAll();
		}
		var data = new Array();
		for(var i=0;i<10;i++){
			var o = new Object();
			o.bb_bccode = benchcode;
			o.bb_detno = detno+1+i;
			data.push(o);
		}
		grid.store.loadData(data,true);
	},
	getGridStore: function(grid){
		var me = this,jsonGridData = new Array();
		var s = grid.getStore().data.items;//获取store里面的数据
		for(var i=0;i<s.length;i++){//将grid里面各行的数据获取并拼成jsonGridData
			var data = s[i].data;
			dd = new Object();
			if(s[i].dirty&&s[i].data['bb_listurl']&&s[i].data['bb_text']&&s[i].data['bb_group']){
				Ext.each(grid.columns, function(c){
					if(!c.isCheckerHd){
						if(c.xtype == 'numbercolumn'){//赋个默认值0吧，不然不好保存
						if(data[c.dataIndex] == null || data[c.dataIndex] == '' || String(data[c.dataIndex]) == 'NaN'){
							dd[c.dataIndex] = '0';//也可以从data里面去掉这些字段
						} else {
							dd[c.dataIndex] = "" + s[i].data[c.dataIndex];
						}
						} else {
							dd[c.dataIndex] = s[i].data[c.dataIndex];
						}
						if (c.defaultValue && (dd[c.dataIndex] == null || dd[c.dataIndex] == '0')) {
							dd[c.dataIndex] = c.defaultValue;
						}
					}
				});
				jsonGridData.push(Ext.JSON.encode(dd));
			}
		}
		return jsonGridData;
	}
});