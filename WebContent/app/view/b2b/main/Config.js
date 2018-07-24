Ext.define('erp.view.b2b.main.Config', {
	extend : 'Ext.Viewport',
	layout : 'border',
	hideBorders : true,
	initComponent : function() {
		var me = this;
		Ext.apply(me, {
			items : [ {
				xtype : 'container',
				region : 'center',
				layout : 'border',
				items : [ {
					xtype : 'form',
					region : 'center',
					autoScroll : true,
					id : 'storePanel',
					title : '上架仓库配置',
					layout : 'column',
					bodyStyle : 'background:#f9f9f9;padding:5px 5px 0',
					defaults : {
						columnWidth : 1,
						margin : '4 8 4 8'
					},
					items:[{						
						id:'top',
						xtype: 'displayfield',
						value: '选择上架产品数据来源：',
						columnWidth:1,
						margin: '5 0 5 15'	
					    },{						
						id:'display',
						xtype: 'displayfield',
						value: '您希望将哪些仓库的空闲库存在优软商城上架售卖',
						columnWidth:1,
						cls: 'help-block',
						margin: '5 0 5 15'	
					    },{
						id:'cbg',
						xtype: 'checkboxgroup',
						fieldLabel:'',
						name: 'chooseStoreFrom',
						margin:'4 8 0 8',
						layout: {
							 type:'table',
							 columns:6
						},
						items:[{
							boxLabel:'全选',inputValue:0,checked:false,id:'allcheck',margin:'10 20 0 20',
								handler:function(){	
										var ChkGrp = Ext.getCmp('cbg');
											if(Ext.getCmp('allcheck').checked){
												for (var i = 0; i <ChkGrp.items.length; i++) {
												    var id =ChkGrp.items.items[i].id;
													if(id!='allcheck'&&id!='display'){
														var item = ChkGrp.items.items[i];
														item.setValue(true);							    		
													}
												}
											}else{
												for (var i = 0; i <ChkGrp.items.length; i++) {
													var id =ChkGrp.items.items[i].id;
													if(id!='allcheck'&&id!='display'){
														var item = ChkGrp.items.items[i];													
														item.setValue(false);
													}
												}
											}						    	
								}
						}]
					} ],
					buttonAlign : 'center',
					buttons : [ {
						text : '保存',
						id : 'btn-save',
						height : 30
					}, {
						text : '关闭',
						id : 'btn-close',
						height : 30
					}]
				},/*{
					xtype : 'form',
					region : 'center',
					autoScroll : true,
					id : 'configPanel',
					title : '参数配置',
					layout : 'column',
					bodyStyle : 'background:#f9f9f9;padding:5px 5px 0',
					defaults : {
						columnWidth : .5,
						margin : '4 8 4 8'
					},
					buttonAlign : 'center',
					buttons : [ {
						text : '保存',
						id : 'btn-save',
						height : 30
					}, {
						text : '关闭',
						id : 'btn-close',
						height : 30
					}, {
						text : '查看日志',
						height : 30,
						id : 'btn-logs',
					} ],
					items : [ {
						html : '没有参数配置',
						cls : 'x-form-empty'
					} ]
				}, */{
					xtype: 'tabpanel',
					title: '逻辑配置',
					id: 'tabpanel',
					region : 'south',
					hidden:true,
					height: window.innerHeight*0.62,
					bodyStyle : 'background:#f9f9f9',
					border: false
					/*collapsible: true,
					collapseDirection: 'bottom',
					collapsed: window.whoami ? false : true*/
				} ]
			}]
		});
		me.callParent(arguments);
		me.resetStorePanel();
	},
	resetStorePanel: function() {
		var me = this;
		var wareHouse;
		//获取所有的仓库
		Ext.Ajax.request({
        	url : basePath + 'scm/getWarehouse.action',
        	async: false,
        	method : 'post',
        	callback : function(options,success,response){
        		var res = new Ext.decode(response.responseText);
        		if(res.exceptionInfo){
        			showError(res.exceptionInfo);return;
        		}
        		wareHouse = res;
        	}
        });
		var cbg = me.down('#cbg');
		Ext.Array.each(wareHouse.data, function(data, index){
			cbg.add({
				xtype:'checkbox',
				boxLabel:data['wh_description'],
				checked:data['wh_ismallstore']=='1'?true:false,
				inputValue:data['wh_ismallstore']=='1'?1:0,
				whcode:data['wh_code'],
				whid:data['wh_id'],
				margin:'10 20 0 20'   					
			});
		});
	}
});