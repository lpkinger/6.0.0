/**
 * 员工交接检测
 */	
Ext.define('erp.view.core.button.EmpTransferCheck',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpEmpTransferCheckButton',
		iconCls: 'x-button-icon-delete',
    	cls: 'x-btn-gray',
    	id: 'checkbtn',
    	text: $I18N.common.button.erpEmpTransferCheck,
    	style: {
    		marginLeft: '10px'
        },
        width: 90,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});