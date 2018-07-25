Ext.define('erp.view.hr.attendance.AttendDataComForm',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.AttendDataCom',
	id: 'form', 
	title: '考勤数据计算',
    frame : true,
	autoScroll : true,
	width: 450,
	height: 300,
	cls: 'singleWindowForm',
	bodyCls: 'singleWindowForm',
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
	items: [
	        
			{		
				margin:'80 0 0 0',
				xtype: 'condatefield',
				fieldLabel: '开始时间',
				allowBlank: false,
				id: 'searchdate',
				name: 'searchdate',
				value: 7
			}
	        /*{		
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
		xtype:'dbfindtrigger',
		fieldLabel:'员工编号',
		allowBlank: true,
		id : 'em_code',
		name:'em_code'
	},{
		xtype:'textfield',
		fieldLabel:'员工名称',
		allowBlank: true,
		id : 'em_name',
		readOnly:true,
		name:'em_name'
	}*/],
	buttons: [{
		xtype: 'erpAttendDataComButton'
	},{
		xtype:'erpCloseButton'
	}]
});