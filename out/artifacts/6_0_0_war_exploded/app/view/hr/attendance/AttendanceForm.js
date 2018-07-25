Ext.define('erp.view.hr.attendance.AttendanceForm',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.AttendanceForm',
	id: 'form', 
	title: ' 考勤分析作业 ',
    frame : true,
	autoScroll : true,
	buttonAlign : 'center',
	FormUtil: Ext.create('erp.util.FormUtil'),
	confirmUrl:'',
	cls: 'singleWindowForm',
	bodyCls: 'singleWindowForm',
	fieldDefaults : {
	       margin : '4 2 4 2',
	       fieldStyle : "background-color:#FFFAFA;color:#515151;",
	       labelAlign : "right",
	       msgTarget: 'side',
	       blankText : $I18N.common.form.blankText
	},       
	initComponent : function(){ 
		this.callParent(arguments);
		/*this.title = this.FormUtil.getActiveTab().title;*/
	},
	items: [{	
		margin:'40 0 0 0',
    	xtype: 'condatefield',
    	fieldLabel: '日期范围',
    	allowBlank: false,
    	id: 'searchdate',
    	name: 'searchdate'
	},{
		xtype:'checkbox',
		boxLabel  : '生成考勤确认单',
		id:'toAttendanceConfirm',
		inputValue:true,
		name:'toAttendanceConfirm'
	}],
	buttons: [{
		xtype: 'erpConfirmButton'
	},{
		xtype:'erpCloseButton'
	}]
});