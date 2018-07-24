Ext.QuickTips.init();

Ext.define('erp.view.scm.purchase.TenderEstimateFormPanel', {
	extend:'erp.view.core.form.Panel',	
	alias:'widget.erpTenderEstimateFormPanel',
	id:'form',
	keyField:'id',
	statusField:"pt_status",
	statuscodeField:"pt_statuscode",
	layout:'column',
	title:'评标单 ',
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
		var me = this;
		Ext.apply(me,{
			items:[
				{
				fieldLabel: "ID", 
				id:'id',
				name:'id',
				hidden:true,
				maxLength:200
			},{
				fieldLabel: "招标编号", 
				id:'code',
				name:'code',
				logic:'ignore'
			},{
				fieldLabel: "招标标题", 
				id:'title',
				name:'title', 
				logic:'ignore'
			},{
		        fieldLabel: '录入人',
		        id:'pt_recordman',
		        name:'pt_recordman'
		    },{
		        fieldLabel: '录入日期',
		        id:'pt_indate',
		        xtype:'datefield',
		        name:'pt_indate',
		        format:'Y-m-d'
		    },{
				fieldLabel: "联系人", 
				id:'user',
				name:'user', 
				logic:'ignore'
			},{
				fieldLabel: "联系电话", 
				id:'userTel',
				name:'userTel', 
				logic:'ignore'
			},{
				fieldLabel: "收货地址", 
				id:'shipAddress',
				name:'shipAddress', 
				logic:'ignore',
				columnWidth:0.5
			},{
				xtype:'datetimefield',
				fieldLabel: "提问截止时间", 
				id:'questionEndDate',
				name:'questionEndDate',
				logic:'ignore',
				format:'Y-m-d'
			},{
				xtype:'datefield',
				fieldLabel: "投标截止时间", 
				id:'endDate',
				name:'endDate',
				logic:'ignore',
				format:'Y-m-d'
			},{
				xtype:'datefield',
				fieldLabel: "公布结果时间", 
				id:'publishDate',
				name:'publishDate',
				logic:'ignore',
				format:'Y-m-d'
			},{
				fieldLabel: "交易币别", 
				id:'currency',
				logic:'ignore',
				name:'currency'
			},{
				xtype:'combo',
				fieldLabel: "是否含税", 
				id:'ifTax',
				name:'ifTax',
				logic:'ignore',
				displayField:'display',
				valueField:'value',
				store: Ext.create('Ext.data.Store', {
					fields: ['display', 'value'],
					data:[{
						display:'是',
						value:1
					},{
						display:'否',
						value:0
					}]
				})
			},{
				fieldLabel: "付款方式", 
				id:'payment',
				name:'payment',
				logic:'ignore'
			},{
		        fieldLabel: '证照要求',
		        id:'certificate',
		        name:'certificate',
		        logic:'ignore'
		    },{
		        fieldLabel: '评标人',
		        id:'pt_auditman',
		        name:'pt_auditman'
		    },{
		        fieldLabel: '评标日期',
		        id:'pt_auditdate',
		        name:'pt_auditdate'
		    },{
				xtype:'combo',
				fieldLabel: "招标类型", 
				id:'ifAll',
				name:'ifAll',
				displayField:'display',
				valueField:'value',
				store: Ext.create('Ext.data.Store', {
					fields: ['display', 'value'],
					data:[{
						display:'全包',
						value:1
					},{
						display:'甲供料',
						value:0
					}]
				})
			},{
		        fieldLabel: '单据状态',
		        id:'pt_status',
		        name:'pt_status'
		    },{
		        fieldLabel: '单据状态码',
		        id:'pt_statuscode',
		        name:'pt_statuscode',
		        hidden:true
		    },{
		        fieldLabel: '业务状态',
		        id:'status',
		        name:'status',
		        logic:'ignore'
		    },{
				xtype:'mfilefield2',
				title: "招标附件", 
				id:'attachs',
				name:'attachs',
				logic:'ignore'
			},{
				xtype:'mfilefield2',
				title: "评标附件", 
				id:'pt_attachs',
				name:'pt_attachs',
				hidden: true,
				readOnly: false
			}],
		    buttons:[{
				xtype:'erpSaveButton',
				hidden:true
			},{
				xtype:'erpSubmitButton',
				hidden:true
			},{
				xtype:'erpResSubmitButton',
				hidden:true
			},{
				xtype:'erpAuditButton',
				text:'评标',
				hidden:true
			},{
				xtype:'button',
				iconCls: 'x-button-icon-submit',
		    	cls: 'x-btn-gray',
		    	text: '审批记录',
		    	maxWidth : 85,
		    	style: {
		    		marginLeft: '10px'
		        },
		    	id:'audithistory',
		    	name:'audithistory'
			},{
				xtype:'button',
				iconCls: 'x-button-icon-submit',
		    	cls: 'x-btn-gray',
		    	text: '转合同',
		    	maxWidth : 85,
		    	style: {
		    		marginLeft: '10px'
		        },
		    	id: 'turnPurchase',
		    	name: 'turnPurchase',
		    	hidden: true
			},{
				xtype:'erpCloseButton'
			}]
		});
	}
});
