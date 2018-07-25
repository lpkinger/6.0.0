/**
 * 测算
 */
Ext.define('erp.view.core.button.Measure',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpMeasureButton',
		param: [],
		id: 'erpMeasureButton',
		text: $I18N.common.button.erpMeasureButton,
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