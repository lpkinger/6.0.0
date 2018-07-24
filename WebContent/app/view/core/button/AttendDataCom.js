Ext.define('erp.view.core.button.AttendDataCom',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpAttendDataComButton',
		param: [],
		id:'AttendDataCombutton',
		text: $I18N.common.button.erpAttendDataComButton,
		iconCls: 'x-button-icon-save',
    	cls: 'x-btn-gray',
    	formBind: true,//form.isValid() == false时,按钮disabled
    	width: 100,
    	style: {
    		marginLeft: '10px'
        },
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});