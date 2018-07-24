Ext.define('erp.view.hr.attendance.CardLogImpForm',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.CardLogImp',
	id: 'form', 
	title: '打卡数据导入',
    frame : true,
	autoScroll : true,
	buttonAlign : 'center',
	FormUtil: Ext.create('erp.util.FormUtil'),
	confirmUrl:'',
	fieldDefaults : {
	       margin : '4 2 4 2',
	       fieldStyle : "background:#FFFAFA;color:#515151;",
	       labelAlign : "right",
	       msgTarget: 'side',
	       blankText : $I18N.common.form.blankText
	},       
	initComponent : function(){ 
		this.callParent(arguments);
		/*this.title = this.FormUtil.getActiveTab().title;*/
	},
	items: [{		
		xtype: 'datefield',
    	fieldLabel: '开始时间',
    	allowBlank: false,
    	id: 'startdate',
    	name: 'startdate'
	},{		
		xtype: 'datefield',
    	fieldLabel: '结束时间',
    	allowBlank: false,
    	id: 'enddate',
    	name: 'enddate'
	},{		
		xtype: 'monthdatefield',
    	fieldLabel: '月份',
    	allowBlank: false,
    	id: 'yearmonth',
    	name: 'yearmonth'
	},{
		xtype:'dbfindtrigger',
		fieldLabel:'员工卡号',
		allowBlank: true,
		id : 'cardcode',
		name:'cardcode'
	},{
		xtype:'textfield',
		fieldLabel:'员工名称',
		allowBlank: true,
		id : 'em_name',
		readOnly:true,
		name:'em_name'
	}],
	buttons: [{
		xtype: 'erpCardLogImpButton'
	},{
		xtype:'erpCloseButton'
	}]
});