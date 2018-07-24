Ext.define('erp.view.scm.purchase.TenderQuestion',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				title: '提问答疑',
				anchor:'100% 100%',
				defaults:{
					xtype: "textfield", 
					columnWidth:0.25,
					allowBlank: true, 
			      	cls: "form-field-allowBlank", 
			      	fieldStyle: "background:#eeeeee;color:#515151;", 
			      	labelAlign: "left",
			      	readOnly: true    	
				},
				enableKeyEvents:false,
				getItemsAndButtons:function(){
					Ext.apply(this,{
						items:[
							{
							fieldLabel: "ID", 
							id:'id',
							name:'id',
							hidden:true
						},{
							fieldLabel: "提问编号", 
							id:'code',
							name:'code'
						},{
							fieldLabel: "提问单位", 
							id:'vendName',
							name:'vendName'
						},{
							fieldLabel: "提问主题", 
							id:'topic',
							name:'topic',
							columnWidth:0.5
						},{
					        fieldLabel: '招标ID',
					        id:'tenderId',
					        name:'tenderId',
					        hidden:true
					    },{
					        fieldLabel: '招标编号',
					        id:'tenderCode',
					        name:'tenderCode',
					        fieldStyle : 'background:#eeeeee;color:blue;text-decoration:underline;',
							listeners : {
								click: {
									element:'inputEl',
									buffer : 100,
									fn: function(e,el) {
										var tenderId = Ext.getCmp('tenderId');
										var tenderCode = Ext.getCmp('tenderCode');
										if(tenderId&&tenderId.value&&tenderCode&&tenderCode.value){
											var url = 'jsps/scm/purchase/tenderEstimate.jsp?formCondition=idIS'+tenderId.value+'&gridCondition';
			    						 	openUrl2(url,'评标单('+tenderCode.value+')');
										}
									}	  
								}
							}
					    },{
					        fieldLabel: '招标标题',
					        id:'tenderTitle',
					        name:'tenderTitle',
					        columnWidth:0.5
					    },{
					    	xtype : 'datetimefield',
					        fieldLabel: '提问截止时间',
					        id:'questionEndDate',
					        xtype:'datetimefield',
					        name:'questionEndDate',
					        format:'Y-m-d H:i:s'
					    },{
					        fieldLabel: '提问时间',
					        id:'inDate',
					        xtype:'datefield',
					        name:'inDate',
					        format:'Y-m-d'
					    },{
					        fieldLabel: '回复时间',
					        id:'replyDate',
					        xtype:'datetimefield',
					        name:'replyDate',
					        format:'Y-m-d H:i:s'
					    },{
							xtype:'combo',
							fieldLabel: "处理状态", 
							id:'status',
							name:'status',
							displayField:'display',
							valueField:'value',
							store: Ext.create('Ext.data.Store', {
								fields: ['display', 'value'],
								data:[{
									display:'未回复',
									value:200
								},{
									display:'已回复',
									value:201
								}]
							})
						},{
							xtype: 'textareafield',
							fieldLabel: "咨询内容", 
							id:'content',
							name:'content', 
							columnWidth:1
						},{
							xtype:'mfilefield2',
							title: "咨询文件", 
							id:'attachs',
							name:'attachs'
						}],
					    buttons:[{
							xtype:'erpCloseButton'
						}]
					});
				}
			}]
		}); 
		me.callParent(arguments); 
	} 
});