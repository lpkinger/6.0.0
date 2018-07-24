/**
 * 转试用员工按钮
 */	
Ext.define('erp.view.core.button.TurnEmployee',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpTurnEmployeeButton',
		param: [],
		id: 'TurnEmployee',
		text: $I18N.common.button.erpTurnEmployeeButton,
		iconCls: 'x-button-icon-save',
    	cls: 'x-btn-gray',
    	formBind: true,//form.isValid() == false时,按钮disabled
    	width: 120,
    	style: {
    		marginLeft: '10px'
        },
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});