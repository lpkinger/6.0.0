Ext.define('erp.view.plm.test.CheckBase',{ 
	extend: 'Ext.Viewport', 
	layout: 'border', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 

					xtype: 'erpFormPanel',
					//anchor: '100% 65%',
					region:'north',
					height:'65%',
					saveUrl: 'plm/check/saveCheck.action',
					deleteUrl: 'plm/check/deleteCheck.action',
					updateUrl: 'plm/check/updateCheckBase.action',
					auditUrl: 'plm/check/auditCheck.action',
					resAuditUrl: 'plm/check/resauditCheck.action',
					submitUrl: 'plm/check/submitCheck.action',
					resSubmitUrl: '/plm/check/resSubmitCheckBase.action',
					getIdUrl: 'common/getId.action?seq=CHECKTABLE_SEQ',
					keyField: 'ch_id',
					codeField:'ch_code'
				},	
					
				{

						title:'测试历史',
						xtype: 'erpGridPanel2',	
						region:'center',
						//id:'grid',
						caller:'CheckListBaseDetail',
						keyField:'ch_id',
						collapsible :true,	//可以伸缩
						mainField:'ch_cbdid',
						autoScroll:true,
							//anchor:'100% 35%',
							plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
								clicksToEdit: 2,
								listeners:{
									beforeedit:function(e){
										var g=e.grid,r=e.record,f=e.field;
										if(g.binds){
											var bool=true;
											Ext.Array.each(g.binds,function(item){
												if(Ext.Array.contains(item.fields,f)){
													Ext.each(item.refFields,function(field){
														if(r.get(field)!=null && r.get(field)!=0 && r.get(field)!='' && r.get(field)!='0'){
															bool=false;
														} 
													});							
												} 
											});
											return bool;
										}
									}
								}

							}), Ext.create('erp.view.core.plugin.CopyPasteMenu')],
							features : [Ext.create('Ext.grid.feature.GroupingSummary',{
								startCollapsed: true,
								groupHeaderTpl: '{name} (共:{rows.length}条)'
							}),{
								ftype : 'summary',
								showSummaryRow : false,//不显示默认合计行
								generateSummaryData: function(){
									// 避开在grid reconfigure后的计算，节约加载时间50~600ms
									return {};
								}
							}],
				
				}
//						title:'测试历史',
//						xtype: 'erpGridPanel2',	
//						//id:'grid',
//						caller:'CheckListBaseDetail',
//						keyField:'ch_id',
//						 collapsible :true,	//可以伸缩
//						mainField:'ch_cbdid',
//							autoScroll:true,
//							anchor:'100% 35%',
//					}
					/* xtype:'tabpanel',
					 collapsible :true,	//可以伸缩
					 anchor:'100% 35%',
					 layout:'fit',
					items:[{
						title:'测试历史',
						xtype: 'erpGridPanel2',	
						id:'grid',
						caller:'CheckListBaseDetail',
						keyField:'ch_id',
						mainField:'ch_cbdid'
					},{
						title:'变更明细',
						xtype: 'erpGridPanel5',
						id:'change',
						caller:'CheckChange',
						 mainField:'cc_cldid',						 
					}]*/
				
				]
		}); 
		me.callParent(arguments); 
	} 
});