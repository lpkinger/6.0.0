/**
 * 设置个人工作台的window
 */
Ext.define('erp.view.common.bench.SelfBusinessSet', {
	extend: 'Ext.window.Window',
	alias: 'widget.selfBusinessSet',
	id : 'selfbusinessset',
	title: '<font color=#CD6839>权限申请</font>',
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
				id: 'powerapply',
				columnLines: true,
		      	isDirty: false,
				columns :[{
					header: '业务编号',
					dataIndex: 'bbcode',
					width: 0
				},{
					header: '业务名称',
					dataIndex: 'bbname',
					flex: 1
				},{
					header: '业务权限',
					dataIndex: 'enable',
					width:100,
					align:'center',
					renderer:function(value, m, record){
				        if (value) {
				            return '已分配';
				        }
				        return '<div style="color:#1874CD;font-weight:bold;" onClick=\"javascript:openUrl(\'jsps/oa/powerApply/powerApply.jsp?whoami=PowerApply\');">申请权限</div>';
				    }
				},{
					header:'显示',
					dataIndex:'show',
					width:60,
					xtype:'actioncolumn',
					processEvent: function(type, view, cell, recordIndex, cellIndex, e) {
				        if (type == 'mousedown' || (type == 'keydown' && (e.getKey() == e.ENTER || e.getKey() == e.SPACE))) {
				        	var record = view.panel.store.getAt(recordIndex);
				        	if(record.data.enable){
					        	var dataIndex = this.dataIndex;
					        	var checked = !record.get(dataIndex);
					            record.set(dataIndex, checked);
					            this.fireEvent('checkchange', this, recordIndex, checked);
				        	}
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
				}],
				store: Ext.create('Ext.data.Store', {
					fields:['bbcode','bbname','enable','show'],
					data:[]
				}),
				viewConfig: {  
			      	plugins: {  
			          ptype: 'gridviewdragdrop',  
			          ddGroup:  'DragDropGroup',  
			          enableDrag : true,
			          enableDrop : true
			      	},
			      	listeners: {
	                	drop: function(node, data,overModel, dropPosition, dropHandlers) {
	                		var powerapply = Ext.getCmp('powerapply');
							if(powerapply){
								powerapply.isDirty = true;
							}
	                	}
	                }
		      	}
			}],
			buttonAlign:'center',
			buttons:[{
				style:'margin-left:5px;',
				text:'确认',
				scope:this,
				handler:function(btn){
					var powerapply = Ext.getCmp('powerapply'),datas=new Array(),index=1;
					powerapply.getStore().each(function(record){
						powerapply.isDirty = powerapply.isDirty||record.dirty;
						if(record.data.enable&&record.data.show){
							var data = new Object();
							data.bbe_bbcode = record.data.bbcode;
							data.bbe_bbdetno = index++;
							datas.push(data);
						}
					});
					if(powerapply.isDirty){
						var param = unescape(escape(Ext.JSON.encode(datas)));
						me.setLoading(true);
						Ext.Ajax.request({
							url : basePath + 'bench/ma/saveSelfBusiness.action',
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
					Ext.Msg.confirm('提示','确定恢复默认业务?',function(option){
						if(option=='yes'){
							Ext.Ajax.request({
								url:basePath + 'bench/ma/selfReset.action',
								params:{
									benchcode: bench,
									isBusiness: true
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
			url : basePath + 'bench/ma/getSelfBusiness.action',
			params: {
				benchcode : bench
			},
			method : 'GET',
			callback : function(options,success,response){
				var res = new Ext.decode(response.responseText);
				if(res.success){
					var powerapply = Ext.getCmp('powerapply');
					powerapply.getStore().loadData(res.data);
				} else if(res.exceptionInfo){
					showError(res.exceptionInfo);
				}
			}
		});
	}
});