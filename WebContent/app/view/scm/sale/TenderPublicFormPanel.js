Ext.QuickTips.init();

Ext.define('erp.view.scm.sale.TenderPublicFormPanel', {
	extend:'erp.view.core.form.Panel',	
	alias:'widget.erpTenderPublicFormPanel',
	id:'form',
	layout:'column',
	title:'公开招标单 ',
	defaults:{
		xtype: "textfield", 
		columnWidth:0.25,
		allowBlank: true, 
      	cls: "form-field-allowBlank", 
      	editable: false, 
      	labelAlign: "left",
      	readOnly: true,
      	fieldStyle : 'background:#eeeeee'
	},
	enableKeyEvents:false,
	getItemsAndButtons:function(){
		var me = this;
		Ext.apply(me,{
			items:[{
				border: false,
				columnWidth:1,  
				html: "<div onclick='javascript:collapse(1);' class='x-form-group-label' id='group1' style='background-color: #E8E8E8;height:22px;width:30%;margin:0 0 10px 0;' title='收拢'><h6>招标信息</h6></div>",
				xtype: "container",
				columnWidth:1
			},{
				fieldLabel: "ID", 
				id:'id',
				name:'id', 
				hidden:true,
				group:1
			},{
				fieldLabel: "招标编号", 
				id:'code',
				name:'code', 
				group:1
			},{
				fieldLabel: "招标标题", 
				id:'title',
				name:'title',
				columnWidth:0.5,
				group:1
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
				}),
		        group:1
			},{
				fieldLabel: "招标企业", 
				id:'enName',
				name:'enName', 
				group:1,
				maxLength:100
			},{
				fieldLabel: "联系人", 
				id:'user',
				name:'user', 
				group:1
			},{
				fieldLabel: "联系电话", 
				id:'userTel',
				name:'userTel', 
				group:1
			},{
				xtype:'datetimefield',
				fieldLabel: "提问截止时间", 
				id:'questionEndDate',
				name:'questionEndDate', 
				group:1
			},{
				xtype:'datefield',
				fieldLabel: "投标截止时间", 
				id:'endDate',
				name:'endDate', 
				group:1
			},{
				xtype:'datefield',
				fieldLabel: "公布结果时间", 
				id:'publishDate',
				name:'publishDate', 
				group:1
			},{
				xtype:'mfilefield2',
				title: "招标附件", 
				id:'attachs',
				name:'attachs',
				group:1
			},{
				border: false,
				columnWidth:1,  
				html: "<div onclick='javascript:collapse(2);' class='x-form-group-label' id='group2' style='background-color: #E8E8E8;height:22px;width:30%;margin:0 0 10px 0;' title='收拢'><h6>招标要求</h6></div>",
				xtype: "container",
				columnWidth:1
			},{
				fieldLabel: "交易币别", 
				maxLength:10, 
				id:'currency',
				name:'currency',
				group:2
			},{
				xtype:'combo',
				fieldLabel: "是否含税", 
				labelStyle: "color:#FF0000",
				allowBlank: false, 
				id:'ifTax',
				name:'ifTax',
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
				}),
				group:2
			},{
		        fieldLabel: '发票要求',
		        xtype:'combo',
		        id:'invoiceType',
		        name:'invoiceType',
		        store:Ext.create('Ext.data.Store', {
			    fields: ['display', 'value'],
			    data : [
			        {display:'不需要发票', value:0},
			        {display:'增值税普通发票', value:1},
			        {display:'增值税专用发票', value:2}
			    ]
				}),
				queryMode: 'local',
				displayField: 'display',
				valueField: 'value',
		        group:2
		    },{
				fieldLabel: "交易方式", 
				maxLength:50, 
				id:'payment',
				name:'payment',
				group:2
			},{
		        fieldLabel: '证照要求',
		        id:'certificate',
		        name:'certificate',
		        group:2,
		        columnWidth:0.5
		    },{
				fieldLabel: "交货地址", 
				id:'shipAddress',
				name:'shipAddress', 
				group:2,
				columnWidth:0.75
			}],
			buttons:[{
				xtype:'button',
				iconCls: 'x-button-icon-submit',
		    	cls: 'x-btn-gray',
		    	text: '我要投标',
		    	id:'bid'
			},{
				xtype:'button',
				iconCls: 'x-button-icon-submit',
		    	cls: 'x-btn-gray',
		    	text: '我的投标',
		    	id:'mytender',
		    	hidden:true
			},{
				xtype:'erpCloseButton'
			}]
		});
	}
});
