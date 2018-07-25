Ext.define('erp.view.oa.doc.DOCManage',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	border: 0,
	hideBorders: true, 
	cls:'mainViewport',
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'desk', 
				layout: 'border', 
				bodyBorder:false,
				bodyStyle:'background-color:#fff !important; border:none;',
				items: [{
					region: 'north',
					xtype : 'erpHeader'
				},{
					region:'center',
					layout:'border',
					items: [{
						region:'west',
						xtype:'erpDocumentTreePanel',
						bodyStyle:'background-color:#4D4A49 !important;border-radius:0px !important',
						width : 220,
						//title:'导航',
						collapsible : true,
						collapsedCls:'headtitle',
						cls: 'doc-tree',
						bodyCls:'tree-body',
						baseCondition:'1=1',
						tbar:[{
							xtype:'buttongroup',
							columns:4,
							cls:'menu-tab-bar',
							style:'background-color: #4D4A49 !important;border-radius:0px !important',
							width: 220,
							height:96,
							border:0,	
							layout: 'hbox',
							items:[{
								xtype:'container',
								cls:'search-container',
								width: 220,
								border: 0,
						        layout: 'hbox',
						        bodyStyle:'background-color:#4D4A49 !important;border-radius:0px !important',
						        items: [{ 
									xtype:'textfield',
									cls: 'search-box',
									id: 'searchbox',
									listeners : {
										specialkey : function(field, e){
											if(e.getKey() == Ext.EventObject.ENTER){
												var re = /[~#^$@%&!*()<>:;'"{}【】  ]/gi;  
												var f=this,tree=field.ownerCt.ownerCt.ownerCt.ownerCt;
												if(f.value == '' || f.value == null){
													return;
									        	}
												if(re.test(f.value)){
													Ext.Msg.alert('提示','输入不能包含特殊字符.')
													return;
												}
												tree.setLoading(true, tree.body);
												Ext.Ajax.request({//拿到tree数据
										        	url : basePath +'doc/searchTree.action',
										        	timeout:120000,
										        	params: {
										        		condition: f.value
										        	},
										        	callback : function(options,success,response){
										        		tree.setLoading(false);
										        		var res = new Ext.decode(response.responseText);
										        		if(res.fileList){
										        			var gird = Ext.getCmp('docgrid');
										        			gird.store.loadData(res.fileList);
										        		}else if(res.redirectUrl){
										        			window.location.href = res.redirectUrl;
										        		} else if(res.exceptionInfo){
										        			Ext.Msg.alert("ERROR:" + res.exceptionInfo);
										        		}
										        	}
												});
							        		}
										}
									}
								},{
									xtype: 'displayfield',
									cls: 'search-icon',
									html: '<span class="fa fa-search"></span>'
								}]
							}/*,{
								xtype: 'image',
								cls:'logo',
								src:'../../../jsps/oa/doc/resources/images/images/tree/treeexpand.png'
								}*/
							,{
								xtype:'container',
								cls:'button-container',
								border: 0,
						        layout: 'hbox',
						        bodyStyle:'background-color:#4D4A49 !important;border-radius:0px !important',
						        items: [{
									xtype:'button',
									cls:"button doctreebtn",
									width:52,
									height:26,
									border:1,
									id:'treeadd',
									text:'创建'
								},{
									xtype:'button',
									cls:"button doctreebtn",
									width:52,
									height:26,
									border:1,
									text:'编辑',
									id:'treeupdate'
								},{
									xtype:'button',
									cls:"button doctreebtn",
									width:52,
									height:26,
									border:1,
									id:'treedelete',
									text:'删除'
								}]
							}]
						}]
					},{
						style:'border:none;',
						flex: 1,
						region:'center',
						layout:'border',
						id:'centerEast',
						items:[{
							region: 'center',
							xtype:'erpDocPanel'							
						},{
							xtype:'erpDocTabPanel',
							region:'south'
						}]
					}
					]}
					/*,{
						xtype: 'pageBottom',
						region: 'south',
						height: 13
					}*/]
			}] 
		});
		me.callParent(arguments); 
	}
});