/**
 * 添加要点
 */
Ext.define('erp.view.core.button.AddPoint',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpAddPointButton',
		param: [],
		id: 'erpAddPointButton',
		text: $I18N.common.button.erpAddPointButton,
		iconCls: 'x-button-icon-save',
    	cls: 'x-btn-gray',
    	width: 120,
    	style: {
    		marginLeft: '10px'
        },
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});