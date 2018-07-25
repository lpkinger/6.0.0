/**
 * 新增按钮
 */	
Ext.define('erp.view.core.button.Transmit',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpTransmitButton',
		iconCls: 'x-button-icon-add',
    	cls: 'x-btn-gray',
    	id: 'transmitbtn',
    	text: $I18N.common.button.erpTransmitButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 60,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});