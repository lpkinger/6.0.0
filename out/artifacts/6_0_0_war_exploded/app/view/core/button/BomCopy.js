Ext.define('erp.view.core.button.BomCopy',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpBomCopyButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	id: 'bomcopy',
    	text: $I18N.common.button.erpBomCopyButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 100,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});