Ext.QuickTips.init();

Ext.define('erp.view.scm.purchase.TenderFormPanel', {
	extend:'erp.view.core.form.Panel',	
	alias:'widget.erpTenderFormPanel',
	id:'form',
	keyField:'id',
	statusField:"tt_status",
	statuscodeField:"tt_statuscode",
	deleteUrl: 'scm/purchase/deleteTender.action',
	resSubmitUrl: 'scm/purchase/resSubmitTender.action',
	layout:'column',
	/*title:'招标单 ',*/
	enableKeyEvents:false,
	formdata:null,
	getItemsAndButtons:function(){
		var me = this;
		Ext.apply(me,{
			defaults:{
				xtype: "textfield", 
				columnWidth:0.25,
				allowBlank: true, 
		      	cls: "form-field-allowBlank", 
		      	fieldStyle: me.formdata&&me.formdata.tt_statuscode!='ENTERING'?'background:#eeeeee;color:#515151;':"background:#FFFAFA;color:#515151;", 
		      	editable: true, 
		      	labelAlign: "right",
		      	margin:'3 0 3 0',
		      	readOnly: me.formdata&&me.formdata.tt_statuscode!='ENTERING'?true: false   	
			}
		});
		var certificate = me.formdata?me.formdata.certificate:null;
		Ext.apply(me,{
			items:[{
				fieldLabel: "ID", 
				id:'id',
				name:'id',
				value:me.formdata?me.formdata.id:null,
				hidden:true
			},{
				fieldLabel: "招标编号", 
				id:'code',
				name:'code', 
				value:me.formdata?me.formdata.code:null,
				readOnly:true,
				hidden:true,
				fieldStyle : 'background:#eeeeee'
			},{
				fieldLabel: "<font color=\"red\" style=\"position:relative; top:2px;right:2px; font-weight: bolder;\">*</font>招标标题", 
				id:'title',
				name:'title', 
				value:me.formdata?me.formdata.title:null,
				allowBlank: false,
				columnWidth:0.5
			},{
				fieldLabel: "<font color=\"red\" style=\"position:relative; top:2px;right:2px; font-weight: bolder;\">*</font>联系人", 
				id:'user',
				name:'user', 
				allowBlank: false,
				value:me.formdata?me.formdata.user:null
			},{
				fieldLabel: "<font color=\"red\" style=\"position:relative; top:2px;right:2px; font-weight: bolder;\">*</font>联系电话", 
				id:'userTel',
				name:'userTel', 
				allowBlank: false,
				value:me.formdata?me.formdata.userTel:null
			},{
				xtype:'combo',
				fieldLabel: "<font color=\"red\" style=\"position:relative; top:2px;right:2px; font-weight: bolder;\">*</font>招标类型", 
				allowBlank: false, 
				id:'ifAll',
				name:'ifAll',
				value:me.formdata?me.formdata.ifAll:0,
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
				fieldLabel: "<font color=\"red\" style=\"position:relative; top:2px;right:2px; font-weight: bolder;\">*</font>交易方式", 
				allowBlank: false,
				id:'payment',
				name:'payment',
				value:me.formdata?me.formdata.payment:'现金',
				xtype:"dbfindtrigger"
			},{
				fieldLabel: "<font color=\"red\" style=\"position:relative; top:2px;right:2px; font-weight: bolder;\">*</font>交易币别", 
				allowBlank: false,
				id:'currency',
				name:'currency',
				value:me.formdata?me.formdata.currency:'RMB',
				xtype:"dbfindtrigger"
			},{
				xtype:'combo',
				fieldLabel: "<font color=\"red\" style=\"position:relative; top:2px;right:2px; font-weight: bolder;\">*</font>是否含税", 
				allowBlank: false, 
				id:'ifTax',
				name:'ifTax',
				value:me.formdata?me.formdata.ifTax:1,
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
		        fieldLabel: '<font color=\"red\" style=\"position:relative; top:2px;right:2px; font-weight: bolder;\">*</font>发票要求',
		        xtype:'combo',
		        id:'invoiceType',
		        name:'invoiceType',
				allowBlank: false, 
				value:me.formdata?me.formdata.invoiceType:2,
		        store:Ext.create('Ext.data.Store', {
				    fields: ['display', 'value'],
				    data : [
				        {display:'不需要发票', value:0},
				        {display:'增值税普通发票', value:1},
				        {display:'增值税专用发票', value:2}]
					}),
				queryMode: 'local',
				displayField: 'display',
				valueField: 'value'
		    },{
				xtype:'datetimefield',
				fieldLabel: "提问截止时间", 
				format:'Y-m-d',
				value:me.formdata?me.formdata.questionEndDate:null,
				id:'questionEndDate',
				name:'questionEndDate'
			},{
				xtype:'datefield',
				fieldLabel: "<font color=\"red\" style=\"position:relative; top:2px;right:2px; font-weight: bolder;\">*</font>投标截止时间", 
				format:'Y-m-d',
				allowBlank: false,
				value:me.formdata?me.formdata.endDate:null,
				id:'endDate',
				name:'endDate'
			},{
				xtype:'datefield',
				fieldLabel: "<font color=\"red\" style=\"position:relative; top:2px;right:2px; font-weight: bolder;\">*</font>公布结果时间", 
				format:'Y-m-d',
				allowBlank: false,
				value:me.formdata?me.formdata.publishDate:null,
				id:'publishDate',
				name:'publishDate'
			},{
				fieldLabel: "<font color=\"red\" style=\"position:relative; top:2px;right:2px; font-weight: bolder;\">*</font>收货地址", 
				xtype:'textareatrigger',
				id:'shipAddress',
				name:'shipAddress',
				value:me.formdata?me.formdata.shipAddress:null,
				allowBlank: false,
				columnWidth:0.5
			},{
				xtype: 'checkboxgroup',
		        fieldLabel: '证件要求',
		        id:'certificate',
		        name:'certificate',
		        columns: 4,
		        vertical: false,
		        columnWidth:1,
		        items: [{ 
		        	boxLabel: '三/五证合一', name: 'certificate', inputValue: '三/五证合一',checked: certificate?certificate.indexOf('三/五证合一')>-1:false
		        },{ 
		        	boxLabel: '营业执照', name: 'certificate', inputValue: '营业执照',checked: certificate?certificate.indexOf('营业执照')>-1:false 
		        },{ 
		        	boxLabel: '组织机构代码证', name: 'certificate', inputValue: '组织机构代码证',checked: certificate?certificate.indexOf('组织机构代码证')>-1:false
		        },{ 
		        	boxLabel: '税务登记证', name: 'certificate', inputValue: '税务登记证',checked: certificate?certificate.indexOf('税务登记证')>-1:false 
		        },{ 
		        	boxLabel: '一般纳税人证明材料', name: 'certificate', inputValue: '一般纳税人证明材料',checked: certificate?certificate.indexOf('一般纳税人证明材料')>-1:false 
		        },{ 
		        	boxLabel: '统计登记证', name: 'certificate', inputValue: '统计登记证',checked: certificate?certificate.indexOf('统计登记证')>-1:false
		        },{ 
		        	boxLabel: '社会保险登记证', name: 'certificate', inputValue: '社会保险登记证',checked: certificate?certificate.indexOf('社会保险登记证')>-1:false
		        }]
		    },{
		        fieldLabel: '录入人',
		        id:'pt_recordman',
		        name:'pt_recordman',
		        readOnly:true,
				fieldStyle : 'background:#eeeeee',
		        value:me.formdata?me.formdata.pt_recordman:emname
		    },{
		        fieldLabel: '录入日期',
		        id:'pt_indate',
		        xtype:'datefield',
		        name:'pt_indate',
		        readOnly:true,
				fieldStyle : 'background:#eeeeee',
		        value:me.formdata?me.formdata.pt_indate:new Date(),
		        format:'Y-m-d'
		    },{
				fieldLabel: "单据状态", 
				id:'tt_status',
				name:'tt_status',
		        logic:'ignore',
		        readOnly:true,
				fieldStyle : 'background:#eeeeee',
		        value:me.formdata?me.formdata.tt_status:null
			},{
				fieldLabel: "单据状态码", 
				id:'tt_statuscode',
				name:'tt_statuscode',
		        logic:'ignore',
		        readOnly:true,
		        hidden: true,
				fieldStyle : 'background:#eeeeee',
		        value:me.formdata?me.formdata.tt_statuscode:null
			},{
		        fieldLabel: '业务状态',
		        id:'status',
		        name:'status',
		        logic:'ignore',
		        readOnly:true,
				fieldStyle : 'background:#eeeeee',
		        value:me.formdata?me.formdata.status:null
		    },{
				xtype: 'radiogroup',
		        fieldLabel: '是否开放报名',
		        id:'ifOpen',
		        name:'ifOpen',
		        vertical: true,
		        columnWidth:0.5,
		        items: [{ 
		        	boxLabel: '允许优软平台的供应商参与', name: 'ifOpen', inputValue: 1,checked: me.formdata?me.formdata.ifOpen==1:true 
		        },{ 
		        	boxLabel: '只允许我邀请的供应商参与', name: 'ifOpen', inputValue: 0,checked: me.formdata?me.formdata.ifOpen==0:false 
		        }]
		    },{
				xtype:'mfilefield2',
				title: "招标附件", 
				id:'attachs',
				name:'attachs',
				value:me.formdata?me.formdata.attachs:null
			}],
			tbar:{margin:'0 0 5 0',items:[{
				xtype:'erpAddButton',
				hidden:true
			},{
				xtype:'erpSaveButton'
			},{
				xtype:'erpUpdateButton',
				formBind: true,
				hidden:true
			},{
				xtype:'erpDeleteButton',
				hidden:true
			},{
				xtype:'button',
				iconCls: 'x-button-icon-submit',
		    	cls: 'x-btn-gray',
		    	text: '发布',
		    	formBind: true,
		    	style: {
		    		marginLeft: '10px'
		        },
		    	id:'release',
		    	name:'release'
			},{
		        xtype:'erpResSubmitButton',
				formBind: true,
				hidden:true
	        },'->',
			{
				xtype:'erpCloseButton'
			}]}
		});
		var _Virtual = getUrlParam('_Virtual');
		if(_Virtual){
			me.tbar = me.setTools();
		}
	}
});
