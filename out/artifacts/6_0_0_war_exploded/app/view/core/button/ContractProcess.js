/**
 * 合同关联流程历史
 */
 
Ext.define('erp.view.core.button.ContractProcess',{ 
	extend: 'Ext.Button', 
	alias: 'widget.erpContractProcess',
	text: $I18N.common.button.erpContractProcess,
	iconCls: 'x-button-icon-add',
	cls: 'x-btn-gray',
	width: 85,
	style: {
		marginLeft: '10px'
    },
	initComponent : function(){ 
		this.callParent(arguments); 
	},
	listeners: {
		click : function(btn){
			btn.getContractProcess();
		}
	},
	getContractProcess : function(){
		var me = this;
		var id = Ext.getCmp('pu_id').value;
		Ext.Ajax.request({
			url : basePath + 'scm/purchase/getContractProcess.action',
			params: {
				id: id,
				_noc:1
			},
			method : 'post',
			callback : function(options,success,response){
				var localJson = new Ext.decode(response.responseText);
				if(localJson.exceptionInfo){
					showError(localJson.exceptionInfo);
					return;
				}else{
					Ext.create('Ext.window.Window', {
						title : "合同流程记录",
						height : "85%",
						width : "80%",
						id:'win',
						closeAction : 'destroy',
						maximizable : true,
						modal : true,
						layout:'fit',
						items : [{
							xtype: 'tabpanel',
							id:'tab',
							items:[{
								title: '招标单',
								xtype: 'panel',
								frame : true,
								autoScroll : true,
								items:[Ext.create("erp.view.core.button.TenderPanel",{
									id: 'tenderform',
									tenderForm : true,
									formdata : localJson.tenderform
								}),Ext.create("erp.view.common.JProcess.AllHistoryGridPanel",{
									id: 'tenderhistory',
									plugins: null,
									nodeId: localJson.nodes.Tender.node
								})]
							},{
								title: '投标单',
								frame : true,
								autoScroll : true,
								xtype: 'panel',
								items:[Ext.create("erp.view.core.button.TenderPanel",{
									id: 'tenderSubmissionform',
									tenderForm : false,
									formdata : localJson.tenderform
								}),{
									xtype : 'gridpanel',
									title : '已投标供应商',
									header : false,
									store:Ext.create('Ext.data.Store',{
								    	fields:['saleId','enName','vendUU','applyStatus'],
								    	data:localJson.vendors
								    }),
									columns: [{
										header: '投标ID',
										dataIndex: 'saleId',
										hidden: true,
										width: 0
									},{
										header: '企业UU',
										dataIndex: 'vendUU',
										hidden: true,
										width: 0
									},{
										header: '供应商',
										dataIndex: 'enName',
										align: 'center',
										width: 250,
										renderer:function(val, meta, record, x, y, store, view){
										 	meta.style="padding-right:0px!important";
										 	meta.tdAttr = 'data-qtip="' + Ext.String.htmlEncode(val) + '"';  
										 	if(val){
										 		return  '<a href="javascript:openUrl(\'jsps/scm/sale/tenderSubmission.jsp?formCondition=readOnlyidEQ' + record.data['saleId'] + '\')">' + Ext.String.htmlEncode(val) + '</a>';
										 	}
										 	return '';
									 	}
									},{
										header:'评标结果',
										dataIndex:'applyStatus',
										align: 'center',
										width:80,
										renderer:function(val,meta,rec){
											return val?'中标':'未中标';
										}
									}]
								}]
							},{
								title: '评标单',
								frame : true,
								autoScroll : true,
								xtype: 'panel',
								items:[Ext.create("erp.view.core.button.TenderPanel",{
									id: 'tenderestimateform',
									tenderForm: true,
									tender: false,
									formdata : localJson.tenderform
								}),Ext.create("erp.view.common.JProcess.AllHistoryGridPanel",{
									id: 'TenderEstimatehistory',
									plugins: null,
									nodeId: localJson.nodes.TenderEstimate.node
								})]
							}],
							listeners: {
								afterrender: function(tab){
									if(localJson.tenderChanges.length>0){
										var items = new Array();
										Ext.Array.each(localJson.tenderChanges,function(tenderchange,index){
											items.push({
												xtype : 'panel',
												margin : '0 0 20 0',
												items : [{
													xtype : 'form',
													id : 'tenderchangeform'+index,
													frame : true,
													layout : 'column',
													labelSeparator : ':',
													defaults : {
														xtype : "textfield", 
														columnWidth : 0.33,
														labelWidth : 120,
												      	fieldStyle : "background:#eeeeee;color:#515151;", 
												      	labelAlign : "left",
												      	editable : false,
												      	readOnly : true    	
													},
													items:[{
														fieldLabel: "ID", 
														name:'tc_id',
														value : tenderchange.form.tc_id,
														hidden:true
													},{
														fieldLabel: "开标变更单号", 
														name:'tc_code', 
														value : tenderchange.form.tc_code,
														fieldStyle : 'background:#eeeeee;color:blue;text-decoration:underline;',
														listeners : {
															click: {
																element:'inputEl',
																buffer : 100,
																fn: function(e,el) {
																	var url = 'jsps/scm/purchase/tenderchange.jsp?whoami=TenderChange&formCondition=tc_idIS'+tenderchange.form.tc_id;
										                            if(tenderchange.form.tc_code){
										    						   openUrl2(url,'开标变更单('+tenderchange.form.tc_code+')');
										                            }
																}	  
															}
														}
													},{
														fieldLabel: "招标编号", 
														name:'tc_ttcode', 
														value : tenderchange.form.tc_ttcode,
														fieldStyle : 'background:#eeeeee;color:blue;text-decoration:underline;',
														listeners : {
															click: {
																element:'inputEl',
																buffer : 100,
																fn: function(e,el) {
																	var url = 'jsps/scm/purchase/tenderEstimate.jsp?formCondition=idIS'+tenderchange.form.tc_ttid+'&gridCondition';
										                            if(tenderchange.form.tc_ttcode){
										    						   openUrl2(url,'评标单('+tenderchange.form.tc_ttcode+')');
										                            }
																}	  
															}
														}
													},{
														fieldLabel: "招标标题", 
														name:'tc_tttitle', 
														value : tenderchange.form.tc_tttitle
													},{
														fieldLabel: "变更类型", 
														name:'tc_type', 
														value : tenderchange.form.tc_type
													},{
														xtype:'datefield',
														name:'tc_oldendtime',
														fieldLabel: "投标截止时间(原)", 
														format:'Y-m-d',
														value : tenderchange.form.tc_oldendtime
													},{
														xtype:'datefield',
														name:'tc_newendtime',
														fieldLabel: "投标截止时间(新)", 
														format:'Y-m-d',
														value : tenderchange.form.tc_newendtime
													},{
														xtype : 'textareatrigger',
														fieldLabel: "变更原因", 
														name:'tc_changereason', 
														value : tenderchange.form.tc_changereason
													},{
												        fieldLabel: '录入人',
												        name:'tc_recordman',
												        value : tenderchange.form.tc_recordman
												    },{
														xtype:'datefield',
														name:'tc_recordtime',
														fieldLabel: "录入时间", 
														format:'Y-m-d',
														value : tenderchange.form.tc_recordtime
													}]
												},Ext.create("erp.view.common.JProcess.AllHistoryGridPanel",{
													id: 'tenderchangehistory'+index,
													plugins: null,
													deferLoadData :tenderchange.nodes.node==-1?true:false,
													nodeId: tenderchange.nodes.node
												})]
											});
										});
										tab.insert(1,{
											xtype : 'panel',
											title: '开标变更单',
											frame : true,
											autoScroll : true,
											items : items
										});
									}
									if(localJson.tenderanswer){
										tab.insert(1,{
											title: '答疑单',
											xtype : 'panel',
											frame : true,
											autoScroll : true,
											items : [{
												xtype : 'form',
												id : 'tenderanswerform',
												frame : true,
												layout : 'column',
												labelSeparator : ':',
												defaults : {
													xtype : "textfield", 
													columnWidth : 0.33,
											      	fieldStyle : "background:#eeeeee;color:#515151;", 
											      	labelAlign : "left",
											      	readOnly : true    	
												},
												items:[{
													fieldLabel: "ID", 
													name:'id',
													value : localJson.tenderanswer?localJson.tenderanswer.id:null,
													hidden:true
												},{
													fieldLabel: "答疑汇总单号", 
													name:'code', 
													value : localJson.tenderanswer?localJson.tenderanswer.code:null,
													fieldStyle : 'background:#eeeeee;color:blue;text-decoration:underline;',
													listeners : {
														click: {
															element:'inputEl',
															buffer : 100,
															fn: function(e,el) {
									                            if(localJson.tenderanswer&&localJson.tenderanswer.code){
									                            	var url = 'jsps/scm/purchase/tenderAnswer.jsp?whoami=TenderAnswer&formCondition=idIS'+localJson.tenderanswer.id+'&gridCondition=tsidIS'+localJson.tenderanswer.id;
									    						   	openUrl2(url,'答疑汇总单('+localJson.tenderanswer.code+')');
									                            }
															}	  
														}
													}
												},{
													fieldLabel: "招标编号", 
													name:'tenderCode', 
													value : localJson.tenderanswer?localJson.tenderanswer.tendercode:null,
													fieldStyle : 'background:#eeeeee;color:blue;text-decoration:underline;',
													listeners : {
														click: {
															element:'inputEl',
															buffer : 100,
															fn: function(e,el) {
									                            if(localJson.tenderanswer&&localJson.tenderanswer.tendercode){
									                            	var url = 'jsps/scm/purchase/tenderEstimate.jsp?formCondition=idIS'+localJson.tenderanswer.tenderId+'&gridCondition';
									    						   openUrl2(url,'评标单('+localJson.tenderanswer.tendercode+')');
									                            }
															}	  
														}
													}
												},{
													fieldLabel: "招标标题", 
													name:'tenderTitle', 
													value : localJson.tenderanswer?localJson.tenderanswer.tendertitle:null
												},{
													xtype:'datefield',
													name:'questionEndDate',
													fieldLabel: "提问截止时间", 
													format:'Y-m-d H:i:s',
													value : localJson.tenderanswer?localJson.tenderanswer.questionenddate:null
												},{
													xtype:'datefield',
													name:'auditDate',
													fieldLabel: "审批时间", 
													format:'Y-m-d',
													value : localJson.tenderanswer?localJson.tenderanswer.auditdate:null
												},{
											        fieldLabel: '审批状态',
											        name:'auditstatus',
											        value : localJson.tenderanswer?localJson.tenderanswer.auditstatus:null
											    },{
											    	xtype:'combo',
													fieldLabel: "回复状态", 
													name:'status',
													value : localJson.tenderanswer?localJson.tenderanswer.status:null,
													displayField:'display',
													valueField:'value',
													store: Ext.create('Ext.data.Store', {
														fields: ['display', 'value'],
														data:[{
															display:'已回复',
															value:201
														},{
															display:'未回复',
															value:200
														}]
													}),
													value : localJson.tenderanswer?localJson.tenderanswer.status:null
												},{
											        fieldLabel: '录入人',
											        name:'recorder',
											        value : localJson.tenderanswer?localJson.tenderanswer.recorder:null
											    },{
													xtype:'datefield',
													name:'indate',
													fieldLabel: "录入时间", 
													format:'Y-m-d',
													value : localJson.tenderanswer?localJson.tenderanswer.indate:null
												}]
											},Ext.create("erp.view.common.JProcess.AllHistoryGridPanel",{
												id: 'tenderanswerhistory',
												plugins: null,
												deferLoadData :localJson.nodes.TenderAnswer?false:true,
												nodeId: localJson.nodes.TenderAnswer?localJson.nodes.TenderAnswer.node:null
											})]
										});
									}
									
								}
							}
						}],
						buttonAlign : 'center',
						buttons : [{
							text : '关  闭',
							iconCls: 'x-button-icon-close',
							cls: 'x-btn-gray',
							handler : function(btn){
								btn.ownerCt.ownerCt.close();
							}
						}]
					}).show();
				}
			}
		});
	}
});

Ext.define('erp.view.core.button.TenderPanel',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.erpTenderForm',
	frame : true,
	layout : 'column',
	labelSeparator : ':',
	defaults:{
		xtype: "textfield", 
		columnWidth:0.33,
      	fieldStyle: "background:#eeeeee;color:#515151;", 
      	labelAlign: "left",
      	readOnly: true    	
	},
	FormUtil: Ext.create('erp.util.FormUtil'),
	formdata : null,
	tenderForm : true,
	tender: true,
	initComponent : function(){ 
		var me = this;
		Ext.apply(me, { 
			items:[{
				fieldLabel: "ID", 
				name:'id',
				value : me.formdata?me.formdata.id:null,
				hidden:true
			},{
				fieldLabel: me.tender?"招标编号":'评标编号', 
				name:'code', 
				value : me.formdata?me.formdata.code:null,
				fieldStyle : 'background:#eeeeee;color:blue;text-decoration:underline;',
				listeners : {
					click: {
						element:'inputEl',
						buffer : 100,
						fn: function(e,el) {
							var url = (me.tender&&me.tenderForm?'jsps/scm/purchase/tender.jsp?formCondition=idIS':'jsps/scm/purchase/tenderEstimate.jsp?formCondition=idIS')+me.formdata.id+'&gridCondition';
                            if(me.formdata&&me.formdata.code){
    						   openUrl2(url,(me.tender?'招标单(':'评标单(')+me.formdata.code+')');
                            }
						}	  
					}
				}
			},{
				fieldLabel: "招标标题", 
				name:'title', 
				value : me.formdata?me.formdata.title:null
			},{
				fieldLabel: "招标联系人", 
				name:'user', 
				value : me.formdata?me.formdata.user:null
			},{
				xtype:'combo',
				fieldLabel: "招标类型", 
				name:'ifAll',
				value : me.formdata?me.formdata.ifAll:0,
				displayField:'display',
				valueField:'value',
				store: Ext.create('Ext.data.Store', {
					fields: ['display', 'value'],
					data:[{
						display:'全包',
						value : 1
					},{
						display:'甲供料',
						value : 0
					}]
				})
			},{
				xtype:'datefield',
				name:'endDate',
				fieldLabel: "投标截止时间", 
				format:'Y-m-d',
				value : me.formdata?me.formdata.endDate:null
			},{
				fieldLabel: "招标联系电话", 
				name:'userTel', 
				value : me.formdata?me.formdata.userTel:null
			},{
				xtype:'combo',
				fieldLabel: "招标方式", 
				name:'ifOpen',
				value : me.formdata?me.formdata.ifAll:0,
				displayField:'display',
				valueField:'value',
				store: Ext.create('Ext.data.Store', {
					fields: ['display', 'value'],
					data:[{
						display:'公开招标',
						value : 1
					},{
						display:'指定招标',
						value : 0
					}]
				})
			},{
				xtype:'datefield',
				name:'publishDate',
				fieldLabel: "公布结果时间", 
				format:'Y-m-d',
				value : me.formdata?me.formdata.publishDate:null
			},{
		        fieldLabel: '招标状态',
		        name:'status',
		        hidden: !me.tenderForm,
		        value : me.formdata?me.formdata.status:null
		    },{
				xtype:'numberfield',
				name:'bidEnNum',
				fieldLabel: "已投标企业数", 
				hidden: me.tenderForm,
				value : me.formdata?me.formdata.bidEnNum:null
			}]
		});
		this.callParent(arguments);
	}
})
	