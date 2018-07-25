/**
 * 转有效
 */
Ext.define('erp.view.core.button.ResAbate',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpResAbateButton',
		param: [],
		id: 'erpResAbateButton',
		text: $I18N.common.button.erpResAbateButton,
		iconCls: 'x-button-icon-recall',
    	cls: 'x-btn-gray',
    	width: 70,
    	style: {
    		marginLeft: '10px'
        },
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});