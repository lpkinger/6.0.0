Ext.define('erp.view.core.button.LabelPreview',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpLabelPreviewButton',
    	cls: 'x-btn-gray',
    	id: 'LabelPreviewBtn',
    	text: $I18N.common.button.erpLabelPreviewButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 100,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});