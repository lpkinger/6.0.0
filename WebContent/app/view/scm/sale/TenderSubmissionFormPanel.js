Ext.QuickTips.init();

Ext.define('erp.view.scm.sale.TenderSubmissionFormPanel', {
	extend:'erp.view.core.form.Panel',	
	alias:'widget.erpTenderSubmissionFormPanel',
	id:'form',
	layout:'column',
	title:'投标单 ',
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
				html: "<div onclick='javascript:collapse(1);' class='x-form-group-label' id='group1' style='background-color: #E8E8E8;height:22px;width:30%;margin:0 0 10px 0;' title='收拢'><h6>基本信息</h6></div>",
				xtype: "container",
				columnWidth:1
			},{
				fieldLabel: "ID", 
				id:'id',
				name:'id',
				hidden:true,
				group:1,
				maxLength:200
			},{
				fieldLabel: "招标编号", 
				id:'code',
				name:'code', 
				logic:'ignore',
				group:1,
				maxLength:200
			},{
				fieldLabel: "招标项目", 
				id:'title',
				name:'title',
				logic:'ignore',
				group:1,
				maxLength:200,
				columnWidth:0.5
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
				id:'enname',
				name:'enname', 
				logic:'ignore',
				group:1,
				maxLength:100
			},{
				fieldLabel: "联系人", 
				id:'user',
				name:'user', 
				logic:'ignore',
				group:1,
				maxLength:30 
			},{
				fieldLabel: "联系电话", 
				id:'usertel',
				name:'usertel', 
				logic:'ignore',
				group:1,
				maxLength:30
			},{
				xtype:'datetimefield',
				fieldLabel: "提问截止时间", 
				id:'questionEndDate',
				name:'questionEndDate', 
				logic:'ignore',
				group:1
			},{
				xtype:'datefield',
				fieldLabel: "投标截止时间", 
				id:'endDate',
				name:'endDate', 
				logic:'ignore',
				group:1
			},{
				xtype:'datefield',
				fieldLabel: "公布结果时间", 
				id:'publishDate',
				name:'publishDate', 
				logic:'ignore',
				group:1
			},{
				fieldLabel: "交易币别", 
				maxLength:10, 
				id:'currency',
				name:'currency',
				logic:'ignore',
				group:1
			},{
				fieldLabel: "是否含税", 
				xtype:'erpYnField',
				id:'ifTax',
				name:'ifTax',
				logic:'ignore',
				group:1
			},{
				fieldLabel: "付款方式", 
				maxLength:50, 
				id:'payment',
				name:'payment',
				logic:'ignore',
				group:1
			},{
		        fieldLabel: '是否开放报名',
		        xtype:'erpYnField',
		        id:'ifOpen',
		        name:'ifOpen',
		        logic:'ignore',
		        group:1
		    },{
		        fieldLabel: '发票要求',
		        xtype:'combo',
		        id:'invoiceType',
		        name:'invoiceType',
		        logic:'ignore',
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
		        group:1
		    },{
				fieldLabel: "交货地址", 
				id:'shipAddress',
				name:'shipAddress', 
				logic:'ignore',
				group:1,
				allowBlank: false,
				columnWidth:0.5,
				maxLength:250
			},{
		        fieldLabel: '证照要求',
		        id:'certificate',
		        name:'certificate',
		        logic:'ignore',
		        group:1,
		        columnWidth:0.5
		    },{
		        fieldLabel: '投标人',
		        id:'st_auditman',
		        name:'st_auditman',
		        group:1,
		        columnWidth:0.25
		    },{
		        fieldLabel: '投标日期',
		        id:'st_auditdate',
		        name:'st_auditdate',
		        group:1,
		        columnWidth:0.25
		    },{
				fieldLabel: "单据状态", 
				id:'st_status',
				name:'st_status', 
				group:1,
				maxLength:200,
				columnWidth:0.25
			},{
		        fieldLabel: '单据状态码',
		        id:'st_statuscode',
		        name:'st_statuscode',
		        logic:'ignore',
		        hidden:true,
		        group:1
		    },{
		        fieldLabel: '投标状态',
		        id:'status',
		        name:'status',
		        logic:'ignore',
		        group:1
		    },{	
		    	xtype:'numberfield',
		    	readOnly:false,
		    	labelStyle: "color:#FF0000",
		        fieldLabel: '项目周期',
		        id:'cycle',
		        name:'cycle',
		        allowBlank: false,
		        editable: true, 
		        hidden:true,
		        group:1,
		        regex:/^[1-9]\d*$/,
				regexText:'采购周期为大于0的整数',
				hideTrigger:true,
		        fieldStyle: "background:#FFFAFA;color:#515151;"
		    },{	
		    	xtype:'numberfield',
		    	readOnly:false,
		    	labelStyle: "color:#FF0000",
		        fieldLabel: '税率',
		        id:'taxrate',
		        name:'taxrate',
		        allowBlank: false,
		        editable: true, 
		        hidden:true,
		        group:1,
		        hideTrigger:true,
				minValue:0,
				maxValue:100,
		        fieldStyle: "background:#FFFAFA;color:#515151;"
		    },{	
		    	xtype:'numberfield',
		        fieldLabel: '总报价',
		        id:'totalMoney',
		        name:'totalMoney',
		        hidden:true,
		        group:1,
		        hideTrigger:true,
		        fieldStyle: "background:#FFFAFA;color:#515151;"
		    },{
				xtype:'mfilefield2',
				title: "招标附件", 
				id:'tendattachs',
				name:'tendattachs',
				logic:'ignore',
				group:1
			},{
				border: false,
				columnWidth:1,  
				html: "<div onclick='javascript:collapse(2);' class='x-form-group-label' id='group2' style='background-color: #E8E8E8;height:22px;width:30%;margin:0 0 10px 0;' title='收拢'><h6>投标企业基本信息</h6></div>",
				xtype: "container",
				columnWidth:1
			},{
		        fieldLabel: '企业名称',
		        id:'enName',
		        name:'enName',
		        group:2
		    },{
		        fieldLabel: '注册地址',
		        id:'enAddress',
		        name:'enAddress',
		        group:2,
		        columnWidth:0.5
		    },{
		        fieldLabel: '企业UU号',
		        id:'uu',
		        name:'uu',
		        group:2
		    },{
		        fieldLabel: '营业执照号',
		        id:'enBusinessCode',
		        name:'enBusinessCode',
		        group:2
		    },{
		    	xtype:'datefield',
		        fieldLabel: '成立时间',
		        id:'enEstablishDate',
		        name:'enEstablishDate',
		        group:2
		    },{
		    	fieldStyle: "background:#FFFAFA;color:#515151;",
		    	readOnly:false,
		        fieldLabel: '员工人数',
		        id:'emNum',
		        name:'emNum',
		        regex:/^[1-9]\d*$/,
				regexText:'员工人数为大于0的整数',
		        group:2
		    },{
		    	readOnly:false,
		        fieldLabel: '企业电话',
		        id:'enTel',
		        name:'enTel',
		        group:2,
		        fieldStyle: "background:#FFFAFA;color:#515151;"
		    },{
		    	readOnly:false,
		        fieldLabel: '企业传真',
		        id:'enFax',
		        name:'enFax',
		        group:2,
		        fieldStyle: "background:#FFFAFA;color:#515151;"
		    },{
		    	readOnly:false,
		        fieldLabel: '管理员',
		        id:'enUser',
		        name:'enUser',
		        group:2,
		        fieldStyle: "background:#FFFAFA;color:#515151;"
		    },{
		    	readOnly:false,
		        fieldLabel: '联系电话',
		        id:'userTel',
		        name:'userTel',
		        group:2,
		        fieldStyle: "background:#FFFAFA;color:#515151;"
		    },{
		    	readOnly:false,
		        fieldLabel: '开户银行',
		        id:'bank',
		        name:'bank',
		        group:2,
		        fieldStyle: "background:#FFFAFA;color:#515151;"
		    },{
		    	readOnly:false,
		        fieldLabel: '经营范围',
		        id:'scope',
		        name:'scope',
		        columnWidth:0.5,
		        group:2,
		        fieldStyle: "background:#FFFAFA;color:#515151;"
		    },{
		    	readOnly:false,
		        fieldLabel: '备注',
		        id:'remark',
		        name:'remark',
		        group:2,
		        columnWidth:0.5,
		        fieldStyle: "background:#FFFAFA;color:#515151;"
		    },{
				xtype:'mfilefield2',
				title: "投标附件", 
				id:'attachs',
				name:'attachs',
				logic:'ignore',
				readOnly:false,
				group:1
			}],
			buttons:[{
				xtype:'erpSaveButton',
				hidden:true
			},{
				xtype:'erpSubmitButton',
				formBind: true,//form.isValid() == false时,按钮disabled
				hidden:true
			},{
				xtype:'erpResSubmitButton',
				hidden:true
			},{
				xtype:'erpAuditButton',
				text:'投标',
				hidden:true
			},{
				xtype:'erpResAuditButton',
				text:'重新投标',
				width:90,
				hidden:true
			},{
				xtype:'erpCloseButton'
			}]
		});
	}
});
