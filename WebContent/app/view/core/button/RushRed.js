/**
 * 红冲
 */
Ext.define('erp.view.core.button.RushRed',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpRushRedButton',
		param: [],
		id: 'erpAbateButton',
		text: $I18N.common.button.erpRushRedButton,
		iconCls: 'x-button-icon-save',
    	cls: 'x-btn-gray',
    	width: 60,
    	style: {
    		marginLeft: '10px'
        },
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});