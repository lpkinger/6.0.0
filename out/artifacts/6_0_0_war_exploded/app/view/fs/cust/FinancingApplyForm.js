Ext.define('erp.view.fs.cust.FinancingApplyForm',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.erpFinancingApplyFormPanel',
   	title :'保理融资申请',
	height: 400,
	width: 550,
	layout: 'vbox',
	bodyStyle: 'background: #f1f1f1;',
	defaults: {
		width: 500,
		margin: '5 10 0 10',
		xtype: 'textfield',
		readOnly:formCondition!=null,
       	blankText : $I18N.common.form.blankText
	},
	buttonAlign:'center',
	initComponent : function(){
		Ext.apply(this,{
			items: [{
				fieldLabel:'ID',
				hidden:true,
				id:'fa_id',
				name:'fa_id'
			},{
				fieldLabel:'企业名称',	
				id:'fa_enname',
				name:'fa_enname',
				labelWidth: 100,
				layout: 'column',
				emptyText: '请填写企业名称',
				readOnly:true,
				allowBlank:false
			},{
				fieldLabel: '拟融资金额',
				labelWidth: 100,
				layout: 'column',
				xtype: 'fieldcontainer',
				allowBlank: false,
				defaults: {
					fieldStyle : "background:#FFFAFA;color:#515151;"
				},
				items: [{
					xtype: 'numberfield',
					name: 'fa_appamount',
					id: 'fa_appamount',
					columnWidth: 0.6,
					hideTrigger:true,
					minValue:0,
					allowBlank:false
				},{
					xtype: 'displayfield',
					columnWidth: 0.4,
					html: '<span>&nbsp;元（只支持人民币）</span>'
				}]
			},{
				fieldLabel:'买方客户编号',		
				id:'fa_buyercode',
				name:'fa_buyercode',
				allowBlank:false,
				hidden:true
			},{
				xtype: 'multidbfindtrigger',
				separator: '；',
				fieldLabel:'买方客户',		
				id:'fa_buyer',
				name:'fa_buyer',
				allowBlank:false
			},{
				fieldLabel:'联系人',		
				id:'fa_contact',
				name:'fa_contact',
				allowBlank:false
			},{
				fieldLabel:'联系方式1',		
				id:'fa_telphone',
				name:'fa_telphone',
				allowBlank:false
			},{
				fieldLabel:'联系方式2',		
				id:'fa_phone',
				name:'fa_phone',
				allowBlank:false
			},{
				fieldLabel:'申请人',		
				id:'fa_applyman',
				name:'fa_applyman',
				value:em_name,
				hidden:true
			},{
				fieldLabel:'申请日期',	
				xtype:'datefield',
				id:'fa_applydate',
				name:'fa_applydate',
				format:'Y-m-d',
				value: Ext.Date.format(new Date(), 'Y-m-d'),
				hidden:true
			},{
				fieldLabel:'保理公司名称',
				xtype: 'dbfindtrigger',
				id:'fa_facorpname',
				name:'fa_facorpname',
				allowBlank:false
			},{
				fieldLabel:'保理公司',	
				id:'fa_facorpcode',
				name:'fa_facorpcode',
				hidden:true,
				allowBlank:false
			},{
				xtype:'checkbox',
				boxLabel:'已阅读并同意<a href="javascript:showWindow(\'grantGetDatas\')"> 客户信息保密协议、企业征信查询及系统数据获取授权书</a>',
                name: 'agreed',
                id:'agreed',
                logic:'ignore',
                style:'text-align:center',
                fieldStyle:null
			},{
				xtype:'checkbox',
				boxLabel:'已阅读并同意<a href="javascript:showWindow(\'inforList\')"> 使用的相关资料</a>',
                name: 'agreed2',
                id:'agreed2',
                logic:'ignore',
                style:'text-align:center',
                fieldStyle:null
			},{
				xtype:'displayfield',
                name: 'telphone',
                id:'telphone',
                logic:'ignore',
                html:'<span>公司电话：26616688转8224</span>',
                style:'text-align:center'
			}],
			buttonAlign: 'center',
			buttons: [{
    			xtype: 'erpSubmitButton'
    		},{
				xtype:'button',
				iconCls: 'x-button-icon-submit',
		    	cls: 'x-btn-gray',
		    	text: '融资进度',
		    	id:'progress',
		        width: 85,
		        disabled:true
			},{
    			xtype:'erpCancelButton'
    		}]
		});
		this.callParent(arguments);
	}
});