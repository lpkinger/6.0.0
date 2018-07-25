/**
 * 批准按钮
 */	
Ext.define('erp.view.core.button.Check',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpCheckButton',
		iconCls: 'x-button-icon-delete',
    	cls: 'x-btn-gray',
    	id: 'checkbtn',
    	text: $I18N.common.button.erpCheckButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 60,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});