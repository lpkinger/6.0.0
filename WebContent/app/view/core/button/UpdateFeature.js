/**
 * 修改按钮
 */	
Ext.define('erp.view.core.button.UpdateFeature',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpUpdateFeatureButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpUpdateFeatureButton,
    	id:'updatefeaturebutton',
    	style: {
    		marginLeft: '10px'
        },
        width: 110,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});