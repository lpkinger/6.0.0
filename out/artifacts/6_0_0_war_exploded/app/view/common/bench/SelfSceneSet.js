/**
 * 设置个人工作台的window
 */
Ext.define('erp.view.common.bench.SelfSceneSet', {
	extend: 'Ext.window.Window',
	alias: 'widget.selfSceneSet',
	id : 'selfsceneset',
	title: '<font color=#CD6839>待办设置</font>',
	iconCls: 'x-button-icon-set',
	height: '85%',
	width: 600,
	modal: true,
    maximizable : false,
	layout: 'fit',
	initComponent: function() {
		var me=this;
		Ext.apply(me, { 
			items: [{
				xtype: 'gridpanel',
				id: 'agencyset',
				columnLines: true,
		      	isDirty: false,
				columns :[{
					header: '场景编号',
					dataIndex: 'code',
					width: 0
				},{
					header: '场景名称',
					dataIndex: 'text',
					flex: 1
				},{
					header:'显示',
					dataIndex:'show',
					width:60,
					xtype:'actioncolumn',
					processEvent: function(type, view, cell, recordIndex, cellIndex, e) {
				        if (type == 'mousedown' || (type == 'keydown' && (e.getKey() == e.ENTER || e.getKey() == e.SPACE))) {
				        	var record = view.panel.store.getAt(recordIndex);
				        	var dataIndex = this.dataIndex;
				        	var checked = !record.get(dataIndex);
				            record.set(dataIndex, checked);
				            this.fireEvent('checkchange', this, recordIndex, checked);
				            return false;
				        }
				    },
					renderer:function(value, m, record){
				        var cssPrefix = Ext.baseCSSPrefix,
				            cls = [cssPrefix + 'grid-enableheader'];
				        if (value) {
				            cls.push(cssPrefix + 'grid-enableheader-checked');
				        }
				        return '<div class="' + cls.join(' ') + '">&#160;</div>';
				    }
				},{
					header: '计数',
					xtype: 'checkcolumn',
					dataIndex: 'iscount',
					width: 50
				}],
				store: Ext.create('Ext.data.Store', {
					fields:['code','text','bbname','show','iscount','groupnum'],
					data:[],
					groupers: [{
			        	property: 'bbname',
			        	sorterFn : function(com1,com2) {
			        		if(com1.data.groupnum<com2.data.groupnum){
			        			return -1;
			        		}else if(com1.data.groupnum>com2.data.groupnum){
			        			return 1;
			        		}else{
			        			return 0;
			        		}
			        	}
			        }]
				}),
				features: [Ext.create('Ext.grid.feature.Grouping',{
			    	startCollapsed: false,
			        groupHeaderTpl: '{name} ({rows.length})'
			    })],
			    viewConfig: {  
			      	plugins: {  
			          ptype: 'gridviewdragdrop',  
			          ddGroup:  'DragDropGroup',  
			          enableDrag : true,
			          enableDrop : true
			      	},
			      	listeners: {
			          	drop : function(node, data, overModel, dropPosition, dropFunction){
						    var agencyset = Ext.getCmp('agencyset');
							if(agencyset){
								agencyset.isDirty = true;
							}
			          	}
			      	}
		      	}
	      	}],
			buttonAlign:'center',
			buttons:['->',{
				style:'margin-left:5px;',
				text:'确认',
				scope:this,
				handler:function(btn){
					var agencyset = Ext.getCmp('agencyset'),datas=new Array(),index=1;
					agencyset.getStore().each(function(record){
						agencyset.isDirty = agencyset.isDirty||record.dirty;
						if(record.data.show){
							var data = new Object();
							data.be_bscode = record.data.code;
							data.be_bsdetno = index++;
							data.be_iscount = record.data.iscount?-1:0;
							datas.push(data);
						}
					});
					if(agencyset.isDirty){
						var param = unescape(escape(Ext.JSON.encode(datas)));
						me.setLoading(true);
						Ext.Ajax.request({
							url : basePath + 'bench/ma/saveSelfScene.action',
							params : {
								benchcode: bench,
								datas: param
							},
							method : 'post',
							callback : function(options,success,response){
								me.setLoading(false);
								var localJson = new Ext.decode(response.responseText);
								if(localJson.success){
									Ext.Msg.alert('提示','保存成功',function(){
										me.close();
										window.location.reload();
									});
								} else{
									saveFailure();//@i18n/i18n.js
								}
							}
						});
					}else{
						Ext.Msg.alert("提示","未修改！");
						return;
					}
				}
			},{
				style:'margin-left:5px;',
				text:'重置',
				handler:function(btn){
					Ext.Msg.confirm('提示','确定恢复默认场景?',function(option){
						if(option=='yes'){
							Ext.Ajax.request({
								url:basePath + 'bench/ma/selfReset.action',
								params:{
									benchcode: bench,
									isBusiness: false
								},
								method:'post',
								callback:function(options,success,resp){
									var res = new Ext.decode(resp.responseText);
									if(res.success){
										window.location.reload();				
									}
									if(res.exceptionInfo){
										showError(res.exceptionInfo);
										return;
									}
								}
							});
						}
					});
				}
			},{
				style:'margin-left:5px;',
				text:'关闭',
				handler:function(btn){
					btn.ownerCt.ownerCt.close();
				}
			}]
		});
		this.callParent(arguments);
		this.show();
		this.getData();
	},
	getData: function(){
		var me = this;
		Ext.Ajax.request({
			url : basePath + 'bench/ma/getSelfScene.action',
			params: {
				benchcode : bench
			},
			method : 'GET',
			callback : function(options,success,response){
				var res = new Ext.decode(response.responseText);
				if(res.success){
					var agencyset = Ext.getCmp('agencyset');
					agencyset.getStore().loadData(res.data);
				} else if(res.exceptionInfo){
					showError(res.exceptionInfo);
				}
			}
		});
	}
});